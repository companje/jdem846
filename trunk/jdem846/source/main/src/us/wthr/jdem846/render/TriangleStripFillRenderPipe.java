package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class TriangleStripFillRenderPipe extends AbstractPipe
{
	private static Log log = Logging.getLog(TriangleStripFillRenderPipe.class);
	
	private RenderPipeline pipeline;
	private ModelContext modelContext;
	private boolean completed = false;
	
	public TriangleStripFillRenderPipe(RenderPipeline pipeline, ModelContext modelContext)
	{
		this.pipeline = pipeline;
		this.modelContext = modelContext;
	}
	
	@Override
	public void run()
	{
		boolean doLoop = true;
		
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		while(doLoop) {
			
			TriangleStripFill triangleStripFillInstance = pipeline.fetchNextTriangleStripFill();
			
			if (triangleStripFillInstance != null) {
				try {
					triangleStripFillInstance.fill(modelCanvas);
				} catch (Exception ex) {
					log.error("Error filling triangles on canvas: " + ex.getMessage(), ex);
					this.cancel();
				}
			}
			
			//Thread.yield();
			
			sleep();
			checkPause();
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
