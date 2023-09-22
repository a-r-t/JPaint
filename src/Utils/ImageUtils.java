package Utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageUtils {

    // the original BufferedImage "getSubimage" method returns a shared instance with the original, which is annoying
    // this method returns a new subimage that is a COPY from the original buffered image, leaving the two independent of one another
    public static BufferedImage getSubimage(BufferedImage image, int x, int y, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, image.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-x, -y));
        g.dispose();
        return newImage;
    }

    // clears all pixels in an image to a given color
    public static void clearImage(BufferedImage image, Color color) {
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(color);

        // handles transparency -- default is fully opaque
        if (color.getAlpha() == 0) {
            graphics.setComposite(AlphaComposite.Clear); // pixels will be fully transparent
        }
        else if (color.getAlpha() < 255) {
            graphics.setComposite(AlphaComposite.SrcOver.derive(color.getAlpha() / 255)); // pixels will have partial alpha applied
        }

        graphics.fillRect (0, 0, image.getWidth(), image.getHeight());

        graphics.dispose();
    }

    // creates a new image of the desired size, then copies and pastes the original image to fit inside the new image
    // if new image is larger, empty space is filled in with emptyColor
    // if new image is smaller, original image will be cut off by appropriate amount to fit
    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight, Color emptyColor) {
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        clearImage(newImage, emptyColor);
        Graphics2D newImageGraphics = newImage.createGraphics();
        newImageGraphics.drawImage(image.getSubimage(0, 0, Math.min(image.getWidth(), newImage.getWidth()), Math.min(image.getHeight(), newImage.getHeight())), 0, 0, null);
        newImageGraphics.dispose();
        return newImage;
    }

    // creates new image clone from a given image
    public static BufferedImage copyImage(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
