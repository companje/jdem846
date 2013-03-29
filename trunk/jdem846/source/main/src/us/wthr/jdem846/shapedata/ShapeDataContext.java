package us.wthr.jdem846.shapedata;

import java.util.Set;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.ShapeFileReference;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;

import com.google.common.collect.Sets;


public class ShapeDataContext implements DataContext
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ShapeDataContext.class);
	
	private double east = 180.0;
	private double west = -180.0;
	private double north = 90.0;
	private double south = -90.0;
	
	private Set<ShapeBase> shapeFiles = Sets.newHashSet();
	
	private boolean isDisposed = false;
	
	public ShapeDataContext()
	{
		
	}
	
	public void prepare() throws ContextPrepareException
	{
		
		east = -180.0;
		west = 180.0;
		north = -90.0;
		south = 90.0;
		
		//for (ShapeFileReference shapeFileRequest : shapeFiles) {
			// TODO: Get the geographic ranges
			
		//}
		
	}
	
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Shape data proxy already disposed.");
		}
		
		for (ShapeBase shapeBase : shapeFiles) {
			//try {
			//	shapeBase.close();
			//} catch (ShapeFileException ex) {
			//	throw new DataSourceException("Error closing shape data: " + ex.getMessage(), ex);
			//}
		}
		
		//for (ShapeFileReference shapeFileRequest : shapeFiles) {
		//	shapeFileRequest.dispose();
		//}
		
		
		// TODO: Finish
		isDisposed = true;
	}

	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	
	public void addShapeFile(String path, String shapeDataDefinitionId) throws ShapeFileException, DataSourceException
	{
		ShapeFileReference shapeFileRef = new ShapeFileReference(path, shapeDataDefinitionId);
		try {
			shapeFiles.add(shapeFileRef.open());
		} catch (Exception ex) {
			throw new DataSourceException("Error opening shape data: " + ex.getMessage(), ex);
		}
		try {
			prepare();
		} catch (ContextPrepareException ex) {
			throw new DataSourceException("Failed to prepare shade data context: " + ex.getMessage(), ex);
		}
	}
	
	public void addShapeFile(ShapeBase shapeBase) throws DataSourceException
	{
		shapeFiles.add(shapeBase);
		try {
			prepare();
		} catch (ContextPrepareException ex) {
			throw new DataSourceException("Failed to prepare shade data context: " + ex.getMessage(), ex);
		}
	}
	
	
	public boolean removeShapeFile(ShapeFileReference shapeFileRequest) throws DataSourceException
	{
		boolean result = shapeFiles.remove(shapeFileRequest);
		if (result) {
			try {
				prepare();
			} catch (ContextPrepareException ex) {
				throw new DataSourceException("Failed to prepare shade data context: " + ex.getMessage(), ex);
			}
		}
		return result;
	}
	
	public Set<ShapeBase> getShapeFiles()
	{
		return shapeFiles;
	}
	
	public int getShapeDataListSize()
	{
		return shapeFiles.size();
	}
	

	public void setShapeFiles(Set<ShapeBase> shapeFiles)
	{
		this.shapeFiles.addAll(shapeFiles);
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
		clone.isDisposed = isDisposed(); // Should be false at this point...// So then throw!
		
		clone.shapeFiles.addAll(shapeFiles);

		return clone;
	}
}
