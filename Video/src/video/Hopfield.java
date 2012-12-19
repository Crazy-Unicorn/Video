package video;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author goryunov
 */
public class Hopfield {

    private int width;
    private int height;
    private int[][] net;
    
    private ArrayList<int[]> examples;
    
    //private int exCount = 3;
    
    public Hopfield(int width, int height) {
        
        this.width = width;
        this.height = height;
        

    }
    
    public void setSize(int width, int height) {
        
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
        
        this.examples = new ArrayList<int[]>();//int[this.exCount][count];
        /*for (int e=0; e<this.exCount; e++)
            for (int i=0; i<count; i++)
                    this.examples[e][i]=-1;
        
        this.examples[0][0] = this.examples[0][1] = 1;
        this.examples[1][6] = this.examples[1][12] = this.examples[1][16] = this.examples[1][18] = 1;
        this.examples[2][6] = this.examples[2][12] = this.examples[2][16] = this.examples[2][18] = this.examples[2][24] = 1;*/
        
        /*int[] ex0 = new int[count];
        ex0[0] = ex0[1] = 1;
        this.examples.add(ex0);
        
        int[] ex1 = new int[count];
        ex1[6] = ex1[12] = ex1[16] = ex1[18] = 1;
        this.examples.add(ex1);

        int[] ex2 = new int[count];
        ex2[6] = ex2[12] = ex2[16] = ex2[18] = ex2[24] = 1;
        this.examples.add(ex2);*/
        
        //java.lang.System.out.println(System.getProperty("user.dir"));
        
        File f = new File(System.getProperty("user.dir")+"/src/net_examples");
        PngFileFilter filter = new PngFileFilter();
        File[] list = f.listFiles(filter);
        
        if (list != null)
        for (int i=0; i<list.length; i++) {
            BufferedImage bufimg; 
            try {
                bufimg = ImageIO.read(new File(list[i].getAbsolutePath()));
                //Image img = (java.awt.Image)Toolkit.getDefaultToolkit().getImage(list[i].getAbsolutePath());


            
            //img.getGraphics().
            //BufferedImage bufimg = (BufferedImage)img;
                int bw = bufimg.getWidth();
                int bh = bufimg.getHeight();
                int[] ex = new int[bw*bh];
                for (int x=0; x<bw; x++)
                    for (int y=0; y<bh; y++) {
                        if (Foo.getColor(bufimg, x, y).equals(Color.BLACK))
                            ex[x*bh+y] = 1;
                        else
                            ex[x*bh+y] = -1;
                    }
                examples.add(ex);
                //java.lang.System.out.println(list[i].getAbsolutePath()+" "+bufimg.getWidth()+" "+bufimg.getHeight());
            } catch (IOException ex) {
                Logger.getLogger(Hopfield.class.getName()).log(Level.SEVERE, null, ex);
            }
            //java.lang.System.out.println(list[i].getAbsolutePath());
        }
        
        /*for (int s=0; s<examples.size(); s++) {
            java.lang.System.out.println();
            for (int q=0; q<examples.get(s).length; q++)
                java.lang.System.out.print(examples.get(s)[q]==1?"+1 ":examples.get(s)[q]+" ");
            java.lang.System.out.println();
        }*/
    }
    
    public void learn() {
        int count = width*height;
        int exsize = examples.size();
        for (int i = 0; i < count; i++) 
            for (int j = 0; j < count; j++) 
                if (i != j) {
                    for (int l = 0; l < exsize/*this.exCount*/; l++) {
                        int[] ex = examples.get(l);
                        net[i][j] += ex[i] * ex[j];
                    }
                }
        
        /*for (int i = 0; i < count; i++) 
            for (int j = 0; j < count; j++) {
                if (j==0)
                    java.lang.System.out.println();
                java.lang.System.out.print(net[i][j]>=0?"+"+net[i][j]+" ":net[i][j]+" ");
            }*/
    }
    
