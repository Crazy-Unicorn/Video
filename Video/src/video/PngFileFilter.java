/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author goryunov
 */
public class PngFileFilter implements FileFilter {
    public boolean accept(File pathname)
    {
        // проверям что это файл и что он заканчивается на .txt
       return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".png");
    }
}
