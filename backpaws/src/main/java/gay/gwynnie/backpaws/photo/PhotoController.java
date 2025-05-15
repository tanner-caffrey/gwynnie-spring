package gay.gwynnie.backpaws.photo;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class PhotoController {

    @Autowired
    private PhotoService photoService;

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

    @PostMapping(path = "/upload",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Photo> handleUpload(
        @RequestParam("photo") MultipartFile photo,
        @RequestParam("title") String title,
        @RequestParam("description") String description) {
        if (photo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String filename = StringUtils.cleanPath(photo.getOriginalFilename());
        Photo photoData = new Photo(filename, title, description, PhotoType.getFromFilename(filename));
        try {
            Photo saved = photoService.savePhoto(
                photo.getBytes(),
                photoData
            );

            URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/photos/{filename}")
                .buildAndExpand(saved.fileName())
                .toUri();

            return ResponseEntity
                .created(location)
                .body(saved);

        } catch (IllegalArgumentException e) {
            // invalid filename, etc.
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            // I/O failure writing file
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
