package us.wthr.jdem846ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class StaticWindowInstanceProvider<E> 
{
	
	private Map<IWorkbenchWindow, E> instanceMap = new HashMap<IWorkbenchWindow, E>();
	
	public StaticWindowInstanceProvider()
	{
		
	}
	
	public void putInstance(E instance)
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		instanceMap.put(window, instance);
	}
	
	public E getInstance(IWorkbenchWindow window)
	{
		E instance = instanceMap.get(window);
		return instance;
	}
	
	public E getInstance()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		return instanceMap.get(window);
	}
	
}
