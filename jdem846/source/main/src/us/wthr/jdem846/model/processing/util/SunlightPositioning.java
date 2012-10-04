package us.wthr.jdem846.model.processing.util;

import java.util.Calendar;
import java.util.TimeZone;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.CardinalDirectionEnum;
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
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelPointGrid;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.processing.shading.RayTracing;

public class SunlightPositioning
{
	private static Log log = Logging.getLog(SunlightPositioning.class);
	
	
	private static double SUN_RADIUS = 0.26667;
	
	
	
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
	
	
	
	protected double limitDegrees(double degrees)
	{
		double limited;
		degrees /= 360.0;
		limited = 360.0 * (degrees - MathExt.floor(degrees));
		if (limited < 0)
			limited += 360.0;
		return limited;
	}
	

	protected double julianDay(EarthDateTime datetime)
	{
		return julianDay(datetime.getYear(), 
						datetime.getMonth(), 
						datetime.getDay(),
						datetime.getHour(), 
						datetime.getMinute(), 
						datetime.getSecond(),
						datetime.getTimezone());
	}
	
	protected double julianDay(int year, int month, int day, int hour, int minute, int second, double tz)
	{
		double day_decimal, julian_day, a;
		
		day_decimal = day + (hour - tz + (minute + second/60.0)/60.0)/24.0;
		
		if (month < 3) {
			month += 12;
			year--;
		}
		
		julian_day = MathExt.floor(365.25*(year+4716.0)) + MathExt.floor(30.6001*(month+1)) + day_decimal - 1524.5;
		if (julian_day > 2299160.0) {
			a = MathExt.floor(year/100);
			julian_day += (2 - a + MathExt.floor(a/4));
		}
		
		return julian_day;
	}
	
