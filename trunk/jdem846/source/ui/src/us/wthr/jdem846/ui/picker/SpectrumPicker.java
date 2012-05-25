package us.wthr.jdem846.ui.picker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class SpectrumPicker extends Panel
{
	
	private BufferedImage spectrumImage;
	
	private Color selectedColor;
	private Color mouseOverColor;
	
	public SpectrumPicker() throws IOException
	{
		spectrumImage = (BufferedImage) ImageIcons.loadImage("resources://color/spectrum.jpg");
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				selectedColor = getColorAtRelativePoint(e.getX(), e.getY(), getWidth(), getHeight());
			}
			public void mouseEntered(MouseEvent e)
			{
				mouseOverColor = getColorAtRelativePoint(e.getX(), e.getY(), getWidth(), getHeight());
			}
			public void mouseExited(MouseEvent e)
			{
				mouseOverColor = null;
			}
			public void mouseMoved(MouseEvent e)
			{
				mouseOverColor = getColorAtRelativePoint(e.getX(), e.getY(), getWidth(), getHeight());
			}
			
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
	}
	
	public Color getColorAtRelativePoint(int x, int y, int width, int height)
	{
		double rH = (double) x / (double) width;
		double rV = (double) y / (double) height;
		
		int xR = (int) Math.round((double) spectrumImage.getWidth() * rH);
		int yR = (int) Math.round((double) spectrumImage.getHeight() * rV);
		
		return getColorAtPoint(xR, yR);
	}
	
	public Color getColorAtPoint(int x, int y)
	{
		int[] rgba = new int[4];
		rgba[3] = 0xFF;
		spectrumImage.getRaster().getPixel(x, y, rgba);
		return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.drawImage(spectrumImage, 0, 0, getWidth(), getHeight(), null);
	}

	public Color getSelectedColor()
	{
		return selectedColor;
	}

	public Color getMouseOverColor()
	{
		return mouseOverColor;
	}
	
	
	
}
