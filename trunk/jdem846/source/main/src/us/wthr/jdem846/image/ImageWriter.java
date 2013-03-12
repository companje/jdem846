package us.wthr.jdem846.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Utility class for saving images to disk.
 * 
 * @author Kevin M. Gill
 *
 */
public class ImageWriter
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ImageWriter.class);

	
	/** Saves image to disk. Attempts to determine the desired output format from the
	 * supplied file name.
	 * 
	 * @param image Image to save to disk.
	 * @param fileName Filesystem location to save the image.
	 * @throws ImageException If the format is invalid or there is a write error.
	 */
	public static void saveImage(BufferedImage image, String fileName) throws ImageException
	{
		
		// If the filename has no extension (e.g. 'fooimage' instead of 'fooimage.jpg')
		if (fileName.indexOf(".") == -1) {
			throw new ImageException("Image type extension missing from file name '" + fileName + "'");
		}
		
		ImageTypeEnum type = ImageTypeEnum.imageTypeFromFileName(fileName);
		if (type != ImageTypeEnum.UNDEFINED_TYPE) {
			ImageWriter.saveImage(image, fileName, type);
		} else {
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			throw new ImageException("Unrecognized and/or unsupported image type extension: " + extension);
		}

		
	}
	
	/** Saves an image to disk. 
	 * 
	 * @param image Image to save to disk.
	 * @param fileName Filesystem location to save image to.
	 * @param format Format number.
	 * @throws ImageException If the format is invalid or there is a write error.
	 */
	public static void saveImage(BufferedImage image, String fileName, ImageTypeEnum format) throws ImageException
	{
		File writeTo = new File(fileName);
		
		FileImageOutputStream out = null;
		
		try {
			out = new FileImageOutputStream(writeTo);
		} catch (FileNotFoundException ex) {
			throw new ImageException("Target file '" + fileName + "' not found: " + ex.getMessage(), ex);
		} catch (IOException ex) {
			throw new ImageException("Error creating file output stream for image: " + ex.getMessage(), ex);
		}
		
		
		saveImage(image, out, format);
		
		try {
			out.flush();
		} catch (IOException ex) {
			throw new ImageException("Error flushing output stream: " + ex.getMessage(), ex);
		}
		
		try {
			out.close();
		} catch (IOException ex) {
			throw new ImageException("Error closing output stream: " + ex.getMessage(), ex);
		}
		
	}
	
	/** Saves an image to disk. 
	 * 
	 * @param image Image to save to disk.
	 * @param out Target output stream
	 * @param format Format number.
	 * @throws ImageException If the format is invalid or there is a write error.
	 */
	public static void saveImage(BufferedImage image, FileImageOutputStream out, ImageTypeEnum format) throws ImageException
	{
		
		if (!ImageTypeEnum.isSupportedFormat(format)) {
			throw new ImageException("Unsupported format: " + format);
		}
		
		if (format == ImageTypeEnum.JPEG) {
			image = ImageWriter.recodeImageForJpeg(image);
			try {
				writeJpeg(image, out, 1.0f);
			} catch (IOException ex) {
				throw new ImageException("Error writing JPEG image: " + ex.getMessage(), ex);
			}
		} else {
		
			String formatName = format.formatName();
			if (formatName == null) {
				throw new ImageException("Format string not found for type: " + format);
			}
			
			try {
				ImageIO.write(image, formatName, out);
			} catch (IOException ex) {
				throw new ImageException("Error writing image to disk: " + ex.getMessage(), ex);
			}
		}
		
		
	}
	
	
	/** Saves an image to disk. 
	 * 
	 * @param image Image to save to disk.
	 * @param out Target output stream
	 * @param format Format number.
	 * @throws ImageException If the format is invalid or there is a write error.
	 */
	public static void saveImage(BufferedImage image, OutputStream out, ImageTypeEnum format) throws ImageException
	{
		
		if (!ImageTypeEnum.isSupportedFormat(format)) {
			throw new ImageException("Unsupported format: " + format);
		}
		
		if (format == ImageTypeEnum.JPEG) {
			image = ImageWriter.recodeImageForJpeg(image);

		} 
		
		String formatName = format.formatName();
		if (formatName == null) {
			throw new ImageException("Format string not found for type: " + format);
		}
		
		try {
			ImageIO.write(image, formatName, out);
		} catch (IOException ex) {
			throw new ImageException("Error writing image to disk: " + ex.getMessage(), ex);
		}

	}
	
	/**
	 * See: http://www.universalwebservices.net/web-programming-resources/java/adjust-jpeg-image-compression-quality-when-saving-images-in-java
	 * @param image
	 * @param out
	 * @throws IOException 
	 */
	protected static void writeJpeg(BufferedImage bufferedImage, FileImageOutputStream out, float quality) throws IOException
	{
		Iterator<javax.imageio.ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		javax.imageio.ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(quality);   // an integer between 0 and 1
										// 1 specifies minimum compression and maximum quality
		
		writer.setOutput(out);
		IIOImage image = new IIOImage(bufferedImage, null, null);
		writer.write(null, image, iwp);
		writer.dispose();
	}
	

	/** Rerenders an image in a format supported by the JPEG specification.
	 * 
	 * @param source The source image that needs rerendering.
	 * @return The new rerendered image or the same image if it was already in 
	 * the correct format.
	 */
	protected static BufferedImage recodeImageForJpeg(BufferedImage source)
	{
		if (source.getType() == BufferedImage.TYPE_INT_RGB) {
			return source;
		}
		
		int width = source.getWidth();
		int height = source.getHeight();
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) image.createGraphics();
		g2d.drawImage(source, 0, 0, width, height, new ImageObserver() {
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
			{ return true; }
		});
		
		g2d.dispose();
		return image;
	}
	
	
}
