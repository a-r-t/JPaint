package Utils;

import java.awt.*;

public class GraphicsUtils {
    public static void fillRect(Graphics2D graphics, int x, int y, int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                graphics.fillRect(x + j, y + i, 1, 1);
            }
        }
    }
}
