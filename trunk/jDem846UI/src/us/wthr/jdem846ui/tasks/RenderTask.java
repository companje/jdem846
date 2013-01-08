package us.wthr.jdem846ui.tasks;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ProgressTracker;
import us.wthr.jdem846.model.exceptions.ContextPrepareException;
import us.wthr.jdem846ui.project.ProjectContext;

public class RenderTask extends Job 
{
	private static Log log = Logging.getLog(RenderTask.class);
	
	private List<RenderCompletionListener> completionListeners = new LinkedList<RenderCompletionListener>();
	
	public RenderTask(List<RenderCompletionListener> completionListeners)
	{
		super("Model Render");
		this.completionListeners.addAll(completionListeners);
		
	}

	@Override
	protected IStatus run(IProgressMonitor progressMonitor) 
	{
		log.info("Model rendering task starting");
		
		
		long start = 0;
		long elapsed = 0;
		
		ModelContext modelContext = null;
		
		try {
			modelContext = ProjectContext.getInstance().getModelContext().copy();
		} catch (DataSourceException ex) {
			fireRenderExceptionListeners(ex);
			return Status.OK_STATUS;
		}
		
		modelContext.getModelProcessManifest().getGlobalOptionModel().setGetStandardResolutionElevation(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.standardResolutionRetrieval"));
		modelContext.getModelProcessManifest().getGlobalOptionModel().setInterpolateData(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.interpolateToHigherResolution"));
		modelContext.getModelProcessManifest().getGlobalOptionModel().setAverageOverlappedData(JDem846Properties.getBooleanProperty("us.wthr.jdem846.rendering.averageOverlappedData"));
		modelContext.getModelProcessManifest().getGlobalOptionModel().setPrecacheStrategy(JDem846Properties.getProperty("us.wthr.jdem846.performance.precacheStrategy"));
		modelContext.getModelProcessManifest().getGlobalOptionModel().setTileSize(JDem846Properties.getIntProperty("us.wthr.jdem846.performance.tileSize"));
		
		
		try {
			modelContext.getScriptingContext().prepare();
		} catch (ContextPrepareException ex) {
			fireRenderExceptionListeners(ex);
			return Status.OK_STATUS;
		}
		
		ModelBuilder modelBuilder = new ModelBuilder(new RenderProgressMonitor(progressMonitor));
		
		log.info("Initializing model builder...");
		try {
			modelBuilder.prepare(modelContext);
		} catch (RenderEngineException ex) {
			fireRenderExceptionListeners(ex);
			return Status.OK_STATUS;
		}
		
		ElevationModel elevationModel = null;
		
		log.info("Processing...");
		start = System.currentTimeMillis();
		
		try {
			elevationModel = modelBuilder.process();
		} catch (RenderEngineException ex) {
			fireRenderExceptionListeners(ex);
			return Status.OK_STATUS;
		}
		
		//OutputProduct<ModelCanvas> product = engine.generate(false, false);
		elapsed = (System.currentTimeMillis() - start) / 1000;
		

		
		log.info("Completed render task in " + elapsed + " seconds");
		
		fireRenderCompletionListeners(elevationModel);
		
		ProjectContext.getInstance().addElevationModel(elevationModel);
		
		return Status.OK_STATUS;
	}

	
	protected void fireRenderExceptionListeners(Exception ex)
	{
		for (RenderCompletionListener l : this.completionListeners) {
			l.onRenderException(ex);
		}
	}
	
	protected void fireRenderCompletionListeners(ElevationModel elevationModel)
	{
		for (RenderCompletionListener l : this.completionListeners) {
			l.onRenderCompleted(elevationModel);
		}
	}
	
	
	
	
	protected static class RenderProgressMonitor implements ProgressTracker
	{
		private IProgressMonitor progressMonitor;
		
		public RenderProgressMonitor(IProgressMonitor progressMonitor)
		{
			this.progressMonitor = progressMonitor;
		}
		
		@Override
		public void beginTask(String name, int totalWork) 
		{
			this.progressMonitor.beginTask(name, totalWork);
		}

		@Override
		public void done() 
		{
			progressMonitor.done();
		}

		@Override
		public void internalWorked(double work)
		{
			progressMonitor.internalWorked(work);
		}

		@Override
		public boolean isCanceled() 
		{
			return progressMonitor.isCanceled();
		}

		@Override
		public void setCanceled(boolean value) 
		{
			progressMonitor.setCanceled(value);
		}

		@Override
		public void setTaskName(String name)
		{
			progressMonitor.setTaskName(name);
		}

		@Override
		public void subTask(String name) 
		{
			progressMonitor.subTask(name);
		}

		@Override
		public void worked(int work) 
		{
			progressMonitor.worked(work);
		}
		
	}
}
