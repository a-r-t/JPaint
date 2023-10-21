package StatusBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Canvas.Canvas;
import Canvas.CanvasListener;
import Models.ChoicesHolder;

public class StatusBar extends JPanel implements CanvasListener {
    private Canvas canvas;
    private ChoicesHolder choicesHolder;
    private JLabel cursorPositionLabel;
    private JLabel rectangleSelectDimensionLabel;
    private JLabel canvasSizeLabel;
    private JLabel cursorPositionIcon;
    private JLabel rectangleSelectDimensionIcon;
    private JLabel canvasSizeIcon;

    public StatusBar(Canvas canvas, ChoicesHolder choicesHolder) {
        setBackground(new Color(240, 240, 240));
        setLayout(null);
        setPreferredSize(new Dimension(getPreferredSize().width, 24));

        this.canvas = canvas;
        this.choicesHolder = choicesHolder;

        cursorPositionLabel = new JLabel("test");
        cursorPositionLabel.setSize(80, 10);
        this.add(cursorPositionLabel);

        rectangleSelectDimensionLabel = new JLabel("test2");
        rectangleSelectDimensionLabel.setSize(50, 10);
        this.add(rectangleSelectDimensionLabel);

        canvasSizeLabel = new JLabel("test3");
        canvasSizeLabel.setSize(50, 10);
        this.add(canvasSizeLabel);

        cursorPositionIcon = new JLabel(new ImageIcon(StatusBar.class.getResource("/cursor-position-icon.png")));
        cursorPositionIcon.setSize(16, 16);
        this.add(cursorPositionIcon);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                cursorPositionIcon.setLocation(new Point(10, getHeight() / 2 - cursorPositionIcon.getHeight() / 2));
                cursorPositionLabel.setLocation(new Point(30, getHeight() / 2 - cursorPositionLabel.getHeight() / 2));
                
                rectangleSelectDimensionLabel.setLocation(new Point(100, getHeight() / 2 - rectangleSelectDimensionLabel.getHeight() / 2));
                
                canvasSizeLabel.setLocation(new Point(200, getHeight() / 2 - canvasSizeLabel.getHeight() / 2));


            }

        });
    }

    @Override
    public void onCursorMove(Point location) {
        if (location != null && location.x >= 0 && location.x / choicesHolder.getScale() < canvas.getCanvasWidth() && location.y >= 0 && location.y / choicesHolder.getScale() < canvas.getCanvasHeight()) {
            cursorPositionLabel.setText(String.format("%s, %spx", location.x / choicesHolder.getScale(), location.y / choicesHolder.getScale()));
        }
        else {
            cursorPositionLabel.setText("");
        }
    }

    @Override
    public void onCanvasSizeChange(int width, int height) {

    }

    @Override
    public void onRectangleSelectChange(Rectangle selectedBounds) {
       
    }

    @Override
    public void onPaintColorChanged(Color color) {
        // unused interface method
    }

    @Override
    public void onEraseColorChanged(Color color) {
        // unused interface method
    }

    @Override
    public void onEyeDropperUsedToChangePaintColor() {
        // unused interface method
    }

    @Override
    public void onSelectedSubImageChanged(BufferedImage subImage) {
        // unused interface method
    }    
}
