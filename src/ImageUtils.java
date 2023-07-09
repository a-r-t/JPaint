import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageUtils {

    // the original BufferedImage "getSubimage" method returns a shared instance with the original, which is annoying
    // this method returns a new subimage that is a COPY from the original buffered image, leaving the two independent of one another
    public static BufferedImage getSubimage(BufferedImage image, int x, int y, int w, int h) {
        BufferedImage newImage = new BufferedImage(w, h, image.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-x, -y));
        g.dispose();
        return newImage;
    }
}
