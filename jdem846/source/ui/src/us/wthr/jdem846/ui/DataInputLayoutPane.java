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

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataRowColumnBox;
import us.wthr.jdem846.gis.projections.EquirectangularProjection;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.ui.border.StandardBorder;

@SuppressWarnings("serial")
public class DataInputLayoutPane extends TitledRoundedPanel
{

	private LayoutGraphicPanel graphicPanel;

	public DataInputLayoutPane(ModelContext modelContext)
	{
		super(I18N.get("us.wthr.jdem846.ui.dataInputLayoutPane.title"));

		graphicPanel = new LayoutGraphicPanel(modelContext);

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
	

	
	
	//public DataPackage getDataPackage()
	//{
	//	return graphicPanel.getDataPackage();
	//}


	//public void setDataPackage(DataPackage dataPackage)
	//{
	//	graphicPanel.setDataPackage(dataPackage);
	//}


	//public ModelOptions getModelOptions() 
	//{
//		return graphicPanel.getModelOptions();
	//}


	//public void setModelOptions(ModelOptions modelOptions) 
	//{
	//	graphicPanel.setModelOptions(modelOptions);
	//}
	
	
	class LayoutGraphicPanel extends JPanel
	{
		private ModelContext modelContext;
		
		public LayoutGraphicPanel(ModelContext modelContext)
		{
			this.modelContext = modelContext;
		}

		public void setModelContext(ModelContext modelContext)
		{
			this.modelContext = modelContext;
		}
		
		public ModelContext getModelContext()
		{
			return modelContext;
		}
		
		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			
			MapProjection projection = new EquirectangularProjection();
			projection.setUp(modelContext.getRasterDataContext().getNorth(), 
					modelContext.getRasterDataContext().getSouth(), 
					modelContext.getRasterDataContext().getEast(), 
					modelContext.getRasterDataContext().getWest(), 
					getWidth(), 
					getHeight());
			
			MapPoint point = new MapPoint();
			
			
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			Color stroke = Color.GRAY;
			Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 0x7F);
			Color text = Color.WHITE;
	
			//for (RasterData rasterData : modelContext.getRasterDataContext().getRasterDataList()) {
			for (int i = modelContext.getRasterDataContext().getRasterDataListSize() - 1; i >= 0; i--) {
				RasterData rasterData = modelContext.getRasterDataContext().getRasterDataList().get(i);
				
				projection.getPoint(rasterData.getNorth(), rasterData.getWest(), 0, point);
				int x = (int) point.column;
				int y = (int) point.row;
				
				projection.getPoint(rasterData.getSouth(), rasterData.getEast(), 0, point);
				int w = (int) point.column - x;
				int h = (int) point.row - y;
				
				
				g2d.setColor(stroke);
				g2d.drawRect(x, y, w, h);
				
				g2d.setColor(fill);
				g2d.fillRect(x, y, w, h);
				
				g2d.setColor(text);
				
				String label = ""+(i+1);
				
				FontMetrics fonts = g2d.getFontMetrics();
				int textWidth = fonts.stringWidth(label);
				
				int textMidX = (int) ((double)x + ((double)w / 2.0) - ((double)textWidth / 2.0));
				int textMidY = (int) ((double)y + ((double)h / 2.0));
				
				
				
				g2d.drawString(label, textMidX, textMidY);
				

				
			}
			
			/*
			// TODO: Don't skew the image. The following code will
			// TODO: Switch to using the modelContext dimensions, not just the raster data
			double xRatio = (double)getWidth() / modelContext.getRasterDataContext().getDataColumns();
			double yRatio = (double)getHeight() / modelContext.getRasterDataContext().getDataRows();
			
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			Color stroke = Color.GRAY;
			Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 0x7F);
			Color text = Color.WHITE;
			int i = 1;

			for (RasterDataRowColumnBox rowColBox : modelContext.getRasterDataContext().getRasterDataRowColumnBoxes()) {
				int x = (int) Math.floor((double)rowColBox.getLeftX() * xRatio);
				int y = (int) Math.floor((double)rowColBox.getTopY() * yRatio);
				
				int w = (int) Math.floor((double)rowColBox.getWidth() * xRatio) - 1;
				int h = (int) Math.floor((double)rowColBox.getHeight() * yRatio) - 1;
				

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
			*/
		}
		
	}
	
	
	
}
