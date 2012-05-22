package us.wthr.jdem846.model.processing.shading;

import java.util.Calendar;
import java.util.TimeZone;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.exception.RayTracingException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarCalculator;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.ModelGrid;
import us.wthr.jdem846.model.ModelPoint;
import us.wthr.jdem846.model.ModelPointHandler;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.annotations.GridProcessing;
import us.wthr.jdem846.model.processing.AbstractGridProcessor;
import us.wthr.jdem846.model.processing.GridProcessingTypesEnum;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorOptionModel;
import us.wthr.jdem846.model.processing.dataload.CornerEnum;
import us.wthr.jdem846.model.processing.shading.RayTracing.RasterDataFetchHandler;
import us.wthr.jdem846.rasterdata.RasterDataContext;


@GridProcessing(id="us.wthr.jdem846.model.processing.coloring.HillshadingProcessor",
				name="Hillshading Process",
				type=GridProcessingTypesEnum.SHADING,
				optionModel=HillshadingOptionModel.class,
				enabled=true
				)
public class HillshadingProcessor extends AbstractGridProcessor implements GridProcessor, ModelPointHandler
{
	private static Log log = Logging.getLog(HillshadingProcessor.class);

	protected double relativeLightIntensity;
	protected double relativeDarkIntensity;
	protected int spotExponent;
	
	private double latitudeResolution;
	private double longitudeResolution;
	
	private double[] normal = new double[3];
	
	protected double sunsource[] = new double[3];
	protected double solarElevation;
	protected double solarAzimuth;
	protected double solarZenith;
	
	protected int[] rgbaBuffer = new int[4];
	
	protected Perspectives perspectives = new Perspectives();
	
	private double lightingMultiple = 1.0;
	
	private Planet planet;
	
	double north;
	double south;
	double east;
	double west;
	

	protected double lightZenith;
	protected double darkZenith;
	protected LightSourceSpecifyTypeEnum lightSourceType;
	protected long lightOnDate;
	protected long lightOnTime;
	protected boolean recalcLightOnEachPoint;
	protected SolarCalculator solarCalculator;
	protected SolarPosition position;
	protected EarthDateTime datetime;
	protected Coordinate latitudeCoordinate;
	protected Coordinate longitudeCoordinate;
	protected boolean sunIsUp = false;
	
	private boolean advancedLightingControl = false;
	private LightingCalculator advancedLightingCalculator;
	private double modelRadius;
	
	protected RayTracing lightSourceRayTracer;
	protected boolean rayTraceShadows;
	protected double shadowIntensity;
	
	public HillshadingProcessor()
	{
		
	}
	
	public HillshadingProcessor(ModelContext modelContext, ModelGrid modelGrid)
	{
		super(modelContext, modelGrid);
	}
	
