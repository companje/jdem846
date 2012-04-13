package us.wthr.jdem846.ui.options;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.ui.base.Spinner;

@SuppressWarnings("serial")
public class TimeControl extends Spinner implements ChangeListener
{
	private static Log log = Logging.getLog(TimeControl.class);
	
	private OptionModelPropertyContainer propertyContainer;
	
	private SpinnerDateModel lightOnTimeModel;
	
	public TimeControl(OptionModelPropertyContainer propertyContainer)
	{
		this.propertyContainer = propertyContainer;
		
		lightOnTimeModel = new SpinnerDateModel();
		this.setModel(lightOnTimeModel);
		
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(this, I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.timeSpinner.format"));
		setEditor(timeEditor);
		
		getModel().addChangeListener(this);
		
		try {
			LightingTime lightingTime = (LightingTime) propertyContainer.getValue();
			if (lightingTime != null) {
				Date date = new Date(lightingTime.getTime() - getCurrentOffset());
				lightOnTimeModel.setValue(date);
			} else {
				lightOnTimeModel.setValue(new Date(0));
			}
		
		} catch (Exception ex) {
			log.error("Error setting initial value to property " + propertyContainer.getPropertyName());
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		
		
		long lightingOnTime = lightOnTimeModel.getDate().getTime() + getCurrentOffset();
		LightingTime lightingTimeInstance = new LightingTime(lightingOnTime);
		
		log.info("Time control value changed: " + lightingOnTime);
		
		try {
			propertyContainer.setValue(lightingTimeInstance);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting value of property " + propertyContainer.getPropertyName(), ex);
		}
		
		
	}
	
	
	
	protected long getCurrentOffset()
	{
		Calendar cal = Calendar.getInstance();
		long currentOffset = cal.get(Calendar.ZONE_OFFSET);
		return currentOffset;
	}
	
	
}
