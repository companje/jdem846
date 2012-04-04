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

package us.wthr.jdem846.ui;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.Slider;

@SuppressWarnings("serial")
public class MonitoredSlider extends Panel
{
	
	private Label lblValue;
	private Slider sldSlider;
	private MonitoredValueListener valueListener;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public MonitoredSlider(int min, int max, int value, MonitoredValueListener listener)
	{
		this.valueListener = listener;
		
		// Create components
		lblValue = new Label("");
		sldSlider = new Slider(min, max, value);
		
		lblValue.setPreferredSize(new Dimension(40, 10));
		
		// Add listeners
		sldSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				if (valueListener != null) {
					String stringValue = valueListener.getValueString();
					lblValue.setText("  " + stringValue);
				} else {
					lblValue.setText("");
				}
				
				if (!sldSlider.getValueIsAdjusting()) {
					fireChangeListeners(e);
				}
				
			}
		});
		
		
		// Set layout
		
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		Box box = Box.createHorizontalBox();
		box.add(sldSlider);
		box.add(lblValue);
		this.add(box);

	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		sldSlider.setEnabled(enabled);
		lblValue.setEnabled(enabled);
	}
	
	public void setValue(int value)
	{
		sldSlider.setValue(value);
		if (valueListener != null)
			lblValue.setText("  " + valueListener.getValueString());
		else
			lblValue.setText("");
	}
	
	public int getValue()
	{
		return sldSlider.getValue();
	}
	
	public void setSnapToTicks(boolean snap)
	{
		sldSlider.setSnapToTicks(snap);
		
	}
	
	public boolean getSnapToTicks()
	{
		return sldSlider.getSnapToTicks();
	}
	
	public MonitoredValueListener getValueListener()
	{
		return valueListener;
	}



	public void setValueListener(MonitoredValueListener valueListener)
	{
		this.valueListener = valueListener;
	}

	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}

	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}

	protected void fireChangeListeners(ChangeEvent e)
	{
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(e);
		}
	}
	
	public interface MonitoredValueListener
	{
		public String getValueString();
	}
	
}
