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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Slider;
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class LightPositionConfigPanel extends RoundedPanel
{
	private static Log log = Logging.getLog(LightPositionConfigPanel.class);
	
	private Slider sldSolarAzimuth;
	private Slider sldSolarElevation;
	private LightingPreviewPanel previewPanel;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	private boolean ignoreChanges = false;
	
	public LightPositionConfigPanel()
	{
		//super(I18N.get("us.wthr.jdem846.ui.lightDirectionPanel.title"));
		//this.setBorder(BorderFactory.createEtchedBorder());
		
		// Create components
		sldSolarAzimuth = new Slider(0, 359);
		sldSolarElevation = new Slider(0, 90);
		
		
		sldSolarElevation.setOrientation(JSlider.VERTICAL);
		previewPanel = new LightingPreviewPanel();
		//previewPanel.setPreferredSize(new Dimension(100, 100));
		//previewPanel.setSize(new Dimension(100, 100));
		
		// Set tooltips
		sldSolarAzimuth.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightDirectionPanel.solarAzimuthSlider.tooltip"));
		sldSolarElevation.setToolTipText(I18N.get("us.wthr.jdem846.ui.lightDirectionPanel.solarElevationSlider.tooltip"));
		
		
		// Set listeners
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (ignoreChanges)
					return;
				
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					syncPreviewToInputs();
					fireChangeListeners();
				}
			}
		};
		sldSolarAzimuth.addChangeListener(changeListener);
		sldSolarElevation.addChangeListener(changeListener);
		
		previewPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				updatePreview(true);
			}
		});
		previewPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				ignoreChanges = true;
				sldSolarAzimuth.setValue((int)Math.round(previewPanel.getSolarAzimuth()));
				sldSolarElevation.setValue((int)Math.round(previewPanel.getSolarElevation()));
				ignoreChanges = false;
				
				fireChangeListeners();
			}
		});
		
		// Set layout


		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth  = 5;//GridBagConstraints.REMAINDER;
		gridbag.setConstraints(sldSolarAzimuth, constraints);
		add(sldSolarAzimuth);
		
		JPanel spacer = new JPanel();
		constraints.weightx = 0.0;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(spacer, constraints);
		add(spacer);
		
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 5.0;
		constraints.weighty = 5.0;
		constraints.gridwidth  = 5;
		gridbag.setConstraints(previewPanel, constraints);
		add(previewPanel);
		
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.weightx = 0.0;
		constraints.gridwidth  = 1;
		gridbag.setConstraints(sldSolarElevation, constraints);
		add(sldSolarElevation);
		
		
		// Defaults
		setSolarAzimuth(180);
		setSolarElevation(45);
		//updatePreview(true);
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		previewPanel.setEnabled(enabled);
		sldSolarAzimuth.setEnabled(enabled);
		sldSolarElevation.setEnabled(enabled);
	}
	
	public void updatePreview(boolean recreatePolygons)
	{
		previewPanel.update(recreatePolygons);
	}
	
	protected void syncPreviewToInputs()
	{
		previewPanel.setSolarAzimuth(sldSolarAzimuth.getValue());
		previewPanel.setSolarElevation(sldSolarElevation.getValue());
		updatePreview(false);
	}
	
	
	public void setSolarAzimuth(double solarAzimuth)
	{
		sldSolarAzimuth.setValue((int)Math.round(solarAzimuth));
		previewPanel.setSolarAzimuth(solarAzimuth);
	}
	
	
	public double getSolarAzimuth()
	{
		return previewPanel.getSolarAzimuth();
		//return sldSolarAzimuth.getValue();
	}
	
	public void setSolarElevation(double solarElevation)
	{
		sldSolarElevation.setValue((int)Math.round(solarElevation));
		previewPanel.setSolarElevation(solarElevation);
	}
	
	
	public double getSolarElevation()
	{
		return previewPanel.getSolarElevation();
		//return sldSolarElevation.getValue();
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	protected void fireChangeListeners()
	{

		ChangeEvent event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
		}
	}

	public void dispose() throws ComponentException
	{
		
		log.info("Light Position Dispose");
		super.dispose();
		//previewPanel.dispose();
	}

}
