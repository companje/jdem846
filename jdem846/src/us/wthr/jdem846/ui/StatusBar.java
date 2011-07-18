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

@SuppressWarnings("serial")
public class StatusBar extends JPanel
{
	
	private JLabel jlblStatus;
	private JProgressBar jprgProgress;
	
	
	public StatusBar()
	{
		// Set Properties
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		// Create components
		jlblStatus = new JLabel(" ");
		jprgProgress = new JProgressBar(0, 100);

		jlblStatus.setFont(new Font("Courier New", Font.PLAIN, 11));

		
		// Set Layout
		BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(layout);
		
	    add(jlblStatus);
	    add( Box.createHorizontalGlue() );
		add(jprgProgress);
		

	}
	
	public void setStatus(String status)
	{
		jlblStatus.setText(status);
	}
	
	public void clear()
	{
		jlblStatus.setText(" ");
	}
	
	public void setProgressVisible(boolean visible)
	{
		jprgProgress.setVisible(visible);
	}
	
	public void setProgress(int progress)
	{
		jprgProgress.setValue(progress);
	}
	
	public int getProgress()
	{
		return jprgProgress.getValue();
	}
	
	
}
