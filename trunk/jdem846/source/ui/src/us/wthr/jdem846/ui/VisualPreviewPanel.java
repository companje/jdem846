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

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.RadioButton;

@SuppressWarnings("serial")
@Deprecated
public class VisualPreviewPanel extends Panel
{
	//protected final static String NAME_PREVIEW = "Preview";
	//protected final static String NAME_LAYOUT = "Data Layout";
	
	protected String NAME_PREVIEW;
	protected String NAME_LAYOUT;
	
	private DataInputLayoutPane layoutPane;
	private ModelPreviewPane previewPane;
	
	private RadioButton radLayout;
	private RadioButton radPreview;
	
	private Panel pnlCards;
	
	public VisualPreviewPanel(ModelContext modelContext)
	{
		// Create components
		layoutPane = new DataInputLayoutPane(modelContext);
		previewPane = new ModelPreviewPane(modelContext);
		
		NAME_PREVIEW = I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.preview.label");
		NAME_LAYOUT = I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.layout.label");
		
		radLayout = new RadioButton(NAME_LAYOUT);
		radPreview = new RadioButton(NAME_PREVIEW);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radLayout);
		group.add(radPreview);
		
		radLayout.setSelected(true);
		setBorder(BorderFactory.createEtchedBorder());
		
		
		// Set Tooltips
		radLayout.setToolTipText(I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.layout.tooltip"));
		radPreview.setToolTipText(I18N.get("us.wthr.jdem846.ui.visualPreviewPanel.preview.tooltip"));
		
		// Set Layout
		pnlCards = new Panel(new CardLayout());
		pnlCards.add(previewPane, NAME_PREVIEW);
		pnlCards.add(layoutPane, NAME_LAYOUT);
		
		CardLayout cardLayout = (CardLayout)(pnlCards.getLayout());
		cardLayout.show(pnlCards, NAME_LAYOUT);
		
	
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.weightx = 2.0;
		constraints.weighty = 2.0;
		constraints.gridwidth  = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(pnlCards, constraints);
		add(pnlCards);
		
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridwidth  = 1;
		gridbag.setConstraints(radLayout, constraints);
		add(radLayout);
		
		gridbag.setConstraints(radPreview, constraints);
		add(radPreview);
		
		
		// Set Listeners

		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				CardLayout cardLayout = (CardLayout)(pnlCards.getLayout());
				if (event.getStateChange() == ItemEvent.SELECTED) {
					if (radLayout.isSelected()) {
						cardLayout.show(pnlCards, NAME_LAYOUT);
					} else if (radPreview.isSelected()) {
						cardLayout.show(pnlCards, NAME_PREVIEW);
					}
				}
			}
		};
		
		radLayout.addItemListener(itemListener);
		radPreview.addItemListener(itemListener);
		
		radPreview.setEnabled(true);
	}
	
	
	
	//public void setModelOptions(ModelOptions modelOptions)
	//{
	//	layoutPane.setModelOptions(modelOptions);
	//	previewPane.setModelOptions(modelOptions);
	//}
	
	public void update()
	{
		layoutPane.update();
		previewPane.update(false, true);
	}
	
}
