package gay.gwynnie.backpaws.pages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import gay.gwynnie.backpaws.photo.Photo;
import gay.gwynnie.backpaws.photo.PhotoService;

@Controller
public class PageController {

    @Autowired
    private PhotoService photoService;


    @GetMapping("/gallery")
    public String gallery(Model model) {
        List<Photo> photos = photoService.getAllPhotos();
        model.addAttribute("photos", photos);
        return "gallery";   // resolves to templates/gallery.html
    }
}
