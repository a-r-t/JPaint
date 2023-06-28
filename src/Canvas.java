import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

public class Canvas extends JPanel {
    private int canvasWidth = 400;
    private int canvasHeight = 400;
    private BufferedImage image;
    private SelectionsHolder selectionsHolder;
    private boolean isMouseDown;
    private Point previousMousePosition;
    private int scale = 1;

    public Canvas(SelectionsHolder selectionsHolder) {
        this.isMouseDown = false;
        this.selectionsHolder = selectionsHolder;
        this.previousMousePosition = null;

        setBackground(new Color(197, 207, 223));
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
                if (e.getX() / scale >= 0 && e.getX() / scale < image.getWidth() && e.getY() / scale >= 0 && e.getY() / scale < image.getHeight()) {
                    if (selectionsHolder.getTool() == Tool.PENCIL) {
                        image.setRGB(e.getX() / scale, e.getY() / scale, ColorUtils.getIntFromColor(Color.black));
                        isMouseDown = true;
                        previousMousePosition = e.getPoint();
                        repaint();
                    } else if (selectionsHolder.getTool() == Tool.BUCKET) {
                        int oldRgb = image.getRGB(e.getX() / scale, e.getY() / scale);
                        int newRgb = ColorUtils.getIntFromColor(Color.black);
                        spreadColor(e.getX() / scale, e.getY() / scale, oldRgb, newRgb);
                        repaint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isMouseDown = false;
                previousMousePosition = null;
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isMouseDown && selectionsHolder.getTool() == Tool.PENCIL) {
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
                            image.setRGB(previousMouseX / scale, previousMouseY / scale, ColorUtils.getIntFromColor(Color.black));
                        }
                    }
                    previousMousePosition = e.getPoint();
                    repaint();
                }
            }
        });


    }

    // paint bucket logic
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


    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( canvasWidth * scale, canvasHeight * scale );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;
        g.drawImage(image, 0, 0, image.getWidth() * scale, image.getHeight() * scale, null);
    }

}
