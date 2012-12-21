/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import com.sun.jndi.toolkit.ctx.HeadTail;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.demos.VideoImage;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import sun.security.action.GetPropertyAction;

public class Foo {

    private File file = null;

    private Hopfield net = null;
    
    
    private boolean stop = true;
    
    public void stop() {
        stop = true;
    }
    
    public void setNet(Hopfield net) {
        this.net = net;
    }
    
    public void fileChecker(File file) {
        //java.lang.System.out.println(filename);
        IContainer container = IContainer.make();

        String fullfilename = file.getPath();
        String filename = file.getName();
        if (container.open(fullfilename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: \n\n"
                    + filename);
        }

        int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;

        // нужно найти видео поток
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        if (videoStreamId == -1) // кажись не нашли
        {
            throw new RuntimeException("could not find video stream in container: \n\n"
                    + filename);
        }

        // пытаемся открыть кодек
        if (videoCoder.open() < 0) {
            throw new RuntimeException(
                    "could not open video decoder for container: \n\n" + filename);
        }

        this.file = file;
    }

    public void simpleview(VideoPanel panel, MainFrame frame) {
        //while(true)
        //panel.paintComponent(panel.getGraphics());
        //openJavaWindow();
        stop = false;
        
        frame.setPassive();
        
        if (this.file == null) {
                throw new RuntimeException("Проблемы с файлом!");
            }

        if (!IVideoResampler.isSupported(
            IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
        throw new RuntimeException("you must install the GPL version" +
      		" of Xuggler (with IVideoResampler support) for " +
      		"this demo to work");

        IContainer container = IContainer.make();

        String fullfilename = file.getPath();
        String filename = file.getName();
        
        //File outdir0 = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out");
        //outdir0.mkdir();
        //File outdir = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out/"+file.getName().substring(0, filename.indexOf(".")));
        //outdir.mkdir();
        
        if (container.open(fullfilename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: \n\n"
                    + filename);
        }

        int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;

        // нужно найти видео поток
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        if (videoStreamId == -1) // кажись не нашли
        {
            throw new RuntimeException("could not find video stream in container: \n\n"
                    + filename);
        }

        // пытаемся открыть кодек
        if (videoCoder.open() < 0) {
            throw new RuntimeException(
                    "could not open video decoder for container: \n\n" + filename);
        }

        IVideoResampler resampler = null;
        if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24)
        {
        // if this stream is not in BGR24, we're going to need to
        // convert it.  The VideoResampler does that for us.
        resampler = IVideoResampler.make(videoCoder.getWidth(), 
            videoCoder.getHeight(), IPixelFormat.Type.BGR24,
            videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
        if (resampler == null)
            throw new RuntimeException("could not create color space " +
                            "resampler for: " + filename);
        }
        /*
        * And once we have that, we draw a window on screen
        */
        //openJavaWindow();

    /*
     * Now, we start walking through the container looking at each packet.
     */
        IPacket packet = IPacket.make();
        long firstTimestampInStream = Global.NO_PTS;
        long systemClockStartTime = 0;
        while(container.readNextPacket(packet) >= 0)
        {
        /*
        * Now we have a packet, let's see if it belongs to our video stream
        */
        if (packet.getStreamIndex() == videoStreamId)
        {
            /*
            * We allocate a new picture to get the data out of Xuggler
            */
            IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                videoCoder.getWidth(), videoCoder.getHeight());

            int offset = 0;
            while(offset < packet.getSize())
            {
                if (stop)
                    break;
            /*
            * Now, we decode the video, checking for any errors.
            * 
            */
            int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
            if (bytesDecoded < 0)
                throw new RuntimeException("got error decoding video in: "
                    + filename);
            offset += bytesDecoded;

            /*
            * Some decoders will consume data in a packet, but will not be able to construct
            * a full video picture yet.  Therefore you should always check if you
            * got a complete picture from the decoder
            */
            if (picture.isComplete())
            {
                IVideoPicture newPic = picture;
                /*
                * If the resampler is not null, that means we didn't get the
                * video in BGR24 format and
                * need to convert it into BGR24 format.
                */
                if (resampler != null)
                {
                // we must resample
                newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
                    picture.getWidth(), picture.getHeight());
                if (resampler.resample(newPic, picture) < 0)
                    throw new RuntimeException("could not resample video from: "
                        + filename);
                }
                if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
                throw new RuntimeException("could not decode video" +
                            " as BGR 24 bit data in: " + filename);

                /**
                * We could just display the images as quickly as we decode them,
                * but it turns out we can decode a lot faster than you think.
                * 
                * So instead, the following code does a poor-man's version of
                * trying to match up the frame-rate requested for each
                * IVideoPicture with the system clock time on your computer.
                * 
                * Remember that all Xuggler IAudioSamples and IVideoPicture objects
                * always give timestamps in Microseconds, relative to the first
                * decoded item. If instead you used the packet timestamps, they can
                * be in different units depending on your IContainer, and IStream
                * and things can get hairy quickly.
                */
                if (firstTimestampInStream == Global.NO_PTS)
                {
                // This is our first time through
                firstTimestampInStream = picture.getTimeStamp();
                // get the starting clock time so we can hold up frames
                // until the right time.
                systemClockStartTime = System.currentTimeMillis();
                } else {
                long systemClockCurrentTime = System.currentTimeMillis();
                long millisecondsClockTimeSinceStartofVideo =
                    systemClockCurrentTime - systemClockStartTime;
                // compute how long for this frame since the first frame in the
                // stream.
                // remember that IVideoPicture and IAudioSamples timestamps are
                // always in MICROSECONDS,
                // so we divide by 1000 to get milliseconds.
                long millisecondsStreamTimeSinceStartOfVideo =
                    (picture.getTimeStamp() - firstTimestampInStream)/1000;
                final long millisecondsTolerance = 50; // and we give ourselfs 50 ms of tolerance
                final long millisecondsToSleep = 
                    (millisecondsStreamTimeSinceStartOfVideo -
                    (millisecondsClockTimeSinceStartofVideo +
                        millisecondsTolerance));
                if (millisecondsToSleep > 0)
                {
                    try
                    {
                    Thread.sleep(millisecondsToSleep);
                    }
                    catch (InterruptedException e)
                    {
                        java.lang.System.out.println("InterruptedException!");
                    // we might get this when the user closes the dialog box, so
                    // just return from the method.
                    return;
                    }
                }
                }

                
                // And finally, convert the BGR24 to an Java buffered image
                BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                
//java.lang.System.out.println(""+javaImage.getWidth()+" "+javaImage.getHeight());
                // and display it on the Java Swing window
                //updateJavaWindow(javaImage);
                panel.setImage(javaImage);
                //generated.setImage(javaImage);
                //frame.repaint();
                //frame.pack();
                
            }
            }
        }
        else
        {
            /*
            * This packet isn't part of our video stream, so we just
            * silently drop it.
            */
            do {} while(false);
        }

        }
        /*
        * Technically since we're exiting anyway, these will be cleaned up by 
        * the garbage collector... but because we're nice people and want
        * to be invited places for Christmas, we're going to show how to clean up.
        */
        if (videoCoder != null)
        {
        videoCoder.close();
        videoCoder = null;
        }
        if (container !=null)
        {
        container.close();
        container = null;
        }
        //closeJavaWindow();
        frame.setActive();
        
        stop = true;
    }
    
