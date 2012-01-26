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
	private ShapeFillPipe shapeFillPipe;
	
	private Thread tileProcessThread;
	private Thread canvasFillRenderThread;
	private Thread scanlinePathRenderingThread;
	private Thread shapeFillThread;
	
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
		shapeFillPipe = new ShapeFillPipe(pipeline, modelContext);
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
		
		shapeFillThread = new Thread()
		{
			public void run()
			{
				shapeFillPipe.run();
			}
		};
		
		log.info("Starting Tile Process Thread...");
		tileProcessThread.start();
		
		log.info("Starting Canvas Fill Render Thread...");
		canvasFillRenderThread.start();
		
		log.info("Starting Shape Fill Thread...");
		shapeFillThread.start();
		
		log.info("Starting Scanline Path Rendering Thread...");
		scanlinePathRenderingThread.start();
		
	}
	
	
	public boolean areQueuesEmpty()
	{
		return (!pipeline.hasMoreCanvasRectangeFills()
				&& !pipeline.hasMoreScanlinePaths()
				&& !pipeline.hasMoreTileRenderRunnables()
				&& !pipeline.hasMoreShapeFills());
	}
	
	
	public void pause()
	{
		tileProcessPipe.pause();
		canvasFillRenderPipe.pause();
		scanlinePathRenderingPipe.pause();
		shapeFillPipe.pause();
	}
	
	public void stop(boolean block)
	{
		tileProcessPipe.cancel();
		canvasFillRenderPipe.cancel();
		scanlinePathRenderingPipe.cancel();
		shapeFillPipe.cancel();
		
		if (block) {
			while(!tileProcessPipe.isCompleted()) {
				
			}
			while(!canvasFillRenderPipe.isCompleted()) {
				
			}
			while(!shapeFillPipe.isCompleted()) {
				
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
