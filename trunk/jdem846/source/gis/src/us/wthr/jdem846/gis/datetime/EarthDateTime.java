package us.wthr.jdem846.gis.datetime;

import java.util.Calendar;

public class EarthDateTime
{
	
	private Calendar calendar;
	
    public EarthDateTime(int jc)
    {
    	this.fromJulianCentury(jc);
    }
    
    public EarthDateTime(int hour, int minute, int second, int timezone, boolean dst)
    {
    	setHour(hour);
    	setMinute(minute);
    	setSecond(second);
    	setTimezone(timezone);
    	setDst(dst);
    }
    
    public EarthDateTime(int year, int month, int day, int hour, int minute, int second, int timezone, boolean dst)
    {
    	setYear(year);
    	setMonth(month);
    	setDay(day);
    	setHour(hour);
    	setMinute(minute);
    	setSecond(second);
    	setDst(dst);
    }
    
    public EarthDateTime(int year, int month, int day, double dayMinutes, int timezone, boolean dst)
    {
    	setYear(year);
    	setMonth(month);
    	setDay(day);
    	setTimezone(timezone);
    	setDst(dst);
    	fromMinutes(dayMinutes);
    }
    
    public EarthDateTime(long milliseconds, int timezone, boolean dst)
    {
    	setTimezone(timezone);
    	setDst(dst);
    	fromMilliseconds(milliseconds);
    }
    
    public EarthDateTime(long milliseconds)
    {
    	fromMilliseconds(milliseconds);
    }
    
    public EarthDateTime()
    {
    	calendar = getCalendar();
    }
    
    protected Calendar getCalendar()
    {
    	if (calendar == null) {
    		calendar = Calendar.getInstance();
    	}
    	return calendar;
    }
    
    public static EarthDateTime today()
    {
    	return new EarthDateTime();
    }

    public double toMinutes()
    {
    	return ((double)getCalendar().getTimeInMillis() / 1000.0) / 60.0;
    }
    
    public void fromMinutes(double dayMinutes)
    {
    	fromMilliseconds(Math.round(dayMinutes * 1000.0));
    }
    
    public void fromMilliseconds(long milliseconds)
    {
    	getCalendar().setTimeInMillis(milliseconds);
    }
    
    public double julianDay()
    {
    	
    	double a = (14 - getMonth())/12;
    	double y = getYear() + 4800 - a;
    	double m = getMonth() + 12*a - 3;
    	return getDay() - 1.5 + ((153*m + 2)/5) + 365*y + y/4 - y/100 + y/400 - 32045;
    }
    
    public double timeLocal()
    {
    	return (getHour() * 60.0) + getMinute() + (getSecond() / 60.0);
    }
    
    public double julianCentury()
    {
    	double jd = julianDay();
        double tl = timeLocal();
        double total = jd + tl / 1440.0;
        return (total - 2451545.0) / 36525.0;
    }
    
    public void fromJulianCentury(double jc)
    {
    	double jd = (jc * 36525.0) + 2451545.0;
        double z = Math.floor(jd + 0.5);
        double f = (jd + 0.5) - z;
       
        double A = 0;
        if (z < 2299161.0) { 
                A = z;
        } else {
        	double alpha = Math.floor((z - 1867216.25) / 36524.25);
            A = z + 1 + alpha - Math.floor(alpha / 4);
        }

        double B = A + 1524.0;
        double C = Math.floor((B - 122.1) / 365.25);
        double D = Math.floor(365.25 * C);
        double E = Math.floor((B - C) / 30.6001);

        double day = B - D - Math.floor(30.6001 * E) + f;
        double month = (E < 14) ? E - 1 : E - 13;
        double year = (month > 2) ? C - 4716 : C - 4715;

        setYear((int)Math.floor(year));
        setMonth((int) Math.floor(month));
        setDay((int) Math.floor(day));

        double tl = Math.floor((jd - julianDay()) * 1440.0);
        fromMinutes(tl);
        setTimezone(0);
    }
    
    public int dayOfYear()
    {
    	return getCalendar().get(Calendar.DAY_OF_YEAR);
    }
    
    public String toString()
    {
    	return null;
    }


    public boolean isLeapYear()
    {
    	return yearIsLeapYear(getYear());
    }
    
    public static boolean yearIsLeapYear(int year)
    {
    	return (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0));
    }

	public int getYear()
	{
		return getCalendar().get(Calendar.YEAR);
	}

	public void setYear(int year)
	{
		getCalendar().set(Calendar.YEAR, year);
	}

	public int getMonth()
	{
		return getCalendar().get(Calendar.MONTH) + 1;
	}

	public void setMonth(int month)
	{
		getCalendar().set(Calendar.MONTH, month - 1);
	}

	public int getDay()
	{
		return getCalendar().get(Calendar.DAY_OF_MONTH);
	}

	public void setDay(int day)
	{
		getCalendar().set(Calendar.DAY_OF_MONTH, day);
	}

	public int getHour()
	{
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}

	public void setHour(int hour)
	{
		getCalendar().set(Calendar.HOUR_OF_DAY, hour);
	}

	public int getMinute()
	{
		return getCalendar().get(Calendar.MINUTE);
	}

	public void setMinute(int minute)
	{
		getCalendar().set(Calendar.MINUTE, minute);
	}

	public int getSecond()
	{
		return getCalendar().get(Calendar.SECOND);
	}

	public void setSecond(int second)
	{
		getCalendar().set(Calendar.SECOND, second);
	}

	public int getMillisecond()
	{
		return getCalendar().get(Calendar.MILLISECOND);
	}
	
	public void setMillisecond(int millisecond)
	{
		getCalendar().set(Calendar.MILLISECOND, millisecond);
	}
	
	public int getTimezone()
	{
		return (int) (((double)getCalendar().get(Calendar.ZONE_OFFSET) / 1000.0) / 3600.0);
	}

	public void setTimezone(int timezone)
	{
		getCalendar().set(Calendar.ZONE_OFFSET, (timezone * 3600) * 1000);
	}

	public boolean isDst()
	{
		return getCalendar().get(Calendar.DST_OFFSET) != 0;
	}

	public void setDst(boolean dst)
	{
		if (dst) {
			getCalendar().set(Calendar.DST_OFFSET, 3600000);
		} else {
			getCalendar().set(Calendar.DST_OFFSET, 0);
		}
	}
    
    
}
