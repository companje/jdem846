package us.wthr.jdem846ui.observers;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846ui.project.ProjectContext;

public class ModelPreviewChangeObserver extends ProjectChangeObserver {
	
	private static Log log = Logging.getLog(ModelPreviewChangeObserver.class);
	
	private static ModelPreviewChangeObserver INSTANCE;
	
	private ModelContext modelContextWorkingCopy = null;
	private double previewTextureQuality = 0.25;
	private double previewModelQuality = 0.25;
	
	private boolean ignoreUpdate = false;
	private boolean autoUpdate = true;
	private boolean useScripting = false;
	
	private int previewWidth = 1000;
	private int previewHeight = 1000;
	
	private ModelBuilder modelBuilder = null;
	
	/*
	 * Holy long variable names, Batman!
	 */
	private boolean lastRerenderCancelledButNeededDataRangeUpdate = false;
	private boolean lastRerenderCancelledButNeededCacheReset = false;
	
	private List<ModelPreviewReadyListener> previewReadyListeners = new LinkedList<ModelPreviewReadyListener>();
	
	private static PreviewRunTask previewRunTaskInstance = null;
	private ReRenderRequestContainer rerendering;
	
	static {
		ModelPreviewChangeObserver.INSTANCE = new ModelPreviewChangeObserver();
	}
	
	protected ModelPreviewChangeObserver()
	{
		super();
		rerendering = new ReRenderRequestContainer();
	}
	
	@Override
	public void onDataAdded() {
		update(true, false);
	}

	@Override
	public void onDataRemoved() {
		update(true, false);
	}

	@Override
	public void onOptionChanged(OptionModelChangeEvent e) {
		update(false, true);
	}
	
	
	@Override
	public void onProjectLoaded() {
		update(true, true);
	}

	protected void setWorkingCopyOptions()
	{

		GlobalOptionModel globalOptionModel = modelContextWorkingCopy.getModelProcessManifest().getGlobalOptionModel();

		globalOptionModel.setMaintainAspectRatio(false);
		//globalOptionModel.setSubpixelGridSize(1);
		globalOptionModel.setAverageOverlappedData(true);
		globalOptionModel.setGetStandardResolutionElevation(true);
		globalOptionModel.setInterpolateData(false);
		globalOptionModel.setPrecacheStrategy("none");
		globalOptionModel.setUseDiskCachedModelGrid(false);
		globalOptionModel.setDisposeGridOnComplete(false);
		//globalOptionModel.setPixelStackDepth(1);
		globalOptionModel.setCreateJdemElevationModel(false);
		globalOptionModel.setForceResetAndRunFilters(true);
		globalOptionModel.setNumberOfThreads(1);
		
		try {
			modelContextWorkingCopy.updateContext();
		} catch (ModelContextException ex) {
			// TODO Display error message dialog
			log.error("Exception updating model context: " + ex.getMessage(), ex);
		}
	}
	
	

	public void update(boolean dataModelChange, boolean optionsChanged)
	{
		update(dataModelChange, optionsChanged, false);
	}
	
	
	
	
	public void update(boolean dataModelChange, boolean optionsChanged, boolean force)
	{
		
		if (ignoreUpdate) {
			return;
		}
		
		if (!autoUpdate && !force) {
			return;
		}
		
		rerendering.setReRenderNeeded(dataModelChange, optionsChanged, force);
		if (!rerendering.isRendererWorking()) {
			rerender();
		}
		
	}
	

