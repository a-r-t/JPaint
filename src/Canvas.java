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

public class Canvas extends JPanel {
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
    private ArrayList<CanvasListener> listeners = new ArrayList<>();

    public Canvas(SelectionsHolder selectionsHolder) {
        this.isLeftMouseDown = false;
        this.isRightMouseDown = false;
        this.selectionsHolder = selectionsHolder;
        this.previousMousePosition = null;

        updateCanvasResizers();

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
                if (isLeftOrRightClick(mouseClick) && isMouseInCanvas(e.getPoint())) {
                    switch(selectionsHolder.getTool()) {
                        case PENCIL:
                            usePencilTool(mouseClick, e.getX(), e.getY());
                            break;
                        case BUCKET:
                            useBucketTool(mouseClick, e.getX(), e.getY());
                            break;
                        case MAGNIFYING_GLASS:
                            useMagnifyingGlassTool(mouseClick);
                            break;
                        case EYE_DROPPER:
                            useEyeDropperTool(mouseClick, e.getX(), e.getY());
                            break;
                        case ERASER:
                            useEraserTool(mouseClick, e.getX(), e.getY());
                            break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isLeftMouseDown && e.getButton() == MouseEvent.BUTTON1) {
                    isLeftMouseDown = false;
                    previousMousePosition = null;
                }
                else if (isRightMouseDown && e.getButton() == MouseEvent.BUTTON3) {
                    isRightMouseDown = false;
                    previousMousePosition = null;
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((isLeftMouseDown || isRightMouseDown) && (selectionsHolder.getTool() == Tool.PENCIL || selectionsHolder.getTool() == Tool.ERASER)) {
                    int previousMouseX = previousMousePosition.x;
                    int previousMouseY = previousMousePosition.y;

                    while(previousMouseX != e.getX() || previousMouseY != e.getY()) {
                        int xOffset = 0;
                        int yOffset = 0;
                        if (previousMouseX > e.getX()) {
                            xOffset = -1;
                        }
                        else if (previousMouseX < e.getX()) {
                            xOffset = 1;
                        }
                        if (previousMouseY > e.getY()) {
                            yOffset = -1;
                        }
                        else if (previousMouseY < e.getY()) {
                            yOffset = 1;
                        }
                        previousMouseX += xOffset;
                        previousMouseY += yOffset;

                        if (previousMouseX >= 0 && previousMouseX / scale < image.getWidth() && previousMouseY >= 0 && previousMouseY / scale < image.getHeight()) {
                            int color = 0;
                            if (selectionsHolder.getTool() == Tool.PENCIL) {
                                if (isLeftMouseDown) {
                                    color = selectionsHolder.getPaintColorAsIntRGB();
                                }
                                else {
                                    color = selectionsHolder.getEraseColorAsIntRGB();
                                }
                            }
                            else if (selectionsHolder.getTool() == Tool.ERASER) {
                                color = selectionsHolder.getEraseColorAsIntRGB();
                            }
                            image.setRGB(previousMouseX / scale, previousMouseY / scale, color);
                        }
                    }
                    previousMousePosition = e.getPoint();
                    repaint();
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            }

        });
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
        horizontalResizer = new Rectangle(canvasWidth * scale, ((canvasHeight * scale) / 2) - 2, 4, 4);
        verticalResizer = new Rectangle(((canvasWidth * scale) / 2) - 2, canvasHeight * scale, 4, 4);
        diagonalResizer = new Rectangle(canvasWidth * scale, canvasHeight * scale, 4, 4);
    }

    private boolean isLeftOrRightClick(MouseClick mouseClick) {
        return mouseClick == MouseClick.LEFT_CLICK || mouseClick == MouseClick.RIGHT_CLICK;
    }

    private boolean isMouseInCanvas(Point mouseCoords) {
        return mouseCoords.x / scale >= 0 && mouseCoords.x / scale < image.getWidth() && mouseCoords.y / scale >= 0 && mouseCoords.y / scale < image.getHeight();
    }


    // this is just to make the scroll pane respect the bounds of the canvas's image
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( (canvasWidth * scale) + EXTRA_CANVAS_WIDTH, (canvasHeight * scale) + EXTRA_CANVAS_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;
        brush.drawImage(image, 0, 0, image.getWidth() * scale, image.getHeight() * scale, null);

        Color oldColor = brush.getColor();
        Stroke oldStroke = brush.getStroke();

        brush.setColor(Color.white);
        brush.fillRect(horizontalResizer.x, horizontalResizer.y, horizontalResizer.width, horizontalResizer.height);
        brush.fillRect(verticalResizer.x, verticalResizer.y, verticalResizer.width, verticalResizer.height);
        brush.fillRect(diagonalResizer.x, diagonalResizer.y, diagonalResizer.width, diagonalResizer.height);

        brush.setColor(new Color(85, 85, 85));
        brush.setStroke(new BasicStroke(1));
        brush.drawRect(horizontalResizer.x, horizontalResizer.y, horizontalResizer.width, horizontalResizer.height);
        brush.drawRect(verticalResizer.x, verticalResizer.y, verticalResizer.width, verticalResizer.height);
        brush.drawRect(diagonalResizer.x, diagonalResizer.y, diagonalResizer.width, diagonalResizer.height);

        brush.setColor(oldColor);
        brush.setStroke(oldStroke);
    }

    public void addListener(CanvasListener listener) {
        listeners.add(listener);
    }
}
