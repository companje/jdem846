package us.wthr.jdem846.ui.options;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;

@SuppressWarnings("serial")
public class DateControl extends JDateChooser implements OptionModelUIControl
{
	private static Log log = Logging.getLog(DateControl.class);
	
	private OptionModelPropertyContainer propertyContainer;
	
	public DateControl(OptionModelPropertyContainer propertyContainer)
	{
		super(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.datePattern"),
											I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.maskPattern"),
											I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.placeHolder").charAt(0));
		this.propertyContainer = propertyContainer;
		this.setToolTipText(propertyContainer.getTooltip());
		
		if (getDateEditor() instanceof JTextFieldDateEditor) {
			JTextFieldDateEditor dateEditor = (JTextFieldDateEditor) getDateEditor();
			dateEditor.setHorizontalAlignment(JTextField.RIGHT);
		}
		
		getJCalendar().setTodayButtonText(I18N.get("us.wthr.jdem846.ui.lightingOptionsPanel.dateChooser.today"));
		getJCalendar().setTodayButtonVisible(true);
		
		refreshUI();
		
		addPropertyChangeListener("date", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				onDateChanged();
			}
			
		});
	}
	
	
	public void refreshUI()
	{
		try {
			LightingDate lightingDate = (LightingDate) propertyContainer.getValue();
			if (lightingDate != null) {
				setDate(new Date(lightingDate.getDate() - getCurrentOffset()));
			} else {
				setDate(new Date(0));
			}
		} catch (Exception ex) {
			// TODO: Display error dialog
			log.error("Error setting initial value for property " + propertyContainer.getPropertyName(), ex);
		}
	}
	
	public void onDateChanged()
	{
		
		if (propertyContainer == null) {
			log.warn("Property container is null. Cannot set value.");
			return;
		}
		

		Date date = getDate();
		if (date == null) {
			log.warn("Date object is null, cannot set property of " + propertyContainer.getPropertyName());
			return;
		}
		
		long lightingOnDate = date.getTime() + getCurrentOffset() ;
		
		lightingOnDate = lightingOnDate - (lightingOnDate % 86400000);
		
		LightingDate lightingDate = new LightingDate(lightingOnDate);
		
		
		try {
			propertyContainer.setValue(lightingDate);
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting date value for property " + propertyContainer.getPropertyName(), ex);
		}
	}
	
	protected long getCurrentOffset()
	{
		Calendar cal = Calendar.getInstance();
		long currentOffset = cal.get(Calendar.ZONE_OFFSET);
		return currentOffset;
	}
	
}
