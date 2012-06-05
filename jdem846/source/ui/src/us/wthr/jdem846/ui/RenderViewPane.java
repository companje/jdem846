package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.tasks.RunnableTask;
import us.wthr.jdem846.tasks.TaskControllerService;
import us.wthr.jdem846.tasks.TaskStatusListener;
import us.wthr.jdem846.ui.FileSaveThread.SaveCompletedListener;
import us.wthr.jdem846.ui.ImageDisplayPanel.MousePositionListener;
import us.wthr.jdem846.ui.base.FileChooser;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.SplitPane;
import us.wthr.jdem846.ui.base.TabPane;
import us.wthr.jdem846.ui.histogram.DistributionGenerator;
import us.wthr.jdem846.ui.histogram.ElevationHistogram;
import us.wthr.jdem846.ui.histogram.TonalHistogram;
import us.wthr.jdem846.ui.histogram.TonalHistogramModel;

@SuppressWarnings("serial")
public class RenderViewPane extends Panel
{
	
	private static Log log = Logging.getLog(RenderViewPane.class);
	
	
	protected static final int NOT_STARTED = 0;
	protected static final int COMPLETED = 1;
	protected static final int FAILED = 2;
	protected static final int CANCELED = 3;
	protected static final int RUNNING = 4;
	protected static final int PAUSED = 5;

	
	
	private int currentState = NOT_STARTED;
	private RunnableTask renderTask;
	private TaskStatusListener taskStatusListener;
	
	private ImageDisplayPanel imageDisplay;
	private TonalHistogram histogramDisplay;
	private ElevationHistogram elevationHistogram;
	
	private ModelCanvas canvas;
	private ModelContext modelContext;
	private boolean isWorking = false;
	private boolean showPreviews = true;
	
	private String lastSavePath = null;

