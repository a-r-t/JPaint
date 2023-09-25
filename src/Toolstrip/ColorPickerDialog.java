package Toolstrip;

import javax.swing.*;
import java.awt.*;

public class ColorPickerDialog extends JDialog {
    public ColorPickerDialog(JComponent parent, Color startingColor, ColorPickerListener listener) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 380));
        pack();
        setLocationRelativeTo(parent);
        setModal(true);
        setTitle("Color Picker");
        setResizable(false);

        ColorPicker colorPicker = new ColorPicker(this, startingColor, listener);
        add(colorPicker, BorderLayout.CENTER);

        setVisible(true);
    }
}
