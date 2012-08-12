package us.wthr.jdem846.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.GridPointFilter;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;

public class FillControlledModelGrid extends ModelPointGrid
{
	private static Log log = Logging.getLog(FillControlledModelGrid.class);
	
	private RasterDataContext rasterDataContext;
	private ModelPointGrid modelGrid;
	private ScriptProxy scriptProxy;
	
	private boolean forceResetAndRunFilters = false;
	
	private List<GridPointFilter> gridFilters = new ArrayList<GridPointFilter>();
	
	private boolean zeroInCaseOfNoRaster = false;
	
	public FillControlledModelGrid(double north, 
									double south, 
									double east,
									double west, 
									double latitudeResolution, 
									double longitudeResolution,
									double minimum, 
									double maximum,
									boolean zeroInCaseOfNoRaster,
									RasterDataContext rasterDataContext,
									ModelPointGrid modelGrid,
									ScriptProxy scriptProxy) {
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);
		
		this.rasterDataContext = rasterDataContext;
		this.modelGrid = modelGrid;
		this.scriptProxy = scriptProxy;
		this.zeroInCaseOfNoRaster = zeroInCaseOfNoRaster;
	}
	
	
	public void addGridFilter(GridPointFilter gridProcessor)
	{
		if (!gridFilters.contains(gridProcessor)) {
			gridFilters.add(gridProcessor);
		}
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
	public void reset() 
	{
		modelGrid.reset();
	}

	@Override
	public ModelPoint get(double latitude, double longitude) 
	{
		return modelGrid.get(latitude, longitude);
	}

	
	public void processFiltersOnPoint(double latitude, double longitude)
	{
		boolean f = forceResetAndRunFilters;
		forceResetAndRunFilters = false;
		for (GridPointFilter filter : this.gridFilters) {
			try {
				filter.onModelPoint(latitude, longitude);
			} catch (RenderEngineException ex) {
				// TODO Add better handling
				log.warn("Error processing point: " + ex.getMessage(), ex);
			}
		}
		forceResetAndRunFilters = f;
	}
	
	
	
	@Override
	public double getElevation(double latitude, double longitude)
	{
		double elevation = modelGrid.getElevation(latitude, longitude);
		boolean doFilters = forceResetAndRunFilters;
		if (elevation == DemConstants.ELEV_UNDETERMINED) {
			try {
				elevation = getRasterData(latitude, longitude);
				//elevation = rasterDataContext.getData(latitude, longitude);
			} catch (Exception ex) {
				// TODO: Add a throw or something similar here
				log.warn("Error fetching elevation: " + ex.getMessage(), ex);
			}
			modelGrid.setElevation(latitude, longitude, elevation);
			doFilters = true;
			
		}
		
		
		if (doFilters) {
			
			if (forceResetAndRunFilters) {
				modelGrid.setRgba(latitude, longitude, 0x0);
			}
			
			processFiltersOnPoint(latitude, longitude);
		}
		
		return elevation;
	}

	@Override
	public void setElevation(double latitude, double longitude, double elevation)
	{
		super.setElevation(latitude, longitude, elevation);
	}

	@Override
	public void getRgba(double latitude, double longitude, int[] fill)
	{
		super.getRgba(latitude, longitude, fill);
	}

	@Override
	public int getRgba(double latitude, double longitude)
	{
		return super.getRgba(latitude, longitude);
	}

	@Override
	public void setRgba(double latitude, double longitude, int rgba) 
	{
		super.setRgba(latitude, longitude, rgba);
	}

	@Override
	public void setRgba(double latitude, double longitude, int[] rgba) 
	{
		super.setRgba(latitude, longitude, rgba);
	}


	public boolean getForceResetAndRunFilters() 
	{
		return forceResetAndRunFilters;
	}


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
					value = ((BigDecimal)before).doubleValue();
				} else if (before instanceof Integer) {
					value = ((Integer)before).doubleValue();
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
					elevation = ((BigDecimal)after).doubleValue();
				} else if (after instanceof Integer) {
					elevation = ((Integer)after).doubleValue();
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
	
	
	
}
