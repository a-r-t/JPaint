package Toolstrip;

import javax.swing.*;
import java.awt.*;

public class ColorPickerDialog extends JDialog {
    public ColorPickerDialog(JFrame parent) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 400));
        pack();
        setLocationRelativeTo(parent);
        setModal(true);
        setTitle("Color Picker");
        setResizable(false);

        ColorPicker colorPicker = new ColorPicker();
        add(colorPicker, BorderLayout.CENTER);

        setVisible(true);
    }
}
