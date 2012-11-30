package us.wthr.jdem846ui.views.modelconfig;

import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.PropertyValidationResult;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.annotations.ValueBounds;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846ui.controls.LabeledCheck;
import us.wthr.jdem846ui.controls.LabeledColor;
import us.wthr.jdem846ui.controls.LabeledCombo;
import us.wthr.jdem846ui.controls.LabeledControl;
import us.wthr.jdem846ui.controls.LabeledDate;
import us.wthr.jdem846ui.controls.LabeledSpinner;
import us.wthr.jdem846ui.controls.LabeledTime;
import us.wthr.jdem846ui.observers.OptionValidationChangeObserver;
import us.wthr.jdem846ui.observers.OptionValidationResultsListener;

public class ModelOptionControlsFactory 
{
	private static Log log = Logging.getLog(ModelOptionControlsFactory.class);
	
	
	public static LabeledControl<?> createControl(Class<?> clazz, Composite parent, final OptionModelPropertyContainer propertyContainer)
	{
		
		if (!propertyContainer.getListModelClass().equals(Object.class)) {
			return ModelOptionControlsFactory.createListControl(parent, propertyContainer);
		} else if (propertyContainer.getType().equals(boolean.class)) {
			return ModelOptionControlsFactory.createBooleanControl(parent, propertyContainer);
		} else if (propertyContainer.getType().equals(RgbaColor.class)) {
			return ModelOptionControlsFactory.createColorControl(parent, propertyContainer);
		} else if (propertyContainer.getType().equals(AzimuthElevationAngles.class)) {
			//addAzimuthElevationAnglesPropertyControl(propertyContainer);
			return null;
		} else if (propertyContainer.getType().equals(ViewPerspective.class)) {
			//addViewPerspectivePropertyControl(propertyContainer);
			return null;
		} else if (propertyContainer.getType().equals(double.class)) {
			return ModelOptionControlsFactory.createDoubleControl(parent, propertyContainer);
		} else if (propertyContainer.getType().equals(int.class)) {
			return ModelOptionControlsFactory.createIntegerControl(parent, propertyContainer);
		} else if (propertyContainer.getType().equals(LightingDate.class)) {
			return ModelOptionControlsFactory.createDateControl(parent, propertyContainer);
		} else if (propertyContainer.getType().equals(LightingTime.class)) {
			return ModelOptionControlsFactory.createTimeControl(parent, propertyContainer);
		} else {
			return null;
		}
		
	}
	
	
	public static LabeledColor createColorControl(Composite parent, final OptionModelPropertyContainer property)
	{
		final LabeledColor labeledColor = LabeledColor.create(parent, property.getLabel(), SWT.NONE);
		
		labeledColor.getControl().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				RgbaColor rgbaColor = new RgbaColor(labeledColor.getControl().getColor());
				
				try {
					property.setValue(rgbaColor);
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		try {
			RgbaColor rgbaColor = (RgbaColor) property.getValue();
			if (rgbaColor != null) {
				labeledColor.getControl().setColor(rgbaColor.getRgba());
			}
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener() {
			public void onOptionValidationResults(List<PropertyValidationResult> results) {
				try {
					RgbaColor rgbaColor = (RgbaColor) property.getValue();
					if (rgbaColor != null) {
						labeledColor.getControl().setColor(rgbaColor.getRgba());
					}
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		return labeledColor;
	}
	
	
	public static LabeledCheck createBooleanControl(Composite parent, final OptionModelPropertyContainer property)
	{
		final LabeledCheck check = LabeledCheck.create(parent, property.getLabel());
		check.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Button check = (Button) event.widget;
				try {
					property.setValue(check.getSelection());
					log.info("Setting property " + property.getPropertyName() + " to " + check.getSelection());
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + check.getSelection(), ex);
				}
			}
		});
		
		
		try {
			check.getControl().setSelection((Boolean)property.getValue());
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener() {
			public void onOptionValidationResults(List<PropertyValidationResult> results) {
				try {
					check.getControl().setSelection((Boolean)property.getValue());
				} catch (MethodContainerInvokeException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		return check;
	}
	
	
	
	
	
	
	public static LabeledDate createDateControl(Composite parent, final OptionModelPropertyContainer property)
	{
		
		final LabeledDate labeledDate = LabeledDate.create(parent, property.getLabel(), SWT.NONE);
		
		labeledDate.getControl().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e) {
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				
				cal.set(Calendar.YEAR, labeledDate.getControl().getYear());
				cal.set(Calendar.MONTH, labeledDate.getControl().getMonth());
				cal.set(Calendar.DAY_OF_MONTH, labeledDate.getControl().getDay());
				
				try {
					property.setValue(new LightingDate(cal.getTimeInMillis()));
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		try {
			ModelOptionControlsFactory.setControlValue(labeledDate, (LightingDate) property.getValue());
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener() {
			public void onOptionValidationResults(List<PropertyValidationResult> results) {
				try {
					ModelOptionControlsFactory.setControlValue(labeledDate, (LightingDate) property.getValue());
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		return labeledDate;
	}
	
	
	
	private static void setControlValue(LabeledDate labeledDate, LightingDate lightingDate)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(lightingDate.getDate());

		labeledDate.getControl().setYear(cal.get(Calendar.YEAR));
		labeledDate.getControl().setMonth(cal.get(Calendar.MONTH));
		labeledDate.getControl().setDay(cal.get(Calendar.DAY_OF_MONTH));
	}
	
	
	
	public static LabeledSpinner createDoubleControl(Composite parent, final OptionModelPropertyContainer property)
	{
		ValueBounds bounds = property.getValueBounds();
		
		
		LabeledSpinner control = null;
		if (bounds != null) {
			control = LabeledSpinner.create(parent, property.getLabel(), (int) bounds.minimum() * 100, (int) bounds.maximum() * 100, 2, (int) bounds.stepSize() * 100);
		} else {
			control =  LabeledSpinner.create(parent, property.getLabel());
		}
		
		final Spinner _spinner = control.getControl();
		
		control.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Spinner spinner = (Spinner) e.widget;
				double value = (double)spinner.getSelection() / 100.0;
				try {
					property.setValue(value);
					log.info("Setting property " + property.getPropertyName() + " to " + value);
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + value, ex);
				}
			}
		});
		
		
		try {
			control.getControl().setSelection((int) ((Double)property.getValue() * 100));
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener() {
			public void onOptionValidationResults(List<PropertyValidationResult> results) {
				try {
					_spinner.setSelection((int) ((Double)property.getValue() * 100));
				} catch (MethodContainerInvokeException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		return control;
	}
	
	
	
	
	
	public static LabeledSpinner createIntegerControl(Composite parent, final OptionModelPropertyContainer property)
	{
		ValueBounds bounds = property.getValueBounds();
		LabeledSpinner control = LabeledSpinner.create(parent, property.getLabel(), (int) bounds.minimum(), (int) bounds.maximum(), 0, (int) bounds.stepSize());
		
		final Spinner _spinner = control.getControl();
		
		control.getControl().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Spinner spinner = (Spinner) e.widget;
				try {
					property.setValue(spinner.getSelection());
					log.info("Setting property " + property.getPropertyName() + " to " + spinner.getSelection());
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + spinner.getSelection(), ex);
				}
			}
		});
		
		
		try {
			control.getControl().setSelection((Integer)property.getValue());
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener() {
			public void onOptionValidationResults(List<PropertyValidationResult> results) {
				try {
					_spinner.setSelection((Integer)property.getValue());
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		return control;
	}
	
	
	
	
	
	
	
	
	public static LabeledCombo createListControl(Composite parent, final OptionModelPropertyContainer property)
	{
		LabeledCombo control = LabeledCombo.create(parent, property.getLabel(), SWT.READ_ONLY);
		
		OptionListModel listModel = null;
		
		try {
			listModel = (OptionListModel) property.getListModelClass().newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		if (listModel != null) {
			for (int i = 0; i < listModel.getSize(); i++) {
				control.getControl().add(listModel.getElementAt(i).toString(), i);
			}
			
			
			try {
				control.getControl().select(listModel.getIndexOfValue(property.getValue()));
			} catch (MethodContainerInvokeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		control.getControl().addListener(SWT.Selection, new ModelOptionControlsFactory.ComboSelectionListener(property, listModel));

		return control;
	}
	
	
	
	
	
	
	
	
	
	private static class ComboSelectionListener implements Listener
	{
		private OptionModelPropertyContainer property;
		private OptionListModel listModel;
		
		public ComboSelectionListener(OptionModelPropertyContainer property, OptionListModel listModel)
		{
			this.property = property;
			this.listModel = listModel;
		}
		
		@Override
		public void handleEvent(Event e) {
			Combo combo = (Combo) e.widget;
			
			Object value = listModel.getValueAt(listModel.getIndexOfLabel(combo.getText()));
			
			try {
				property.setValue(value);
				log.info("Setting property " + property.getPropertyName() + " to " + value.toString());
			} catch (MethodContainerInvokeException ex) {
				log.error("Error setting property " + property.getPropertyName() + " to " + value.toString(), ex);
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	public static LabeledTime createTimeControl(Composite parent, final OptionModelPropertyContainer property)
	{
		
		final LabeledTime labeledTime = LabeledTime.create(parent, property.getLabel(), SWT.NONE);

		
		
		labeledTime.getControl().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e) {
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, labeledTime.getControl().getHours());
				cal.set(Calendar.MINUTE, labeledTime.getControl().getMinutes());
				cal.set(Calendar.SECOND, labeledTime.getControl().getSeconds());
				cal.set(Calendar.MILLISECOND, 0);

				
				cal.set(Calendar.YEAR, 0);
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.DAY_OF_MONTH, 0);
				
				try {
					property.setValue(new LightingTime(cal.getTimeInMillis()));
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		try {
			ModelOptionControlsFactory.setTimeControlValue(labeledTime, (LightingTime) property.getValue());
		} catch (MethodContainerInvokeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener() {
			public void onOptionValidationResults(List<PropertyValidationResult> results) {
				try {
					ModelOptionControlsFactory.setTimeControlValue(labeledTime, (LightingTime) property.getValue());
				} catch (MethodContainerInvokeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		return labeledTime;
	}
	
	
	private static void setTimeControlValue(LabeledTime labeledTime, LightingTime lightingTime)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(lightingTime.getTime());

		labeledTime.getControl().setHours(cal.get(Calendar.HOUR_OF_DAY));
		labeledTime.getControl().setMinutes(cal.get(Calendar.MINUTE));
		labeledTime.getControl().setSeconds(cal.get(Calendar.SECOND));
	}
	
}