	protected void rerender()
	{

		
		
		if(rerendering.isReRenderNeeded()) {
			boolean dataModelChanged = rerendering.isDataModelChanged();
			boolean optionsChanged = rerendering.isOptionsChanged();
			boolean force = rerendering.isForce();
			
			
			
			if (__rerenderThreaded(dataModelChanged, optionsChanged, force)) {
				rerendering.setReRenderCaptured();
			}
			//__rerender(dataModelChanged, optionsChanged, force);
			
			//repaint();
		}

		//rerendering.setRendererWorking(false);
		
		
		
		// TODO: Make sure this never results in an infinite loop. That'd be bad.
		//if (rerendering.isReRenderNeeded()) {
		//	rerendering.setReRenderCaptured();
		//	update(rerendering.isDataModelChanged(), rerendering.isOptionsChanged(), rerendering.isForce());
		//} 
	}
	
	
	
	protected boolean __rerenderThreaded(boolean dataModelChanged, boolean optionsChanged, boolean force)
	{
		PreviewRunTask task = createPreviewRunTask(dataModelChanged, optionsChanged, force);
		if (task == null) {
			return false;
		}

		log.info("Scheduling preview render task");
		task.schedule();
		
		return true;
	}
	

	protected void __rerender(boolean dataModelChange, boolean optionsChanged, boolean force)
	{
		if (optionsChanged || dataModelChange) {
			log.info("Model visualization: update working context from actual");
			
			if (modelContextWorkingCopy != null) {
				log.info("Disposing of working copy of model context information...");
				
				try {
					modelContextWorkingCopy.dispose(true);
				} catch (DataSourceException ex) {
					log.error("Error disposing of working copy of model context information: " + ex.getMessage(), ex);
				}
			}
			
			try {
				modelContextWorkingCopy = ProjectContext.getInstance().getModelContext().copy();
				if (modelContextWorkingCopy == null || modelContextWorkingCopy.getModelProcessManifest() == null) {
					return;
				}
				
				setWorkingCopyOptions();
			} catch (DataSourceException ex) {
				log.error("Failed to copy model context: " + ex.getMessage(), ex);
				return;
			}
			

		}
		
		
		if (modelContextWorkingCopy == null || modelContextWorkingCopy.getModelProcessManifest() == null) {
			return;
		}
		
		GlobalOptionModel globalOptionModel = modelContextWorkingCopy.getModelProcessManifest().getGlobalOptionModel();
		globalOptionModel.setModelQuality(previewModelQuality);
		globalOptionModel.setTextureQuality(previewTextureQuality);

		// Only disable scripting if it's already enabled. Don't enable it if the user turned it off via the 
		// global option model
		if (globalOptionModel.getUseScripting()) {
			globalOptionModel.setUseScripting(useScripting);
		}

		if (modelContextWorkingCopy.getRasterDataContext().getRasterDataListSize() == 0) {
			modelContextWorkingCopy.setNorthLimit(90);
			modelContextWorkingCopy.setSouthLimit(-90);
			modelContextWorkingCopy.setEastLimit(180);
			modelContextWorkingCopy.setWestLimit(-180);
		}

		
		boolean resetCache = dataModelChange;
		boolean resetDataRange = dataModelChange;
		
		if (previewWidth <= 20 || previewHeight <= 20) {
			
			if (resetDataRange) {
				lastRerenderCancelledButNeededDataRangeUpdate = true;
			}
			if (resetCache) {
				lastRerenderCancelledButNeededCacheReset = true;
			}
			
			return;
		}
		if (lastRerenderCancelledButNeededCacheReset) {
			resetCache = true;
		}
		if (lastRerenderCancelledButNeededDataRangeUpdate) {
			resetDataRange = true;
		}
		lastRerenderCancelledButNeededCacheReset = false;
		lastRerenderCancelledButNeededDataRangeUpdate = false;
		
		log.info("Rendering model visualization image");
		
		int maxPreviewSize = (int) MathExt.min(this.previewHeight, this.previewWidth);//(int) MathExt.min((double)pnlModelDisplay.getWidth(), (double)pnlModelDisplay.getHeight()) - 10;
		 
		double width = globalOptionModel.getWidth();
		double height = globalOptionModel.getHeight();
		
		double aspect = width / height;
		
		if (width > height) {
			
			width = maxPreviewSize;
			height = Math.round(width / aspect);
			
		} else {
			
			height = maxPreviewSize;
			width = Math.round(height * aspect);
			
		}
		
		
		
		globalOptionModel.setWidth((int)width);
		globalOptionModel.setHeight((int)height);

		if (globalOptionModel.getLimitCoordinates()) {
			
			double optNorthLimit = globalOptionModel.getNorthLimit();
			double optSouthLimit = globalOptionModel.getSouthLimit();
			double optEastLimit = globalOptionModel.getEastLimit();
			double optWestLimit = globalOptionModel.getWestLimit();
			
			if (optNorthLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setNorthLimit(optNorthLimit);
			if (optSouthLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setSouthLimit(optSouthLimit);
			if (optEastLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setEastLimit(optEastLimit);
			if (optWestLimit != DemConstants.ELEV_NO_DATA)
				modelContextWorkingCopy.setWestLimit(optWestLimit);
		}
		
		try {
			modelContextWorkingCopy.updateContext();
		} catch (ModelContextException ex) {
			// TODO Display error message dialog
			log.error("Exception updating model context: " + ex.getMessage(), ex);
		}
		
		if (dataModelChange) {
			modelBuilder = null;
		}
		
		if (modelBuilder == null) {
			modelBuilder = new ModelBuilder();
		}
		
		try {
			
			modelBuilder.prepare(modelContextWorkingCopy);
		} catch (RenderEngineException ex) {
			log.warn("Error preparing renderer: " + ex.getMessage(), ex);
			// TODO Display error message
		}

		ElevationModel elevationModel = null;
		log.info("Rendering vizualization model...");
		try {
			elevationModel = modelBuilder.process();
		} catch (RenderEngineException ex) {
			log.error("Error rendering preview model: " + ex.getMessage(), ex);
		}
		log.info("Done rendering vizualization model");
		

		log.info("Firing model preview ready listeners");
		fireModelPreviewReadyListeners(elevationModel);
		
	}
	
	
	
	public PreviewRunTask createPreviewRunTask(boolean dataModelChange, boolean optionsChanged, boolean force)
	{
		
		if (previewRunTaskInstance == null) {
			previewRunTaskInstance = new PreviewRunTask(dataModelChange, optionsChanged, force, new PreviewRenderThreadCallback() {

				@Override
				public void render(boolean dataModelChange, boolean optionsChanged, boolean force) throws Exception {
					
					rerendering.setRendererWorking(true);
					try {
						__rerender(dataModelChange, optionsChanged, force);
					} catch (Exception ex) {
						throw ex;
					} finally {
						rerendering.setRendererWorking(false);
						previewRunTaskInstance = null;
						rerender();
					}
				}
				
			});
			return previewRunTaskInstance;
		} else {
			return null;
		}

		
	}
	
	
	
	
	
	
	public int getPreviewWidth() 
	{
		return previewWidth;
	}

	public void setPreviewWidth(int previewWidth) 
	{
		this.previewWidth = previewWidth;
	}

	public int getPreviewHeight() 
	{
		return previewHeight;
	}

	public void setPreviewHeight(int previewHeight) 
	{
		this.previewHeight = previewHeight;
	}

	
	
	public void addModelPreviewReadyListener(ModelPreviewReadyListener l)
	{
		this.previewReadyListeners.add(l);
	}
	
	public boolean removeModelPreviewReadyListener(ModelPreviewReadyListener l)
	{
		return this.previewReadyListeners.remove(l);
	}
	
	protected void fireModelPreviewReadyListeners(ElevationModel elevationModel)
	{
		for (ModelPreviewReadyListener l : this.previewReadyListeners) {
			l.onPreviewReady(elevationModel);
		}
	}
	
	public static ModelPreviewChangeObserver getInstance()
	{
		return ModelPreviewChangeObserver.INSTANCE;
	}
	
	
	

	
}
