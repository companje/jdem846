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

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class OutputImageViewButtonBar extends JToolBar
{
	public static final int BTN_SAVE = 0;
	public static final int BTN_ZOOM_IN = 1;
	public static final int BTN_ZOOM_OUT = 2;
	public static final int BTN_ZOOM_ACTUAL = 3;
	public static final int BTN_ZOOM_FIT = 4;
	public static final int OPTION_QUALITY = 5;
	public static final int BTN_STOP = 6;
	
	private ToolbarButton jbtnSave;
	private ToolbarButton jbtnZoomIn;
	private ToolbarButton jbtnZoomOut;
	private ToolbarButton jbtnZoomActual;
	private ToolbarButton jbtnZoomFit;
	private ToolbarButton jbtnStop;
	
	private JComboBox jcmbQuality;
	private ImageQualityListModel qualityModel;
	

	private List<ButtonClickedListener> buttonClickedListeners = new LinkedList<ButtonClickedListener>();	
	private List<OptionChangeListener> optionChangeListeners = new LinkedList<OptionChangeListener>();
	
	public OutputImageViewButtonBar()
	{
		// Create components
		jbtnSave = new ToolbarButton("Save", "/us/wthr/jdem846/ui/icons/document-save.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_SAVE);
			}
		});
		jbtnZoomIn = new ToolbarButton("Zoom In", "/us/wthr/jdem846/ui/icons/zoom-in.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_IN);
			}
		});
		jbtnZoomOut = new ToolbarButton("Zoom Out", "/us/wthr/jdem846/ui/icons/zoom-out.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_OUT);
			}
		});
		jbtnZoomActual = new ToolbarButton("Full Size", "/us/wthr/jdem846/ui/icons/zoom-original.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_ACTUAL);
			}
		});
		jbtnZoomFit = new ToolbarButton("Best Fit", "/us/wthr/jdem846/ui/icons/zoom-fit-best.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_FIT);
			}
		});
		
		jbtnStop = new ToolbarButton("Stop", "/us/wthr/jdem846/ui/icons/process-stop.png", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_STOP);
			}
		});

		qualityModel = new ImageQualityListModel();
		jcmbQuality = new JComboBox(qualityModel);
		
		// Set Tooltips
		jbtnSave.setToolTipText("Save the image to disk");
		jbtnZoomIn.setToolTipText("Enlarge the image");
		jbtnZoomOut.setToolTipText("Shrink the image");
		jbtnZoomActual.setToolTipText("Show image at full size");
		jbtnZoomFit.setToolTipText("Fit the image to the window");
		jcmbQuality.setToolTipText("Image scaling quality (slow/normal/fast)");
		jbtnStop.setToolTipText("Stop the current render process");
		
		// Add Listeners
		jcmbQuality.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fireOptionChangeListeners(OPTION_QUALITY);
				}	
			}
		});
		
		
		this.setMargin(new Insets(3, 3, 3, 3));
		
		// Set layout
		add(jbtnSave);
		addSeparator();
		add(jbtnZoomIn);
		add(jbtnZoomOut);
		add(jbtnZoomActual);
		add(jbtnZoomFit);
		addSeparator();
		add(jbtnStop);
		addSeparator();
		add(new JLabel("Quality: "));
		add(jcmbQuality);
		
	}
	
	
	public void setComponentEnabled(int button, boolean enabled)
	{
		switch(button) {
		case BTN_SAVE:
			jbtnSave.setEnabled(enabled);
			break;
		case BTN_ZOOM_IN:
			jbtnZoomIn.setEnabled(enabled);
			break;
		case BTN_ZOOM_OUT:
			jbtnZoomOut.setEnabled(enabled);
			break;
		case BTN_ZOOM_ACTUAL:
			jbtnZoomActual.setEnabled(enabled);
			break;
		case BTN_ZOOM_FIT:
			jbtnZoomFit.setEnabled(enabled);
			break;
		case OPTION_QUALITY:
			jcmbQuality.setEnabled(enabled);
			break;
		case BTN_STOP:
			jbtnStop.setEnabled(enabled);
			break;
		}
	}
	
	public void addButtonClickedListener(ButtonClickedListener listener)
	{
		buttonClickedListeners.add(listener);
	}
	
	public void removeButtonClickedListener(ButtonClickedListener listener)
	{
		buttonClickedListeners.remove(listener);
	}
	
	protected void fireButtonClickedListeners(int button)
	{
		for (ButtonClickedListener listener : buttonClickedListeners) {
			switch(button) {
			case BTN_SAVE:
				listener.onSaveClicked();
				break;
			case BTN_ZOOM_IN:
				listener.onZoomInClicked();
				break;
			case BTN_ZOOM_OUT:
				listener.onZoomOutClicked();
				break;
			case BTN_ZOOM_ACTUAL:
				listener.onZoomActualClicked();
				break;
			case BTN_ZOOM_FIT:
				listener.onZoomFitClicked();
				break;
			case BTN_STOP:
				listener.onStopClicked();
				break;
			}
		}
	}
	
	public interface ButtonClickedListener
	{
		public void onSaveClicked();
		public void onZoomInClicked();
		public void onZoomOutClicked();
		public void onZoomActualClicked();
		public void onZoomFitClicked();
		public void onStopClicked();
	}
	
	
	public void addOptionChangeListener(OptionChangeListener listener)
	{
		optionChangeListeners.add(listener);
	}
	
	public void removeOptionChangeListener(OptionChangeListener listener)
	{
		optionChangeListeners.remove(listener);
	}
	
	public void fireOptionChangeListeners(int option)
	{
		for (OptionChangeListener listener : optionChangeListeners) {
			switch (option) {
			case OPTION_QUALITY:
				listener.onImageQualityChanged(qualityModel.getSelectedItemValue());
				break;
			}
		}
	}
	
	
	public interface OptionChangeListener
	{
		public void onImageQualityChanged(int quality);
	}
	
	
	class ImageQualityListModel extends JComboBoxModel<Integer>
	{
		
		public ImageQualityListModel()
		{
			addItem("High", Image.SCALE_SMOOTH);
			addItem("Normal", Image.SCALE_DEFAULT);
			addItem("Low", Image.SCALE_FAST);
			this.setSelectedItemByValue(Image.SCALE_DEFAULT);
		}
	}
}
