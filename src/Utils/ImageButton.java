package Utils;

import Toolstrip.ToolStrip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageButton {
    protected BufferedImage image;
    protected Point location;
    protected Rectangle bounds;
    protected boolean isHovered;
    protected boolean isSelected;

    public ImageButton(String imageResource) {
        try {
            image = ImageIO.read(ToolStrip.class.getResource("/" + imageResource));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        location = new Point(0, 0);
        bounds = new Rectangle(location.x - 5, location.y - 5, image.getWidth() + 10, image.getHeight() + 10);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
        bounds = new Rectangle(location.x - 5, location.y - 5, image.getWidth() + 10, image.getHeight() + 10);
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
        bounds = new Rectangle(location.x - 5, location.y - 5, image.getWidth() + 10, image.getHeight() + 10);
    }

    public int getX() {
        return location.x;
    }

    public void setX(int x) {
        this.location = new Point(x, location.y);
        bounds = new Rectangle(location.x - 5, location.y - 5, image.getWidth() + 10, image.getHeight() + 10);
    }

    public int getY() {
        return location.y;
    }

    public void setY(int y) {
        this.location = new Point(location.x, y);
        bounds = new Rectangle(location.x - 5, location.y - 5, image.getWidth() + 10, image.getHeight() + 10);
    }

    public boolean isPointInBounds(Point p) {
        return p.x >= bounds.x && p.x < bounds.x + bounds.width && p.y >= bounds.y && p.y < bounds.y + bounds.height;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public void setHovered(boolean hovered) {
        isHovered = hovered;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void paint(Graphics2D brush) {
        Color oldColor = brush.getColor();
        Stroke oldStroke = brush.getStroke();

        if (isSelected) {
            brush.setColor(new Color(201, 224, 247));
            brush.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            brush.setColor(new Color(98, 162, 228));
            brush.setStroke(new BasicStroke(2));
            brush.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        else if (isHovered) {
            brush.setColor(new Color(232, 239, 247));
            brush.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            brush.setColor(new Color(164, 206, 249));
            brush.setStroke(new BasicStroke(2));
            brush.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        brush.drawImage(image, location.x, location.y, image.getWidth(), image.getHeight(), null);

        brush.setColor(oldColor);
        brush.setStroke(oldStroke);
    }
}
