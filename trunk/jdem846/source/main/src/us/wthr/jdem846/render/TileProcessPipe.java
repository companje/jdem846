package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.render2d.TileRenderContainer;
import us.wthr.jdem846.render.render2d.TileRenderRunnable;

public class TileProcessPipe extends InterruptibleProcess implements RenderPipe
{
	private static Log log = Logging.getLog(TileProcessPipe.class);

	private RenderPipeline pipeline;
	private ModelContext modelContext;
	
	private boolean completed = false;
	
	public TileProcessPipe(RenderPipeline pipeline, ModelContext modelContext)
	{
		this.pipeline = pipeline;
		this.modelContext = modelContext;
	}
	
	@Override
	public void run()
	{
		boolean doLoop = true;
		
		while(doLoop) {
			TileRenderContainer tileRenderContainerInstance = pipeline.fetchNextTileRenderRunnable();
			
			if (tileRenderContainerInstance != null) {
				try {
					tileRenderContainerInstance.render(pipeline);
				} catch (RenderEngineException ex) {
					log.error("Error rendering tile: " + ex.getMessage(), ex);
					this.cancel();
				}
			}
			
			this.checkPause();
			if (isCancelled()) {
				doLoop = false;
			}
		}
		
		completed = true;
	}

	@Override
	public boolean isCompleted()
	{
		return completed;
	}
	
	
}
