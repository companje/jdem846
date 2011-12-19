package us.wthr.jdem846.gis.datetime;

import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.Location;

/** Provides a collection of static solar calculations. 
 * 
 * @author Kevin M. Gill
 *
 */
public class SolarUtil
{
	
	private static SolarCalculator calculator = new SolarCalculator();
	
	
	
	public static SolarPosition getSolarPosition(EarthDateTime dt, Location location)
	{
		return SolarUtil.getSolarPosition(dt, location, null);
	}
	
	public static SolarPosition getSolarPosition(EarthDateTime dt, Location location, SolarPosition position)
	{
		return getSolarPosition(dt, location.getLatitude(), location.getLongitude(), position);
	}
	
	public static SolarPosition getSolarPosition(EarthDateTime dt, Coordinate latitude, Coordinate longitude)
	{
		return getSolarPosition(dt, latitude, longitude, null);
	}
	
	public static SolarPosition getSolarPosition(EarthDateTime dt, double latitude, double longitude)
	{
		return getSolarPosition(dt, latitude, longitude, null);
	}
	
	public static SolarPosition getSolarPosition(EarthDateTime dt, double latitude, double longitude, SolarPosition position)
	{
		return getSolarPosition(dt, new Coordinate(latitude, CoordinateTypeEnum.LATITUDE), new Coordinate(longitude, CoordinateTypeEnum.LONGITUDE), position);
	}
	
	public static SolarPosition getSolarPosition(EarthDateTime dt, Coordinate latitude, Coordinate longitude, SolarPosition position)
	{
		if (position == null) {
			position = new SolarPosition();
		}
		
		synchronized(calculator) {
			calculator.setDatetime(dt);
			calculator.update();
			calculator.setLatitude(latitude);
			calculator.setLongitude(longitude);
			
			position.setAzimuth(calculator.solarAzimuthAngle());
			position.setElevation(calculator.solarElevationAngle());
			position.setEquationOfTime(calculator.equationOfTime());
			position.setSolarDeclination(calculator.declinationOfSun());
			position.setZenithAngle(calculator.solarZenithAngle());
			
			position.setApparentSunrise(calculator.sunrise());
			position.setApparentSunriseUtc(calculator.sunriseUTC());
			position.setApprentSunset(calculator.sunset());
			position.setApprentSunsetUtc(calculator.sunsetUTC());
			position.setSolarNoon(calculator.solarNoon());
			
		}
		
		return position;
	}
	
	
}
