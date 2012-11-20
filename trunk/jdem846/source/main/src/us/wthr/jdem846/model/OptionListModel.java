package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;


public class OptionListModel<E>
{
	private List<OptionListModelItem<E>> itemList = new LinkedList<OptionListModelItem<E>>();
	private OptionListModelItem<E> selectedItem = null;
	private List<ModelDataListener> listDataListeners = new LinkedList<ModelDataListener>();
	
	
	
	public OptionListModel()
	{
		
	}
	
	public void addItem(OptionListModelItem<E> item)
	{
		itemList.add(item);
	}
	
	public void addItem(String label, E value)
	{
		itemList.add(new OptionListModelItem<E>(label, value));
	}
	
	
	
	public Object getSelectedItem() 
	{
		return selectedItem;
	}

	public void setSelectedItem(Object anItem) 
	{
		selectedItem = (OptionListModelItem<E>) anItem;
	}

	public void setSelectedItemByValue(E value)
	{
		for (OptionListModelItem<E> item : itemList) {
			if (item.getValue().equals(value)) {
				setSelectedItem(item);
				break;
			}
		}
	}
	
	public E getSelectedItemValue()
	{
		if (selectedItem != null) {
			return selectedItem.getValue();
		} else {
			return null;
		}
	}
	
	public int getSelectionIndex()
	{
		if (selectedItem != null) {
			return getIndexOfValue(selectedItem.getValue());
		} else {
			return 0;
		}
	}
	
	public int getIndexOfLabel(String label)
	{
		int i = 0;
		for (OptionListModelItem<E> item : itemList) {
			if (item.getLabel().equals(label)) {
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public int getIndexOfValue(E value)
	{
		int i = 0;
		for (OptionListModelItem<E> item : itemList) {
			if (item.getValue().equals(value)) {
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public Object getElementAt(int index) 
	{	
		return itemList.get(index);
	}

	public E getValueAt(int index)
	{
		return itemList.get(index).getValue();
	}
	
	public int getSize()
	{
		return itemList.size();
	}

	
	
	public void addListDataListener(ModelDataListener l) 
	{
		listDataListeners.add(l);
	}
	
	
	public void removeListDataListener(ModelDataListener l) 
	{
		listDataListeners.remove(l);
		
	}
}
