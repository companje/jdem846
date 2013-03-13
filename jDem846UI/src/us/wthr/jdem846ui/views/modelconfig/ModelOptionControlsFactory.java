package us.wthr.jdem846ui.views.modelconfig;

import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import us.wthr.jdem846.model.FilePath;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.model.OptionModel;
import us.wthr.jdem846.model.OptionModelChangeEvent;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.PropertyValidationResult;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.annotations.ValueBounds;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846.project.context.ProjectContext;
import us.wthr.jdem846ui.controls.LabeledCheck;
import us.wthr.jdem846ui.controls.LabeledColor;
import us.wthr.jdem846ui.controls.LabeledCombo;
import us.wthr.jdem846ui.controls.LabeledControl;
import us.wthr.jdem846ui.controls.LabeledDate;
import us.wthr.jdem846ui.controls.LabeledFilePicker;
import us.wthr.jdem846ui.controls.LabeledSpinner;
import us.wthr.jdem846ui.controls.LabeledTime;
import us.wthr.jdem846ui.observers.OptionValidationChangeObserver;
import us.wthr.jdem846ui.observers.OptionValidationResultsListener;

// TODO: Do something with all the exceptions...

public class ModelOptionControlsFactory
{
	private static Log log = Logging.getLog(ModelOptionControlsFactory.class);

	public static LabeledControl<?> createControl(Class<?> clazz, Composite parent, OptionModel optionModel, OptionModelPropertyContainer propertyContainer)
	{

		if (!propertyContainer.getListModelClass().equals(Object.class)) {
			return ModelOptionControlsFactory.createListControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(boolean.class)) {
			return ModelOptionControlsFactory.createBooleanControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(RgbaColor.class)) {
			return ModelOptionControlsFactory.createColorControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(AzimuthElevationAngles.class)) {
			// addAzimuthElevationAnglesPropertyControl(propertyContainer);
			return null;
		} else if (propertyContainer.getType().equals(ViewPerspective.class)) {
			// addViewPerspectivePropertyControl(propertyContainer);
			return null;
		} else if (propertyContainer.getType().equals(double.class)) {
			return ModelOptionControlsFactory.createDoubleControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(int.class)) {
			return ModelOptionControlsFactory.createIntegerControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(LightingDate.class)) {
			return ModelOptionControlsFactory.createDateControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(LightingTime.class)) {
			return ModelOptionControlsFactory.createTimeControl(parent, optionModel, propertyContainer);
		} else if (propertyContainer.getType().equals(FilePath.class)) {
			return ModelOptionControlsFactory.createFilePickerControl(parent, optionModel, propertyContainer);
		} else {
			return null;
		}

	}
	
