package gay.gwynnie.backpaws.photo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class PhotoService {

    @Value("${PHOTO_PATH}")
    private String photoPath;
    private Path dir;

    @Autowired
    private PhotoRepository photoRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void syncJsonAndDb() throws IOException {
        this.dir = Paths.get(photoPath);
        if (!dir.toFile().exists()) {
            throw new RuntimeException("Photo path does not exist: " + photoPath);
        }

        Path jsonFile = Paths.get(photoPath, "photos.json");

        PhotoJsonData jsonData;
        if (Files.exists(jsonFile)) {
            // 1) Read existing JSON
            jsonData = mapper.readValue(jsonFile.toFile(), PhotoJsonData.class);
        } else {
            // 1b) No file yet → start with an empty list
            jsonData = new PhotoJsonData("/photos", List.of());
            Files.createDirectories(jsonFile.getParent());
        }

        String uriPrefix = jsonData.path();

        // 2) Upsert JSON → Mongo
        for (PhotoJson pj : jsonData.photos()) {
            Photo p = new Photo(
                pj.filename(),
                pj.title(),
                pj.description(),
                PhotoType.getFromFilename(pj.filename()));
            photoRepository.save(p);
        }

        // 3) Fetch all from Mongo and find which are missing in the JSON
        List<Photo> allFromDb = photoRepository.findAll();
        Set<String> seen = jsonData.photos()
                                .stream()
                                .map(PhotoJson::filename)
                                .collect(Collectors.toSet());

        List<PhotoJson> merged = new ArrayList<>(jsonData.photos());
        for (Photo dbp : allFromDb) {
            if (!seen.contains(dbp.fileName())) {
                merged.add(new PhotoJson(
                    dbp.fileName(),
                    dbp.title(),
                    dbp.description()
                ));
            }
        }

        // 4) Write back the JSON (pretty-printed)
        PhotoJsonData out = new PhotoJsonData(uriPrefix, merged);
        mapper.writerWithDefaultPrettyPrinter()
              .writeValue(jsonFile.toFile(), out);
    }

    public Photo getPhoto(String fileName) {
        return photoRepository.findById(fileName).orElse(null);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    private boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        if (!PhotoType.isValidFileName(fileName)) {
            return false;
        }
        return true;
    }

    public boolean fileExists(String fileName) {
        Path path = dir.resolve(fileName);
        if (!path.toFile().exists()) {
            return false;
        }
        return true;
    }

    /**
     * Saves the raw photo bytes to disk (overwriting if already exists),
     * then upserts the Photo document in MongoDB.
     *
     * @param data        the image bytes
     * @param fileName    the name (and key) of the photo
     * @param title       user-friendly title
     * @param description optional description
     * @return the saved Photo record
     * @throws IOException              if writing to disk fails
     * @throws IllegalArgumentException on invalid fileName
     */
    private Photo savePhotoFromData(byte[] data, Photo photo) throws IOException {
        // Validate the file name
        if (!isValidFileName(photo.fileName())) {
            throw new IllegalArgumentException("Invalid file name: " + photo.fileName());
        }

        // Write or overrite file to disk
        Path file = dir.resolve(photo.fileName());
        Files.write(file, data, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        // Upsert the photo in MongoDB
        return photoRepository.save(photo);
    }

    public Photo savePhoto(byte[] data, Photo photo) throws IOException {
        return savePhotoFromData(data, photo);
    }

    /**
     * Loads the file from disk as a Spring Resource.
     * Throws IOException if not found or unreadable.
     */
    public Resource loadAsResource(String filename) throws IOException {
        Path file = Paths.get(photoPath).resolve(filename).normalize();

        if (!Files.exists(file) || !Files.isReadable(file)) {
            throw new IOException("Could not read file: " + filename);
        }

        return new UrlResource(file.toUri());
    }

    public String getPhotoPath() {
        return photoPath;
    }

}