        boolean fonmodel = false;
        boolean porogBit = false;
        boolean porogColor = false;
        boolean porogGrey = false;
        boolean porogColorSubstr = false;
        boolean smenFon = false;
        boolean itog = false;
        boolean noBorderFigures = false;
        double smenFonLim = 2;
        
    private long imgNumber = 0;
    
    int width = 0;
    int height = 0;
    
    int distSkle = 0;
    
    public void processview(VideoPanel panel, VideoPanel output, MainFrame frame, int each, int framesCount, int colorlimit) {
        stop = false;
        
        frame.setPassive();
        
        fonmodel = frame.isSelectedRButtonFonModel();
        porogBit = frame.isSelectedRButtonPorogBit();
        porogColor = frame.isSelectedRButtonPorogColor();
        porogGrey = frame.isSelectedRButtonPorogGrey();
        porogColorSubstr = frame.isSelectedRButtonPorogColorSubstr();
        smenFon = frame.isSelectedSmenFon();
        itog = frame.isSelectedRButtonItog();
        noBorderFigures = frame.isSelectedNoBorderFigures();
        
        distSkle = frame.getDistSkleValue();
        
        smenFonLim = 2;
        try {
            smenFonLim = frame.getSmenFonLimValue();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(),
                                "Ошибка ввода", JOptionPane.ERROR_MESSAGE); 
            frame.getSmenFonLim().setText(""+smenFonLim);
        }
        
        int type = 0;
        if (porogBit)
            type = 1;
        if (porogColor)
            type = 2;
        if (porogGrey)
            type = 3;
        if (porogColorSubstr)
            type = 4;
        if (itog)
            type = 5;
        
        if (this.file == null) {
                throw new RuntimeException("Проблемы с файлом!");
            }
        
        imgNumber = 0;
        images = new ArrayList<BufferedImage>();
        long pre,post;
        rgb = null;
        
