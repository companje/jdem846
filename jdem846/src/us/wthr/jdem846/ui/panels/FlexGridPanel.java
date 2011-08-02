package us.wthr.jdem846.ui.panels;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.BasePanel;

@SuppressWarnings("serial")
public class FlexGridPanel extends BasePanel
{
	private static Log log = Logging.getLog(FlexGridPanel.class);
	
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
	public void dispose() throws ComponentException
	{
		super.dispose();
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
