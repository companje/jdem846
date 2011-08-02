package us.wthr.jdem846.ui;

import java.awt.Component;

import javax.swing.Box;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class DisposableBox extends Box implements Disposable
{
	private static Log log = Logging.getLog(DisposableBox.class);
	
	
	public DisposableBox(int axis)
	{
		super(axis);
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
