package us.wthr.jdem846.ui;

import java.awt.Component;

import javax.swing.JSplitPane;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class DisposableSplitPane extends JSplitPane implements Disposable
{
	private static Log log = Logging.getLog(DisposableSplitPane.class);
	
	
	
	
	
	public DisposableSplitPane()
	{
		super();
		// TODO Auto-generated constructor stub
	}





	public DisposableSplitPane(int newOrientation, boolean newContinuousLayout,
			Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
		// TODO Auto-generated constructor stub
	}





	public DisposableSplitPane(int newOrientation, boolean newContinuousLayout)
	{
		super(newOrientation, newContinuousLayout);
		// TODO Auto-generated constructor stub
	}





	public DisposableSplitPane(int newOrientation, Component newLeftComponent,
			Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);
		// TODO Auto-generated constructor stub
	}





	public DisposableSplitPane(int newOrientation)
	{
		super(newOrientation);
		// TODO Auto-generated constructor stub
	}





	@Override
	public void dispose() throws ComponentException
	{
		Component components[] = getComponents();
		for (Component component : components) {
			if (component instanceof Disposable) {
				Disposable disposableComponent = (Disposable) component;
				try {
					disposableComponent.dispose();
				} catch (ComponentException ex) {
					log.warn("Failed to dispose of component", ex);
					ex.printStackTrace();
				}
			}
		}
		
	}

}
