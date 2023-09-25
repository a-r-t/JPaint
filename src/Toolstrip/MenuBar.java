package Toolstrip;

import Canvas.Canvas;
import Canvas.CanvasListener;
import Canvas.CanvasHistoryListener;
import GUI.FileChooser;
import Models.ChoicesHolder;
import Models.ChoicesListener;
import Utils.ClipboardUtils;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class MenuBar extends JMenuBar implements CanvasListener, CanvasHistoryListener {
    private String currentlyOpenFile = null;
    private JMenuItem newCanvas;
    private JMenuItem open;
    private JMenuItem save;
    private JMenuItem saveAs;
    private JMenuItem undo;
    private JMenuItem redo;
    private JMenuItem cut;
    private JMenuItem copy;
    private JMenuItem paste;
    private JMenuItem canvasSize;
    private ArrayList<MenuBarListener> listeners = new ArrayList<>();

    public MenuBar(Canvas canvas, ChoicesHolder choicesHolder) {
        createFileSection(canvas);
        createEditSection(canvas, choicesHolder);

        JMenu help = new JMenu("Help");
        add(help);
    }

    // all menu logic for the "File" section
    private void createFileSection(Canvas canvas) {
        JMenu file = new JMenu("File");
        add(file);

        newCanvas = new JMenuItem("New");
        newCanvas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.reset();
            }
        });
        newCanvas.setIcon(new ImageIcon(JMenuBar.class.getResource("/new-canvas-icon.png")));
        newCanvas.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(newCanvas);

        open = new JMenuItem("Open");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile(canvas);
            }
        });
        open.setIcon(new ImageIcon(JMenuBar.class.getResource("/open-icon.png")));
        open.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(open);

        save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(canvas);
            }
        });
        save.setIcon(new ImageIcon(JMenuBar.class.getResource("/save-icon.png")));
        save.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        file.add(save);

        saveAs = new JMenuItem("Save As");
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsFile(canvas);
            }
        });
        saveAs.setIcon(new ImageIcon(JMenuBar.class.getResource("/save-as-icon.png")));
        saveAs.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | java.awt.event.InputEvent.SHIFT_MASK));
        file.add(saveAs);
    }

    private void createEditSection(Canvas canvas, ChoicesHolder choicesHolder) {
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

        edit.add(new JSeparator());

        cut = new JMenuItem("Cut");
        cut.setEnabled(false);
        cut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage selectedSubImage = canvas.getSelectedSubimage();
                if (selectedSubImage != null) {
                    ClipboardUtils.copyToClipboard(selectedSubImage);
                    canvas.clearSelectedSubimage();
                }
            }
        });
        cut.setIcon(new ImageIcon(JMenuBar.class.getResource("/scissors-icon.png")));
        cut.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        edit.add(cut);

        copy = new JMenuItem("Copy");
        copy.setEnabled(false);
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage selectedSubImage = canvas.getSelectedSubimage();
                if (selectedSubImage != null) {
                    ClipboardUtils.copyToClipboard(selectedSubImage);
                }
            }
        });
        copy.setIcon(new ImageIcon(JMenuBar.class.getResource("/copy-icon.png")));
        copy.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        edit.add(copy);

        paste = new JMenuItem("Paste");
        paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage clipboardImage = ClipboardUtils.copyToImage();
                if (clipboardImage != null) {
                    choicesHolder.setTool(Tool.RECTANGLE_SELECT);
                    if (canvas.getCanvasWidth() < clipboardImage.getWidth()) {
                        canvas.resizeCanvas(clipboardImage.getWidth(), canvas.getCanvasHeight());
                    }
                    if (canvas.getCanvasHeight() < clipboardImage.getHeight()) {
                        canvas.resizeCanvas(canvas.getCanvasWidth(), clipboardImage.getHeight());
                    }
                    canvas.setSelectedSubimage(clipboardImage);
                    canvas.revalidate();
                    canvas.repaint();
                }
            }
        });
        paste.setIcon(new ImageIcon(JMenuBar.class.getResource("/paste-icon.png")));
        paste.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        edit.add(paste);

        edit.add(new JSeparator());

        canvasSize = new JMenuItem("Canvas Size");
        canvasSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // pops open a dialog asking user for width and height to set canvas to
                JTextField widthTextField = new JTextField(String.valueOf(canvas.getCanvasWidth()));
                widthTextField.setPreferredSize(new Dimension(50, 20));
                JTextField heightTextField = new JTextField(String.valueOf(canvas.getCanvasHeight()));
                heightTextField.setPreferredSize(new Dimension(50, 20));
                final JComponent[] inputs = new JComponent[] {
                        new JLabel("Width"),
                        widthTextField,
                        new JLabel("Height"),
                        heightTextField
                };
                int result = JOptionPane.showConfirmDialog(null, inputs, "Canvas Size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    int width = 0;
                    int height = 0;
                    try {
                        width = Integer.parseInt(widthTextField.getText());
                        height = Integer.parseInt(heightTextField.getText());
                    }
                    catch (Exception ex) {}
                    if (width > 0 && width < Integer.MAX_VALUE && height > 0 && height < Integer.MAX_VALUE) {
                        canvas.getCanvasHistory().createPerformedState();
                        canvas.resizeCanvas(width, height);
                    }
                }
            }
        });
        edit.add(canvasSize);
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

    private void openFile(Canvas canvas) {
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
                    canvas.setMainImage(chosenFileImage);
                    canvas.fitCanvasToMainImage();

                    currentlyOpenFile = chosenFile.getAbsolutePath();
                    canvas.setIsDirty(false);

                    // let subscribers know a file was opened
                    for (MenuBarListener listener : listeners) {
                        listener.onFileOpened(currentlyOpenFile);
                    }
                } catch (IOException ioException) {
                    // TODO: Error message in UI that file can't be read
                }
            }
        }
    }

    private void saveFile(Canvas canvas) {
        if (currentlyOpenFile == null) {
            saveAsFile(canvas);
        }
        else {
            String saveFileType = null;
            if (currentlyOpenFile.endsWith(".jpg") || currentlyOpenFile.endsWith(".jpeg")) {
                saveFileType = "JPEG";
            } else {
                saveFileType = "PNG";
            }

            try {
                ImageIO.write(canvas.getMainImage().getRaw(), saveFileType, new File(currentlyOpenFile));
                canvas.setIsDirty(false);
            } catch (IOException e) {
                // TODO: Error message in UI that file can't be saved
            }
        }
    }

    private void saveAsFile(Canvas canvas) {
        JFileChooser fileChooser = new FileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText("Save");
        fileChooser.setApproveButtonMnemonic('a');
        fileChooser.setApproveButtonToolTipText("Save file from application");
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png", "*.png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG (*.jpeg, *.jpg)", "jpeg", "jpg", "*.jpeg", "*.jpg");
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.addChoosableFileFilter(jpgFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(pngFilter);
        int returnVal = fileChooser.showDialog(null, "Save");

        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            if (chosenFile != null) {
                String filePath = chosenFile.getAbsolutePath();
                String fileType = "";

                if (fileChooser.getFileFilter() == pngFilter) {
                    if (!filePath.endsWith(".png")) {
                        filePath = filePath + ".png";
                    }
                    fileType = "PNG";
                }
                else if (fileChooser.getFileFilter() == jpgFilter) {
                    if ((!filePath.endsWith(".jpg") || !filePath.endsWith(".jpeg"))) {
                        filePath = filePath + ".jpg";
                    }
                    fileType = "JPEG";
                }

                try {
                    ImageIO.write(canvas.getMainImage().getRaw(), fileType, new File(filePath));
                    currentlyOpenFile = filePath;
                    canvas.setIsDirty(false);

                    // let subscribers know a file was opened
                    for (MenuBarListener listener : listeners) {
                        listener.onFileOpened(currentlyOpenFile);
                    }
                } catch (IOException e) {
                    // TODO: Error message in UI that file can't be saved
                }
            }
        }
    }


    @Override
    public void onPaintColorChanged(Color color) {
        // unused interface method
    }

    @Override
    public void onEraseColorChanged(Color color) {
        // unused interface method
    }

    @Override
    public void onEyeDropperUsedToChangePaintColor() {
        // unused interface method
    }

    @Override
    public void onSelectedSubImageChanged(BufferedImage subImage) {
        cut.setEnabled(subImage != null);
        copy.setEnabled(subImage != null);
    }

    public void addListener(MenuBarListener listener) {
        listeners.add(listener);
    }
}
