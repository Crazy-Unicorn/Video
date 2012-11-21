package video;

/**
 *
 * @author goryunov
 */
public class Hopfield {

    private int width;
    private int height;
    private int[][] net;
    
    private int[][] examples;
    
    private int exCount = 1;
    
    public Hopfield(int width, int height) {
        
        this.width = width;
        this.height = height;
        
        int count = width*height;
        
        this.net = new int[count][count];

        for (int i=0; i<count; i++)
            for (int j=0; j<count; j++)
                this.net[i][j]=0;
        
        this.examples = new int[this.exCount][count];
        for (int e=0; e<this.exCount; e++)
            for (int i=0; i<count; i++)
                    this.examples[e][i]=-1;
        
        this.examples[0][0] = 1;
        this.examples[0][1] = 1;
    }
    
    public void learn() {
        int count = width*height;
        for (int i = 0; i < count; i++) 
            for (int j = 0; j < count; j++) 
                if (i != j) {
                    for (int l = 0; l < this.exCount; l++) {
                        net[i][j] += examples[l][i] * examples[l][j];
                    }
                }
    }
    
    public void identify(int[] figure) {

        int count = width*height;
        double sum;

        boolean changed;
        while (true) {
            changed = false;
            for (int j = 0; j < count; j++) {
                sum = 0;
                for (int i = 0; i < count; i++) {
                    sum += net[i][j] * figure[i];
                }
                if (sum * figure[j] < 0) {
                    figure[j] = -figure[j];
                    changed = true;
                    break;
                }
            }
            if (changed == false) {
                break;
            }
        }
        
    }
    
    public int[] resize(int[][] figure, int width, int height) {
        int[] newfigure = new int[this.width*this.height];
        
        if (this.width>width) {
            
        }
        
        return null;
    }
    
}
