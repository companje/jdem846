package us.wthr.jdem846.model.processing.util;

import java.util.Calendar;
import java.util.TimeZone;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
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
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.processing.shading.RayTracing;

public class SunlightPositioning
{
	private static Log log = Logging.getLog(SunlightPositioning.class);
	
	private ModelContext modelContext;
	private ModelPointGrid modelGrid;
	private ModelGridDimensions modelDimensions;
	private GlobalOptionModel globalOptionModel;
	
	
	
	protected double sunsource[] = new double[3];
	protected double solarElevation;
	protected double solarAzimuth;
	protected double solarZenith;
	
	protected int[] rgbaBuffer = new int[4];
	
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

	private double modelRadius;
	
	protected RayTracing lightSourceRayTracer;
	protected boolean rayTraceShadows;
	protected double shadowIntensity;

	
	protected ViewPerspective viewPerspective;
	
	public SunlightPositioning(ModelContext modelContext, ModelPointGrid modelGrid, long lightOnDate, ViewPerspective viewPerspective)
	{
		this.modelContext = modelContext;
		this.modelGrid = modelGrid;
		this.globalOptionModel = modelContext.getModelProcessManifest().getGlobalOptionModel();
		this.lightOnDate = lightOnDate;
		
		planet = PlanetsRegistry.getPlanet(globalOptionModel.getPlanet());
		if (planet != null) {
			modelRadius = planet.getMeanRadius() * 1000;
		} else {
			modelRadius = DemConstants.EARTH_MEAN_RADIUS * 1000;
		}
		
		modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);
		
		this.viewPerspective = viewPerspective;
		
		//lightOnDate = System.currentTimeMillis();
		this.lightOnDate = lightOnDate;
		this.lightOnTime = lightOnDate - (long) MathExt.floor((double)lightOnDate / 86400000.0) * 86400000;
		
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
		

		
	}
	
	
	public void getLightPositionByCoordinates(double latitude, double longitude, double[] xyz)
	{
		latitudeCoordinate.fromDecimal(latitude);
		longitudeCoordinate.fromDecimal(longitude);

		
		solarCalculator.update();
		solarCalculator.setLatitude(latitudeCoordinate);
		solarCalculator.setLongitude(longitudeCoordinate);
		
		double declination = solarCalculator.declinationOfSun();
		double hourAngle = solarCalculator.hourAngle();
		solarZenith = solarCalculator.solarZenithAngle(declination, hourAngle);
		solarAzimuth = solarCalculator.solarAzimuthAngle(declination, hourAngle, solarZenith);
		solarElevation = 90 - solarZenith;
		
		
		
		//solarAzimuth = solarCalculator.solarAzimuthAngle();
		//solarElevation = solarCalculator.solarElevationAngle();
		//solarElevation = solarCalculator.correctedSolarElevation();
		//solarZenith = solarCalculator.solarZenithAngle();
		
		//double declination = solarCalculator.declinationOfSun();
		//double hourAngle = solarCalculator.hourAngle();
		
		if (solarZenith > darkZenith) {
			sunIsUp = false;
		} else {
			sunIsUp = true;
		}
		
		//double rotateY = 180.0 - (((double) lightOnTime / 86400000.0) * 360.0);
		//getLightPositionByAngles(declination, longitude-hourAngle, xyz);
		getLightPositionByAngles(solarElevation, solarAzimuth, xyz);
	}
	
	public void getLightPositionByAngles(double solarElevation, double solarAzimuth, double[] xyz)
	{
		sunsource[0] = 0.0;
		sunsource[1] = 0.0;
		sunsource[2] = -149598000000.0; // One AU in meters

		
		//Vectors.rotate(0.0, viewPerspective.getRotateY()-90.0+solarAzimuth, 0.0, sunsource);
		//Vectors.rotate(viewPerspective.getRotateX()+solarElevation, 0.0, 0.0, sunsource);
		Vectors.rotate(solarElevation, -solarAzimuth, 0, sunsource);
		
		xyz[0] = sunsource[0];
		xyz[1] = sunsource[1];
		xyz[2] = sunsource[2];
	}
	

	
	
}