	double julianCentury(double jd)
	{
		return (jd-2451545.0)/36525.0;
	}
	
	
	public void getLightPositionByCoordinates(double latitude, double longitude, Vector xyz)
	{
		latitude = 0;
		longitude = 0;

	    double jd = julianDay(datetime);
	    double jc = julianCentury(jd);

		double n = jd - 2451545.0; // Number of days from J2000.0
		double L = 280.460 + 0.9856474 * n; // Mean longitude of the Sun, corrected for the aberration of light
		double g = 357.528 + 0.9856003 * n; // Mean anomaly of the sun
		L = limitDegrees(L);
		g = limitDegrees(g);
		
		
		
		//double _2g = MathExt.radians(g * 2);
		double _g = MathExt.radians(g);
		double _L = MathExt.radians(L);

		double eclipticLongitude = L + 1.915 * MathExt.sin(_g) + 0.020 * MathExt.sin(2 * _g);
		double eclipticLatitude = 0;
		
		// Distance of the sun in astronomical units
		double R = 1.00014 - 0.01671 * MathExt.cos(_g) - 0.00014 * MathExt.cos(2 * _g); 
		
		// Obliquity of the ecliptic
		double e = 23.439 - 0.0000004 * n;
		
		
		double eccentricityEarthOrbit = 0.016708634 - jc * (0.000042037 + 0.0000001267 * jc);

		double _eclipticLongitude = MathExt.radians(eclipticLongitude);
		double _eclipticLatitude = MathExt.radians(eclipticLatitude);
		
		double _e = MathExt.radians(e);

		double N = datetime.dayOfYear();
		
		double rightAscension = MathExt.atan((MathExt.sin(_eclipticLongitude) * MathExt.cos(_e) - MathExt.tan(_eclipticLatitude) * MathExt.sin(_e)) / MathExt.cos(_eclipticLongitude));
		double declination = MathExt.atan((MathExt.sin(_e) * MathExt.sin(_eclipticLongitude) * MathExt.cos(_eclipticLatitude) + MathExt.cos(_e) * MathExt.sin(_eclipticLatitude)));
		double o = -e * (MathExt.cos(MathExt.radians((360.0 / 365.0) * (N + 10.0))));
		
		
		//o = MathExt.degrees(-MathExt.asin(MathExt.radians(0.39779 * MathExt.cos(MathExt.radians(0.98565 * (N + 10) + 1.914 * MathExt.sin(MathExt.radians(0.98565 * (N - 2))))))));
		
		
		double obliquityCorrection = e + 0.00256 * MathExt.cos(MathExt.radians(125.04 - 1934.136 * jc));
        double y = MathExt.pow(MathExt.tan(MathExt.radians(obliquityCorrection)/2.0), 2);
     
        double equationOfTime = MathExt.degrees(y * MathExt.sin(2.0 * _L) - 2.0 * eccentricityEarthOrbit * MathExt.sin(_g) + 4.0 * eccentricityEarthOrbit * y * MathExt.sin(_g) * MathExt.cos(2.0 * _L) - 0.5 * y * y * MathExt.sin(4.0 * _L) - 1.25 * eccentricityEarthOrbit * eccentricityEarthOrbit * MathExt.sin(2.0 * _g)) * 4.0;    // in minutes of time
        
        double tod = datetime.toMinutes();
        double trueSolarTime = (tod+ equationOfTime + 4.0 * longitude - 60.0 * datetime.getTimezone());
		
        double ha = 0;
        if (trueSolarTime / 4.0 < 0.0)
            ha = trueSolarTime / 4.0 + 180.0;
    	else
            ha = trueSolarTime / 4.0 - 180.0;

        xyz.x = 149598000000.0; // R (AU) in meters
        xyz.y = 0.0;
        xyz.z = 0.0;
		//xyz[0] = R * 149598000000.0; // R (AU) in meters
		//xyz[1] = 0.0;
		//xyz[2] = 0.0; 
		
		double rotateY = rightAscension + (ha - longitude);
		double rotateX = declination;
		
		if (viewPerspective != null) {
			//rotateY = viewPerspective.getRotateY() + rotateY;
			//rotateX = viewPerspective.getRotateX() - rotateX;
		}
		
		//Vectors.rotate(0.0, 0.0, obliquityCorrection, xyz);
		//Vectors.rotate(rotateX, rotateY, o, xyz, Vectors.ZYX);
		Vectors.rotate(rotateX, -rotateY, o, xyz, Vectors.ZYX);
		Vectors.inverse(xyz);
		//Vectors.rotate(0.0, 0.0, o, xyz);
		//Vectors.rotate(0.0, rotateY, 0.0, xyz);
		//Vectors.rotate(rotateX, 0.0, 0.0, xyz);


		
	}
	
	protected double boundAngle(double a)
	{
		while (a > 360) {
			a -= 360.0;
		}
		
		while (a < 360) {
			a += 360.0;
		}
		
		return a;
	}
	
