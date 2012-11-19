package video;

import java.io.File;

/**
 *
 * @author goryunov
 */
public class FileChooserFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File f) {
        return true;
    }

    public String getDescription() {
        return "все файлы";
    }
}
