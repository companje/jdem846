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

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.ComboBox;
import us.wthr.jdem846.ui.base.JComboBoxModel;

@SuppressWarnings("serial")
public class OutputImageViewButtonBar extends ComponentButtonBar
{
	public static final int BTN_SAVE = 0;
	public static final int BTN_ZOOM_IN = 1;
	public static final int BTN_ZOOM_OUT = 2;
	public static final int BTN_ZOOM_ACTUAL = 3;
	public static final int BTN_ZOOM_FIT = 4;
	public static final int OPTION_QUALITY = 5;
	public static final int BTN_STOP = 6;
	public static final int BTN_PAUSE = 7;
	public static final int BTN_RESUME = 8;
	
	private ToolbarButton jbtnSave;
	private ToolbarButton jbtnZoomIn;
	private ToolbarButton jbtnZoomOut;
	private ToolbarButton jbtnZoomActual;
	private ToolbarButton jbtnZoomFit;
	private ToolbarButton jbtnStop;
	private ToolbarButton jbtnPause;
	private ToolbarButton jbtnResume;
	
	private ComboBox cmbQuality;
	private ImageQualityListModel qualityModel;
	
	private JLabel lblQuality;
	
	private List<ButtonClickedListener> buttonClickedListeners = new LinkedList<ButtonClickedListener>();	
	private List<OptionChangeListener> optionChangeListeners = new LinkedList<OptionChangeListener>();
	
	public OutputImageViewButtonBar(Component owner)
	{
		super(owner);
		
		// Create components
		jbtnSave = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.exportButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.export"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_SAVE);
			}
		});
		jbtnZoomIn = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomInButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomIn"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_IN);
			}
		});
		jbtnZoomOut = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomOutButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomOut"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_OUT);
			}
		});
		jbtnZoomActual = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomActualButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomActual"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_ACTUAL);
			}
		});
		jbtnZoomFit = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomFitButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.zoomFit"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_ZOOM_FIT);
			}
		});
		
		jbtnStop = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.stopButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.stop"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_STOP);
			}
		});
		
		jbtnPause = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.pauseButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.pause"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_PAUSE);
			}
		});
		
		jbtnResume = new ToolbarButton(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.resumeButton"), JDem846Properties.getProperty("us.wthr.jdem846.ui.outputImageView.resume"), new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireButtonClickedListeners(BTN_RESUME);
			}
		});

		qualityModel = new ImageQualityListModel();
		cmbQuality = new ComboBox(qualityModel);
		lblQuality = new JLabel("Quality: ");
		
		// Set Tooltips
		jbtnSave.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.exportTooltip"));
		jbtnZoomIn.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomInTooltip"));
		jbtnZoomOut.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomOutTooltip"));
		jbtnZoomActual.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomActualTooltip"));
		jbtnZoomFit.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.zoomFitTooltip"));
		cmbQuality.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.qualityTooltip"));
		jbtnStop.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.stopTooltip"));
		jbtnPause.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.pauseTooltip"));
		jbtnResume.setToolTipText(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.resumeTooltip"));
		
		boolean displayText = JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.outputImageButtonBar.displayText");
		jbtnSave.setTextDisplayed(displayText);
		jbtnZoomIn.setTextDisplayed(displayText);
		jbtnZoomOut.setTextDisplayed(displayText);
		jbtnZoomActual.setTextDisplayed(displayText);
		jbtnZoomFit.setTextDisplayed(displayText);
		jbtnStop.setTextDisplayed(displayText);
		jbtnPause.setTextDisplayed(displayText);
		jbtnResume.setTextDisplayed(displayText);
		lblQuality.setVisible(displayText);
		
		
		// Add Listeners
		cmbQuality.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fireOptionChangeListeners(OPTION_QUALITY);
				}	
			}
		});
		
		
		
		// Set layout
		add(jbtnSave);
		addSeparator();
		add(jbtnZoomIn);
		add(jbtnZoomOut);
		add(jbtnZoomActual);
		add(jbtnZoomFit);
		addSeparator();
		add(jbtnStop);
		add(jbtnPause);
		add(jbtnResume);
		addSeparator();
		add(lblQuality);
		add(cmbQuality);
		
	}
	
	public void setSelectedImageQuality(int imageQuality)
	{
		qualityModel.setSelectedItemByValue(imageQuality);
	}
	
	public int getSelectedImageQuality()
	{
		return qualityModel.getSelectedItemValue();
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
			cmbQuality.setEnabled(enabled);
			break;
		case BTN_STOP:
			jbtnStop.setEnabled(enabled);
			break;
		case BTN_PAUSE:
			jbtnPause.setEnabled(enabled);
			break;
		case BTN_RESUME:
			jbtnResume.setEnabled(enabled);
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
			case BTN_PAUSE:
				listener.onPauseClicked();
				break;
			case BTN_RESUME:
				listener.onResumeClicked();
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
		public void onPauseClicked();
		public void onResumeClicked();
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
			addItem(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.quality.smooth"), Image.SCALE_SMOOTH);
			addItem(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.quality.default"), Image.SCALE_DEFAULT);
			addItem(I18N.get("us.wthr.jdem846.ui.outputImageViewButtonBar.quality.fast"), Image.SCALE_FAST);
			this.setSelectedItemByValue(Image.SCALE_DEFAULT);
		}
	}
}
