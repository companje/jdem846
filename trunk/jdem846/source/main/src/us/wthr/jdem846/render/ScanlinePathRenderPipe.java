package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.render2d.ScanlinePath;

public class ScanlinePathRenderPipe extends AbstractPipe
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ScanlinePathRenderPipe.class);

	private RenderPipeline pipeline;
	private ModelContext modelContext;
	
	private boolean completed = false;
	
	public ScanlinePathRenderPipe(RenderPipeline pipeline, ModelContext modelContext)
	{
		this.pipeline = pipeline;
		this.modelContext = modelContext;
	}
	
	@Override
	public void run()
	{
		boolean doLoop = true;
		
		ModelCanvas modelCanvas = null;
		try {
			modelCanvas = modelContext.getModelCanvas();
		} catch (ModelContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(doLoop) {
			
			ScanlinePath scanlinePathInstance = pipeline.fetchNextScanlinePath();
			
			if (scanlinePathInstance != null) {
			//	modelCanvas.fillScanLine(scanlinePathInstance);
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
