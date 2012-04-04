package us.wthr.jdem846.gis.projections;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.math.MathExt;

public abstract class AbstractBaseProjection implements MapProjection
{

	
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
	
	private double meridian = 0.0;
	
	private Planet planet = null;
	
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
		
		meridian = (getEast() + getWest()) / 2.0;
		
		planet = PlanetsRegistry.getPlanet("Earth");
	}
	
	
	@Override
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		double a_longitude = longitude - getMeridian();
		double a_latitude = latitude;// - equator;
		
		double phi = MathExt.radians(a_latitude);
		double lam = MathExt.radians(a_longitude);
		
		try {
			project(phi, lam, elevation, point);
		} catch (MapProjectionException ex) {
			throw new MapProjectionException("Error projecting coordinates " + latitude + "/" + longitude, ex);
		}
		point.column = MathExt.degrees(point.column);
		point.row = MathExt.degrees(point.row);
		
	}
	
	public abstract void project(double latitudeRadians, double longitudeRadians, double elevation, MapPoint point) throws MapProjectionException;
	
	
	
	/*
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
	*/
	
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
	
	protected double getMeridian()
	{
		return meridian;
	}

	public Planet getPlanet()
	{
		return planet;
	}

	public void setPlanet(Planet planet)
	{
		this.planet = planet;
	}
	
	
}
