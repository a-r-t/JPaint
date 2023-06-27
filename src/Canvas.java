import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    private int canvasWidth = 400;
    private int canvasHeight = 400;

    private BufferedImage image;

    private SelectionsHolder selectionsHolder;

    private boolean isMouseDown;

    private Point previousMousePosition;

    public Canvas(SelectionsHolder selectionsHolder) {
        this.isMouseDown = false;
        this.selectionsHolder = selectionsHolder;
        this.previousMousePosition = null;

        setSize(new Dimension(canvasWidth, canvasHeight));
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
                if (selectionsHolder.getTool() == Tool.PENCIL) {
                    image.setRGB(e.getX(), e.getY(), getIntFromColor(Color.black));
                    isMouseDown = true;
                    previousMousePosition = e.getPoint();
                    repaint();
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

                        if (previousMouseX >= 0 && previousMouseX < image.getWidth() && previousMouseY >= 0 && previousMouseY < image.getHeight()) {
                            image.setRGB(previousMouseX, previousMouseY, getIntFromColor(Color.black));
                        }
                    }
                    previousMousePosition = e.getPoint();
                    repaint();
                }
            }
        });


    }

    public int getIntFromColor(Color color){
        int red = (color.getRed() << 16) &  0x00FF0000;
        int green = (color.getGreen() << 8) &  0x00FF0000;
        int blue = color.getBlue() &  0x00FF0000;
        return 0xFF000000 | red | green | blue;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension( canvasWidth,canvasHeight );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

}
