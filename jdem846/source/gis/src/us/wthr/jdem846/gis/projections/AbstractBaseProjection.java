package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.math.MathExt;

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
	
	//private double north;
	//private double south;
	//private double east;
	//private double west;
	
	private Location northWest;
	private Location northEast;
	private Location southWest;
	private Location southEast;
	
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

	public AbstractBaseProjection(double northWestLatitude,
									double northWestLongitude,
									double northEastLatitude,
									double northEastLongitude,
									double southWestLatitude,
									double southWestLongitude,
									double southEastLatitude,
									double southEastLongitude, 
									double width, 
									double height)
	{
		setUp(northWestLatitude,
				northWestLongitude,
				northEastLatitude,
				northEastLongitude,
				southWestLatitude,
				southWestLongitude,
				southEastLatitude,
				southEastLongitude, 
				width, 
				height);
		
	}
	
	public AbstractBaseProjection(Location northWest, Location northEast, Location southWest, Location southEast, double width, double height)
	{
		setUp(northWest, northEast, southWest, southEast, width, height);
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
		//this.north = north;
		//this.south = south;
		//this.east = east;
		//this.west = west;
		setUp(north, west,
				north, east,
				south, west,
				south, east,
				width, height);
		//this.width = width;
		//this.height = height;
	}
	
	public void setUp(double northWestLatitude,
					double northWestLongitude,
					double northEastLatitude,
					double northEastLongitude,
					double southWestLatitude,
					double southWestLongitude,
					double southEastLatitude,
					double southEastLongitude, 
					double width, 
					double height)
	{
		setUp(new Location(northWestLatitude, northWestLongitude),
				new Location(northEastLatitude, northEastLongitude),
				new Location(southWestLatitude, southWestLongitude),
				new Location(southEastLatitude, southEastLongitude),
				width, height);
	}
	
	public void setUp(Location northWest, Location northEast, Location southWest, Location southEast, double width, double height)
	{
		this.northWest = northWest;
		this.northEast = northEast;
		this.southWest = southWest;
		this.southEast = southEast;
		this.width = width;
		this.height = height;
	}
	
	
	public double latitudeToRow(double latitude)
	{
		// TODO: This method is not very accurate... Fix it
		
		double wyFrac = (latitude - southWest.getLatitude().toDecimal()) / (northWest.getLatitude().toDecimal() - southWest.getLatitude().toDecimal());
		double eyFrac = (latitude - southEast.getLatitude().toDecimal()) / (northEast.getLatitude().toDecimal() - southEast.getLatitude().toDecimal());
		double yFrac = 1.0 - ((wyFrac + eyFrac) / 2.0);
		double row = yFrac * (double) height;
		return row;
		
		
		//double range = getNorth() - getSouth();
		//double pos = range - (getNorth() - latitude);
		//double row = (1.0 - (pos / range)) * (double)height;
		//return row;
	}
	
	public double longitudeToColumn(double longitude)
	{
		
		// TODO: This method is not very accurate... Fix it
		double nxFrac = (longitude - northWest.getLongitude().toDecimal()) / (northEast.getLongitude().toDecimal() - northWest.getLongitude().toDecimal());
		double sxFrac = (longitude - southWest.getLongitude().toDecimal()) / (southEast.getLongitude().toDecimal() - southWest.getLongitude().toDecimal());
		double xFrac = (nxFrac + sxFrac) / 2.0;
		double column = xFrac * (double)width;
		return column;
		

		//double range = getEast() - getWest();
		//double pos = range - (longitude - getWest());
		//double col = (1.0 - (pos / range)) * (double) width;
		//return col;
	}

	protected double getNorth()
	{
		return getMaxNorth();
	}

	protected double getSouth()
	{
		return getMinSouth();
	}

	protected double getEast()
	{
		return getMaxEast();
	}

	protected double getWest()
	{
		return getMinWest();
	}

	protected double getWidth()
	{
		return width;
	}

	protected double getHeight()
	{
		return height;
	}
	
	
	/*
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
	*/

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
	
	
	
	
	
	
	
	protected double getMaxNorth()
	{
		return MathExt.max(northWest.getLatitude().toDecimal(), northEast.getLatitude().toDecimal());
	}

	protected double getMinSouth()
	{
		return MathExt.min(southWest.getLatitude().toDecimal(), southEast.getLatitude().toDecimal());
	}

	protected double getMaxEast()
	{
		return MathExt.max(northEast.getLongitude().toDecimal(), southEast.getLongitude().toDecimal());
		
		//return southEast.getLongitude().toDecimal();
	}

	protected double getMinWest()
	{
		return MathExt.min(northWest.getLongitude().toDecimal(), southWest.getLongitude().toDecimal());
	}
}
