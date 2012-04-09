package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;


public class OptionListModel<E>
{
	private List<OptionModelItem<E>> itemList = new LinkedList<OptionModelItem<E>>();
	private OptionModelItem<E> selectedItem = null;
	private List<ModelDataListener> listDataListeners = new LinkedList<ModelDataListener>();
	
	
	
	public OptionListModel()
	{
		
	}
	
	public void addItem(OptionModelItem<E> item)
	{
		itemList.add(item);
	}
	
	public void addItem(String label, E value)
	{
		itemList.add(new OptionModelItem<E>(label, value));
	}
	
	
	
	public Object getSelectedItem() 
	{
		return selectedItem;
	}

	public void setSelectedItem(Object anItem) 
	{
		selectedItem = (OptionModelItem<E>) anItem;
	}

	public void setSelectedItemByValue(E value)
	{
		for (OptionModelItem<E> item : itemList) {
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
	
	
	

	
	public Object getElementAt(int index) 
	{	
		return itemList.get(index);
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
