import java.awt.*;

public interface CanvasListener {
    void onPaintColorChanged(Color color);
    void onEraseColorChanged(Color color);
    void onEyeDropperUsedToChangePaintColor();
}
