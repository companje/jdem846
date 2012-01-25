package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.render2d.ScanlinePath;

public class ScanlinePathRenderPipe extends InterruptibleProcess implements RenderPipe
{
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
		
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		while(doLoop) {
			
			ScanlinePath scanlinePathInstance = pipeline.fetchNextScanlinePath();
			
			if (scanlinePathInstance != null) {
				modelCanvas.fillScanLine(scanlinePathInstance);
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
