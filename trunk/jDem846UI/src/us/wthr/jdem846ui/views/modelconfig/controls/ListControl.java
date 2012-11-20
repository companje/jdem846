package us.wthr.jdem846ui.views.modelconfig.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.model.OptionModelPropertyContainer;
import us.wthr.jdem846.model.exceptions.MethodContainerInvokeException;
import us.wthr.jdem846ui.controls.LabeledCombo;

public class ListControl {
	private static Log log = Logging.getLog(ListControl.class);
	
	public static LabeledCombo create(Composite parent, final OptionModelPropertyContainer property,  String labelText)
	{
		LabeledCombo control = LabeledCombo.create(parent, labelText, SWT.READ_ONLY);
		
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
		
		control.getControl().addListener(SWT.Selection, new ListControl.ComboSelectionListener(property, listModel));

		return control;
	}
	
	
	
	public static class ComboSelectionListener implements Listener
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
	
	
}