        if (!IVideoResampler.isSupported(
            IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
        throw new RuntimeException("you must install the GPL version" +
      		" of Xuggler (with IVideoResampler support) for " +
      		"this demo to work");

        IContainer container = IContainer.make();

        String fullfilename = file.getPath();
        String filename = file.getName();
        
        if (container.open(fullfilename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: \n\n"
                    + filename);
        }

        int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;

        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        if (videoStreamId == -1) {
            throw new RuntimeException("could not find video stream in container: \n\n"
                    + filename);
        }

        if (videoCoder.open() < 0) {
            throw new RuntimeException(
                    "could not open video decoder for container: \n\n" + filename);
        }

        IVideoResampler resampler = null;
        if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
            resampler = IVideoResampler.make(videoCoder.getWidth(), 
                videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
            if (resampler == null)
                throw new RuntimeException("could not create color space " +
                                "resampler for: " + filename);
        }

        Graphics gPanel = panel.getGraphics();
        Graphics gOutput = output.getGraphics();
        
        
        IPacket packet = IPacket.make();
        long firstTimestampInStream = Global.NO_PTS;
        long systemClockStartTime = 0;
        while (container.readNextPacket(packet) >= 0) {

            if (packet.getStreamIndex() == videoStreamId) {
                IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                        videoCoder.getWidth(), videoCoder.getHeight());

                int offset = 0;
                while (offset < packet.getSize()) {
                    if (stop) {
                        break;
                    }
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
                    if (bytesDecoded < 0) {
                        throw new RuntimeException("got error decoding video in: "
                                + filename);
                    }
                    offset += bytesDecoded;

                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        if (resampler != null) {
                            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
                                    picture.getWidth(), picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException("could not resample video from: "
                                        + filename);
                            }
                        }
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException("could not decode video"
                                    + " as BGR 24 bit data in: " + filename);
                        }

                        if (firstTimestampInStream == Global.NO_PTS) {
                            firstTimestampInStream = picture.getTimeStamp();
                            systemClockStartTime = System.currentTimeMillis();
                        } else {
                            long systemClockCurrentTime = System.currentTimeMillis();
                            long millisecondsClockTimeSinceStartofVideo =
                                    systemClockCurrentTime - systemClockStartTime;
                            long millisecondsStreamTimeSinceStartOfVideo =
                                    (picture.getTimeStamp() - firstTimestampInStream) / 1000;
                            final long millisecondsTolerance = 50;
                            final long millisecondsToSleep =
                                    (millisecondsStreamTimeSinceStartOfVideo
                                    - (millisecondsClockTimeSinceStartofVideo
                                    + millisecondsTolerance));
                            if (millisecondsToSleep > 0) {
                                try {
                                    Thread.sleep(millisecondsToSleep);
                                } catch (InterruptedException e) {
                                    java.lang.System.out.println("InterruptedException2");
                                    return;
                                }
                            }
                        }
                        BufferedImage javaImage = Utils.videoPictureToImage(newPic);

                        width = javaImage.getWidth();
                        height = javaImage.getHeight();
                        
                        if (imgNumber==0) {
                            panel.setSize(javaImage);
                            output.setSize(javaImage);
                        }
                        
                        //System.out.println("\n width = "+javaImage.getWidth()+" & height = "+javaImage.getHeight()+"\n");

                        panel.setImage(gPanel, javaImage);
                        
                        ////pre = System.currentTimeMillis();

                        rgbpix = new int[javaImage.getWidth()*javaImage.getHeight()*3];
                        
                        getProcessedResult(javaImage, each, framesCount, colorlimit, smenFon, smenFonLim);

                        try {

                            if (type>0) {
                                getCutFon(javaImage, false, colorlimit, type, smenFon, smenFonLim);
                                    
                                if (itog)
                                    getFinalImage(javaImage);
                            }
                            //processedResult = getShineFon(javaImage, processedResult, false, colorlimit);
                            
                            
                            //Arrays.fill(((DataBufferInt)javaImage.getData().getDataBuffer()).getData(),color);    
                            //output.setImage(gOutput, processedResult);
                        } catch (RuntimeException e) {
                            if (!e.getMessage().equals("forgetOldBackground"))
                                throw e;
                        }
                      
                        BufferedImage res = new BufferedImage(width,
                            height, BufferedImage.TYPE_INT_RGB);
                        
                        int[] imagePixelData = ((DataBufferInt)res.getRaster().getDataBuffer()).getData();
                        for (int i=0; i<width; i++)
                            for (int j=0; j<height; j++)
                                imagePixelData[j*width+i] = rgbpix[(i*height+j)*3]<<16 | rgbpix[(i*height+j)*3+1] <<8 | rgbpix[(i*height+j)*3+2];
                        
                        output.setImage(gOutput, res);
                        
                        ////post = System.currentTimeMillis();
                        ////java.lang.System.out.println(post-pre);
                    }
                }
            } else {
                do {
                } while (false);
            }

        }

        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }
        frame.setActive();

        stop = true;
    }

public BufferedImage processFrame(byte[] frame, int width, int height)
{
   BufferedImage currentImage = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
   byte[] imgData = ((DataBufferByte)currentImage.getRaster().getDataBuffer()).getData();
   System.arraycopy(frame,0,imgData,0,frame.length);
   return currentImage;
}

