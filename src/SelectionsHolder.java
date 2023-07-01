import java.awt.*;

public class SelectionsHolder {
    private Tool tool;
    private Color paintColor;
    private Color eraseColor;

    public SelectionsHolder() {
        tool = null;
        paintColor = new Color(0, 0, 0);
        eraseColor = new Color(255, 255, 255);
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }

    public Color getPaintColor() {
        return paintColor;
    }

    public int getPaintColorAsIntRGB() {
        return ColorUtils.getIntFromColor(paintColor);
    }

    public void setPaintColor(Color paintColor) {
        this.paintColor = paintColor;
    }

    public Color getEraseColor() {
        return eraseColor;
    }

    public int getEraseColorAsIntRGB() {
        return ColorUtils.getIntFromColor(eraseColor);
    }

    public void setEraseColor(Color eraseColor) {
        this.eraseColor = eraseColor;
    }
}
