package us.wthr.jdem846.render.simple;

import java.awt.Color;
import java.util.Calendar;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Line;
import us.wthr.jdem846.geom.Polygon;
import us.wthr.jdem846.geom.Triangle;
import us.wthr.jdem846.geom.TriangleStrip;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarCalculator;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.rasterdata.ElevationDataMap;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.CanvasProjection;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.RayTracing;
import us.wthr.jdem846.render.RayTracing.RasterDataFetchHandler;
import us.wthr.jdem846.render.gfx.Vector;

public class SimpleRenderer
{
	
	private enum CornerEnum {
		NORTHEAST,
		NORTHWEST,
		SOUTHEAST,
		SOUTHWEST
	};
	
	private static Log log = Logging.getLog(SimpleRenderer.class);
	
	private ModelContext modelContext;
	private ElevationDataMap elevationMap;
	
	private Perspectives perspectives = new Perspectives();
	private double normal[] = new double[3];
	private double backLeftPoints[] = new double[3];
	private double backRightPoints[] = new double[3];
	private double frontLeftPoints[] = new double[3];
	private double frontRightPoints[] = new double[3];
	private double sunsource[] = new double[3];	
	private int colorBufferA[] = new int[4];
	private int colorBufferB[] = new int[4];
	private double[] pointNormal = new double[3];
	
	private double longitudeResolution;
	private double latitudeResolution;
	
	private double elevationMultiple;
	
	private boolean lightingEnabled = true;
	private double spotExponent = 0;
	private double relativeLightIntensity = 0;
	private double relativeDarkIntensity = 0;
	private double lightingMultiple = 0;
	private boolean paintGlobalBaseGrid = true;
	
	private RayTracing lightSourceRayTracer;
	private boolean rayTraceShadows;
	private double shadowIntensity;
	
	private ModelColoring modelColoring;
	private CanvasProjection projection;
	private MapPoint point = new MapPoint();
	
	private double solarElevation;
	private double solarAzimuth;
	private LightSourceSpecifyTypeEnum lightSourceType;
	private long lightOnDate;
	private boolean recalcLightOnEachPoint;
	private SolarCalculator solarCalculator;
	private SolarPosition position;
	private EarthDateTime datetime;
	private Coordinate latitudeCoordinate;
	private Coordinate longitudeCoordinate;
	private Planet planet;
	private boolean sunIsUp = false;
	
	private double latitudeSlices = 50;
	private double longitudeSlices = 50;
	
	private boolean getStandardResolutionElevation = true;
	private boolean interpolateData = true;
	private boolean averageOverlappedData = true;
	
	private int[] sourceLineColor = {Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 255};
	private int[] baseGridColor = {Color.LIGHT_GRAY.getRed(), Color.LIGHT_GRAY.getGreen(), Color.LIGHT_GRAY.getBlue(), 255};
	
