package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Line;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.ElevationDataMap;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.Canvas3d;
import us.wthr.jdem846.render.CanvasProjection;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.simple.SimpleRenderer;
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class ModelVisualizationPanel extends RoundedPanel
{
	
	private static Log log = Logging.getLog(ModelVisualizationPanel.class);
	
	private ModelContext modelContextActual;
	private ModelContext modelContextWorkingCopy;
	private Image modelVisualizationImage = null;
	
	int buttonDown = -1;
	int lastX = -1;
	int lastY = -1;
	
	double rotateX;
	double rotateY;
	double rotateZ;
	
	double shiftZ;
	double shiftX;
	
	private boolean useElevationOnDataGrids = true;

	private List<ProjectionChangeListener> projectionChangeListeners = new LinkedList<ProjectionChangeListener>();
	
	private boolean ignoreUpdate = false;
	
	private SimpleRenderer renderer;
	
	public ModelVisualizationPanel(ModelContext modelContext)
	{
		super();
		this.modelContextActual = modelContext;
		setBackground(Color.WHITE);
		
		

		rotateX = modelContextActual.getModelOptions().getProjection().getRotateX();
		rotateY = modelContextActual.getModelOptions().getProjection().getRotateY();
		rotateZ = modelContextActual.getModelOptions().getProjection().getRotateZ();
		
		try {
			modelContextWorkingCopy = modelContextActual.copy();
			setWorkingCopyOptions();
		} catch (DataSourceException ex) {
			log.error("Failed to copy model context: " + ex.getMessage(), ex);
			return;
		}
		
		//modelContextWorkingCopy.getModelOptions().setBackgroundColor("255;255;255;0");
		
		
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
					renderer.prepare(true, false);
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

				ignoreUpdate = true;
				fireProjectionChangeListeners();
				ignoreUpdate = false;
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
		// Set Layout
		setLayout(new BorderLayout());
		

	}
	
	protected void setWorkingCopyOptions()
	{
		modelContextWorkingCopy.getModelOptions().setBackgroundColor("0;0;0;0");
		//modelContextWorkingCopy.getModelOptions().setColoringType("hypsometric-tint");
		modelContextWorkingCopy.getModelOptions().setAntialiased(false);
		modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		modelContextWorkingCopy.getModelOptions().setElevationMultiple(1.0);
		

		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.standardResolutionRetrieval", JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.data.standardResolutionRetrieval"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.interpolate", JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.data.interpolate"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.averageOverlappedData", JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.data.averageOverlappedData"));
		
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices", JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices", JDem846Properties.getProperty("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices"));
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintLightSourceLines", true);
		modelContextWorkingCopy.getModelOptions().setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintBaseGrid", true);
		
		
		modelContextWorkingCopy.updateContext();
	}
	
	protected void onMouseDragged(MouseEvent e)
	{
		if (buttonDown == 1) {
			onMouseDraggedLeftButton(e);
		} else if (buttonDown == 2) {
			onMouseDraggedMiddleButton(e);
		}
		
	}
	
	protected void onMouseDraggedMiddleButton(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		if (lastX != -1 && lastY != -1) {
			
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			
			shiftZ += (deltaY * 5);
			shiftX += (deltaX * 5);
			
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
			

			rotateX += (deltaY * 1);
			if (rotateX < 0)
				rotateX = 0;
			if (rotateX > 90)
				rotateX = 90;
			
			rotateY += (deltaX * 1);
			if (rotateY < -180)
				rotateY = -180;
			if (rotateY > 180)
				rotateY = 180;
			
		}
		
		lastX = x;
		lastY = y;
		
		update(false, false);
	}
	
	
	
	public void update(boolean dataModelChange, boolean updateFromActual)
	{
		if (ignoreUpdate) {
			return;
		}

		if (updateFromActual) {
			try {
				modelContextWorkingCopy = modelContextActual.copy();
				setWorkingCopyOptions();
			} catch (DataSourceException ex) {
				log.error("Failed to copy model context: " + ex.getMessage(), ex);
				return;
			}
		}
		

		modelContextWorkingCopy.getModelOptions().getProjection().setRotateX(rotateX);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateY(rotateY);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateZ(rotateZ);
		
		modelContextWorkingCopy.getModelOptions().getProjection().setShiftX(shiftX);
		modelContextWorkingCopy.getModelOptions().getProjection().setShiftZ(shiftZ);
		
		
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

		renderer.prepare(dataModelChange || updateFromActual, dataModelChange || updateFromActual);

		
		renderModelVisualizationImage();
		
		repaint();
		
		//if (dataModelChange) {
		//	update(false, false);
		//}
	}
	
	
	public void renderModelVisualizationImage()
	{
		if (getWidth() <= 20 || getHeight() <= 20) {
			return;
		}
		
		log.info("Rendering model visualization image");
		
		int dimension = (int) MathExt.min((double)getWidth(), (double)getHeight());
		////modelContextWorkingCopy.getModelOptions().setWidth(dimension - 20);
		//modelContextWorkingCopy.getModelOptions().setHeight(dimension - 20);
		
		modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		
		modelContextWorkingCopy.updateContext();
		modelContextWorkingCopy.resetModelCanvas();
		//
		
		renderer.render();
		
		ModelCanvas modelCanvas = modelContextWorkingCopy.getModelCanvas();
		modelVisualizationImage = modelCanvas.getImage();

	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		super.paint(g2d);
		
		
		if (modelVisualizationImage != null) {
			g2d.drawImage(modelVisualizationImage, 10, 10, null);
		}

		
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
			listener.onProjectionChanged(rotateX, rotateY, rotateZ, shiftX, 0.0, shiftZ);
		}
	}
	
	
	public interface ProjectionChangeListener 
	{
		public void onProjectionChanged(double rotateX, double rotateY, double rotateZ, double shiftX, double shiftY, double shiftZ);
	}
	
}
