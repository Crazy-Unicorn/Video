package video;

import java.util.ArrayList;

/**
 *
 * @author goryunov
 */
public class RaskrasObject {
    
    /*private ArrayList<ArrayList<Integer>> object;
    
    private int width = 0;
    private int height = 0;
    
    private int left = 0;
    private int top = 0;*/

    public int left = 0;
    public int top = 0;
    
    public int width = 0;
    public int height = 0;
    
    //public int[][] img;
    
    public int getRight() {
        return left + width;
    }
    
    public int getBottom() {
        return top + height;
    }
    
}
