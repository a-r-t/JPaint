package Canvas;

import Models.ChoicesHolder;
import Models.ChoicesListener;
import Tools.*;
import Toolstrip.Tool;
import Utils.*;
import Utils.Image;
import javafx.scene.input.KeyCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Canvas extends JPanel implements ChoicesListener, CanvasHistoryListener {
    private int canvasWidth = 400;
    private int canvasHeight = 400;
    private Image mainImage;
    private ChoicesHolder choicesHolder;

    private boolean isLeftMouseDown;
    private boolean isRightMouseDown;
    private final int EXTRA_CANVAS_WIDTH = 10;
    private final int EXTRA_CANVAS_HEIGHT = 10;
    private final int CANVAS_START_X = 10;
    private final int CANVAS_START_Y = 10;

    private CanvasMode canvasMode;
    private CanvasHistory canvasHistory;

    // for canvas resizing
    private CanvasResizeDirection canvasResizeDirection = null;
    private Rectangle canvasResizeBorder;
    private Rectangle horizontalResizer;
    private Rectangle verticalResizer;
    private Rectangle diagonalResizer;
    private boolean allowCanvasResizing = true;

    // for selection
    private Image selectionImageLayer;

    private ArrayList<CanvasListener> listeners = new ArrayList<>();

    private CanvasCursorManager cursors;

    private CanvasMouseInfoHolder mouseInfoHolder;

    private PencilTool pencilTool;
    private BucketTool bucketTool;
    private EyeDropperTool eyeDropperTool;
    private EraserTool eraserTool;
    private RectangleSelectTool rectangleSelectTool;

    public Canvas(ChoicesHolder choicesHolder) {
        mainImage = new Image(canvasWidth, canvasHeight);
        mainImage.clear(new Color(255, 255, 255));

        selectionImageLayer = new Image(canvasWidth, canvasHeight, ImageType.ARGB);
        selectionImageLayer.clear(new Color(0, 0, 0, 0));

        this.isLeftMouseDown = false;
        this.isRightMouseDown = false;
        this.choicesHolder = choicesHolder;
        this.canvasMode = CanvasMode.PAINT;
        this.canvasHistory = new CanvasHistory(this);
        canvasHistory.addListener(this);
        this.cursors = new CanvasCursorManager();
        this.mouseInfoHolder = new CanvasMouseInfoHolder(this);
        this.pencilTool = new PencilTool(this, choicesHolder, mouseInfoHolder, cursors);
        this.bucketTool = new BucketTool(this, choicesHolder, mouseInfoHolder, cursors);
        this.eyeDropperTool = new EyeDropperTool(this, choicesHolder, mouseInfoHolder, cursors, listeners);
        this.eraserTool = new EraserTool(this, choicesHolder, mouseInfoHolder, cursors);
        this.rectangleSelectTool = new RectangleSelectTool(this, choicesHolder, mouseInfoHolder, cursors, listeners);

        updateCanvasResizers();

        setBackground(new Color(197, 207, 223));
        setBorder(BorderFactory.createMatteBorder(5, 5, 0, 0, new Color(197, 207, 223)));

        this.setDoubleBuffered(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
                mouseInfoHolder.mouseButtonPressed(mouseClick);
                mouseInfoHolder.updateMousePosition(e.getPoint());

                if (mouseClick == MouseClick.LEFT_CLICK && allowCanvasResizing) {
                    if (spreadRectangle(horizontalResizer, 0, 5).contains(e.getPoint())) {
                        canvasMode = CanvasMode.RESIZE;
                        canvasResizeDirection = CanvasResizeDirection.EAST;
                    } else if (spreadRectangle(verticalResizer, 5, 0).contains(e.getPoint())) {
                        canvasMode = CanvasMode.RESIZE;
                        canvasResizeDirection = CanvasResizeDirection.SOUTH;
                    } else if (spreadRectangle(diagonalResizer, 5, 5).contains(e.getPoint())) {
                        canvasMode = CanvasMode.RESIZE;
                        canvasResizeDirection = CanvasResizeDirection.SOUTH_EAST;
                    }
                }
                if (canvasMode == CanvasMode.PAINT && isLeftOrRightClick(mouseClick) && isMouseInCanvas(e.getPoint()) && choicesHolder.getTool() != null) {
                    switch(choicesHolder.getTool()) {
                        case PENCIL:
                            pencilTool.mousePressed();
                            break;
                        case BUCKET:
                            bucketTool.mousePressed();
                            break;
                        case EYE_DROPPER:
                            eyeDropperTool.mousePressed();
                            break;
                        case ERASER:
                            eraserTool.mousePressed();
                            break;
                        case RECTANGLE_SELECT:
                            rectangleSelectTool.mousePressed();
                            break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
                mouseInfoHolder.mouseButtonReleased(mouseClick);
                mouseInfoHolder.updateMousePosition(e.getPoint());
                if (canvasMode == CanvasMode.RESIZE && mouseClick == MouseClick.LEFT_CLICK) {

                    if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                        canvasWidth = Math.max(e.getX() / choicesHolder.getScale(), 1);
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                        canvasHeight = Math.max(e.getY() / choicesHolder.getScale(), 1);
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH_EAST) {
                        canvasWidth = Math.max(e.getX() / choicesHolder.getScale(), 1);
                        canvasHeight = Math.max(e.getY() / choicesHolder.getScale(), 1);
                    }
                    resizeCanvas(canvasWidth, canvasHeight);
                    canvasMode = CanvasMode.PAINT;
                    canvasResizeDirection = null;
                    setCursor(Cursor.getDefaultCursor());
                    revalidate();
                    repaint();
                }
                else if (choicesHolder.getTool() == Tool.PENCIL) {
                    pencilTool.mouseReleased();
                }
                else if (choicesHolder.getTool() == Tool.ERASER) {
                    eraserTool.mouseReleased();
                }
                else if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                    rectangleSelectTool.mouseReleased();
                }

                else if (isLeftMouseDown && mouseClick == MouseClick.LEFT_CLICK) {
                    isLeftMouseDown = false;
                }
                else if (isRightMouseDown && mouseClick == MouseClick.RIGHT_CLICK) {
                    isRightMouseDown = false;
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseInfoHolder.updateMousePosition(e.getPoint());
                Point mousePosition = e.getPoint();
                setCursor(getProperCursor(mousePosition));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseInfoHolder.updateMousePosition(e.getPoint());

                if (canvasMode == CanvasMode.PAINT) {
                    if (choicesHolder.getTool() == Tool.PENCIL) {
                        pencilTool.mouseDragged();
                    }
                    else if (choicesHolder.getTool() == Tool.ERASER) {
                        eraserTool.mouseDragged();
                    }
                    else if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                        rectangleSelectTool.mouseDragged();
                    }
                }
                else if (canvasMode == CanvasMode.RESIZE) {
                    if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, e.getPoint().x, canvasHeight * choicesHolder.getScale());
                        repaint();
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, canvasWidth * choicesHolder.getScale(), e.getPoint().y);
                        repaint();
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH_EAST) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, e.getPoint().x, e.getPoint().y);
                        repaint();
                    }
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            }

        });
    }

    private Cursor getProperCursor(Point mousePosition) {
        if (allowCanvasResizing) {
            if (spreadRectangle(horizontalResizer, 0, 5).contains(mousePosition)) {
                return cursors.get(CanvasCursor.E_RESIZE_CURSOR);
            }
            else if (spreadRectangle(verticalResizer, 5, 0).contains(mousePosition)) {
                return cursors.get(CanvasCursor.S_RESIZE_CURSOR);
            }
            else if (spreadRectangle(diagonalResizer, 5, 5).contains(mousePosition)) {
                return cursors.get(CanvasCursor.SE_RESIZE_CURSOR);
            }
        }
        if (isMouseInCanvas(mousePosition)) {
            if (choicesHolder.getTool() == Tool.PENCIL) {
                return pencilTool.getCursor();
            }
            else if (choicesHolder.getTool() == Tool.BUCKET) {
                return bucketTool.getCursor();
            }
            else if (choicesHolder.getTool() == Tool.EYE_DROPPER) {
                return eyeDropperTool.getCursor();
            }
            else if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                return rectangleSelectTool.getCursor();
            }
        }
        return Cursor.getDefaultCursor();
    }

    // increase size of rectangle in all directions
    private Rectangle spreadRectangle(Rectangle original, int xSpread, int ySpread) {
        return new Rectangle(original.x - xSpread, original.y - ySpread, original.width + (xSpread * 2), original.height + (ySpread * 2));
    }

    private void updateCanvasResizers() {
        horizontalResizer = new Rectangle((CANVAS_START_X + canvasWidth * choicesHolder.getScale()),  ((CANVAS_START_Y + canvasHeight * choicesHolder.getScale()) / 2) + 2, 4, 4);
        verticalResizer = new Rectangle(((CANVAS_START_X + canvasWidth * choicesHolder.getScale()) / 2) + 2, CANVAS_START_Y + canvasHeight * choicesHolder.getScale(), 4, 4);
        diagonalResizer = new Rectangle(CANVAS_START_X + canvasWidth * choicesHolder.getScale(), CANVAS_START_Y + canvasHeight * choicesHolder.getScale(), 4, 4);
    }

    private boolean isLeftOrRightClick(MouseClick mouseClick) {
        return mouseClick == MouseClick.LEFT_CLICK || mouseClick == MouseClick.RIGHT_CLICK;
    }

    private boolean isMouseInCanvas(Point mousePosition) {
        return mousePosition.x - CANVAS_START_X >= 0 && mousePosition.x - CANVAS_START_X < mainImage.getWidth() * choicesHolder.getScale() && mousePosition.y - CANVAS_START_Y >= 0 && mousePosition.y - CANVAS_START_Y < mainImage.getHeight() * choicesHolder.getScale();
    }

    public void resizeCanvas(int newWidth, int newHeight) {
        canvasWidth = newWidth;
        canvasHeight = newHeight;
        mainImage.resize(newWidth, newHeight, choicesHolder.getEraseColor());
        updateCanvasResizers();

        selectionImageLayer.resize(newWidth, newHeight, new Color(0, 0, 0, 0));
        selectionImageLayer.clear(new Color(0, 0, 0, 0));
        repaint();
    }

    // this is just to make the scroll pane respect the bounds of the canvas's image
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( CANVAS_START_X + (canvasWidth * choicesHolder.getScale()) + EXTRA_CANVAS_WIDTH, CANVAS_START_Y + (canvasHeight * choicesHolder.getScale()) + EXTRA_CANVAS_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;

        Color oldColor = brush.getColor();
        Stroke oldStroke = brush.getStroke();

        // paint current image as canvas
        mainImage.paint(brush, CANVAS_START_X, CANVAS_START_Y, choicesHolder.getScale());

        // canvas resizers
        if (allowCanvasResizing) {
            brush.setColor(Color.white);
            brush.fillRect(horizontalResizer.x, horizontalResizer.y, horizontalResizer.width, horizontalResizer.height);
            brush.fillRect(verticalResizer.x, verticalResizer.y, verticalResizer.width, verticalResizer.height);
            brush.fillRect(diagonalResizer.x, diagonalResizer.y, diagonalResizer.width, diagonalResizer.height);

            brush.setColor(new Color(85, 85, 85));
            brush.drawRect(horizontalResizer.x, horizontalResizer.y, horizontalResizer.width, horizontalResizer.height);
            brush.drawRect(verticalResizer.x, verticalResizer.y, verticalResizer.width, verticalResizer.height);
            brush.drawRect(diagonalResizer.x, diagonalResizer.y, diagonalResizer.width, diagonalResizer.height);

            brush.setColor(oldColor);
            brush.setStroke(oldStroke);
        }

        if (canvasMode == CanvasMode.PAINT) {
            // select layer
            if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                selectionImageLayer.paint(brush, CANVAS_START_X, CANVAS_START_Y, choicesHolder.getScale());
            }
        }
        // canvas resize borders
        else if (canvasMode == CanvasMode.RESIZE) {
            brush.setColor(new Color(0, 0, 0));
            if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                for (int i = CANVAS_START_X; i < canvasResizeBorder.getX() + canvasResizeBorder.getWidth(); i += 2) {
                    brush.fillRect(i, CANVAS_START_Y, 1, 1);
                    brush.fillRect(i, CANVAS_START_Y + canvasHeight * choicesHolder.getScale(), 1, 1);
                }
                for (int i = CANVAS_START_Y; i < canvasResizeBorder.getY() + canvasResizeBorder.getHeight(); i += 2) {
                    brush.fillRect(CANVAS_START_X, i, 1, 1);
                    brush.fillRect((int) (canvasResizeBorder.getX() + canvasResizeBorder.getWidth()), i, 1, 1);
                }
            }
            else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                for (int i = CANVAS_START_X; i < canvasResizeBorder.getX() + canvasResizeBorder.getWidth(); i += 2) {
                    brush.fillRect(i, CANVAS_START_Y, 1, 1);
                    brush.fillRect(i, (int) (canvasResizeBorder.getY() + canvasResizeBorder.getHeight()), 1, 1);
                }
                for (int i = CANVAS_START_Y; i < canvasResizeBorder.getY() + canvasResizeBorder.getHeight(); i += 2) {
                    brush.fillRect(CANVAS_START_X, i, 1, 1);
                    brush.fillRect((int) (canvasResizeBorder.getX() + canvasResizeBorder.getWidth()), i, 1, 1);
                }
            }
            else if (canvasResizeDirection == CanvasResizeDirection.SOUTH_EAST) {
                for (int i = CANVAS_START_X; i < canvasResizeBorder.getX() + canvasResizeBorder.getWidth(); i += 2) {
                    brush.fillRect(i, CANVAS_START_Y, 1, 1);
                    brush.fillRect(i, (int) (canvasResizeBorder.getY() + canvasResizeBorder.getHeight()), 1, 1);
                }
                for (int i = CANVAS_START_Y; i < canvasResizeBorder.getY() + canvasResizeBorder.getHeight(); i += 2) {
                    brush.fillRect(CANVAS_START_X, i, 1, 1);
                    brush.fillRect((int) (canvasResizeBorder.getX() + canvasResizeBorder.getWidth()), i, 1, 1);
                }
            }

        }

        brush.setColor(oldColor);
    }
    
    public void addListener(CanvasListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onToolChanged(Tool tool) {
        Point mousePosition = MouseInfo.getPointerInfo().getLocation();

        // this Swing method updates the mousePosition variable in place...ew
        SwingUtilities.convertPointFromScreen(mousePosition, this);

        setCursor(getProperCursor(mousePosition));

        allowCanvasResizing = true;

        rectangleSelectTool.applyChanges();

        selectionImageLayer.clear(new Color(0, 0, 0, 0));

        repaint();
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
    public void onScaleChanged(int scale) {
        updateCanvasResizers();
        revalidate(); // updates scroll control to correct itself when canvas grows
        repaint();
    }

    public Image getMainImage() {
        return mainImage;
    }

    public void setMainImage(BufferedImage image) {
        this.mainImage = new Image(image);
    }

    public Image getSelectionImageLayer() {
        return selectionImageLayer;
    }

    public void setAllowCanvasResizing(boolean allowCanvasResizing) {
        this.allowCanvasResizing = allowCanvasResizing;
    }

    public int getStartX() {
        return CANVAS_START_X;
    }

    public int getStartY() {
        return CANVAS_START_Y;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public CanvasHistory getCanvasHistory() {
        return canvasHistory;
    }

    @Override
    public void onHistorySizeChange(int performedSize, int recallSize) {
        // interface method not used
    }

    @Override
    public void onUndo() {
        if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
            rectangleSelectTool.reset();
        }
    }

    @Override
    public void onRedo() {
        if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
            rectangleSelectTool.reset();
        }
    }

    // essentially "resets" the canvas to "fit" the main image (matches its size)
    public void fitCanvasToMainImage() {
        canvasWidth = mainImage.getWidth();
        canvasHeight = mainImage.getHeight();
        resizeCanvas(mainImage.getWidth(), mainImage.getHeight());
        updateCanvasResizers();
        revalidate();
        repaint();
    }

    // if rectangle tool is used and has selected a sub image, this returns that sub image
    // returns null if rectangle tool not selected or if no sub image is currently selected
    public BufferedImage getSelectedSubimage() {
        if (choicesHolder.getTool() != Tool.RECTANGLE_SELECT) {
            return null;
        }
        return rectangleSelectTool.getSelectedSubimage();

    }

    public void setSelectedSubimage(BufferedImage selectedSubimage) {
        if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
            rectangleSelectTool.setSelectedSubimage(selectedSubimage);
        }
    }
}
