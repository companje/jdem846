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

package us.wthr.jdem846.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;

import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.SplitPane;

@SuppressWarnings("serial")
public class DockingContainerPanel extends Panel
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(DockingContainerPanel.class);
	
	private SideDockingPanel leftPanel = null;
	private SideDockingPanel rightPanel = null;
	private EmbeddedTabbedPane centerTabPanel = null;
	private EmbeddedTabbedPane southTabPanel = null;
	private SplitPane outterSplit = null;
	private SplitPane innerSplit = null;
	private SplitPane verticalSplit = null;
	
	private List<Component> componentList = new LinkedList<Component>();
	
	public DockingContainerPanel()
	{
		
		// Create components
		leftPanel = new SideDockingPanel();
		rightPanel = new SideDockingPanel();
		
		
		
		centerTabPanel = new EmbeddedTabbedPane();
		southTabPanel = new EmbeddedTabbedPane();
		
		ContainerListener containerListener = new ContainerListener() {

			public void componentAdded(ContainerEvent e)
			{
				log.info("Component Added");
				componentList.add(e.getComponent());
			}

			public void componentRemoved(ContainerEvent e)
			{
				log.info("Component Removed");
				componentList.remove(e.getComponent());
			}
			
		};
		
		leftPanel.addContainerListener(containerListener);
		rightPanel.addContainerListener(containerListener);
		centerTabPanel.addContainerListener(containerListener);
		southTabPanel.addContainerListener(containerListener);
	
		
		
		// Set Layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		//add(leftPanel, BorderLayout.WEST);
		//add(rightPanel, BorderLayout.EAST);
		//add(centerTabPanel, BorderLayout.CENTER);
		
		verticalSplit = new SplitPane(SplitPane.VERTICAL_SPLIT);
		verticalSplit.setBorder(BorderFactory.createEmptyBorder());
		verticalSplit.setResizeWeight(1);
		
		outterSplit = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
		outterSplit.setBorder(BorderFactory.createEmptyBorder());
		outterSplit.setResizeWeight(0);
		//outterSplit.setDividerSize(5);
		
		innerSplit = new SplitPane(SplitPane.HORIZONTAL_SPLIT);
		innerSplit.setBorder(BorderFactory.createEmptyBorder());
		innerSplit.setResizeWeight(1);
		//innerSplit.setDividerSize(5);
		
		innerSplit.add(centerTabPanel);
		innerSplit.add(rightPanel);
		
		
		outterSplit.add(leftPanel);
		outterSplit.add(innerSplit);
		
		verticalSplit.add(outterSplit);
		verticalSplit.add(southTabPanel);
		
		add(verticalSplit, BorderLayout.CENTER);

	}
	
	
	protected boolean containerHasComponent(Container container, Component comp)
	{
		for (Component _comp : container.getComponents()) {
			if (_comp == comp) {
				return true;
			}
		}
		return false;
	}
	
	public void setComponentVisible(Component comp)
	{
		if (containerHasComponent(centerTabPanel, comp))
			centerTabPanel.setSelectedComponent(comp);
		
		if (containerHasComponent(southTabPanel, comp))
			southTabPanel.setSelectedComponent(comp);

		
	}
	
	public void addLeft(Component component, boolean scroll)
	{
		if (scroll) {
			leftPanel.add(new DockPanel(component));
		} else {
			leftPanel.add(component);
		}
	}
	
	public void addRight(Component component, boolean scroll)
	{
		if (scroll) {
			rightPanel.add(new DockPanel(component));
		} else {
			rightPanel.add(component);
		}
	}
	
	public void addCenter(String title, Component component)
	{
		centerTabPanel.addTab(title, component);
	}
	
	
	public void addSouth(String title, Component component)
	{
		southTabPanel.addTab(title, component);
	}
	
	public void setSouth(Component component)
	{
		this.add(component, BorderLayout.SOUTH);
	}
	
	public void setLeftWidth(int width)
	{
		outterSplit.setDividerLocation(width);
	}
	
	public int getLeftWidth()
	{
		return outterSplit.getDividerLocation();
	}
	
	public void setRightWidth(int width)
	{
		innerSplit.setDividerLocation(innerSplit.getWidth() - width);
	}
	
	public int getRightWidth()
	{
		return innerSplit.getWidth() - innerSplit.getDividerLocation();
	}
	
	public void setLeftVisible(boolean v)
	{
		leftPanel.setVisible(v);
		if (!v) {
			outterSplit.setDividerSize(0);
		} else {
			outterSplit.setDividerSize(5);
		}
	}
	
	public void setRightVisible(boolean v)
	{
		rightPanel.setVisible(v);
		if (!v) {
			innerSplit.setDividerSize(0);
		} else {
			innerSplit.setDividerSize(5);
		}
		
	}
	
	public void setSouthVisible(boolean v)
	{
		southTabPanel.setVisible(v);
		if (!v) {
			verticalSplit.setDividerSize(0);
		} else {
			verticalSplit.setDividerSize(5);
		}
	}
	
	@Override
	public void dispose() throws ComponentException
	{
		super.dispose();
	}
	

	
	
	
}
