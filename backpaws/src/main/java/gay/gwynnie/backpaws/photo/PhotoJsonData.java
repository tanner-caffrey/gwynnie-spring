package gay.gwynnie.backpaws.photo;

import java.util.List;

public record PhotoJsonData(
    String path,
    List<PhotoJson> photos
) {}
