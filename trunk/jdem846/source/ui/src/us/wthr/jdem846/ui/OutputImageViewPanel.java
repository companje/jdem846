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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.render.RenderEngine.TileCompletionListener;
import us.wthr.jdem846.render.RenderEngine;
import us.wthr.jdem846.tasks.RunnableTask;
import us.wthr.jdem846.tasks.TaskControllerService;
import us.wthr.jdem846.tasks.TaskStatusListener;
import us.wthr.jdem846.ui.FileSaveThread.SaveCompletedListener;
import us.wthr.jdem846.ui.ImageDisplayPanel.MousePositionListener;
import us.wthr.jdem846.ui.ModelingWorkerThread.ModelCompletionListener;
import us.wthr.jdem846.ui.OutputImageViewButtonBar.ButtonClickedListener;
import us.wthr.jdem846.ui.OutputImageViewButtonBar.OptionChangeListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Menu;
import us.wthr.jdem846.ui.base.MenuItem;
import us.wthr.jdem846.ui.base.ProgressBar;

@SuppressWarnings("serial")
public class OutputImageViewPanel extends JdemPanel implements Savable
{
	private static Log log = Logging.getLog(OutputImageViewPanel.class);
	
	private ModelCanvas canvas;
	
	private OutputImageViewButtonBar buttonBar;
	private ImageDisplayPanel imageDisplay;
	//private StatusBar statusBar;
	private ProcessWorkingSpinner spinner;
	
	//private DataPackage dataPackage;
	//private ModelOptions modelOptions;
	//private RenderEngine engine;
	private ModelContext modelContext;
	
	private boolean isWorking = false;
	private ProgressBar prgProgress;
	
	private RunnableTask renderTask;
	private TaskStatusListener taskStatusListener;
	//private ModelingWorkerThread worker;
	
	//private ModelCompletionListener modelCompletionListener = null;
	private TileCompletionListener tileCompletionListener = null;
	
	private Menu modelMenu;
	
	private String lastSavePath = null;
	
	private boolean showPreviews = true;
	
