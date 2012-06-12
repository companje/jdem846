package us.wthr.jdem846.gis.datetime;

import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.math.MathExt;

/** Collection of solar equations. Based on solar calculator at http://www.esrl.noaa.gov/gmd/grad/solcalc/
 * 
 * @author Kevin M. Gill
 *
 */
public class SolarCalculator
{
	public static final double SOL_NO_VALUE = Double.NaN;
    
	private EarthDateTime datetime;
    private double julianDay;
    private double timeLocal;
    private double julianCentury;

    private Coordinate latitude;
    private Coordinate longitude;

    public SolarCalculator()
    {
    	
    }
    
    public SolarCalculator(EarthDateTime _dt, Location location)
    {
    	datetime = _dt;
    	this.update();
    	this.latitude = location.getLatitude();
    	this.longitude = location.getLongitude();
    }
    public SolarCalculator(EarthDateTime _dt, Coordinate _latitude, Coordinate _longitude)
    {
    	datetime = _dt;
        this.update();

        this.latitude = _latitude;
        this.longitude = _longitude;
    }
    
    public SolarCalculator(EarthDateTime _dt, double _latitude, double _longitude)
    {
    	this.datetime = _dt;
        this.julianDay = _dt.julianDay();
        this.timeLocal = _dt.timeLocal();
        this.julianCentury = _dt.julianCentury();

        this.latitude = new Coordinate(_latitude, CoordinateTypeEnum.LATITUDE);
        this.longitude = new Coordinate(_longitude, CoordinateTypeEnum.LONGITUDE);
    }


    public void update()
    {
    	this.julianDay = this.datetime.julianDay();
        this.timeLocal = this.datetime.timeLocal();
        this.julianCentury = this.datetime.julianCentury();
    }
    
    public double meanObliquityOfEcliptic()
    {
    	return meanObliquityOfEcliptic(julianCentury);
    }
    
    public double meanObliquityOfEcliptic(double t)
    {
    	return 23.0 + (26.0 + ((21.448 - t * (46.8150 + t * (0.00059 - t * (0.001813)))) / 60.0)) / 60.0;
    }
    
    public double obliquityCorrection()
    {
    	return obliquityCorrection(julianCentury);
    }
    
    public double obliquityCorrection(double t)
    {
    	return meanObliquityOfEcliptic() + 0.00256 * MathExt.cos(MathExt.radians(125.04 - 1934.136 * t));
    }
    
    public double geomMeanLongSun()
    {
    	return geomMeanLongSun(julianCentury);
    }
    
    public double geomMeanLongSun(double t)
    {
    	double L0 = 280.46646 + t * (36000.76983 + t * (0.0003032));
    	while (L0 > 360.0) {
    		L0 -= 360.0;
    	}
    	while(L0 < 0.0) {
    		L0 += 360.0;
    	}
    	return L0;
    }
    
    public double eccentricityEarthOrbit()
    {
    	return eccentricityEarthOrbit(julianCentury);
    }
    
    public double eccentricityEarthOrbit(double t)
    {
    	return 0.016708634 - t * (0.000042037 + 0.0000001267 * t);
    }
    
    public double geomMeanAnomalySun()
    {
    	return geomMeanAnomalySun(julianCentury);
    }
    
    public double geomMeanAnomalySun(double t)
    {
    	return 357.52911 + t * (35999.05029 - 0.0001537 * t);
    }
    
    public double sunEqOfCenter()
    {
    	return sunEqOfCenter(julianCentury);
    }
    
    public double sunEqOfCenter(double t)
    {
    	double mrad = MathExt.radians(geomMeanAnomalySun());
    	return MathExt.sin(mrad) * (1.914602 - t * (0.004817 + 0.000014 * t)) + MathExt.sin(mrad+mrad) * (0.019993 - 0.000101 * t) + MathExt.sin(mrad+mrad+mrad) * 0.000289;
    }
    
    public double sunTrueLong()
    {
    	return sunTrueLong(julianCentury);
    }
    
    public double sunTrueLong(double t)
    {
    	return geomMeanLongSun(t) + sunEqOfCenter(t);
    }
    
    public double sunTrueAnomaly()
    {
    	return sunTrueAnomaly(julianCentury);
    }
    
    public double sunTrueAnomaly(double t)
    {
    	return geomMeanAnomalySun(t) + sunEqOfCenter(t);
    }
    
    public double sunApparentLong()
    {
    	return sunApparentLong(julianCentury);
    }
    
    public double sunApparentLong(double t)
    {
    	return sunTrueLong(t) - 0.00569 - 0.00478 * MathExt.sin(MathExt.radians(125.04 - 1934.136 * t));
    }
    
