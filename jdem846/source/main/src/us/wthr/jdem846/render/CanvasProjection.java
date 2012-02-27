package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class CanvasProjection
{
	private static Log log = Logging.getLog(CanvasProjection.class);
	
	private MapProjection mapProjection;
	
	private double north;
	private double south;
	private double east;
	private double west;
	
	private double width; 
	private double height;
	
	//protected ModelContext modelContext;
	
	public CanvasProjection(ModelContext modelContext)
	{
		setUp(modelContext,
				modelContext.getMapProjection(), 
				modelContext.getNorth(), 
				modelContext.getSouth(),
				modelContext.getEast(),
				modelContext.getWest(),
				modelContext.getModelDimensions().getOutputWidth(),
				modelContext.getModelDimensions().getOutputHeight());
	}
	
	public void setUp(ModelContext modelContext,
					MapProjection mapProjection,
					double north,
					double south,
					double east,
					double west,
					double width,
					double height)
	{
		//this.modelContext = modelContext;
		this.mapProjection = mapProjection;
		this.north = north;
		this.south = south;
		this.east = east;
		this.west = west;
		this.width = width;
		this.height = height;
	}
	
	
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		if (mapProjection != null) {
			mapProjection.getPoint(latitude, longitude, elevation, point);
			point.row = latitudeToRow(point.row);
			point.column = longitudeToColumn(point.column);
			//point.z = elevation;
			//int i = 0;
			point.z = 0.0;
		} else {
			point.row = latitudeToRow(latitude);
			point.column = longitudeToColumn(longitude);
			point.z = elevation;
		}
	}
	
	
	public double latitudeToRow(double latitude)
	{
		return ((double) height) * ((getNorth() - latitude) / (getNorth() - getSouth()));
	}
	
	public double longitudeToColumn(double longitude)
	{
		return ((double)width) * ((longitude - getWest()) / (getEast() - getWest()));
	}

	public MapProjection getMapProjection()
	{
		return mapProjection;
	}

	public void setMapProjection(MapProjection mapProjection)
	{
		this.mapProjection = mapProjection;
	}

	public double getNorth()
	{
		return north;
	}

	public void setNorth(double north)
	{
		this.north = north;
	}

	public double getSouth()
	{
		return south;
	}

	public void setSouth(double south)
	{
		this.south = south;
	}

	public double getEast()
	{
		return east;
	}

	public void setEast(double east)
	{
		this.east = east;
	}

	public double getWest()
	{
		return west;
	}

	public void setWest(double west)
	{
		this.west = west;
	}

	public double getWidth()
	{
		return width;
	}

	public void setWidth(double width)
	{
		this.width = width;
	}

	public double getHeight()
	{
		return height;
	}

	public void setHeight(double height)
	{
		this.height = height;
	}
	
	
}
