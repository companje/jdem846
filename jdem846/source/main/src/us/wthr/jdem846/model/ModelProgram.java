package us.wthr.jdem846.model;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.graphics.framebuffer.FrameBuffer;
import us.wthr.jdem846.model.processing.GridFilter;
import us.wthr.jdem846.model.processing.GridFilterMethodStack;
import us.wthr.jdem846.model.processing.GridProcessMethodStack;
import us.wthr.jdem846.model.processing.GridProcessor;
import us.wthr.jdem846.model.processing.GridWorker;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;

public class ModelProgram
{
	private GridProcessMethodStack processStack = new GridProcessMethodStack();
	private GridFilterMethodStack filterStack = new GridFilterMethodStack();
	
	
	private ModelContext modelContext;
	private ScriptProxy script;

	private RasterDataContext rasterDataContext;
	private ModelPointGrid modelGrid;
	private ModelDimensions modelDimensions;
	private GlobalOptionModel globalOptionModel;
	
	
	public ModelProgram()
	{
		
	}

	public ModelProgram(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}
	
	public void addWorker(GridWorker worker)
	{
		if (worker instanceof GridProcessor) {
			addProcessor((GridProcessor) worker);
		} else if (worker instanceof GridFilter) {
			addFilter((GridFilter)worker);
		}
	}
	
	public void prepare() throws Exception
	{
		this.filterStack.prepare();
		this.processStack.prepare();
	}
	
	public GridProcessMethodStack getProcessStack()
	{
		return processStack;
	}

	public GridFilterMethodStack getFilterStack()
	{
		return filterStack;
	}

	public void addProcessor(GridProcessor gridProcessor)
	{
		processStack.add(gridProcessor);
	}
	
	public void addFilter(GridFilter gridFilter)
	{
		filterStack.add(gridFilter);
	}

	public ModelContext getModelContext()
	{
		return modelContext;
	}

	public void setModelContext(ModelContext modelContext) throws Exception
	{
		this.modelContext = modelContext;
		filterStack.setModelContext(modelContext);
		processStack.setModelContext(modelContext);
	}

	public ScriptProxy getScript()
	{
		return script;
	}

	public void setScript(ScriptProxy script) throws Exception
	{
		this.script = script;
		filterStack.setScript(script);
		processStack.setScript(script);
	}

	public RasterDataContext getRasterDataContext()
	{
		return rasterDataContext;
	}

	public void setRasterDataContext(RasterDataContext rasterDataContext) throws Exception
	{
		this.rasterDataContext = rasterDataContext;
		
	}

	public ModelPointGrid getModelGrid()
	{
		return modelGrid;
	}

	public void setModelGrid(ModelPointGrid modelGrid) throws Exception
	{
		this.modelGrid = modelGrid;
		filterStack.setModelGrid(modelGrid);
		processStack.setModelGrid(modelGrid);
	}

	public ModelDimensions getModelDimensions()
	{
		return modelDimensions;
	}

	public void setModelDimensions(ModelDimensions modelDimensions) throws Exception
	{
		this.modelDimensions = modelDimensions;
		filterStack.setModelDimensions(modelDimensions);
		processStack.setModelDimensions(modelDimensions);
	}

	public GlobalOptionModel getGlobalOptionModel()
	{
		return globalOptionModel;
	}

	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel) throws Exception
	{
		this.globalOptionModel = globalOptionModel;
		filterStack.setGlobalOptionModel(globalOptionModel);
		processStack.setGlobalOptionModel(globalOptionModel);
	}
	
	
	
	
}
