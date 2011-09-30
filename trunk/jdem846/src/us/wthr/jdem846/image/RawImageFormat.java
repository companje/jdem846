package us.wthr.jdem846.image;

import java.awt.image.BufferedImage;

import us.wthr.jdem846.exception.ImageException;

/** Class for reading and writing simple raster-based image files
 * 
 * @author Kevin M. Gill
 *
 */
public class RawImageFormat
{
	
	
	protected RawImageFormat()
	{
		
	}
	
	
	/** Writes an image raster to disk.
	 * 
	 * @param image Image to write to disk.
	 * @param fileName Filesystem location for the destination image file.
	 * @throws ImageException
	 */
	public static void writeRawImageFile(BufferedImage image, String fileName) throws ImageException
	{
		
	}
	
	/** Reads an image raster from disk.
	 * 
	 * @param fileName Filesystem location for the source image file.
	 * @return The image
	 * @throws ImageException
	 */
	public static BufferedImage readRawImageFile(String fileName) throws ImageException
	{
		return null;
	}
	
}
