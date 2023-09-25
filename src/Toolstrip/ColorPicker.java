package Toolstrip;

import javax.swing.*;
import java.awt.*;

public class ColorPicker extends JPanel {
    public ColorPicker() {
        setLayout(null);
        JSlider redSlider = new JSlider() {
            @Override
            public void updateUI() {
                setUI(new ColorSlider(this));
            }
        };
        redSlider.setSize(255, 20);
        add(redSlider);
    }
}
