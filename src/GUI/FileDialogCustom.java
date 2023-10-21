package GUI;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.awt.Frame;

public class FileDialogCustom extends FileDialog {
    public FileDialogCustom(FilePickerType filePickerType) {
        super(dummy(), "", convertFilePickerType(filePickerType));

        setFile("*.png;*.jpg;*.jpeg");
        setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
            }
        });

        if (filePickerType == FilePickerType.OPEN) {
            setTitle("Choose a file");
        }
        else if (filePickerType == FilePickerType.SAVE) {
            setTitle("Choose where to save file");
        }
        setVisible(true);
    }

    // FileDialog is such a pain to work with, yes this is actually needed because if null is passed in directly to the super class constructor, it causes a "constructor calls are ambiguous" compilation error due to how horribly the super class is laid out
    private static Frame dummy() {
        return null;
    }

    private static int convertFilePickerType(FilePickerType filePickerType) {
        switch (filePickerType) {
            case OPEN:
                return FileDialogCustom.LOAD;
            case SAVE:
                return FileDialogCustom.SAVE;
            default:
                throw new RuntimeException("Invalid argument", null); // shouldn't ever happen
        }
    }
}
