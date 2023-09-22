package Toolstrip;

import Canvas.Canvas;
import Canvas.CanvasHistoryListener;
import javax.swing.*;
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
        edit.add(undo);

        redo = new JMenuItem("Redo");
        redo.setEnabled(false);
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.getCanvasHistory().redo();
            }
        });
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