public BufferedImage processFrame(BufferedImage image, byte[] frame, int width, int height)
{
   byte[] imgData = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
   System.arraycopy(frame,0,imgData,0,frame.length);
   return image;
}

    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    
    public static Color getColor(BufferedImage img, int x, int y) {

      Raster raster = img.getRaster();
      ColorModel model = img.getColorModel();
      Object data = raster.getDataElements(x, y, null);
      int argb = model.getRGB(data);
      
      return new Color(argb, true);
    }
    
    public static Object[] getAllRasterAndColorModel(BufferedImage img) {

      Raster raster = img.getRaster();
      ColorModel model = img.getColorModel();
      return new Object[] {raster, model};
    }

    public static Color getColorFromRaster(Raster raster, ColorModel model, int x, int y) {

      Object data = raster.getDataElements(x, y, null);
      int argb = model.getRGB(data);
      return new Color(argb, true);
    }
    
    private long[][][] rgb = null;
    private int[] rgbpix = null;
    
    
    
    
    public void getProcessedResult(BufferedImage image, int each, int framesCount, int limit, boolean smenFon, double smenFonLim) {
      
        Object[] rasterNmodel = getAllRasterAndColorModel(image);
        
        Raster raster = (Raster)rasterNmodel[0];
        ColorModel model = (ColorModel)rasterNmodel[1];
        
        if (rgb==null) {
            rgb = new long[width][height][3];
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++)
                    for (int k=0; k<3; k++)
                        rgb[i][j][k]=0;
        }

        if (imgNumber%each==0) {
            if (images.size()<framesCount) {
                for (int i=0; i<width; i++)
                    for (int j=0; j<height; j++) {
                        Color color = getColorFromRaster(raster, model, i, j);//getColor(image,i,j);
                        rgb[i][j][0] += color.getRed();
                        rgb[i][j][1] += color.getGreen();
                        rgb[i][j][2] += color.getBlue();
                    }
                images.add(image);
            } else {
                BufferedImage oldImage = images.get((int)((imgNumber/each)%framesCount));
                Object[] oldrasterNmodel = getAllRasterAndColorModel(oldImage);

                Raster oldraster = (Raster)oldrasterNmodel[0];
                ColorModel oldmodel = (ColorModel)oldrasterNmodel[1];                
                for (int i=0; i<width; i++)
                    for (int j=0; j<height; j++) {
                        Color color = getColorFromRaster(oldraster, oldmodel, i, j);
                        rgb[i][j][0] -= color.getRed();
                        rgb[i][j][1] -= color.getGreen();
                        rgb[i][j][2] -= color.getBlue();
                        //System.out.println("colorr "+color.getRed());
                    }
                for (int i=0; i<width; i++)
                    for (int j=0; j<height; j++) {
                        Color color = getColorFromRaster(raster, model, i, j);
                        rgb[i][j][0] += color.getRed();
                        rgb[i][j][1] += color.getGreen();
                        rgb[i][j][2] += color.getBlue();
                    }
                images.set((int)((imgNumber/each)%framesCount), image);     
            }
        }
        imgNumber++;
      
        if (images.isEmpty()) {
            int size = width*height*3;
            for (int i=0; i<size; i++)
                rgbpix[i]=0;
                
        }
        int N = images.size();

        //long pre = System.currentTimeMillis();
        int r, g, b, or, og, ob, bpix=0, pos;
        
        ////long pre = System.currentTimeMillis();
        
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++) {
                r = (int)(rgb[i][j][0]/N);
                g = (int)(rgb[i][j][1]/N);
                b = (int)(rgb[i][j][2]/N);

                pos = (i*height+j)*3;
                
                rgbpix[pos]   = r;
                rgbpix[pos+1] = g;
                rgbpix[pos+2] = b;

                Color oldCol = getColorFromRaster(raster, model, i, j);
                or = oldCol.getRed();
                og = oldCol.getGreen();
                ob = oldCol.getBlue();
                if (Math.abs(r-or)+Math.abs(g-og)+Math.abs(b-ob)<limit)
                    bpix++;
            }
      ////long post = System.currentTimeMillis();
      ////java.lang.System.out.println(post-pre);

        if (smenFon) {
            int all = width*height;
            if (all/(bpix+1)>smenFonLim) {
                    images = new ArrayList<BufferedImage>();
                    rgb = null;
            }
        }
      ////long post = System.currentTimeMillis();
      ////java.lang.System.out.println(post-pre);
    }
    
    
    public void getCutFon(BufferedImage original, boolean simple, int limit, int typePorog, boolean smenFon, double smenFonLim) {

        int[][] bitmap = null;

        if (typePorog==1||typePorog==5)
            bitmap = new int[width][height];
        
        int pos, cCutR, cCutG, cCutB, cOrigR, cOrigG, cOrigB;
        
        if (simple) {
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    Color cOrig = getColor(original, i, j);
                    //Color cCut = getColor(cutter, i, j);
                    pos = (i*height+j)*3;
                    cCutR = rgbpix[pos];
                    cCutG = rgbpix[pos+1];
                    cCutB = rgbpix[pos+2];
                    int r = Math.abs(cOrig.getRed()-cCutR);
                    int g = Math.abs(cOrig.getGreen()-cCutG);
                    int b = Math.abs(cOrig.getBlue()-cCutB);
                    rgbpix[pos] = r;
                    rgbpix[pos+1] = g;
                    rgbpix[pos+2] = b;
                }
        } else {
            //int limit = 80;
            int pixcount = width*height;
            int backgrpixcount = 0;
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    pos = (i*height+j)*3;
                    Color cOrig = getColor(original, i, j);
                    //Color cCut = getColor(cutter, i, j);
                    cOrigR = cOrig.getRed();
                    cOrigG = cOrig.getGreen();
                    cOrigB = cOrig.getBlue();
                    cCutR = rgbpix[pos];
                    cCutG = rgbpix[pos+1];
                    cCutB = rgbpix[pos+2];
                    int value = Math.abs(cOrigR-cCutR)+Math.abs(cOrigG-cCutG)+Math.abs(cOrigB-cCutB);
                    value = colorToBin(value, limit);
                    
                    //if (toBit) {
                    if (typePorog==1||typePorog==5) {
                        if (value==255) {
                            backgrpixcount++;
                            bitmap[i][j]=-1;
                        } else bitmap[i][j]=1;
                        rgbpix[pos]   = value;
                        rgbpix[pos+1] = value;
                        rgbpix[pos+2] = value;
                        //g2.setColor(new Color(value,value,value));   
                    } else if (typePorog==2) {
                        //System.out.println("qwe");
                        if (value==255) {
                            backgrpixcount++;
                            rgbpix[pos]   = value;
                            rgbpix[pos+1] = value;
                            rgbpix[pos+2] = value;
                            //g2.setColor(new Color(value,value,value));
                        } else {
                            rgbpix[pos]   = cOrigR;
                            rgbpix[pos+1] = cOrigG;
                            rgbpix[pos+2] = cOrigB;
                            //g2.setColor(cOrig);
                        }
                    } else if (typePorog==3) {
                        if (value==255) {
                            backgrpixcount++;
                            rgbpix[pos]   = value;
                            rgbpix[pos+1] = value;
                            rgbpix[pos+2] = value;
                            //g2.setColor(new Color(value,value,value));
                        } else {
                            int grey = (int)(cOrig.getRed()*0.299 + cOrig.getGreen()*0.587 + cOrig.getBlue()*0.114);
                            //Color grey = new Color()
                            rgbpix[pos]   = grey;
                            rgbpix[pos+1] = grey;
                            rgbpix[pos+2] = grey;
                            //g2.setColor(new Color(grey,grey,grey));
                        }
                    } else if (typePorog==4) {
                        if (value==255) {
                            backgrpixcount++;
                            rgbpix[pos]   = value;
                            rgbpix[pos+1] = value;
                            rgbpix[pos+2] = value;
                            //g2.setColor(new Color(value,value,value));
                        } else {
                            rgbpix[pos]   = Math.abs(cOrigR-cCutR);
                            rgbpix[pos+1] = Math.abs(cOrigG-cCutG);
                            rgbpix[pos+2] = Math.abs(cOrigB-cCutB);
                            //g2.setColor(new Color(Math.abs(cOrig.getRed()-cCut.getRed()),Math.abs(cOrig.getGreen()-cCut.getGreen()),Math.abs(cOrig.getBlue()-cCut.getBlue())));
                        }
                    }
                    
                    //g2.drawLine(i, j, i, j);   
                }
            
            if (smenFon)
                if (pixcount/(backgrpixcount+1)>smenFonLim) {//3) {
                    images = new ArrayList<BufferedImage>();
                    //images.add(original);
                    rgb = null;
                    //getProcessedResult(original, 1, 10);
                    //res = 
                    ////////////throw new RuntimeException("forgetOldBackground");
                }
        }
        
        if (net!=null && typePorog==5) {

            try {
                bitmap = binfilter(bitmap);
                //raskras = null;
                raskras = identify(bitmap);
                //System.out.println("hopfielter!");
                
                raskras = hopfielter(width, height);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }
    
    public void getShineFon(BufferedImage original, BufferedImage cutter, boolean simple, int limit) {
        
        int pos;
        
        if (simple) {
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    pos = (i*height+j)*3;
                    
                    Color cOrig = getColor(original, i, j);
                    Color cCut = getColor(cutter, i, j);
                    int r = Math.abs(cOrig.getRed()-cCut.getRed());
                    int g = Math.abs(cOrig.getGreen()-cCut.getGreen());
                    int b = Math.abs(cOrig.getBlue()-cCut.getBlue());
                    rgbpix[pos]   = r;
                    rgbpix[pos+1] = g;
                    rgbpix[pos+2] = b;
                    //g2.setColor(new Color(r,g,b));
                    //g2.drawLine(i, j, i, j);                
                }
        } else {
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    pos = (i*height+j)*3;
                    
                    Color cOrig = getColor(original, i, j);
                    Color cCut = getColor(cutter, i, j);
                    int value = Math.abs(cOrig.getRed()-cCut.getRed())+Math.abs(cOrig.getGreen()-cCut.getGreen())+Math.abs(cOrig.getBlue()-cCut.getBlue());
                    value = colorToBin(value, limit);

                    if (value==255) {
                        rgbpix[pos]   = cOrig.getRed();
                        rgbpix[pos+1] = cOrig.getGreen();
                        rgbpix[pos+2] = cOrig.getBlue();
                        //g2.setColor(new Color(cOrig.getRed(),cOrig.getGreen(),cOrig.getBlue()));  
                    }
                    else {
                        Color shining = cOrig.brighter();
                        
                        //g2.setColor(new Color(Math.min((int)(shining.getRed()*1.2),255), Math.min((int)(shining.getGreen()*1.0),255), Math.min((int)(shining.getBlue()*0.7),255)));
                        
                        rgbpix[pos]   = Math.min((int)(shining.getRed()*1.2),255);
                        rgbpix[pos+1] = Math.min((int)(shining.getGreen()*1.0),255);
                        rgbpix[pos+2] = Math.min((int)(shining.getBlue()*0.7),255);
                        
                        //g2.setColor(new Color(cOrig.getRed(),cOrig.getGreen(),cOrig.getBlue()/2));   
                    }
                    //g2.drawLine(i, j, i, j);   
                }
        }
    }
    
    private int colorToBin(int value, int limit) {
        if (value<limit)
            return 255;
        else
            return 0;
    }
    
    public int[][] binfilter(int[][] pixs) {
        int width = pixs.length;
        if (width == 0)
            throw new RuntimeException("binfilter width==0");
        int height = pixs[0].length;
        int[][] t1 = new int[width][height];
        int[][] t2 = new int[width][height];
        boolean first = true;
        boolean changed = true;
        int sum = 0;
        
        boolean collision = false;
        
        for (int t=0; t<width; t++)
            System.arraycopy(pixs[t], 0, t1[t], 0, height);
        
        int a=0;
        
        int[][] sums = new int[width][height];
        
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++) {
                sum = 0;
                for (int q=-1; q<2; q++)
                    for (int w=-1; w<2; w++)
                        try {
                            sum += pixs[i+q][j+w];
                        } catch (Exception e) {}
                sums[i][j] = sum;
            }
        
        while (changed) {
            changed = false;
            a++;
            if (a>20)
                collision = true;
            if (collision==true)
                for (int i=0; i<width; i++)
                    for (int j=0; j<height; j++) {
                        sums[i][j] += t1[i][j];
                    }
            
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    if (sums[i][j]>0)
                        if (t1[i][j]<0) {
                            t1[i][j] = 1;
                            changed = true;
                        }
                    if (sums[i][j]<0)
                        if (t1[i][j]>0) {
                            t1[i][j] = -1;
                            changed = true;
                        }
                }
            if (changed==false)
                return t1;
            
        }
        return pixs;
    }
    
    private int[][] raskras = null;

    //ArrayList<int[][]> objects = null;
    
    private int raskrasId = 0;
    //private ArrayList<Integer> raskrasIds = new ArrayList<Integer>();
    
    private ArrayList<RaskrasObject> raskrobjs = new ArrayList<RaskrasObject>();

    public int[][] identify(int[][] pixs) {
        int width = pixs.length;
        if (width == 0)
            throw new RuntimeException("binfilter width==0");
        int height = pixs[0].length;
        
        //System.out.println("\n"+width+" wh "+height+"\n");
        
        raskras = new int[width][height];
        raskrobjs = new ArrayList<RaskrasObject>();
        //objects = new ArrayList<int[][]>();

        raskrasId = 1;
        
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++)
                raskras[i][j] = pixs[i][j]<0?0:1;
        
        for (int j=0; j<height; j++)
            for (int i=0; i<width; i++)
                ident_rekurs_start(i, j, width, height);
        
        
        /* The Most Important */
        return raskras;
    }
    
    private void ident_rekurs_start(int x, int y, int width, int height) {
        if (raskras[x][y]==1) {
            raskrasId++;
            //raskrasIds.add(raskrasId);
            RaskrasObject raskrobj = new RaskrasObject();
            raskrobj.left = x;
            raskrobj.top = y;
            raskrobj.width = 1;
            raskrobj.height = 1;
            raskrobjs.add(raskrobj);
            
            ident_rekurs(x, y, width, height);
            
            int nd = distSkle;//5;
            
            try {
                int shift = raskrasId-2;
                RaskrasObject newr = raskrobjs.get(shift);
                /*for (int s=0; s<shift; s++) {
                    RaskrasObject oldr = raskrobjs.get(s);
                    if ((newr.left>oldr.getRight()||newr.getRight()<oldr.left) && (newr.top>oldr.getBottom()||newr.getBottom()<oldr.top)) {
                    } else {
                        //System.out.println("intersection");
                        for (int i=newr.left; i<newr.width; i++)
                            for (int j=newr.top; j<newr.height; j++) {
                                if (raskras[i][j]==shift)
                                    raskras[i][j] = s;
                            }

                        oldr.width = Math.max(Math.max(oldr.width, newr.width), Math.max(oldr.getRight()-newr.left, newr.getRight()-oldr.left));
                        oldr.height = Math.max(Math.max(oldr.height, newr.height), Math.max(oldr.getBottom()-newr.top, newr.getBottom()-oldr.top));

                        oldr.left = Math.min(oldr.left, newr.left);
                        oldr.top = Math.min(oldr.top, newr.top);

                        raskrasId--;
                        raskrobjs.remove(shift);
                        break;
                    }
                }*/
                
                for (int s=0; s<shift; s++) {
                    RaskrasObject oldr = raskrobjs.get(s);
                    int oldId = s+2;
                    if ((newr.left>(oldr.getRight()+nd)||newr.getRight()<(oldr.left-nd)) && (newr.top>(oldr.getBottom()+nd)||newr.getBottom()<(oldr.top-nd))) {
                    } else {
                        //System.out.println("intersection");
                        for (int i=oldr.left; i<oldr.getRight(); i++)
                            for (int j=oldr.top; j<oldr.getBottom(); j++) {
                                if (raskras[i][j]==oldId)
                                    raskras[i][j] = raskrasId;
                            }

                        newr.width = Math.max(Math.max(oldr.width, newr.width), Math.max(oldr.getRight()-newr.left, newr.getRight()-oldr.left));
                        newr.height = Math.max(Math.max(oldr.height, newr.height), Math.max(oldr.getBottom()-newr.top, newr.getBottom()-oldr.top));

                        newr.left = Math.min(oldr.left, newr.left);
                        newr.top = Math.min(oldr.top, newr.top);

                        //raskrasId--;
                        //raskrobjs.remove(shift);
                        
                        oldr.width = 0;
                        oldr.height = 0;
                    }
                }
                
            } catch (Exception e) {
                System.out.println("CATCH");
            }
            
        }
/*        ident_rekurs(x, y, width, height);
        
        try {
            int shift = raskrasId-2;
            RaskrasObject newr = raskrobjs.get(shift);
            for (int i=0; i<shift; i++) {
                RaskrasObject oldr = raskrobjs.get(i);
                if ((newr.left>oldr.getRight()||newr.getRight()<oldr.left) && (newr.top>oldr.getBottom()||newr.getBottom()<oldr.top)) {
                } else {
                    System.out.println("intersection");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }*/
    }
    
    private void ident_rekurs(int x, int y, int width, int height) {

        if (raskras[x][y]>1||raskras[x][y]==0)
            return;
        raskras[x][y] = raskrasId;
        RaskrasObject raskrobj = raskrobjs.get(raskrasId-2);
        
        /*if (x<0)
            System.out.println("x<0 qqq");
        if (y<0)
            System.out.println("y<0 qqq");
        if (x>=width)
            System.out.println("x>=width qqq");
        if (y>=height)
            System.out.println("y>=height qqq");
        */
        if (x>=0&&x<raskrobj.left) {
            raskrobj.left--;
            raskrobj.width++;
            /*if (raskrobj.getRight()>width)
                System.out.println("errrrorrrrWidth1 "+raskrasId);*/

        } else if (x<width&&x>=raskrobj.getRight()) {
            raskrobj.width++;
            /*if (raskrobj.getRight()>width)
                System.out.println("errrrorrrrWidth2 "+raskrasId);*/
        }
        
        if (y>=0&&y<raskrobj.top) {
            raskrobj.top--;
            raskrobj.height++;
            /*if (raskrobj.getBottom()>height)
                System.out.println("errrrorrrrHeight1 "+raskrasId);*/
        } else if (y<height&&y>=raskrobj.getBottom()) {
            raskrobj.height++;
            /*if (raskrobj.getBottom()>height)
                System.out.println("errrrorrrrHeight2 "+raskrasId);*/
        }
       
        if (x>0)
            if (raskras[x-1][y]==1) {
                //System.out.println("\nx-1, y x="+x+" y="+y+"\n");
                ident_rekurs(x-1,y, width, height);
            }
        if (y>0)
            if (raskras[x][y-1]==1) {
                ident_rekurs(x,y-1, width, height);
            }
        if (x>0&&y>0)
            if (raskras[x-1][y-1]==1) {
                ident_rekurs(x-1,y-1, width, height);
            }
        if (x<width-1)
            if (raskras[x+1][y]==1) {
                ident_rekurs(x+1,y, width, height);
            }
        if (y<height-1)
            if (raskras[x][y+1]==1) {
                ident_rekurs(x,y+1, width, height);
            }
        if (x<width-1&&y<height-1)
            if (raskras[x+1][y+1]==1) {
                ident_rekurs(x+1,y+1, width, height);
            }
        if (x>0&&y<height-1)
            if (raskras[x-1][y+1]==1) {
                ident_rekurs(x-1,y+1, width, height);
            }
        if (x<width-1&&y>0)
            if (raskras[x+1][y-1]==1) {
                ident_rekurs(x+1,y-1, width, height);
            }
    }
    
    public void getFinalImage(BufferedImage original) {
        
        if (raskras==null)
            return;
        
        int count = raskrasId-1;
       
        int pos;
        
        //g2.setColor(Color.BLACK);
        
        Object[] rastNmod = getAllRasterAndColorModel(original);
        
        Raster raster = (Raster)rastNmod[0];
        ColorModel cmodel = (ColorModel)rastNmod[1];
        
        
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++) {
                pos = (i*height+j)*3;
                Color color = getColorFromRaster(raster, cmodel, i, j);
                rgbpix[pos]   = color.getRed();
                rgbpix[pos+1] = color.getGreen();
                rgbpix[pos+2] = color.getBlue();
                //g2.setColor(getColorFromRaster(raster, cmodel, i, j));
                //g2.drawLine(i, j, i, j);
            }
        
        //g2.setColor(Color.WHITE);
        
        //System.out.println("\n"+width+" wh "+height+" rosize "+raskrobjs.size()+"\n");
        for (int id = 0; id<count; id++) {
            RaskrasObject raskrobj = raskrobjs.get(id);
            boolean access = true;
            if (noBorderFigures==true) {
                if (raskrobj.left>0&&raskrobj.top>0&&raskrobj.getRight()<width&&raskrobj.getBottom()<height) {} 
                else access = false;
            }
            //if (raskrobj.left>0&&raskrobj.top>0&&raskrobj.getRight()<width&&raskrobj.getBottom()<height) {
            if (access) {
                int objRaskrasId = id+2;
                int randR = (int)(Math.random()*255);
                int randG = (int)(Math.random()*255);
                int randB = (int)(Math.random()*255);
                //g2.setColor(new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)));
                for (int i=raskrobj.left; i<raskrobj.getRight(); i++)
                    for (int j=raskrobj.top; j<raskrobj.getBottom(); j++) {
                        pos = (i*height+j)*3;
                        //System.out.println(i+" "+j);
                    /* if (raskras[i][j]==objRaskrasId)
                            g2.setColor(Color.BLACK);
                        else
                            g2.setColor(Color.WHITE);
                        g2.drawLine(i, j, i, j);*/
                        try {
                            if (raskras[i][j]==objRaskrasId) {
                                //g2.setColor(Color.YELLOW);
                                //g2.setColor(Color.YELLOW);
                                rgbpix[pos]   = randR;
                                rgbpix[pos+1] = randG;
                                rgbpix[pos+2] = randB;
                                //g2.drawLine(i, j, i, j);
                            } /*else {
                                getColor(original, i, j);
                                g2.drawLine(i, j, i, j);
                            }*/
                        } catch (Exception e) {
                            System.out.println("Exce i = "+i+" j = "+j+" width = "+width+" height = "+height);
                            System.out.println(raskrasId+" "+raskrobj.left+" "+raskrobj.getRight()+" "+raskrobj.top+" "+raskrobj.getBottom()+" "+width+" "+height);
                        }
                    }
            }
        }
        //return res;
    }
    
    private int[][] hopfielter (int width, int height) {
        int[][] res = new int[width][height];
        
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++)
                res[i][j] = 0;
        
        ArrayList<int[][]> allrobjpixs = new ArrayList<int[][]>();
        
        //System.out.println("raskrobjs size = "+raskrobjs.size());
        
        
        for (int k=0; k<raskrobjs.size(); k++) {
            RaskrasObject robj = raskrobjs.get(k);
            int[][] robjpixs = new int[robj.width][robj.height];
            for (int i=0, ii = robj.left; i<robj.width; i++, ii++)
                for (int j=0, jj = robj.top; j<robj.height; j++, jj++) {
                    robjpixs[i][j] = raskras[ii][jj]==0?-1:1;
                }

            robjpixs = net.process(robjpixs, robj.width, robj.height);

            allrobjpixs.add(robjpixs);
        }

        //System.out.println("raskrobjs size = "+raskrobjs.size()+" _1");
        
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++) {
                raskras[i][j] = 0;
            }
        
        //System.out.println("raskrobjs size = "+raskrobjs.size()+" _2");
        
        for (int k=0; k<raskrobjs.size(); k++) {
            RaskrasObject robj = raskrobjs.get(k);
            int[][] robjpixs = allrobjpixs.get(k);
            if (robjpixs!=null)
            for (int i=0, ii = robj.left; i<robj.width; i++, ii++)
                for (int j=0, jj = robj.top; j<robj.height; j++, jj++) {
                    raskras[ii][jj] = robjpixs[i][j]==-1?0:(k+2);
                }
        }
        //System.out.println("raskrobjs size = "+raskrobjs.size()+" _3");
        return raskras;
    }
    
    /*
  private static VideoImage mScreen = null;

  private static void updateJavaWindow(BufferedImage javaImage)
  {
      //java.lang.System.out.println("123");
    mScreen.setImage(javaImage);
    //mScreen.paintComponents(mScreen.getGraphics());
  }


  private static void openJavaWindow()
  {
    mScreen = new VideoImage();

        //mScreen.setSize(800, 800);
  }


  private static void closeJavaWindow()
  {
      mScreen.dispose();
    //System.exit(0);
  }
    */
    
