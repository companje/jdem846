package us.wthr.jdem846.gis;

import java.text.NumberFormat;

public class Coordinate {
	
	private static final NumberFormat nf = NumberFormat.getNumberInstance();
	
	static {
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);
	}
	
	private double decimal;
    private int hour;
    private int minute;
    private double second;
    private CardinalDirectionEnum direction;
    private CoordinateTypeEnum coordinateType;

    public Coordinate(int hour, int minute, double second, CardinalDirectionEnum direction, CoordinateTypeEnum coordinateType)
    {
    	setHour(hour);
    	setMinute(minute);
    	setSecond(second);
    	setDirection(direction);
    	setCoordinateType(coordinateType);
    }
    
    public Coordinate(double decimal, CoordinateTypeEnum coordinateType)
    {
    	setDecimal(decimal);
    	setCoordinateType(coordinateType);
    	fromDecimal(decimal, coordinateType);
    }

    public double toDecimal()
    {
    	double _d = Math.abs((double)this.hour + ((double)this.minute / 60.0) + ((double)this.second / 3600.0));
        if (this.direction == CardinalDirectionEnum.SOUTH || this.direction == CardinalDirectionEnum.WEST) {
        	_d *= -1.0;
        }
        return _d;
    }
    
    public void fromDecimal(double decimal)
    {
    	fromDecimal(decimal, this.getCoordinateType());
    }
    
    public void fromDecimal(double decimal, CoordinateTypeEnum coordinateType)
    {
    	this.coordinateType = coordinateType;
    	if (decimal < 0 && coordinateType == CoordinateTypeEnum.LATITUDE) {
            direction = CardinalDirectionEnum.SOUTH;
    	} else if (decimal >= 0 && coordinateType == CoordinateTypeEnum.LATITUDE) {
    		direction = CardinalDirectionEnum.NORTH;
    	} else if (decimal < 0 && coordinateType == CoordinateTypeEnum.LONGITUDE) {
            direction = CardinalDirectionEnum.WEST;
    	} else if (decimal >= 0 && coordinateType == CoordinateTypeEnum.LONGITUDE) {
    		direction = CardinalDirectionEnum.EAST;
    	}

    	this.decimal = Math.abs(decimal);
    	this.hour = (int) Math.floor(this.decimal);
    	this.minute = (int)  Math.floor(60 * (this.decimal - hour));
    	this.second = ((this.decimal - hour - (minute / 60.0)) * 3600.0);
    }
    
    
    public double getDecimal()
	{
		return decimal;
	}

	public void setDecimal(double decimal)
	{
		this.decimal = decimal;
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

	public double getSecond()
	{
		return second;
	}

	public void setSecond(double second)
	{
		this.second = second;
	}

	public CardinalDirectionEnum getDirection()
	{
		return direction;
	}

	public void setDirection(CardinalDirectionEnum direction)
	{
		this.direction = direction;
	}

	public CoordinateTypeEnum getCoordinateType()
	{
		return coordinateType;
	}

	public void setCoordinateType(CoordinateTypeEnum coordinateType)
	{
		this.coordinateType = coordinateType;
	}

	public String toString()
    {
		
		char d = ' ';
		
	
		if (direction == CardinalDirectionEnum.EAST)
			d = 'E';
		else if (direction == CardinalDirectionEnum.WEST)
			d = 'W';
		else if (direction == CardinalDirectionEnum.NORTH)
			d = 'N';
		else if (direction == CardinalDirectionEnum.SOUTH)
			d = 'S';
	 
		
		
		
		String str = "" + this.hour + "\u00B0 " + this.minute + "' " + nf.format(this.second) + "\" " + d;
        return str;
    }
	
	
	public boolean equals(Object other)
	{
		if (other != null && other instanceof Coordinate) {
			Coordinate cOther = (Coordinate) other;
			
			if (cOther.decimal == this.decimal && cOther.coordinateType == this.coordinateType) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}
}
