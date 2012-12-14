package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;

public class ModelGridContext implements DataContext
{
	private static Log log = Logging.getLog(ModelGridContext.class);

	private IModelGrid modelGrid = null;
	private IFillControlledModelGrid fillControlledModelGrid = null;

	private String gridLoadedFrom = null;
	private ModelGridHeader userProvidedModelGridHeader;
	
	private boolean isDisposed = false;

	public ModelGridContext()
	{

	}

	@Override
	public void prepare() throws ContextPrepareException
	{

	}

	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Modelgrid context already disposed of");
		}

		isDisposed = true;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}

	public void setModelGrid(IModelGrid modelGrid)
	{
		this.modelGrid = modelGrid;
	}

	public IModelGrid getModelGrid()
	{
		return modelGrid;
	}
	
	public IFillControlledModelGrid getFillControlledModelGrid()
	{
		return fillControlledModelGrid;
	}

	public void setFillControlledModelGrid(IFillControlledModelGrid fillControlledModelGrid)
	{
		this.fillControlledModelGrid = fillControlledModelGrid;
	}

	public void importModelGrid(String filePath) throws DataSourceException
	{
		this.modelGrid = null;
		this.userProvidedModelGridHeader = null;
		this.gridLoadedFrom = filePath;
		
		if (filePath != null) {
			UserProvidedModelGrid userProvidedModelGrid = new UserProvidedModelGrid(filePath);
			this.userProvidedModelGridHeader = userProvidedModelGrid.getModelGridHeader();
			this.modelGrid = userProvidedModelGrid;
		} 
	}

	public void exportModelGrid(String filePath) throws DataSourceException
	{

	}
	

	public ModelGridHeader getUserProvidedModelGridHeader()
	{
		return userProvidedModelGridHeader;
	}
	
	public String getGridLoadedFrom()
	{
		return gridLoadedFrom;
	}
	
	@Override
	public ModelGridContext copy() throws DataSourceException
	{
		ModelGridContext context = new ModelGridContext();
		
		context.modelGrid = this.modelGrid;
		context.fillControlledModelGrid = context.fillControlledModelGrid;

		context.gridLoadedFrom = this.gridLoadedFrom;
		context.userProvidedModelGridHeader = this.userProvidedModelGridHeader;
		
		context.isDisposed = this.isDisposed;
		
		return context;
	}

	@Override
	public double getNorth()
	{
		if (modelGrid != null) {
			return modelGrid.getNorth();
		} else {
			return DemConstants.COORDINATE_NOT_SET;
		}
	}

	@Override
	public double getSouth()
	{
		if (modelGrid != null) {
			return modelGrid.getSouth();
		} else {
			return DemConstants.COORDINATE_NOT_SET;
		}
	}

	@Override
	public double getEast()
	{
		if (modelGrid != null) {
			return modelGrid.getEast();
		} else {
			return DemConstants.COORDINATE_NOT_SET;
		}
	}

	@Override
	public double getWest()
	{
		if (modelGrid != null) {
			return modelGrid.getWest();
		} else {
			return DemConstants.COORDINATE_NOT_SET;
		}
	}

}
