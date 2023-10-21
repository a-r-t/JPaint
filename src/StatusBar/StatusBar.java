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

        cursorPositionIcon = new JLabel(new ImageIcon(StatusBar.class.getResource("/cursor-position-icon.png")));
        cursorPositionIcon.setSize(16, 16);
        this.add(cursorPositionIcon);

        cursorPositionLabel = new JLabel("");
        cursorPositionLabel.setSize(80, 10);
        this.add(cursorPositionLabel);

        rectangleSelectDimensionIcon = new JLabel(new ImageIcon(StatusBar.class.getResource("/selection-size-icon.png")));
        rectangleSelectDimensionIcon.setSize(16, 16);
        this.add(rectangleSelectDimensionIcon);

        rectangleSelectDimensionLabel = new JLabel("");
        rectangleSelectDimensionLabel.setSize(80, 10);
        this.add(rectangleSelectDimensionLabel);

        canvasSizeIcon = new JLabel(new ImageIcon(StatusBar.class.getResource("/canvas-size-icon.png")));
        canvasSizeIcon.setSize(16, 16);
        this.add(canvasSizeIcon);

        canvasSizeLabel = new JLabel("");
        canvasSizeLabel.setSize(80, 10);
        this.add(canvasSizeLabel);



        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                cursorPositionIcon.setLocation(new Point(10, getHeight() / 2 - cursorPositionIcon.getHeight() / 2));
                cursorPositionLabel.setLocation(new Point(30, getHeight() / 2 - cursorPositionLabel.getHeight() / 2));
           
                rectangleSelectDimensionIcon.setLocation(new Point(100, getHeight() / 2 - rectangleSelectDimensionIcon.getHeight() / 2));
                rectangleSelectDimensionLabel.setLocation(new Point(120, getHeight() / 2 - rectangleSelectDimensionLabel.getHeight() / 2));

                canvasSizeIcon.setLocation(new Point(190, getHeight() / 2 - canvasSizeIcon.getHeight() / 2));
                canvasSizeLabel.setLocation(new Point(210, getHeight() / 2 - canvasSizeLabel.getHeight() / 2));
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
    public void onRectangleSelectChange(Rectangle selectedBounds) {
        if (selectedBounds != null && selectedBounds.x > 0 && selectedBounds.height > 0) {
            rectangleSelectDimensionLabel.setText(String.format("%s x %spx", selectedBounds.width, selectedBounds.height));
        }
        else {
            rectangleSelectDimensionLabel.setText("");
        }
    }

    @Override
    public void onCanvasSizeChange(int width, int height) {
        canvasSizeLabel.setText(String.format("%s x %spx", width, height));
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
