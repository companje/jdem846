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


import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ProgressBar;

@SuppressWarnings("serial")
public class StatusBar extends Panel
{
	
	private Label lblStatus;
	private ProgressBar prgProgress;
	
	
	public StatusBar()
	{
		// Set Properties
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		// Create components
		lblStatus = new Label(" ");
		prgProgress = new ProgressBar(0, 100);

		lblStatus.setFont(new Font("Courier New", Font.PLAIN, 11));

		
		// Set Layout
		BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(layout);
		
	    add(lblStatus);
	    add( Box.createHorizontalGlue() );
		add(prgProgress);
		

	}
	
	public void setStatus(String status)
	{
		lblStatus.setText(status);
	}
	
	public void clear()
	{
		lblStatus.setText(" ");
	}
	
	public void setProgressVisible(boolean visible)
	{
		prgProgress.setVisible(visible);
	}
	
	public void setProgress(int progress)
	{
		prgProgress.setValue(progress);
	}
	
	public int getProgress()
	{
		return prgProgress.getValue();
	}
	
	
}
