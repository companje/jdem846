package us.wthr.jdem846ui.observers;


public class RenderedModelSelectionObserver
{
	
	private static RenderedModelSelectionObserver INSTANCE;
	
	static {
		RenderedModelSelectionObserver.INSTANCE = new RenderedModelSelectionObserver();
	}
	
	
	public RenderedModelSelectionObserver()
	{
		
		
		
		/*
		DataView.addTreeSelectionListener(new TreeSelectionAdapter()
		{
			public void onRenderedModelSelectionChanged(ElevationModel elevationModel)
			{

				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RenderedModelDisplayView.ID);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		*/
		
	}
	
	
	
	public static RenderedModelSelectionObserver getInstance()
	{
		return RenderedModelSelectionObserver.INSTANCE;
	}
}
