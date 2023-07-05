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

    private boolean isLeftMouseDown;
    private boolean isRightMouseDown;
    private Point previousMousePosition;
    private int scale = 1;
    private final int MIN_SCALE = 1;
    private final int MAX_SCALE = 10;
    private final int EXTRA_CANVAS_WIDTH = 10;
    private final int EXTRA_CANVAS_HEIGHT = 10;
    private final int CANVAS_START_X = 10;
    private final int CANVAS_START_Y = 10;

    private ArrayList<CanvasListener> listeners = new ArrayList<>();
    private CanvasMode canvasMode;
    private CanvasResizeDirection canvasResizeDirection = null;
    private Rectangle canvasResizeBorder;

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
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, Color.WHITE.getRGB());
            }
        }
        this.setDoubleBuffered(true);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MouseClick mouseClick = MouseClick.convertToMouseClick(e.getButton());
                Point mousePosition = new Point(e.getX() - CANVAS_START_X, e.getY() - CANVAS_START_Y);

                if (mouseClick == MouseClick.LEFT_CLICK) {
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
                        case MAGNIFYING_GLASS:
                            useMagnifyingGlassTool(mouseClick);
                            break;
                        case EYE_DROPPER:
                            useEyeDropperTool(mouseClick, mousePosition.x, mousePosition.y);
                            break;
                        case ERASER:
                            useEraserTool(mouseClick, e.getX(), e.getY());
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
                }
                else if (isRightMouseDown && mouseClick == MouseClick.RIGHT_CLICK) {
                    isRightMouseDown = false;
                    previousMousePosition = null;
                }
                else if (canvasMode == CanvasMode.RESIZE && mouseClick == MouseClick.LEFT_CLICK) {

                    if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                        canvasWidth = Math.max(e.getX() / scale, 1);
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                        canvasHeight = Math.max(e.getY() / scale, 1);
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH_EAST) {
                        canvasWidth = Math.max(e.getX() / scale, 1);
                        canvasHeight = Math.max(e.getY() / scale, 1);
                    }
                    resizeCanvas();
                    canvasMode = CanvasMode.PAINT;
                    canvasResizeDirection = null;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point mousePosition = e.getPoint();
                updateCursor(mousePosition);
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

                            if (previousMouseX >= 0 && previousMouseX / scale < image.getWidth() && previousMouseY >= 0 && previousMouseY / scale < image.getHeight()) {
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
                                image.setRGB(previousMouseX / scale, previousMouseY / scale, color);
                            }
                        }
                        previousMousePosition = mousePosition;
                        repaint();
                    }
                }
                else if (canvasMode == CanvasMode.RESIZE) {
                    if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, e.getPoint().x, canvasHeight * scale);
                        repaint();
                    }
                    else if (canvasResizeDirection == CanvasResizeDirection.SOUTH) {
                        canvasResizeBorder = new Rectangle(CANVAS_START_X, CANVAS_START_Y, canvasWidth * scale, e.getPoint().y);
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

    private void updateCursor(Point mousePosition) {
        if (spreadRectangle(horizontalResizer, 0, 5).contains(mousePosition)) {
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        }
        else if (spreadRectangle(verticalResizer, 5, 0).contains(mousePosition)) {
            setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
        }
        else if (spreadRectangle(diagonalResizer, 5, 5).contains(mousePosition)) {
            setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
        }
        else if (isMouseInCanvas(mousePosition)) {
            if (selectionsHolder.getTool() == Tool.PENCIL) {
                setCursor(cursors.get("PENCIL"));
            }
            else if (selectionsHolder.getTool() == Tool.BUCKET) {
                setCursor(cursors.get("BUCKET"));

            }
            else if (selectionsHolder.getTool() == Tool.EYE_DROPPER) {
                setCursor(cursors.get("EYE_DROPPER"));
            }
            else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
        else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void loadCursors() {
        // note: cursor image sizes MUST be 32x32, otherwise they will be forcibly scaled up/down to match 32x32
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor pencilCursor = null;
        Cursor bucketCursor = null;
        Cursor eyedropperCursor = null;
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
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        cursors.put("PENCIL", pencilCursor);
        cursors.put("BUCKET", bucketCursor);
        cursors.put("EYE_DROPPER", eyedropperCursor);
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
            image.setRGB(mouseX / scale, mouseY / scale, color);
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
        int oldRgb = image.getRGB(mouseX / scale, mouseY / scale);
        int newRgb = color;
        if (oldRgb != newRgb) {
            spreadColor(mouseX / scale, mouseY / scale, oldRgb, newRgb);
            repaint();
        }
    }

    private void useMagnifyingGlassTool(MouseClick mouseClick) {
        int oldScale = scale;
        if (mouseClick == MouseClick.LEFT_CLICK) {
            if (scale < MAX_SCALE) {
                scale++;
            }
        }
        else if (mouseClick == MouseClick.RIGHT_CLICK) {
            if (scale > MIN_SCALE) {
                scale--;
            }
        }
        if (oldScale != scale) {
            updateCanvasResizers();
            revalidate(); // updates scroll control to correct itself when canvas grows
            repaint();
        }
    }

    private void useEyeDropperTool(MouseClick mouseClick, int mouseX, int mouseY) {
        int rgb = image.getRGB(mouseX / scale, mouseY / scale);
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
            image.setRGB(mouseX / scale, mouseY / scale, color);
            isLeftMouseDown = true;
            previousMousePosition = new Point(mouseX, mouseY);
            repaint();
        }
    }

    // paint bucket spread logic
    //
    // note: I am aware this is a recursive algorithm, however doing this using recursive method calls will run out of memory when used on large areas
    // so this method replicates the recursive nature using a queue strategy
    //
    // also note: on REALLY large areas, there may be a slight delay due to the amount of computation required
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
        horizontalResizer = new Rectangle((CANVAS_START_X + canvasWidth * scale),  ((CANVAS_START_Y + canvasHeight * scale) / 2) + 2, 4, 4);
        verticalResizer = new Rectangle(((CANVAS_START_X + canvasWidth * scale) / 2) + 2, CANVAS_START_Y + canvasHeight * scale, 4, 4);
        diagonalResizer = new Rectangle(CANVAS_START_X + canvasWidth * scale, CANVAS_START_Y + canvasHeight * scale, 4, 4);
    }

    private boolean isLeftOrRightClick(MouseClick mouseClick) {
        return mouseClick == MouseClick.LEFT_CLICK || mouseClick == MouseClick.RIGHT_CLICK;
    }

    private boolean isMouseInCanvas(Point mousePosition) {
        return mousePosition.x - CANVAS_START_X >= 0 && mousePosition.x - CANVAS_START_X < image.getWidth() * scale && mousePosition.y - CANVAS_START_Y >= 0 && mousePosition.y - CANVAS_START_Y < image.getHeight() * scale;
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
    }

    // this is just to make the scroll pane respect the bounds of the canvas's image
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( CANVAS_START_X + (canvasWidth * scale) + EXTRA_CANVAS_WIDTH, CANVAS_START_Y + (canvasHeight * scale) + EXTRA_CANVAS_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;

        // paint current image as canvas
        brush.drawImage(image, CANVAS_START_X, CANVAS_START_Y, image.getWidth() * scale, image.getHeight() * scale, null);

        // paint resizers
        Color oldColor = brush.getColor();
        Stroke oldStroke = brush.getStroke();

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

        // paint resize borders
        if (canvasMode == CanvasMode.RESIZE) {
            brush.setColor(new Color(0, 0, 0));
            if (canvasResizeDirection == CanvasResizeDirection.EAST) {
                for (int i = CANVAS_START_X; i < canvasResizeBorder.getX() + canvasResizeBorder.getWidth(); i += 2) {
                    brush.fillRect(i, CANVAS_START_Y, 1, 1);
                    brush.fillRect(i, CANVAS_START_Y + canvasHeight * scale, 1, 1);
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

        updateCursor(mousePosition);

    }

    @Override
    public void onPaintColorChanged(Color color) {

    }

    @Override
    public void onEraseColorChanged(Color color) {

    }
}
