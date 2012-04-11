package us.wthr.jdem846.ui.options;

import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.ColorSelection;

@SuppressWarnings("serial")
public class ColorSelectControl extends ColorSelection implements ChangeListener
{
	private static Log log = Logging.getLog(ColorSelectControl.class);
	
	private final OptionModelPropertyContainer propertyContainer;
	
	
	public ColorSelectControl(OptionModelPropertyContainer propertyContainer)
	{
		super();
		
		this.propertyContainer = propertyContainer;
		
		this.addChangeListener(this);
		
		try {
			if (propertyContainer.getValue() != null) {
				RgbaColor color = (RgbaColor) propertyContainer.getValue();
				this.setValue(color.toAwtColor());
			}
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting initial value to property " + propertyContainer.getPropertyName());
		}
		
	}


	@Override
	public void stateChanged(ChangeEvent e)
	{
		Color c = this.getValue();
		RgbaColor color = new RgbaColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		
		try {
			propertyContainer.setValue(color);
		} catch (MethodContainerInvokeException ec) {
			log.error("Error setting value of property " + propertyContainer.getPropertyName());
		}
	}
	
}
