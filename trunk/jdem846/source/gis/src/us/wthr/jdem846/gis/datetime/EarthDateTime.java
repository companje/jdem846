package us.wthr.jdem846.gis.datetime;

import java.util.Calendar;
import java.util.Date;


public class EarthDateTime implements Cloneable
{
	
	//private Calendar calendar;
	private int year = 0;
	private int month = 0;
	private int day = 0;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private int millisecond = 0;
	private double timezone = 0;
	private boolean dst = false;
	
	
    public EarthDateTime(int jc)
    {
    	this.fromJulianCentury(jc);
    }
    
    public EarthDateTime(int hour, int minute, int second, double timezone, boolean dst)
    {
    	setHour(hour);
    	setMinute(minute);
    	setSecond(second);
    	setTimezone(timezone);
    	setDst(dst);
    }
    
    public EarthDateTime(int year, int month, int day, int hour, int minute, int second, double timezone, boolean dst)
    {
    	setYear(year);
    	setMonth(month);
    	setDay(day);
    	setHour(hour);
    	setMinute(minute);
    	setSecond(second);
    	setTimezone(timezone);
    	setDst(dst);
    }
    
    public EarthDateTime(int year, int month, int day, double dayMinutes, double timezone, boolean dst)
    {
    	setYear(year);
    	setMonth(month);
    	setDay(day);
    	setTimezone(timezone);
    	setDst(dst);
    	fromMinutes(dayMinutes);
    }
    

    
    public EarthDateTime(Date date)
    {
    	Calendar calendar = Calendar.getInstance();
    	setYear(calendar.get(Calendar.YEAR));
    	setMonth(calendar.get(Calendar.MONTH) + 1);
    	setDay(calendar.get(Calendar.DAY_OF_MONTH));
    	setHour(calendar.get(Calendar.HOUR_OF_DAY));
    	setMinute(calendar.get(Calendar.MINUTE));
    	setSecond(calendar.get(Calendar.SECOND));
    	setMillisecond(calendar.get(Calendar.MILLISECOND));
    	//this.calendar.setTime(date);
    }
    

    
    
    
    public EarthDateTime()
    {
    	
    }
    
    /*
    protected Calendar getCalendar()
    {
    	if (calendar == null) {
    		calendar = Calendar.getInstance();
    	}
    	return calendar;
    }
    */
    
    public static EarthDateTime today()
    {
    	return new EarthDateTime();
    }

    public double toMinutes()
    {
    	double hour = getHour();
    	double minute = getMinute();
    	double second = getSecond();
    	
    	return hour * 60.0 + minute + (second / 60.0);
    }
    
    public void fromMinutes(double dayMinutes)
    {
    	double hour = Math.floor(dayMinutes / 60.0);
    	double minute = Math.floor(dayMinutes - (hour * 60.0));
    	double second = Math.floor(60.0 * (dayMinutes - (hour * 60.0) -minute));
    	
    	setHour((int) hour);
        setMinute((int) minute);
        setSecond((int) second);
        setMillisecond(0);
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
    	return toMinutes();
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
        //setTimezone(0);
    }
    
    public int dayOfYear()
    {
    	//return getCalendar().get(Calendar.DAY_OF_YEAR);
    	double k = (isLeapYear()) ? 1 : 2;
        return (int) (Math.floor((275.0 * getMonth()) / 9.0) - k * Math.floor((getMonth() + 9.0) / 12.0) + getDay() - 30.0);
    }
    
    public String toString()
    {
    	String s = ""+getYear()+"."+getMonth()+"."+getDay()+" "+getHour()+":"+getMinute()+":"+getSecond();
    	return s;
    	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
    	//return sdf.format(calendar.getTime());
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
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public int getMonth()
	{
		return month;
	}

	public void setMonth(int month)
	{
		this.month = month;
	}

	public int getDay()
	{
		return day;
	}

	public void setDay(int day)
	{
		this.day = day;
	}

	public int getHour()
	{
		return hour;
	}

	public void setHour(int hour)
	{
		this.hour = hour;
	}

	public int getMinute()
	{
		return minute;
	}

	public void setMinute(int minute)
	{
		this.minute = minute;
	}

	public int getSecond()
	{
		return second;
	}

	public void setSecond(int second)
	{
		this.second = second;
	}

	public int getMillisecond()
	{
		return millisecond;
	}
	
	public void setMillisecond(int millisecond)
	{
		this.millisecond = millisecond;
	}
	

	
	public double getTimezone()
	{
		return timezone;
	}

	public void setTimezone(double timezone)
	{
		this.timezone = timezone;
	}

	public boolean isDst()
	{
		return dst;
	}

	public void setDst(boolean dst)
	{
		this.dst = dst;
	}
    
	public EarthDateTime clone() 
	{
		try {
			EarthDateTime e = (EarthDateTime) super.clone();
			return e;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
}
