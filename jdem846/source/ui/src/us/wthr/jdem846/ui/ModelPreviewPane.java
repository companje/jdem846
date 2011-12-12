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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.tasks.RunnableTask;
import us.wthr.jdem846.tasks.TaskControllerService;
import us.wthr.jdem846.tasks.TaskStatusAdapter;
import us.wthr.jdem846.tasks.TaskStatusListener;
import us.wthr.jdem846.ui.ModelPreviewPane.ImagePanel;
import us.wthr.jdem846.ui.ModelingWorkerThread.ModelCompletionListener;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.border.StandardBorder;
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class ModelPreviewPane extends RoundedPanel
{
	private static Log log = Logging.getLog(ModelPreviewPane.class);
	
	//private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	private ModelContext modelContext;
	
	private int previewWidth = 250;
	private int previewHeight = 100;
	
	//private WorkingGlassPane glassPane;
	
	private ModelCanvas rasterLayerCanvas;
	private ModelCanvas shapeLayerCanvas;
	
	private boolean shapeLayerNeedsUpdate = false;
	private boolean rasterLayerNeedsUpdate = false;
	private ImagePanel imagePanel;
	
	private boolean shapePreviewEnabled = true;
	private boolean rasterPreviewEnabled = true;
	private boolean useSimpleRasterPreview = true;
	
	public ModelPreviewPane(ModelContext modelContext)
	{
		//super(I18N.get("us.wthr.jdem846.ui.modelPreviewPane.title"));
		//((StandardBorder) this.getBorder()).setPadding(1);
		
		this.setBackground(Color.WHITE);

		rasterPreviewEnabled = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.preview.rasterData");
		shapePreviewEnabled = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.preview.shapeData");
		useSimpleRasterPreview = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.preview.useSimplifiedRasterPreview");

		
		
		imagePanel = new ImagePanel();
		this.modelContext = modelContext;
		
		
		
		
		//modelContext = ModelContext.createInstance(dataPackage, modelOptions);
		// Set properties
		//setDataPackage(dataPackage);
		//setModelOptions(modelOptions);
		//setPreviewWidth(previewWidth);
		//setPreviewHeight(previewHeight);

		// Create components
		setDefaultImage();
		//setBorder(BorderFactory.createEtchedBorder());
		this.setOpaque(false);
		
		//glassPane = new WorkingGlassPane();
		//glassPane.setVisible(true);
		//JdemFrame.getInstance().setGlassPane(glassPane);
		
		
		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) { }
			public void componentMoved(ComponentEvent e) { }
			public void componentResized(ComponentEvent e)
			{
				//update();
			}
			public void componentShown(ComponentEvent e) { 
				if (rasterLayerNeedsUpdate || shapeLayerNeedsUpdate) {
					update(rasterLayerNeedsUpdate, shapeLayerNeedsUpdate);
				}
			}
		});
		
		
		setLayout(new BorderLayout());
		add(imagePanel, BorderLayout.CENTER);
		
		shapeLayerNeedsUpdate = true;
		rasterLayerNeedsUpdate = true;
	}
	
	protected void setDefaultImage()
	{
		imagePanel.setImage(null);
		//canvas = null;
	}
	
	
	public void update(boolean updateRasterData, boolean updateShapes)
	{
		rasterLayerNeedsUpdate = updateRasterData;
		shapeLayerNeedsUpdate = updateShapes;
		
		if (!rasterPreviewEnabled) {
			rasterLayerNeedsUpdate = false;
		}
		
		if (!shapePreviewEnabled) {
			shapeLayerNeedsUpdate = false;
		}
		
		
		
		
		float width = getWidth();
		float height = getHeight();
		
		if (width == 0 || height == 0)
			return;
		
		if (isVisible() == false) {
			shapeLayerNeedsUpdate = true;
			rasterLayerNeedsUpdate = true;
			return;
		}

		//JdemFrame.getInstance().setGlassVisible(I18N.get("us.wthr.jdem846.ui.modelPreviewPane.working"), this, true);

			
		TaskStatusListener taskStatusListener = new TaskStatusAdapter() {
			public void taskCompleted(RunnableTask task)  { 
				//JdemFrame.getInstance().setGlassVisible(false);
			}
			public void taskFailed(RunnableTask task, Throwable thrown) { 
				//JdemFrame.getInstance().setGlassVisible(false);
					
				JOptionPane.showMessageDialog(getRootPane(),
						I18N.get("us.wthr.jdem846.ui.modelPreviewPane.modelFailed.message") + ": " + thrown.getMessage(),
						I18N.get("us.wthr.jdem846.ui.modelPreviewPane.modelFailed.title"),
						JOptionPane.ERROR_MESSAGE);
			}
		};
		
		
		updateShapeLayers(taskStatusListener);
		updateRasterLayers(taskStatusListener);
	}
	
	
	protected void updateShapeLayers(TaskStatusListener taskStatusListener)
	{
		
		if (!shapeLayerNeedsUpdate) {
			return;
		}
		
		if (modelContext.getShapeDataContext().getShapeDataListSize() > 0) {
			updateLayer(taskStatusListener, false, true);
		}
		
		shapeLayerNeedsUpdate = false;
	}
	
	
	protected void updateRasterLayers(TaskStatusListener taskStatusListener)
	{
		if (!rasterLayerNeedsUpdate) {
			return;
		}
		
		if (modelContext.getRasterDataContext().getRasterDataListSize() > 0) {
			updateLayer(taskStatusListener, true, false);
		}
		
		rasterLayerNeedsUpdate = true;
	}
	
	protected void updateLayer(TaskStatusListener taskStatusListener, final boolean renderRasterData, final boolean renderShapes)
	{
		ModelContext modelContext;
		try {
			modelContext = this.modelContext.copy();
		} catch (DataSourceException ex) {
			log.error("Failed to copy model context: " + ex.getMessage(), ex);
			return;
		}
		
		//modelContext.getModelOptions().setBackgroundColor("255;255;255;0");
		modelContext.getModelOptions().setBackgroundColor("0;0;0;0");
		modelContext.getModelOptions().setColoringType("hypsometric-tint");
		modelContext.getModelOptions().setAntialiased(true);
		modelContext.getModelOptions().setWidth(1000);
		modelContext.getModelOptions().setHeight(1000);
		
		
		if (modelContext.getRasterDataContext().getDataMinimumValue() >= modelContext.getRasterDataContext().getDataMaximumValue()) {
			modelContext.getRasterDataContext().setDataMaximumValue(8850);
			modelContext.getRasterDataContext().setDataMinimumValue(-10971);
		}
		
		modelContext.updateContext();
		ModelCanvas canvas = modelContext.getModelCanvas(true);
		
		if (useSimpleRasterPreview && rasterPreviewEnabled) {// && renderShapes) {
			try {
				applySimpleRasterPreview(canvas);
				
				if (renderRasterData) {
					rasterLayerCanvas = canvas;
					mergeAndSetLayers();
					return;
				}
			} catch (CanvasException ex) {
				log.error("Failed to apply simple raster preview to canvas: " + ex.getMessage(), ex);
			}
		}
		
		
		
		//modelContext.getModelOptions().setGridSize(modelContext.getRasterDataContext().getLongitudeResolution() * 1);
		
		final Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		
		RunnableTask renderTask = new RunnableTask("Vector Rendering Task") {
			public void run() throws RenderEngineException {
				log.info("Model rendering task starting");
				this.setStoppable(true);
					
				OutputProduct<ModelCanvas> product = dem2d.generate(!renderRasterData, !renderShapes);
				ModelCanvas modelCanvas = product.getProduct();
				if (modelCanvas != null) {
					
					if (renderRasterData) {
						rasterLayerCanvas = modelCanvas;
					}
					if (renderShapes) {
						shapeLayerCanvas = modelCanvas;
					}
					
					mergeAndSetLayers();
				}
					
			}
			public void cancel() {
				dem2d.cancel();
			}
		};
		TaskControllerService.addTask(renderTask, taskStatusListener);
		
	}
	
	protected void mergeAndSetLayers()
	{
		ModelContext modelContext;
		try {
			modelContext = this.modelContext.copy();
		} catch (DataSourceException ex) {
			log.error("Failed to copy model context: " + ex.getMessage(), ex);
			return;
		}
		
		modelContext.getModelOptions().setBackgroundColor("0;0;0;0");
		modelContext.getModelOptions().setAntialiased(true);
		modelContext.getModelOptions().setWidth(1000);
		modelContext.getModelOptions().setHeight(1000);
		modelContext.updateContext();
		ModelCanvas canvas = modelContext.getModelCanvas(true);
		
		//ModelCanvas canvas = modelContext.createModelCanvas();
		
		//ModelCanvas canvas;
		//try {
		//	canvas = shapeLayerCanvas.getCopy(false);
		//} catch (CanvasException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//	return;
		//}
		
		
		log.info("Merging layers onto canvas of W/H: " + canvas.getWidth() + "/" + canvas.getHeight());
		
		if (rasterLayerCanvas != null && rasterPreviewEnabled) {
			
			// The following is WRONG!
			canvas.drawImage(rasterLayerCanvas.getImage(), 0, 0, rasterLayerCanvas.getWidth(), rasterLayerCanvas.getHeight());
		}
		
		
		
		
		if (shapeLayerCanvas != null && shapePreviewEnabled) {

			// The following is WRONG!
			canvas.drawImage(shapeLayerCanvas.getImage(), 0, 0, shapeLayerCanvas.getWidth(), shapeLayerCanvas.getHeight());
		}

		imagePanel.setImage((BufferedImage)canvas.getImage());
		repaint();
	}
	
	
	protected void applySimpleRasterPreview(ModelCanvas canvas) throws CanvasException
	{
		log.info("Drawing simple raster preview...");
		
		//MapPoint point = new MapPoint();
		
		//MapProjection projection = canvas.getMapProjection();
		
		
		//g2d.setColor(getBackground());
		//g2d.fillRect(0, 0, getWidth(), getHeight());
		
		//Color stroke = Color.GRAY;
		//Color fill = new Color(stroke.getRed(), stroke.getGreen(), stroke.getBlue(), 0x7F);
		int[] strokeColor = {Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 255};
		int[] fillColor = {Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 0x7F};
		int[] textColor = {0x0, 0x0, 0x0, 0xFF};
		//Color text = Color.WHITE;

		
		for (int i = modelContext.getRasterDataContext().getRasterDataListSize() - 1; i >= 0; i--) {
			RasterData rasterData = modelContext.getRasterDataContext().getRasterDataList().get(i);
			
			double latitude = rasterData.getNorth();
			double longitude = rasterData.getWest();
			
			double height = latitude - rasterData.getSouth() - rasterData.getLatitudeResolution();
			double width = rasterData.getEast() - (longitude + rasterData.getLongitudeResolution());
			
			canvas.fillRectangle(fillColor,
					latitude, longitude, 
					width, height,
					0);
			
			
			canvas.drawRectangle(strokeColor,
					latitude, longitude, 
					width, height,
					0);
			

			String label = "#"+(i+1);
			canvas.drawText(label, textColor, latitude-(height/2.0), longitude+(width/2.0), true);
			
		}
	}
	

	public int getPreviewWidth() 
	{
		return previewWidth;
	}

	public void setPreviewWidth(int previewWidth) 
	{
		this.previewWidth = previewWidth;
	}

	public int getPreviewHeight() 
	{
		return previewHeight;
	}

	public void setPreviewHeight(int previewHeight) 
	{
		this.previewHeight = previewHeight;
	}
	

	protected Rectangle getWindowLocation()
	{
		Point pt = this.getLocationOnScreen();
		Rectangle rec = new Rectangle(pt.x, pt.y, getWidth(), getHeight());
		return rec;
	}
	
	
	class ImagePanel extends Panel
	{
		//private ModelCanvas canvas = null;
		private BufferedImage image;
		
		public ImagePanel()
		{
			
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
}
