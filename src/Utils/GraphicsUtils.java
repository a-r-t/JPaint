package Utils;

import java.awt.*;

// This class exists because the awt Graphics2D implementation of "fillRect" and "drawRect" is different between Windows and MacOS
// It is always a pixel difference on the width and height, with MacOS adding the extra pixels
// In order to bridge the gap to make the app run correctly on both, I re-implemented these methods to force consistency
public class GraphicsUtils {
    public static void fillRect(Graphics2D graphics, int x, int y, int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                graphics.fillRect(x + j, y + i, 1, 1);
            }
        }
    }

    public static void drawRect(Graphics2D graphics, int x, int y, int width, int height) {
        graphics.drawLine(x, y, x + width - 1, y);
        graphics.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
        graphics.drawLine(x + width - 1, y + height - 1, x, y + height - 1);
        graphics.drawLine(x, y + height - 1, x, y);
    }
}