	private ModelBuilder modelBuilder;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public RenderViewPane(final ModelContext modelContext)
	{
		this.modelContext = modelContext;
		
		showPreviews = JDem846Properties.getBooleanProperty("us.wthr.jdem846.general.ui.renderInProcessPreviewing");
		
		imageDisplay = new ImageDisplayPanel();
		// Set the image to be zoomed all the way out so the user can see the image updating
		// as it is drawn.
		imageDisplay.setScalePercent(0.0);
		
		
		
		imageDisplay.addMousePositionListener(new MousePositionListener() {
			public void onMousePositionChanged(int x, int y, double scaledPercent) {
				
				onMousePosition(x, y, scaledPercent);
			}
		});
		
		histogramDisplay = new TonalHistogram();
		histogramDisplay.setPreferredSize(new Dimension(255, 150));
		
		elevationHistogram = new ElevationHistogram();
		elevationHistogram.setPreferredSize(new Dimension(255, 150));
		
		//BorderFactory.createEtchedBorder()
		//histogramDisplay.setBorder(BorderFactory.createTitledBorder("RGB Histogram"));
		
		TabPane toolsPanel = new TabPane();
		toolsPanel.addTab("Tools", new Label("Tools go here."));
		toolsPanel.addTab("RGB Histogram", histogramDisplay);
		toolsPanel.addTab("Elevation Histogram", elevationHistogram);
		
		Panel southPanel = new Panel();
		southPanel.setLayout(new BorderLayout());
		//southPanel.add(histogramDisplay, BorderLayout.EAST);
		southPanel.setPreferredSize(new Dimension(400, 150));
		southPanel.add(toolsPanel, BorderLayout.CENTER);
		
		
		setLayout(new BorderLayout());
		
		
		

		this.add(imageDisplay, BorderLayout.CENTER);
		this.add(southPanel, BorderLayout.SOUTH);
		
		
		
		
		modelBuilder = new ModelBuilder();
		renderTask = new RunnableTask("Model Render Task") {
			
			public void run() throws RenderEngineException
			{
				log.info("Model rendering task starting");
				this.setStoppable(true);
				
				long start = 0;
				long elapsed = 0;
				
				ModelProcessManifest modelProcessManifest = modelContext.getModelProcessManifest();
				modelProcessManifest.getGlobalOptionModel().setDisposeGridOnComplete(false);
				//ModelGridDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);
				log.info("Initializing model builder...");
				modelBuilder.prepare(modelContext, modelProcessManifest);
				
				log.info("Processing...");
				start = System.currentTimeMillis();
				modelBuilder.process();
				

				elapsed = (System.currentTimeMillis() - start) / 1000;
				
				canvas = modelContext.getModelCanvas();
				
				BufferedImage modelImage = (BufferedImage) canvas.getImage();
				boolean[][] modelMask = canvas.getModelMask();
				
				synchronized(imageDisplay) {
					imageDisplay.setImage(modelImage);
					imageDisplay.zoomFit();
				}
				
				TonalHistogramModel histogramModel = DistributionGenerator.generateHistogramModelFromImage(modelImage, modelMask);
				histogramDisplay.setHistogramModel(histogramModel);
				
				ElevationHistogramModel elevationHistogramModel = modelBuilder.getModelGrid().getElevationHistogramModel();
				elevationHistogram.setElevationHistogramModel(elevationHistogramModel);
				
				/*
				int min = (int) MathExt.round(elevationHistogramModel.getMinimum());
				int max = (int) MathExt.round(elevationHistogramModel.getMaximum());
				int step = (int) MathExt.round(((double)max - (double)min) / (double)100);
				
				for (int e = min; e <= max; e+=step) {
					int c = elevationHistogramModel.getCountWithinElevationRange(e, e+step-1);
					log.info("" + e + ": " + c);
				}

				log.info("Max: " + elevationHistogramModel.getMaximumCount(step));
				log.info("Min: " + elevationHistogramModel.getMinimumCount(step));
				*/
				
				modelBuilder.dispose();
				modelBuilder = null;
				
				log.info("Completed render task in " + elapsed + " seconds");

			}
			
			@Override
			public void cancel()
			{
				modelBuilder.cancel();
			}
			
			@Override
			public void pause()
			{
				modelBuilder.pause();
			}
			
			@Override
			public void resume()
			{
				modelBuilder.resume();
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
				currentState = RenderViewPane.COMPLETED;
				
				detachModelListeners(true);
				
				disposeModelingInformation();
				
				setWorking(false);
				
				fireChangeListeners();
				
				repaint();
			}
			public void taskFailed(RunnableTask task, Throwable thrown)
			{
				canvas = null;
				currentState = RenderViewPane.FAILED;
				
				detachModelListeners(true);
				disposeModelingInformation();
				
				if (thrown != null) {
					JOptionPane.showMessageDialog(getRootPane(),
						    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.modelFailed.message") + ": " + thrown.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.modelFailed.title"),
						    JOptionPane.ERROR_MESSAGE);
				}
				setWorking(false);
				
				fireChangeListeners();
				
				repaint();
			}
			
			protected void onNonSuccessfulCompletion(RunnableTask task, Throwable thrown) 
			{
				canvas = null;
				
				currentState = RenderViewPane.CANCELED;
				
				
				detachModelListeners(true);
				
				disposeModelingInformation();
				
				if (thrown != null) {
					JOptionPane.showMessageDialog(getRootPane(),
						    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.modelFailed.message") + ": " + thrown.getMessage(),
						    I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.modelFailed.title"),
						    JOptionPane.ERROR_MESSAGE);
				}
				
				setWorking(false);
				
				fireChangeListeners();
				
				repaint();
			}
			
			public void taskStarting(RunnableTask task)
			{
				currentState = RenderViewPane.RUNNING;
				fireChangeListeners();
			}
			
			public void taskPaused(RunnableTask task)
			{
				currentState = RenderViewPane.PAUSED;
				fireChangeListeners();
			}
			
			public void taskResumed(RunnableTask task)
			{
				currentState = RenderViewPane.RUNNING;
				fireChangeListeners();
			}
		};
		
	}
	
	@Override
	public void dispose() throws ComponentException
	{
		log.info("Closing output image pane.");
		detachModelListeners(false);
	}
	
	public void startWorker()
	{
		if (!this.isWorking) {
			TaskControllerService.addTask(renderTask, taskStatusListener);
			setWorking(true);
			//spinner.start();
			//JdemFrame.getInstance().setGlassVisible("Working...", this, true);
		}
		

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
	
	protected void detachModelListeners(boolean delayed)
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

		}
	}
	
	
	public void setButtonBarState(OutputImageViewButtonBar buttonBar)
	{
		setButtonBarState(buttonBar, currentState);
	}
	
	protected void setButtonBarState(OutputImageViewButtonBar buttonBar, int state)
	{
		
		currentState = state;
		
		int scaleQuality = imageDisplay.getScaleQuality();
		buttonBar.setSelectedImageQuality(scaleQuality);
		
		switch (currentState) {
		case RenderViewPane.NOT_STARTED:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			
			break;
		case RenderViewPane.COMPLETED:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			
			break;
		case RenderViewPane.FAILED:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			
			break;
		case RenderViewPane.CANCELED:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			
			break;
		case RenderViewPane.RUNNING:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			
			break;
		case RenderViewPane.PAUSED:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, true);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, true);
			
			break;
		default:
			
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_SAVE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_IN, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_OUT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_ACTUAL, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_ZOOM_FIT, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.OPTION_QUALITY, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_STOP, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_PAUSE, false);
			buttonBar.setComponentEnabled(OutputImageViewButtonBar.BTN_RESUME, false);
			
			
		}
		
	}
	
	
	
	public void onImageQualityChanged(int quality) 
	{
		imageDisplay.setScaleQuality(quality);
		imageDisplay.repaint();
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
	
	public boolean isWorking() 
	{
		return isWorking;
	}


	protected void setWorking(boolean isWorking) 
	{
		this.isWorking = isWorking;
		
		if (isWorking) {
			JdemFrame.getInstance().addShadedComponent(this, "Working...");
		} else {
			JdemFrame.getInstance().removeShadedComponent(this);
		}
		
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

		
	}
	

	
	public void save()
	{
		if (lastSavePath == null) {
			saveAs();
		} else {
			saveTo(lastSavePath);
		}
		
	}

	
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
		FileSaveThread saveThread = new FileSaveThread(canvas.getImage(), path);
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
	
	
	public void addChangeListener(ChangeListener l)
	{
		changeListeners.add(l);
	}
	
	public boolean removeChangeListener(ChangeListener l)
	{
		return changeListeners.remove(l);
	}
	
	public void fireChangeListeners()
	{
		
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(e);
		}
		
	}
	
}
