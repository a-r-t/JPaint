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
        frame = new JFrame();
        mainPanel = new JPanel();

        selectionsHolder = new SelectionsHolder();

        canvas = new Canvas(selectionsHolder);
        toolstrip = new ToolStrip(selectionsHolder);

        mainPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, new Color(197, 207, 223)));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(toolstrip, BorderLayout.NORTH);
        frame.setTitle("JPaint");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}