package us.wthr.jdem846.ui.options;

import java.util.LinkedList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import us.wthr.jdem846.model.ModelDataListener;
import us.wthr.jdem846.model.OptionListModel;
import us.wthr.jdem846.model.OptionListModelItem;

public class ComboBoxListModel<E> implements ComboBoxModel<Object>
{
	private List<ListDataListener> listDataListeners = new LinkedList<ListDataListener>();
	private OptionListModel<E> optionListModel;
	
	public ComboBoxListModel(OptionListModel<E> optionListModel)
	{
		this.optionListModel = optionListModel;
		
		optionListModel.addListDataListener(new ModelDataListener() {
			public void contentsChanged()
			{
				fireListDataListeners();
			}
		});
	}

	@Override
	public int getSize()
	{
		return optionListModel.getSize();
	}

	@Override
	public Object getElementAt(int index)
	{
		return optionListModel.getElementAt(index);
	}

	protected void fireListDataListeners()
	{
		// Kind of an incorrect hack...
		ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0);
		
		
		for (ListDataListener l : this.listDataListeners) {
			l.contentsChanged(e);
		}
	}
	
	@Override
	public void addListDataListener(ListDataListener l)
	{
		listDataListeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l)
	{
		listDataListeners.remove(l);
	}

	@Override
	public void setSelectedItem(Object anItem)
	{
		optionListModel.setSelectedItem(anItem);
	}

	@Override
	public Object getSelectedItem()
	{
		return optionListModel.getSelectedItem();
	}
	
	public void setSelectedItemByValue(E value)
	{
		optionListModel.setSelectedItemByValue(value);
	}
	
	public E getSelectedItemValue()
	{
		return optionListModel.getSelectedItemValue();
	}
	
}
