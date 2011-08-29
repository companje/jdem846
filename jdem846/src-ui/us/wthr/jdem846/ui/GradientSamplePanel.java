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

package us.wthr.jdem846.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class GradientSamplePanel extends Panel
{
	
	private String gradientIdentifier = null;
	private ColoringInstance coloringInstance = null;
	
	public GradientSamplePanel()
	{
		//setBorder(BorderFactory.createEtchedBorder());
		this.setOpaque(false);
	}
	
	@Override
	public void paint(Graphics g)
	{
		int height = getHeight();
		int width = getWidth();
		
		if (coloringInstance == null) {
			super.paint(g);
			return;
		}
		
		ModelColoring coloring = coloringInstance.getImpl();
		
		int[] rgba = new int[4];
		
		for (int y = 0; y < height; y++) {
			double ratio = 1.0 - ((double)y / height);
			coloring.getColor(ratio, rgba);
			
			Color color = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
			g.setColor(color);
			g.drawLine(0, y, width, y);
		}
		
		super.paint(g);
		
		
	}

	protected void updateGradient()
	{
		coloringInstance = ColoringRegistry.getInstance(gradientIdentifier);
	}
	
	public String getGradientIdentifier()
	{
		return gradientIdentifier;
	}

	public void setGradientIdentifier(String gradientIdentifier)
	{
		this.gradientIdentifier = gradientIdentifier;
		updateGradient();
		repaint();
	}
	
	
	
	
}
