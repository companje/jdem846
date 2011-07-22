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

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;

@SuppressWarnings("serial")
public class VisualPreviewPanel extends JPanel
{
	//protected final static String NAME_PREVIEW = "Preview";
	//protected final static String NAME_LAYOUT = "Data Layout";
	
	protected String NAME_PREVIEW;
	protected String NAME_LAYOUT;
	
	private DataInputLayoutPane layoutPane;
	private ModelPreviewPane previewPane;
	
	private JRadioButton jradLayout;
	private JRadioButton jradPreview;
	
	private JPanel jpnlCards;
	
	public VisualPreviewPanel(DataPackage dataPackage, ModelOptions modelOptions)
	{
		// Create components
		layoutPane = new DataInputLayoutPane(dataPackage, modelOptions);
		previewPane = new ModelPreviewPane(dataPackage, modelOptions);
		
		NAME_PREVIEW = I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.preview.label");
		NAME_LAYOUT = I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.layout.label");
		
		jradLayout = new JRadioButton(NAME_LAYOUT);
		jradPreview = new JRadioButton(NAME_PREVIEW);
		
		ButtonGroup group = new ButtonGroup();
		group.add(jradLayout);
		group.add(jradPreview);
		
		jradLayout.setSelected(true);
		setBorder(BorderFactory.createEtchedBorder());
		
		
		// Set Tooltips
		jradLayout.setToolTipText(I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.layout.tooltip"));
		jradPreview.setToolTipText(I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.preview.tooltip"));
		
		// Set Layout
		jpnlCards = new JPanel(new CardLayout());
		jpnlCards.add(previewPane, NAME_PREVIEW);
		jpnlCards.add(layoutPane, NAME_LAYOUT);
		
		CardLayout cardLayout = (CardLayout)(jpnlCards.getLayout());
		cardLayout.show(jpnlCards, NAME_LAYOUT);
		
	
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.weightx = 2.0;
		constraints.weighty = 2.0;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(jpnlCards, constraints);
		add(jpnlCards);
		
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth  = 1;
		gridbag.setConstraints(jradLayout, constraints);
		add(jradLayout);
		
		gridbag.setConstraints(jradPreview, constraints);
		add(jradPreview);
		
		
		// Set Listeners

		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				CardLayout cardLayout = (CardLayout)(jpnlCards.getLayout());
				if (event.getStateChange() == ItemEvent.SELECTED) {
					if (jradLayout.isSelected()) {
						cardLayout.show(jpnlCards, NAME_LAYOUT);
					} else if (jradPreview.isSelected()) {
						cardLayout.show(jpnlCards, NAME_PREVIEW);
					}
				}
			}
		};
		
		jradLayout.addItemListener(itemListener);
		jradPreview.addItemListener(itemListener);
		
		jradPreview.setEnabled(true);
	}
	
	
	
	public void setModelOptions(ModelOptions modelOptions)
	{
		layoutPane.setModelOptions(modelOptions);
		previewPane.setModelOptions(modelOptions);
	}
	
	public void update()
	{
		layoutPane.update();
		previewPane.update();
	}
	
}
