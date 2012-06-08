package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ProjectMarshalException;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.ProjectMarshaller;
import us.wthr.jdem846.project.ProjectTypeEnum;
import us.wthr.jdem846.tasks.RunnableTask;
import us.wthr.jdem846.tasks.TaskControllerService;
import us.wthr.jdem846.tasks.TaskStatusListener;
import us.wthr.jdem846.ui.FileSaveThread.SaveCompletedListener;
import us.wthr.jdem846.ui.ImageDisplayPanel.MousePositionListener;
import us.wthr.jdem846.ui.ImageToolButtonGridPanel.ClickListener;
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
	
	private SplitPane viewSplit;
	private Panel componentPanel;
	
	//private ModelCanvas canvas;
	private JDemElevationModel jdemElevationModel;
	private ModelContext modelContext;
	private boolean isWorking = false;
	private boolean showPreviews = true;
	

	private ModelBuilder modelBuilder;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	private Coordinate latitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LATITUDE);
    private Coordinate longitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LONGITUDE);

    private int lastMouseX = -1;
    private int lastMouseY = -1;

    private String lastExportPath = null;
    
    public RenderViewPane(ModelContext _modelContext)
	{
    	this(_modelContext, null);
	}
    
    public RenderViewPane(JDemElevationModel jdemElevationModel)
	{
    	this(null, jdemElevationModel);
	}
    
    
	public RenderViewPane(ModelContext _modelContext, JDemElevationModel jdemElevationModel)
	{
		this.modelContext = _modelContext;
		
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
		

		ImageToolButtonGridPanel toolButtonGrid = new ImageToolButtonGridPanel();
		toolButtonGrid.addClickListener(new ClickListener() {
			public void onButtonClicked(String action)
			{
				log.info("Action button clicked: " + action);
				JOptionPane.showMessageDialog(getRootPane(),
						I18N.get("us.wthr.jdem846.ui.notYetImplemented.message"),
					    I18N.get("us.wthr.jdem846.ui.notYetImplemented.title"),
					    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		TabPane toolsPanel = new TabPane();
		toolsPanel.addTab("Quick Tools", toolButtonGrid);

		
		histogramDisplay.setBorder(BorderFactory.createTitledBorder("RGB Histogram"));
		elevationHistogram.setBorder(BorderFactory.createTitledBorder("Elevation Histogram"));
		
		componentPanel = new Panel();
		componentPanel.setLayout(new BoxLayout(componentPanel, BoxLayout.Y_AXIS));
		componentPanel.add(toolsPanel);
		componentPanel.add(histogramDisplay);
		componentPanel.add(elevationHistogram);

		viewSplit = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
		viewSplit.setBorder(BorderFactory.createEmptyBorder());
		viewSplit.setResizeWeight(1.0);
		viewSplit.add(imageDisplay);
		viewSplit.add(componentPanel);
		
		setLayout(new BorderLayout());
		
		
		

		this.add(viewSplit, BorderLayout.CENTER);

		viewSplit.setDividerLocation(viewSplit.getWidth() - JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.renderViewPane.mainSplitPosition"));
		
		ComponentListener splitHandler = new ComponentAdapter() {
		
			
			private boolean initialSizeSet = false;
			
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (initialSizeSet) {
					int location = componentPanel.getWidth();
					JDem846Properties.setProperty("us.wthr.jdem846.state.ui.renderViewPane.mainSplitPosition", location);
				}
			}
			
			@Override
			public void componentShown(ComponentEvent e)
			{
				if (!initialSizeSet) {
					viewSplit.setDividerLocation(viewSplit.getWidth() - JDem846Properties.getIntProperty("us.wthr.jdem846.state.ui.renderViewPane.mainSplitPosition"));
					initialSizeSet = true;
				}
			}
			
		};
		this.addComponentListener(splitHandler);
		imageDisplay.addComponentListener(splitHandler);
		//imageDisplay.setBackground(Color.WHITE);
		
		if (jdemElevationModel != null) {
			initializeWithJDemElevationModel(jdemElevationModel);
			currentState = RenderViewPane.COMPLETED;
		} else if (modelContext != null) {
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
					JDemElevationModel jdemElevationModel = modelBuilder.process();
					
	
					elapsed = (System.currentTimeMillis() - start) / 1000;
	
					//canvas = modelContext.getModelCanvas();
					
					initializeWithJDemElevationModel(jdemElevationModel);
					
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
					//if (jdemElevationModel != null) {
					//	taskCompleted(task);
					//} else {
						onNonSuccessfulCompletion(task, null);
				//	}
					
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
					//jdemElevationModel = null;
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
					//jdemElevationModel = null;
					
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
	}
	
	
	
	
	protected void initializeWithJDemElevationModel(JDemElevationModel jdemElevationModel)
	{
		this.jdemElevationModel = jdemElevationModel;
		
		BufferedImage modelImage = (BufferedImage) jdemElevationModel.getImage();

		synchronized(imageDisplay) {
			imageDisplay.setImage(modelImage);
			imageDisplay.zoomFit();
		}
		
		if (!imageDisplay.isVisible()) {
			imageDisplay.addComponentListener(new ComponentAdapter() {
				
				private boolean shownOnce = false;
				@Override
				public void componentShown(ComponentEvent e)
				{
					if (!shownOnce) {
						imageDisplay.zoomFit();
						shownOnce = true;
					}
				}
				
			});
		}
		
		TonalHistogramModel histogramModel = DistributionGenerator.generateHistogramModelFromImage(jdemElevationModel);
		histogramDisplay.setHistogramModel(histogramModel);
		
		ElevationHistogramModel elevationHistogramModel = jdemElevationModel.getElevationHistogramModel();
		//ElevationHistogramModel elevationHistogramModel = modelBuilder.getModelGrid().getElevationHistogramModel();
		if (elevationHistogramModel != null) {
			elevationHistogram.setElevationHistogramModel(elevationHistogramModel);
		}
		
		
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

		updateStatus(x, y, scaledPercent);

		lastMouseX = x;
		lastMouseY = y;
	}
	
	protected void updateStatus(int mouseX, int mouseY, double scaledPercent)
	{
		
		if (isWorking()) {
			return;
		}
		
		
		int paintedX = imageDisplay.getPaintedImageBounds().x;
		int paintedY = imageDisplay.getPaintedImageBounds().y;
		
		int trueX = (int) Math.round((double)(mouseX - paintedX) / scaledPercent);
		int trueY = (int) Math.round((double)(mouseY - paintedY) / scaledPercent);

		if (jdemElevationModel != null && jdemElevationModel.getMask(trueX, trueY)) {
			
			double latitude = jdemElevationModel.getLatitude(trueX, trueY);
			double longitude = jdemElevationModel.getLongitude(trueX, trueY);
			double elevation = jdemElevationModel.getElevation(trueX, trueY);
			
			latitudeCoordinate.fromDecimal(latitude);
			longitudeCoordinate.fromDecimal(longitude);
			
			String status = ""+latitudeCoordinate + "  " + longitudeCoordinate + "  elev " + ((int)MathExt.round(elevation)) + "m";
			
			imageDisplay.setStatus(status);
		} else {
			imageDisplay.setStatus(null);
		}
		
		imageDisplay.repaint();
		
	}

	
	
	public JDemElevationModel getJdemElevationModel()
	{
		return jdemElevationModel;
	}

	public void save()
	{
		if (lastExportPath == null) {
			saveAs();
		} else {
			saveTo(lastExportPath);
		}
		
	}

	
	public void saveAs()
	{
		log.info("Save");
		
		FileChooser chooser = new FileChooser();
		FileNameExtensionFilter filter = null;
		
		
		

		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.jdemimg"), "jdemimg");
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		
		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.jpeg"), "jpg", "jpeg");
		chooser.addChoosableFileFilter(filter);
		
		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.png"), "png");
		chooser.addChoosableFileFilter(filter);
		
		filter = new FileNameExtensionFilter(I18N.get("us.wthr.jdem846.ui.outputImageViewPanel.saveImage.supportedTypes"), "png", "jpg", "jpeg");
		chooser.addChoosableFileFilter(filter);

	    int returnVal = chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	String filePath = chooser.getSelectedFile().getAbsolutePath();
	    	

	    	log.info("Saving to: " + filePath);
	    	saveTo(filePath);
	    	//canvas.save(filePath);
	    	log.info("Done Save");
	    }
	}
	
	protected void saveTo(String path) 
	{
		String extension = path.substring(path.lastIndexOf("."));
		if (extension == null) {
			extension = ".jdemimg";
			path += extension;
		}
		
		if (extension.toLowerCase().equals(".jdemimg")) {
			saveDemImageTo(path);
		} else {
			saveImageTo(path);
		}
		
	}
	
	protected void saveDemImageTo(String path) 
	{
		try {
			ProjectMarshall projectMarshall = ProjectMarshaller.marshallProject(null);
			
			projectMarshall.setProjectType(ProjectTypeEnum.DEM_IMAGE);
			projectMarshall.getElevationModels().add(this.jdemElevationModel);
			
			
			ProjectFiles.write(projectMarshall, path);
			
			lastExportPath = path;
			
			RecentProjectTracker.addProject(path);
			
			log.info("Project file saved to " + path);
			SharedStatusBar.setStatus("Project file saved to " + path);
			
			
			
		} catch (Exception ex) {
			log.warn("Error trying to write project to disk: " + ex.getMessage(), ex);
			JOptionPane.showMessageDialog(getRootPane(),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.message"),
				    I18N.get("us.wthr.jdem846.ui.jdemFrame.saveError.writeError.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
	}
	
	protected void saveImageTo(String path) 
	{
		FileSaveThread saveThread = new FileSaveThread(jdemElevationModel.getImage(), path);
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
		lastExportPath = path;
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
