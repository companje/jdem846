package us.wthr.jdem846.modelgrid;

import java.math.BigDecimal;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.graphics.Colors;
import us.wthr.jdem846.graphics.IColor;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.GridFilter;
import us.wthr.jdem846.model.processing.GridFilterMethodStack;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;

public class FillControlledModelGrid extends BaseModelGrid implements IFillControlledModelGrid
{
	private static Log log = Logging.getLog(FillControlledModelGrid.class);

	private RasterDataContext rasterDataContext;
	private IModelGrid modelGrid;
	private ScriptProxy scriptProxy;

	private boolean forceResetAndRunFilters = false;

	// private List<GridPointFilter> gridFilters = new
	// ArrayList<GridPointFilter>();
	private GridFilterMethodStack gridFilters = new GridFilterMethodStack();

	private boolean zeroInCaseOfNoRaster = false;

	public FillControlledModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum,
			boolean zeroInCaseOfNoRaster, RasterDataContext rasterDataContext, IModelGrid modelGrid, ScriptProxy scriptProxy)
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);

		this.rasterDataContext = rasterDataContext;
		this.modelGrid = modelGrid;
		this.scriptProxy = scriptProxy;
		this.zeroInCaseOfNoRaster = zeroInCaseOfNoRaster;
	}

	public void setGridFilters(GridFilterMethodStack gridFilters)
	{
		this.gridFilters = gridFilters;
	}

	public void addGridFilter(GridFilter gridFilter)
	{
		gridFilters.add(gridFilter);
	}

	@Override
	public void dispose()
	{
		if (!modelGrid.isDisposed()) {
			modelGrid.dispose();
		}
	}

	@Override
	public boolean isDisposed()
	{
		return modelGrid.isDisposed();
	}

	@Override
	public void reset() throws DataSourceException
	{
		modelGrid.reset();
	}

	@Override
	public boolean isCompleted()
	{
		return modelGrid.isCompleted();
	}
	
	@Override
	public void setCompleted(boolean completed)
	{
		modelGrid.setCompleted(completed);
	}
	

	@Override
	public void processFiltersOnPoint(double latitude, double longitude)
	{
		boolean f = forceResetAndRunFilters;
		forceResetAndRunFilters = false;
		try {
			gridFilters.onModelPoint(latitude, longitude);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		forceResetAndRunFilters = f;
	}
	
	
	@Override
	public double getElevationByIndex(int index) throws DataSourceException
	{
		throw new DataSourceException("Index fetch not supported by fill controlled model grid");
	}

	@Override
	public void setElevationByIndex(int index, double elevation) throws DataSourceException
	{
		throw new DataSourceException("Index set not supported by fill controlled model grid");
	}
	
	
	@Override
	public double getElevation(double latitude, double longitude, boolean basic) throws DataSourceException
	{
		double elevation = modelGrid.getElevation(latitude, longitude);
		boolean doFilters = forceResetAndRunFilters;
		if (elevation == DemConstants.ELEV_UNDETERMINED) {
			try {
				elevation = getRasterData(latitude, longitude);

			} catch (Exception ex) {
				// TODO: Add a throw or something similar here
				log.warn("Error fetching elevation: " + ex.getMessage(), ex);
			}
			if (!basic) {
				modelGrid.setElevation(latitude, longitude, elevation);
				doFilters = true;
			}
		}

		if (doFilters && !basic) {

			if (forceResetAndRunFilters) {
				modelGrid.setRgba(latitude, longitude, Colors.TRANSPARENT);
			}

			processFiltersOnPoint(latitude, longitude);
		}

		return elevation;
	}

	@Override
	public void setElevation(double latitude, double longitude, double elevation) throws DataSourceException
	{
		modelGrid.setElevation(latitude, longitude, elevation);
	}

	@Override
	public void getRgbaByIndex(int index, int[] fill) throws DataSourceException
	{
		modelGrid.getRgbaByIndex(index, fill);
	}

	@Override
	public IColor getRgbaByIndex(int index) throws DataSourceException
	{
		return modelGrid.getRgbaByIndex(index);
	}

	@Override
	public void setRgbaByIndex(int index, IColor rgba) throws DataSourceException
	{
		modelGrid.setRgbaByIndex(index, rgba);
	}

	@Override
	public void setRgbaByIndex(int index, int[] rgba) throws DataSourceException
	{
		modelGrid.setRgbaByIndex(index, rgba);
	}
	
	@Override
	public IColor getRgba(int x, int y) throws DataSourceException
	{
		return modelGrid.getRgba(x, y);
	}
	
	@Override
	public void getRgba(double latitude, double longitude, int[] fill) throws DataSourceException
	{
		modelGrid.getRgba(latitude, longitude, fill);
	}

	@Override
	public IColor getRgba(double latitude, double longitude) throws DataSourceException
	{
		return modelGrid.getRgba(latitude, longitude);
	}

	@Override
	public void setRgba(double latitude, double longitude, IColor rgba) throws DataSourceException
	{
		modelGrid.setRgba(latitude, longitude, rgba);
	}

	@Override
	public void setRgba(double latitude, double longitude, int[] rgba) throws DataSourceException
	{
		modelGrid.setRgba(latitude, longitude, rgba);
	}

	@Override
	public boolean getForceResetAndRunFilters()
	{
		return forceResetAndRunFilters;
	}

	@Override
	public void setForceResetAndRunFilters(boolean forceResetAndRunFilters)
	{
		this.forceResetAndRunFilters = forceResetAndRunFilters;
	}

	protected double getRasterDataRaw(double latitude, double longitude) throws DataSourceException
	{
		double data = DemConstants.ELEV_NO_DATA;

		if (rasterDataContext.getRasterDataListSize() > 0) {
			data = rasterDataContext.getData(latitude, longitude);
		} else if (zeroInCaseOfNoRaster) {
			data = 0;
		} else {
			data = DemConstants.ELEV_NO_DATA;
		}

		return data;
	}

	protected double getRasterData(double latitude, double longitude) throws DataSourceException, RenderEngineException
	{

		double elevation = DemConstants.ELEV_NO_DATA;

		if (scriptProxy != null) {
			try {
				Object before = onGetElevationBefore(latitude, longitude);

				double value = DemConstants.ELEV_UNDETERMINED;

				if (before instanceof Double) {
					value = (Double) before;
				} else if (before instanceof BigDecimal) {
					value = ((BigDecimal) before).doubleValue();
				} else if (before instanceof Integer) {
					value = ((Integer) before).doubleValue();
				}

				if (value != DemConstants.ELEV_UNDETERMINED) {
					return value;
				}

			} catch (Exception ex) {
				throw new RenderEngineException("Error executing onGetElevationBefore(" + latitude + ", " + longitude + ")", ex);
			}
		}

		elevation = getRasterDataRaw(latitude, longitude);

		if (scriptProxy != null) {
			try {
				Object after = onGetElevationAfter(latitude, longitude, elevation);

				if (after instanceof Double) {
					elevation = (Double) after;
				} else if (after instanceof BigDecimal) {
					elevation = ((BigDecimal) after).doubleValue();
				} else if (after instanceof Integer) {
					elevation = ((Integer) after).doubleValue();
				} else {

					if (after != null) {
						int i = 0;
					}

				}

			} catch (Exception ex) {
				throw new RenderEngineException("Error executing onGetElevationAfter(" + latitude + ", " + longitude + ", " + elevation + ")", ex);
			}
		}

		return elevation;
	}

	protected Object onGetElevationBefore(double latitude, double longitude) throws RenderEngineException
	{
		Object result = null;
		try {
			if (scriptProxy != null) {
				result = scriptProxy.onGetElevationBefore(latitude, longitude);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

		return result;
	}

	protected Object onGetElevationAfter(double latitude, double longitude, double elevation) throws RenderEngineException
	{
		Object result = null;
		try {
			if (scriptProxy != null) {
				result = scriptProxy.onGetElevationAfter(latitude, longitude, elevation);
			}
		} catch (Exception ex) {
			throw new RenderEngineException("Exception thrown in user script", ex);
		}

		return result;
	}

	@Override
	public IIntBuffer getModelTexture() throws DataSourceException
	{
		return this.modelGrid.getModelTexture();
	}

	@Override
	public IFillControlledModelGrid createDependentInstance(RasterDataContext rasterDataContext)
	{
		FillControlledModelGrid instance = new FillControlledModelGrid(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum, zeroInCaseOfNoRaster, rasterDataContext,
				modelGrid, scriptProxy);
		instance.setForceResetAndRunFilters(getForceResetAndRunFilters());

		return instance;

	}





}
