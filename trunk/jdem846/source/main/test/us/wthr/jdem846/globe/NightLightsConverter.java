package us.wthr.jdem846.globe;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.ElevationEstimationDatasetTestMain;
import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

public class NightLightsConverter extends AbstractTestMain
{
private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(NightLightsConverter.class);
		
		//String basePath = "C:\\srv\\elevation\\Earth\\";
		String basePath = "F:\\bluemarble\\";
		
		String imageFilePath = basePath + "nightearth.gif";
		String binaryFilePath = basePath + "nightearth.21601x10801.bin";
		int scaleToWidth = 21601;
		int scaleToHeight = 10801;
		
		//String imageFilePath = basePath + "cloud.W.2001210.21600x21600.png";
		//String binaryFilePath = basePath + "cloud.W.2001210.10800x10800.bin";
		//int scaleToWidth = 10800;
		//int scaleToHeight = 10800;
		
		
		try {
			NightLightsConverter testMain = new NightLightsConverter();
			testMain.process(imageFilePath, binaryFilePath, scaleToWidth, scaleToHeight);
			testMain.validate(binaryFilePath, scaleToWidth, scaleToHeight);
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void validate(String binaryFilePath, int width, int height) throws Exception
	{
		int length = width * height;
		
		
		byte[] buffer = new byte[length];
		
		InputStream in = new BufferedInputStream(new FileInputStream(binaryFilePath));
		
		int bytesRead = in.read(buffer, 0, length);
		
		log.info("Bytes Read: " + bytesRead);
		
		int[] histogram = new int[256];
		
		int min = 999999;
		int max = -999999;
		
		for (int i = 0; i < length; i++) {
			
			int c = (int) (0xFF & buffer[i]);
			
			histogram[c]++;
			
			min = (int) MathExt.min(min, c);
			max = (int) MathExt.max(max, c);
			
		}
		
		for (int i = 0; i < 256; i++) {
			log.info("" + i + " -> " + histogram[i]);
		}
		
		
		log.info("Min: " + min);
		log.info("Max: " + max);
		
		
	}
	
	
	public void process(String imageFilePath, String binaryFilePath, int resizeWidth, int resizeHeight) throws Exception
	{
		
		
		
		InputStream in = new BufferedInputStream(new FileInputStream(imageFilePath));
		
		log.info("Loading...");
		BufferedImage imageFullSize = ImageIO.read(in);
		
		BufferedImage image = null;
		
		if (resizeWidth > 0 && resizeHeight > 0) {
		
			log.info("Resizing...");
			image = ImageUtilities.getScaledInstance(imageFullSize, resizeWidth, resizeHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);

		} else {
			image = imageFullSize;
		}
		
		
		log.info("Width: " + image.getWidth());
		log.info("Height: " + image.getHeight());
		
		Raster raster = image.getRaster();
		
		int[] histogram = new int[256];
		
		Arrays.fill(histogram, 0x0);
		
		int[] rgba = new int[4];
		
		int min = 255; 
		int max = 0;
		
		for (int y = 0; y < image.getHeight(); y++) {
			
			for (int x = 0; x < image.getWidth(); x++) {
				
				raster.getPixel(x, y, rgba);

				min = (int) MathExt.min(min, rgba[0]);
				max = (int) MathExt.max(max, rgba[0]);
				
				
			}
			
		}
		
		log.info("Min: " + min);
		log.info("Max: " + max);
		
		
		log.info("Processing...");
		File binaryFile = new File(binaryFilePath);
		if (binaryFile.exists()) {
			binaryFile.delete();
		}
		
		OutputStream out = new BufferedOutputStream(new FileOutputStream(binaryFile));
		
		long bytesWritten = 0;
		
		for (int y = 0; y < image.getHeight(); y++) {
			
			for (int x = 0; x < image.getWidth(); x++) {
				
				raster.getPixel(x, y, rgba);
				
				int c = rgba[0];
				
				int p = (int) MathExt.round(255 * ((double)c - (double)min) / ((double)max - (double)min));
				
				byte b = (byte) (0xFF & p);
				
				out.write(b);
				bytesWritten++;
				//if (c > min) {
				//	log.info("X/Y: " + x + "/" + y + " -> " + p + " (" + c + ", " + b + ")");
				//}
			}
		}
		
		out.flush();
		out.close();
		
		log.info("Wrote " + bytesWritten + " bytes");
		
		
	}
	
}
