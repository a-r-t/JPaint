import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class LabeledColorSwatch extends ColorSwatch {
    private String label;
    private Font font;
    private Color labelColor;

    public LabeledColorSwatch(Color color, Point location, Dimension size, String label) {
        super(color, location, size);
        this.label = label;
        this.font = new Font("Tahoma", Font.PLAIN, 11);
        this.labelColor = Color.black;
    }

    public Dimension getSizeWithLabel() {
        return new Dimension((int)size.getWidth(), (int)size.getHeight() + font.getSize());
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        this.labelColor = labelColor;
    }

    public void paint(Graphics2D brush) {
        super.paint(brush);

        Color oldColor = brush.getColor();
        Font oldFont = brush.getFont();


        brush.setColor(labelColor);
        brush.setFont(font);

        FontMetrics fm = brush.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(label, brush);
        int x = size.width / 2 - (int) r.getWidth() / 2;

        brush.drawString(label, location.x + x, (int)(location.y + size.getHeight() + r.getHeight()));

        brush.setColor(oldColor);
        brush.setFont(oldFont);
    }
}
