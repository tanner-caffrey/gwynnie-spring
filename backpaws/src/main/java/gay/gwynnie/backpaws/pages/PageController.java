package gay.gwynnie.backpaws.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import gay.gwynnie.backpaws.photo.PhotoService;

@Controller
public class PageController {

  @Autowired
  private PhotoService photoService;

  @GetMapping("/upload")
  public String uploadPage() {
    return "forward:/upload.html";
  }

  @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("photos", photoService.getAllPhotos());
        return "gallery";   // resolves to templates/gallery.html
    }
}