package us.wthr.jdem846.kml;

import java.util.LinkedList;
import java.util.List;

public class GriddedModel
{
	private List<Tile> tiles = new LinkedList<Tile>();
	
	private double latitudeResolution;
	private double longitudeResolution ;
	
	private double north = 0;
	private double south = 0;
	private double east = 0;
	private double west = 0;
	
	public GriddedModel(double latitudeResolution, double longitudeResolution)
	{
		setLatitudeResolution(latitudeResolution);
		setLongitudeResolution(longitudeResolution);
	}
	
	
	public List<Tile> getTilesIntersecting(int fromRow, int fromColumn, int toRow, int toColumn)
	{
		List<Tile> tilesIntersecting = new LinkedList<Tile>();
		
		for (Tile tile : tiles) {
			if (tile.intersects(fromRow, fromColumn, toRow, toColumn)) {
				tilesIntersecting.add(tile);
			}
		}
		
		return tilesIntersecting;
	}
	
	public void addTile(Tile tile)
	{
		tiles.add(tile);
	}

	public List<Tile> getTiles()
	{
		return tiles;
	}

	public void setTiles(List<Tile> tiles)
	{
		this.tiles = tiles;
	}

	public double getLatitudeResolution()
	{
		return latitudeResolution;
	}

	public void setLatitudeResolution(double latitudeResolution)
	{
		this.latitudeResolution = latitudeResolution;
	}

	public double getLongitudeResolution()
	{
		return longitudeResolution;
	}

	public void setLongitudeResolution(double longitudeResolution)
	{
		this.longitudeResolution = longitudeResolution;
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
	
	
	
	
	
}
