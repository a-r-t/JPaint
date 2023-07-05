import java.awt.*;

public interface SelectionsListener {
    void onToolChanged(Tool tool);
    void onPaintColorChanged(Color color);
    void onEraseColorChanged(Color color);
}
