package gay.gwynnie.backpaws.photo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    // @GetMapping("/photo")
    // public ResponseEntity<Photo> getPhoto(@RequestParam(value = "fileName") String fileName) {
    //     Photo photo = photoService.getPhoto(fileName);
        
    //     if (photo != null) {
    //         return ResponseEntity.ok(photo);
    //     } else {
    //         return ResponseEntity.notFound().build();
    //     }
    // }

    @GetMapping("/photos/{fileName:.+}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String fileName) {
        try{
            Resource file = photoService.loadAsResource(fileName);
            String contentType = Files.probeContentType(Paths.get(photoService.getPhotoPath(), fileName));
            MediaType mediaType = (contentType != null) ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path={"/photos", "/photos/"})
    public ResponseEntity<List<Photo>> getAllPhotos() {
        List<Photo> photos = photoService.getAllPhotos();
        
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
