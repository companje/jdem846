package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class CanvasFillRenderPipe extends InterruptibleProcess implements RenderPipe
{
	private static Log log = Logging.getLog(CanvasFillRenderPipe.class);

	private RenderPipeline pipeline;
	private ModelContext modelContext;
	
	private boolean completed = false;
	
	public CanvasFillRenderPipe(RenderPipeline pipeline, ModelContext modelContext)
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
			
			CanvasRectangleFill canvasRectangeFillInstance = null;//pipeline.fetchNextCanvasRectangeFill();
			
			if (canvasRectangeFillInstance != null) {
				try {
					canvasRectangeFillInstance.fill(modelCanvas);
				} catch (CanvasException ex) {
					log.error("Error filling polygon on canvas: " + ex.getMessage(), ex);
					this.cancel();
				}
			}
			
			Thread.yield();
			/*
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
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
