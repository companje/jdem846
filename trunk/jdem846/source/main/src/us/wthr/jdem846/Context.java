package us.wthr.jdem846;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;

public interface Context
{
	public void prepare() throws ContextPrepareException;
	public void dispose() throws DataSourceException;
	public boolean isDisposed();
	public Context copy() throws DataSourceException;
}
