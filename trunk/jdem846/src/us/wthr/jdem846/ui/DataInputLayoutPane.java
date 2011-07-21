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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.input.DataBounds;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.ui.border.StandardBorder;

@SuppressWarnings("serial")
public class DataInputLayoutPane extends TitledRoundedPanel
{

	private LayoutGraphicPanel graphicPanel;

	public DataInputLayoutPane(DataPackage dataPackage, ModelOptions modelOptions)
	{
		super("Elevation Dataset Layout");
		//((StandardBorder) this.getBorder()).setPadding(1);
		
		graphicPanel = new LayoutGraphicPanel(dataPackage, modelOptions);

		setLayout(new BorderLayout());
		add(graphicPanel, BorderLayout.CENTER);
	}

	protected void setDefaultImage()
	{
		// TODO: Remove this
	}

	public void update()
	{
		
		repaint();
	}
	

	
	
	public DataPackage getDataPackage()
	{
		return graphicPanel.getDataPackage();
	}


	public void setDataPackage(DataPackage dataPackage)
	{
		graphicPanel.setDataPackage(dataPackage);
	}


	public ModelOptions getModelOptions() 
	{
		return graphicPanel.getModelOptions();
	}


	public void setModelOptions(ModelOptions modelOptions) 
	{
		graphicPanel.setModelOptions(modelOptions);
	}
	
	
	class LayoutGraphicPanel extends JPanel
	{
		private DataPackage dataPackage;
		private ModelOptions modelOptions;
		
		public LayoutGraphicPanel(DataPackage dataPackage, ModelOptions modelOptions)
		{
			this.dataPackage = dataPackage;
			this.modelOptions = modelOptions;
		}

		public DataPackage getDataPackage()
		{
			return dataPackage;
		}

		public void setDataPackage(DataPackage dataPackage)
		{
			this.dataPackage = dataPackage;
		}

		public ModelOptions getModelOptions()
		{
			return modelOptions;
		}

		public void setModelOptions(ModelOptions modelOptions)
		{
			this.modelOptions = modelOptions;
		}
		
		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			
			// TODO: Don't skew the image. The following code will
			double xRatio = (double)getWidth() / dataPackage.getColumns();
			double yRatio = (double)getHeight() / dataPackage.getRows();
			
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			Color stroke = Color.YELLOW;
			Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 0x7F);
			Color text = Color.WHITE;
			
			//System.out.println("--------------------------------");
			int i = 1;
			for (DataBounds dataBounds : dataPackage.getDataBounds()) {
				int x = (int) ((double)dataBounds.getLeftX() * xRatio);
				int y = (int) ((double)dataBounds.getTopY() * yRatio);
				
				int w = (int) ((double)dataBounds.getWidth() * xRatio);
				int h = (int) ((double)dataBounds.getHeight() * yRatio);
				
				//System.out.println("x/y: " + x + "/" + y + ", w/h: " + w + "/" + h + ", x/y ratios: " + xRatio + "/" + yRatio);
				
				g2d.setColor(stroke);
				g2d.drawRect(x, y, w, h);
				
				g2d.setColor(fill);
				g2d.fillRect(x, y, w, h);
				
				g2d.setColor(text);
				
				String label = ""+(i);
				
				FontMetrics fonts = g2d.getFontMetrics();
				int textWidth = fonts.stringWidth(label);
				
				int textMidX = (int) ((double)x + ((double)w / 2.0) - ((double)textWidth / 2.0));
				int textMidY = (int) ((double)y + ((double)h / 2.0));
				
				
				
				g2d.drawString(label, textMidX, textMidY);
				
				i++;
			}
			
		}
		
	}
	
	
	
}
