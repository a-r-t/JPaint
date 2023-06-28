import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ToolStrip extends JPanel {
    private ArrayList<ToolButton> toolButtons = new ArrayList<>();
    private SelectionsHolder selectionsHolder;

    public ToolStrip(SelectionsHolder selectionsHolder) {
        this.selectionsHolder = selectionsHolder;


        setBackground(new Color(245, 246, 247));
        setPreferredSize(new Dimension(getPreferredSize().width, 50));
        setLayout(null);

        ToolButton pencil = new ToolButton("pencil-icon-transparent.png", Tool.PENCIL);
        ToolButton bucket = new ToolButton("bucket-icon-transparent.png", Tool.BUCKET);

        toolButtons.add(pencil);
        toolButtons.add(bucket);


        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                boolean needsRepaint = false;
                for (int i = 0; i < toolButtons.size(); i++) {
                    ToolButton tb = toolButtons.get(i);
                    boolean oldState = tb.isHovered();

                    if (tb.isPointInBounds(e.getPoint())) {
                        toolButtons.forEach(toolButton -> toolButton.setHovered(false));
                        tb.setHovered(true);
                    } else {
                        tb.setHovered(false);
                    }

                    if (oldState != tb.isHovered()) {
                        needsRepaint = true;
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
                boolean needsRepaint = false;
                for (int i = 0;  i < toolButtons.size(); i++) {
                    ToolButton tb = toolButtons.get(i);
                    boolean oldState = tb.isSelected();

                    if (tb.isPointInBounds(e.getPoint())) {
                        toolButtons.forEach(toolButton -> toolButton.setSelected(false));
                        tb.setSelected(true);
                        selectionsHolder.setTool(tb.getTool());
                    }

                    if (oldState != tb.isSelected()) {
                        needsRepaint = true;
                    }
                }
                if (needsRepaint) {
                    repaint();
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pencil.setLocation(new Point(10, getHeight() / 2 - pencil.getHeight() / 2));
                bucket.setLocation(new Point(40, getHeight() / 2 - bucket.getHeight() / 2));
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



    }
}
