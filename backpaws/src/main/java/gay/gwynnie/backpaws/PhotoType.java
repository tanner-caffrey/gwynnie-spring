package gay.gwynnie.backpaws;

public enum PhotoType {
    JPEG("jpg"),
    PNG("png"),
    GIF("gif"),
    BMP("bmp"),
    TIFF("tiff"),
    WEBP("webp"),
    HEIC("heic"),
    AVIF("avif"),
    SVG("svg");

    private final String extension;

    PhotoType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static PhotoType fromExtension(String extension) {
        for (PhotoType type : values()) {
            if (type.extension.equalsIgnoreCase(extension)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported file extension: " + extension);
    }

    public static boolean isValidExtension(String extension) {
        for (PhotoType type : values()) {
            if (type.extension.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    public static PhotoType getFromFilename(String filename) {
        String[] parts = filename.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }
        String extension = parts[parts.length - 1];
        return fromExtension(extension);
    }

    public static boolean isValidFileName(String fileName) {
        try{
            getFromFilename(fileName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
