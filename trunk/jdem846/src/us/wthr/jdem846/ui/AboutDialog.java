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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Button;
import us.wthr.jdem846.ui.base.Dialog;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.util.ImageIcons;

@SuppressWarnings("serial")
public class AboutDialog extends Dialog
{
	private static Log log = Logging.getLog(AboutDialog.class);
	
	public AboutDialog(Frame owner)
	{
		super(owner);
		this.setModal(true);
		this.setTitle(I18N.get("us.wthr.jdem846.ui.about.about") + " " + JDem846Properties.getProperty("us.wthr.jdem846.applicationName"));
		
		ImagePanel imagePanel = new ImagePanel();
		
		
		Panel infoPanel = new Panel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		BoxLayout infoPanelLayout = new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS);
		infoPanel.setLayout(infoPanelLayout);
		
		Label label = null;
		
		label = new Label(JDem846Properties.getProperty("us.wthr.jdem846.applicationName"));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.add(label);
		infoPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		label = new Label(I18N.get("us.wthr.jdem846.ui.about.version") + ": " + JDem846Properties.getProperty("us.wthr.jdem846.version"));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.add(label);
		infoPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		label = new Label(JDem846Properties.getProperty("us.wthr.jdem846.copyRight"));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.add(label);
		infoPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		label = new Label(JDem846Properties.getProperty("us.wthr.jdem846.website"));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.add(label);
		infoPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		Button btnClose = new Button(I18N.get("us.wthr.jdem846.ui.ok"));
		btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		infoPanel.add(btnClose);
		
		
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		setLayout(layout);
		

		add(imagePanel, BorderLayout.CENTER);
		add(infoPanel, BorderLayout.SOUTH);

		this.pack();
		
		this.setLocationRelativeTo(null);
	}
	
	
	
	class ImagePanel extends JLabel
	{
		
		public ImagePanel()
		{
			ImageIcon image = null;
			try {
				image = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.about"));
			} catch (IOException ex) {
				log.warn("Failed to load graphic for about dialog: " + ex.getMessage(), ex);
			}
			setIcon(image);
			
			this.setSize(image.getIconWidth(), image.getIconHeight());
			
			this.setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
			this.setMinimumSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
			
		}
		
	}
	

}
