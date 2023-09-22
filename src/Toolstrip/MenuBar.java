package Toolstrip;

import Canvas.Canvas;
import Canvas.CanvasHistoryListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar implements CanvasHistoryListener {

    private JMenuItem undo;
    private JMenuItem redo;

    public MenuBar(Canvas canvas) {
        JMenu file = new JMenu("File");

        add(file);

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
}
