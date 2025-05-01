package gay.gwynnie.backpaws.photo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("photos")
public record Photo(@Id String fileName, String title, String description, PhotoType photoType) {}