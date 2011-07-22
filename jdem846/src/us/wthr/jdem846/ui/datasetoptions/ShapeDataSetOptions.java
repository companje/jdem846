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

package us.wthr.jdem846.ui.datasetoptions;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinition;
import us.wthr.jdem846.shapefile.modeling.ShapeDataDefinitionLoader;
import us.wthr.jdem846.ui.JComboBoxModel;
import us.wthr.jdem846.ui.TitledRoundedPanel;
import us.wthr.jdem846.ui.border.StandardTitledBorder;

@SuppressWarnings("serial")
public class ShapeDataSetOptions extends TitledRoundedPanel
{
	private static Log log = Logging.getLog(ShapeDataSetOptions.class);
	
	private ShapeFileRequest shapeFileRequest;
	
	private JComboBox jcmbShapeStyles;
	private ShapeStylesListModel shapeStylesModel;
	
	public ShapeDataSetOptions(ShapeFileRequest shapeFileRequest)
	{
		super(I18N.get("us.wthr.jdem846.ui.datasetoptions.shapeOptions.title"));
		
		this.shapeFileRequest = shapeFileRequest;
		
		// Create components
		shapeStylesModel = new ShapeStylesListModel();
		jcmbShapeStyles = new JComboBox(shapeStylesModel);
		

		// Set initial values.
		shapeStylesModel.setSelectedItemByValue(shapeFileRequest.getShapeDataDefinitionId());
		
		// Set listeners
		ItemListener comboBoxItemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				//if (e.getStateChange() == ItemEvent.SELECTED) {
					setShapeDataDefinitionId(shapeStylesModel.getSelectedItemValue());
				//}
			}
		};
		jcmbShapeStyles.addItemListener(comboBoxItemListener);
		
		// Set layout
		GridLayout gridLayout = new GridLayout(2, 2);
		gridLayout.setVgap(2);
		setLayout(gridLayout);
		//setBorder(new StandardTitledBorder("Shape Options"));
		
		add(new JLabel(I18N.get("us.wthr.jdem846.ui.datasetoptions.shapeOptions.shapeStyles") + ":"));
		add(jcmbShapeStyles);
		
		
		log.info("Created shape datset options panel");
	}
	
	public void setShapeDataDefinitionId(String shapeDataDefinitionId)
	{
		log.info("Setting def id to: " + shapeDataDefinitionId + " for " + shapeFileRequest.getPath());
		shapeFileRequest.setShapeDataDefinitionId(shapeDataDefinitionId);
	}
	
	class ShapeStylesListModel extends JComboBoxModel<String>
	{
		
		public ShapeStylesListModel()
		{
			ShapeDataDefinitionLoader loader = new ShapeDataDefinitionLoader();
			
			for (ShapeDataDefinition shapeDataDefinition : loader.getShapeDataDefinitions()) {
				addItem(shapeDataDefinition.getName(), shapeDataDefinition.getId());
			}
		}
	}
}
