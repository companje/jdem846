package us.wthr.jdem846ui.observers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class PreviewRunTask extends Job {
	private static Log log = Logging.getLog(PreviewRunTask.class);
	
	private boolean dataModelChange;
	private boolean optionsChanged;
	private boolean force;
	
	private boolean active = false;
	
	private PreviewRenderThreadCallback callback;
	
	public PreviewRunTask(boolean dataModelChange, boolean optionsChanged, boolean force, PreviewRenderThreadCallback callback)
	{
		super("Model Preview Render Task");
		
		this.dataModelChange = dataModelChange;
		this.optionsChanged = optionsChanged;
		this.force = force;
		this.callback = callback;
	}
	


	@Override
	protected IStatus run(IProgressMonitor arg0) 
	{
		active = true;
		try {
			callback.render(dataModelChange, optionsChanged, force);
		} catch (Exception ex) {
			log.error("Failed to rerender preview: " + ex.getMessage(), ex);
			return Status.CANCEL_STATUS;
		}
		active = false;
		return Status.OK_STATUS;
	}
	
	
	
}
