package Utils;

import java.awt.*;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

        GraphicsUtils.fillRect(graphics, 0, 0, image.getWidth(), image.getHeight());

        graphics.dispose();
    }

    // creates a new image of the desired size, then copies and pastes the original image to fit inside the new image
    // if new image is larger, empty space is filled in with emptyColor
    // if new image is smaller, original image will be cut off by appropriate amount to fit
    public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight, Color emptyColor) {
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
        clearImage(newImage, emptyColor);
        Graphics2D newImageGraphics = newImage.createGraphics();
        newImageGraphics.drawImage(image.getSubimage(0, 0, Math.min(image.getWidth(), newImage.getWidth()), Math.min(image.getHeight(), newImage.getHeight())), 0, 0, null);
        newImageGraphics.dispose();
        return newImage;
    }

    // creates new image deepcopy from a given image
    public static BufferedImage copyImage(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(image.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    // attempts to convert a generic awt image into a buffered image
    // got this entire thing from https://stackoverflow.com/a/41026422/16948475
    public static BufferedImage convertImageToBufferedImage(Image image) {
        if(image instanceof BufferedImage) return (BufferedImage)image;
        Lock lock = new ReentrantLock();
        Condition size = lock.newCondition(), data = lock.newCondition();
        ImageObserver o = (img, infoflags, x, y, width, height) -> {
            lock.lock();
            try {
                if((infoflags&ImageObserver.ALLBITS)!=0) {
                    size.signal();
                    data.signal();
                    return false;
                }
                if((infoflags&(ImageObserver.WIDTH|ImageObserver.HEIGHT))!=0)
                    size.signal();
                return true;
            }
            finally { lock.unlock(); }
        };
        BufferedImage bi;
        lock.lock();
        try {
            int width, height=0;
            while( (width=image.getWidth(o))<0 || (height=image.getHeight(o))<0 )
                size.awaitUninterruptibly();
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            try {
                g.setBackground(new Color(0, true));
                g.clearRect(0, 0, width, height);
                while(!g.drawImage(image, 0, 0, o)) data.awaitUninterruptibly();
            } finally { g.dispose(); }
        } finally { lock.unlock(); }
        return bi;
    }
}