    public double sunRadVector()
    {
    	return sunRadVector(julianCentury);
    }
    
    public double sunRadVector(double t)
    {
    	double e = eccentricityEarthOrbit(t);
        return (1.000001018 * (1.0 - e * e)) / (1.0 + e * MathExt.cos(MathExt.radians(sunTrueAnomaly(t))));
    }
    
    public double sunRtAscension()
    {
    	return sunRtAscension(julianCentury);
    }
    
    public double sunRtAscension(double t)
    {
    	double _l = MathExt.radians(sunApparentLong(t));
        return MathExt.degrees(MathExt.atan2(MathExt.cos(_l), (MathExt.cos(MathExt.radians(obliquityCorrection(t))) * MathExt.sin(_l))));
    }
    
    public double varY()
    {
    	return varY(julianCentury);
    }
    
    public double varY(double t)
    {
    	double e = obliquityCorrection(t);
        return MathExt.tan(MathExt.radians(e / 2)) * MathExt.tan(MathExt.radians(e / 2));
    }
    
    public double equationOfTime()
    {
    	return equationOfTime(julianCentury);
    }
    
    public double equationOfTime(double t)
    {
    	double l0 = MathExt.radians(geomMeanLongSun(t));
        double e = eccentricityEarthOrbit(t);
        double m = MathExt.radians(geomMeanAnomalySun(t));
 
        double y = MathExt.pow(MathExt.tan(MathExt.radians(obliquityCorrection(t))/2.0), 2);
     
        return MathExt.degrees(y * MathExt.sin(2.0 * l0) - 2.0 * e * MathExt.sin(m) + 4.0 * e * y * MathExt.sin(m) * MathExt.cos(2.0 * l0) - 0.5 * y * y * MathExt.sin(4.0 * l0) - 1.25 * e * e * MathExt.sin(2.0 * m)) * 4.0;    // in minutes of time
    }
    
    public double declinationOfSun()
    {
    	return declinationOfSun(julianCentury);
    }
    
    public double declinationOfSun(double t)
    {
    	return MathExt.degrees(MathExt.asin(MathExt.sin(MathExt.radians(obliquityCorrection(t))) * MathExt.sin(MathExt.radians(sunApparentLong(t)))));
    }
    
    public double hourAngleSunrise()
    {
    	return hourAngleSunrise(declinationOfSun());
    }
    
    public double hourAngleSunrise(double decl)
    {
    	double latRad = MathExt.radians(latitude.toDecimal());
        double sdRad  = MathExt.radians(decl);
        return MathExt.degrees(MathExt.acos((MathExt.cos(MathExt.radians(90.833))/(MathExt.cos(latRad)*MathExt.cos(sdRad))-MathExt.tan(latRad) * MathExt.tan(sdRad))));
    }
    
    public double trueSolarTime()
    {
    	return trueSolarTime(equationOfTime());
    }
    
    public double trueSolarTime(double eot)
    {
    	double tod = todToMinutes(datetime.getHour(), datetime.getMinute(), datetime.getSecond());
    	return (tod+ eot + 4.0 * longitude.toDecimal() - 60.0 * datetime.getTimezone());
    }
    
    public double hourAngle()
    {
    	return hourAngle(trueSolarTime());
    }
    
    public double hourAngle(double true_sol_time)
    {
    	if (true_sol_time / 4.0 < 0.0)
            return true_sol_time / 4.0 + 180.0;
    	else
            return true_sol_time / 4.0 - 180.0;
    }
    
    public double solarZenithAngle()
    {
    	return solarZenithAngle(declinationOfSun(), hourAngle());
    }
    
    public double solarZenithAngle(double decl, double ha)
    {
    	double latitude = this.latitude.toDecimal();
    	double csz = MathExt.sin(MathExt.radians(latitude)) * MathExt.sin(MathExt.radians(decl)) + MathExt.cos(MathExt.radians(latitude)) * MathExt.cos(MathExt.radians(decl)) * MathExt.cos(MathExt.radians(ha));
    	if (csz > 1.0) {
    		csz = 1.0;
    	} else if (csz < -1.0) { 
    		csz = -1.0;
    	}
    	double zenith = MathExt.degrees(MathExt.acos(csz));
    	
    	
    	double exoatmElevation = 90.0 - zenith;
    	double refractionCorrection = 0.0;
    	
    	if (exoatmElevation > 85.0) {
    		refractionCorrection = 0.0;
    	} else {
    		double te = MathExt.tan (MathExt.radians(exoatmElevation));
    		if (exoatmElevation > 5.0) {
    			refractionCorrection = 58.1 / te - 0.07 / (te*te*te) + 0.000086 / (te*te*te*te*te);
    		} else if (exoatmElevation > -0.575) {
    			refractionCorrection = 1735.0 + exoatmElevation * (-518.2 + exoatmElevation * (103.4 + exoatmElevation * (-12.79 + exoatmElevation * 0.711) ) );
    		} else {
    			refractionCorrection = -20.774 / te;
    		}
    		refractionCorrection = refractionCorrection / 3600.0;
    	}

    	double solarZen = zenith - refractionCorrection;
    	
    	return solarZen;
    	
    	
    }
    
