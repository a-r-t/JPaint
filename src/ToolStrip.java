import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ToolStrip extends JPanel {
    private BufferedImage pencil;
    private BufferedImage bucket;
    private BufferedImage eraser;
    private BufferedImage eyedropper;
    private BufferedImage magnifyingGlass;

    private Point pencilLocation;
    private Point bucketLocation;
    private Point eraserLocation;
    private Point eyedropperLocation;
    private Point magnifyingGlassLocation;

    private Tool hoveredTool = null;
    private Tool previousHoveredTool = null;

    private SelectionsHolder selectionsHolder;

    public ToolStrip(SelectionsHolder selectionsHolder) {
        this.selectionsHolder = selectionsHolder;

        setBackground(new Color(245, 246, 247));
        setPreferredSize(new Dimension(getPreferredSize().width, 50));
        setLayout(null);

        try {
            pencil = ImageIO.read(ToolStrip.class.getResource("/pencil-icon-transparent.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pencilLocation = new Point(10, getHeight() / 2 - pencil.getHeight() / 2);
        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                previousHoveredTool = hoveredTool;
                if (isPointInsideRectangle(e.getPoint(), new Rectangle(pencilLocation.x - 5, pencilLocation.y - 5, pencil.getWidth() + 10, pencil.getHeight() + 10))) {
                    hoveredTool = Tool.PENCIL;
                }
                else {
                    hoveredTool = null;
                }
                if (previousHoveredTool != hoveredTool) {
                    repaint();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Tool previousSelectedTool = ToolStrip.this.selectionsHolder.getTool();
                if (isPointInsideRectangle(e.getPoint(), new Rectangle(pencilLocation.x - 5, pencilLocation.y - 5, pencil.getWidth() + 10, pencil.getHeight() + 10))) {
                    ToolStrip.this.selectionsHolder.setTool(Tool.PENCIL);
                }

                if (previousSelectedTool != ToolStrip.this.selectionsHolder.getTool()) {
                    repaint();
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pencilLocation = new Point(10, getHeight() / 2 - pencil.getHeight() / 2);
            }

        });
    }

    private boolean isPointInsideRectangle(Point p, Rectangle r) {
        return p.x >= r.x && p.x < r.x + r.width && p.y >= r.y && p.y < r.y + r.height;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;

        if (this.selectionsHolder.getTool() == Tool.PENCIL) {
            brush.setColor(new Color(201, 224, 247));
            brush.fillRect(pencilLocation.x - 5, pencilLocation.y - 5, pencil.getWidth() + 10, pencil.getHeight() + 10);

            brush.setColor(new Color(98, 162, 228));
            brush.setStroke(new BasicStroke(2));
            brush.drawRect(pencilLocation.x - 5, pencilLocation.y - 5, pencil.getWidth() + 10, pencil.getHeight() + 10);
        }

        if (hoveredTool == Tool.PENCIL && this.selectionsHolder.getTool() != Tool.PENCIL) {
            brush.setColor(new Color(232, 239, 247));
            brush.fillRect(pencilLocation.x - 5, pencilLocation.y - 5, pencil.getWidth() + 10, pencil.getHeight() + 10);

            brush.setColor(new Color(164, 206, 249));
            brush.setStroke(new BasicStroke(2));
            brush.drawRect(pencilLocation.x - 5, pencilLocation.y - 5, pencil.getWidth() + 10, pencil.getHeight() + 10);
        }

        brush.drawImage(pencil, pencilLocation.x, pencilLocation.y, pencil.getWidth(), pencil.getHeight(), null);



    }
}