    public void identify_old(int[] figure) {

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
    
    public int[][] identify(int[][] figure) {

        //int count = width*height;
        double sum;

        int wh, whs;
        
        boolean changed;
        while (true) {
            changed = false;
            F: for (int i = 0; i < width; i++) {
                wh = i*height;
                for (int j = 0; j < height; j++) {
                    sum = 0;
                    for (int is = 0; is < width; is++) {
                        whs = is*height;
                        for (int js = 0; js < height; js++)
                            sum += net[whs+js][wh+j] * figure[is][js];
                    }
                    if (sum * figure[i][j] < 0) {
                        //java.lang.System.out.print("\nchanged "+i+" "+j+" "+sum+" "+(figure[i][j]>=0?"+"+figure[i][j]:figure[i][j]));                        
                        figure[i][j] = -figure[i][j];
                        changed = true;
                        break F;
                    }
                }
            }
            if (changed == false) {
                break;
            }
        }
        return figure;
    }
    
    public int[][] resize(int[][] figure, int width, int height) { //figure[width][height]
        
        int[][] tempfigure1 = new int[this.width][height];
        int[][] result = new int[this.width][this.height];
        
        int tw = this.width;
        int w = width;
        int th = this.height;
        int h = height;
        
        int multi = 0;
        int mod = 0;
        double koef = 0;
            
        if (tw==w) {
            for (int i=0; i<width; i++)
                System.arraycopy(figure[i], 0, tempfigure1[i], 0, height);
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
                        tempfigure1[iwtw++][j] = figure[iw][j];
                    if (mod>0) {
                        int ndiv = (int) (iw/koef);
                        if (ndiv!=div) {
                            div = ndiv;
                            tempfigure1[iwtw++][j] = figure[iw][j];
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
                    tempfigure1[itw][j] = figure[(int)(itw*koef)][j];
            }
        }
        
        if (th==h) {
            for (int i=0; i<tw; i++)
                System.arraycopy(tempfigure1[i], 0, result[i], 0, height);
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
                        result[i][jhth++] = tempfigure1[i][jh];
                    if (mod>0) {
                        int ndiv = (int) (jh/koef);
                        if (ndiv!=div) {
                            div = ndiv;
                            result[i][jhth++] = tempfigure1[i][jh];
                        } 
                    }
                }
            }
        }
        
        if (th<h) {
            koef = (double)h/th;
            for (int i=0; i<tw; i++) {
                for (int jth=0; jth<th; jth++)
                    result[i][jth] = tempfigure1[i][(int)(jth*koef)];
            }
        }
        return result;
    }
    

    public int[][] resize_back(int[][] figure, int toWidth, int toHeight) { //figure[this.width][this.height]

        int[][] tempfigure1 = new int[toWidth][this.height];
        int[][] result = new int[toWidth][toHeight];

        int tw = this.width;
        int w = toWidth;
        int th = this.height;
        int h = toHeight;

        int multi = 0;
        int mod = 0;
        double koef = 0;

        if (tw==w) {
            for (int i=0; i<width; i++)
                System.arraycopy(figure[i], 0, tempfigure1[i], 0, height);
        }
        
        if (tw>w) {
            multi = w / tw;
            mod = w % tw;
            koef = 0;
            if (mod>0)
                koef = (double)tw/mod;
            for (int j=0; j<th; j++) {
                //int div = -1;
                for (int itw=0, iww=0, div=-1; itw<tw; itw++) {
                    for (int iw=0; iw<multi; iw++)
                        tempfigure1[iww++][j] = figure[itw][j];
                    if (mod>0) {
                        int ndiv = (int) (itw/koef);
                        if (ndiv!=div) {
                            div = ndiv;
                            tempfigure1[iww++][j] = figure[itw][j];
                        }
                        //if (iw%koef==0)
                        //    tempfigure[j][iwtw++] = figure[j][iw];
                    }
                }
            }
        }

        if (tw<w) {
            koef = (double)tw/w;
            for (int j=0; j<th; j++) {
                for (int iw=0; iw<w; iw++)
                    //try {
                        tempfigure1[iw][j] = figure[(int)(iw*koef)][j];
                    //} catch (Exception e) {
                    //    System.out.println("#1 itw = "+itw+" j = "+j+" (int)(itw*koef) = "+(int)(itw*koef)+" width = "+width+" "+e.toString());
                    //}
            }
        }
    
            
        if (th==h) {
            for (int i=0; i<w; i++)
                System.arraycopy(tempfigure1[i], 0, result[i], 0, toHeight);
        }

        if (th>h) {
            multi = h / th;
            mod = h % th;
            koef = 0;
            if (mod>0)
                koef = (double)th/mod;
            for (int i=0; i<w; i++) {
                for (int jth=0, jhh=0, div=-1; jth<h; jth++) {
                    for (int jh=0; jh<multi; jh++)
                        result[i][jhh++] = tempfigure1[i][jth];
                    if (mod>0) {
                        int ndiv = (int) (jth/koef);
                        if (ndiv!=div) {
                            div = ndiv;
                            result[i][jhh++] = tempfigure1[i][jth];
                        }
                    }
                }
            }
        }

        if (th<h) {
            koef = (double)th/h;
            for (int i=0; i<w; i++) {
                for (int jh=0; jh<h; jh++)
                    result[i][jh] = tempfigure1[i][(int)(jh*koef)];
            }
        }
        

        
        /*} catch (Exception e) {
            System.out.println("#2 "+e.toString());
        }*/
        return result;
    }


