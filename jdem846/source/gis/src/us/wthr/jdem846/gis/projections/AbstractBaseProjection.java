package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;

public abstract class AbstractBaseProjection implements MapProjection
{
	protected static final double PI = Math.PI;
	protected static final double TWOPI = Math.PI * 2.0; 
	protected static final double HALFPI = (Math.PI / 2.0);
	protected static final double FORTPI = (Math.PI / 4.0);
	protected static final double EPS10 = 1.e-10;
	
	protected final static double C00 = 1.0;
	protected final static double C02 = .25;
	protected final static double C04 = .046875;
	protected final static double C06 = .01953125;
	protected final static double C08 = .01068115234375;
	protected final static double C22 = .75;
	protected final static double C44 = .46875;
	protected final static double C46 = .01302083333333333333;
	protected final static double C48 = .00712076822916666666;
	protected final static double C66 = .36458333333333333333;
	protected final static double C68 = .00569661458333333333;
	protected final static double C88 = .3076171875;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double width; 
	private double height;
	
	private double scaleFactor = 1.0;
	
	public AbstractBaseProjection()
	{
		
	}
	
	public AbstractBaseProjection(double north, double south, double east, double west, double width, double height)
	{
		setUp(north, south, east, west, width, height);
	}

	public void setUp(ModelContext modelContext)
	{
		setUp(modelContext.getNorth(), 
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelContext.getModelDimensions().getOutputWidth(),
				modelContext.getModelDimensions().getOutputHeight());
	}
	
	public void setUp(double north, double south, double east, double west, double width, double height)
	{
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.width = width;
		this.height = height;
	}
	
	
	public double latitudeToRow(double latitude)
	{
		double range = north - south;
		double pos = range - (north - latitude);
		double row = (1.0 - (pos / range)) * (double)height;
		return row;
	}
	
	public double longitudeToColumn(double longitude)
	{
		double range = east - west;
		double pos = range - (longitude - west);
		double col = (1.0 - (pos / range)) * (double) width;
		return col;
	}

	protected double getNorth()
	{
		return north;
	}

	protected double getSouth()
	{
		return south;
	}

	protected double getEast()
	{
		return east;
	}

	protected double getWest()
	{
		return west;
	}

	protected double getWidth()
	{
		return width;
	}

	protected double getHeight()
	{
		return height;
	}
	
	
	
	protected void setNorth(double north)
	{
		this.north = north;
	}

	protected void setSouth(double south)
	{
		this.south = south;
	}

	protected void setWest(double west)
	{
		this.west = west;
	}

	protected void setWidth(double width)
	{
		this.width = width;
	}

	protected double getScaleFactor()
	{
		return scaleFactor;
	}
	
	protected double tsfn(double phi, double sinphi, double e)
	{
		sinphi *= e;
		return (Math.tan (.5 * (HALFPI - phi)) /
				Math.pow((1. - sinphi) / (1. + sinphi), .5 * e));
	}
	
	protected static double msfn(double sinphi, double cosphi, double es) 
	{
		return cosphi / Math.sqrt(1.0 - es * sinphi * sinphi);
	}
	
	public static double[] enfn(double es) 
	{
		double t;
		double[] en = new double[5];
		en[0] = C00 - es * (C02 + es * (C04 + es * (C06 + es * C08)));
		en[1] = es * (C22 - es * (C04 + es * (C06 + es * C08)));
		en[2] = (t = es * es) * (C44 - es * (C46 + es * C48));
		en[3] = (t *= es) * (C66 - es * C68);
		en[4] = t * es * C88;
		return en;
	}
	
	
	public static double mlfn(double phi, double sphi, double cphi, double[] en) 
	{
		cphi *= sphi;
		sphi *= sphi;
		return en[0] * phi - cphi * (en[1] + sphi*(en[2] + sphi*(en[3] + sphi*en[4])));
	}
	
	public static double acos(double v) {
		if (Math.abs(v) > 1.)
			return v < 0.0 ? Math.PI : 0.0;
		return Math.acos(v);
	}
}
