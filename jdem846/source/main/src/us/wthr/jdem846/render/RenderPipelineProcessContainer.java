package us.wthr.jdem846.render;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RenderPipelineProcessContainer
{
	private static Log log = Logging.getLog(RenderPipelineProcessContainer.class);
	
	private ModelContext modelContext;
	
	private RenderPipeline pipeline;
	
	private TileProcessPipe tileProcessPipe;
	private CanvasFillRenderPipe canvasFillRenderPipe;
	private ScanlinePathRenderPipe scanlinePathRenderingPipe;
	
	private Thread tileProcessThread;
	private Thread canvasFillRenderThread;
	private Thread scanlinePathRenderingThread;
	
	public RenderPipelineProcessContainer(ModelContext modelContext)
	{
		this(new RenderPipeline(modelContext), modelContext);
	}
	
	public RenderPipelineProcessContainer(RenderPipeline pipeline, ModelContext modelContext)
	{
		this.modelContext = modelContext;
		this.pipeline = pipeline;
		
		tileProcessPipe = new TileProcessPipe(pipeline, modelContext);
		canvasFillRenderPipe = new CanvasFillRenderPipe(pipeline, modelContext);
		scanlinePathRenderingPipe = new ScanlinePathRenderPipe(pipeline, modelContext);
		
	}
	
	public void start()
	{
		tileProcessThread = new Thread()
		{
			public void run()
			{
				tileProcessPipe.run();
			}
		};
		
		canvasFillRenderThread = new Thread()
		{
			public void run()
			{
				canvasFillRenderPipe.run();
			}
		};
		
		scanlinePathRenderingThread = new Thread()
		{
			public void run()
			{
				scanlinePathRenderingPipe.run();
			}
		};
		
		tileProcessThread.start();
		canvasFillRenderThread.start();
		scanlinePathRenderingThread.start();
		
	}
	
	
	public boolean areQueuesEmpty()
	{
		return (!pipeline.hasMoreCanvasRectangeFills()
				&& !pipeline.hasMoreScanlinePaths()
				&& !pipeline.hasMoreTileRenderRunnables());
	}
	
	
	public void pause()
	{
		tileProcessPipe.pause();
		canvasFillRenderPipe.pause();
		scanlinePathRenderingPipe.pause();
	}
	
	public void stop(boolean block)
	{
		tileProcessPipe.cancel();
		canvasFillRenderPipe.cancel();
		scanlinePathRenderingPipe.cancel();
		
		if (block) {
			while(!tileProcessPipe.isCompleted()) {
				
			}
			while(!canvasFillRenderPipe.isCompleted()) {
				
			}
			while(!scanlinePathRenderingPipe.isCompleted()) {
				
			}
		}
		
	}
	
	
	public RenderPipeline getRenderPipeline()
	{
		return pipeline;
	}
}
