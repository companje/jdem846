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

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Line;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.render.Canvas3d;
import us.wthr.jdem846.render.ModelCanvas;
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
	
	private boolean useElevationOnDataGrids = true;
	
	public ModelVisualizationPanel(ModelContext modelContext)
	{
		super();
		this.modelContextActual = modelContext;
		setBackground(Color.WHITE);
		
		// Create working copy of model context
		
		try {
			modelContextWorkingCopy = modelContextActual.copy();
		} catch (DataSourceException ex) {
			log.error("Failed to copy model context: " + ex.getMessage(), ex);
			return;
		}
		
		//modelContextWorkingCopy.getModelOptions().setBackgroundColor("255;255;255;0");
		modelContextWorkingCopy.getModelOptions().setBackgroundColor("0;0;0;0");
		modelContextWorkingCopy.getModelOptions().setColoringType("hypsometric-tint");
		modelContextWorkingCopy.getModelOptions().setAntialiased(false);
		modelContextWorkingCopy.getModelOptions().setWidth(getWidth() - 20);
		modelContextWorkingCopy.getModelOptions().setHeight(getHeight() - 20);
		modelContextWorkingCopy.getModelOptions().setElevationMultiple(1.0);
		
		if (modelContextWorkingCopy.getRasterDataContext().getDataMinimumValue() >= modelContext.getRasterDataContext().getDataMaximumValue()) {
			modelContextWorkingCopy.getRasterDataContext().setDataMaximumValue(8850);
			modelContextWorkingCopy.getRasterDataContext().setDataMinimumValue(-10971);
		}
		
		modelContextWorkingCopy.updateContext();
		
		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0)
			{
				
			}
			public void componentMoved(ComponentEvent arg0)
			{
				
			}
			public void componentResized(ComponentEvent arg0)
			{
				update();
			}
			public void componentShown(ComponentEvent arg0)
			{
				update();
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
				update();
				//fireChangeListeners();
			}
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
		// Set Layout
		setLayout(new BorderLayout());
		
	}
	
	protected void onMouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		if (lastX != -1 && lastY != -1) {
			
			int deltaX = x - lastX;
			int deltaY = y - lastY;
			
			//log.info("X: " + x + ", Y: " + y + ", deltaX: " + deltaX + ", deltaY: " + deltaY);
		
			double rotateX = modelContextWorkingCopy.getModelOptions().getProjection().getRotateX();
			double rotateY = modelContextWorkingCopy.getModelOptions().getProjection().getRotateY();
			double rotateZ = modelContextWorkingCopy.getModelOptions().getProjection().getRotateZ();
			
			rotateX += (deltaY * 2);
			if (rotateX < 0)
				rotateX = 0;
			if (rotateX > 90)
				rotateX = 90;
			
			rotateY += (deltaX * 2);
			if (rotateY < -180)
				rotateY = -180;
			if (rotateY > 180)
				rotateY = 180;
			
			modelContextWorkingCopy.getModelOptions().getProjection().setRotateX(rotateX);
			modelContextWorkingCopy.getModelOptions().getProjection().setRotateY(rotateY);
			modelContextWorkingCopy.getModelOptions().getProjection().setRotateZ(rotateZ);
			
			update();
			//this.setRotation(rotateX, rotateY, rotateZ);
		}
		
		lastX = x;
		lastY = y;
		
		update();
	}
	
	
	public void update()
	{
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
		
		for (int i = modelContextWorkingCopy.getRasterDataContext().getRasterDataListSize() - 1; i >= 0; i--) {
			RasterData rasterData = modelContextWorkingCopy.getRasterDataContext().getRasterDataList().get(i);
			
			try {
				paintRasterPlot(modelContextWorkingCopy, modelCanvas, rasterData);
			} catch (Exception ex) {
				log.error("Error painting raster grid: " + ex.getMessage(), ex);
			}
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
		int[] rgba = {Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue(), 255};
		
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
	
	protected void paintRasterPlot(ModelContext modelContext, ModelCanvas canvas, RasterData rasterData) throws Exception
	{
		int[] rgba = {Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 255};
		
		double north = rasterData.getNorth();
		double south = rasterData.getSouth();
		double east = rasterData.getEast();
		double west = rasterData.getWest();
		
		Line line = new Line();
		
		/*
		double datanw = modelContext.getRasterDataContext().getData(north, west);
		double datane = modelContext.getRasterDataContext().getData(north, east);
		double datasw = modelContext.getRasterDataContext().getData(lat-latStep, lon);
		double datase = modelContext.getRasterDataContext().getData(lat-latStep, lon+lonStep);
		
		
		line.addEdge(createEdge(modelContext, north, west, 1.0, south, west, 1.0));
		line.addEdge(createEdge(modelContext, south, west, 1.0, south, east, 1.0));
		line.addEdge(createEdge(modelContext, south, east, 1.0, north, east, 1.0));
		line.addEdge(createEdge(modelContext, north, east, 1.0, north, west, 1.0));
		*/
		
		//double lonStep = (east - west) / 12.0;
		//double latStep = (north - south) / 12.0;
		double lonStep = ((rasterData.getColumns() - 1) / 12.0) * rasterData.getLongitudeResolution();
		double latStep = ((rasterData.getRows() - 1) / 12.0) * rasterData.getLatitudeResolution();
		
		//double latitudeResolution = modelContext.getRasterDataContext().
		
		for (double lon = west; lon < east; lon+=lonStep) {
			for (double lat = north; lat > south; lat-=latStep) {
				
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
						nwElev = rasterData.getData(nwLat, nwLon);
						neElev = rasterData.getData(neLat, neLon);
						swElev = rasterData.getData(swLat, swLon);
						seElev = rasterData.getData(seLat, seLon);
					} catch (Exception ex) {
						ex.printStackTrace();
						continue;
					}
				}
				
				if (Double.isNaN(nwElev)) {
					continue;
				}
				
				if (Double.isNaN(neElev)) {
					neElev = nwElev;
				}
				
				if (Double.isNaN(swElev)) {
					swElev = nwElev;
				}
				
				if (Double.isNaN(seElev)) {
					seElev = swElev;
				}
				
				
				
				
				line.addEdge(createEdge(modelContext, nwLat, nwLon, nwElev, swLat, swLon, swElev));
				line.addEdge(createEdge(modelContext, swLat, swLon, swElev, seLat, seLon, seElev));
				
				line.addEdge(createEdge(modelContext, seLat, seLon, seElev, neLat, neLon, neElev));
				line.addEdge(createEdge(modelContext, neLat, neLon, neElev, nwLat, nwLon, nwElev));
				
			}
			
			//line.addEdge(createEdge(modelContext, north, lon, 0.0, south, lon, 0.0));
		}
		
		
		
			//line.addEdge(createEdge(modelContext, lat, west, 0.0, lat, east, 0.0));
		//}
		
		canvas.drawShape(line, rgba);
		
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
		MapProjection projection = modelContext.getMapProjection();
		
    	MapPoint point = new MapPoint();
    	projection.getPoint(lat0, lon0, elev0, point);
    	
    	double x0 = (int) point.column;
    	double y0 = (int) point.row;
    	double z0 = (int) point.z;
    	
    	projection.getPoint(lat1, lon1, elev1, point);
    	
    	double x1 = (int) point.column;
    	double y1 = (int) point.row;
    	double z1 = (int) point.z;
    	
    	if (x0 != 0 && x1 == 0 && y1 == 0 && z1 == 0) {
    		int i = 0;
    	}
    	
    	//log.info("Edge: " + x0 + "/" + y0 + "/" + z0 + ", " + x1 + "/" + y1 + "/" + z1);
    	
    	return new Edge(x0, y0, z0, x1, y1, z1);
    	
    }
}
