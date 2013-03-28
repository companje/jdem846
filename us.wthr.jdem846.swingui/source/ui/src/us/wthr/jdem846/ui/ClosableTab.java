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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class ClosableTab extends Panel
{
	private static Log log = Logging.getLog(ClosableTab.class);
	
	private TabLabel lblTitle;
	private Button btnClose;
	private boolean closable;
	
	private List<ActionListener> actionListeners = new LinkedList<ActionListener>();
	
	private Label tabIconLabel;
	private ImageIcon closeImage;
	private ImageIcon closeImageActive;
	
	public ClosableTab(String title, boolean closable)
	{
		this(title, null, closable);
	}
	
	public ClosableTab(String title, String iconUrl, boolean closable)
	{
		// Set Properties
		this.closable = closable;
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.setOpaque(false);
		
		// Create components
		lblTitle = new TabLabel(title);
		
		if (iconUrl != null) {
			try {
				ImageIcon tabIcon = ImageIcons.loadImageIcon(iconUrl);
				tabIconLabel = new Label();
				tabIconLabel.setIcon(tabIcon);
				tabIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
			} catch (IOException ex) {
				log.warn("Error loading tab icon from " + iconUrl, ex);
			}
		}
		
		
		
		btnClose = new Button();		
		String closeImageUrl = JDem846Properties.getProperty("us.wthr.jdem846.ui.closableTab.tabClose");
		try {
			closeImage = ImageIcons.loadImageIcon(closeImageUrl);
			btnClose.setIcon(closeImage);
		} catch (IOException ex) {
			log.warn("Failed to load icon at " + closeImageUrl, ex);
		}
		
		String closeImageActiveUrl = JDem846Properties.getProperty("us.wthr.jdem846.ui.closableTab.tabCloseActive");
		try {
			closeImageActive = ImageIcons.loadImageIcon(closeImageActiveUrl);
		} catch (IOException ex) {
			log.warn("Failed to load active icon from " + closeImageActiveUrl, ex);
		}
		
		
		btnClose.setOpaque(false);
		btnClose.setContentAreaFilled(false);
		btnClose.setFocusable(false);
		btnClose.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

		// Set Tooltips
		btnClose.setToolTipText(I18N.get("us.wthr.jdem846.ui.closableTab.closeButton.tooltip"));
		
		// Add Listeners
		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				onActionEvent(e);

			}
			
		});
		
		btnClose.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				btnClose.setIcon(closeImageActive);
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
				btnClose.setIcon(closeImage);
			}
			
		});
		
		
		// Set Layout
		if (tabIconLabel != null) {
			add(tabIconLabel);
		}
		add(lblTitle);
		if (closable) {
			add(btnClose);
		}
		
	}
	
	
	

	@Override
	public void dispose() throws ComponentException
	{
		log.info("ClosableTab.dispose()");
		super.dispose();
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
		lblTitle.setText(title);
	}
	
	class TabLabel extends Label {
		
		public TabLabel(String text)
		{
			super(text);
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			setFont(new Font("arial unicode MS", Font.PLAIN, 12));
		}
		
		@Override
		public void paint(Graphics g)
		{
			if (!JDem846Properties.getBooleanProperty("us.wthr.jdem846.ui.usingJGoodies")) {
				super.paint(g);
			}
		}
		
	}
}
