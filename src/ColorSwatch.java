import java.awt.*;

public class ColorSwatch {

    protected Color color;
    protected Color outerBorderColor;
    protected Color hoveredOuterBorderColor;
    protected Color innerBorderColor;
    protected Color hoveredInnerBorderColor;
    protected Point location;
    protected Dimension size;
    protected boolean isHovered;
    protected boolean showBorder;

    public ColorSwatch(Color color, Point location, Dimension size) {
        this.color = color;
        this.location = location;
        this.size = size;
        this.showBorder = true;
        outerBorderColor = new Color(160, 160, 160);
        hoveredOuterBorderColor = new Color(100, 165, 231);

        innerBorderColor = new Color(255, 255, 255);
        hoveredInnerBorderColor = new Color(203, 228, 253);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public void setHovered(boolean hovered) {
        isHovered = hovered;
    }

    public Color getOuterBorderColor() {
        return outerBorderColor;
    }

    public void setOuterBorderColor(Color outerBorderColor) {
        this.outerBorderColor = outerBorderColor;
    }

    public Color getHoveredOuterBorderColor() {
        return hoveredOuterBorderColor;
    }

    public void setHoveredOuterBorderColor(Color hoveredOuterBorderColor) {
        this.hoveredOuterBorderColor = hoveredOuterBorderColor;
    }

    public Color getInnerBorderColor() {
        return innerBorderColor;
    }

    public void setInnerBorderColor(Color innerBorderColor) {
        this.innerBorderColor = innerBorderColor;
    }

    public Color getHoveredInnerBorderColor() {
        return hoveredInnerBorderColor;
    }

    public void setHoveredInnerBorderColor(Color hoveredInnerBorderColor) {
        this.hoveredInnerBorderColor = hoveredInnerBorderColor;
    }

    public boolean isPointInBounds(Point p) {
        return p.x >= location.x && p.x < location.x + size.width && p.y >= location.y && p.y < location.y + size.height;
    }

    public boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public void paint(Graphics2D brush) {
        Color oldColor = brush.getColor();

        if (showBorder) {
            if (isHovered) {
                brush.setColor(hoveredInnerBorderColor);
                brush.fillRect(location.x - 2, location.y - 2, size.width + 3, size.height + 3);
                brush.setColor(hoveredOuterBorderColor);
                brush.drawRect(location.x - 2, location.y - 2, size.width + 3, size.height + 3);
            } else {
                brush.setColor(innerBorderColor);
                brush.fillRect(location.x - 2, location.y - 2, size.width + 3, size.height + 3);
                brush.setColor(outerBorderColor);
                brush.drawRect(location.x - 2, location.y - 2, size.width + 3, size.height + 3);
            }
        }

        brush.setColor(color);
        brush.fillRect(location.x, location.y, size.width, size.height);

        brush.setColor(oldColor);
    }
}
