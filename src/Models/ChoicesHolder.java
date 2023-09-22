package Models;

import Toolstrip.Tool;
import Utils.ColorUtils;

import java.awt.*;
import java.util.ArrayList;

public class ChoicesHolder {
    private Tool tool;
    private Tool previousTool;
    private Color paintColor;
    private Color eraseColor;
    private int scale;
    private ArrayList<ChoicesListener> listeners = new ArrayList<>();

    public ChoicesHolder() {
        tool = null;
        previousTool = null;
        paintColor = new Color(0, 0, 0);
        eraseColor = new Color(255, 255, 255);
        scale = 1;
    }

    public Tool getTool() {
        return tool;
    }

    public Tool getPreviousTool() {
        return previousTool;
    }

    public void setTool(Tool tool) {
        previousTool = this.tool;
        this.tool = tool;

        for (ChoicesListener listener : listeners) {
            listener.onToolChanged(this.tool);
        }
    }

    public Color getPaintColor() {
        return paintColor;
    }

    public int getPaintColorAsIntRGB() {
        return ColorUtils.getIntFromColor(paintColor);
    }

    public void setPaintColor(Color paintColor) {
        this.paintColor = paintColor;

        for (ChoicesListener listener : listeners) {
            listener.onPaintColorChanged(this.paintColor);
        }
    }

    public Color getEraseColor() {
        return eraseColor;
    }

    public int getEraseColorAsIntRGB() {
        return ColorUtils.getIntFromColor(eraseColor);
    }

    public void setEraseColor(Color eraseColor) {
        this.eraseColor = eraseColor;

        for (ChoicesListener listener : listeners) {
            listener.onEraseColorChanged(this.eraseColor);
        }
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;

        for (ChoicesListener listener : listeners) {
            listener.onScaleChanged(this.scale);
        }
    }

    public void addListener(ChoicesListener listener) {
        listeners.add(listener);
    }
}
