package Toolstrip;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class ColorPicker extends JPanel {
    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    
    public ColorPicker() {
        setLayout(null);

        JLabel redLabel = new JLabel("R:");
        redLabel.setSize(30, 20);
        redLabel.setLocation(10, 0);
        redLabel.setForeground(Color.red);
        add(redLabel);

        JTextField redColorValue = new JTextField();
        redColorValue.setSize(50, 20);
        redColorValue.setLocation(300, 0);
        redColorValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onColorTextValueChange(redColorValue, redSlider);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onColorTextValueChange(redColorValue, redSlider);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onColorTextValueChange(redColorValue, redSlider);
            }
        });
        add(redColorValue);

        redSlider = new JSlider() {
            @Override
            public void updateUI() {
                ColorSlider cs = new ColorSlider(this);
                cs.setFillColor(new Color(255, 0, 0));
                cs.setEnabledThumbColor(new Color(255, 0, 0));
                cs.setDisabledThumbColor((new Color(200, 200 ,200)));
                setUI(cs);
            }
        };
        redSlider.setMaximum(255);
        redSlider.setMinimum(0);
        redSlider.setSize(255, 20);
        redSlider.setLocation(30, 0);
        redSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChange(redSlider, redColorValue);
            }
        });
        add(redSlider);
        redColorValue.setText(String.valueOf(redSlider.getValue()));

        JLabel greenLabel = new JLabel("G:");
        greenLabel.setSize(30, 20);
        greenLabel.setLocation(10, 50);
        greenLabel.setForeground(new Color(14, 193, 14));
        add(greenLabel);

        JTextField greenColorValue = new JTextField();
        greenColorValue.setSize(50, 20);
        greenColorValue.setLocation(300, 50);
        greenColorValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onColorTextValueChange(greenColorValue, greenSlider);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onColorTextValueChange(greenColorValue, greenSlider);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onColorTextValueChange(greenColorValue, greenSlider);
            }
        });
        add(greenColorValue);

        greenSlider = new JSlider() {
            @Override
            public void updateUI() {
                ColorSlider cs = new ColorSlider(this);
                cs.setFillColor(new Color(14, 193, 14));
                cs.setEnabledThumbColor(new Color(14, 193, 14));
                cs.setDisabledThumbColor((new Color(200, 200 ,200)));
                setUI(cs);
            }
        };
        greenSlider.setMaximum(255);
        greenSlider.setMinimum(0);
        greenSlider.setSize(255, 20);
        greenSlider.setLocation(30, 50);
        greenSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChange(greenSlider, greenColorValue);
            }
        });
        add(greenSlider);
        greenColorValue.setText(String.valueOf(greenSlider.getValue()));

        JLabel blueLabel = new JLabel("B:");
        blueLabel.setSize(30, 20);
        blueLabel.setLocation(10, 100);
        blueLabel.setForeground(Color.blue);
        add(blueLabel);

        JTextField blueColorValue = new JTextField();
        blueColorValue.setSize(50, 20);
        blueColorValue.setLocation(300, 100);
        blueColorValue.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onColorTextValueChange(blueColorValue, blueSlider);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onColorTextValueChange(blueColorValue, blueSlider);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onColorTextValueChange(blueColorValue, blueSlider);
            }
        });
        add(blueColorValue);

        blueSlider = new JSlider() {
            @Override
            public void updateUI() {
                ColorSlider cs = new ColorSlider(this);
                cs.setFillColor(new Color(0, 0, 255));
                cs.setEnabledThumbColor(new Color(0, 0, 255));
                cs.setDisabledThumbColor((new Color(200, 200 ,200)));
                setUI(cs);
            }
        };
        blueSlider.setMaximum(255);
        blueSlider.setMinimum(0);
        blueSlider.setSize(255, 20);
        blueSlider.setLocation(30, 100);
        blueSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChange(blueSlider, blueColorValue);
            }
        });
        add(blueSlider);
        blueColorValue.setText(String.valueOf(blueSlider.getValue()));

    }

    private boolean isColorValueValid(String colorValue) {
        int value = 0;
        boolean isValid = true;
        try {
            value = Integer.parseInt(colorValue);
            if (value < 0 || value > 255) {
                isValid = false;
            }
        }
        catch(NumberFormatException ex) {
            isValid = false;
        }
        return isValid;
    }

    private void onColorTextValueChange(JTextField colorTextField, JSlider colorSlider) {
        String textValue = colorTextField.getText();
        if (isColorValueValid(textValue)) {
            colorSlider.setEnabled(true);
            colorSlider.setValue(Integer.parseInt(textValue));
            ((ColorSlider)colorSlider.getUI()).setThumbEnabled(true);
        }
        else {
            colorSlider.setValue(0);
            ((ColorSlider)colorSlider.getUI()).setThumbEnabled(false);
            colorSlider.setEnabled(false);
        }
    }

    private void onColorSliderValueChange(JSlider colorSlider, JTextField colorTextField) {
        String colorValue = colorTextField.getText();
        if (isColorValueValid(colorValue) && Integer.parseInt(colorValue) != colorSlider.getValue()) {
            colorTextField.setText(String.valueOf(colorSlider.getValue()));
        }
    }
}
