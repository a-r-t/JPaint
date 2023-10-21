package GUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Arrays;
import java.util.stream.Stream;

public class FileChooserCustom extends JFileChooser {
    private String approveButtonText = "";

    public FileChooserCustom(FilePickerType filePickerType) {

        if (filePickerType == FilePickerType.OPEN) {
            setDialogType(JFileChooser.OPEN_DIALOG);
            setApproveButtonText("Open");
            approveButtonText = "Open";
            setApproveButtonMnemonic('a');
            setApproveButtonToolTipText("Open file in application");
            setFileFilter(new FileNameExtensionFilter("Image Files (*.jpg, *.jpeg, *.png)", "jpg", "jpeg", "png"));
        }
        else if (filePickerType == FilePickerType.SAVE) {
            setDialogType(JFileChooser.SAVE_DIALOG);
            setApproveButtonText("Save");
            approveButtonText = "Save";
            setApproveButtonMnemonic('a');
            setApproveButtonToolTipText("Save file from application");
            addChoosableFileFilter(FilePicker.pngFilter);
            addChoosableFileFilter(FilePicker.jpgFilter);
            setAcceptAllFileFilterUsed(false);
            setFileFilter(FilePicker.pngFilter);
        }
        
        // this post gave me this wacky code to make the file chooser scroll vertically instead of horizontally: https://stackoverflow.com/a/47570750/16948475
        stream(this)
                .filter(JList.class::isInstance)
                .map(JList.class::cast)
                .findFirst()
                .ifPresent(FileChooserCustom::addHierarchyListener);
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

    public int load() {
        return showDialog(null, approveButtonText);
    }
}
