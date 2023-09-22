package Canvas;

import java.awt.image.BufferedImage;

public class CanvasState {
    private BufferedImage mainImage;
    private int width;
    private int height;

    public CanvasState(BufferedImage mainImage, int width, int height) {
        this.mainImage = mainImage;
        this.width = width;
        this.height = height;
    }

    public BufferedImage getMainImage() {
        return mainImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