    public int[][] process(int[][] figure, int width, int height) {
        
        figure = resize(figure, width, height);
        figure = identify(figure);

        //System.out.println(figure.length+" x "+figure[0].length);
        
            figure = resize_back(figure, width, height);
            
        return figure;
    }
    
    public int[] resize_old(int[][] figure, int width, int height) { //figure[height][width]
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
    
    public void test_old() {
        long pre = System.currentTimeMillis();
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
        
        int[] result = resize_old(test, width, height);
        for (int h=0; h<this.height; h++)
            for (int w=0; w<this.width; w++) {
                if (w==0)
                    java.lang.System.out.println();
                if (w>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(result[h*this.width+w]==1?"#":result[h*this.width+w]);
            }

        java.lang.System.out.println("\n");
        long post = System.currentTimeMillis();
        java.lang.System.out.println((post-pre)+" test_old");
    }
    
    public void test() {
        long pre = System.currentTimeMillis();
        int width = 13;
        int height = 13;
        int[][] test = new int[width][height];
        for (int w=0; w<width; w++)
            for (int h=0; h<height; h++)
                test[w][h]=0;
        test[1][1] = test[2][1] = test[3][1] = test[9][1] = test[10][1] = test[11][1] = test[2][2] = test[3][2] = test[4][2] = test[8][2] = test[9][2] = test[10][2] = 
                test[3][3] = test[4][3] = test[5][3] = test[7][3] = test[8][3] = test[9][3] = test[3][4] = test[4][4] = test[5][4] = test[7][4] = test[8][4] = test[9][4] = 
                test[5][5] = test[6][5] = test[7][5] = test[5][6] = test[6][6] = test[7][6] = test[5][7] = test[6][7] = test[7][7] = test[5][8] = test[6][8] = test[7][8] = 
                test[5][9] = test[6][9] = test[7][9] = test[5][10] = test[6][10] = test[7][10] = test[5][11] = test[6][11] = test[7][11] = 
                1;
        
        java.lang.System.out.println("\nДо трансформации");
        
        for (int h=0; h<height; h++) {
            for (int w=0; w<width; w++) {
                if (w==0)
                    java.lang.System.out.println();
                if (w>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(test[w][h]==1?"#":test[w][h]);
            }
        }
        java.lang.System.out.println("\n\n\nПосле трансформации");
        
        int[][] result = resize(test, width, height);
        for (int h=0; h<this.height; h++)
            for (int w=0; w<this.width; w++) {
                if (w==0)
                    java.lang.System.out.println();
                if (w>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(result[w][h]==1?"#":result[w][h]);
            }

        java.lang.System.out.println("\n");
        long post = System.currentTimeMillis();
        java.lang.System.out.println((post-pre)+" test");
    }
    
}
