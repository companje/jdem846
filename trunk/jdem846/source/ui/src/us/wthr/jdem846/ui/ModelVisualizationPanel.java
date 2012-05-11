package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.PropertiesChangeListener;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.simple.SimpleRenderer;
import us.wthr.jdem846.ui.base.CheckBox;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.Slider;
import us.wthr.jdem846.ui.panels.RoundedPanel;
import us.wthr.jdem846.util.ColorSerializationUtil;

@SuppressWarnings("serial")
public class ModelVisualizationPanel extends Panel
{
	
	private static Log log = Logging.getLog(ModelVisualizationPanel.class);
	
	private ModelContext modelContextActual;
	private ModelContext modelContextWorkingCopy;
	
	private ImageDisplayPanel pnlModelDisplay;
	private Slider sldQuality;
	private CheckBox chkPreviewRaster;
	private ToolbarButton btnUpdate;
	private CheckBox chkAutoUpdate;
	private CheckBox chkScripting;
	
	int buttonDown = -1;
	int downX = -1;
	int downY = -1;
	int lastX = -1;
	int lastY = -1;
	
	double rotateX;
	double rotateY;
	double rotateZ;
	
	double shiftZ;
	double shiftX;
	double shiftY;
	
	double zoom;
	
	
	boolean rasterPreview = true;
	double maxPreviewSlices = 300;
	double minPreviewSlices = 10;
	double previewQuality = 0.25;
	double latitudeSlices;
	double longitudeSlices;

	private List<ProjectionChangeListener> projectionChangeListeners = new LinkedList<ProjectionChangeListener>();
	
	private boolean ignoreUpdate = false;
	private boolean autoUpdate = true;
	private boolean useScripting = true;
	
	/*
	 * Holy long variable names, Batman!
	 */
	private boolean lastRerenderCancelledButNeededDataRangeUpdate = false;
	private boolean lastRerenderCancelledButNeededCacheReset = false;
	
	private ModelBuilder modelBuilder = null;
	
