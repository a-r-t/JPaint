package Utils;

import Utils.ColorUtils;
import Utils.ImageType;
import Utils.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
    private BufferedImage image;

    public Image(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public Image(int width, int height, ImageType type) {
        if (type == ImageType.RGB) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        else if (type == ImageType.ARGB) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        else {
            throw new IllegalArgumentException("Invalid ImageType");
        }
    }

    public Image(BufferedImage image) {
        this.image = image;
    }

    public Graphics2D getGraphics() {
        return image.createGraphics();
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public BufferedImage getRaw() {
        return image;
    }

    public int getRGB(int x, int y) {
        return image.getRGB(x, y);
    }

    public Color getRGBAsColor(int x, int y) {
        return ColorUtils.getColorFromInt(image.getRGB(x, y));
    }

    public void setRGB(int x, int y, int rgb) {
        image.setRGB(x, y, rgb);
    }

    public void setRGB(int x, int y, Color color) {
        image.setRGB(x, y, ColorUtils.getIntFromColor(color));
    }

    public void clear(Color color) {
        ImageUtils.clearImage(image, color);
    }

    public void resize(int newWidth, int newHeight, Color emptyColor) {
        image = ImageUtils.resizeImage(image, newWidth, newHeight, emptyColor);
    }

    public BufferedImage getSubImage(int x, int y, int width, int height) {
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (width > image.getWidth()) {
            width = image.getWidth();
        }
        if (height > image.getHeight()) {
            height = image.getHeight();
        }
        return ImageUtils.getSubimage(image, x, y, width, height);
    }

    public void paint(Graphics2D g, int x, int y) {
        g.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);
    }

    public void paint(Graphics2D g, int x, int y, int width, int height) {
        g.drawImage(image, x, y, width, height, null);
    }

    // TODO: alpha stuff
    public void paint(Graphics2D g, int x, int y, int scale) {
        g.drawImage(image, x, y, image.getWidth() * scale, image.getHeight() * scale, null);
    }

    public void paint(Graphics2D g, int x, int y, int width, int height, int scale) {
        g.drawImage(image, x, y, width * scale, height * scale, null);
    }

}
