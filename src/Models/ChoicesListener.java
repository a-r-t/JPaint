package Models;

import Toolstrip.Tool;

import java.awt.*;

public interface ChoicesListener {
    void onToolChanged(Tool tool);
    void onPaintColorChanged(Color color);
    void onEraseColorChanged(Color color);
    void onScaleChanged(int scale);
}
