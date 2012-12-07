package us.wthr.jdem846.modelgrid;

import us.wthr.jdem846.model.processing.GridFilter;
import us.wthr.jdem846.model.processing.GridFilterMethodStack;
import us.wthr.jdem846.rasterdata.RasterDataContext;

public interface IFillControlledModelGrid extends IModelGrid
{
	public boolean getForceResetAndRunFilters();

	public void setForceResetAndRunFilters(boolean forceResetAndRunFilters);

	public IFillControlledModelGrid createDependentInstance(RasterDataContext rasterDataContext);

	public void processFiltersOnPoint(double latitude, double longitude);

	public void setGridFilters(GridFilterMethodStack gridFilters);

	public void addGridFilter(GridFilter gridFilter);
}
