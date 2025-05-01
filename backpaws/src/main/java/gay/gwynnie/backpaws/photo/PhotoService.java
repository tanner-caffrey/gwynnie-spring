package gay.gwynnie.backpaws.photo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    @Value("${PHOTO_PATH}")
    private String photoPath;
    private Path dir;

    @Autowired
    private PhotoRepository photoRepository;

    public PhotoService() {
        this.dir = Paths.get(photoPath);
        if(!dir.toFile().exists()) {
            throw new IllegalArgumentException("Photo path does not exist: " + photoPath);
        }
    }

    public Photo getPhoto(String fileName) {
        return photoRepository.findById(fileName).orElse(null);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }

    private boolean isValidFileName(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            return false;
        }
        if(!PhotoType.isValidFileName(fileName)) {
            return false;
        }
        return true;
    }

    public boolean fileExists(String fileName) {
        Path path = dir.resolve(fileName);
        if(!path.toFile().exists()) {
            return false;
        }
        return true;
    }

    /**
     * Saves the raw photo bytes to disk (overwriting if already exists),
     * then upserts the Photo document in MongoDB.
     *
     * @param data         the image bytes
     * @param fileName     the name (and key) of the photo
     * @param title        user-friendly title
     * @param description  optional description
     * @return the saved Photo record
     * @throws IOException if writing to disk fails
     * @throws IllegalArgumentException on invalid fileName
     */
    private Photo savePhotoFromData(byte[] data, Photo photo) throws IOException {
        // Validate the file name
        if(!isValidFileName(photo.fileName())) {
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