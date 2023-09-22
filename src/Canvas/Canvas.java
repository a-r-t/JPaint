package Canvas;

import Models.ChoicesHolder;
import Models.ChoicesListener;
import Tools.BucketTool;
import Tools.EraserTool;
import Tools.EyeDropperTool;
import Tools.PencilTool;
import Toolstrip.Tool;
import Utils.*;
import Utils.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Canvas extends JPanel implements ChoicesListener {
    private int canvasWidth = 400;
    private int canvasHeight = 400;
    private Image mainImage;
    private ChoicesHolder choicesHolder;

    private boolean isLeftMouseDown;
    private boolean isRightMouseDown;
    private Point previousMousePosition;
    private final int EXTRA_CANVAS_WIDTH = 10;
    private final int EXTRA_CANVAS_HEIGHT = 10;
    private final int CANVAS_START_X = 10;
    private final int CANVAS_START_Y = 10;

    private CanvasMode canvasMode;

    // for canvas resizing
    private CanvasResizeDirection canvasResizeDirection = null;
    private Rectangle canvasResizeBorder;
    private Rectangle horizontalResizer;
    private Rectangle verticalResizer;
    private Rectangle diagonalResizer;
    private boolean allowCanvasResizing = true;

    // for selection
    private Image selectionImageLayer;
    private Rectangle selectBorder;
    private Point selectAnchor;
    private boolean moveSelection;
    private BufferedImage selectedSubimage;
    private Point selectedSubimageOriginalLocation;
    private Point selectedSubimageCurrentLocation;
    private Rectangle originalSelectBorder;

    private ArrayList<CanvasListener> listeners = new ArrayList<>();

    private CanvasCursorManager cursors;

    private MouseInfoHolder mouseInfoHolder;

    private PencilTool pencilTool;
    private BucketTool bucketTool;
    private EyeDropperTool eyeDropperTool;
    private EraserTool eraserTool;

    public Canvas(ChoicesHolder choicesHolder) {
        this.isLeftMouseDown = false;
        this.isRightMouseDown = false;
        this.choicesHolder = choicesHolder;
        this.previousMousePosition = null;
        this.canvasMode = CanvasMode.PAINT;
        this.selectBorder = new Rectangle(0, 0, 0, 0);
        this.cursors = new CanvasCursorManager();
        this.mouseInfoHolder = new MouseInfoHolder();
        this.pencilTool = new PencilTool(this, choicesHolder, mouseInfoHolder);
        this.bucketTool = new BucketTool(this, choicesHolder, mouseInfoHolder);
        this.eyeDropperTool = new EyeDropperTool(this, choicesHolder, mouseInfoHolder, listeners);
        this.eraserTool = new EraserTool(this, choicesHolder, mouseInfoHolder);

        updateCanvasResizers();

        setBackground(new Color(197, 207, 223));
        setBorder(BorderFactory.createMatteBorder(5, 5, 0, 0, new Color(197, 207, 223)));

        mainImage = new Image(canvasWidth, canvasHeight);
        mainImage.clear(new Color(255, 255, 255));

        selectionImageLayer = new Image(canvasWidth, canvasHeight, ImageType.ARGB);
        selectionImageLayer.clear(new Color(0, 0, 0, 0));

        this.setDoubleBuffered(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
                mouseInfoHolder.mouseButtonPressed(mouseClick);

                Point mousePosition = new Point(e.getX() - CANVAS_START_X, e.getY() - CANVAS_START_Y);

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
                            useRectangleSelectTool(mouseClick, mousePosition.x, mousePosition.y);
                            break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
                mouseInfoHolder.mouseButtonReleased(mouseClick);
                if (choicesHolder.getTool() == Tool.PENCIL) {
                    pencilTool.mouseReleased();
                }
                else if (choicesHolder.getTool() == Tool.ERASER) {
                    eraserTool.mouseReleased();
                }

                else if (isLeftMouseDown && mouseClick == MouseClick.LEFT_CLICK) {
                    isLeftMouseDown = false;
                    previousMousePosition = null;

                    if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                        if (selectBorder.width == 0 || selectBorder.height == 0) {
                            allowCanvasResizing = true;
                            repaint();
                        }
                    }
                }
                else if (isRightMouseDown && mouseClick == MouseClick.RIGHT_CLICK) {
                    isRightMouseDown = false;
                    previousMousePosition = null;
                }
                else if (canvasMode == CanvasMode.RESIZE && mouseClick == MouseClick.LEFT_CLICK) {

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
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseInfoHolder.updateMousePosition(new Point(e.getPoint().x - CANVAS_START_X, e.getPoint().y - CANVAS_START_Y));
                Point mousePosition = e.getPoint();
                setCursor(getProperCursor(mousePosition));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseInfoHolder.updateMousePosition(new Point(e.getPoint().x - CANVAS_START_X, e.getPoint().y - CANVAS_START_Y));
                Point mousePosition = new Point(e.getX() - CANVAS_START_X, e.getY() - CANVAS_START_Y);

                if (canvasMode == CanvasMode.PAINT) {
                    if (choicesHolder.getTool() == Tool.PENCIL) {
                        pencilTool.mouseDragged();
                    }
                    else if (choicesHolder.getTool() == Tool.ERASER) {
                        eraserTool.mouseDragged();
                    }
                    else if (isLeftMouseDown && choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                        if (!moveSelection) { // creating a new selection
                            int pixelX = (int) mousePosition.getX() / choicesHolder.getScale();
                            int pixelY = (int) mousePosition.getY() / choicesHolder.getScale();

                            int x = selectAnchor.x;
                            int y = selectAnchor.y;
                            int width = pixelX - selectAnchor.x;
                            int height = pixelY - selectAnchor.y;
                            if (width < 0) {
                                x = pixelX;
                                if (x < 0) {
                                    x = 0;
                                }
                                width = selectAnchor.x - x;
                            }
                            if (height < 0) {
                                y = pixelY;
                                if (y < 0) {
                                    y = 0;
                                }
                                height = selectAnchor.y - y;
                            }

                            if (x + width >= canvasWidth) {
                                width = canvasWidth - x - 1;
                            }
                            if (y + height >= canvasHeight) {
                                height = canvasHeight - y - 1;
                            }


                            selectBorder = new Rectangle(x, y, width, height);

                            selectionImageLayer.clear(new Color(0, 0, 0, 0));
                            Graphics2D graphics = selectionImageLayer.getGraphics();
                            graphics.setColor(new Color(0, 0, 0, 255));

                            if (selectBorder.width > 0 && selectBorder.height > 0) {
                                for (int i = selectBorder.x; i < selectBorder.x + selectBorder.width; i += 2) {
                                    graphics.fillRect(i, selectBorder.y, 1, 1);
                                    graphics.fillRect(i, selectBorder.y + selectBorder.height, 1, 1);
                                }
                                for (int i = selectBorder.y; i < selectBorder.y + selectBorder.height; i += 2) {
                                    graphics.fillRect(selectBorder.x, i, 1, 1);
                                    graphics.fillRect(selectBorder.x + selectBorder.width, i, 1, 1);
                                }

                                // this entire block fixes the bottom left of the rectangle select border because it was sometimes coming out wonky due to pixel count
                                // it forces the bottom left pixel to always be filled in, and then potentially adjusts the adjacent pixels to make it look less awkward
                                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height, 1, 1);
                                if (selectionImageLayer.getRGB(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height) == ColorUtils.getIntFromColor(Color.black) && selectionImageLayer.getRGB(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1) == ColorUtils.getIntFromColor(Color.black)) {
                                    graphics.setColor(new Color(0, 0, 0, 0));
                                    graphics.setComposite(AlphaComposite.Clear);
                                    graphics.fillRect(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height, 1, 1);
                                    graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1, 1, 1);
                                    graphics.setColor(new Color(0, 0, 0, 255));
                                    graphics.setComposite(AlphaComposite.SrcOver);
                                    graphics.fillRect(selectBorder.x + selectBorder.width - 2, selectBorder.y + selectBorder.height, 1, 1);
                                    graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 2, 1, 1);
                                }
                            }
                            graphics.dispose();
                            originalSelectBorder = new Rectangle(selectBorder.x, selectBorder.y, selectBorder.width, selectBorder.height);
                        }
                        else { // moving an existing selection

                            int differenceX = e.getX() / choicesHolder.getScale() - previousMousePosition.x / choicesHolder.getScale();
                            int differenceY = e.getY() / choicesHolder.getScale() - previousMousePosition.y / choicesHolder.getScale();

                            Graphics2D graphics = selectionImageLayer.getGraphics();
                            selectionImageLayer.clear(new Color(0, 0, 0, 0));

                            graphics.setColor(choicesHolder.getEraseColor());
                            graphics.fillRect(selectedSubimageOriginalLocation.x, selectedSubimageOriginalLocation.y, selectBorder.width + 1, selectBorder.height + 1);

                            selectedSubimageCurrentLocation = new Point(selectedSubimageCurrentLocation.x + differenceX, selectedSubimageCurrentLocation.y + differenceY);

                            graphics.drawImage(selectedSubimage, selectedSubimageCurrentLocation.x, selectedSubimageCurrentLocation.y, selectBorder.width + 1, selectBorder.height + 1, null);
                            selectBorder = new Rectangle(selectedSubimageCurrentLocation.x, selectedSubimageCurrentLocation.y, selectBorder.width, selectBorder.height);

                            graphics.setColor(new Color(0, 0, 0, 255));

                            for (int i = selectBorder.x; i < selectBorder.x + selectBorder.width; i += 2) {
                                graphics.fillRect(i, selectBorder.y, 1, 1);
                                graphics.fillRect(i, selectBorder.y + selectBorder.height, 1, 1);
                            }
                            for (int i = selectBorder.y; i < selectBorder.y + selectBorder.height; i += 2) {
                                graphics.fillRect(selectBorder.x, i, 1, 1);
                                graphics.fillRect(selectBorder.x + selectBorder.width, i, 1, 1);
                            }

                            // this entire block fixes the bottom left of the rectangle select border because it was sometimes coming out wonky due to pixel count
                            // it forces the bottom left pixel to always be filled in, and then potentially adjusts the adjacent pixels to make it look less awkward
                            graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height, 1, 1);
                            if (selectionImageLayer.getRGB(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height) == ColorUtils.getIntFromColor(Color.black) && selectionImageLayer.getRGB(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1) == ColorUtils.getIntFromColor(Color.black)) {
                                graphics.setColor(new Color(0, 0, 0, 0));
                                graphics.setComposite(AlphaComposite.Clear);
                                graphics.fillRect(selectBorder.x + selectBorder.width - 1, selectBorder.y + selectBorder.height, 1, 1);
                                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 1, 1, 1);
                                graphics.setColor(new Color(0, 0, 0, 255));
                                graphics.setComposite(AlphaComposite.SrcOver);
                                graphics.fillRect(selectBorder.x + selectBorder.width - 2, selectBorder.y + selectBorder.height, 1, 1);
                                graphics.fillRect(selectBorder.x + selectBorder.width, selectBorder.y + selectBorder.height - 2, 1, 1);
                            }

                            previousMousePosition = e.getPoint();
                        }
                        repaint();
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
                return cursors.get(CanvasCursor.PENCIL);
            }
            else if (choicesHolder.getTool() == Tool.BUCKET) {
                return cursors.get(CanvasCursor.BUCKET);

            }
            else if (choicesHolder.getTool() == Tool.EYE_DROPPER) {
                return cursors.get(CanvasCursor.EYE_DROPPER);
            }
            else if (choicesHolder.getTool() == Tool.RECTANGLE_SELECT) {
                if (selectBorder.contains(new Point((mousePosition.x - CANVAS_START_X) / choicesHolder.getScale(), (mousePosition.y - CANVAS_START_Y) / choicesHolder.getScale()))) {
                    return cursors.get(CanvasCursor.DRAG);
                }
                return cursors.get(CanvasCursor.SELECT);
            }
        }
        return Cursor.getDefaultCursor();
    }

    // increase size of rectangle in all directions
    private Rectangle spreadRectangle(Rectangle original, int xSpread, int ySpread) {
        return new Rectangle(original.x - xSpread, original.y - ySpread, original.width + (xSpread * 2), original.height + (ySpread * 2));
    }

    private void useRectangleSelectTool(MouseClick mouseClick, int mouseX, int mouseY) {
        if (mouseClick == MouseClick.LEFT_CLICK) {
            isLeftMouseDown = true;

            if (selectBorder.contains(new Point(mouseX / choicesHolder.getScale(), mouseY / choicesHolder.getScale()))) { // move existing selection
                moveSelection = true;
                selectedSubimage = mainImage.getSubImage(originalSelectBorder.x, originalSelectBorder.y, selectBorder.width + 1, selectBorder.height + 1);
                selectedSubimageOriginalLocation = new Point(originalSelectBorder.x, originalSelectBorder.y);
                selectedSubimageCurrentLocation = new Point(selectBorder.x, selectBorder.y);
                previousMousePosition = new Point(mouseX + CANVAS_START_X, mouseY + CANVAS_START_Y);
            }
            else { // create new selection

                // if a previous selection exists and was moved from its original location, permanently apply the changes to the image
                if (selectBorder.width > 0 && selectBorder.height > 0 && (selectBorder.x != originalSelectBorder.x || selectBorder.y != originalSelectBorder.y)) {
                    Graphics2D graphics = mainImage.getGraphics();

                    graphics.setColor(choicesHolder.getEraseColor());
                    graphics.fillRect(originalSelectBorder.x, originalSelectBorder.y, selectBorder.width + 1, selectBorder.height + 1);
                    graphics.drawImage(selectedSubimage, selectBorder.x, selectBorder.y, selectedSubimage.getWidth(), selectedSubimage.getHeight(), null);

                    graphics.dispose();
                }

                // prepare for new selection to be made
                moveSelection = false;
                selectionImageLayer.clear(new Color(0, 0, 0, 0));
                selectAnchor = new Point(mouseX / choicesHolder.getScale(), mouseY / choicesHolder.getScale());
                allowCanvasResizing = false;
                selectBorder = new Rectangle(0, 0, 0, 0);
                originalSelectBorder = new Rectangle(selectBorder.x, selectBorder.y, selectBorder.width, selectBorder.height);
                repaint();
            }
        }
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

    private void resizeCanvas(int newWidth, int newHeight) {
        mainImage.resize(newWidth, newHeight, choicesHolder.getEraseColor());
        updateCanvasResizers();

        selectionImageLayer.resize(newWidth, newHeight, new Color(0, 0, 0, 0));
        selectionImageLayer.clear(new Color(0, 0, 0, 0));
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

        // this method updates the mousePosition variable in place...ew
        SwingUtilities.convertPointFromScreen(mousePosition, this);

        setCursor(getProperCursor(mousePosition));

        allowCanvasResizing = true;

        selectionImageLayer.clear(new Color(0, 0, 0, 0));

        repaint();
    }

    @Override
    public void onPaintColorChanged(Color color) {

    }

    @Override
    public void onEraseColorChanged(Color color) {

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
}