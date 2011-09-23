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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class WorkingGlassPane extends Panel
{
	private static Log log = Logging.getLog(WorkingGlassPane.class);

	private String text;
	private Font labelFont = new Font("SansSerif", Font.BOLD, 15);
	//private JLabel jlblLabel;
	private Component shadeComponent;
	
	private int arcAngle = 0;
	private Timer arcTimer;
	
	public WorkingGlassPane()
	{
		setLayout(new BorderLayout());
		setOpaque(false);
		//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setVisible(false);
		
		arcTimer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				arcAngle+=5;
				if (arcAngle >= 360)
					arcAngle = 0;
				repaint();
			}
		});
	}
	
	public void setVisible(boolean visible)
	{
		if (arcTimer == null)
			return;
		
		try {
			if (visible && !arcTimer.isRunning())
				arcTimer.restart();
			else if(!visible && arcTimer.isRunning())
				arcTimer.stop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		super.setVisible(visible);
	}
	
	public void clearText()
	{
		this.text = null;
		//jlblLabel.setText(null);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}

	

	public Component getShadeComponent()
	{
		return shadeComponent;
	}

	public void setShadeComponent(Component shadeComponent)
	{
		this.shadeComponent = shadeComponent;
	}

	@Override
	public void paint(Graphics g)
	{
		if (shadeComponent == null) {
			shadeComponent = JdemFrame.getInstance();
		}
		
		if (!shadeComponent.isVisible()) {
			return;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Color color = new Color(150, 150, 150, 100);
		g2d.setColor(color);
		
		Point pt = this.getLocationOnScreen();
		Point compPoint = shadeComponent.getLocationOnScreen();
		int x = compPoint.x - pt.x;
		int y = compPoint.y - pt.y;
		int width = shadeComponent.getWidth();
		int height = shadeComponent.getHeight();
		
		//int x = (limitSpace != null) ? limitSpace.x - pt.x : 0;
		//int y = (limitSpace != null) ? limitSpace.y - pt.y : 0;
		//int width = (limitSpace != null) ? limitSpace.width : getWidth();
		//int height = (limitSpace != null) ? limitSpace.height : getHeight();
		
		g2d.fillRect(x, y, width, height);
		
		
		
		g2d.setColor(new Color(100, 100, 100, 150));
		int arcWidth = 100;
		int arcHeight = 100;
		int arcX = x + (width / 2) - (arcWidth / 2);
		int arcY = y + (height / 2) - (arcHeight / 2);// + (labelFont.getSize() / 2);
		g2d.fillArc(arcX, arcY, arcWidth, arcHeight, 0, arcAngle);
		
		
		if (text != null) {
			g2d.setColor(Color.black);
			g2d.setFont(labelFont);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			
			int fontWidth = fontMetrics.stringWidth(text);
			
			int labelX = x + (width / 2) - (fontWidth / 2);
			int labelY = y + (height / 2) + (fontMetrics.getHeight() / 2) - (fontMetrics.getAscent() / 2);// - labelFont.getSize();
			g2d.drawString(text, labelX, labelY);
		}
	}
}