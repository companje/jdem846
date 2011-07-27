package us.wthr.jdem846.swtui;

import java.util.LinkedList;
import java.util.List;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;


public class ComboModelController<E>
{
	private static Log log = Logging.getLog(ComboModelController.class);
	
	private List<ComboItem<E>> itemList = new LinkedList<ComboItem<E>>();
	private ComboItem<E> selectedItem = null;

	private Combo combo;
	private List<Listener> selectionListeners = new LinkedList<Listener>();
	
	public ComboModelController(Combo _combo)
	{
		this.combo = _combo;
		this.combo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event)
			{
				onSelectionChanged();
			}
		});
	}
	
	protected void addItem(String label, E value)
	{
		itemList.add(new ComboItem<E>(label, value));
		combo.add(label, combo.getItemCount());
	}
	
	protected void onSelectionChanged()
	{
		selectedItem = getComboItemByLabel(combo.getText());
		fireSelectionListeners();
	}
	
	public void setSelected(int index)
	{
		combo.setText(combo.getItem(index));
		onSelectionChanged();
	}
	
	public void setSelectionByValue(E value)
	{
		ComboItem<E> item = getComboItemByValue(value);
		setSelectionByLabel(item.getLabel());
	}
	
	public void setSelectionByLabel(String label)
	{
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (combo.getItem(i).equals(label)) {
				combo.setText(label);
				onSelectionChanged();
				break;
			}
		}
	}
	
	public String getSelectedLabel()
	{
		if (selectedItem != null) {
			return selectedItem.getLabel();
		} else {
			return null;
		}
	}
	
	public E getSelectedValue()
	{
		if (selectedItem != null) {
			return selectedItem.getValue();
		} else {
			return null;
		}
	}
	
	protected ComboItem<E> getComboItemByValue(E value)
	{
		for (ComboItem<E> comboItem : itemList) {
			if (comboItem.getValue().equals(value))
				return comboItem;
		}
		return null;
	}
	
	protected ComboItem<E> getComboItemByLabel(String value)
	{
		for (ComboItem<E> comboItem : itemList) {
			if (comboItem.getLabel().equals(value))
				return comboItem;
		}
		return null;
	}
	
	public void fireSelectionListeners()
	{
		Event event = new Event();
		event.data = this.selectedItem;
		for (Listener listener : selectionListeners) {
			listener.handleEvent(event);
		}
	}
	
	public void addSelectionListener(Listener listener)
	{
		selectionListeners.add(listener);
	}
	
	public boolean removeSelectionListener(Listener listener)
	{
		return selectionListeners.remove(listener);
	}
}
