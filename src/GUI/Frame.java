package GUI;

import Canvas.Canvas;
import Models.ChoicesHolder;
import Toolstrip.MenuBar;
import Toolstrip.ToolStrip;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import Canvas.*;
import Toolstrip.*;
import Models.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import static javax.swing.WindowConstants.*;

public class Frame implements MenuBarListener {
    private JFrame frame;
    private JPanel mainPanel;
    private Canvas canvas;
    private ToolStrip toolstrip;
    private ChoicesHolder choicesHolder;

    public Frame() {
        setSwingStyle();
        frame = new JFrame();
        frame.setIconImages(getIcons());
        mainPanel = new JPanel();
        choicesHolder = new ChoicesHolder();

        canvas = new Canvas(choicesHolder);
        MenuBar menuBar = new MenuBar(canvas, choicesHolder);
        menuBar.addListener(this);
        canvas.getCanvasHistory().addListener(menuBar);
        choicesHolder.addListener(canvas);
        toolstrip = new ToolStrip(choicesHolder);
        choicesHolder.addListener(toolstrip);
        canvas.addListener(toolstrip);
        canvas.addListener(menuBar);

        mainPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(canvas);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(toolstrip, BorderLayout.NORTH);
        frame.setTitle("Untitled - JPaint");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setJMenuBar(menuBar);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                // if closing window and canvas is dirty, prompt user to save
                if (canvas.isDirty()) {
                    int result = menuBar.promptToSave(canvas);
                    if (result != JOptionPane.CANCEL_OPTION) {
                        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    }
                }
            }
        });

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

    @Override
    public void onFileOpened(String filePath) {
        if (filePath == null) {
            frame.setTitle("Untitled - JPaint");
        }
        else {
            File file = new File(filePath);
            frame.setTitle(file.getName() + " - JPaint");
        }
    }
}