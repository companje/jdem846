package us.wthr.jdem846.ui.options;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.perspective.PerspectiveValueControl;

@SuppressWarnings("serial")
public class ViewPerspectiveControl extends PerspectiveValueControl implements ChangeListener
{
	private static Log log = Logging.getLog(ViewPerspectiveControl.class);
	
	
	private final OptionModelPropertyContainer propertyContainer;
	
	public ViewPerspectiveControl(OptionModelPropertyContainer propertyContainer)
	{
		this.propertyContainer = propertyContainer;
		
		
		try {
			ViewPerspective initialValue = (ViewPerspective) propertyContainer.getValue();
			if (initialValue != null) {
				this.setRotateX(initialValue.getRotateX());
				this.setRotateY(initialValue.getRotateY());
				//this.setRotateZ(initialValue.getRotateZ());
			}
		
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting initial value for property " + propertyContainer.getPropertyName());
		}
		
		this.addChangeListener(this);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		
		ViewPerspective viewPerspective = null;
		
		try {
			viewPerspective = (ViewPerspective) propertyContainer.getValue();
		} catch (MethodContainerInvokeException ex) {
			log.error("Error retrieving value for property " + propertyContainer.getPropertyName(), ex);
		}
		
		if (viewPerspective == null) {
			viewPerspective = new ViewPerspective();
		}
		
		viewPerspective.setRotateX(getRotateX());
		viewPerspective.setRotateY(getRotateY());
		
		try {
			propertyContainer.setValue(viewPerspective);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting value for property " + propertyContainer.getPropertyName(), ex);
		}
		
	}
	
	
}
