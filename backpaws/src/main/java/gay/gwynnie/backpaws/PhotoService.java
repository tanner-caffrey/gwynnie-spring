package gay.gwynnie.backpaws;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    @Value("${PHOTO_PATH}")
    private String photoPath;

    public Photo getPhoto(String fileName) {
        // Logic to retrieve the photo from the file system or database
        // For example, you can use a service to fetch the photo details
        if(isValidFileName(fileName) && fileExists(fileName)) {
            return new Photo(fileName, "Title", "Description", PhotoType.getFromFilename(fileName), photoPath + "/" + fileName);
        } else {
            return null; // or throw an exception
        }
    }

    public List<Photo> getAllPhotos() {
        // Logic to retrieve all photos from the file system or database
        return List.of(); // Placeholder for actual implementation
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

    private boolean fileExists(String fileName) {
        Path path = Paths.get(photoPath, fileName);
        if(!path.toFile().exists()) {
            return false;
        }
        return true;
    }

    public boolean savePhotoFromData(byte[] data, String fileName, String title, String description) {
        // Logic to save the photo to the file system or database
        // For example, you can use a service to save the photo details
        if(isValidFileName(fileName)) {
            // Save the photo data to the file system or database
            return true; // Placeholder for actual implementation
        } else {
            return false; // Invalid file name
        }
    }

    public boolean savePhoto(Photo photo) {
        return true;
    }

}