package GUI;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import Utils.OperatingSystem;
import Utils.OperatingSystemUtils;

// uses custom JFileChooser on Windows,
// and FileDialog on Mac

// JFileChooser straight up doesn't really work anymore on modern versions of MacOS
// However FileDialog is really bad on Windows
// ...so this class ensures JFileChooser is used on Windows, while FileDialog is used on MacOS

 
public class FilePicker {

    public static FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG (*.png)", "png", "*.png");
    public static FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG (*.jpeg, *.jpg)", "jpeg", "jpg", "*.jpeg", "*.jpg");

    public static FilePickerResponse showDialog(FilePickerType filePickerType) {
        if (OperatingSystemUtils.getOperatingSystem() == OperatingSystem.WINDOWS) {
            FileChooserCustom fileChooserCustom = new FileChooserCustom(filePickerType);
            int returnValue = fileChooserCustom.load();
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                return new FilePickerResponse(fileChooserCustom.getSelectedFile(), fileChooserCustom.getFileFilter());
            }
            return new FilePickerResponse(null, null);
        }
        else {
            FileDialogCustom fileDialogCustom = new FileDialogCustom(filePickerType);
            if (fileDialogCustom.getFile() != null) {
                return new FilePickerResponse(new File(fileDialogCustom.getDirectory() + fileDialogCustom.getFile()), null);
            }
            return new FilePickerResponse(null, null);
        }
    }
}
