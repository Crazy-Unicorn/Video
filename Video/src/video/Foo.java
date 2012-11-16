/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.Utils;
 
public class Foo {
 
	public static void main(String a[]) throws Exception {
		String filename = "D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/in/testfile_h264_mp4a_tmcd.mov";
		File outdir = new File("D:/Users/goryunov/NetBeans/Video/Video/Video/4testing/out/testfile_h264_mp4a_tmcd");
		IContainer container = IContainer.make();
 
		if (container.open(filename, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("could not open file: "
					+ filename);
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
		if (videoStreamId == -1)
			// кажись не нашли
			throw new RuntimeException("could not find video stream in container: " 
                                         + filename);
 
		// пытаемся открыть кодек
		if (videoCoder.open() < 0)
			throw new RuntimeException(
					"could not open video decoder for container: " + filename);
 
		IPacket packet = IPacket.make();
		// с 3-ей по 5-ую микросекунду
		long start = 6 * 1000 * 1000;
		long end = 12 * 1000 * 1000;
		// с разницей в 100 милисекунд
		long step = 500 * 1000;
 
		END: while (container.readNextPacket(packet) >= 0) {
			if (packet.getStreamIndex() == videoStreamId) {
				IVideoPicture picture = IVideoPicture.make(
						videoCoder.getPixelType(), videoCoder.getWidth(),
						videoCoder.getHeight());
				int offset = 0;
				while (offset < packet.getSize()) {
					int bytesDecoded = videoCoder.decodeVideo(picture, packet,
							offset);
					// Если что-то пошло не так
					if (bytesDecoded < 0)
						throw new RuntimeException("got error decoding video in: "
                                                                                         + filename);
					offset += bytesDecoded;
					// В общем случае, нужно будет использовать Resampler. См.
					// tutorials!
					if (picture.isComplete()) {
						IVideoPicture newPic = picture;
						// в микросекундах
						long timestamp = picture.getTimeStamp();
						if (timestamp > start) {
							// Получаем стандартный BufferedImage
							BufferedImage javaImage = Utils
									.videoPictureToImage(newPic);
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
}