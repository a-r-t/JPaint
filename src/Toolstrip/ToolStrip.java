package Toolstrip;

import Models.ChoicesHolder;
import Models.ChoicesListener;
import Canvas.*;
import Utils.ImageButton;
import Utils.LabeledColorSwatch;
import Utils.MouseClick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ToolStrip extends JPanel implements ChoicesListener, CanvasListener {
    private ArrayList<ToolButton> toolButtons = new ArrayList<>();
    private ChoicesHolder choicesHolder;
    private ColorSelect colorSelect;
    private LabeledColorSwatch paintColorDisplay;
    private LabeledColorSwatch eraseColorDisplay;
    private Tool previousPaintSelectedTool;
    private ImageButton zoomIn;
    private ImageButton zoomOut;
    private JSeparator separator1;
    private JSeparator separator2;

    public ToolStrip(ChoicesHolder choicesHolder) {
        this.choicesHolder = choicesHolder;

        setBackground(new Color(245, 246, 247));
        setPreferredSize(new Dimension(getPreferredSize().width, 60));
        setLayout(null);

        ToolButton pencil = new ToolButton("pencil-icon-transparent.png", Tool.PENCIL);
        ToolButton bucket = new ToolButton("bucket-icon-transparent.png", Tool.BUCKET);
        ToolButton eyedropper = new ToolButton("dropper-icon-transparent.png", Tool.EYE_DROPPER);
        ToolButton eraser = new ToolButton("eraser-icon-transparent.png", Tool.ERASER);
        ToolButton rectangleSelect = new ToolButton("rectangle-select-icon.png", Tool.RECTANGLE_SELECT);
        ToolButton freeFormSelect = new ToolButton("freeform-select-icon.png", Tool.FREE_FORM_SELECT);

        toolButtons.add(pencil);
        toolButtons.add(bucket);
        toolButtons.add(eyedropper);
        // toolButtons.add(eraser); TODO: fix eraser to actually be useful
        toolButtons.add(rectangleSelect);
        // toolButtons.add(freeFormSelect); TODO: figure out if I am actually going to implement this or not

        colorSelect = new ColorSelect(this.choicesHolder);
        add(colorSelect);

        paintColorDisplay = new LabeledColorSwatch(choicesHolder.getPaintColor(), new Point(0, 0), new Dimension(24, 24), "Paint");
        eraseColorDisplay = new LabeledColorSwatch(choicesHolder.getEraseColor(), new Point(0, 0), new Dimension(24, 24), "Erase");

        zoomIn = new ImageButton("zoom-in-icon.png");
        zoomOut = new ImageButton("zoom-out-icon.png");

        separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setSize(1, 60);
        //add(separator1);
        separator2 = new JSeparator(SwingConstants.VERTICAL);
        separator2.setSize(1, 60);
        //add(separator2);

        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean needsRepaint = false;
                boolean oldState = false;
                for (int i = 0; i < toolButtons.size(); i++) {
                    ToolButton tb = toolButtons.get(i);
                    oldState = tb.isHovered();

                    if (tb.isPointInBounds(e.getPoint())) {
                        toolButtons.forEach(toolButton -> toolButton.setHovered(false));
                        tb.setHovered(true);
                    } else {
                        tb.setHovered(false);
                    }

                    if (oldState != tb.isHovered()) {
                        needsRepaint = true;
                        if (tb.isHovered()) {
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                        }
                        else {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                }

                oldState = zoomIn.isHovered();
                if (zoomIn.isPointInBounds(e.getPoint())) {
                    zoomIn.setHovered(true);
                }
                else {
                    zoomIn.setHovered(false);
                }
                if (oldState != zoomIn.isHovered()) {
                    needsRepaint = true;
                    if (zoomIn.isHovered()) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                    else {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }

                oldState = zoomOut.isHovered();
                if (zoomOut.isPointInBounds(e.getPoint())) {
                    zoomOut.setHovered(true);
                }
                else {
                    zoomOut.setHovered(false);
                }
                if (oldState != zoomOut.isHovered()) {
                    needsRepaint = true;
                    if (zoomOut.isHovered()) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                    else {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }

                if (needsRepaint) {
                    repaint();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
                if (mouseClick == MouseClick.LEFT_CLICK) {
                    boolean needsRepaint = false;
                    boolean oldState = false;
                    for (int i = 0; i < toolButtons.size(); i++) {
                        ToolButton tb = toolButtons.get(i);
                        oldState = tb.isSelected();

                        if (tb.isPointInBounds(e.getPoint())) {
                            toolButtons.forEach(toolButton -> toolButton.setSelected(false));
                            tb.setSelected(true);
                            choicesHolder.setTool(tb.getTool());
                            if (tb.getTool() == Tool.PENCIL || tb.getTool() == Tool.BUCKET) {
                                previousPaintSelectedTool = tb.getTool();
                            }
                        }

                        if (oldState != tb.isSelected()) {
                            needsRepaint = true;
                        }
                    }

                    oldState = zoomIn.isSelected();
                    if (zoomIn.isPointInBounds(e.getPoint())) {
                        zoomIn.setSelected(true);
                        choicesHolder.setScale(choicesHolder.getScale() + 1);
                    } else {
                        zoomIn.setSelected(false);
                    }
                    if (oldState != zoomIn.isSelected()) {
                        needsRepaint = true;
                    }

                    oldState = zoomOut.isSelected();
                    if (zoomOut.isPointInBounds(e.getPoint())) {
                        zoomOut.setSelected(true);
                        choicesHolder.setScale(choicesHolder.getScale() - 1);
                    } else {
                        zoomOut.setSelected(false);
                    }
                    if (oldState != zoomOut.isHovered()) {
                        needsRepaint = true;
                    }

                    if (needsRepaint) {
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boolean needsRepaint = false;
                boolean oldState = false;

                oldState = zoomIn.isSelected();
                if (zoomIn.isSelected()) {
                    zoomIn.setSelected(false);
                }
                if (oldState != zoomIn.isSelected()) {
                    needsRepaint = true;
                }

                oldState = zoomOut.isSelected();
                if (zoomOut.isSelected()) {
                    zoomOut.setSelected(false);
                }
                if (oldState != zoomOut.isSelected()) {
                    needsRepaint = true;
                }

                if (needsRepaint) {
                    repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // if double click on either paint or erase color displays, load up color picker
                if (e.getClickCount() == 2) {
                    if (paintColorDisplay.isPointInBounds(e.getPoint())) {
                        new ColorPickerDialog(ToolStrip.this, paintColorDisplay.getColor(), new ColorPickerListener() {
                            @Override
                            public void onColorChosen(Color color) {
                                paintColorDisplay.setColor(color);
                                choicesHolder.setPaintColor(color);
                                repaint();
                            }
                        });
                    }
                    else if (eraseColorDisplay.isPointInBounds(e.getPoint())) {
                        new ColorPickerDialog(ToolStrip.this, eraseColorDisplay.getColor(), new ColorPickerListener() {
                            @Override
                            public void onColorChosen(Color color) {
                                eraseColorDisplay.setColor(color);
                                choicesHolder.setEraseColor(color);
                                repaint();
                            }
                        });
                    }
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pencil.setLocation(new Point(10, getHeight() / 2 - pencil.getHeight() / 2));
                bucket.setLocation(new Point(40, getHeight() / 2 - bucket.getHeight() / 2));
                eyedropper.setLocation(new Point(70, getHeight() / 2 - eyedropper.getHeight() / 2));
                // eraser.setLocation(new Point(100, getHeight() / 2 - eraser.getHeight() / 2));
                colorSelect.setLocation(new Point(225, getHeight() / 2 - colorSelect.getHeight() / 2));
                paintColorDisplay.setLocation(new Point(445, getHeight() / 2 - (int)paintColorDisplay.getSizeWithLabel().getHeight() / 2));
                eraseColorDisplay.setLocation(new Point(485, getHeight() / 2 - (int)eraseColorDisplay.getSizeWithLabel().getHeight() / 2));
                zoomIn.setLocation(new Point(145, getHeight() / 2 - zoomIn.getHeight() / 2));
                zoomOut.setLocation(new Point(175, getHeight() / 2 - zoomOut.getHeight() / 2));
                rectangleSelect.setLocation(new Point(100, getHeight() / 2 - eraser.getHeight() / 2));
                //freeFormSelect.setLocation(new Point(520, getHeight() / 2 - freeFormSelect.getHeight() / 2));

                separator1.setLocation(new Point(130, getHeight() / 2 - separator1.getHeight() / 2 ));
                separator2.setLocation(new Point(205, getHeight() / 2 - separator2.getHeight() / 2 ));
            }

        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;

        for (ToolButton tb : toolButtons) {
            tb.paint(brush);
        }
        paintColorDisplay.paint(brush);
        eraseColorDisplay.paint(brush);

        zoomIn.paint(brush);
        zoomOut.paint(brush);

        // separator lines
        g.setColor(new Color(112, 112, 112));
        g.drawLine(130, 0, 130, getHeight());
        g.drawLine(205, 0, 205, getHeight());
    }

    @Override
    public void onToolChanged(Tool tool) {
        for (int i = 0;  i < toolButtons.size(); i++) {
            ToolButton tb = toolButtons.get(i);
            tb.setSelected(tb.getTool() == tool);
        }
        repaint();
    }

    @Override
    public void onPaintColorChanged(Color color) {
        paintColorDisplay.setColor(color);
        repaint();
    }

    @Override
    public void onEraseColorChanged(Color color) {
        eraseColorDisplay.setColor(color);
        repaint();
    }

    @Override
    public void onScaleChanged(int scale) { }

    @Override
    public void onEyeDropperUsedToChangePaintColor() {
        if (previousPaintSelectedTool == null) {
            previousPaintSelectedTool = Tool.PENCIL;
        }
        for (int i = 0;  i < toolButtons.size(); i++) {
            ToolButton tb = toolButtons.get(i);
            tb.setSelected(tb.getTool() == previousPaintSelectedTool);
        }
        choicesHolder.setTool(previousPaintSelectedTool);
        repaint();
    }

    @Override
    public void onSelectedSubImageChanged(BufferedImage subImage) {
        // unused interface method
    }

    @Override
    public void onCursorMove(Point location) {
        // unused interface method
    }

    @Override
    public void onRectangleSelectChange(Rectangle selectedBounds) {
        // unused interface method
    }

    @Override
    public void onCanvasSizeChange(int width, int height) {
        // unused interface method
    }
}
