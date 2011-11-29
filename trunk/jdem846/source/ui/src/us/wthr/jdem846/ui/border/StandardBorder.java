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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

/** Border with rounded corners. Based on RoundedBorder from SwingSet3.
 * 
 * @author Kevin M. Gill
 *
 */
public class StandardBorder implements Border
{

	private int cornerRadius;
	
	private int padding = 5;
	private int margin = 5;
	
	public StandardBorder()
	{
		this.cornerRadius = 5;
	}
	
	public StandardBorder(int cornerRadius)
	{
		this.cornerRadius = cornerRadius;
	}

	public Color getBorderColor(Component c)
	{
		//return c.getBackground().darker();//Utilities.deriveColorHSB(c.getBackground(), 0, 0, -.3f); 
		return Color.GRAY;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		Graphics2D g2 = (Graphics2D)g.create();      
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
      
		x += getMargin();
		y += getMargin();
		
		height -= (getMargin()*2);
		width -= (getMargin()*2);
		
		Color color = getBorderColor(c);
		
		g2.setColor(deriveColorAlpha(color, 40));         
		g2.drawRoundRect(x, y + 2, width - 1, height - 3, cornerRadius, cornerRadius); 
        
		g2.setColor(deriveColorAlpha(color, 90));         
		g2.drawRoundRect(x, y + 1, width - 1, height - 2, cornerRadius, cornerRadius);  
      
		g2.setColor(deriveColorAlpha(color, 255));         
		g2.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius); 
      
		g2.dispose();  
		
	}
	

	
	
	@Override
	public Insets getBorderInsets(Component c)
	{
		return getBorderInsets(c, new Insets(0,0,0,0));
	}

	public Insets getBorderInsets(Component c, Insets insets) 
	{        
		insets.top = insets.bottom = getMargin() + getPadding() + cornerRadius/2;         
		insets.left = insets.right = getMargin() + getPadding();      
		
		return insets;    
	} 

	
	@Override
	public boolean isBorderOpaque()
	{
		return false;
	}
	
	
	
	public int getCornerRadius()
	{
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius)
	{
		this.cornerRadius = cornerRadius;
	}

	protected Color deriveColorAlpha(Color color, int alpha)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	public int getPadding()
	{
		return padding;
	}

	public void setPadding(int padding)
	{
		this.padding = padding;
	}

	public int getMargin()
	{
		return margin;
	}

	public void setMargin(int margin)
	{
		this.margin = margin;
	}
	
	
}
