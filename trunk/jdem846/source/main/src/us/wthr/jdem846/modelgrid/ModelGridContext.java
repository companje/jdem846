package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.DataContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;

public class ModelGridContext implements DataContext
{
	private static Log log = Logging.getLog(ModelGridContext.class);

	private IModelGrid modelGrid = null;

	private boolean isDisposed = false;

	public ModelGridContext()
	{

	}

	@Override
	public void prepare() throws ContextPrepareException
	{
		// TODO Auto-generated method stub

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

	public void importModelGrid(String filePath) throws DataSourceException
	{

	}

	public void exportModelGrid(String filePath) throws DataSourceException
	{

	}

	@Override
	public ModelGridContext copy() throws DataSourceException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getNorth()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSouth()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEast()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWest()
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
