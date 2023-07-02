import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
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
        frame.setIconImages(getIcons());
        mainPanel = new JPanel();

        selectionsHolder = new SelectionsHolder();
        canvas = new Canvas(selectionsHolder);
        toolstrip = new ToolStrip(selectionsHolder);
        canvas.addListener(toolstrip);

        mainPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(canvas);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(toolstrip, BorderLayout.NORTH);
        frame.setTitle("JPaint");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setJMenuBar(new MenuBar());
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

    private ArrayList<BufferedImage> getIcons() {
        ArrayList<BufferedImage> icons = new ArrayList<>();
        try {
            // credit to Freepik for the icon files: https://www.flaticon.com/authors/freepik
            // icon file found here: https://www.flaticon.com/free-icon/paint-palette_2970785?term=paint&page=1&position=5&origin=tag&related_id=2970785#
            BufferedImage icon16 = ImageIO.read(Frame.class.getResource("/icon-16.png"));
            BufferedImage icon24 = ImageIO.read(Frame.class.getResource("/icon-24.png"));
            BufferedImage icon32 = ImageIO.read(Frame.class.getResource("/icon-32.png"));
            BufferedImage icon64 = ImageIO.read(Frame.class.getResource("/icon-64.png"));
            BufferedImage icon128 = ImageIO.read(Frame.class.getResource("/icon-128.png"));
            BufferedImage icon256 = ImageIO.read(Frame.class.getResource("/icon-256.png"));
            BufferedImage icon512 = ImageIO.read(Frame.class.getResource("/icon-512.png"));

            icons.add(icon16);
            icons.add(icon24);
            icons.add(icon32);
            icons.add(icon64);
            icons.add(icon128);
            icons.add(icon256);
            icons.add(icon512);

        }
        catch (IOException e) {
            System.out.println("Unable to load icon images! Using defaults.");
        }
        return icons;
    }
}