/*    public void process() throws IOException {
        if (this.file == null) {
            throw new RuntimeException("Проблемы с файлом!");
        }
        IContainer container = IContainer.make();

        String fullfilename = file.getPath();
        String filename = file.getName();
        
        //File outdir0 = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out");
        //outdir0.mkdir();
        File outdir = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out/"+file.getName().substring(0, filename.indexOf(".")));
        outdir.mkdir();
        
        if (container.open(fullfilename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: \n\n"
                    + filename);
        }

        int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;

        // нужно найти видео поток
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        if (videoStreamId == -1) // кажись не нашли
        {
            throw new RuntimeException("could not find video stream in container: \n\n"
                    + filename);
        }

        // пытаемся открыть кодек
        if (videoCoder.open() < 0) {
            throw new RuntimeException(
                    "could not open video decoder for container: \n\n" + filename);
        }

        IPacket packet = IPacket.make();
        // с 3-ей по 5-ую микросекунду
        long start = 6 * 1000 * 1000;
        long end = 12 * 1000 * 1000;
        // с разницей в 100 милисекунд
        long step = 500 * 1000;

        END:
        while (container.readNextPacket(packet) >= 0) {
            if (packet.getStreamIndex() == videoStreamId) {
                IVideoPicture picture = IVideoPicture.make(
                        videoCoder.getPixelType(), videoCoder.getWidth(),
                        videoCoder.getHeight());
                int offset = 0;
                while (offset < packet.getSize()) {
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet,
                            offset);
                    // Если что-то пошло не так
                    if (bytesDecoded < 0) {
                        throw new RuntimeException("got error decoding video in: "
                                + filename);
                    }
                    offset += bytesDecoded;
                    // В общем случае, нужно будет использовать Resampler. См.
                    // tutorials!
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        // в микросекундах
                        long timestamp = picture.getTimeStamp();
                        if (timestamp > start) {
                            // Получаем стандартный BufferedImage
                            BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                            String fileName = String.format("%07d.png",
                                    timestamp);
                            ImageIO.write(javaImage, "PNG", new File(outdir,
                                    fileName));
                            start += step;
                        }
                        if (timestamp > end) {
                            break END;
                        }
                    }
                }
            }
        }
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }

    }

    public static void maino(String a[]) throws Exception {
        String filename = "D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/in/testfile_h264_mp4a_tmcd.mov";
        File outdir = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out/testfile_h264_mp4a_tmcd");
        IContainer container = IContainer.make();

        if (container.open(filename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: "
                    + filename);
        }
        int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;

        // нужно найти видео поток
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        if (videoStreamId == -1) // кажись не нашли
        {
            throw new RuntimeException("could not find video stream in container: "
                    + filename);
        }

        // пытаемся открыть кодек
        if (videoCoder.open() < 0) {
            throw new RuntimeException(
                    "could not open video decoder for container: " + filename);
        }

        IPacket packet = IPacket.make();
        // с 3-ей по 5-ую микросекунду
        long start = 6 * 1000 * 1000;
        long end = 12 * 1000 * 1000;
        // с разницей в 100 милисекунд
        long step = 500 * 1000;

        END:
        while (container.readNextPacket(packet) >= 0) {
            if (packet.getStreamIndex() == videoStreamId) {
                IVideoPicture picture = IVideoPicture.make(
                        videoCoder.getPixelType(), videoCoder.getWidth(),
                        videoCoder.getHeight());
                int offset = 0;
                while (offset < packet.getSize()) {
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet,
                            offset);
                    // Если что-то пошло не так
                    if (bytesDecoded < 0) {
                        throw new RuntimeException("got error decoding video in: "
                                + filename);
                    }
                    offset += bytesDecoded;
                    // В общем случае, нужно будет использовать Resampler. См.
                    // tutorials!
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        // в микросекундах
                        long timestamp = picture.getTimeStamp();
                        if (timestamp > start) {
                            // Получаем стандартный BufferedImage
                            BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                            String fileName = String.format("%07d.png",
                                    timestamp);
                            ImageIO.write(javaImage, "PNG", new File(outdir,
                                    fileName));
                            start += step;
                        }
                        if (timestamp > end) {
                            break END;
                        }
                    }
                }
            }
        }
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }

    }*/
}