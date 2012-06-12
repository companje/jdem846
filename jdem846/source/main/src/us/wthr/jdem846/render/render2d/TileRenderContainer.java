package us.wthr.jdem846.render.render2d;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.render.RenderPipeline;

@Deprecated
public class TileRenderContainer
{
	private static Log log = Logging.getLog(TileRenderContainer.class);
	
	private ModelContext modelContext;
	private double northLimit; 
	private double southLimit;
	private double eastLimit;
	private double westLimit;
	
	private int tileColumn = 0;
	private int tileRow = 0;
	
	private TileRenderer tileRenderer = null;
	
	public TileRenderContainer(ModelContext modelContext, double northLimit, double southLimit, double eastLimit, double westLimit, int tileColumn, int tileRow)
	{
		this(modelContext, null, northLimit, southLimit, eastLimit, westLimit, tileColumn, tileRow);
	}
	
	public TileRenderContainer(ModelContext modelContext, TileRenderer tileRenderer, double northLimit, double southLimit, double eastLimit, double westLimit, int tileColumn, int tileRow)
	{
		this.modelContext = modelContext;
		this.northLimit = northLimit;
		this.southLimit = southLimit;
		this.eastLimit = eastLimit;
		this.westLimit = westLimit;
		this.tileColumn = tileColumn;
		this.tileRow = tileRow;
		this.tileRenderer = tileRenderer;
	}
	
	
	public RenderedTile render(RenderPipeline pipeline) throws RenderEngineException
	{
		//ModelCanvas modelCanvas = modelContext.getModelCanvas();
		log.info("Starting tile row #" + tileRow + ", column #" + tileColumn);
		
		if (tileRenderer == null) {
			tileRenderer = new TileRenderer(modelContext, pipeline);
		}
		
		tileRenderer.prepare(northLimit, southLimit, eastLimit, westLimit, true);
		tileRenderer.renderTile();

		tileRenderer = null;
		ModelCanvas modelCanvas = null;
		try {
			modelCanvas = modelContext.getModelCanvas();
		} catch (ModelContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		modelContext = null;
				
		log.info("Completed tile row #" + tileRow + ", column #" + tileColumn);
		return new RenderedTile(modelCanvas, northLimit, southLimit, eastLimit, westLimit, tileColumn, tileRow);
				
	}
}