	public void __getLightPositionByCoordinates(double latitude, double longitude, double[] xyz)
	{
		//latitudeCoordinate.fromDecimal(0);
		//longitudeCoordinate.fromDecimal(0);
		
		double lonTime = (longitude / 180.0) * 12.0;
		//double minutes = (lonTime + 12) * 60;
		//datetime.fromMinutes(minutes);
		double hour = datetime.getHour();
		//double minute = datetime.getMinute();
		//double second = datetime.getSecond();

		double timezone = -1 * (12 - hour);
		//datetime.setTimezone(lonTime);
		
		
		latitudeCoordinate.fromDecimal(latitude);
		if (latitude < 0) {
			latitudeCoordinate.setDirection(CardinalDirectionEnum.SOUTH);
		} else {
			latitudeCoordinate.setDirection(CardinalDirectionEnum.NORTH);
		}
		
		
		
		
		longitudeCoordinate.fromDecimal(longitude);
		if (longitude < 0) {
			longitudeCoordinate.setDirection(CardinalDirectionEnum.WEST);
		} else {
			longitudeCoordinate.setDirection(CardinalDirectionEnum.EAST);
		}
		//latitudeCoordinate.fromDecimal(0);
		//longitudeCoordinate.fromDecimal(0);
		
		solarCalculator.update();
		solarCalculator.setLatitude(latitudeCoordinate);
		solarCalculator.setLongitude(longitudeCoordinate);
		
		double declination = solarCalculator.declinationOfSun();
		double hourAngle = solarCalculator.hourAngle();
		solarZenith = solarCalculator.solarZenithAngle(declination, hourAngle);
		solarAzimuth = solarCalculator.solarAzimuthAngle(declination, hourAngle, solarZenith);
		solarElevation = 90 - solarZenith;
		//double rtAscension = solarCalculator.sunRtAscension();
		

		//latitudeCoordinate.fromDecimal(latitude);
	///longitudeCoordinate.fromDecimal(longitude);


		//solarAzimuth = solarCalculator.solarAzimuthAngle();
		//solarElevation = solarCalculator.solarElevationAngle();
		//solarElevation = solarCalculator.correctedSolarElevation();
		//solarZenith = solarCalculator.solarZenithAngle();
		
		//double declination = solarCalculator.declinationOfSun();
		//double hourAngle = solarCalculator.hourAngle();
		
		//if (solarZenith > darkZenith) {
		//	sunIsUp = false;
		//} else {
		//	sunIsUp = true;
		//}
		
		//double rotateY = 180.0 - (((double) lightOnTime / 86400000.0) * 360.0);
		//getLightPositionByAngles(declination, longitude-hourAngle, xyz);
		//getLightPositionByAngles(solarElevation, solarAzimuth, xyz);
		//getLightPositionByAngles(solarZenith, hourAngle-latitude-rtAscension, xyz);
		
		//if (longitude > 0) {
		//	longitude = 180.0 + longitude;
		//} else {
		//	longitude = -1 * longitude;
		//}
		
		//double rotateY = hourAngle - longitude - rtAscension;
		//double rotateX = 0.0;//solarElevation;
		
		//double rotateY = rtAscension - 90;
		//double rotateY = hourAngle - longitude;//solarAzimuth - longitude;
		//double rotateY = hourAngle;
		//double rotateX = 0.0;//declination;//solarElevation - latitude;s
		//double rotateX = solarCalculator.correctedSolarElevation() + solarCalculator.obliquityCorrection() - 90 ;
		//solarCalculator.solarZenithAngle();//
		//double rotateY = solarCalculator.solarAzimuthAngle();
		//double rotateX = solarCalculator.solarElevationAngle();
		
		double e = solarCalculator.meanObliquityOfEcliptic();
		
		double rotateY = hourAngle;
		double rotateX = solarCalculator.declinationOfSun();
		
		
		//real rotateY = rightAscension + (ha - longitude);
		//real rotateX = declination;
		
		
		
		//rotateY = rotateY - longitude;
		//rotateX = rotateX - latitude;
		
		boolean doLog = false;
		if (doLog) {
		log.info("Latitude: " + latitude 
				+ ", Longitude: " + longitude 
				+ ", Obliquity: " + solarCalculator.obliquityCorrection() 
				+ ", Declination: " + declination 
				+ ", Elevation: " + solarCalculator.correctedSolarElevation()
				+ ", Azimuth: " + solarCalculator.solarAzimuthAngle()
				+ ", Zenith: " + solarCalculator.solarZenithAngle());
		}
		//double rotateX = solarCalculator.solarElevationAngle();
		//double rotateX = declination - solarCalculator.obliquityCorrection();;
		//double rotateX = 23.4 + solarCalculator.obliquityCorrection();
		//double rotateX = 0;//declination;
		
		/*
		double n = solarCalculator.getJulianCentury() - 2451545.0;
		double L = 280.460 + 0.9856474 * n;
		double g = 357.528 + 0.9856003 * n;
		double e = solarCalculator.obliquityCorrection();
		while (L > 360) {
			L -= 360;
		}
		
		while (g > 360) {
			g -= 360;
		}
		
		while(L < 0 ) {
			L += 360;
		}
		
		while (g < 0) {
			g += 360;
		}
		
		double eclipticLongitude = L + 1.915 * MathExt.degrees(MathExt.sin(MathExt.radians(g))) + 0.020 * MathExt.degrees(MathExt.sin(2 * MathExt.radians(g)));
		double R = 1.00014 - 0.01671 * MathExt.cos(MathExt.radians(g)) - 0.00014 * MathExt.cos(2 * MathExt.radians(g));
		
		double X = R * MathExt.cos(MathExt.radians(eclipticLongitude));
		double Y = R * MathExt.cos(MathExt.radians(e)) * MathExt.sin(MathExt.radians(eclipticLongitude));
		double Z = R * MathExt.sin(MathExt.radians(e)) * MathExt.sin(MathExt.radians(eclipticLongitude));
		
		
		//declination = MathExt.asin(MathExt.sin(solarCalculator.obliquityCorrection() * MathExt.sin(MathExt.radians(eclipticLongitude))));
		
		double rotateY = eclipticLongitude;
		double rotateX = declination;
		
		
		log.info("Lat: " + eclipticLongitude + ", R: " + R + ", X: " + X + ", Y: " + Y + ", Z: " + Z);
		
		sunsource[0] = X;
		sunsource[1] = Y;
		sunsource[2] = Z; // One AU in meters
		*/
		sunsource[0] = 149598000000.0;
		sunsource[1] = 0.0;
		sunsource[2] = 0; // One AU in meters
		
		//sunsource[0] = 0.0;
		//sunsource[1] = 0.0;
		//sunsource[2] = -149598000000.0; // One AU in meters
		
		//Vectors.rotate(0.0, , 0.0, sunsource);
		//Vectors.rotate(, 0.0, 0.0, sunsource);
		
		//Vectors.rotate(0.0, , 0, sunsource);
		
		if (viewPerspective != null) {
			//rotateY += viewPerspective.getRotateY();
			//rotateX = viewPerspective.getRotateX() + rotateX;
		}
		Vectors.rotate(rotateX, rotateY, 0.0, sunsource, Vectors.ZYX);
		Vectors.inverse(sunsource);
		
		//Vectors.rotate(0.0, rotateY, 0.0, sunsource);
		//Vectors.rotate(rotateX, 0.0, 0.0, sunsource);
		//Vectors.rotate(, 0.0, 0, sunsource);
		
		//sunsource[0] = 149598000000.0;
		//sunsource[1] = 0.0;
		//sunsource[2] = 0; // One AU in meters
		
		//Vectors.rotate(0.0, longitude, 0.0, sunsource);
		//Vectors.rotate(latitude, 0.0, 0.0, sunsource);
		
		//Vectors.rotate(0.0, viewPerspective.getRotateY(), 0.0, sunsource);
		//Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0.0, sunsource);
		
		xyz[0] = sunsource[0];
		xyz[1] = sunsource[1];
		xyz[2] = sunsource[2];
	}
	
	public void getLightPositionByAngles(double solarElevation, double solarAzimuth, double[] xyz)
	{
		//sunsource[0] = 0.0;
		//sunsource[1] = 0.0;
		//sunsource[2] = -149598000000.0; // One AU in meters
		
		sunsource[0] = 149598000000.0;
		sunsource[1] = 0.0;
		sunsource[2] = 0; // One AU in meters
		
		//Vectors.rotate(0.0, viewPerspective.getRotateY()-90.0+solarAzimuth, 0.0, sunsource);
		//Vectors.rotate(viewPerspective.getRotateX()+solarElevation, 0.0, 0.0, sunsource);
		Vectors.rotate(0.0, solarAzimuth, 0, sunsource);
		Vectors.rotate(solarElevation, 0.0, 0, sunsource);
		
		xyz[0] = sunsource[0];
		xyz[1] = sunsource[1];
		xyz[2] = sunsource[2];
	}
	

	
}
