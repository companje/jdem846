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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;

@SuppressWarnings("serial")
public class ClosableTab extends JPanel
{
	private JLabel jlblTitle;
	private JButton jbtnClose;
	private boolean closable;
	
	private List<ActionListener> actionListeners = new LinkedList<ActionListener>();
	
	public ClosableTab(String title, boolean closable)
	{
		// Set Properties
		this.closable = closable;
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		this.setOpaque(false);

		// Create components
		jlblTitle = new JLabel(title);
		jlblTitle.setOpaque(false);
		jlblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		
		jbtnClose = new JButton();
		jbtnClose.setIcon(new ImageIcon(getClass().getResource(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/window-close.png")));
		jbtnClose.setOpaque(false);
		jbtnClose.setContentAreaFilled(false);
		jbtnClose.setFocusable(false);
		jbtnClose.setBorder(BorderFactory.createEtchedBorder());
		jbtnClose.setBorderPainted(false);
		
		// Set Tooltips
		jbtnClose.setToolTipText(I18N.get("us.wthr.jdem846.ui.closableTab.closeButton.tooltip"));
		
		// Add Listeners
		jbtnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				onActionEvent(e);

			}
			
		});
		
		
		// Set Layout
		add(jlblTitle);
		if (closable)
			add(jbtnClose);
		
		
	}

	/** Replaces the event source component with this tab and fires
	 * the action listeners.
	 * 
	 * @param e
	 */
	protected void onActionEvent(ActionEvent e)
	{
		e.setSource(this);
		fireActionPerformedEvent(e);
	}
	
	protected void fireActionPerformedEvent(ActionEvent e) 
	{
		for (ActionListener listener : actionListeners) {
			listener.actionPerformed(e);
		}
	}
	
	public void addActionListener(ActionListener listener)
	{
		this.actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener)
	{
		this.actionListeners.remove(listener);
	}
	
	public boolean isClosable() 
	{
		return closable;
	}

	public void setClosable(boolean closable) 
	{
		this.closable = closable;
	}
	
	public void setTitle(String title)
	{
		jlblTitle.setText(title);
	}
}
