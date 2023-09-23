package GUI;

import Toolstrip.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Arrays;
import java.util.stream.Stream;

public class FileChooser extends JFileChooser {
    public FileChooser() {

        // this post gave me this wacky code to make the file chooser scroll vertically instead of horizontally: https://stackoverflow.com/a/47570750/16948475
        stream(this)
                .filter(JList.class::isInstance)
                .map(JList.class::cast)
                .findFirst()
                .ifPresent(FileChooser::addHierarchyListener);
    }

    public static Stream<Component> stream(Container parent) {
        return Arrays.stream(parent.getComponents())
                .filter(Container.class::isInstance)
                .map(c -> stream(Container.class.cast(c)))
                .reduce(Stream.of(parent), Stream::concat);
    }

    private static void addHierarchyListener(JList<?> list) {
        list.addHierarchyListener(new HierarchyListener() {
            @Override public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                        && e.getComponent().isShowing()) {
                    list.putClientProperty("List.isFileList", Boolean.FALSE);
                    list.setLayoutOrientation(JList.VERTICAL);
                }
            }
        });
    }

}
