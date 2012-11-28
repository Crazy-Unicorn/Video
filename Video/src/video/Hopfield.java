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
        

    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void init() {
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
    
    public int[] resize(int[][] figure, int width, int height) { //figure[height][width]
        int[] newfigure = new int[this.width*this.height];
        
        int[][] tempfigure1 = new int[height][this.width];
        int[][] tempfigure2 = new int[this.height][this.width];
        
        int tw = this.width;
        int w = width;
        int th = this.height;
        int h = height;
        
        int multi = 0;
        int mod = 0;
        double koef = 0;
            
        
        if (tw==w) {
            for (int j=0; j<height; j++)
                for (int i=0; i<width; i++)
                    tempfigure1[j][i] = figure[j][i];
        }
        
        if (tw>w) {
            multi = tw / w;
            mod = tw % w;
            koef = 0;
            if (mod>0)
                koef = (double)w/mod;
            for (int j=0; j<height; j++) {
                //int div = -1;
                for (int iw=0, iwtw=0, div=-1; iw<w; iw++) {
                    for (int itw=0; itw<multi; itw++)
                        tempfigure1[j][iwtw++] = figure[j][iw];
                    if (mod>0) {
                        int ndiv = (int) (iw/koef);
                        if (ndiv!=div) {
                            div = ndiv;
                            tempfigure1[j][iwtw++] = figure[j][iw];
                        } 
                        //if (iw%koef==0)
                        //    tempfigure[j][iwtw++] = figure[j][iw];
                    }
                }
            }
        }
        if (tw<w) {
            koef = (double)w/tw;
            for (int j=0; j<height; j++) {
                for (int itw=0; itw<tw; itw++)
                    tempfigure1[j][itw] = figure[j][(int)(itw*koef)];
            }
        }
        
        if (th==h) {
            for (int j=0; j<height; j++)
                for (int i=0; i<tw; i++)
                    tempfigure2[j][i] = tempfigure1[j][i];
        }
        
        if (th>h) {
            multi = th / h;
            mod = th % h;
            koef = 0;
            if (mod>0)
                koef = (double)h/mod;            
            for (int i=0; i<tw; i++) {
                for (int jh=0, jhth=0, div=-1; jh<h; jh++) {
                    for (int jth=0; jth<multi; jth++)
                        tempfigure2[jhth++][i] = tempfigure1[jh][i];
                    if (mod>0) {
                        int ndiv = (int) (jh/koef);
                        if (ndiv!=div) {
                            div = ndiv;
                            tempfigure2[jhth++][i] = tempfigure1[jh][i];
                        } 
                    }
                }
            }
        }
        
        if (th<h) {
            koef = (double)h/th;
            for (int i=0; i<tw; i++) {
                for (int jth=0; jth<th; jth++)
                    tempfigure2[jth][i] = tempfigure1[(int)(jth*koef)][i];
            }
        }
        
        for (int j=0; j<th; j++)
            for (int i=0; i<tw; i++)
                newfigure[j*tw+i] = tempfigure2[j][i];
        
        return newfigure;
    }
    
    public void test() {
        int width = 13;
        int height = 13;
        int[][] test = new int[height][width];
        for (int h=0; h<height; h++)
            for (int w=0; w<width; w++)
                test[h][w]=0;
        test[1][1] = test[1][2] = test[1][3] = test[1][9] = test[1][10] = test[1][11] = test[2][2] = test[2][3] = test[2][4] = test[2][8] = test[2][9] = test[2][10] = 
                test[3][3] = test[3][4] = test[3][5] = test[3][7] = test[3][8] = test[3][9] = test[4][3] = test[4][4] = test[4][5] = test[4][7] = test[4][8] = test[4][9] = 
                test[5][5] = test[5][6] = test[5][7] = test[6][5] = test[6][6] = test[6][7] = test[7][5] = test[7][6] = test[7][7] = test[8][5] = test[8][6] = test[8][7] = 
                test[9][5] = test[9][6] = test[9][7] = test[10][5] = test[10][6] = test[10][7] = test[11][5] = test[11][6] = test[11][7] = 
                1;
        
        java.lang.System.out.println("\nДо трансформации");
        
        for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
                if (w==0)
                    java.lang.System.out.println();
                if (w>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(test[h][w]==1?"#":test[h][w]);
            }
        }
        java.lang.System.out.println("\n\n\nПосле трансформации");
        
        int[] result = resize(test, width, height);
        for (int h=0; h<this.height; h++)
            for (int w=0; w<this.width; w++) {
                if (w==0)
                    java.lang.System.out.println();
                if (w>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(result[h*this.width+w]==1?"#":result[h*this.width+w]);
            }

        java.lang.System.out.println("\n");
    }
    
}
