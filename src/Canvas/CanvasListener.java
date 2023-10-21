package Canvas;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface CanvasListener {
    void onPaintColorChanged(Color color);
    void onEraseColorChanged(Color color);
    void onEyeDropperUsedToChangePaintColor();
    void onSelectedSubImageChanged(BufferedImage subImage);
    void onCursorMove(Point location);
    void onRectangleSelectChange(Rectangle selectedBounds);
    void onCanvasSizeChange(int width, int height);
}
