package us.wthr.jdem846.ui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class DataLayersPane extends Panel
{
	private static Log log = Logging.getLog(DataLayersPane.class);
	
	private DataSetTree datasetTree;
	private DataSetOptionsPanel datasetOptionsPanel;
	private OrderingButtonBar orderingButtonBar;
	private DataOverviewPanel overviewPanel;
	
	public DataLayersPane(OrderingButtonBar orderingButtonBar,
			DataSetTree datasetTree,
			DataSetOptionsPanel datasetOptionsPanel,
			DataOverviewPanel overviewPanel)
	{
		this.orderingButtonBar = orderingButtonBar;
		this.datasetTree = datasetTree;
		this.datasetOptionsPanel = datasetOptionsPanel;
		this.overviewPanel = overviewPanel;
		
		//BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		add(orderingButtonBar, BorderLayout.NORTH);
		add(datasetTree, BorderLayout.CENTER);
		add(datasetOptionsPanel, BorderLayout.SOUTH);
		//add(Box.createVerticalGlue());
		add(overviewPanel, BorderLayout.PAGE_END);
		
		
	}
	
	
}
