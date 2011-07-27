package us.wthr.jdem846.ui.panels;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FlexGridPanel extends JPanel
{
	private int columns = 1;
	private int addColumn = 0;
	private GridBagLayout gridbag;
	
	public FlexGridPanel(int columns)
	{
		this.columns = columns;
		
		gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
	}
	
	@Override
	public Component add(Component component)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		addColumn++;
		
		if (addColumn >= columns) {
			constraints.gridwidth  = GridBagConstraints.REMAINDER;
			addColumn = 0;	
		} else {
			constraints.gridwidth  = 1;
		}
		gridbag.setConstraints(component, constraints);
		return super.add(component);
	}
}