    public double solarElevationAngle()
    {
    	return solarElevationAngle(declinationOfSun(), hourAngle());
    }
    
    public double solarElevationAngle(double decl, double ha)
    {
    	return 90.0 - solarZenithAngle(decl, ha);
    }
    
    public double approxAtmosphericRefraction()
    {
    	return approxAtmosphericRefraction(solarElevationAngle());
    }
    
    public double approxAtmosphericRefraction(double sol_elev)
    {
    	double refr = 0;
    	if (sol_elev > 85)
            refr = 0;
    	else if (sol_elev > 5)
            refr = 58.1 / MathExt.tan(MathExt.radians(sol_elev)) - 0.07 / (MathExt.pow(MathExt.tan(MathExt.radians(sol_elev)),3)) + 0.000086 / (MathExt.pow(MathExt.tan(MathExt.radians(sol_elev)),5));
    	else if (sol_elev > -0.575)
            refr = 1735.0 + sol_elev *(-518.2 + sol_elev * (103.4 + sol_elev * (-12.79+sol_elev*0.711)));
    	else
            refr = -20.772/MathExt.tan(MathExt.radians(sol_elev));
    	return refr / 3600.0;
    }
    
    public double correctedSolarElevation()
    {
    	return correctedSolarElevation(solarElevationAngle());
    }
    
    public double correctedSolarElevation(double sol_elev)
    {
    	return sol_elev + approxAtmosphericRefraction(sol_elev);
    }
    
    public double solarAzimuthAngle()
    {
    	return solarAzimuthAngle(declinationOfSun(), hourAngle());
    }
    
    protected double solarAzimuthAngle(double decl, double ha)
    {
    	return solarAzimuthAngle(decl, ha, solarZenithAngle(decl, ha));
    }
    
    public double solarAzimuthAngle(double decl, double ha, double zenith)
    {
    	double latitude = this.latitude.toDecimal();
    	double azimuth;
    	double azDenom = ( MathExt.cos(MathExt.radians(latitude)) * MathExt.sin(MathExt.radians(zenith)) );
    	
    	if (Math.abs(azDenom) > 0.001) {
    		double azRad = (( MathExt.sin(MathExt.radians(latitude)) * MathExt.cos(MathExt.radians(zenith)) ) - MathExt.sin(MathExt.radians(decl))) / azDenom;
    		if (MathExt.abs(azRad) > 1.0) {
    			if (azRad < 0) {
    				azRad = -1.0;
    			} else {
    				azRad = 1.0;
    			}
    		}
    		azimuth = 180.0 - MathExt.degrees(MathExt.acos(azRad));
    		if (ha > 0.0) {
    			azimuth = -azimuth;
    		}
    	} else {
    		if (latitude > 0.0) {
    			azimuth = 180.0;
    		} else { 
    			azimuth = 0.0;
    		}
    	}
    	if (azimuth < 0.0) {
    		azimuth += 360.0;
    	}
    	
    	return azimuth;
    	/*
    	double latitude = this.latitude.toDecimal();
    	double a = 0;
    	if (ha > 0)
            a = degrees(acos(((sin(radians(latitude))*cos(radians(zenith)))-sin(radians(decl)))/(cos(radians(latitude))*sin(radians(zenith)))))+180;
    	else
            a = 540-degrees(acos(((sin(radians(latitude))*cos(radians(zenith)))-sin(radians(decl)))/(cos(radians(latitude))*sin(radians(zenith)))));
   
	    while (a > 360) {
            a -= 360.0;
	    }
	    
	    return a; 
	    */
    }
    
    public static double todToMinutes(int hours, int minutes, int seconds)
    {
    	return (double)hours * 60 + (double)minutes + ((double)seconds / 60.0);
    }
    
    
    public double todToMinutes(EarthDateTime tod)
    {
    	return ((double)tod.getHour()) * 60 + ((double)tod.getMinute()) + (((double)tod.getSecond()) / 60.0);
    }
    
    public EarthDateTime minutesToTod(double minutes)
    {
    	int hour = (int) MathExt.floor(minutes / 60.0);
        int minute = (int)MathExt.floor(minutes - (hour * 60.0));
        int second = (int) MathExt.floor(60.0 * (minutes - (hour * 60.0) - minute));

        EarthDateTime tod = datetime.clone();
        tod.setHour(hour);
        tod.setMinute(minute);
        tod.setSecond(second);
        return tod;
    }
    
