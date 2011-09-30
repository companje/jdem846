package us.wthr.jdem846.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	private static Log log = Logging.getLog(ImageWriter.class);
	
	
	public static final int JPEG = 0;
	public static final int PNG = 1;
	protected static final int UNDEFINED_TYPE = -1;
	
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
		
		int type = ImageWriter.imageTypeFromFileName(fileName);
		if (type != ImageWriter.UNDEFINED_TYPE) {
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
	public static void saveImage(BufferedImage image, String fileName, int format) throws ImageException
	{
		
		if (!ImageWriter.isSupportedFormat(format)) {
			throw new ImageException("Unsupported format: " + format);
		}
		
		if (format == ImageWriter.JPEG) {
			image = ImageWriter.recodeImageForJpeg(image);
		}
		
		String formatName = ImageWriter.getFormatString(format);
		if (formatName == null) {
			throw new ImageException("Format string not found for type: " + format);
		}
		
		File writeTo = new File(fileName);
		
		try {
			ImageIO.write(image, formatName, writeTo);
		} catch (IOException ex) {
			throw new ImageException("Error writing image to disk: " + ex.getMessage(), ex);
		}
		
		
	}
	
	/** Determines the format name from the format type number.
	 * 
	 * @param format A supported format type number.
	 * @return The informal format name to be used by ImageIO.
	 */
	protected static String getFormatString(int format)
	{
		switch(format) {
		case ImageWriter.JPEG:
			return "JPG";
		case ImageWriter.PNG:
			return "PNG";
		default:
			return null;
		}
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
		g2d.drawImage(source, 0, 0, new ImageObserver() {
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
			{ return true; }
		});
		
		g2d.dispose();
		return image;
	}
	
	/** Determines the image format type from the file name.
	 * 
	 * @param fileName A file name with an image format extension (e.g. 'foo.jpg' or 'foo.png')
	 * @return The type number
	 */
	protected static int imageTypeFromFileName(String fileName)
	{
		if (fileName == null) {
			return ImageWriter.UNDEFINED_TYPE;
		}
		
		if (fileName.toLowerCase().endsWith(".jpg")
				|| fileName.toLowerCase().endsWith(".jpeg")) {
			return ImageWriter.JPEG;
		} else if (fileName.toLowerCase().endsWith(".png")) {
			return ImageWriter.PNG;
		} else {
			return ImageWriter.UNDEFINED_TYPE;
		}
		
		
	}
	
	/** Determines if the specified format type number is supported by this class.
	 * 
	 * @param format A format type number.
	 * @return True if the type number is supported, otherwise false.
	 */
	protected static boolean isSupportedFormat(int format)
	{
		switch (format) {
		case ImageWriter.JPEG:
		case ImageWriter.PNG:
			return true;
		default:
			return false;
		}
	}
	
}
