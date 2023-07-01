import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar() {
        JMenu file = new JMenu("File");

        add(file);

        JMenu edit = new JMenu("Edit");

        add(edit);

        JMenu help = new JMenu("Help");

        add(help);
    }
}
