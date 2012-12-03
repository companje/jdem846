package us.wthr.jdem846.rasterdata.generic;

import java.io.File;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.AbstractRasterDataProvider;
import us.wthr.jdem846.rasterdata.RasterData;

public class GenericRasterDataProvider extends AbstractRasterDataProvider 
{
	private static Log log = Logging.getLog(GenericRasterDataProvider.class);

	private RasterDefinition rasterDefinition;
	
	private File dataFile;
	
	private boolean isDisposed = false;
	
	public GenericRasterDataProvider()
	{
		setRasterDefinition(new RasterDefinition());
	}
	
	@Override
	public void create(String file) throws DataSourceException 
	{
		dataFile = new File(file);
		
		
	}

	@Override
	public void dispose() throws DataSourceException 
	{
		// TODO: Dispose of stuff
		isDisposed = true;
	}

	@Override
	public boolean isDisposed() 
	{
		return isDisposed;
	}

	@Override
	public RasterData copy() throws DataSourceException 
	{
		if (isDisposed()) {
			throw new DataSourceException("Cannot copy object: already disposed");
		}
		
		GenericRasterDataProvider clone = new GenericRasterDataProvider();
		clone.create(getFilePath());
		clone.setRasterDefinition(getRasterDefinition().copy());
		
		return clone;
	}

	@Override
	public String getFilePath()
	{
		return dataFile.getAbsolutePath();
	}

	@Override
	public double getData(int row, int column) throws DataSourceException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean fillBuffer(double north, double south, double east,
			double west) throws DataSourceException 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBufferFilled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearBuffer() throws DataSourceException 
	{
		// TODO Auto-generated method stub
		
	}

	public RasterDefinition getRasterDefinition()
	{
		return rasterDefinition;
	}

	public void setRasterDefinition(RasterDefinition rasterDefinition) 
	{
		this.rasterDefinition = rasterDefinition;
		if (this.rasterDefinition != null) {
			this.rasterDefinition.addDefinitionChangeListener(new DefinitionChangeListener() {
	
				@Override
				public void onDefinitionChanged() {
					refreshDefinitionData();
				}
				
			});
		}
		
	}
	
	protected void refreshDefinitionData()
	{
		if (this.rasterDefinition == null) {
			return;
		}
		
		this.setColumns(this.rasterDefinition.getImageWidth());
		this.setRows(this.rasterDefinition.getImageHeight());
		
		this.setNorth(this.rasterDefinition.getNorth());
		this.setSouth(this.rasterDefinition.getSouth());
		this.setEast(this.rasterDefinition.getEast());
		this.setWest(this.rasterDefinition.getWest());
		
		this.setLatitudeResolution((rasterDefinition.getNorth() - rasterDefinition.getSouth()) / rasterDefinition.getImageHeight());
		this.setLongitudeResolution((rasterDefinition.getEast() - rasterDefinition.getWest()) / rasterDefinition.getImageWidth());

		
	}
	

}
