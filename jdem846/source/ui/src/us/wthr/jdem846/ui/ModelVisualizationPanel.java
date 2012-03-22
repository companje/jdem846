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
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.PropertiesChangeListener;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Line;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.ElevationDataMap;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.Canvas3d;
import us.wthr.jdem846.render.CanvasProjection;
import us.wthr.jdem846.render.CanvasProjectionTypeEnum;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.simple.SimpleRenderer;
import us.wthr.jdem846.ui.base.Button;
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
	
	private ModelDisplayPanel pnlModelDisplay;
	private Slider sldQuality;
	private CheckBox chkPreviewRaster;
	private ToolbarButton btnUpdate;
	private CheckBox chkAutoUpdate;
	
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
	
	private boolean useElevationOnDataGrids = true;

	private List<ProjectionChangeListener> projectionChangeListeners = new LinkedList<ProjectionChangeListener>();
	
	private boolean ignoreUpdate = false;
	private boolean autoUpdate = true;
	
	/*
	 * Holy long variable names, Batman!
	 */
	private boolean lastRerenderCancelledButNeededDataRangeUpdate = false;
	private boolean lastRerenderCancelledButNeededCacheReset = false;
	
	private SimpleRenderer renderer;
	
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
	
		rotateX = modelContextActual.getModelOptions().getProjection().getRotateX();
		rotateY = modelContextActual.getModelOptions().getProjection().getRotateY();
		rotateZ = modelContextActual.getModelOptions().getProjection().getRotateZ();
		shiftZ = modelContextActual.getModelOptions().getProjection().getShiftZ();
		shiftX = modelContextActual.getModelOptions().getProjection().getShiftX();
		shiftY = modelContextActual.getModelOptions().getProjection().getShiftY();
		zoom = modelContextActual.getModelOptions().getProjection().getZoom();
		
		try {
			modelContextWorkingCopy = modelContextActual.copy();
			setWorkingCopyOptions();
		} catch (DataSourceException ex) {
			log.error("Failed to copy model context: " + ex.getMessage(), ex);
			return;
		}
		
		//modelContextWorkingCopy.getModelOptions().setBackgroundColor("255;255;255;0");
		
		// Create components
		this.pnlModelDisplay = new ModelDisplayPanel();
		sldQuality = new Slider(1, 100);
		chkPreviewRaster = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.previewRaster.label"));
		chkAutoUpdate = new CheckBox(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.autoUpdate.label"));
		btnUpdate = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.update.label"), JDem846Properties.getProperty("us.wthr.jdem846.ui.preview.refresh"), new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				update(true, true, true);
			}
		});
		
		chkPreviewRaster.setOpaque(false);
		chkAutoUpdate.setOpaque(false);
		sldQuality.setOpaque(false);
		
		// Set Tooltips
		
		sldQuality.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.quality.tooltip"));
		chkPreviewRaster.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.previewRaster.tooltip"));
		chkAutoUpdate.setToolTipText(I18N.get("us.wthr.jdem846.ui.modelVisualizationPanel.autoUpdate.tooltip"));
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
				if (renderer != null) {
					try {
						renderer.prepare(true, false);
					} catch (RenderEngineException ex) {
						log.warn("Error preparing renderer: " + ex.getMessage(), ex);
						// TODO Display error message
					}
				}
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
		//modelContextWorkingCopy.getModelOptions().setBackgroundColor("0;0;0;0");
		//modelContextWorkingCopy.getModelOptions().setColoringType("hypsometric-tint");
		modelContextWorkingCopy.getModelOptions().setAntialiased(false);
		modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		//modelContextWorkingCopy.getModelOptions().setElevationMultiple(1.0);
		modelContextWorkingCopy.getModelOptions().setOption(ModelOptionNamesEnum.MAINTAIN_ASPECT_RATIO_TO_DATA, false);

		//modelContextWorkingCopy.getModelOptions().setOption(ModelOptionNamesEnum.STANDARD_RESOLUTION_RETRIEVAL, JDem846Properties.getProperty("us.wthr.jdem846.ui.modelVisualizationPanel.data.standardResolutionRetrieval"));
		//modelContextWorkingCopy.getModelOptions().setOption(ModelOptionNamesEnum.INTERPOLATE_HIGHER_RESOLUTION, JDem846Properties.getProperty("us.wthr.jdem846.ui.modelVisualizationPanel.data.interpolate"));
		//modelContextWorkingCopy.getModelOptions().setOption(ModelOptionNamesEnum.AVERAGE_OVERLAPPING_RASTER_DATA, JDem846Properties.getProperty("us.wthr.jdem846.ui.modelVisualizationPanel.data.averageOverlappedData"));
		
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices", latitudeSlices);// JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices", longitudeSlices);//JDem846Properties.getDoubleProperty("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices"));
		
		
		
		
		boolean paintLightSourceLines = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.paintLightSourceLines");
		boolean paintBaseGrid = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.paintBaseGrid");
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintLightSourceLines", paintLightSourceLines);
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintBaseGrid", paintBaseGrid);
		
		
		
		modelContextWorkingCopy.updateContext();
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
	
	public void update(boolean dataModelChange, boolean updateFromActual)
	{
		update(dataModelChange, updateFromActual, false);
	}
	
	public void update(boolean dataModelChange, boolean updateFromActual, boolean force)
	{
		if (ignoreUpdate) {
			return;
		}
		
		if (!autoUpdate && !force) {
			return;
		}

		if (updateFromActual) {
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
		
		longitudeSlices = this.minPreviewSlices + (previewQuality * (this.maxPreviewSlices - this.minPreviewSlices));
		latitudeSlices = longitudeSlices;
		
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices", latitudeSlices);// JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices", longitudeSlices);//JDem846Properties.getDoubleProperty("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintRasterPreview", rasterPreview);
		
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateX(rotateX);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateY(rotateY);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateZ(rotateZ);
		
		modelContextWorkingCopy.getModelOptions().getProjection().setShiftX(shiftX);
		modelContextWorkingCopy.getModelOptions().getProjection().setShiftY(shiftY);
		//modelContextWorkingCopy.getModelOptions().getProjection().setShiftZ(shiftZ);
		
		modelContextWorkingCopy.getModelOptions().getProjection().setZoom(zoom);
		
		boolean paintGlobalBaseGrid = false;
		
		if (modelContextWorkingCopy.getModelOptions().getModelProjection() == CanvasProjectionTypeEnum.PROJECT_SPHERE) {
			paintGlobalBaseGrid = true;
		}
		
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintGlobalBaseGrid", paintGlobalBaseGrid);
		
		if (renderer == null) {
			renderer = new SimpleRenderer(modelContextWorkingCopy);
		} else {
			renderer.setModelContext(modelContextWorkingCopy);
		}

		if (modelContextWorkingCopy.getRasterDataContext().getRasterDataListSize() == 0) {
			modelContextWorkingCopy.setNorthLimit(90);
			modelContextWorkingCopy.setSouthLimit(-90);
			modelContextWorkingCopy.setEastLimit(180);
			modelContextWorkingCopy.setWestLimit(-180);
		}

		
		
		renderModelVisualizationImage(dataModelChange || updateFromActual, dataModelChange || updateFromActual);
		
		repaint();
		
		//if (dataModelChange) {
		//	update(false, false);
		//}
	}
	
	
	public void renderModelVisualizationImage(boolean resetCache, boolean resetDataRange)
	{
		
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
		
		int dimension = (int) MathExt.min((double)pnlModelDisplay.getWidth(), (double)pnlModelDisplay.getHeight());
		
		
		//log.info("Dimension: " + dimension);
		modelContextWorkingCopy.getModelOptions().setWidth(dimension - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(dimension - 20);
		
		//modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		//modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		
		if (modelContextWorkingCopy.getModelOptions().getBooleanOption(ModelOptionNamesEnum.LIMIT_COORDINATES)) {
			
			double optNorthLimit = modelContextWorkingCopy.getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_NORTH);
			double optSouthLimit = modelContextWorkingCopy.getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_SOUTH);
			double optEastLimit = modelContextWorkingCopy.getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_EAST);
			double optWestLimit = modelContextWorkingCopy.getModelOptions().getDoubleOption(ModelOptionNamesEnum.LIMITS_WEST);
			
			if (optNorthLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setNorthLimit(optNorthLimit);
			if (optSouthLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setSouthLimit(optSouthLimit);
			if (optEastLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setEastLimit(optEastLimit);
			if (optWestLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setWestLimit(optWestLimit);
		}
		
		modelContextWorkingCopy.updateContext();
		modelContextWorkingCopy.resetModelCanvas();
		//
		try {
			renderer.prepare(resetCache, resetDataRange);
		} catch (RenderEngineException ex) {
			log.warn("Error preparing renderer: " + ex.getMessage(), ex);
			// TODO Display error message
		}

		
		
		renderer.render();
		
		ModelCanvas modelCanvas = modelContextWorkingCopy.getModelCanvas();
		pnlModelDisplay.backgroundColor = ColorSerializationUtil.stringToColor(modelContextWorkingCopy.getModelOptions().getBackgroundColor());
		pnlModelDisplay.modelVisualizationImage = modelCanvas.getImage();

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
	
	
	
	
	private class ModelDisplayPanel extends RoundedPanel
	{
		public Image modelVisualizationImage = null;
		public Color backgroundColor = Color.WHITE;
		
		public ModelDisplayPanel()
		{
			this.setOpaque(false);
			
		}
		
		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2d = (Graphics2D) g;
			
			setBackground(backgroundColor);
			
			super.paint(g2d);
			
			
			if (modelVisualizationImage != null) {
				
				int x = (int) ((getWidth() / 2.0) - (modelVisualizationImage.getWidth(null) / 2.0));
				int y = (int) ((getHeight() / 2.0) - (modelVisualizationImage.getHeight(null) / 2.0));
				
				g2d.drawImage(modelVisualizationImage, x, y, modelVisualizationImage.getWidth(null), modelVisualizationImage.getHeight(null), null);
			}
			
			if (downX != -1 && downY != -1 && buttonDown == 3) {
				
				
				int x0 = (int) MathExt.min(downX, lastX);
				int y0 = (int) MathExt.min(downY, lastY);
				
				int x1 = (int) MathExt.max(downX, lastX);
				int y1 = (int) MathExt.max(downY, lastY);
				
				g2d.setColor(Color.BLUE);
				g2d.drawRect(x0, y0, x1 - x0, y1 - y0);
				
				
				g2d.setColor(new Color(Color.BLUE.getRed(), Color.BLUE.getGreen(), Color.BLUE.getBlue(), 25));
				g2d.fillRect(x0, y0, x1 - x0, y1 - y0);
			}
			
			
			
		}
		
		
	}
	
}