	@Override
	public void prepare() throws RenderEngineException
	{
		
		HillshadingOptionModel optionModel = (HillshadingOptionModel) this.getProcessOptionModel();
		
		relativeLightIntensity = optionModel.getLightIntensity();
		relativeDarkIntensity = optionModel.getDarkIntensity();
		spotExponent = optionModel.getSpotExponent();

		advancedLightingControl = optionModel.getAdvancedLightingControl();
		advancedLightingCalculator = new LightingCalculator(optionModel.getEmmisive(), optionModel.getAmbient(), optionModel.getDiffuse(), optionModel.getSpecular());

		
		
		if (planet != null) {
			modelRadius = planet.getMeanRadius() * 1000;
		} else {
			modelRadius = DemConstants.EARTH_MEAN_RADIUS * 1000;
		}

		
		/*
		double t = (emmisive + ambient + diffuse + specular);
		emmisive = emmisive / t;
		ambient = ambient / t;
		diffuse = diffuse / t;
		specular = specular / t;
		*/
		
		/*
		 *private double emmisive;
	private double ambient;
	private double diffuse;
	private double specular; 
		 */
		
		latitudeResolution = getModelDimensions().getOutputLatitudeResolution();
		longitudeResolution = getModelDimensions().getOutputLongitudeResolution();
		
		north = getGlobalOptionModel().getNorthLimit();
		south = getGlobalOptionModel().getSouthLimit();
		east = getGlobalOptionModel().getEastLimit();
		west = getGlobalOptionModel().getWestLimit();
		
		
		planet = PlanetsRegistry.getPlanet(getGlobalOptionModel().getPlanet());
		
		solarAzimuth = optionModel.getSourceLocation().getAzimuthAngle();
		solarElevation = optionModel.getSourceLocation().getElevationAngle();
		
		//solarAzimuth = 270.0;//modelContext.getLightingContext().getLightingAzimuth();
		//solarElevation = 25.0;//modelContext.getLightingContext().getLightingElevation();

		
		lightSourceType = LightSourceSpecifyTypeEnum.getByOptionValue(optionModel.getSourceType());
		
		lightOnTime = optionModel.getSunlightTime().getTime();//optionModel.getSunlightTime();
		lightOnDate = optionModel.getSunlightDate().getDate();//optionModel.getSunlightDate();
		lightOnDate += lightOnTime;
		
		recalcLightOnEachPoint = optionModel.isRecalcLightForEachPoint();
		lightZenith = optionModel.getLightZenith();
		darkZenith = optionModel.getDarkZenith();
		
		
		if (lightSourceType == LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION) {
			sunIsUp = true;
			setUpLightSource(0, 0, solarElevation, solarAzimuth, true);
		}
		
		if (lightSourceType == LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME) {
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			
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

		lightingMultiple = optionModel.getLightMultiple();
		rayTraceShadows = optionModel.isRayTraceShadows();
		shadowIntensity = optionModel.getShadowIntensity();
		if (rayTraceShadows) {
			lightSourceRayTracer = new RayTracing(
					modelContext,
					new RasterDataFetchHandler() {
						public double getRasterData(double latitude, double longitude) throws Exception {
							return getElevationAtPoint(latitude, longitude);
						}
			});
		} else {
			lightSourceRayTracer = null;
		}
		
	}

	@Override
	public void process() throws RenderEngineException
	{
		super.process();
	}
	
	@Override
	public void onCycleStart() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModelLatitudeStart(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModelPoint(double latitude, double longitude)
			throws RenderEngineException
	{

		setUpLightSource(latitude, longitude, 0, 0, recalcLightOnEachPoint);
		
		ModelPoint modelPoint = modelGrid.get(latitude, longitude);

		//calculateDotProduct(modelPoint, latitude, longitude);
		processPointColor(modelPoint, latitude, longitude);
		
		
	}

	@Override
	public void onModelLatitudeEnd(double latitude)
			throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCycleEnd() throws RenderEngineException
	{
		// TODO Auto-generated method stub
		
	}


	

	
	protected double calculateDotProduct(ModelPoint modelPoint, double latitude, double longitude) throws RenderEngineException
	{
		modelPoint.getNormal(normal);
		double dot = perspectives.dotProduct(normal, sunsource);
		
		
		double lower = lightZenith;
		double upper = darkZenith;
		
		if (solarZenith > lower && solarZenith <= upper) {
			double range = (solarZenith - lower) / (upper - lower);
			dot = dot - (2 * range);
		} else if (solarZenith > upper) {
			dot = dot - (2 * 1.0);
		}
		if (dot < -1.0) {
			dot = -1.0;
		}
		
		
		try {
			double blockAmt = calculateRayTracedShadow(modelPoint, latitude, longitude);
			dot = dot - (2 * shadowIntensity * blockAmt);
			if (dot < -1.0) {
				dot = -1.0;
			}
		} catch (RayTracingException ex) {
			throw new RenderEngineException("Error ray tracing shadows: " + ex.getMessage(), ex);
		}
		
		return dot;
	
		/*
		if (spotExponent != 1) {
			dot = MathExt.pow(dot, spotExponent);
		}
		*/
		
		/*
		if (dot >= 0) {
			dot = dot + ((lightingMultiple / 100.0) * (1.0 - dot));
		} else {
			dot = dot - ((lightingMultiple / 100.0) * (1.0 - MathExt.abs(dot)));
		}
		*/
		
		//modelPoint.setDotProduct(dot);
		
	}
	
	protected double calculateRayTracedShadow(ModelPoint modelPoint, double latitude, double longitude) throws RayTracingException
	{
		if (this.rayTraceShadows) {	
			double blockAmt = lightSourceRayTracer.isRayBlocked(this.solarElevation, this.solarAzimuth, latitude, longitude, modelPoint.getElevation());
			return blockAmt;
		} else {
			return 0.0;
		}
	}
	

	
	protected void processPointColor(ModelPoint modelPoint, double latitude, double longitude) throws RenderEngineException
	{
		
		modelPoint.getRgba(rgbaBuffer);
		
		if (advancedLightingControl) {
			advancedLightingCalculator.calculateColor(modelPoint, 
													latitude, 
													longitude, 
													modelRadius, 
													spotExponent, 
													sunsource,
													rgbaBuffer);
			
			
		} else {
			double dot = calculateDotProduct(modelPoint, latitude, longitude);
			
			if (dot > 0) {
				dot *= relativeLightIntensity;
			} else if (dot < 0) {
				dot *= relativeDarkIntensity;
			}
			
			if (spotExponent != 1) {
				dot = MathExt.pow(dot, spotExponent);
			}
			ColorAdjustments.adjustBrightness(rgbaBuffer, dot);
		}

		modelPoint.setRgba(rgbaBuffer);
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
		
		solarAzimuth = solarCalculator.solarAzimuthAngle();
		solarElevation = solarCalculator.solarElevationAngle();
		solarZenith = solarCalculator.solarZenithAngle();

		if (solarZenith > darkZenith) {
			sunIsUp = false;
		} else {
			sunIsUp = true;
		}
		
		setUpLightSourceBasic(solarElevation, solarAzimuth);
		
	}
	
	protected void setUpLightSourceBasic(double solarElevation, double solarAzimuth)
	{
		
		sunsource[0] = 0.0;
		sunsource[1] = 0.0;
		sunsource[2] = -149598000000.0; // One AU in meters

		Vectors.rotate(solarElevation, -solarAzimuth, 0, sunsource);
		
	}
	

	public boolean rayTraceShadows()
	{
		return rayTraceShadows;
	}

	public void setRayTraceShadows(boolean rayTraceShadows)
	{
		this.rayTraceShadows = rayTraceShadows;
	}

	public boolean recalcLightOnEachPoint()
	{
		return recalcLightOnEachPoint;
	}

	public void setRecalcLightOnEachPoint(boolean recalcLightOnEachPoint)
	{
		this.recalcLightOnEachPoint = recalcLightOnEachPoint;
	}
	
	
	
	
	
}
