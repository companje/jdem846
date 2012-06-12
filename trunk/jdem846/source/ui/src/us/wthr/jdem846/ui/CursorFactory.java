package us.wthr.jdem846.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class CursorFactory
{
	private static Log log = Logging.getLog(CursorFactory.class);
	
	public static Cursor PREDEFINED_OPEN_HAND = null;
	public static Cursor PREDEFINED_CLOSED_HAND = null;
	
	static {
		try {
			PREDEFINED_OPEN_HAND = CursorFactory.createCursor(JDem846Properties.getProperty("us.wthr.jdem846.ui.cursors.openHand"), 8, 8, "OpenHand");
		} catch (IOException ex) {
			log.warn("Error loading image: " + ex.getMessage(), ex);
		}
		
		try {
			PREDEFINED_CLOSED_HAND = CursorFactory.createCursor(JDem846Properties.getProperty("us.wthr.jdem846.ui.cursors.closedHand"), 8, 8, "ClosedHand");
		} catch (IOException ex) {
			log.warn("Error loading image: " + ex.getMessage(), ex);
		}
	}
	
	
	public static Cursor createCursor(String url, String name) throws IOException
	{
		Point hotSpot = new Point(0,0);
		return createCursor(url, hotSpot, name);
	}
	
	public static Cursor createCursor(String url, int hotSpotX, int hotSpotY, String name) throws IOException
	{
		Point hotSpot = new Point(hotSpotX, hotSpotY);
		return createCursor(url, hotSpot, name);
	}
	
	public static Cursor createCursor(String url, Point hotSpot, String name) throws IOException
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		BufferedImage cursorImage = (BufferedImage) ImageIcons.loadImage(url);
		Dimension dim = toolkit.getBestCursorSize(16, 16);
		
		if (dim.width != cursorImage.getWidth() || dim.height != cursorImage.getHeight()) {
			cursorImage = resizeImageBounds(cursorImage, dim.width, dim.height);
		}

		Cursor cursor = toolkit.createCustomCursor(cursorImage, hotSpot, name);
		return cursor;
	}
	
	
	
	protected static BufferedImage resizeImageBounds(BufferedImage srcImage, int width, int height)
	{
		
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) resizedImage.createGraphics();
		
		g2d.drawImage(srcImage, 0, 0, null);
		
		g2d.dispose();
		
		return resizedImage;
	}
	
}
