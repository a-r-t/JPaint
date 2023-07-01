import java.awt.*;

import javax.swing.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Frame {
    private JFrame frame;
    private JPanel mainPanel;
    private Canvas canvas;
    private ToolStrip toolstrip;
    private SelectionsHolder selectionsHolder;

    public Frame() {
        setSwingStyle();
        frame = new JFrame();
        mainPanel = new JPanel();

        selectionsHolder = new SelectionsHolder();
        canvas = new Canvas(selectionsHolder);
        toolstrip = new ToolStrip(selectionsHolder);

        mainPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(canvas);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(toolstrip, BorderLayout.NORTH);
        frame.setTitle("JPaint");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setSwingStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}