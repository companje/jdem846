/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui.border;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;


public class StandardTitledBorder extends StandardBorder
{
	private String title;
	private Font titleFont = new Font("SansSerif", Font.BOLD, 12);
	
	private Color titleColor = new Color(31, 64, 102);
	
	private Color titleSpaceColor = new Color(149, 174, 201);
	
	public StandardTitledBorder(String title)
	{
		this.title = title;
	}
	
	@Override
	public Insets getBorderInsets(Component c, Insets insets)
	{
		Insets borderInserts = super.getBorderInsets(c, insets);
		borderInserts.top = getTitleHeight(c) + 3;
		return borderInserts;
	}
	
	
	
	
	protected int getTitleHeight(Component c)
	{
		return (int) Math.round(c.getFontMetrics(titleFont).getHeight() * 1.80);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height)
	{
		int origX = x;
		int origY = y;
		int origHeight = height;
		int origWidth = width;
		
		x += 5;
		y += 5;
		
		height -= 5;
		width -= 6;
		
		int titleHeight = getTitleHeight(c);
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
		
		
		for (int iX = x; iX < width - (this.getCornerRadius() / 2); iX++) {
			Color color = getGradientColor(titleSpaceColor, c.getBackground(), ((double)iX / ((double)width - (this.getCornerRadius() / 2))));
			g2d.setColor(color);
			
			if (iX - x <= this.getCornerRadius() / 2) {
				int iY = y + getYPoint2D(iX -x,this.getCornerRadius()) - 1;
				g2d.drawLine(iX, iY, iX, titleHeight);
			} else {
				g2d.drawLine(iX, y, iX, titleHeight);
			}
			
			
		}
		
		Color borderColor = getBorderColor(c);
		g2d.setColor(borderColor);
		g2d.drawLine(x, titleHeight, width, titleHeight);
		
		
		g2d.setFont(titleFont);
		FontMetrics metrics = c.getFontMetrics(c.getFont()); 
		
		g2d.setColor(Color.GRAY);        
		g2d.drawString(title, x + 9, y + 1 + (titleHeight - metrics.getHeight())/2 + metrics.getAscent());  

		g2d.setColor(titleColor);        
		g2d.drawString(title, x + 8, y + (titleHeight - metrics.getHeight())/2 + metrics.getAscent());  

		
		g2d.dispose();
		
		super.paintBorder(c, g, origX, origY, origWidth, origHeight);
	}


	protected int getYPoint2D(double x, double radius)
	{
		x = (radius - x);
		double a = Math.acos(x / radius);
		double y = Math.sin(a) * radius;
		return (int) (radius - y);
	}

	
	protected Color getGradientColor(Color c0, Color c1, double frac)
	{
		double r0 = c0.getRed();
		double g0 = c0.getGreen();
		double b0 = c0.getBlue();
		
		double r1 = c1.getRed();
		double g1 = c1.getGreen();
		double b1 = c1.getBlue();
		
		double r2 = (r1 * frac) + (r0 * (1.0 - frac));
		double g2 = (g1 * frac) + (g0 * (1.0 - frac));
		double b2 = (b1 * frac) + (b0 * (1.0 - frac));
		
		return new Color((int)r2, (int)g2, (int)b2);
	}
}
