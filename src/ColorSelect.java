import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ColorSelect extends JPanel {
    private ChoicesHolder choicesHolder;
    private ColorSwatch[] defaultColors;
    private ArrayList<ColorSelectListener> listeners = new ArrayList<>();

    public ColorSelect(ChoicesHolder choicesHolder) {
        this.choicesHolder = choicesHolder;
        setLayout(null);
        setBackground(new Color(245, 246, 247));
        defaultColors = loadDefaultColors();
        setSize(new Dimension(202, 44));

        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean needsRepaint = false;
                boolean isSwatchHovered = false;
                for (int i = 0; i < defaultColors.length; i++) {
                    ColorSwatch cs = defaultColors[i];
                    boolean oldState = cs.isHovered();

                    if (cs.isPointInBounds(e.getPoint())) {
                        Arrays.stream(defaultColors).forEach(colorSwatch -> colorSwatch.setHovered(false));
                        cs.setHovered(true);
                        isSwatchHovered = true;
                    } else {
                        cs.setHovered(false);
                    }

                    if (oldState != cs.isHovered()) {
                        needsRepaint = true;
                    }
                }

                if (isSwatchHovered) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                else {
                    setCursor(Cursor.getDefaultCursor());
                }

                if (needsRepaint) {
                    repaint();
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) { // left or right click
                    boolean needsRepaint = false;
                    for (int i = 0; i < defaultColors.length; i++) {
                        ColorSwatch cs = defaultColors[i];

                        if (cs.isPointInBounds(e.getPoint())) {
                            if (e.getButton() == MouseEvent.BUTTON1) { // left click
                                choicesHolder.setPaintColor(cs.getColor());

                                // let subscribers know paint color was just changed
                                for (ColorSelectListener listener : listeners) {
                                    listener.onPaintColorChanged(cs.getColor());
                                }
                            }
                            else if (e.getButton() == MouseEvent.BUTTON3) { // right click
                                choicesHolder.setEraseColor(cs.getColor());

                                // let subscribers know erase color was just changed
                                for (ColorSelectListener listener : listeners) {
                                    listener.onEraseColorChanged(cs.getColor());
                                }
                            }
                            needsRepaint = true;
                            break;
                        }
                    }
                    if (needsRepaint) {
                        repaint();
                    }
                }
            }
        });

    }


    protected ColorSwatch[] loadDefaultColors() {
        // TODO: this should come from a file, or a file should be created if doesn't already exist
        Color[] defaultColors = new Color[] {
            new Color(0, 0, 0),
            new Color(255, 255, 255),
            new Color(133, 20, 75),
            new Color(255, 65, 54),
            new Color(255, 133, 27),
            new Color(255, 220, 0),
            new Color(61, 153, 112),
            new Color(46, 204, 64),
            new Color(0, 116, 217),
            new Color(127, 219, 255),
            new Color(0, 31, 63),
            new Color(57, 204, 204),
            new Color(177, 13, 201),
            new Color(240, 18, 190),
            new Color(124, 71, 0),
            new Color(153, 121, 80),
            new Color(170, 170, 170),
            new Color(221, 221, 221)
        };

        ColorSwatch[] colorSwatches = new ColorSwatch[defaultColors.length];
        int index = 0;
        for (Color color : defaultColors) {
            int x = (22 * (index / 2)) + 2;
            int y = index % 2 == 0 ? 4 : 26;
            colorSwatches[index] = new ColorSwatch(color, new Point(x, y), new Dimension(16, 16));
            index++;
        }
        return colorSwatches;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D) g;
        for (ColorSwatch colorSwatch : defaultColors) {
            colorSwatch.paint(brush);
        }
    }

    public void addListener(ColorSelectListener listener) {
        listeners.add(listener);
    }
}
