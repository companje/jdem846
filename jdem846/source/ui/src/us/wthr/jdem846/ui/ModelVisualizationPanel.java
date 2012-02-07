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
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Line;
import us.wthr.jdem846.geom.Polygon;
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
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class ModelVisualizationPanel extends RoundedPanel
{
	
	private static Log log = Logging.getLog(ModelVisualizationPanel.class);
	
	private ModelContext modelContextActual;
	private ModelContext modelContextWorkingCopy;
	private Image modelVisualizationImage = null;
	
	int lastX = -1;
	int lastY = -1;
	
	double rotateX;
	double rotateY;
	double rotateZ;
	
	private boolean useElevationOnDataGrids = true;
	
	private ElevationDataMap elevationMap;
	
	private List<ProjectionChangeListener> projectionChangeListeners = new LinkedList<ProjectionChangeListener>();
	
	private Perspectives perspectives = new Perspectives();
	private double normal[] = new double[3];
	private double backLeftPoints[] = new double[3];
	private double backRightPoints[] = new double[3];
	private double frontLeftPoints[] = new double[3];
	private double frontRightPoints[] = new double[3];
	private double sunsource[] = new double[3];	
	private int colorBufferA[] = new int[4];
	private int colorBufferB[] = new int[4];
	
	private double spotExponent = 0;
	private double relativeLightIntensity = 0;
	private double relativeDarkIntensity = 0;
	
	private double latitudeSlices = 70;
	private double longitudeSlices = 70;
	
	public ModelVisualizationPanel(ModelContext modelContext)
	{
		super();
		this.modelContextActual = modelContext;
		setBackground(Color.WHITE);
		
		// Create working copy of model context
		
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
				elevationMap = null;
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
				//useElevationOnDataGrids = false;
			}
			public void mouseDragged(MouseEvent e)
			{
				onMouseDragged(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				lastX = -1;
				lastY = -1;
				//useElevationOnDataGrids = true;
				update(false, false);
				fireProjectionChangeListeners();
				//fireChangeListeners();
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
		// Set Layout
		setLayout(new BorderLayout());
		
		
		backLeftPoints[0] = -1.0;
		backLeftPoints[1] = 0.0;
		backLeftPoints[2] = -1.0;
		
		backRightPoints[0] = 1.0;
		backRightPoints[1] = 0.0;
		backRightPoints[2] = -1.0;
		
		frontLeftPoints[0] = -1.0;
		frontLeftPoints[1] = 0.0;
		frontLeftPoints[2] = 1.0;
		
		frontRightPoints[0] = 1.0;
		frontRightPoints[1] = 0.0;
		frontRightPoints[2] = 1.0;
	}
	
	protected void setWorkingCopyOptions()
	{
		modelContextWorkingCopy.getModelOptions().setBackgroundColor("0;0;0;0");
		modelContextWorkingCopy.getModelOptions().setColoringType("hypsometric-tint");
		modelContextWorkingCopy.getModelOptions().setAntialiased(false);
		modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		modelContextWorkingCopy.getModelOptions().setElevationMultiple(1.0);

		
		//if (modelContextWorkingCopy.getRasterDataContext().getDataMinimumValue() >= modelContextWorkingCopy.getRasterDataContext().getDataMaximumValue()) {
		//	modelContextWorkingCopy.getRasterDataContext().setDataMaximumValue(8850);
		//	modelContextWorkingCopy.getRasterDataContext().setDataMinimumValue(-10971);
		//}
		
		modelContextWorkingCopy.updateContext();
	}
	
	
	protected void onMouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		if (lastX != -1 && lastY != -1) {
			
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			
			//log.info("X: " + x + ", Y: " + y + ", deltaX: " + deltaX + ", deltaY: " + deltaY);
		
			//double _rotateX = modelContextWorkingCopy.getModelOptions().getProjection().getRotateX();
			//double _rotateY = modelContextWorkingCopy.getModelOptions().getProjection().getRotateY();
			//double _rotateZ = modelContextWorkingCopy.getModelOptions().getProjection().getRotateZ();
			
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
			
			//modelContextWorkingCopy.getModelOptions().getProjection().setRotateX(rotateX);
		//	modelContextWorkingCopy.getModelOptions().getProjection().setRotateY(rotateY);
			//modelContextWorkingCopy.getModelOptions().getProjection().setRotateZ(rotateZ);
			
			//update(false);
			//this.setRotation(rotateX, rotateY, rotateZ);
		}
		
		lastX = x;
		lastY = y;
		
		update(false, false);
	}
	
	
	
	public void update(boolean dataModelChange, boolean updateFromActual)
	{
		
		//if (updateFromActual) {
		//	modelContextWorkingCopy.getModelOptions().setProjection(modelContextActual.getModelOptions().getProjection().copy());
		//	modelContextWorkingCopy.getModelOptions().setMapProjection(modelContextActual.getModelOptions().getMapProjection());
		//}
		
		if (updateFromActual) {
			try {
				modelContextWorkingCopy = modelContextActual.copy();
				setWorkingCopyOptions();
			} catch (DataSourceException ex) {
				log.error("Failed to copy model context: " + ex.getMessage(), ex);
				return;
			}
		}
		
		//modelContextWorkingCopy.getModelOptions().setProject3d(true);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateX(rotateX);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateY(rotateY);
		modelContextWorkingCopy.getModelOptions().getProjection().setRotateZ(rotateZ);
		
		
		if (elevationMap == null || dataModelChange) {
			elevationMap = ElevationDataMap.create(modelContextWorkingCopy.getNorth(), 
					modelContextWorkingCopy.getSouth(), 
					modelContextWorkingCopy.getEast(), 
					modelContextWorkingCopy.getWest(), 
					modelContextWorkingCopy.getRasterDataContext().getEffectiveLatitudeResolution(), 
					modelContextWorkingCopy.getRasterDataContext().getEffectiveLongitudeResolution());
			
			try {
				determineDataRangeLowRes(modelContextWorkingCopy);
			} catch (DataSourceException ex) {
				log.error("Error determining elevation min & max: " + ex.getMessage(), ex);
			}
		}
		
		renderModelVisualizationImage();
		
		this.repaint();
	}
	
	
	public void renderModelVisualizationImage()
	{
		if (getWidth() <= 20 || getHeight() <= 20) {
			return;
		}
		
		log.info("Rendering model visualization image");
		
		
		modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		
		modelContextWorkingCopy.updateContext();
		modelContextWorkingCopy.resetModelCanvas();
		ModelCanvas modelCanvas = modelContextWorkingCopy.getModelCanvas(true);
		
	
		
		
		//modelContext.getModelOptions().setBackgroundColor("255;255;255;255");
		//ModelCanvas modelCanvas = new ModelCanvas(modelContext);
		
		//Canvas3d canvas = new Canvas3d(getWidth(), getHeight());
		
		
		try {
			paintLightSourceLines(modelContextWorkingCopy, modelCanvas);
		} catch (Exception ex) {
			log.error("Error painting light source lines: " + ex.getMessage(), ex);
		}
		
		/*
		for (int i = modelContextWorkingCopy.getRasterDataContext().getRasterDataListSize() - 1; i >= 0; i--) {
			RasterData rasterData = modelContextWorkingCopy.getRasterDataContext().getRasterDataList().get(i);
			
			try {
				paintRasterPlot(modelContextWorkingCopy, modelCanvas, rasterData);
			} catch (Exception ex) {
				log.error("Error painting raster grid: " + ex.getMessage(), ex);
			}
		}
		*/
		try {
			paintRasterPlot(modelContextWorkingCopy, modelCanvas);
		} catch (Exception ex) {
			log.error("Error painting raster grid: " + ex.getMessage(), ex);
		}
		
		try {
			paintBasicGrid(modelContextWorkingCopy, modelCanvas);
		} catch (Exception ex) {
			log.error("Error painting base grid: " + ex.getMessage(), ex);
		}

		
		modelVisualizationImage = modelCanvas.getFinalizedImage();
		
		
		
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
	
	protected void paintLightSourceLines(ModelContext modelContext, ModelCanvas canvas) throws Exception
	{
		int[] rgba = {Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 255};
		
		if (modelContext.getLightingContext() == null || !modelContext.getLightingContext().isLightingEnabled()) {
			log.info("Lighting not enabled, skipping light source lines");
			return;
		}
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		double latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = modelContext.getRasterDataContext().getLongitudeResolution();
		double centerLatitude = (north + south) / 2.0;
		double centerLongitude = (east + west) / 2.0;
		double metersResolution = modelContext.getRasterDataContext().getMetersResolution();
		double radiusInterval = MathExt.sqrt(MathExt.sqr(latitudeResolution) + MathExt.sqr(longitudeResolution));
		
		
		Line line = new Line();
		
		double lightAzimuth = modelContext.getLightingContext().getLightingAzimuth();
		double lightElevation = modelContext.getLightingContext().getLightingElevation();
		double[] points = new double[3];
		double radius = MathExt.sqrt(MathExt.sqr(north - south) + MathExt.sqr(east - west));
				
		
		//log.info("Azimuth: " + lightAzimuth + ", Elevation: " + lightElevation + ", Radius: " + radius);
		Spheres.getPoint3D(lightAzimuth, lightElevation, radius, points);
	
		double latitude = centerLatitude + points[0];
		double longitude = centerLongitude - points[2];
		double resolution = (points[1] / radiusInterval);
		double elevation = (resolution * metersResolution);
		//double lesserElevation = (resolution * metersResolution) - 100.0;
		
		line.addEdge(createEdge(modelContext, latitude, longitude, elevation, centerLatitude, centerLongitude, 0.0));
		//line.addEdge(createEdge(modelContext, latitude, longitude, lesserElevation, centerLatitude, centerLongitude, 0.0));
		line.addEdge(createEdge(modelContext, latitude, longitude, 0, centerLatitude, centerLongitude, 0.0));
		
		canvas.drawShape(line, rgba);
	}
	
	protected void paintRasterPlot(ModelContext modelContext, ModelCanvas canvas) throws Exception
	{
		int[] rgba = {Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 255};
		
		setUpLightSource(modelContext.getLightingContext().getLightingElevation(), modelContext.getLightingContext().getLightingAzimuth());
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		
		spotExponent = modelContext.getLightingContext().getSpotExponent();
		relativeLightIntensity = modelContext.getLightingContext().getRelativeLightIntensity();
		relativeDarkIntensity = modelContext.getLightingContext().getRelativeDarkIntensity();
		
		double lightingMultiple = modelContext.getLightingContext().getLightingMultiple();
		
		double north = rasterDataContext.getNorth();
		double south = rasterDataContext.getSouth();
		double east = rasterDataContext.getEast();
		double west = rasterDataContext.getWest();
		
		//Line line = new Line();

		//double lonStep = ((rasterData.getColumns() - 1) / 12.0) * rasterData.getLongitudeResolution();
		//double latStep = ((rasterData.getRows()) / 12.0) * rasterData.getLatitudeResolution();
		
		double latStep = (north - south - modelContext.getRasterDataContext().getEffectiveLatitudeResolution()) / latitudeSlices;
		double lonStep = (east - west - modelContext.getRasterDataContext().getEffectiveLongitudeResolution()) / longitudeSlices;
		

		for (double lon = west; lon < east - rasterDataContext.getLongitudeResolution(); lon+=lonStep) {
			for (double lat = north; lat > south + rasterDataContext.getLatitudeResolution(); lat-=latStep) {
				
				//if (lon >= east) {
				//	lon -= rasterData.getLongitudeResolution();
				//}
				
				//if (lat <= south) {
				//	lat += rasterData.getLatitudeResolution();
				//}
				
				double nwLat = lat;
				double nwLon = lon;
				
				double neLat = lat;
				double neLon = lon + lonStep;
				
				double swLat = lat - latStep;
				double swLon = lon;
				
				double seLat = lat - latStep;
				double seLon = lon + lonStep;
				
				
				double nwElev = 0;
				double neElev = 0;
				double swElev = 0;
				double seElev = 0;
				
				if (useElevationOnDataGrids) {
					try {
						nwElev = getElevation(modelContext, nwLat, nwLon);
						neElev = getElevation(modelContext, neLat, neLon);
						swElev = getElevation(modelContext, swLat, swLon);
						seElev = getElevation(modelContext, seLat, seLon);
					} catch (Exception ex) {
						ex.printStackTrace();
						continue;
					}
				}
				
				
				if (Double.isNaN(nwElev) || nwElev == DemConstants.ELEV_NO_DATA) {
					continue;
				}
				
				if (Double.isNaN(neElev) || neElev == DemConstants.ELEV_NO_DATA) {
					//neElev = nwElev;
					continue;
				}
				
				if (Double.isNaN(swElev) || swElev == DemConstants.ELEV_NO_DATA) {
					//swElev = nwElev;
					continue;
				}
				
				if (Double.isNaN(seElev) || seElev == DemConstants.ELEV_NO_DATA) {
					//seElev = swElev;
					continue;
				}
				
				calculateNormal(nwElev, swElev, seElev, neElev, normal);
				double dot = calculateDotProduct();
				
				colorBufferA[0] = rgba[0];
				colorBufferA[1] = rgba[1];
				colorBufferA[2] = rgba[2];
				colorBufferA[3] = rgba[3];
				
				ColorAdjustments.adjustBrightness(colorBufferA, dot);
				ColorAdjustments.interpolateColor(rgba, colorBufferA, colorBufferB, lightingMultiple);
				
				colorBufferB[3] = 255;
				
				Polygon poly = new Polygon();
				poly.addEdge(createEdge(modelContext, nwLat, nwLon, nwElev, swLat, swLon, swElev));
				poly.addEdge(createEdge(modelContext, swLat, swLon, swElev, seLat, seLon, seElev));
				poly.addEdge(createEdge(modelContext, seLat, seLon, seElev, neLat, neLon, neElev));
				poly.addEdge(createEdge(modelContext, neLat, neLon, neElev, nwLat, nwLon, nwElev));
				canvas.fillShape(poly, colorBufferB);

			}
			
			
		}

		
		//canvas.drawShape(line, rgba);
		
	}
	
	protected void calculateNormal(double nw, double sw, double se, double ne, double[] normal)
	{
		backLeftPoints[1] = nw;
		backRightPoints[1] = ne;
		frontLeftPoints[1] = sw;
		frontRightPoints[1] = se;
		
		perspectives.calcNormal(backLeftPoints, frontLeftPoints, backRightPoints, normal);
	}
	
	protected double calculateDotProduct()
	{
		double dot = perspectives.dotProduct(normal, sunsource);
		dot = Math.pow(dot, spotExponent);
		

		
		if (dot > 0) {
			dot *= relativeLightIntensity;
		} else if (dot < 0) {
			dot *= relativeDarkIntensity;
		}
		
		return dot;
	}
	
	protected void setUpLightSource(double solarElevation, double solarAzimuth)
	{
		
		Vector sun = new Vector(0.0, 0.0, -1.0);
		Vector angles = new Vector(solarElevation, -solarAzimuth, 0.0);
		sun.rotate(angles);

		sunsource[0] = sun.getX();
		sunsource[1] = sun.getY();
		sunsource[2] = sun.getZ();
		
	}
	
	
	protected double getElevation(ModelContext modelContext, double latitude, double longitude) throws DataSourceException
	{
		
		double elevation = elevationMap.get(latitude, longitude, DemConstants.ELEV_NO_DATA);
		if (elevation != DemConstants.ELEV_NO_DATA)
			return elevation;
	
		elevation = modelContext.getRasterDataContext().getDataStandardResolution(latitude, longitude, false, false);
		
		elevationMap.put(latitude, longitude, elevation);
		return elevation;
	}
	
	protected void paintBasicGrid(ModelContext modelContext, ModelCanvas canvas) throws Exception
	{
		
		int[] rgba = {Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 255};
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		Line line = new Line();

		line.addEdge(createEdge(modelContext, north, west, -1.0, south, west, -1.0));
		line.addEdge(createEdge(modelContext, south, west, -1.0, south, east, -1.0));
		line.addEdge(createEdge(modelContext, south, east, -1.0, north, east, -1.0));
		line.addEdge(createEdge(modelContext, north, east, -1.0, north, west, -1.0));
		
		//canvas.fillShape(line, rgba);
		
		double lonStep = (east - west) / 12;
		for (double lon = west; lon <= east; lon+=lonStep) {
			line.addEdge(createEdge(modelContext, north, lon, 0.0, south, lon, 0.0));
		}
		
		double latStep = (north - south) / 12;
		for (double lat = south; lat <= north; lat+=latStep) {
			line.addEdge(createEdge(modelContext, lat, west, 0.0, lat, east, 0.0));
		}
		
		
		canvas.drawShape(line, rgba);
		//canvas.fillShape(line, rgba);
		//canvas.fill(line, rgba);
	}
	
	
	
	
	protected Edge createEdge(ModelContext modelContext, double lat0, double lon0, double elev0, double lat1, double lon1, double elev1) throws MapProjectionException
    {
		//MapProjection projection = modelContext.getMapProjection();
		CanvasProjection projection = modelContext.getModelCanvas().getCanvasProjection();
		
    	MapPoint point = new MapPoint();
    	projection.getPoint(lat0, lon0, elev0, point);
    	
    	double x0 = (int) point.column;
    	double y0 = (int) point.row;
    	double z0 = (int) point.z;
    	
    	projection.getPoint(lat1, lon1, elev1, point);
    	
    	double x1 = (int) point.column;
    	double y1 = (int) point.row;
    	double z1 = (int) point.z;

    	return new Edge(x0, y0, z0, x1, y1, z1);
    	
    }
	
	
	public void determineDataRangeLowRes(ModelContext modelContext) throws DataSourceException
	{
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();

		double north = rasterDataContext.getNorth();
		double south = rasterDataContext.getSouth();
		double east = rasterDataContext.getEast();
		double west = rasterDataContext.getWest();
		
		double latStep = (north - south - modelContext.getRasterDataContext().getEffectiveLatitudeResolution()) / latitudeSlices;
		double lonStep = (east - west - modelContext.getRasterDataContext().getEffectiveLongitudeResolution()) / longitudeSlices;
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (double lon = west; lon < east - rasterDataContext.getLongitudeResolution(); lon+=lonStep) {
			for (double lat = north; lat > south + rasterDataContext.getLatitudeResolution(); lat-=latStep) {
				double elevation = getElevation(modelContext, lat, lon);
				
				if (!Double.isNaN(elevation) && elevation != DemConstants.ELEV_NO_DATA) {
					min = MathExt.min(elevation, min);
					max = MathExt.max(elevation, max);
				}
				
			}
			
		}
		
		rasterDataContext.setDataMaximumValue(max);
		rasterDataContext.setDataMinimumValue(min);
		
		
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
			listener.onProjectionChanged(rotateX, rotateY, rotateZ);
		}
	}
	
	
	public interface ProjectionChangeListener 
	{
		public void onProjectionChanged(double rotateX, double rotateY, double rotateZ);
	}
	
}
