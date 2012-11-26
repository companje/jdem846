package us.wthr.jdem846ui.observers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelChangeEvent;

public class ElevationRangeChangeObserver extends ProjectChangeObserver {

	private static Log log = Logging.getLog(ElevationRangeChangeObserver.class);
	
	public ElevationRangeChangeObserver() {
		super();
	}

	
	@Override
	public void onDataAdded() {

	}

	@Override
	public void onDataRemoved() {

	}

	@Override
	public void onOptionChanged(OptionModelChangeEvent e) {
		
		
		
		  // new DecathlonJob().schedule();
		
	}
	
	
	static class DecathlonJob extends Job {
		private static Log log = Logging.getLog(DecathlonJob.class);
	      public DecathlonJob() {
	         super("Athens decathlon 2004");
	      }
	      public IStatus run(IProgressMonitor monitor) {
	    	  System.err.println("Running fake job...");
	    	  try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	  System.err.println("Ending fake job...");
	         return Status.OK_STATUS;
	      }
	   };
}