	public static LabeledFilePicker createFilePickerControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{
		final LabeledFilePicker labeledPicker = LabeledFilePicker.create(parent, property.getLabel(), SWT.NONE);
		
		labeledPicker.getControl().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e)
			{
				log.info("File location modified");
				try {
					property.setValue(labeledPicker.getControl().getFilePath());
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting file path property: " + ex.getMessage(), ex);
				}
			}
			
		});
		

		try {
			FilePath filePath = (FilePath) property.getValue();
			if (filePath != null && !labeledPicker.getControl().isDisposed()) {
				labeledPicker.getControl().setFilePath(filePath.getPath());
			}
		} catch (MethodContainerInvokeException ex) {
			log.error("Error setting field value from property: " + ex.getMessage(), ex);
		}
		
		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			labeledPicker.getControl().setEnabled(enabled);
		} catch (MethodContainerInvokeException e) {
			e.printStackTrace();
		}
		
		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				if (originatingEvent != null && !originatingEvent.getPropertyId().equals(property.getId())) {
					try {
						FilePath filePath = (FilePath) property.getValue();
						if (filePath != null && !labeledPicker.getControl().isDisposed()) {
							labeledPicker.getControl().setFilePath(filePath.getPath());
						}
					} catch (MethodContainerInvokeException ex) {
						log.error("Error setting field value from property: " + ex.getMessage(), ex);
					}
				}
				
				try {
					boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
					if (!labeledPicker.getControl().isDisposed()) {
						labeledPicker.getControl().setEnabled(enabled);
					}
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting field enable state: " + ex.getMessage(), ex);
				}

			}
		});
		
		return labeledPicker;
	}

	
	/*
	 * OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				if (originatingEvent != null && !originatingEvent.getPropertyId().equals(property.getId())) {
					try {
						if (!_spinner.isDisposed()) {
							_spinner.setSelection((int) ((Double) property.getValue() * 100));
						}
					} catch (MethodContainerInvokeException e1) {
						e1.printStackTrace();
					}

					try {
						boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
						if (!_spinner.isDisposed()) {
							_spinner.setEnabled(enabled);
						}
					} catch (MethodContainerInvokeException e) {
						e.printStackTrace();
					}
				}

			}
		});
	 */
	
	public static LabeledColor createColorControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{
		final LabeledColor labeledColor = LabeledColor.create(parent, property.getLabel(), SWT.NONE);

		labeledColor.getControl().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				RgbaColor rgbaColor = new RgbaColor(labeledColor.getControl().getColor());

				try {
					property.setValue(rgbaColor);
				} catch (MethodContainerInvokeException e1) {
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
			e1.printStackTrace();
		}

		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			labeledColor.getControl().setEnabled(enabled);
		} catch (MethodContainerInvokeException e) {
			e.printStackTrace();
		}

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				try {
					RgbaColor rgbaColor = (RgbaColor) property.getValue();
					if (rgbaColor != null && !labeledColor.getControl().isDisposed()) {
						labeledColor.getControl().setColor(rgbaColor.getRgba());
					}
				} catch (MethodContainerInvokeException e1) {
					e1.printStackTrace();
				}

				try {
					boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
					if (!labeledColor.getControl().isDisposed()) {
						labeledColor.getControl().setEnabled(enabled);
					}
				} catch (MethodContainerInvokeException e) {
					e.printStackTrace();
				}

			}
		});

		return labeledColor;
	}

	public static LabeledCheck createBooleanControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{
		final LabeledCheck check = LabeledCheck.create(parent, property.getLabel());
		check.getControl().addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
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
			check.getControl().setSelection((Boolean) property.getValue());
		} catch (MethodContainerInvokeException e1) {
			e1.printStackTrace();
		}

		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			check.getControl().setEnabled(enabled);
		} catch (MethodContainerInvokeException e) {
			e.printStackTrace();
		}

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				try {
					if (!check.getControl().isDisposed()) {
						check.getControl().setSelection((Boolean) property.getValue());
					}
				} catch (MethodContainerInvokeException e1) {
					e1.printStackTrace();
				}

				try {
					boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
					if (!check.getControl().isDisposed()) {
						check.getControl().setEnabled(enabled);
					}
				} catch (MethodContainerInvokeException e) {
					e.printStackTrace();
				}

			}
		});

		return check;
	}

	public static LabeledDate createDateControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{

		final LabeledDate labeledDate = LabeledDate.create(parent, property.getLabel(), SWT.NONE);

		labeledDate.getControl().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

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
					e1.printStackTrace();
				}

			}
		});

		try {
			ModelOptionControlsFactory.setControlValue(labeledDate, (LightingDate) property.getValue());
		} catch (MethodContainerInvokeException e1) {
			e1.printStackTrace();
		}

		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			labeledDate.getControl().setEnabled(enabled);
		} catch (MethodContainerInvokeException e) {
			e.printStackTrace();
		}

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				try {
					ModelOptionControlsFactory.setControlValue(labeledDate, (LightingDate) property.getValue());
				} catch (MethodContainerInvokeException e1) {
					e1.printStackTrace();
				}

				try {
					boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
					if (!labeledDate.getControl().isDisposed()) {
						labeledDate.getControl().setEnabled(enabled);
					}
				} catch (MethodContainerInvokeException e) {
					e.printStackTrace();
				}
			}
		});

		return labeledDate;
	}

	private static void setControlValue(LabeledDate labeledDate, LightingDate lightingDate)
	{
		if (!labeledDate.getControl().isDisposed()) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(lightingDate.getDate());

			labeledDate.getControl().setYear(cal.get(Calendar.YEAR));
			labeledDate.getControl().setMonth(cal.get(Calendar.MONTH));
			labeledDate.getControl().setDay(cal.get(Calendar.DAY_OF_MONTH));
		}
	}

	public static LabeledSpinner createDoubleControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{
		ValueBounds bounds = property.getValueBounds();

		LabeledSpinner control = null;
		if (bounds != null) {
			int min = (int) (bounds.minimum() * 100.0);
			int max = (int) (bounds.maximum() * 100.0);
			int step = (int) (bounds.stepSize() * 100.0);
			control = LabeledSpinner.create(parent, property.getLabel(), min, max, 2, step);
		} else {
			control = LabeledSpinner.create(parent, property.getLabel());
		}

		final Spinner _spinner = control.getControl();

		control.getControl().addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
				Spinner spinner = (Spinner) e.widget;
				double value = (double) spinner.getSelection() / 100.0;
				try {
					property.setValue(value);
					log.info("Setting property " + property.getPropertyName() + " to " + value);
				} catch (MethodContainerInvokeException ex) {
					log.error("Error setting property " + property.getPropertyName() + " to " + value, ex);
				}
			}
		});

		try {
			
			double value = ((Double) property.getValue() * 100.0);
			int iValue = (int) value;
			control.getControl().setSelection(iValue);
		} catch (MethodContainerInvokeException e1) {
			e1.printStackTrace();
		}

		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			if (!_spinner.isDisposed()) {
				_spinner.setEnabled(enabled);
			}
		} catch (MethodContainerInvokeException e) {
			e.printStackTrace();
		}

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				if (originatingEvent != null && !originatingEvent.getPropertyId().equals(property.getId())) {
					try {
						if (!_spinner.isDisposed()) {
							_spinner.setSelection((int) ((Double) property.getValue() * 100));
						}
					} catch (MethodContainerInvokeException e1) {
						e1.printStackTrace();
					}

					try {
						boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
						if (!_spinner.isDisposed()) {
							_spinner.setEnabled(enabled);
						}
					} catch (MethodContainerInvokeException e) {
						e.printStackTrace();
					}
				}

			}
		});

		return control;
	}

	public static LabeledSpinner createIntegerControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{
		ValueBounds bounds = property.getValueBounds();
		LabeledSpinner control = LabeledSpinner.create(parent, property.getLabel(), (int) bounds.minimum(), (int) bounds.maximum(), 0, (int) bounds.stepSize());

		final Spinner _spinner = control.getControl();

		control.getControl().addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{
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
			if (!control.getControl().isDisposed()) {
				control.getControl().setSelection((Integer) property.getValue());
			}
		} catch (MethodContainerInvokeException e1) {
			e1.printStackTrace();
		}

		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			if (!_spinner.isDisposed()) {
				_spinner.setEnabled(enabled);
			}
		} catch (MethodContainerInvokeException e) {
			e.printStackTrace();
		}

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				if (originatingEvent != null && !originatingEvent.getPropertyId().equals(property.getId())) {
					try {
						if (!_spinner.isDisposed()) {
							_spinner.setSelection((Integer) property.getValue());
						}
					} catch (MethodContainerInvokeException e1) {

						e1.printStackTrace();
					}

					try {
						boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
						if (!_spinner.isDisposed()) {
							_spinner.setEnabled(enabled);
						}
					} catch (MethodContainerInvokeException e) {

						e.printStackTrace();
					}
				}
			}
		});

		return control;
	}

	public static LabeledCombo createListControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{
		final LabeledCombo control = LabeledCombo.create(parent, property.getLabel(), SWT.READ_ONLY);

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

				e1.printStackTrace();
			}
		}

		try {
			boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
			control.getControl().setEnabled(enabled);
		} catch (MethodContainerInvokeException e) {

			e.printStackTrace();
		}

		control.getControl().addListener(SWT.Selection, new ModelOptionControlsFactory.ComboSelectionListener(property, listModel));

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				try {
					if (!control.getControl().isDisposed()) {
						boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
						control.getControl().setEnabled(enabled);
					}
				} catch (MethodContainerInvokeException e) {

					e.printStackTrace();
				}
			}
		});

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
		public void handleEvent(Event e)
		{
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

	public static LabeledTime createTimeControl(Composite parent, final OptionModel optionModel, final OptionModelPropertyContainer property)
	{

		final LabeledTime labeledTime = LabeledTime.create(parent, property.getLabel(), SWT.NONE);

		labeledTime.getControl().addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

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

					e1.printStackTrace();
				}

			}
		});

		try {
			ModelOptionControlsFactory.setTimeControlValue(labeledTime, (LightingTime) property.getValue());
		} catch (MethodContainerInvokeException e1) {

			e1.printStackTrace();
		}

		OptionValidationChangeObserver.getInstance().addOptionValidationResultsListener(new OptionValidationResultsListener()
		{
			public void onOptionValidationResults(List<PropertyValidationResult> results, OptionModelChangeEvent originatingEvent)
			{
				try {
					ModelOptionControlsFactory.setTimeControlValue(labeledTime, (LightingTime) property.getValue());
				} catch (MethodContainerInvokeException e1) {
					e1.printStackTrace();
				}

				try {
					boolean enabled = property.isPropertyEnabled(ProjectContext.getInstance().getModelContext(), optionModel);
					if (!labeledTime.getControl().isDisposed()) {
						labeledTime.getControl().setEnabled(enabled);
					}
				} catch (MethodContainerInvokeException e) {
					e.printStackTrace();
				}
			}
		});

		return labeledTime;
	}

	private static void setTimeControlValue(LabeledTime labeledTime, LightingTime lightingTime)
	{
		if (!labeledTime.getControl().isDisposed()) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(lightingTime.getTime());

			labeledTime.getControl().setHours(cal.get(Calendar.HOUR_OF_DAY));
			labeledTime.getControl().setMinutes(cal.get(Calendar.MINUTE));
			labeledTime.getControl().setSeconds(cal.get(Calendar.SECOND));
		}
	}

}
