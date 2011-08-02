package us.wthr.jdem846.ui;

import java.awt.Component;

import javax.swing.JPanel;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class BasePanel extends JPanel implements Disposable
{
	
	private static Log log = Logging.getLog(BasePanel.class);
	
	
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
