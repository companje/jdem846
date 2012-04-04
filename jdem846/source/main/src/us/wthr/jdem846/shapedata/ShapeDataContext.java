package us.wthr.jdem846.shapedata;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;


public class ShapeDataContext implements DataContext
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ShapeDataContext.class);
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private List<ShapeFileRequest> shapeFiles = new LinkedList<ShapeFileRequest>();
	
	private boolean isDisposed = false;
	
	public ShapeDataContext()
	{
		
	}
	
	public void prepare() throws DataSourceException
	{
		
		east = -180.0;
		west = 180.0;
		north = -90.0;
		south = 90.0;
		
		for (ShapeFileRequest shapeFileRequest : shapeFiles) {
			// TODO: Get the geographic ranges
			
		}
		
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Shape data proxy already disposed.");
		}
		
		for (ShapeFileRequest shapeFileRequest : shapeFiles) {
			shapeFileRequest.dispose();
		}
		
		
		// TODO: Finish
		isDisposed = true;
	}

	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	
	public void addShapeFile(String path, String shapeDataDefinitionId) throws ShapeFileException, DataSourceException
	{
		shapeFiles.add(new ShapeFileRequest(path, shapeDataDefinitionId));
		prepare();
	}
	
	public void addShapeFile(ShapeFileRequest shapeFileRequest) throws DataSourceException
	{
		shapeFiles.add(shapeFileRequest);
		prepare();
	}
	
	public ShapeFileRequest removeShapeFile(int index) throws DataSourceException
	{
		ShapeFileRequest removed = shapeFiles.remove(index);
		if (removed != null) {
			prepare();
		}
		return removed;
	}
	
	public boolean removeShapeFile(ShapeFileRequest shapeFileRequest) throws DataSourceException
	{
		boolean result = shapeFiles.remove(shapeFileRequest);
		if (result) {
			prepare();
		}
		return result;
	}
	
	public List<ShapeFileRequest> getShapeFiles()
	{
		return shapeFiles;
	}
	
	public int getShapeDataListSize()
	{
		return shapeFiles.size();
	}
	

	public void setShapeFiles(List<ShapeFileRequest> shapeFiles)
	{
		this.shapeFiles = shapeFiles;
	}
	
	
	
	public double getEast()
	{
		return east;
	}

	public double getWest()
	{
		return west;
	}

	public double getNorth()
	{
		return north;
	}

	public double getSouth()
	{
		return south;
	}

	public ShapeDataContext copy() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Cannot copy object: already disposed");
		}
		
		ShapeDataContext clone = new ShapeDataContext();
		clone.north = getNorth();
		clone.south = getSouth();
		clone.east = getEast();
		clone.west = getWest();
		clone.isDisposed = isDisposed(); // Should be false at this point...	
		
		for (ShapeFileRequest shapeFileRequest : shapeFiles) {
			clone.shapeFiles.add(shapeFileRequest.copy());
		}
		

		return clone;
	}
}
