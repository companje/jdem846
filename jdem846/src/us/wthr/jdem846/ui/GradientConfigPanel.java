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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.GradientLevelsControl.GradientChangedListener;
import us.wthr.jdem846.ui.base.Button;

@SuppressWarnings("serial")
public class GradientConfigPanel extends TitledRoundedPanel
{
	
	private GradientSamplePanel samplePanel;
	private GradientLevelsControl levelsControl;
	private Button btnReset;
	
	private String gradientIdentifier = null;
	private ColoringInstance coloringInstance = null;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public GradientConfigPanel()
	{
		super(I18N.get("us.wthr.jdem846.ui.gradientConfigPanel.title"));
		
		// Create components
		//this.setBorder(BorderFactory.createEtchedBorder());
		levelsControl = new GradientLevelsControl();
		samplePanel = new GradientSamplePanel();
		btnReset = new Button(I18N.get("us.wthr.jdem846.ui.gradientConfigPanel.resetButton.label"));
		btnReset.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		
		// Set tooltips
		btnReset.setToolTipText(I18N.get("us.wthr.jdem846.ui.gradientConfigPanel.resetButton.tooltip"));
		
		// Add listeners
		levelsControl.addGradientChangedListener(new GradientChangedListener() {
			public void onGradientChanged(String configString)
			{
				samplePanel.repaint();
			}
		});
		
		levelsControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				fireChangeListeners(e);
			}
		});
		
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				onReset();
			}
		});
		
		
		// Set Layout
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 5.0;
		constraints.gridwidth  = GridBagConstraints.BOTH;
		gridbag.setConstraints(samplePanel, constraints);
		add(samplePanel);
	
		constraints.weightx = 0.5;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(levelsControl, constraints);
		add(levelsControl);
		
		constraints.weightx = 0.0;
		constraints.weighty = .1;
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.fill = GridBagConstraints.NONE;
		gridbag.setConstraints(btnReset, constraints);
		add(btnReset);
		
		
	}
	
	protected void onReset()
	{
		if (coloringInstance != null) {
			coloringInstance.getImpl().reset();
			repaint();
			fireChangeListeners(null);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		btnReset.setEnabled(enabled);
		levelsControl.setEnabled(enabled);
	}
	
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
	}

	protected void updateGradient()
	{
		coloringInstance = ColoringRegistry.getInstance(gradientIdentifier);
	}
	
	public String getGradientIdentifier()
	{
		return gradientIdentifier;
	}

	public void setGradientIdentifier(String gradientIdentifier)
	{
		
		this.gradientIdentifier = gradientIdentifier;
		levelsControl.setGradientIdentifier(gradientIdentifier);
		samplePanel.setGradientIdentifier(gradientIdentifier);
		updateGradient();
		repaint();
	}
	
	public void setConfigString(String configString)
	{
		levelsControl.setConfigString(configString);
	}
	
	public String getConfigString()
	{
		return levelsControl.getConfigString();
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	protected void fireChangeListeners(ChangeEvent event)
	{
		if (event == null)
			event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
		}
	}
	
}
