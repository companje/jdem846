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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.border.ButtonBorder;

/** A really quick and dirty memory monitor histogram
 * 
 * @author A345926
 *
 */
@SuppressWarnings("serial")
public class MemoryMonitor extends Label
{
	private LinkedList<MemorySnapshot> usageList = new LinkedList<MemorySnapshot>();
	private MemoryMXBean memoryBean;
	private Timer timer;
	
	public MemoryMonitor(int pollDelay)
	{
		setToolTipText(I18N.get("us.wthr.jdem846.ui.memoryMonitor.tooltip"));
		setText("                       ");
		this.setOpaque(false);
		//this.setBorder(BorderFactory.createEtchedBorder());
		this.setBorder(new ButtonBorder());
		///this.setPreferredSize(new Dimension(100, 25));
		
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e)
			{
				memoryBean.gc();
			}
			public void mouseEntered(MouseEvent e) { }
			public void mouseExited(MouseEvent e) { }
			public void mousePressed(MouseEvent e) { }
			public void mouseReleased(MouseEvent e) { }
		});
		memoryBean = ManagementFactory.getMemoryMXBean();
		
		timer = new Timer(pollDelay, new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				getNextProfile();
			}
		});
	}
	
	public void start()
	{
		if (!isRunning())
			timer.start();
	}
	
	public void stop()
	{
		if (isRunning())
			timer.stop();
	}
	
	public boolean isRunning()
	{
		return timer.isRunning();
	}
	
	protected void getNextProfile()
	{
		MemoryUsage usage = memoryBean.getHeapMemoryUsage();
		usageList.add(new MemorySnapshot(usage.getUsed(), usage.getCommitted(), usage.getMax()));
		
		
		trimProfiles();
		repaint();
	}
	
	protected void trimProfiles()
	{
		while (usageList.size() > this.getWidth()) {
			usageList.remove();
		}
	}
	
	protected long getMaxCommittedInList()
	{
		long max = 0;
		for (MemorySnapshot usage : usageList) {
			if (usage.getCommitted() > max)
				max = usage.getCommitted();
		}
		return max;
	}
	
	protected long getMaxUsedInList()
	{
		long max = 0;
		for (MemorySnapshot usage : usageList) {
			if (usage.getUsed() > max)
				max = usage.getUsed();
		}
		return max;
	}
	
	@Override
	public void paint(Graphics g)
	{
		if (usageList.size() == 0) {
			super.paint(g);
			return;
		}
		
		
		int width = this.getWidth();
		int height = this.getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		
		g.setColor(getForeground());
		//g.setColor(Color.GRAY);
		
		long maxCommitted = getMaxCommittedInList();
		long maxUsed = getMaxUsedInList();
		for (int i = 0; i < usageList.size(); i++) {
			MemorySnapshot usage = usageList.get(usageList.size()-i-1);
			double pct = (double)usage.getUsed() / (double)maxCommitted;
			int y = (int) Math.round(((double)height * pct));
			if (y == 0)
				y = 1;

			g.drawLine(width-i, height - y - 2, width-i, height);
		}
		
		g.setColor(Color.GRAY);
		
		Stroke origStroke = ((Graphics2D)g).getStroke(); 
		
		BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[] {1.0f, 2.0f}, 0.0f);
		((Graphics2D)g).setStroke(stroke);
		int y = (int) Math.round(((double)height * (double)maxUsed / (double)maxCommitted));
		if (y == 0)
			y = 1;
		g.drawLine(0, height - y - 2, width, height - y - 2);
		((Graphics2D)g).setStroke(origStroke);
		
		super.paint(g);
	}
	
	class MemorySnapshot
	{
		private long used;
		private long committed;
		private long max;
		
		public MemorySnapshot(long used, long committed, long max)
		{
			this.used = used;
			this.committed = committed;
			this.max = max;
		}
		
		public long getUsed()
		{
			return used;
		}
		
		public long getCommitted()
		{
			return committed;
		}
		
		public long getMax()
		{
			return max;
		}
	}
}
