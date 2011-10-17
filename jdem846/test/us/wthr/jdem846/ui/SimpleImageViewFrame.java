package us.wthr.jdem846.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Frame;

/** A very simple frame that displays an image. 
 * 
 * @author Kevin M. Gill
 *
 */
@SuppressWarnings("serial")
public class SimpleImageViewFrame extends Frame
{
	
	private static Log log = Logging.getLog(SimpleImageViewFrame.class);
	private static int defaultFrameWidth = 600;
	private static int defaultFrameHeight = 600;
	
	private Image image;
	
	public SimpleImageViewFrame(Image image)
	{
		this(image, defaultFrameWidth, defaultFrameHeight);
	}
	
	public SimpleImageViewFrame(Image image, int frameWidth, int frameHeight)
	{
		this.image = image;
		
		this.setTitle("Image Viewer");
		this.setSize(frameWidth, frameHeight);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(SimpleImageViewFrame.EXIT_ON_CLOSE);	
	}
	
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		double scale = getZoomToFitScalePercentage();
		int width = (int) Math.round((double)image.getWidth(this) * scale);
		int height = (int) Math.round((double)image.getHeight(this) * scale);
		
		BufferedImage scaled = ImageUtilities.getScaledInstance((BufferedImage)image, width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		
		int x = (int) Math.round(((double)getWidth() / 2.0) - ((double)width / 2.0));
		int y = (int) Math.round(((double)getHeight() / 2.0) - ((double)height / 2.0));
		
		
		g2d.drawImage(scaled, x, y, this);
		
		
	}
	
	
	protected double getZoomToFitScalePercentage()
	{
		double imageWidth = image.getWidth(this);
		double imageHeight = image.getHeight(this);
		
		double panelWidth = getWidth();
		double panelHeight = getHeight();
		
		double scaleWidth = 0;
		double scaleHeight = 0;
		
		double scale = Math.max(panelHeight/imageHeight, panelWidth/imageWidth);
		scaleHeight = imageHeight * scale;
		scaleWidth = imageWidth * scale;
		
		
		if (scaleHeight > panelHeight) {
			scale = panelHeight/scaleHeight;
		    scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		if (scaleWidth > panelWidth) {
		    scale = panelWidth/scaleWidth;
		    scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		
		
		return (scaleWidth / imageWidth);
	}
	
	
	public static void main(String[] args)
	{
		String testImagePath = "/elev/etopo1_bed_g_f4/kml/dist.jpg/1/4/7.jpg";
		BufferedImage testImage = null;
		int frameWidth = 700;
		int frameHeight = 600;
		
		
		try {
			testImage = ImageIO.read(new File(testImagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if (testImage != null) {
			SimpleImageViewFrame viewFrame = new SimpleImageViewFrame(testImage, frameWidth, frameHeight);
			viewFrame.setVisible(true);
		} else {
			log.error("Test image is null, cannot create frame");
		}
		
		
		
	}
	
}
