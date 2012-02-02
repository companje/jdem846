package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.shapelayer.ShapeFill;

public class ShapeFillPipe extends InterruptibleProcess implements RenderPipe
{
	private static Log log = Logging.getLog(ShapeFillPipe.class);
	
	
	private RenderPipeline pipeline;
	private ModelContext modelContext;
	
	private boolean completed = false;
	
	public ShapeFillPipe(RenderPipeline pipeline, ModelContext modelContext)
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
			
			//CanvasRectangleFill canvasRectangeFillInstance = pipeline.fetchNextCanvasRectangeFill();
			
			ShapeFill shapeFillInstance = pipeline.fetchNextShapeFill();
			
			if (shapeFillInstance != null) {
				try {
					shapeFillInstance.fill(modelCanvas);
				} catch (CanvasException ex) {
					log.error("Error filling polygon on canvas: " + ex.getMessage(), ex);
					this.cancel();
				} catch (RenderEngineException ex) {
					log.error("Error processing shape for rendering: " + ex.getMessage(), ex);
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
