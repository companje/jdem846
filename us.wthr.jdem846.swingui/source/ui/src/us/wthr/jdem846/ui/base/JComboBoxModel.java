/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui.base;

import java.util.LinkedList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;


public class JComboBoxModel<E> implements ComboBoxModel
{
	
	private List<ComboBoxItem<E>> itemList = new LinkedList<ComboBoxItem<E>>();
	private ComboBoxItem<E> selectedItem = null;
	private List<ListDataListener> listDataListeners = new LinkedList<ListDataListener>();
	
	public JComboBoxModel()
	{
		
	}
	
	public void addItem(ComboBoxItem<E> item)
	{
		itemList.add(item);
	}
	
	public void addItem(String label, E value)
	{
		itemList.add(new ComboBoxItem<E>(label, value));
	}
	
	
	
	public ComboBoxItem<E> getSelectedItem() 
	{
		return selectedItem;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem(Object anItem) 
	{
		selectedItem = (ComboBoxItem<E>) anItem;
	}

	public void setSelectedItemByValue(E value)
	{
		for (ComboBoxItem<E> item : itemList) {
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
	
	
	@Override
	public void addListDataListener(ListDataListener l) 
	{
		listDataListeners.add(l);
	}

	@Override
	public ComboBoxItem<E> getElementAt(int index) 
	{	
		return itemList.get(index);
	}

	@Override
	public int getSize()
	{
		return itemList.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) 
	{
		listDataListeners.remove(l);
		
	}
}
