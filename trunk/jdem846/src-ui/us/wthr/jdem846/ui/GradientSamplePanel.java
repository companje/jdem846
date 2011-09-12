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
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class GradientSamplePanel extends Panel
{
	private static Log log = Logging.getLog(GradientSamplePanel.class);
	
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
		
		float min = (float) coloring.getMinimumSupported();
		float max = (float) coloring.getMaximumSupported();
		float range = Math.abs(min) + Math.abs(max);
		
		for (int y = 0; y < height; y++) {
			float ratio = 1.0f - ((float)y / height);
			float value = min + (ratio * range);
			
			//log.info("Value: " + value + ", " + min + "/" + max + "/" + range);
			//double value = min + ((double)y / height);
			
			coloring.getGradientColor(value, min, max, rgba);
			
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
