package us.wthr.jdem846.gis;

public class Location
{
	
	private String name;
    private int timezone;
    private boolean dst;

    private Coordinate latitude;
    private Coordinate longitude;

    public Location(String name, Coordinate latitude, Coordinate longitude)
    {
    	this(name, latitude, longitude, 0, false);
    }
    
    public Location(String name, Coordinate latitude, Coordinate longitude, int timezone, boolean dst)
    {
    	setName(name);
    	setLatitude(latitude);
    	setLongitude(longitude);
    	setTimezone(timezone);
    	setDst(dst);
    }
    
    Location(String name, double latitude, double longitude)
    {
    	this(name, latitude, longitude, 0, false);
    }
    
    Location(String name, double latitude, double longitude, int timezone, boolean dst)
    {
    	setName(name);
    	setLatitude(new Coordinate(latitude, CoordinateTypeEnum.LATITUDE));
    	setLongitude(new Coordinate(longitude, CoordinateTypeEnum.LONGITUDE));
    	setTimezone(timezone);
    	setDst(dst);
    }

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getTimezone()
	{
		return timezone;
	}

	public void setTimezone(int timezone)
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
    
    
    
    
}
