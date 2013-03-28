package us.wthr.jdem846.ui.picker;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;

import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class ColorSwatch extends Panel
{
	
	private Color displayColor;
	
	public ColorSwatch()
	{
		this.setOpaque(false);
		this.setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void paint(Graphics g)
	{
		if (displayColor == null) {
			displayColor = this.getBackground();
		}
		
		g.setColor(displayColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		super.paint(g);
	}
	
	public void setDisplayColor(Color displayColor)
	{
		this.displayColor = displayColor;
		this.repaint();
	}
	
}
