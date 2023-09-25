package Toolstrip;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorPicker extends JPanel {
    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    private JButton okayButton;
    JTextField redColorValue;
    JTextField greenColorValue;
    JTextField blueColorValue;

    public ColorPicker(ColorPickerDialog parent, Color startingColor, ColorPickerListener listener) {
        setLayout(null);

        okayButton = new JButton("OK");
        okayButton.setSize(80, 30);
        okayButton.setLocation(120, 300);
        okayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onColorChosen(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
                parent.dispose();
            }
        });
        add(okayButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setSize(80, 30);
        cancelButton.setLocation(210, 300);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.dispose();
            }
        });
        add(cancelButton);

        JLabel redLabel = new JLabel("R:");
        redLabel.setSize(30, 20);
        redLabel.setLocation(30, 30);
        redLabel.setForeground(Color.red);
        add(redLabel);

        redColorValue = new JTextField();
        redColorValue.setSize(50, 20);
        redColorValue.setLocation(310, 30);
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
        redSlider.setLocation(40, 30);
        redSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChange(redSlider, redColorValue);
            }
        });
        add(redSlider);

        JLabel greenLabel = new JLabel("G:");
        greenLabel.setSize(30, 20);
        greenLabel.setLocation(30, 60);
        greenLabel.setForeground(new Color(14, 193, 14));
        add(greenLabel);

        greenColorValue = new JTextField();
        greenColorValue.setSize(50, 20);
        greenColorValue.setLocation(310, 60);
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
        greenSlider.setLocation(40, 60);
        greenSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChange(greenSlider, greenColorValue);
            }
        });
        add(greenSlider);

        JLabel blueLabel = new JLabel("B:");
        blueLabel.setSize(30, 20);
        blueLabel.setLocation(30, 90);
        blueLabel.setForeground(Color.blue);
        add(blueLabel);

        blueColorValue = new JTextField();
        blueColorValue.setSize(50, 20);
        blueColorValue.setLocation(310, 90);
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
        blueSlider.setLocation(40, 90);
        blueSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onColorSliderValueChange(blueSlider, blueColorValue);
            }
        });
        add(blueSlider);

        JLabel selectedColorLabel = new JLabel("Selected Color");
        selectedColorLabel.setSize(100, 20);
        selectedColorLabel.setLocation(165, 140);
        add(selectedColorLabel);

        redSlider.setValue(startingColor.getRed());
        greenSlider.setValue(startingColor.getGreen());
        blueSlider.setValue(startingColor.getBlue());
        redColorValue.setText(String.valueOf(redSlider.getValue()));
        greenColorValue.setText(String.valueOf(greenSlider.getValue()));
        blueColorValue.setText(String.valueOf(blueSlider.getValue()));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D brush = (Graphics2D)g;

        brush.setColor(Color.black);
        brush.drawRect(150, 160, 100, 100);

        brush.setColor(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
        brush.fillRect(152, 162, 97, 97);
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
        updateOkButtonEnabledStatus();
        repaint();
    }

    private void updateOkButtonEnabledStatus() {
        if (isColorValueValid(redColorValue.getText()) && isColorValueValid(greenColorValue.getText()) && isColorValueValid(blueColorValue.getText())) {
            okayButton.setEnabled(true);
        }
        else {
            okayButton.setEnabled(false);
        }
    }

    private void onColorSliderValueChange(JSlider colorSlider, JTextField colorTextField) {
        String colorValue = colorTextField.getText();
        if (isColorValueValid(colorValue) && Integer.parseInt(colorValue) != colorSlider.getValue()) {
            colorTextField.setText(String.valueOf(colorSlider.getValue()));
        }
        repaint();
    }
}
