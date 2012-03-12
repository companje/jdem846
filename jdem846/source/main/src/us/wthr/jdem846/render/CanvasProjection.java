package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.gis.Location;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;

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
	
	private boolean usePointAdjustments = false;
	private double minX;
	private double maxX;
	private double minY;
	private double maxY;
	
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
	
	public CanvasProjection(ModelContext modelContext,
			MapProjection mapProjection,
			double north,
			double south,
			double east,
			double west,
			double width,
			double height)
	{
		setUp(modelContext,
				mapProjection,
				north, 
				south,
				east,
				west,
				width,
				height);
	}
	
	public CanvasProjection(MapProjection mapProjection,
			double north,
			double south,
			double east,
			double west,
			double width,
			double height)
	{
		setUp(null,
				mapProjection,
				north, 
				south,
				east,
				west,
				width,
				height);
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
		
		determineXYAdjustments();
	}
	
	private void determineXYAdjustments()
	{
		if (mapProjection != null) {
			usePointAdjustments = true;
			
			/*
			minX = west;
			maxX = east;
			minY = south;
			maxY = north;
			*/
			
			
			minX = 180;
			maxX = -180;
			
			minY = Double.MAX_VALUE;
			maxY = Double.MIN_VALUE;
			
			MapPoint point = new MapPoint();
			try {
				mapProjection.getPoint(north, west, 0.0, point);
				checkXYMinMax(point);
				
				mapProjection.getPoint(north, east, 0.0, point);
				checkXYMinMax(point);
				
				mapProjection.getPoint(north, (west + east) / 2.0, 0.0, point);
				checkXYMinMax(point);
				
				mapProjection.getPoint(south, west, 0.0, point);
				checkXYMinMax(point);
				
				mapProjection.getPoint(south, east, 0.0, point);
				checkXYMinMax(point);

				mapProjection.getPoint(south, (west + east) / 2.0, 0.0, point);
				checkXYMinMax(point);
				
				mapProjection.getPoint((north + south) / 2.0, west, 0.0, point);
				checkXYMinMax(point);
				
				mapProjection.getPoint((north + south) / 2.0, east, 0.0, point);
				checkXYMinMax(point);
				
				int i = 0;
			} catch (MapProjectionException ex) {
				ex.printStackTrace();
			}
			
		}
	}
	
	private void checkXYMinMax(MapPoint point)
	{
		minX = MathExt.min(minX, point.column);
		maxX = MathExt.max(maxX, point.column);
		minY = MathExt.min(minY, point.row);
		maxY = MathExt.max(maxY, point.row);

	}

	
	public void getPoint(double latitude, double longitude, double elevation, MapPoint point) throws MapProjectionException
	{
		if (mapProjection != null) {
			mapProjection.getPoint(latitude, longitude, elevation, point);
			
			double orig_column = point.column;
			
			point.row = latitudeToRow(point.row);
			point.column = longitudeToColumn(point.column);
			
			if (Double.isInfinite(point.column)) {
				int i = 0;
			}
			
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
		if (usePointAdjustments) {
			return ((double) height) * ((maxY - latitude) / (maxY - minY));
		} else {
			return ((double) height) * ((getNorth() - latitude) / (getNorth() - getSouth()));
		}
	}
	
	public double longitudeToColumn(double longitude)
	{
		if (usePointAdjustments) {
			return ((double)width) * ((longitude - minX)) / (maxX - minX);
		} else {
			return ((double)width) * ((longitude - getWest()) / (getEast() - getWest()));
		}
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
