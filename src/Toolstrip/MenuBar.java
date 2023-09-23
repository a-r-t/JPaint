package Toolstrip;

import Canvas.Canvas;
import Canvas.CanvasHistoryListener;
import GUI.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class MenuBar extends JMenuBar implements CanvasHistoryListener {
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem undo;
    private JMenuItem redo;

    public MenuBar(Canvas canvas) {
        JMenu file = new JMenu("File");
        add(file);

        open = new JMenuItem("Open");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new FileChooser();
                fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                fileChooser.setApproveButtonText("Open");
                fileChooser.setApproveButtonMnemonic('a');
                fileChooser.setApproveButtonToolTipText("Open file in application");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
                int returnVal = fileChooser.showDialog(null, "Open");

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    if (chosenFile != null) {
                        BufferedImage chosenFileImage = null;
                        try {
                            chosenFileImage = ImageIO.read(chosenFile);
                        } catch (IOException ioException) {
                            // TODO: Error message in UI that file can't be read
                        }
                        canvas.setMainImage(chosenFileImage);
                        canvas.fitCanvasToMainImage();
                    }
                }
            }
        });
        open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(open);

        save = new JMenuItem("Save");
        save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(save);

        saveAs = new JMenuItem("Save As");
        saveAs.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.META_MASK));
        file.add(saveAs);

        JMenu edit = new JMenu("Edit");
        add(edit);

        undo = new JMenuItem("Undo");
        undo.setEnabled(false);
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.getCanvasHistory().undo();
            }
        });
        undo.setIcon(new ImageIcon(JMenuBar.class.getResource("/undo-icon.png")));
        undo.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        edit.add(undo);

        redo = new JMenuItem("Redo");
        redo.setEnabled(false);
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.getCanvasHistory().redo();
            }
        });
        redo.setIcon(new ImageIcon(JMenuBar.class.getResource("/redo-icon.png")));
        redo.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        edit.add(redo);

        JMenu help = new JMenu("Help");

        add(help);
    }

    @Override
    public void onHistorySizeChange(int performedSize, int recallSize) {
        undo.setEnabled(performedSize > 0);
        redo.setEnabled(recallSize > 0);
    }

    @Override
    public void onUndo() {
        // interface method not used
    }

    @Override
    public void onRedo() {
        // interface method not used
    }

}
