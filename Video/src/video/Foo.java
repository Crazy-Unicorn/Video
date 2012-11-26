/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import com.sun.jndi.toolkit.ctx.HeadTail;
import com.xuggle.xuggler.*;
import com.xuggle.xuggler.demos.VideoImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;

public class Foo {

    private File file = null;

    private boolean stop = true;
    
    public void stop() {
        stop = true;
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
    
    
    private long imgNumber = 0;
    
    public void processview(VideoPanel panel, VideoPanel output, MainFrame frame, int each, int framesCount, int colorlimit) {
        stop = false;
        
        frame.setPassive();
        
        boolean fonmodel = frame.isSelectedRButtonFonModel();
        boolean porogBit = frame.isSelectedRButtonPorogBit();
        boolean porogColor = frame.isSelectedRButtonPorogColor();
        boolean porogGrey = frame.isSelectedRButtonPorogGrey();
        boolean smenFon = frame.isSelectedSmenFon();
        
        int type = 0;
        if (porogBit)
            type = 1;
        if (porogColor)
            type = 2;
        if (porogGrey)
            type = 3;
        //java.lang.System.out.println(smenFon);
        
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
        
        //File outdir = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out/"+file.getName().substring(0, filename.indexOf(".")));
        //outdir.mkdir();
        
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
                                    return;
                                }
                            }
                        }
                        BufferedImage javaImage = Utils.videoPictureToImage(newPic);

                        if (imgNumber==0) {
                            panel.setSize(javaImage);
                            output.setSize(javaImage);
                        }
                        
                        
                        
                        ///pre = System.currentTimeMillis();
                        panel.setImage(gPanel, javaImage);
                        ///post = System.currentTimeMillis();
                        //java.lang.System.out.println((post-pre)+" view");
                        
                        pre = System.currentTimeMillis();

                        
                        /*if (imgNumber%each==0) {
                            if (images.size()<framesCount)
                                images.add(javaImage);
                            else
                                images.set((imgNumber/each)%framesCount, javaImage);
                        }
                        imgNumber++;*/

                        //try {
                            BufferedImage processedResult = getProcessedResult(javaImage, each, framesCount, colorlimit, smenFon);

                            try {
                            //BufferedImage cutFon = getCutFon(javaImage, processedResult, false, colorlimit);
                                //if (porogBit||porogColor)
                                if (type>0)
                                    processedResult = getCutFon(javaImage, processedResult, false, colorlimit, type, smenFon);
                            //processedResult = getShineFon(javaImage, processedResult, false, colorlimit);
                            
                                output.setImage(gOutput, processedResult);//cutFon);
                            //java.lang.System.out.println(images.size()+" Nsize");
                            } catch (RuntimeException e) {
                                if (!e.getMessage().equals("forgetOldBackground"))
                                    throw e;
                            }
                        /*} catch (RuntimeException e) {
                            if (!e.getMessage().equals("empty")) {
                                java.lang.System.out.println("err!");
                            }
                        }*/
                        post = System.currentTimeMillis();
                        java.lang.System.out.println(post-pre);
                        //java.lang.System.out.println((post-pre)+" processed");
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
    
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    
    public Color getColor(BufferedImage img, int x, int y) {

      Raster raster = img.getRaster();
      ColorModel model = img.getColorModel();
      Object data = raster.getDataElements(x, y, null);
      int argb = model.getRGB(data);
      
      return new Color(argb, true);
    }
    
    private long[][][] rgb = null;
    
    public BufferedImage getProcessedResult(BufferedImage image, int each, int framesCount, int limit, boolean smenFon) {

        int width = image.getWidth();
        int height = image.getHeight();
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
                        Color color = getColor(image,i,j);
                        rgb[i][j][0] += color.getRed();
                        rgb[i][j][1] += color.getGreen();
                        rgb[i][j][2] += color.getBlue();
                    }
                images.add(image);
                //java.lang.System.out.println("add, "+images.size());
            } else {
                //java.lang.System.out.println("Nsize = "+images.size()+"  UPDpos = "+((int)((imgNumber/each)%framesCount)));
                BufferedImage oldImage = images.get((int)((imgNumber/each)%framesCount));
                for (int i=0; i<width; i++)
                    for (int j=0; j<height; j++) {
                        Color color = getColor(oldImage,i,j);
                        rgb[i][j][0] -= color.getRed();
                        rgb[i][j][1] -= color.getGreen();
                        rgb[i][j][2] -= color.getBlue();
                    }
                for (int i=0; i<width; i++)
                    for (int j=0; j<height; j++) {
                        Color color = getColor(image,i,j);
                        rgb[i][j][0] += color.getRed();
                        rgb[i][j][1] += color.getGreen();
                        rgb[i][j][2] += color.getBlue();
                    }
                images.set((int)((imgNumber/each)%framesCount), image);     
                //java.lang.System.out.println("set "+(int)((imgNumber/each)%framesCount));
            }
        }
        imgNumber++;
        
        if (images.isEmpty())
            return new BufferedImage(width,
            height, BufferedImage.TYPE_INT_ARGB);
            //throw new RuntimeException("empty");
        

        int N = images.size();
        BufferedImage res = new BufferedImage(width,
            height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        //new Color
        //g2.setColor(Color.red);
        //g2.drawOval(10, 10, 1, 1);
        //java.lang.System.out.println(getColor(res, 1, 1).getBlue());
        /*int i,j,r,g,b,n;
        //Color color;
              
      //long pre = System.currentTimeMillis();

        for (i=0; i<width; i++)
            for (j=0; j<height; j++) {
                r = 0;
                g = 0;
                b = 0;
                for (n=0; n<N; n++) {
                    Color color = getColor(images.get(n), i, j);
                    r += color.getRed();
                    g += color.getGreen();
                    b += color.getBlue();
                }
                r = r/N;
                g = g/N;
                b = b/N;
                g2.setColor(new Color(r,g,b));
                g2.drawLine(i, j, i, j);
            }*/
        //long pre = System.currentTimeMillis();
        int r=0, g=0, b=0;
        
    
        int or=0, og=0, ob=0;
        int bpix = 0;
                
        //try {
        for (int i=0; i<width; i++)
            for (int j=0; j<height; j++) {
                r = (int)(rgb[i][j][0]/N);
                g = (int)(rgb[i][j][1]/N);
                b = (int)(rgb[i][j][2]/N);
                
                g2.setColor(new Color(r,g,b));
                g2.drawLine(i, j, i, j);
                
                Color oldCol = getColor(image, i, j);
                or = oldCol.getRed();
                og = oldCol.getGreen();
                ob = oldCol.getBlue();
                if (Math.abs(r-or)+Math.abs(g-og)+Math.abs(b-ob)<limit)
                    bpix++;
                
            }
        if (smenFon) {
            int all = width*height;
            if (all/(bpix+1)>3) {
                //java.lang.System.out.println(bpix+" "+all+" смена");
                    images = new ArrayList<BufferedImage>();
                    rgb = null;
            }
        }
        //} catch (Exception e) {
        //    java.lang.System.out.println(r+" red "+g+" green "+b+" blue "+N+" N");
        //}
      //long post = System.currentTimeMillis();
      //java.lang.System.out.println(post-pre);
        return res;
    }
    
    
    /*public BufferedImage getProcessedResult(BufferedImage image) {
        //java.lang.System.out.println(images.size());
        int width = image.getWidth();
        int height = image.getHeight();
        if (images.isEmpty())
            return new BufferedImage(width,
            height, BufferedImage.TYPE_INT_ARGB);
        
        //int width = images.get(0).getWidth();
        //int height = images.get(0).getHeight();
        int N = images.size();
        BufferedImage res = new BufferedImage(width,
            height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = res.createGraphics();
        //new Color
        //g2.setColor(Color.red);
        //g2.drawOval(10, 10, 1, 1);
        //java.lang.System.out.println(getColor(res, 1, 1).getBlue());
        int i,j,r,g,b,n;
        //Color color;
              
      //long pre = System.currentTimeMillis();

        for (i=0; i<width; i++)
            for (j=0; j<height; j++) {
                r = 0;
                g = 0;
                b = 0;
                for (n=0; n<N; n++) {
                    Color color = getColor(images.get(n), i, j);
                    r += color.getRed();
                    g += color.getGreen();
                    b += color.getBlue();
                }
                r = r/N;
                g = g/N;
                b = b/N;
                g2.setColor(new Color(r,g,b));
                g2.drawLine(i, j, i, j);
            }
      //long post = System.currentTimeMillis();
      //java.lang.System.out.println(post-pre);
        return res;
    }*/
    
    public BufferedImage getCutFon(BufferedImage original, BufferedImage cutter, boolean simple, int limit, /*boolean toBit,*/ int typePorog, boolean smenFon) {
  
        int width = original.getWidth();
        int height = original.getHeight();
        
        BufferedImage res = new BufferedImage(width,
            height, BufferedImage.TYPE_INT_ARGB);
        
        //#
        if (cutter==null)
            return res;
        //#
        
        Graphics2D g2 = res.createGraphics();
        
        if (simple) {
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    Color cOrig = getColor(original, i, j);
                    Color cCut = getColor(cutter, i, j);
                    int r = Math.abs(cOrig.getRed()-cCut.getRed());
                    int g = Math.abs(cOrig.getGreen()-cCut.getGreen());
                    int b = Math.abs(cOrig.getBlue()-cCut.getBlue());
                    g2.setColor(new Color(r,g,b));
                    g2.drawLine(i, j, i, j);                
                }
        } else {
            //int limit = 80;
            int pixcount = width*height;
            int backgrpixcount = 0;
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    Color cOrig = getColor(original, i, j);
                    Color cCut = getColor(cutter, i, j);
                    int value = Math.abs(cOrig.getRed()-cCut.getRed())+Math.abs(cOrig.getGreen()-cCut.getGreen())+Math.abs(cOrig.getBlue()-cCut.getBlue());
                    value = colorToBin(value, limit);
                    
                    //if (toBit) {
                    if (typePorog==1) {
                        if (value==255)
                            backgrpixcount++;
                        g2.setColor(new Color(value,value,value));   
                    } else if (typePorog==2) {
                        if (value==255) {
                            backgrpixcount++;
                            g2.setColor(new Color(value,value,value));
                        } else
                            g2.setColor(new Color(cOrig.getRed(),cOrig.getGreen(),cOrig.getBlue()));
                    } else if (typePorog==3) {
                        if (value==255) {
                            backgrpixcount++;
                            g2.setColor(new Color(value,value,value));
                        } else {
                            int grey = (int)(cOrig.getRed()*0.299 + cOrig.getGreen()*0.587 + cOrig.getBlue()*0.114);
                            //Color grey = new Color()
                            g2.setColor(new Color(grey,grey,grey));
                        }
                    }
                    
                    g2.drawLine(i, j, i, j);   
                }
            
            if (smenFon)
                if (pixcount/(backgrpixcount+1)>3) {
                    images = new ArrayList<BufferedImage>();
                    //images.add(original);
                    rgb = null;
                    //getProcessedResult(original, 1, 10);
                    //res = 
                    throw new RuntimeException("forgetOldBackground");
                }
        }
        return res;
    }
    
    public BufferedImage getShineFon(BufferedImage original, BufferedImage cutter, boolean simple, int limit) {
        int width = original.getWidth();
        int height = original.getHeight();
        
        BufferedImage res = new BufferedImage(width,
            height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = res.createGraphics();
        
        if (simple) {
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    Color cOrig = getColor(original, i, j);
                    Color cCut = getColor(cutter, i, j);
                    int r = Math.abs(cOrig.getRed()-cCut.getRed());
                    int g = Math.abs(cOrig.getGreen()-cCut.getGreen());
                    int b = Math.abs(cOrig.getBlue()-cCut.getBlue());
                    g2.setColor(new Color(r,g,b));
                    g2.drawLine(i, j, i, j);                
                }
        } else {
            for (int i=0; i<width; i++)
                for (int j=0; j<height; j++) {
                    Color cOrig = getColor(original, i, j);
                    Color cCut = getColor(cutter, i, j);
                    int value = Math.abs(cOrig.getRed()-cCut.getRed())+Math.abs(cOrig.getGreen()-cCut.getGreen())+Math.abs(cOrig.getBlue()-cCut.getBlue());
                    value = colorToBin(value, limit);

                    if (value==255)
                        g2.setColor(new Color(cOrig.getRed(),cOrig.getGreen(),cOrig.getBlue()));   
                    else {
                        Color shining = cOrig.brighter();
                        
                        g2.setColor(new Color(Math.min((int)(shining.getRed()*1.2),255), Math.min((int)(shining.getGreen()*1.0),255), Math.min((int)(shining.getBlue()*0.7),255)));
                        //g2.setColor(new Color(cOrig.getRed(),cOrig.getGreen(),cOrig.getBlue()/2));   
                    }
                    g2.drawLine(i, j, i, j);   
                }
        }
        return res;
    }
    
    private int colorToBin(int value, int limit) {
        if (value<limit)
            return 255;
        else
            return 0;
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