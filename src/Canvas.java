import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Canvas extends JPanel implements SelectionsListener {
    private int canvasWidth = 400;
    private int canvasHeight = 400;
    private BufferedImage image;
    private SelectionsHolder selectionsHolder;

    private Rectangle horizontalResizer;
    private Rectangle verticalResizer;
    private Rectangle diagonalResizer;
    private boolean allowCanvasResizing = true;

    private boolean isLeftMouseDown;
    private boolean isRightMouseDown;
    private Point previousMousePosition;
    private final int EXTRA_CANVAS_WIDTH = 10;
    private final int EXTRA_CANVAS_HEIGHT = 10;
    private final int CANVAS_START_X = 10;
    private final int CANVAS_START_Y = 10;

    private ArrayList<CanvasListener> listeners = new ArrayList<>();
    private CanvasMode canvasMode;
    private CanvasResizeDirection canvasResizeDirection = null;
    private Rectangle canvasResizeBorder;
    private BufferedImage selectionImageLayer;

    private Rectangle selectBorder;
    private Point selectAnchor;

    private HashMap<String, Cursor> cursors = new HashMap<>();

    public Canvas(SelectionsHolder selectionsHolder) {
        this.isLeftMouseDown = false;
        this.isRightMouseDown = false;
        this.selectionsHolder = selectionsHolder;
        this.previousMousePosition = null;
        this.canvasMode = CanvasMode.PAINT;

        updateCanvasResizers();
        loadCursors();

        setBackground(new Color(197, 207, 223));
        setBorder(BorderFactory.createMatteBorder(5, 5, 0, 0, new Color(197, 207, 223)));

        image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        clearImage(image, new Color(255, 255, 255));

        selectionImageLayer = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        clearImage(selectionImageLayer, new Color(0, 0, 0, 0));


        this.setDoubleBuffered(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
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
                if (canvasMode == CanvasMode.PAINT && isLeftOrRightClick(mouseClick) && isMouseInCanvas(e.getPoint()) && selectionsHolder.getTool() != null) {
                    switch(selectionsHolder.getTool()) {
                        case PENCIL:
                            usePencilTool(mouseClick, mousePosition.x, mousePosition.y);
                            break;
                        case BUCKET:
                            useBucketTool(mouseClick, mousePosition.x, mousePosition.y);
                            break;
                        case EYE_DROPPER:
                            useEyeDropperTool(mouseClick, mousePosition.x, mousePosition.y);
                            break;
                        case ERASER:
                            useEraserTool(mouseClick, e.getX(), e.getY());
                            break;
                        case RECTANGLE_SELECT:
                            useRectangleSelectTool(mouseClick, e.getX(), e.getY());
                            break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());

                if (isLeftMouseDown && mouseClick == MouseClick.LEFT_CLICK) {
                    isLeftMouseDown = false;
                    previousMousePosition = null;

                    if (selectionsHolder.getTool() == Tool.RECTANGLE_SELECT && (selectBorder.width == 0 || selectBorder.height == 0)) {
                        allowCanvasResizing = true;
                    }
                }
                else if (isRightMouseDown && mouseClick == MouseClick.RIGHT_CLICK) {
                    isRightMouseDown = false;
                    previousMousePosition = null;
                }
                else if (canvasMode == CanvasMode.RESIZE && mouseClick == MouseClick.LEFT_CLICK) {

                    if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                        canvasWidth = Math.max(e.getX() / selectionsHolder.getScale(), 1);
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                        canvasHeight = Math.max(e.getY() / selectionsHolder.getScale(), 1);
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH_EAST) {
                        canvasWidth = Math.max(e.getX() / selectionsHolder.getScale(), 1);
                        canvasHeight = Math.max(e.getY() / selectionsHolder.getScale(), 1);
                    }
                    resizeCanvas();
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
                Point mousePosition = e.getPoint();
                setCursor(getProperCursor(mousePosition));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point mousePosition = new Point(e.getX() - CANVAS_START_X, e.getY() - CANVAS_START_Y);

                if (canvasMode == CanvasMode.PAINT) {
                    if ((isLeftMouseDown || isRightMouseDown) && (selectionsHolder.getTool() == Tool.PENCIL || selectionsHolder.getTool() == Tool.ERASER)) {
                        int previousMouseX = previousMousePosition.x;
                        int previousMouseY = previousMousePosition.y;

                        while (previousMouseX != mousePosition.x || previousMouseY != mousePosition.y) {
                            int xOffset = 0;
                            int yOffset = 0;
                            if (previousMouseX > mousePosition.x) {
                                xOffset = -1;
                            } else if (previousMouseX < mousePosition.x) {
                                xOffset = 1;
                            }
                            if (previousMouseY > mousePosition.y) {
                                yOffset = -1;
                            } else if (previousMouseY < mousePosition.y) {
                                yOffset = 1;
                            }
                            previousMouseX += xOffset;
                            previousMouseY += yOffset;

                            if (previousMouseX >= 0 && previousMouseX / selectionsHolder.getScale() < image.getWidth() && previousMouseY >= 0 && previousMouseY / selectionsHolder.getScale() < image.getHeight()) {
                                int color = 0;
                                if (selectionsHolder.getTool() == Tool.PENCIL) {
                                    if (isLeftMouseDown) {
                                        color = selectionsHolder.getPaintColorAsIntRGB();
                                    } else {
                                        color = selectionsHolder.getEraseColorAsIntRGB();
                                    }
                                } else if (selectionsHolder.getTool() == Tool.ERASER) {
                                    color = selectionsHolder.getEraseColorAsIntRGB();
                                }
                                image.setRGB(previousMouseX / selectionsHolder.getScale(), previousMouseY / selectionsHolder.getScale(), color);
                            }
                        }
                        previousMousePosition = mousePosition;
                        repaint();
                    }
                    else if (isLeftMouseDown && selectionsHolder.getTool() == Tool.RECTANGLE_SELECT) {
                        int pixelX = (int)mousePosition.getX() / selectionsHolder.getScale();
                        int pixelY = (int)mousePosition.getY() / selectionsHolder.getScale();

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
                            width = canvasWidth - x;
                        }
                        if (y + height >= canvasHeight) {
                            height = canvasHeight - y;
                        }


                        selectBorder = new Rectangle(x, y, width, height);

                        clearImage(selectionImageLayer, new Color(0, 0, 0, 0));
                        Graphics2D graphics = selectionImageLayer.createGraphics();
                        graphics.setColor ( new Color ( 0, 0, 0, 255 ) );

                        if (selectBorder.width > 0 && selectBorder.height > 0) {
                            for (int i = selectBorder.x; i < selectBorder.x + selectBorder.width; i += 2) {
                                graphics.fillRect(i, selectBorder.y, 1, 1);
                                graphics.fillRect(i, selectBorder.y + selectBorder.height, 1, 1);
                            }
                            for (int i = selectBorder.y; i < selectBorder.y + selectBorder.height; i += 2) {
                                graphics.fillRect(selectBorder.x, i, 1, 1);
                                graphics.fillRect(selectBorder.x + selectBorder.width, i, 1, 1);
                            }
                        }
                        graphics.dispose();


                        repaint();
                    }
                }
                else if (canvasMode == CanvasMode.RESIZE) {
                    if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, e.getPoint().x, canvasHeight * selectionsHolder.getScale());
                        repaint();
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, canvasWidth * selectionsHolder.getScale(), e.getPoint().y);
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
                return new Cursor(Cursor.E_RESIZE_CURSOR);
            }
            else if (spreadRectangle(verticalResizer, 5, 0).contains(mousePosition)) {
                return new Cursor(Cursor.S_RESIZE_CURSOR);
            }
            else if (spreadRectangle(diagonalResizer, 5, 5).contains(mousePosition)) {
                return new Cursor(Cursor.SE_RESIZE_CURSOR);
            }
        }
        if (isMouseInCanvas(mousePosition)) {
            if (selectionsHolder.getTool() == Tool.PENCIL) {
                return cursors.get("PENCIL");
            }
            else if (selectionsHolder.getTool() == Tool.BUCKET) {
                return cursors.get("BUCKET");

            }
            else if (selectionsHolder.getTool() == Tool.EYE_DROPPER) {
                return cursors.get("EYE_DROPPER");
            }
            else if (selectionsHolder.getTool() == Tool.RECTANGLE_SELECT) {
                return cursors.get("SELECT");
            }
        }
        return Cursor.getDefaultCursor();
    }

    private void loadCursors() {
        // note: cursor image sizes MUST be 32x32, otherwise they will be forcibly scaled up/down to match 32x32
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor pencilCursor = null;
        Cursor bucketCursor = null;
        Cursor eyedropperCursor = null;
        Cursor selectCursor = null;
        try {
            pencilCursor = toolkit.createCustomCursor(
                    ImageIO.read(ToolStrip.class.getResource("/pencil-cursor-transparent.png")),
                    new Point(7, 23),
                    "pencil"
            );
            bucketCursor = toolkit.createCustomCursor(
                    ImageIO.read(ToolStrip.class.getResource("/bucket-cursor-transparent.png")),
                    new Point(8, 19),
                    "bucket"
            );
            eyedropperCursor = toolkit.createCustomCursor(
                    ImageIO.read(ToolStrip.class.getResource("/eyedropper-cursor-transparent.png")),
                    new Point(9, 22),
                    "eyedropper"
            );
            selectCursor = toolkit.createCustomCursor(
                    ImageIO.read(ToolStrip.class.getResource("/select-cursor-transparent.png")),
                    new Point(15, 15),
                    "select"
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        cursors.put("PENCIL", pencilCursor);
        cursors.put("BUCKET", bucketCursor);
        cursors.put("EYE_DROPPER", eyedropperCursor);
        cursors.put("SELECT", selectCursor);
    }

    // increase size of rectangle in all directions
    private Rectangle spreadRectangle(Rectangle original, int xSpread, int ySpread) {
        return new Rectangle(original.x - xSpread, original.y - ySpread, original.width + (xSpread * 2), original.height + (ySpread * 2));
    }

    private void usePencilTool(MouseClick mouseClick, int mouseX, int mouseY) {
        if (!isLeftMouseDown && !isRightMouseDown) {
            int color = 0;
            if (mouseClick == MouseClick.LEFT_CLICK) {
                color = selectionsHolder.getPaintColorAsIntRGB();
                isLeftMouseDown = true;
            }
            else if (mouseClick == MouseClick.RIGHT_CLICK) {
                color = selectionsHolder.getEraseColorAsIntRGB();
                isRightMouseDown = true;
            }
            image.setRGB(mouseX / selectionsHolder.getScale(), mouseY / selectionsHolder.getScale(), color);
            previousMousePosition = new Point(mouseX, mouseY);
            repaint();
        }
    }

    private void useBucketTool(MouseClick mouseClick, int mouseX, int mouseY) {
        int color = 0;
        if (mouseClick == MouseClick.LEFT_CLICK) {
            color = selectionsHolder.getPaintColorAsIntRGB();
        }
        else if (mouseClick == MouseClick.RIGHT_CLICK) {
            color = selectionsHolder.getEraseColorAsIntRGB();
        }
        int oldRgb = image.getRGB(mouseX / selectionsHolder.getScale(), mouseY / selectionsHolder.getScale());
        int newRgb = color;
        if (oldRgb != newRgb) {
            spreadColor(mouseX / selectionsHolder.getScale(), mouseY / selectionsHolder.getScale(), oldRgb, newRgb);
            repaint();
        }
    }

    private void useEyeDropperTool(MouseClick mouseClick, int mouseX, int mouseY) {
        int rgb = image.getRGB(mouseX / selectionsHolder.getScale(), mouseY / selectionsHolder.getScale());
        Color color = ColorUtils.getColorFromInt(rgb);
        if (mouseClick == MouseClick.LEFT_CLICK) {
            selectionsHolder.setPaintColor(color);

            // let subscribers know paint color was just changed
            for (CanvasListener listener : listeners) {
                listener.onPaintColorChanged(color);
            }

            // let subscribers know eye dropper was just used to change paint color
            for (CanvasListener listener : listeners) {
                listener.onEyeDropperUsedToChangePaintColor();
            }
        }
        else if (mouseClick == MouseClick.RIGHT_CLICK) {
            selectionsHolder.setEraseColor(color);

            // let subscribers know erase color was just changed
            for (CanvasListener listener : listeners) {
                listener.onEraseColorChanged(color);
            }
        }
    }

    private void useEraserTool(MouseClick mouseClick, int mouseX, int mouseY) {
        if (mouseClick == MouseClick.LEFT_CLICK) {
            int color = selectionsHolder.getEraseColorAsIntRGB();
            image.setRGB(mouseX / selectionsHolder.getScale(), mouseY / selectionsHolder.getScale(), color);
            isLeftMouseDown = true;
            previousMousePosition = new Point(mouseX, mouseY);
            repaint();
        }
    }

    private void useRectangleSelectTool(MouseClick mouseClick, int mouseX, int mouseY) {
        if (mouseClick == MouseClick.LEFT_CLICK) {
            clearImage(selectionImageLayer, new Color(0, 0, 0, 0));

            isLeftMouseDown = true;
            selectAnchor = new Point((mouseX - CANVAS_START_X) / selectionsHolder.getScale(), (mouseY - CANVAS_START_Y) / selectionsHolder.getScale());
            allowCanvasResizing = false;
            repaint();
        }
    }


    // paint bucket spread logic
    // note: on REALLY large areas, there may be a slight delay due to the amount of computation required
    private void spreadColor(int x, int y, int oldRgb, int newRgb) {
        Queue<Point> spreadQueue = new LinkedList<>();
        spreadQueue.add(new Point(x, y));
        while (!spreadQueue.isEmpty()) {
            Point next = spreadQueue.poll();
            if (next.x >= 0 && next.x < image.getWidth() && next.y >= 0 && next.y < image.getHeight()) {
                int rgb = image.getRGB(next.x, next.y);
                if (rgb == oldRgb) {
                    image.setRGB(next.x, next.y, newRgb);
                    spreadQueue.add(new Point(next.x + 1, next.y));
                    spreadQueue.add(new Point(next.x - 1, next.y));
                    spreadQueue.add(new Point(next.x, next.y + 1));
                    spreadQueue.add(new Point(next.x, next.y - 1));
                }
            }
        }
    }

    private void updateCanvasResizers() {
        horizontalResizer = new Rectangle((CANVAS_START_X + canvasWidth * selectionsHolder.getScale()),  ((CANVAS_START_Y + canvasHeight * selectionsHolder.getScale()) / 2) + 2, 4, 4);
        verticalResizer = new Rectangle(((CANVAS_START_X + canvasWidth * selectionsHolder.getScale()) / 2) + 2, CANVAS_START_Y + canvasHeight * selectionsHolder.getScale(), 4, 4);
        diagonalResizer = new Rectangle(CANVAS_START_X + canvasWidth * selectionsHolder.getScale(), CANVAS_START_Y + canvasHeight * selectionsHolder.getScale(), 4, 4);
    }

    private boolean isLeftOrRightClick(MouseClick mouseClick) {
        return mouseClick == MouseClick.LEFT_CLICK || mouseClick == MouseClick.RIGHT_CLICK;
    }

    private boolean isMouseInCanvas(Point mousePosition) {
        return mousePosition.x - CANVAS_START_X >= 0 && mousePosition.x - CANVAS_START_X < image.getWidth() * selectionsHolder.getScale() && mousePosition.y - CANVAS_START_Y >= 0 && mousePosition.y - CANVAS_START_Y < image.getHeight() * selectionsHolder.getScale();
    }

    private void resizeCanvas() {
        BufferedImage newImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < newImage.getWidth(); x++) {
            for (int y = 0; y < newImage.getHeight(); y++) {
                if (x < image.getWidth() && y < image.getHeight()) {
                    newImage.setRGB(x, y, image.getRGB(x, y));
                }
                else {
                    newImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        image = newImage;
        updateCanvasResizers();

        selectionImageLayer = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        clearImage(selectionImageLayer, new Color(0, 0, 0, 0));
    }

    // this is just to make the scroll pane respect the bounds of the canvas's image
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( CANVAS_START_X + (canvasWidth * selectionsHolder.getScale()) + EXTRA_CANVAS_WIDTH, CANVAS_START_Y + (canvasHeight * selectionsHolder.getScale()) + EXTRA_CANVAS_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;

        Color oldColor = brush.getColor();
        Stroke oldStroke = brush.getStroke();

        // paint current image as canvas
        brush.drawImage(image, CANVAS_START_X, CANVAS_START_Y, image.getWidth() * selectionsHolder.getScale(), image.getHeight() * selectionsHolder.getScale(), null);

        // paint resizers
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
            // paint select
            if (selectionsHolder.getTool() == Tool.RECTANGLE_SELECT) {
                brush.drawImage(selectionImageLayer, CANVAS_START_X, CANVAS_START_Y, selectionImageLayer.getWidth() * selectionsHolder.getScale(), selectionImageLayer.getHeight() * selectionsHolder.getScale(), null);
            }
        }
        // paint resize borders
        else if (canvasMode == CanvasMode.RESIZE) {
            brush.setColor(new Color(0, 0, 0));
            if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                for (int i = CANVAS_START_X; i < canvasResizeBorder.getX() + canvasResizeBorder.getWidth(); i += 2) {
                    brush.fillRect(i, CANVAS_START_Y, 1, 1);
                    brush.fillRect(i, CANVAS_START_Y + canvasHeight * selectionsHolder.getScale(), 1, 1);
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

        // apparently this method updates the mousePosition variable in place...yikes
        SwingUtilities.convertPointFromScreen(mousePosition, this);

        setCursor(getProperCursor(mousePosition));

        allowCanvasResizing = true;

        if (tool == Tool.RECTANGLE_SELECT) {
            clearImage(selectionImageLayer, new Color(0, 0, 0, 0));
        }

        repaint();
    }

    private void clearImage(BufferedImage image, Color color) {
        Graphics2D graphics = image.createGraphics();
        Color oldColor = graphics.getColor();

        graphics.setColor(color);
        if (color.getAlpha() == 0) {
            graphics.setComposite(AlphaComposite.Clear); // set transparency rule
        }
        graphics.fillRect (0, 0, image.getWidth(), image.getHeight());

        // reset graphics config
        graphics.setColor(oldColor);
        graphics.setComposite(AlphaComposite.SrcOver);
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
}
