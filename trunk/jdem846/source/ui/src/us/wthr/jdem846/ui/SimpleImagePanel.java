package us.wthr.jdem846.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class SimpleImagePanel extends Panel
{
	private static Log log = Logging.getLog(SimpleImagePanel.class);
	
	private BufferedImage image;
	
	public SimpleImagePanel()
	{
		setBackground(Color.WHITE);
	}
	
	public void setImage(BufferedImage image)
	{
		this.image = image;
	}
	
	public BufferedImage getImage()
	{
		return this.image;
	}
	

	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		//g2d.setColor(Color.WHITE);
		//g2d.fillRect(0, 0, getWidth(), getHeight());
		
		if (image == null) {
			super.paint(g2d);
			return;
		}
		
		double canvasWidth = image.getWidth();
		double canvasHeight = image.getHeight();
		
		double panelWidth = getWidth();
		double panelHeight = getHeight();
		
		double scaleWidth = 0;
		double scaleHeight = 0;
		
		Image toPaint = null;
		
		double scale = Math.max(panelHeight/canvasHeight, panelWidth/canvasWidth);
		scaleHeight = canvasHeight * scale;
		scaleWidth = canvasWidth * scale;
		
		
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
		
		int topLeftX = (int)Math.round((panelWidth / 2) - (scaleWidth / 2));
		int topLeftY = (int)Math.round((panelHeight / 2) - (scaleHeight / 2));
		
		toPaint = ImageUtilities.getScaledInstance(image, (int)scaleWidth, (int)scaleHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		
		g2d.drawImage(toPaint, topLeftX, topLeftY, (int)scaleWidth, (int)scaleHeight, this);

		

	}
}
