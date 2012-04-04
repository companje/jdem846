package us.wthr.jdem846.render.render2d;


@Deprecated
public class ModelPoint
{
	
	private double latitude;
	private double longitude;
	private double elevation;
	private int color;
	private double dot;
	
	public ModelPoint()
	{
		
	}
	
	public ModelPoint(double latitude, double longitude, double elevation, int color, double dot)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.color = color;
		this.dot = dot;
	}

	public ModelPoint(double latitude, double longitude, double elevation, int[] color, double dot)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
		this.setColor(color);
		this.dot = dot;
	}
	

	public double getLatitude()
	{
		return latitude;
	}


	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}


	public double getLongitude()
	{
		return longitude;
	}


	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}


	public double getElevation()
	{
		return elevation;
	}


	public void setElevation(double elevation)
	{
		this.elevation = elevation;
	}


	public int getColor()
	{
		return color;
	}
	
	public void getColor(int[] buffer)
	{
		//intToBytes(color, buffer);
		if (buffer.length >= 4) {
			buffer[3] = 0xFF & (color >>> 24);
		}
		buffer[0] = 0xFF & (color >>> 16);
		buffer[1] = 0xFF & (color >>> 8);
		buffer[2] = 0xFF & color;
	}


	public void setColor(int color)
	{
		this.color = color;
	}
	
	public void setColor(int[] buffer)
	{
		setColor(buffer[0], buffer[1], buffer[2]);
	}

	public void setColor(int r, int g, int b)
	{
		int rgb = (0xFF << 24) |
			((r & 0xff) << 16) |
			((g & 0xff) << 8) |
			(b & 0xff);
		setColor(rgb);
	}

	public double getDot()
	{
		return dot;
	}


	public void setDot(double dot)
	{
		this.dot = dot;
	}
	

}