	public SimpleRenderer(ModelContext modelContext)
	{
		this.modelContext = modelContext;
		

	}
	
	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}
	
	public void prepare(boolean resetCache, boolean resetDataRange)
	{
		log.info("Resetting simple renderer cache");
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();

		latitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices");
		longitudeSlices = modelContext.getModelOptions().getDoubleOption("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices");
		
		
		latitudeResolution = (north - south - modelContext.getRasterDataContext().getEffectiveLatitudeResolution()) / latitudeSlices;
		longitudeResolution = (east - west - modelContext.getRasterDataContext().getEffectiveLongitudeResolution()) / longitudeSlices;
		
		////latitudeResolution = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		//longitudeResolution = modelContext.getRasterDataContext().getEffectiveLongitudeResolution();
		
		
		if (resetCache) {
			elevationMap = ElevationDataMap.create(modelContext.getNorth(), 
					modelContext.getSouth(), 
					modelContext.getEast(), 
					modelContext.getWest(), 
					modelContext.getRasterDataContext().getEffectiveLatitudeResolution(), 
					modelContext.getRasterDataContext().getEffectiveLongitudeResolution());
		}
		
		if (resetDataRange) {
			try {
				determineDataRangeLowRes();
			} catch (DataSourceException ex) {
				log.error("Error determining elevation min & max: " + ex.getMessage(), ex);
			}
		}
		
		planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		LightingContext lightingContext = modelContext.getLightingContext();
		lightSourceType = lightingContext.getLightSourceSpecifyType();
		lightOnDate = lightingContext.getLightingOnDate();
		
		/* Force this to be false. This renderer needs to be fast and recalculating
		 * adds way too much overhead. Should the recalc method be optimized enough,
		 * perhaps this will again be configurable.
		 */
		recalcLightOnEachPoint = true;
		
		
		if (lightSourceType == LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION) {
			sunIsUp = true;
			setUpLightSource(0, 0, modelContext.getLightingContext().getLightingElevation(), modelContext.getLightingContext().getLightingAzimuth(), true);
		}
		
		if (lightSourceType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME) {
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(lightOnDate);
			
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = cal.get(Calendar.SECOND);
			
			log.info("Setting initial date/time to " + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
			
			
			position = new SolarPosition();
			datetime = new EarthDateTime(year, month, day, hour, minute, second, 0, false);
			latitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LATITUDE);
			longitudeCoordinate = new Coordinate(0.0, CoordinateTypeEnum.LONGITUDE);
			solarCalculator = new SolarCalculator();
			solarCalculator.setDatetime(datetime);
			
		
		
			
			if (!recalcLightOnEachPoint) {
				
				double latitude = (north + south) / 2.0;
				double longitude = (east + west) / 2.0;
				
				setUpLightSource(latitude, longitude, 0, 0, true);
			}
		}
		//getStandardResolutionElevation = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.STANDARD_RESOLUTION_RETRIEVAL);
		//interpolateData = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.INTERPOLATE_HIGHER_RESOLUTION);
		//averageOverlappedData = modelContext.getModelOptions().getBooleanOption(ModelOptionNamesEnum.AVERAGE_OVERLAPPING_RASTER_DATA);
		
		getStandardResolutionElevation = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.standardResolutionRetrieval");
		interpolateData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.interpolateToHigherResolution");
		averageOverlappedData = JDem846Properties.getBooleanProperty("us.wthr.jdem846.previewing.ui.averageOverlappedData");
		
		lightingEnabled = modelContext.getLightingContext().isLightingEnabled();
		spotExponent = modelContext.getLightingContext().getSpotExponent();
		relativeLightIntensity = modelContext.getLightingContext().getRelativeLightIntensity();
		relativeDarkIntensity = modelContext.getLightingContext().getRelativeDarkIntensity();
		lightingMultiple = modelContext.getLightingContext().getLightingMultiple();
		
		solarAzimuth = modelContext.getLightingContext().getLightingAzimuth();
		solarElevation = modelContext.getLightingContext().getLightingElevation();
		
		elevationMultiple = modelContext.getModelOptions().getElevationMultiple();
		
		modelColoring = ColoringRegistry.getInstance(modelContext.getModelOptions().getColoringType()).getImpl();
		projection = modelContext.getModelCanvas().getCanvasProjection();
		
		paintGlobalBaseGrid = modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintGlobalBaseGrid");
		
		modelContext.getModelDimensions().outputLatitudeResolution = latitudeResolution;
		modelContext.getModelDimensions().outputLongitudeResolution = longitudeResolution;
		
		rayTraceShadows = modelContext.getLightingContext().getRayTraceShadows();
		shadowIntensity = modelContext.getLightingContext().getShadowIntensity();
		if (rayTraceShadows) {
			lightSourceRayTracer = new RayTracing(modelContext,
					new RasterDataFetchHandler() {
						public double getRasterData(double latitude, double longitude) throws Exception {
							return getElevation(latitude, longitude);
						}
			});
		} else {
			lightSourceRayTracer = null;
		}
		

	}
	
	
	public void render()
	{

		log.info("Rendering model simple image");
		
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		
		
		if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintLightSourceLines")) {
			try {
				paintLightSourceLines(modelCanvas);
			} catch (Exception ex) {
				log.error("Error painting light source lines: " + ex.getMessage(), ex);
			}
		}
		
		if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintBaseGrid")) {
			try {
				paintBasicGrid(modelCanvas);
			} catch (Exception ex) {
				log.error("Error painting base grid: " + ex.getMessage(), ex);
			}
		}
		
		
		
		if (modelContext.getModelOptions().getBooleanOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintRasterPreview")) {
			try {
				paintRasterPlot(modelCanvas);
			} catch (Exception ex) {
				log.error("Error painting raster grid: " + ex.getMessage(), ex);
			}
		}
		
	}
	

	

	protected void paintLightSourceLines(ModelCanvas canvas) throws Exception
	{
		
		
		if (modelContext.getLightingContext() == null || !modelContext.getLightingContext().isLightingEnabled()) {
			log.info("Lighting not enabled, skipping light source lines");
			return;
		}
		
		double minElevation = modelContext.getRasterDataContext().getDataMinimumValue() - 10;
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		double latitudeResolution = modelContext.getRasterDataContext().getLatitudeResolution();
		double longitudeResolution = modelContext.getRasterDataContext().getLongitudeResolution();
		double centerLatitude = (north + south) / 2.0;
		double centerLongitude = (east + west) / 2.0;
		double metersResolution = modelContext.getRasterDataContext().getMetersResolution();
		
		double latRes = modelContext.getRasterDataContext().getLatitudeResolution();
		double effLatRes = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		metersResolution = metersResolution / (latRes / effLatRes);
		
		double radiusInterval = MathExt.sqrt(MathExt.sqr(latitudeResolution) + MathExt.sqr(longitudeResolution));
		
		
		//Line line = new Line();
		
		
		double[] points = new double[3];
		double radius = MathExt.sqrt(MathExt.sqr(north - south) + MathExt.sqr(east - west));
				
		Spheres.getPoint3D(solarAzimuth, solarElevation, radius, points);
	
		double latitude = centerLatitude + points[0];
		double longitude = centerLongitude - points[2];
		double resolution = (points[1] / radiusInterval);
		double elevation = (resolution * metersResolution);

		
		Vertex vMid = createVertex(centerLatitude, centerLongitude, minElevation, sourceLineColor);
		Vertex vHigh = createVertex(latitude, longitude, elevation, sourceLineColor);
		Vertex vLow = createVertex(latitude, longitude, minElevation, sourceLineColor);
		
		Edge e = new Edge(vMid, vHigh);
    	Line l = new Line();
    	l.addEdge(e);
    	canvas.drawShape(l, sourceLineColor);
    	
    	e = new Edge(vMid, vLow);
    	l = new Line();
    	l.addEdge(e);
    	canvas.drawShape(l, sourceLineColor);

	}
	

	protected void paintBasicGrid(ModelCanvas canvas) throws Exception
	{
		
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();
		
		if (paintGlobalBaseGrid) {
			north = 90;
			south = -90;
			east = 180;
			west = -180;
		}
		
		Line line = new Line();

		line.addEdge(createEdge(north, west, -1.0, south, west, -1.0));
		line.addEdge(createEdge(south, west, -1.0, south, east, -1.0));
		line.addEdge(createEdge(south, east, -1.0, north, east, -1.0));
		line.addEdge(createEdge(north, east, -1.0, north, west, -1.0));
		
		
		double strips = 10.0;
		double slices = 20.0;
		
		double strip_step = (north - south) / strips;
		double slice_step = (east - west) / slices;
		
		Edge e;
		Line l;
		
		double minElevation = modelContext.getRasterDataContext().getDataMinimumValue() - 10;
		
		for (double phi = south; phi < north/* - strip_step*/; phi +=strip_step) {
            for (double theta = west; theta < east/*-slice_step*/; theta+=slice_step) {
            	
            	
            	Vertex v0 = createVertex(phi, theta, minElevation, baseGridColor);
            	Vertex v1 = createVertex(phi, theta+slice_step, minElevation, baseGridColor);
            	Vertex v2 = createVertex(phi+strip_step, theta, minElevation, baseGridColor);
            	Vertex v3 = createVertex(phi+strip_step, theta+slice_step, minElevation, baseGridColor);
            	
            	e = new Edge(v0, v1);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
            	
            	
            	e = new Edge(v0, v2);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
            	
            	e = new Edge(v1, v3);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
            	
            	e = new Edge(v2, v3);
            	l = new Line();
            	l.addEdge(e);
            	canvas.drawShape(l, baseGridColor);
			}
			
		}

	}
	
	
	protected void paintRasterPlot(ModelCanvas canvas) throws Exception
	{
		int[] rgba = new int[4];
		rgba[3] = 255;
		

		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		
		if (rasterDataContext.getRasterDataListSize() == 0) {
			return;
		}
		
		
		double north = modelContext.getNorth();
		double south = modelContext.getSouth();
		double east = modelContext.getEast();
		double west = modelContext.getWest();

		double maxLon = east - longitudeResolution;
		double minLat = south + latitudeResolution;
		
		TriangleStrip strip = new TriangleStrip();
		
		
			
		double lastElevN = rasterDataContext.getDataMinimumValue();
		double lastElevS = rasterDataContext.getDataMinimumValue();	
		
		
		
		for (double lat = north; lat >= minLat; lat-=latitudeResolution) {
			strip.reset();
			
			for (double lon = west; lon <= maxLon; lon+=longitudeResolution) {
				double nwLat = lat;
				double nwLon = lon;

				double swLat = lat - latitudeResolution;
				double swLon = lon;

				double elev = 0;
				
				// NW
				elev = calculateShadedColor(nwLat, nwLon, rgba);
				if (elev == DemConstants.ELEV_NO_DATA) {
					rgba[0] = 255;
					rgba[1] = 255;
					rgba[2] = 255;
					rgba[3] = 255;
					elev = lastElevN;
				} else {
					lastElevN = elev;
				}
				Vertex nwVtx = createVertex(nwLat, nwLon, elev, rgba);
				
				// SW
				elev = calculateShadedColor(swLat, swLon, rgba);
				if (elev == DemConstants.ELEV_NO_DATA) {
					rgba[0] = 255;
					rgba[1] = 255;
					rgba[2] = 255;
					rgba[3] = 255;
					elev = lastElevS;
				} else {
					lastElevS = elev;
				}
				Vertex swVtx = createVertex(swLat, swLon, elev, rgba);
				
				/*
				// NW
				if ((elev = calculateShadedColor(nwLat, nwLon, rgba)) == DemConstants.ELEV_NO_DATA) 
					continue;
				Vertex nwVtx = createVertex(nwLat, nwLon, elev, rgba);
				
				// SW
				if ((elev = calculateShadedColor(swLat, swLon, rgba)) == DemConstants.ELEV_NO_DATA)
					continue;
				Vertex swVtx = createVertex(swLat, swLon, elev, rgba);
				*/
				
				
				strip.addVertex(nwVtx);
				strip.addVertex(swVtx);
				
				
			}
			//log.info("Filling " + strip.getTriangleCount() + " triangles with " + strip.getVertexCount() + " vertecies");
			canvas.fillShape(strip);
			
		}
		
	}
	
	protected double calculateShadedColor(double latitude, double longitude, int[] rgba) throws DataSourceException, RayTracingException
	{
		double eLat = latitude;
		double eLon = longitude + longitudeResolution;
		
		double sLat = latitude - latitudeResolution;
		double sLon = longitude;
		
		double wLat = latitude;
		double wLon = longitude - longitudeResolution;
		
		double nLat = latitude + latitudeResolution;
		double nLon = longitude;
		
		
		double midElev = getElevation(latitude, longitude);
		
		
		
		if (midElev == DemConstants.ELEV_NO_DATA)
		//	midElev = 0;
			return DemConstants.ELEV_NO_DATA;
		
		
		double min = modelContext.getRasterDataContext().getDataMinimumValue();
		double max = modelContext.getRasterDataContext().getDataMaximumValue();
		modelColoring.getGradientColor(midElev, min, max, colorBufferA);
		
		
		/*
		 * Ok, just trust me on these ones.... 
		 */
		
		if (lightingEnabled) {
			double eElev = getElevation(eLat, eLon);
			double sElev = getElevation(sLat, sLon);
			double wElev = getElevation(wLat, wLon);
			double nElev = getElevation(nLat, nLon);
			
			
			if (eElev == DemConstants.ELEV_NO_DATA)
				eElev = midElev;
			if (sElev == DemConstants.ELEV_NO_DATA)
				sElev = midElev;
			if (wElev == DemConstants.ELEV_NO_DATA)
				wElev = midElev;
			if (nElev == DemConstants.ELEV_NO_DATA)
				nElev = midElev;
			
			setUpLightSource(latitude, longitude, 0.0, 0.0, recalcLightOnEachPoint);
			resetBuffers(latitude, longitude);
			
			// NW Normal
			calculateNormal(0.0, wElev, midElev, nElev, CornerEnum.SOUTHEAST, normal);
			pointNormal[0] = normal[0];
			pointNormal[1] = normal[1];
			pointNormal[2] = normal[2];
			
			// SW Normal
			calculateNormal(wElev, 0.0, sElev, midElev, CornerEnum.NORTHEAST, normal);
			pointNormal[0] += normal[0];
			pointNormal[1] += normal[1];
			pointNormal[2] += normal[2];
			
			// SE Normal
			calculateNormal(midElev, sElev, 0.0, eElev, CornerEnum.NORTHWEST, normal);
			pointNormal[0] += normal[0];
			pointNormal[1] += normal[1];
			pointNormal[2] += normal[2];
			
			// NE Normal
			calculateNormal(nElev, midElev, eElev, 0.0, CornerEnum.SOUTHWEST, normal);
			pointNormal[0] += normal[0];
			pointNormal[1] += normal[1];
			pointNormal[2] += normal[2];
			
			normal[0] = pointNormal[0] / 4.0;
			normal[1] = pointNormal[1] / 4.0;
			normal[2] = pointNormal[2] / 4.0;
			
			double dot = calculateDotProduct(latitude, longitude);
			
			if (this.rayTraceShadows) {
				if (lightSourceRayTracer.isRayBlocked(this.solarElevation, this.solarAzimuth, latitude, longitude, midElev)) {
					// I'm not 100% happy with this method...
					dot = dot - (2 * shadowIntensity);
					if (dot < -1.0) {
						dot = -1.0;
					}
				}
			}
			
			copyRgba(colorBufferA, rgba);
			ColorAdjustments.adjustBrightness(rgba, dot);
			
			//copyRgba(colorBufferA, colorBufferB);
			//ColorAdjustments.adjustBrightness(colorBufferB, dot);
			//ColorAdjustments.interpolateColor(colorBufferA, colorBufferB, rgba, lightingMultiple);
		
		} else {
			copyRgba(colorBufferA, rgba);
		}
		rgba[3] = 255;
		
		return midElev;
	}
	
	protected void calculateNormal(double nw, double sw, double se, double ne, CornerEnum corner, double[] normal)
	{
		backLeftPoints[1] = nw * lightingMultiple;
		backRightPoints[1] = ne * lightingMultiple;
		frontLeftPoints[1] = sw * lightingMultiple;
		frontRightPoints[1] = se * lightingMultiple;
		
		if (corner == CornerEnum.NORTHWEST) {
			perspectives.calcNormal(backLeftPoints, frontLeftPoints, backRightPoints, normal);
		} else if (corner == CornerEnum.SOUTHWEST) {
			perspectives.calcNormal(backLeftPoints, frontLeftPoints, frontRightPoints, normal);
		} else if (corner == CornerEnum.SOUTHEAST) {
			perspectives.calcNormal(frontLeftPoints, frontRightPoints, backRightPoints, normal);
		} else if (corner == CornerEnum.NORTHEAST) {
			perspectives.calcNormal(backLeftPoints, frontRightPoints, backRightPoints, normal);
		}
		
	}
	
	
	protected double calculateDotProduct(double latitude, double longitude)
	{
		double dot = 0;
		double terrainDotProduct = calculateTerrainDotProduct();
		double sphericalDotProduct = calculateSphericalDotProduct(latitude, longitude);
		
		dot = (terrainDotProduct + sphericalDotProduct) / 2;
		return dot;
		
	}
	
	
	protected double calculateSphericalDotProduct(double latitude, double longitude)
	{
		double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius();
		}
		
		
		Spheres.getPoint3D(longitude+180, latitude, meanRadius, pointNormal);
		
		
		//double resolutionMeters = RasterDataContext.getMetersResolution(meanRadius, latitude, longitude, latitudeResolution, longitudeResolution);
		//double xzRes = (resolutionMeters / 2.0);
		
		//pointNormal[0] = 0.0;
		//pointNormal[1] = meanRadius;
		//pointNormal[2] = 0.0;
		//Vector.rotate(x, y, z, pointNormal);
		
		
		perspectives.normalize(pointNormal, normal);
		double dot = perspectives.dotProduct(pointNormal, sunsource);
		dot = Math.pow(dot, spotExponent);
		
		if (dot > 0) {
			dot *= relativeLightIntensity;
		} else if (dot < 0) {
			dot *= relativeDarkIntensity;
		}
		
		return dot;
	}
	
	protected double calculateTerrainDotProduct()
	{
		if (!sunIsUp) {
			return -1;
		}
		
		double dot = perspectives.dotProduct(normal, sunsource);
		dot = Math.pow(dot, spotExponent);
		
		if (dot > 0) {
			dot *= relativeLightIntensity;
		} else if (dot < 0) {
			dot *= relativeDarkIntensity;
		}

		return dot;
	}
	protected void setUpLightSource(double latitude, double longitude, double solarElevation, double solarAzimuth, boolean force)
	{

		if (force && lightSourceType == LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION) {
			setUpLightSourceBasic(solarElevation, solarAzimuth);
		} else if (lightSourceType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME) {
			setUpLightSourceOnDate(latitude, longitude);
		}
		
		
		
	}
	
	protected void setUpLightSourceOnDate(double latitude, double longitude)
	{
		// This stuff in here probably can be vastly optimized...
		
		//int timezone = (int) Math.floor((longitude / 180.0) * 12.0);

		//datetime.setTimezone(timezone);
		
		latitudeCoordinate.fromDecimal(latitude);
		longitudeCoordinate.fromDecimal(longitude);

		
		solarCalculator.update();
		solarCalculator.setLatitude(latitudeCoordinate);
		solarCalculator.setLongitude(longitudeCoordinate);
		//double solarElevation = solarCalculator.solarElevationAngle();
		//double solarAzimuth = solarCalculator.solarAzimuthAngle();
		solarAzimuth = solarCalculator.solarAzimuthAngle();
		solarElevation = solarCalculator.solarElevationAngle();
		double solarZenith = solarCalculator.solarZenithAngle();
		
		if (solarZenith > 130.0) {
			sunIsUp = false;
		} else {
			sunIsUp = true;
		}
		sunIsUp = true;
		setUpLightSourceBasic(solarElevation, solarAzimuth);
		
	}
	
	protected void setUpLightSourceBasic(double solarElevation, double solarAzimuth)
	{
		
		sunsource[0] = 0.0;
		sunsource[1] = 0.0;
		sunsource[2] = -149598000000.0;
		Vector.rotate(solarElevation, -solarAzimuth, 0, sunsource);
		
	}
	
	protected void resetBuffers(double latitude, double longitude)
	{
		//double effLatRes = modelContext.getRasterDataContext().getEffectiveLatitudeResolution();
		//double effLonRes = modelContext.getRasterDataContext().getEffectiveLongitudeResolution();
		
		//Planet planet = PlanetsRegistry.getPlanet(modelContext.getModelOptions().getOption(ModelOptionNamesEnum.PLANET));
		double meanRadius = DemConstants.EARTH_MEAN_RADIUS;
		
		if (planet != null) {
			meanRadius = planet.getMeanRadius();
		}
		
		double resolutionMeters = RasterDataContext.getMetersResolution(meanRadius, latitude, longitude, latitudeResolution, longitudeResolution);
		double xzRes = (resolutionMeters / 2.0);
		
		backLeftPoints[0] = -xzRes;
		backLeftPoints[1] = 0.0;
		backLeftPoints[2] = -xzRes;
		
		backRightPoints[0] = xzRes;
		backRightPoints[1] = 0.0;
		backRightPoints[2] = -xzRes;
		
		frontLeftPoints[0] = -xzRes;
		frontLeftPoints[1] = 0.0;
		frontLeftPoints[2] = xzRes;
		
		frontRightPoints[0] = xzRes;
		frontRightPoints[1] = 0.0;
		frontRightPoints[2] = xzRes;
		
	}
	
	protected double getElevation(double latitude, double longitude) throws DataSourceException
	{
		
		double elevation = elevationMap.get(latitude, longitude, DemConstants.ELEV_NO_DATA);
		if (elevation != DemConstants.ELEV_NO_DATA)
			return elevation;
	
		if (getStandardResolutionElevation) {
			elevation = modelContext.getRasterDataContext().getDataStandardResolution(latitude, longitude, averageOverlappedData, interpolateData);
		} else {
			elevation = modelContext.getRasterDataContext().getDataAtEffectiveResolution(latitude, longitude, averageOverlappedData, interpolateData);
		}
		
		elevationMap.put(latitude, longitude, elevation);
		return elevation;
	}
	
	protected Edge createEdge(double lat0, double lon0, double elev0, double lat1, double lon1, double elev1) throws MapProjectionException
    {
		return createEdge(lat0, lon0, elev0, null, lat1, lon1, elev1, null);
    }
	
	protected Edge createEdge(double lat0, double lon0, double elev0, int[] rgba0, double lat1, double lon1, double elev1, int[] rgba1) throws MapProjectionException
    {

		Vertex v0 = createVertex(lat0, lon0, elev0, rgba0);
		Vertex v1 = createVertex(lat1, lon1, elev1, rgba1);
    	return new Edge(v0, v1);
    	
    }
	
	protected Vertex createVertex(double lat, double lon, double elev, int[] rgba) throws MapProjectionException
	{
    	projection.getPoint(lat, lon, elev, point);
    	
    	double x = point.column;
    	double y = point.row;
    	double z = point.z;
    	//double z = modelContext.getRasterDataContext().getDataMinimumValue();//point.z;
		
    	Vertex v = new Vertex(x, y, z, rgba);
    	return v;
	}
	
	public void determineDataRangeLowRes() throws DataSourceException
	{
		
		RasterDataContext rasterDataContext = modelContext.getRasterDataContext();
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		if (rasterDataContext.getRasterDataListSize() > 0) {
		
			double north = rasterDataContext.getNorth();
			double south = rasterDataContext.getSouth();
			double east = rasterDataContext.getEast();
			double west = rasterDataContext.getWest();
			
			//double latStep = (north - south - modelContext.getRasterDataContext().getEffectiveLatitudeResolution()) / latitudeSlices;
			//double lonStep = (east - west - modelContext.getRasterDataContext().getEffectiveLongitudeResolution()) / longitudeSlices;
			
			
			
			for (double lon = west; lon < east - rasterDataContext.getLongitudeResolution(); lon+=longitudeResolution) {
				for (double lat = north; lat > south + rasterDataContext.getLatitudeResolution(); lat-=latitudeResolution) {
					double elevation = getElevation(lat, lon);
					
					if (!Double.isNaN(elevation) && elevation != DemConstants.ELEV_NO_DATA) {
						min = MathExt.min(elevation, min);
						max = MathExt.max(elevation, max);
					}
					
				}
				
			}
		} else {
			max = 0;
			min = 0;
		}
		
		log.info("Simple renderer data maximum: " + max);
		log.info("Simple renderer data minimum: " + min);
		
		rasterDataContext.setDataMaximumValue(max);
		rasterDataContext.setDataMinimumValue(min);
		
		
	}
	
	protected void copyRgba(int[] rgba0, int[] rgba1)
	{
		rgba1[0] = rgba0[0];
		rgba1[1] = rgba0[1];
		rgba1[2] = rgba0[2];
		rgba1[3] = rgba0[3];
		
	}
	
}
