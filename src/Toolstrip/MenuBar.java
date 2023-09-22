package Toolstrip;

import Canvas.Canvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar {

    public MenuBar(Canvas canvas) {
        JMenu file = new JMenu("File");

        add(file);

        JMenu edit = new JMenu("Edit");
        add(edit);

        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.getCanvasHistory().undo();
            }
        });
        edit.add(undo);

        JMenuItem redo = new JMenuItem("Redo");
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
}
