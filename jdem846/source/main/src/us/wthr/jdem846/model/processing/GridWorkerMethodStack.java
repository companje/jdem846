package us.wthr.jdem846.model.processing;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelDimensions;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.modelgrid.IModelGrid;
import us.wthr.jdem846.scripting.ScriptProxy;

public class GridWorkerMethodStack
{
	private GridMethodStack prepareStack = new GridMethodStack();
	private GridMethodStack onProcessBeforeStack = new GridMethodStack();
	private GridMethodStack onModelPointStack = new GridMethodStack();
	private GridMethodStack onProcessAfterStack = new GridMethodStack();
	private GridMethodStack disposeStack = new GridMethodStack();

	private GridMethodStack modelContextStack = new GridMethodStack();
	private GridMethodStack modelGridStack = new GridMethodStack();
	private GridMethodStack globalOptionModelStack = new GridMethodStack();
	private GridMethodStack optionModelStack = new GridMethodStack();
	private GridMethodStack modelDimensionsStack = new GridMethodStack();
	private GridMethodStack scriptStack = new GridMethodStack();

	public GridWorkerMethodStack()
	{

	}

	public void add(IGridWorker worker)
	{

		try {
			prepareStack.addMethod(worker, worker.getClass().getMethod("prepare"));
			onProcessBeforeStack.addMethod(worker, worker.getClass().getMethod("onProcessBefore"));
			onModelPointStack.addMethod(worker, worker.getClass().getMethod("onModelPoint", double.class, double.class));
			onProcessAfterStack.addMethod(worker, worker.getClass().getMethod("onProcessAfter"));
			disposeStack.addMethod(worker, worker.getClass().getMethod("dispose"));

			modelContextStack.addMethod(worker, worker.getClass().getMethod("setModelContext", ModelContext.class));
			modelGridStack.addMethod(worker, worker.getClass().getMethod("setModelGrid", IModelGrid.class));
			globalOptionModelStack.addMethod(worker, worker.getClass().getMethod("setGlobalOptionModel", GlobalOptionModel.class));
			optionModelStack.addMethod(worker, worker.getClass().getMethod("setOptionModel", OptionModel.class));
			modelDimensionsStack.addMethod(worker, worker.getClass().getMethod("setModelDimensions", ModelDimensions.class));
			scriptStack.addMethod(worker, worker.getClass().getMethod("setScript", ScriptProxy.class));

		} catch (NoSuchMethodException ex) {

			ex.printStackTrace();
		}

	}

	public void prepare() throws Exception
	{
		prepareStack.invoke();
	}

	public void onProcessBefore() throws Exception
	{
		onProcessBeforeStack.invoke();
	}

	public void onModelPoint(double latitude, double longitude) throws Exception
	{
		onModelPointStack.invoke(latitude, longitude);
	}

	public void onProcessAfter() throws Exception
	{
		onProcessAfterStack.invoke();
	}

	public void dispose() throws Exception
	{
		disposeStack.invoke();
	}

	public void setModelContext(ModelContext modelContext) throws Exception
	{
		modelContextStack.invoke(modelContext);
	}

	public void setModelGrid(IModelGrid modelGrid) throws Exception
	{
		modelGridStack.invoke(modelGrid);
	}

	public void setModelDimensions(ModelDimensions modelDimensions) throws Exception
	{
		modelDimensionsStack.invoke(modelDimensions);
	}

	public void setGlobalOptionModel(GlobalOptionModel globalOptionModel) throws Exception
	{
		globalOptionModelStack.invoke(globalOptionModel);
	}

	public void setScript(ScriptProxy script) throws Exception
	{
		scriptStack.invoke(script);
	}

}
