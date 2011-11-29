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

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import us.wthr.jdem846.ui.base.Frame;
import us.wthr.jdem846.ui.base.Label;

@SuppressWarnings("serial")
public class SimpleImageViewer extends Frame
{
	
	
	public SimpleImageViewer(Image image)
	{
		this.setTitle("SimpleImageViewer");
		this.setSize(1000, 1000);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ImageIcon imageIcon = new ImageIcon(image);
		
		Label imageLabel = new Label();
		imageLabel.setIcon(imageIcon);
		this.add(imageLabel);
		
		
	}
	
	
}
