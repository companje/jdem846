package us.wthr.jdem846.model.processing.util;

import java.util.Calendar;
import java.util.TimeZone;

import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;

public class SunlightPositioning
{
	private static Log log = Logging.getLog(SunlightPositioning.class);

	private static double SUN_RADIUS = 0.26667;

	protected long lightOnTime;
	protected EarthDateTime datetime;

	public SunlightPositioning(long lightOnDate)
	{
		this.lightOnTime = lightOnDate - (long) MathExt.floor((double) lightOnDate / 86400000.0) * 86400000;

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

		datetime = new EarthDateTime(year, month, day, hour, minute, second, 0, false);
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
		return julianDay(datetime.getYear(), datetime.getMonth(), datetime.getDay(), datetime.getHour(), datetime.getMinute(), datetime.getSecond(), datetime.getTimezone());
	}

	protected double julianDay(int year, int month, int day, int hour, int minute, int second, double tz)
	{
		double day_decimal, julian_day, a;

		day_decimal = day + (hour - tz + (minute + second / 60.0) / 60.0) / 24.0;

		if (month < 3) {
			month += 12;
			year--;
		}

		julian_day = MathExt.floor(365.25 * (year + 4716.0)) + MathExt.floor(30.6001 * (month + 1)) + day_decimal - 1524.5;
		if (julian_day > 2299160.0) {
			a = MathExt.floor(year / 100);
			julian_day += (2 - a + MathExt.floor(a / 4));
		}

		return julian_day;
	}

	double julianCentury(double jd)
	{
		return (jd - 2451545.0) / 36525.0;
	}

	
	public void getLightPosition(Vector xyz)
	{
		getLightPositionByCoordinates(0.0, 0.0, xyz);
	}
	
	public void getLightPositionByCoordinates(double latitude, double longitude, Vector xyz)
	{
		latitude = 0;
		longitude = 0;

		double jd = julianDay(datetime);
		double jc = julianCentury(jd);

		double n = jd - 2451545.0; // Number of days from J2000.0
		double L = 280.460 + 0.9856474 * n; // Mean longitude of the Sun,
											// corrected for the aberration of
											// light
		double g = 357.528 + 0.9856003 * n; // Mean anomaly of the sun
		L = limitDegrees(L);
		g = limitDegrees(g);

		// double _2g = MathExt.radians(g * 2);
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


		double obliquityCorrection = e + 0.00256 * MathExt.cos(MathExt.radians(125.04 - 1934.136 * jc));
		double y = MathExt.pow(MathExt.tan(MathExt.radians(obliquityCorrection) / 2.0), 2);

		double equationOfTime = MathExt.degrees(y * MathExt.sin(2.0 * _L) - 2.0 * eccentricityEarthOrbit * MathExt.sin(_g) + 4.0 * eccentricityEarthOrbit * y * MathExt.sin(_g) * MathExt.cos(2.0 * _L)
				- 0.5 * y * y * MathExt.sin(4.0 * _L) - 1.25 * eccentricityEarthOrbit * eccentricityEarthOrbit * MathExt.sin(2.0 * _g)) * 4.0; // in
																																				// minutes
																																				// of
																																				// time

		double tod = datetime.toMinutes();
		double trueSolarTime = (tod + equationOfTime + 4.0 * longitude - 60.0 * datetime.getTimezone());

		double ha = 0;
		if (trueSolarTime / 4.0 < 0.0)
			ha = trueSolarTime / 4.0 + 180.0;
		else
			ha = trueSolarTime / 4.0 - 180.0;

		xyz.x = 0.0; 
		xyz.y = 0.0;
		xyz.z = R * 149598000000.0; // R (AU) in meters

		double rotateY = rightAscension + (ha - longitude);
		double rotateX = declination;

		Vectors.rotate(rotateX, -rotateY, -o, xyz, Vectors.XYZ);


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

	@Deprecated
	public void getLightPositionByAngles(double solarElevation, double solarAzimuth, double[] xyz)
	{
		double sunsource[] = new double[3];
		
		sunsource[0] = 149598000000.0;
		sunsource[1] = 0.0;
		sunsource[2] = 0; // One AU in meters

		Vectors.rotate(0.0, solarAzimuth, 0, sunsource);
		Vectors.rotate(solarElevation, 0.0, 0, sunsource);

		xyz[0] = sunsource[0];
		xyz[1] = sunsource[1];
		xyz[2] = sunsource[2];
	}
	
	
	public static Vector calculate(long dateTimeMillis)
	{
		Vector sunsource = new Vector();
		SunlightPositioning sunlightPosition = new SunlightPositioning(dateTimeMillis);
		sunlightPosition.getLightPosition(sunsource);
		return sunsource;
	}

}
