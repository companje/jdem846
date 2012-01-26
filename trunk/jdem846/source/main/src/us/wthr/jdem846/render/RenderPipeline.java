package us.wthr.jdem846.render;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.render2d.ModelPoint;
import us.wthr.jdem846.render.render2d.ScanlinePath;
import us.wthr.jdem846.render.render2d.TileRenderContainer;
import us.wthr.jdem846.render.render2d.TileRenderRunnable;
import us.wthr.jdem846.render.shapelayer.ShapeFill;

public class RenderPipeline
{
	private static Log log = Logging.getLog(RenderPipeline.class);
	
	private Queue<TileRenderContainer> tileRenderQueue;
	private Queue<CanvasRectangleFill> canvasRectangeFillQueue;
	private Queue<ShapeFill> shapeFillQueue;
	private Queue<ScanlinePath> scanlinePathQueue;
	
	
	
	private ModelContext modelContext;
	
	private boolean completed = false;
	
	public RenderPipeline(ModelContext modelContext)
	{
		//List list = Collections.synchronizedList(new LinkedList(...));
		this.modelContext = modelContext;
		
		tileRenderQueue = new ConcurrentLinkedQueue<TileRenderContainer>();
		canvasRectangeFillQueue = new ConcurrentLinkedQueue<CanvasRectangleFill>();
		scanlinePathQueue = new ConcurrentLinkedQueue<ScanlinePath>();
		shapeFillQueue = new ConcurrentLinkedQueue<ShapeFill>();
	}
	
	public void submit(TileRenderContainer tileRenderRunnableInstance)
	{
		tileRenderQueue.add(tileRenderRunnableInstance);
	}
	
	public void submit(CanvasRectangleFill canvasRectangeFillInstance)
	{
		canvasRectangeFillQueue.add(canvasRectangeFillInstance);
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
	
	public CanvasRectangleFill fetchNextCanvasRectangeFill()
	{
		return canvasRectangeFillQueue.poll();
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
	
	public boolean hasMoreCanvasRectangeFills()
	{
		return (canvasRectangeFillQueue.size() > 0);
	}
	
	public boolean hasMoreScanlinePaths()
	{
		return (scanlinePathQueue.size() > 0);
	}
	
	public boolean hasMoreShapeFills()
	{
		return (shapeFillQueue.size() > 0);
	}
	
	protected void setCompleted(boolean completed)
	{
		this.completed = completed;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
	
}
