package us.wthr.jdem846.render;


import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.render2d.ScanlinePath;
import us.wthr.jdem846.render.render2d.TileRenderContainer;
import us.wthr.jdem846.render.shapelayer.ShapeFill;

public class RenderPipeline
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(RenderPipeline.class);
	
	private PipelineQueue<TileRenderContainer> tileRenderQueue;
	private PipelineQueue<TriangleStripFill> triangleStripFillQueue;
	private PipelineQueue<ShapeFill> shapeFillQueue;
	private PipelineQueue<ScanlinePath> scanlinePathQueue;
	
	
	
	private ModelContext modelContext;
	
	private boolean completed = false;
	
	public RenderPipeline(ModelContext modelContext)
	{
		//List list = Collections.synchronizedList(new LinkedList(...));
		this.modelContext = modelContext;
		
		tileRenderQueue = new PipelineQueue<TileRenderContainer>();
		triangleStripFillQueue = new PipelineQueue<TriangleStripFill>();
		scanlinePathQueue = new PipelineQueue<ScanlinePath>();
		shapeFillQueue = new PipelineQueue<ShapeFill>();
	}
	
	public void submit(TileRenderContainer tileRenderRunnableInstance)
	{
		tileRenderQueue.add(tileRenderRunnableInstance);
	}
	
	public void submit(TriangleStripFill triangleStripFillInstance)
	{
		triangleStripFillQueue.add(triangleStripFillInstance);
	}
	
	public void submit(ScanlinePath scanlinePathInstance)
	{
		scanlinePathQueue.add(scanlinePathInstance);
	}
	
	public void submit(ShapeFill shapeFillInstance)
	{
		shapeFillQueue.add(shapeFillInstance);
	}
	
	
	public TileRenderContainer fetchNextTileRenderRunnable()
	{
		return tileRenderQueue.poll();
	}
	
	public TriangleStripFill fetchNextTriangleStripFill()
	{
		return triangleStripFillQueue.poll();
	}
	
	public ScanlinePath fetchNextScanlinePath()
	{
		return scanlinePathQueue.poll();
	}
	
	public ShapeFill fetchNextShapeFill()
	{
		return shapeFillQueue.poll();
	}
	
	public boolean hasMoreTileRenderRunnables()
	{
		return (tileRenderQueue.size() > 0);
	}
	
	public boolean hasMoreTriangleStripFills()
	{
		return (triangleStripFillQueue.size() > 0);
	}
	
	public boolean hasMoreScanlinePaths()
	{
		return (scanlinePathQueue.size() > 0);
	}
	
	public boolean hasMoreShapeFills()
	{
		return (shapeFillQueue.size() > 0);
	}
	
	
	public void flushQueues()
	{
		flushTileRenderQueue();
		flushTriangleStripFillQueue();
		flushShapeFillQueue();
		flushScanlinePathQueue();
	}
	
	public void flushTileRenderQueue()
	{
		tileRenderQueue.clear();

	}
	
	public void flushTriangleStripFillQueue()
	{
		triangleStripFillQueue.clear();
	}
	
	public void flushShapeFillQueue()
	{
		shapeFillQueue.clear();
	}
	
	public void flushScanlinePathQueue()
	{
		scanlinePathQueue.clear();
	}
	
	
	public void closeQueues()
	{
		
		closeTitleRenderQueue();
		closeTriangleStripFillQueue();
		closeShapeFillQueue();
		closeScanlinePathQueue();
	}
	
	public void closeTitleRenderQueue()
	{
		tileRenderQueue.close();
	}
	
	public void closeTriangleStripFillQueue()
	{
		triangleStripFillQueue.close();
	}
	
	public void closeShapeFillQueue()
	{
		shapeFillQueue.close();
	}
	
	public void closeScanlinePathQueue()
	{
		scanlinePathQueue.close();
	}
	
	

	/*
	private Queue<TileRenderContainer> tileRenderQueue;
	private Queue<TriangleStripFill> triangleStripFillQueue;
	private Queue<ShapeFill> shapeFillQueue;
	private Queue<ScanlinePath> scanlinePathQueue;
	*/
	
	protected void setCompleted(boolean completed)
	{
		this.completed = completed;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
	
}
