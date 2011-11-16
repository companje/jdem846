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

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
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

@SuppressWarnings("serial")
public class ModelPreviewPane extends TitledRoundedPanel
{
	private static Log log = Logging.getLog(ModelPreviewPane.class);
	
	//private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	private ModelContext modelContext;
	
	private int previewWidth = 250;
	private int previewHeight = 100;
	
	//private WorkingGlassPane glassPane;
	
	
	
	private boolean needsUpdate = false;
	private ImagePanel imagePanel;
	
	public ModelPreviewPane(ModelContext modelContext)
	{
		super(I18N.get("us.wthr.jdem846.ui.modelPreviewPane.title"));
		((StandardBorder) this.getBorder()).setPadding(1);
		
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
				if (needsUpdate) {
					update();
				}
			}
		});
		
		
		setLayout(new BorderLayout());
		add(imagePanel, BorderLayout.CENTER);
	}
	
	protected void setDefaultImage()
	{
		imagePanel.setCanvas(null);
		//canvas = null;
	}
	
	
	public void update()
	{
		if (isVisible() == false) {
			needsUpdate = true;
			return;
		}

		JdemFrame.getInstance().setGlassVisible(I18N.get("us.wthr.jdem846.ui.modelPreviewPane.working"), this, true);

			float width = getWidth();
			float height = getHeight();
			
			if (width == 0 || height == 0)
				return;
			
			//ModelContext modelContext = ModelContext.createInstance(dataPackage, modelOptions);
			ModelContext modelContext;
			try {
				modelContext = this.modelContext.copy();
			} catch (DataSourceException ex) {
				// TODO Display error dialog
				log.error("Failed to copy model context: " + ex.getMessage(), ex);
				return;
			}
			
			modelContext.getModelOptions().setBackgroundColor("255;255;255;255");
			
			final Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);

			
			RunnableTask renderTask = new RunnableTask("Model Render Task") {
				
				public void run() throws RenderEngineException
				{
					log.info("Model rendering task starting");
					this.setStoppable(true);
					
					
					
					
					OutputProduct<ModelCanvas> product = dem2d.generate(true);
					ModelCanvas modelCanvas = product.getProduct();
					if (modelCanvas != null) {
						imagePanel.setCanvas(modelCanvas);
						repaint();
					}
					
				}
				
				@Override
				public void cancel()
				{
					dem2d.cancel();
				}
			};
			TaskStatusListener taskStatusListener = new TaskStatusAdapter() {
				public void taskCompleted(RunnableTask task) 
				{ 
					JdemFrame.getInstance().setGlassVisible(false);
				}
				public void taskFailed(RunnableTask task, Throwable thrown) 
				{ 
					JdemFrame.getInstance().setGlassVisible(false);
					
					JOptionPane.showMessageDialog(getRootPane(),
						    I18N.get("us.wthr.jdem846.ui.modelPreviewPane.modelFailed.message") + ": " + thrown.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.modelPreviewPane.modelFailed.title"),
						    JOptionPane.ERROR_MESSAGE);
				}
			};
			
			TaskControllerService.addTask(renderTask, taskStatusListener);
			
			
			
			
			
			/*
			final String background = modelContext.getModelOptions().getBackgroundColor();
			modelContext.getModelOptions().setBackgroundColor("White");
			ModelingWorkerThread workerThread = new ModelingWorkerThread(dem2d);
			workerThread.addModelCompletionListener(new ModelCompletionListener() {
				public void onModelComplete(ModelCanvas modelCanvas)
				{
					imagePanel.setCanvas(modelCanvas);
					JdemFrame.getInstance().setGlassVisible(false);
					repaint();
				}
				public void onModelFailed(Exception ex)
				{
					imagePanel.setCanvas(null);
					JdemFrame.getInstance().setGlassVisible(false);
					
					JOptionPane.showMessageDialog(getRootPane(),
						    I18N.get("us.wthr.jdem846.ui.modelPreviewPane.modelFailed.message") + ": " + ex.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.modelPreviewPane.modelFailed.title"),
						    JOptionPane.ERROR_MESSAGE);
				}
			});
			workerThread.setPreviewModel(true);
			workerThread.start();
			//OutputProduct<DemCanvas> canvasProduct = dem2d.generate(true);
			//modelOptions.setBackgroundColor(background);
			//canvas = canvasProduct.getProduct();
			*/
			
			//this.repaint();
		//} else {
		//	setDefaultImage();
		//}
			
		needsUpdate = false;
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
	

	
	
	//public DataPackage getDataPackage()
	//{
	//	return dataPackage;
	//}

	//public void setDataPackage(DataPackage dataPackage)
	//{
	//	this.dataPackage = dataPackage;
	//}

	//public ModelOptions getModelOptions()
	//{
	//	return modelOptions;
	//}

	//public void setModelOptions(ModelOptions modelOptions)
	//{
	//	this.modelOptions = modelOptions;
	//}

	protected Rectangle getWindowLocation()
	{
		Point pt = this.getLocationOnScreen();
		Rectangle rec = new Rectangle(pt.x, pt.y, getWidth(), getHeight());
		return rec;
	}
	
	
	class ImagePanel extends Panel
	{
		private ModelCanvas canvas = null;
		
		public ImagePanel()
		{
			
		}
		
		public void setCanvas(ModelCanvas canvas)
		{
			this.canvas = canvas;
		}
		
		public ModelCanvas getCanvas()
		{
			return this.canvas;
		}
		

		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			
			if (canvas == null) {
				super.paint(g2d);
				return;
			}
			
			double canvasWidth = canvas.getWidth();
			double canvasHeight = canvas.getHeight();
			
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
			
			BufferedImage image = (BufferedImage) canvas.getFinalizedImage();
			toPaint = ImageUtilities.getScaledInstance(image, (int)scaleWidth, (int)scaleHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
			//toPaint = canvas.getScaled((int)scaleWidth, (int)scaleHeight).getImage();
			
			g2d.drawImage(toPaint, topLeftX, topLeftY, (int)scaleWidth, (int)scaleHeight, this);

			

		}
	}
}
