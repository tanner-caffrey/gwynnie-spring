package gay.gwynnie.backpaws;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhotoController {
    
    @Value("${PHOTO_PATH}")
    private String photoPath;

    @Autowired
    private PhotoService photoService;

    @GetMapping("/photo")
    public ResponseEntity<Photo> getPhoto(@RequestParam(value = "fileName") String fileName) {
        Photo photo = photoService.getPhoto(fileName);
        
        if (photo != null) {
            return ResponseEntity.ok(photo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/photos")
    public ResponseEntity<List<Photo>> getAllPhotos() {
        List<Photo> photos = photoService.getAllPhotos();
        
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