	public ModelVisualizationPanel(ModelContext modelContext)
	{
		super();
		this.modelContextActual = modelContext;
		
		// Set default values
		latitudeSlices = JDem846Properties.getDoubleProperty("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices");
		longitudeSlices = JDem846Properties.getDoubleProperty("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices");
				
		maxPreviewSlices = JDem846Properties.getDoubleProperty("us.wthr.jdem846.ui.modelVisualizationPanel.maxPreviewSlices");
		minPreviewSlices = JDem846Properties.getDoubleProperty("us.wthr.jdem846.ui.modelVisualizationPanel.minPreviewSlices");
		previewQuality = JDem846Properties.getDoubleProperty("us.wthr.jdem846.previewing.ui.previewQuality");
		rasterPreview = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.rasterPreview");
		autoUpdate = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.autoUpdate");
		useScripting = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.scripting");
		
		ViewPerspective viewAngle = null;
		
		try {
			viewAngle = (ViewPerspective) modelContextActual.getModelProcessManifest().getPropertyById("us.wthr.jdem846.model.ModelRenderOptionModel.viewAngle");
		} catch (ModelContainerException ex) {
			log.error("Error fetching value for view angle from model process manifest: " + ex.getMessage(), ex);
		}
		

		rotateX = (viewAngle != null) ? viewAngle.getRotateX() : 30;
		rotateY = (viewAngle != null) ? viewAngle.getRotateY() : 0;
		rotateZ = (viewAngle != null) ? viewAngle.getRotateZ() : 0;
		shiftX = (viewAngle != null) ? viewAngle.getShiftX() : 0;
		shiftY = (viewAngle != null) ? viewAngle.getShiftY() : 0;
		shiftZ = (viewAngle != null) ? viewAngle.getShiftZ() : 0;
		zoom = (viewAngle != null) ? viewAngle.getZoom() : 1.0;

		
		try {
			modelContextWorkingCopy = modelContextActual.copy();
			setWorkingCopyOptions();
		} catch (DataSourceException ex) {
			log.error("Failed to copy model context: " + ex.getMessage(), ex);
			return;
		}
		

		// Create components
		pnlModelDisplay = new ImageDisplayPanel();
		pnlModelDisplay.setAllowZooming(false);
		pnlModelDisplay.zoomFit();
		
		sldQuality = new Slider(1, 100);
		chkPreviewRaster = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.previewRaster.label"));
		chkAutoUpdate = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.autoUpdate.label"));
		chkScripting = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.scripting.label"));
		btnUpdate = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.update.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.preview.refresh"), new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				update(true, true, true);
			}
		});
		
		chkPreviewRaster.setOpaque(false);
		chkAutoUpdate.setOpaque(false);
		sldQuality.setOpaque(false);
		chkScripting.setOpaque(false);
		
		// Set Tooltips
		
		sldQuality.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.quality.tooltip"));
		chkPreviewRaster.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.previewRaster.tooltip"));
		chkAutoUpdate.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.autoUpdate.tooltip"));
		chkScripting.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.scripting.tooltip"));
		btnUpdate.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.update.tooltip"));
		
		// Add listeners
		
		sldQuality.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				
				if (!source.getValueIsAdjusting()) {
					onQualitySliderChanged();
				}
			}
		});
		
		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0)
			{
				
			}
			public void componentMoved(ComponentEvent arg0)
			{
				
			}
			public void componentResized(ComponentEvent arg0)
			{
				update(false, false);
			}
			public void componentShown(ComponentEvent arg0)
			{
				update(false, false);
			}
		});
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mousePressed(MouseEvent e) 
			{
				buttonDown = e.getButton();
				downX = e.getX();
				downY = e.getY();
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				onMouseWheelMoved(e);
				
				
			}
			public void mouseDragged(MouseEvent e)
			{
				onMouseDragged(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				buttonDown = -1;
				lastX = -1;
				lastY = -1;
				downX = -1;
				downY = -1;
				
				ignoreUpdate = true;
				fireProjectionChangeListeners();
				ignoreUpdate = false;
				pnlModelDisplay.repaint();
			}
		};
		pnlModelDisplay.addMouseListener(mouseAdapter);
		pnlModelDisplay.addMouseMotionListener(mouseAdapter);
		pnlModelDisplay.addMouseWheelListener(mouseAdapter);
		chkPreviewRaster.getModel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				onRasterPreviewCheckChanged();
			}
		});
		chkAutoUpdate.getModel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				onAutoUpdateCheckChanged();
			}
		});
		chkScripting.getModel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				onScriptingCheckChanged();
			}
		});
		
		JDem846Properties.addPropertiesChangeListener(new PropertiesChangeListener() {
			public void onPropertyChanged(String property, String oldValue, String newValue)
			{
				if (property.equals("us.wthr.jdem846.previewing.ui.rasterPreview")) {
					rasterPreview = Boolean.parseBoolean(newValue);
					update(false, false);
					setControlState();
				} else if (property.equals("us.wthr.jdem846.previewing.ui.previewQuality")) {
					previewQuality = Double.parseDouble(newValue);
					update(false, false);
					setControlState();
				} else if (property.equals("us.wthr.jdem846.previewing.ui.autoUpdate")) {
					autoUpdate = Boolean.parseBoolean(newValue);
					setControlState();
				}
				
			}
		});
		
		Panel pnlControls = new Panel();
		pnlControls.setLayout(new FlowLayout());
		
		ComponentButtonBar buttonBar = new ComponentButtonBar(null);
		
		
		
		
		buttonBar.add(new Label(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.quality.label") + ":"));
		buttonBar.add(sldQuality);
		buttonBar.addSeparator();
		buttonBar.add(chkPreviewRaster);
		buttonBar.addSeparator();
		buttonBar.add(chkScripting);
		buttonBar.addSeparator();
		buttonBar.add(chkAutoUpdate);
		buttonBar.add(btnUpdate);
		
		
		// Set Layout
		setLayout(new BorderLayout());
		add(pnlModelDisplay, BorderLayout.CENTER);
		add(buttonBar, BorderLayout.SOUTH);
		
		setControlState();
	}
	
	
	protected void setControlState()
	{
		int value = (int) Math.round(previewQuality * 100);
		sldQuality.setValue(value);
		
		chkPreviewRaster.setSelected(rasterPreview);
		chkAutoUpdate.setSelected(autoUpdate);
		chkScripting.setSelected(useScripting);
		
	}

	protected void onScriptingCheckChanged()
	{
		useScripting = chkScripting.getModel().isSelected();
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.scripting", ""+useScripting);
	}
	
	protected void onAutoUpdateCheckChanged()
	{
		autoUpdate = chkAutoUpdate.getModel().isSelected();
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.autoUpdate", ""+autoUpdate);
	}
	
	protected void onRasterPreviewCheckChanged()
	{
		rasterPreview = chkPreviewRaster.getModel().isSelected();
		//update(false, false);
		
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.rasterPreview", ""+rasterPreview);
		
	}
	
	protected void onQualitySliderChanged()
	{
		double value = (double) sldQuality.getValue();
		previewQuality = (value / 100);
		//update(true, false);
		
		JDem846Properties.setProperty("us.wthr.jdem846.previewing.ui.previewQuality", ""+previewQuality);
	}
	
	protected void setWorkingCopyOptions()
	{

		GlobalOptionModel globalOptionModel = modelContextWorkingCopy.getModelProcessManifest().getGlobalOptionModel();

		globalOptionModel.setMaintainAspectRatio(false);
		globalOptionModel.setSubpixelGridSize(1);
		
		try {
			modelContextWorkingCopy.updateContext();
		} catch (ModelContextException ex) {
			// TODO Display error message dialog
			log.error("Exception updating model context: " + ex.getMessage(), ex);
		}
	}
	
	protected void onMouseWheelMoved(MouseWheelEvent e)
	{
		log.info("Scroll: " + e.getScrollAmount() + ", " + e.getWheelRotation());
		
		zoom += (e.getWheelRotation() * -0.1);
		
		update(false, false);
		
		ignoreUpdate = true;
		fireProjectionChangeListeners();
		ignoreUpdate = false;
	}
	
	protected void onMouseDragged(MouseEvent e)
	{
		if (buttonDown == 1) {
			onMouseDraggedLeftButton(e);
		} else if (buttonDown == 2) {
			onMouseDraggedMiddleButton(e);
		} else if (buttonDown == 3) {
			onMouseDraggedRightButton(e);
		}
		
	}
	
	protected void onMouseDraggedRightButton(MouseEvent e)
	{
		
		lastX = e.getX();
		lastY = e.getY();
		pnlModelDisplay.repaint();
	}
	
	protected void onMouseDraggedMiddleButton(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		if (lastX != -1 && lastY != -1) {
			
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			
			//shiftZ += (deltaY * 5);
			shiftX += (deltaX * .01);
			shiftY -= (deltaY * .01);
			
		}
		
		lastX = x;
		lastY = y;
			
		update(false, false);
	}
	
	protected void onMouseDraggedLeftButton(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		if (lastX != -1 && lastY != -1) {
			
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			

			rotateX += ((deltaY * 1) / zoom);
			rotateY += ((deltaX * 1) / zoom);
		}
		
		lastX = x;
		lastY = y;
		
		update(false, false);
	}
	
	public void update(boolean dataModelChange, boolean optionsChanged)
	{
		update(dataModelChange, optionsChanged, false);
	}
	
	public void update(boolean dataModelChange, boolean optionsChanged, boolean force)
	{
		if (ignoreUpdate) {
			return;
		}
		
		if (!autoUpdate && !force) {
			return;
		}

		
		
		if (optionsChanged || dataModelChange) {
			log.info("Model visualization: update working context from actual");
			
			if (modelContextWorkingCopy != null) {
				log.info("Disposing of working copy of model context information...");
				
				try {
					modelContextWorkingCopy.dispose(true);
				} catch (DataSourceException ex) {
					log.error("Error disposing of working copy of model context information: " + ex.getMessage(), ex);
				}
			}
			
			try {
				modelContextWorkingCopy = modelContextActual.copy();
				setWorkingCopyOptions();
			} catch (DataSourceException ex) {
				log.error("Failed to copy model context: " + ex.getMessage(), ex);
				return;
			}
		}
		
		GlobalOptionModel globalOptionModel = modelContextWorkingCopy.getModelProcessManifest().getGlobalOptionModel();
		
		longitudeSlices = this.minPreviewSlices + (previewQuality * (this.maxPreviewSlices - this.minPreviewSlices));
		latitudeSlices = longitudeSlices;
		
		globalOptionModel.setLatitudeSlices(latitudeSlices);
		globalOptionModel.setLongitudeSlices(longitudeSlices);
		
		// Only disable scripting if it's already enabled. Don't enable it if the user turned it off via the 
		// global option model
		if (globalOptionModel.getUseScripting()) {
			globalOptionModel.setUseScripting(useScripting);
		}
		

		ViewPerspective viewAngle = new ViewPerspective(rotateX, 
														rotateY, 
														rotateZ,
														shiftX,
														shiftY,
														shiftZ,
														zoom);


		try {
			modelContextWorkingCopy.getModelProcessManifest().setPropertyById("us.wthr.jdem846.model.ModelRenderOptionModel.viewAngle", viewAngle);
		} catch (ModelContainerException ex) {
			log.error("Error setting new projection values to option model: " + ex.getMessage(), ex);
			// TODO: Display error dialog
		}
		
		if (modelContextWorkingCopy.getRasterDataContext().getRasterDataListSize() == 0) {
			modelContextWorkingCopy.setNorthLimit(90);
			modelContextWorkingCopy.setSouthLimit(-90);
			modelContextWorkingCopy.setEastLimit(180);
			modelContextWorkingCopy.setWestLimit(-180);
		}

		
		
		renderModelVisualizationImage(dataModelChange);
		
		repaint();
	}
	
	
	public void renderModelVisualizationImage(boolean dataModelChange)
	{
		boolean resetCache = dataModelChange;
		boolean resetDataRange = dataModelChange;
		
		if (getWidth() <= 20 || getHeight() <= 20) {
			
			if (resetDataRange) {
				lastRerenderCancelledButNeededDataRangeUpdate = true;
			}
			if (resetCache) {
				lastRerenderCancelledButNeededCacheReset = true;
			}
			
			return;
		}
		if (lastRerenderCancelledButNeededCacheReset) {
			resetCache = true;
		}
		if (lastRerenderCancelledButNeededDataRangeUpdate) {
			resetDataRange = true;
		}
		lastRerenderCancelledButNeededCacheReset = false;
		lastRerenderCancelledButNeededDataRangeUpdate = false;
		
		log.info("Rendering model visualization image");
		
		int maxPreviewSize = (int) MathExt.min((double)pnlModelDisplay.getWidth(), (double)pnlModelDisplay.getHeight()) - 10;
		 

		GlobalOptionModel globalOptionModel = modelContextWorkingCopy.getModelProcessManifest().getGlobalOptionModel();
		
		double width = globalOptionModel.getWidth();
		double height = globalOptionModel.getHeight();
		
		double aspect = width / height;
		
		if (width > height) {
			
			width = maxPreviewSize;
			height = Math.round(width / aspect);
			
		} else {
			
			height = maxPreviewSize;
			width = Math.round(height * aspect);
			
		}
		
		
		
		globalOptionModel.setWidth((int)width);
		globalOptionModel.setHeight((int)height);

		if (globalOptionModel.getLimitCoordinates()) {
			
			
			
			double optNorthLimit = globalOptionModel.getNorthLimit();
			double optSouthLimit = globalOptionModel.getSouthLimit();
			double optEastLimit = globalOptionModel.getEastLimit();
			double optWestLimit = globalOptionModel.getWestLimit();
			
			if (optNorthLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setNorthLimit(optNorthLimit);
			if (optSouthLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setSouthLimit(optSouthLimit);
			if (optEastLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setEastLimit(optEastLimit);
			if (optWestLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setWestLimit(optWestLimit);
		}
		
		try {
			modelContextWorkingCopy.updateContext();
		} catch (ModelContextException ex) {
			// TODO Display error message dialog
			log.error("Exception updating model context: " + ex.getMessage(), ex);
		}
		
		modelContextWorkingCopy.resetModelCanvas();
		
		if (dataModelChange) {
			modelBuilder = null;
		}
		
		if (modelBuilder == null) {
			modelBuilder = new ModelBuilder();
		}
		
		try {
			
			modelBuilder.prepare(modelContextWorkingCopy, modelContextWorkingCopy.getModelProcessManifest());
		} catch (RenderEngineException ex) {
			log.warn("Error preparing renderer: " + ex.getMessage(), ex);
			// TODO Display error message
		}

		
		log.info("Rendering vizualization model...");
		try {
			modelBuilder.process();
		} catch (RenderEngineException ex) {
			log.error("Error rendering preview model: " + ex.getMessage(), ex);
		}
		log.info("Done rendering vizualization model");
		
		ModelCanvas modelCanvas = modelContextWorkingCopy.getModelCanvas();

		setBackground(globalOptionModel.getBackgroundColor().toAwtColor());
		pnlModelDisplay.setBackground(globalOptionModel.getBackgroundColor().toAwtColor());
		pnlModelDisplay.setImage(modelCanvas.getImage());
		pnlModelDisplay.zoomFit();
	}
	

	
	public void addProjectionChangeListener(ProjectionChangeListener listener)
	{
		projectionChangeListeners.add(listener);
	}
	
	public boolean removeProjectionChangeListener(ProjectionChangeListener listener)
	{
		return projectionChangeListeners.remove(listener);
	}
	
	protected void fireProjectionChangeListeners()
	{
		for (ProjectionChangeListener listener : projectionChangeListeners) {
			listener.onProjectionChanged(rotateX, rotateY, rotateZ, shiftX, shiftY, shiftZ, zoom);
		}
	}
	
	
	public interface ProjectionChangeListener 
	{
		public void onProjectionChanged(double rotateX, double rotateY, double rotateZ, double shiftX, double shiftY, double shiftZ, double zoom);
	}
	
	
	
	

	
	
}
