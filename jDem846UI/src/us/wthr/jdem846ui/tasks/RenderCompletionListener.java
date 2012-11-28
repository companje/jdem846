package us.wthr.jdem846ui.tasks;

import us.wthr.jdem846.ElevationModel;

public interface RenderCompletionListener {
	
	public void onRenderCompleted(ElevationModel elevationModel);
	public void onRenderException(Exception ex);
	
}