	public OutputImageViewPanel(final RenderEngine engine)
	{
		// Set Properties
		//this.canvas = canvas;
		showPreviews = JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.renderInProcessPreviewing");
		this.setLayout(new BorderLayout());
		//this.engine = engine;
		//this.dataPackage = engine.getDataPackage();
		//this.modelOptions = engine.getModelOptions();
		this.modelContext = engine.getModelContext();
		
		// Create components
		imageDisplay = new ImageDisplayPanel();
		buttonBar = new OutputImageViewButtonBar(this);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, true);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, true);
		buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
		
		prgProgress = new ProgressBar(0, 100);
		SharedStatusBar.addControl(prgProgress, this);
		
		//statusBar = new StatusBar();
		//statusBar.setProgressVisible(true);
		//bottomStatusLabel = new JLabel(" ");
		//bottomStatusLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		spinner = new ProcessWorkingSpinner();
		spinner.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		spinner.step();
		buttonBar.add( Box.createHorizontalGlue() );
		buttonBar.add(spinner);

		// Set the image to be zoomed all the way out so the user can see the image updating
		// as it is drawn.
		imageDisplay.setScalePercent(0.0);
		
		modelMenu = new ComponentMenu(this, I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu"), KeyEvent.VK_M);
		MainMenuBar.insertMenu(modelMenu);
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.save"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.save"), KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));

		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomIn"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomIn"), KeyEvent.VK_I, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomIn();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomOut"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomOut"), KeyEvent.VK_O, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomOut();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomFit"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomFit"), KeyEvent.VK_F, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomFit();
			}
		}));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.zoomActual"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomActual"), KeyEvent.VK_A, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onZoomActual();
			}
		}));
		
		modelMenu.add(new MenuItem(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.menu.stop"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.stop"), KeyEvent.VK_S, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onStop();
			}
		}));
		// Add listeners
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e)
			{
				modelMenu.setVisible(false);
			}
			public void componentShown(ComponentEvent e)
			{
				modelMenu.setVisible(true);
			}
		});
		
		
		buttonBar.addButtonClickedListener(new ButtonClickedListener() {
			public void onSaveClicked() {
				save();
			}
			public void onZoomActualClicked() {
				onZoomActual();
			}
			public void onZoomFitClicked() {
				onZoomFit();
			}
			public void onZoomInClicked() {
				onZoomIn();
			}
			public void onZoomOutClicked() {
				onZoomOut();
			}
			public void onStopClicked() {
				onStop();
			}
			public void onPauseClicked() {
				onPause();
			}
			public void onResumeClicked() {
				onResume();
			}
		});
		buttonBar.addOptionChangeListener(new OptionChangeListener() {
			public void onImageQualityChanged(int quality) {
				imageDisplay.setScaleQuality(quality);
				imageDisplay.repaint();
			}
		});
		
		imageDisplay.addMousePositionListener(new MousePositionListener() {
			public void onMousePositionChanged(int x, int y, double scaledPercent) {
				
				onMousePosition(x, y, scaledPercent);
			}
		});
		
		
		// Set layout
		MainButtonBar.addToolBar(buttonBar);
		//this.add(buttonBar, BorderLayout.NORTH);
		this.add(imageDisplay, BorderLayout.CENTER);
		//this.add(statusBar, BorderLayout.SOUTH);
		
		// Set up worker thread
		
		tileCompletionListener = new TileCompletionListener() {
			public void onTileCompleted(ModelCanvas modelCanvas, double pctComplete) {
				prgProgress.setValue((int)(pctComplete * 100));
				
				if (showPreviews) {
					imageDisplay.setImage(modelCanvas.getFinalizedImage());
					canvas = modelCanvas;
					repaint();
				}
			}
		};
		engine.addTileCompletionListener(tileCompletionListener);
		
		
		renderTask = new RunnableTask("Model Render Task") {
			
			public void run() throws RenderEngineException
			{
				log.info("Model rendering task starting");
				this.setStoppable(true);
				
				long start = 0;
				long elapsed = 0;
				
				/*
				start = System.currentTimeMillis();
				boolean requiresMinMaxElevation = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).requiresMinMaxElevation();
				try {
					if (requiresMinMaxElevation) {
						modelContext.getRasterDataContext().calculateElevationMinMax(true);
					}
				} catch (Exception ex) {
					throw new RenderEngineException("Error calculating elevation min/max: " + ex.getMessage(), ex);
				}
				elapsed = (System.currentTimeMillis() - start) / 1000;
				log.info("Completed elevation min/max task in " + elapsed + " seconds");
				*/

				start = System.currentTimeMillis();
				OutputProduct<ModelCanvas> product = engine.generate(false, false);
				elapsed = (System.currentTimeMillis() - start) / 1000;
				ModelCanvas demCanvas = product.getProduct();
				synchronized(imageDisplay) {
					imageDisplay.setImage(demCanvas.getFinalizedImage());
				}
				//if (demCanvas != null) {
				//	synchronized(canvas) {
						canvas = demCanvas;
				//	}
				//}
				log.info("Completed render task in " + elapsed + " seconds");

			}
			
			@Override
			public void cancel()
			{
				engine.cancel();
			}
			
			@Override
			public void pause()
			{
				engine.pause();
			}
			
			@Override
			public void resume()
			{
				engine.resume();
			}
		};
		
		taskStatusListener = new TaskStatusListener() {
			public void taskCancelled(RunnableTask task)
			{
				if (canvas != null) {
					taskCompleted(task);
				} else {
					onNonSuccessfulCompletion(task, null);
				}
				
			}
			public void taskCompleted(RunnableTask task)
			{
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, true);
				setWorking(false);
				//statusBar.setProgressVisible(false);
				SharedStatusBar.removeControl(prgProgress);
				spinner.stop();
				detachModelListeners(true);
				
				disposeModelingInformation();
				
				repaint();
			}
			public void taskFailed(RunnableTask task, Throwable thrown)
			{
				onNonSuccessfulCompletion(task, thrown);
			}
			
			protected void onNonSuccessfulCompletion(RunnableTask task, Throwable thrown) 
			{
				canvas = null;
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
				setWorking(false);
				//statusBar.setProgressVisible(false);
				SharedStatusBar.removeControl(prgProgress);
				spinner.stop();
				detachModelListeners(true);
				
				disposeModelingInformation();
				
				if (thrown != null) {
					JOptionPane.showMessageDialog(getRootPane(),
						    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.modelFailed.message") + ": " + thrown.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.modelFailed.title"),
						    JOptionPane.ERROR_MESSAGE);
				}
				
				
				repaint();
			}
			
			public void taskStarting(RunnableTask task)
			{
				
			}
			
			public void taskPaused(RunnableTask task)
			{
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, true);
			}
			
			public void taskResumed(RunnableTask task)
			{
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, true);
				buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			}
		};
			

	}
	
	protected void disposeModelingInformation()
	{
		if (!modelContext.isDisposed()) {
			log.info("Disposing of model context information...");
			
			try {
				modelContext.dispose(true);
			} catch (DataSourceException ex) {
				log.info("Error disposing of model context information: " + ex.getMessage(), ex);
			}
		}
	}
	
	@Override
	public void dispose() throws ComponentException
	{
		log.info("Closing output image pane.");
		detachModelListeners(false);
		MainMenuBar.removeMenu(modelMenu);
		MainButtonBar.removeToolBar(buttonBar);
	}

	
	public void detachModelListeners(boolean delayed)
	{
		
		if (delayed) {
			Timer timer = new Timer(500, new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					detachModelListeners(false);
				}
			});
			timer.setRepeats(false);
			timer.start();
		} else {
			//if (modelCompletionListener != null) {
				//worker.removeModelCompletionListener(modelCompletionListener);
			//	modelCompletionListener = null;
			//}
			
			//if (tileCompletionListener != null) {
				//worker.removeTileCompletionListener(tileCompletionListener);
			//	tileCompletionListener = null;
			//}
		}
	}
	
	public void startWorker()
	{
		if (!this.isWorking) {
			TaskControllerService.addTask(renderTask, taskStatusListener);
			setWorking(true);
			spinner.start();
		}
		
		//if (worker != null) {
		//	setWorking(true);
		//	spinner.start();
		//	worker.start();
		//}
	}
	
	
	
	public void onStop()
	{
		log.info("Render process requested to stop");
		TaskControllerService.cancelTask(renderTask);
	}
	
	public void onPause()
	{
		log.info("Render process requested to pause");
		TaskControllerService.pauseTask(renderTask);
	}
	
	public void onResume()
	{
		log.info("Render process requested to resume");
		TaskControllerService.resumeTask(renderTask);
	}
	
	public void onZoomActual() 
	{
		imageDisplay.zoomActual();
	}
	public void onZoomFit()
	{
		imageDisplay.zoomFit();
	}
	public void onZoomIn()
	{
		imageDisplay.zoomIn();
	}
	public void onZoomOut() 
	{
		imageDisplay.zoomOut();
	}
	
	protected void onMousePosition(int x, int y, double scaledPercent)
	{
		if (isWorking()) {
			return;
		}
		
		int trueX = (int) Math.round((double)x / scaledPercent);
		int trueY = (int) Math.round((double)y / scaledPercent);
		
		if (trueX == -1 || trueY == -1) {
			//statusBar.setStatus(" ");
			SharedStatusBar.setStatus("");
			return;
		}
		
		/*
		double mouseLatitude = modelContext.getRasterDataContext().rowToLatitude(trueY);
		double mouseLongitude = modelContext.getRasterDataContext().columnToLongitude(trueX);
		
		DecimalFormat formatter = new DecimalFormat(I18N.get("us.wthr.jdem846.ui.decimalFormat"));
		String strMouseLatitude = formatter.format(mouseLatitude);
		String strMouseLongitude = formatter.format(mouseLongitude);;
		
		
		double elevation = 0;
		try {
			elevation = modelContext.getRasterDataContext().getData(mouseLatitude, mouseLongitude);
		} catch (DataSourceException ex) {
			log.warn("Failed to retrieve elevation data: " + ex.getMessage(), ex);
		}
		if (elevation == DemConstants.ELEV_NO_DATA)
			elevation = 0;
		
		statusBar.setStatus("X/Y: " + trueX + " / " + trueY + ", " + I18N.get("us.wthr.jdem846.ui.latitude") + "/" + I18N.get("us.wthr.jdem846.ui.longitude") + ": " + strMouseLatitude + " / " + strMouseLongitude + ", " + I18N.get("us.wthr.jdem846.ui.elevation") + ": " + elevation);
		*/
		
	}


	public boolean isWorking() 
	{
		return isWorking;
	}


	protected void setWorking(boolean isWorking) 
	{
		this.isWorking = isWorking;
	}

	@Override
	public void save()
	{
		if (lastSavePath == null) {
			saveAs();
		} else {
			saveTo(lastSavePath);
		}
		
	}

	@Override
	public void saveAs()
	{
		log.info("Save");
		
		FileChooser chooser = new FileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.png"), "png");
		chooser.setFileFilter(filter);
		
		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.jpeg"), "jpg", "jpeg");
		chooser.setFileFilter(filter);
		
		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.supportedTypes"), "png", "jpg", "jpeg");
		chooser.setFileFilter(filter);
		
		
	   // int returnVal = chooser.showOpenDialog(this);
	    int returnVal = chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filePath = chooser.getSelectedFile().getAbsolutePath();
	    	
	    	//if (!filePath.toLowerCase().endsWith(".png")) {
	    	//	filePath = filePath + ".png";
    		//}
	    	
	    	log.info("Saving to: " + filePath);
	    	saveTo(filePath);
	    	//canvas.save(filePath);
	    	log.info("Done Save");
	    }
	}
	

	
	protected void saveTo(String path) 
	{
		FileSaveThread saveThread = new FileSaveThread(canvas.getFinalizedImage(), path);
		saveThread.addSaveCompletedListener(new SaveCompletedListener() {
			public void onSaveSuccessful()
			{
				JOptionPane.showMessageDialog(getRootPane(),
					    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveSuccess.message"),
					    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveSuccess.title"),
					    JOptionPane.INFORMATION_MESSAGE);
			}
			public void onSaveFailed(Exception ex)
			{
				JOptionPane.showMessageDialog(getRootPane(),
					    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveFailed.message") + ": " + ex.getMessage(),
					    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveFailed.title"),
					    JOptionPane.ERROR_MESSAGE);
			}
		});
		saveThread.start();
		lastSavePath = path;
	}

	
}
