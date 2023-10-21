package GUI;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FilePickerResponse {
    private File selectedFile;
    private FileFilter selectedFileFilter;

    public FilePickerResponse(File selectedFile, FileFilter selectedFileFilter) {
        this.selectedFile = selectedFile;
        this.selectedFileFilter = selectedFileFilter;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public FileFilter getSelectedFileFilter() {
        return selectedFileFilter;
    }
}
