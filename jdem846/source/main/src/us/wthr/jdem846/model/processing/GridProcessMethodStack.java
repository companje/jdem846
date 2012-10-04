package us.wthr.jdem846.model.processing;

public class GridProcessMethodStack extends GridWorkerMethodStack
{
	
	private GridMethodStack onLatitudeStartStack = new GridMethodStack();
	private GridMethodStack onLatitudeEndStack = new GridMethodStack();
	
	
	@Override
	public void add(GridWorker worker)
	{
		super.add(worker);
		
		if (worker instanceof GridProcessor) {
			GridProcessor gridProcessor = (GridProcessor) worker;
			
			try {
				onLatitudeStartStack.addMethod(worker, gridProcessor.getClass().getMethod("onLatitudeStart", double.class));
				onLatitudeEndStack.addMethod(worker, gridProcessor.getClass().getMethod("onLatitudeEnd", double.class));
			} catch (NoSuchMethodException ex) {
				
				ex.printStackTrace();
			} 
			
			
		}
		
	}
	
	
	public void onLatitudeStart(double latitude) throws Exception
	{
		onLatitudeStartStack.invoke(latitude);
	}
	
	
	public void onLatitudeEnd(double latitude) throws Exception
	{
		onLatitudeEndStack.invoke(latitude);
	}
	
	
	
}