    public EarthDateTime solarNoon()
    {
    	return solarNoon(julianCentury);
    }
    
    public EarthDateTime solarNoon(double t)
    {
	    double longitude = this.longitude.toDecimal();
	    double tnoon = julianCentury(t - longitude/360.0);
	    double eqTime = equationOfTime(tnoon);
	    double solNoonOffset = 720.0 - (longitude * 4.0) - eqTime ;
	    double newt = julianCentury(t + solNoonOffset/1440.0);
	    eqTime = equationOfTime(newt);
	    double solNoonLocal = 720.0 - (longitude * 4.0) - eqTime + (datetime.getTimezone()*60.0);
	    //*solNoonLocal += (this->datetime->dst) ? 60.0 : 0.0;
	    return minutesToTod(solNoonLocal);
    }
    

    public EarthDateTime sunriseUTC()
    {
    	return sunriseSetUTC(julianCentury, true);
    }
    
    public EarthDateTime sunsetUTC()
    {
    	return sunriseSetUTC(julianCentury, false);
    }
    
    public EarthDateTime sunriseSetUTC(double t)
    {
    	return sunriseSetUTC(t, true);
    }
    
    public EarthDateTime sunriseSetUTC(boolean rise)
    {
    	return sunriseSetUTC(julianCentury, rise);
    }
    
    public EarthDateTime sunriseSetUTC(double t, boolean rise)
    {
    	double longitude = this.longitude.toDecimal();
    	double eqTime = equationOfTime(t);
    	double solarDec = declinationOfSun(t);
    	double hourAngle = hourAngleSunrise(solarDec);
    	hourAngle = (rise) ? hourAngle : -hourAngle;
    	double delta = longitude + hourAngle;
    	double timeUTC = 720.0 - (4.0 * delta) - eqTime;
   
    	return minutesToTod(timeUTC);
    }
    
    public EarthDateTime sunrise()
    {
    	return sunriseSet(true);
    }
    
    public EarthDateTime sunset()
    {
    	return sunriseSet(false);
    }
    
    public EarthDateTime sunriseSet(boolean rise)
    {
        double JD = julianDay;
        double timezone = datetime.getTimezone();
        //boolean dst = datetime.isDst();
       
        EarthDateTime timeUTC_obj = sunriseSetUTC(rise);
        double timeUTC = todToMinutes(timeUTC_obj.getHour(), timeUTC_obj.getMinute(), timeUTC_obj.getSecond());

        double newJC = julianCentury((JD + timeUTC/1440.0));
        EarthDateTime newTimeUTC_obj = sunriseSetUTC(newJC, rise);
        double newTimeUTC = todToMinutes(newTimeUTC_obj.getHour(), newTimeUTC_obj.getMinute(), newTimeUTC_obj.getSecond());


        double timeLocal = newTimeUTC + (timezone * 60.0);
        if ((timeLocal >= 0.0) && (timeLocal < 1440.0)) {
        	return minutesToTod(timeLocal);
        } else {
        	//double jday = JD;
        	double increment = (timeLocal < 0) ? 1 : -1;
        	while ((timeLocal < 0.0) || (timeLocal >= 1440.0)) {
        		timeLocal += increment * 1440.0;
        		//jday -= increment;
        	}
        	return minutesToTod(timeLocal);
        }
    }

	public EarthDateTime getDatetime()
	{
		return datetime;
	}

	public void setDatetime(EarthDateTime datetime)
	{
		this.datetime = datetime;
	}

	public double getJulianDay()
	{
		return julianDay;
	}

	public void setJulianDay(double julianDay)
	{
		this.julianDay = julianDay;
	}

	public double getTimeLocal()
	{
		return timeLocal;
	}

	public void setTimeLocal(double timeLocal)
	{
		this.timeLocal = timeLocal;
	}

	public double getJulianCentury()
	{
		return julianCentury;
	}

	public void setJulianCentury(double julianCentury)
	{
		this.julianCentury = julianCentury;
	}

	public Coordinate getLatitude()
	{
		return latitude;
	}

	public void setLatitude(Coordinate latitude)
	{
		this.latitude = latitude;
	}

	public Coordinate getLongitude()
	{
		return longitude;
	}

	public void setLongitude(Coordinate longitude)
	{
		this.longitude = longitude;
	}

    

    
    
    

	public static boolean isLeapYear(int year)
	{
        return (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0));
	}


	public static double julianCentury(double julian)
	{      
        return (julian - 2451545.0) / 36525.0;
	}
    
    
    
    
    
    
    
}
