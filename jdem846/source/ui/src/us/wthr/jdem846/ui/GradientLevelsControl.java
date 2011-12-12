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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.color.ColoringInstance;
import us.wthr.jdem846.color.ColoringRegistry;
import us.wthr.jdem846.color.GradientColorStop;
import us.wthr.jdem846.color.GradientLoader;
import us.wthr.jdem846.color.ModelColoring;
import us.wthr.jdem846.exception.GradientLoadException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.base.Panel;

@SuppressWarnings("serial")
public class GradientLevelsControl extends Panel
{
	private static Log log = Logging.getLog(GradientLevelsControl.class);
	
	private String gradientIdentifier = null;
	private ColoringInstance coloringInstance = null;
	private List<PolyStop> polyList = new LinkedList<PolyStop>();
	private PolyStop selectedPolyStop = null;
	
	private List<GradientChangedListener> gradientChangedListeners = new LinkedList<GradientChangedListener>();
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	public GradientLevelsControl()
	{
		this.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) { }
			public void componentMoved(ComponentEvent arg0) { updatePolyList(); }
			public void componentResized(ComponentEvent arg0) { updatePolyList(); }
			public void componentShown(ComponentEvent arg0) { updatePolyList(); }
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent event)
			{
				onMouseDragged(event);
			}
			public void mouseMoved(MouseEvent event)
			{
				onMouseMoved(event);
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent event)
			{
				
			}
			public void mouseEntered(MouseEvent event)
			{
				
			}
			public void mouseExited(MouseEvent event)
			{
				
			}
			public void mousePressed(MouseEvent event)
			{
				onMousePressed(event);
			}
			public void mouseReleased(MouseEvent event)
			{
				onMouseReleased(event);
			}
		});
	}
	
	
	
	protected void onMouseDragged(MouseEvent event)
	{
		if (!this.isEnabled())
			return;
		
		if (coloringInstance == null) {
			return;
		}
		
		ModelColoring coloring = coloringInstance.getImpl();
		double min = coloring.getMinimumSupported();
		double max = coloring.getMaximumSupported();
		double range = Math.abs(min) + Math.abs(max);
		
		if (selectedPolyStop != null) {
			double height = this.getHeight();
			double mouseY = event.getY();
			
			double newStop = min + ((1.0f - (mouseY / height)) * range);
			selectedPolyStop.getColorStop().setPosition(newStop);
			updatePolyList();
			repaint();
			
			fireGradientChangedListeners();
		}
		
		
	}
	
	protected void onMouseMoved(MouseEvent event)
	{
		if (!this.isEnabled())
			return;
		
		//int mouseX = event.getX();
		//int mouseY = event.getY();
		
		PolyStop polyStop = getPolyStopByXY(event.getX(), event.getY());
		// TODO: Do something with it...
		
		if (polyStop != null) { // Mouse is over a polystop
			this.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		} else { // Mouse is not over a polystop
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	protected void onMousePressed(MouseEvent event)
	{
		if (!this.isEnabled())
			return;
		
		selectedPolyStop = getPolyStopByXY(event.getX(), event.getY());
	}
	
	protected void onMouseReleased(MouseEvent event)
	{
		if (!this.isEnabled())
			return;
		
		selectedPolyStop = null;
		fireChangeListeners();
	}
	
	
	protected PolyStop getPolyStopByXY(int x, int y)
	{

		
		PolyStop polyStopOnPoint = null;
		
		for (PolyStop polyStop : polyList) {
			if (polyStop.getClickablePolygon().contains(x, y)) {
				polyStopOnPoint = polyStop;
				break;
			}
		}
		
		return polyStopOnPoint;
	}
	
	@Override
	public void paint(Graphics g)
	{	
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		updatePolyList();
		
		Color fillColor = (isEnabled()) ? Color.WHITE : Color.GRAY;
		Color borderColor = (isEnabled()) ? Color.BLACK : Color.DARK_GRAY;
		
		

		
		//if (this.isEnabled())
		//	g2d.setColor(Color.BLACK);
		//else 
		//	g2d.setColor(Color.GRAY);
		
		for (PolyStop polyStop : polyList) {
			g2d.setColor(fillColor);
			g2d.fillPolygon(polyStop.getPolygon());
			g2d.setColor(borderColor);
			g2d.drawPolygon(polyStop.getPolygon());
		}
		
	}
	
	protected void updatePolyList()
	{
		polyList.clear();
		
		int height = getHeight();
		int width = getWidth();
		
		if (coloringInstance == null) {
			return;
		}
		
		ModelColoring coloring = coloringInstance.getImpl();
		
		
		GradientLoader gradient = coloring.getGradientLoader();
		if (gradient == null) {
			return;
		}
		
		double min = coloring.getMinimumSupported();
		double max = coloring.getMaximumSupported();
		double range = Math.abs(min) + Math.abs(max);
		
		for (GradientColorStop stop : gradient.getColorStops()) {
			int levelY = (int) Math.round((1.0 - (stop.getPosition() - min) / range) * ((double) height));
			
			if (levelY >= height)
				levelY = height - 1;
			
			Polygon triangle = new Polygon();
			triangle.addPoint(0, levelY);
			triangle.addPoint(4, levelY-4);
			triangle.addPoint(4, levelY+4);
			
			Polygon clickable = new Polygon();
			clickable.addPoint(0, levelY-4);
			clickable.addPoint(width, levelY-4);
			clickable.addPoint(width, levelY+4);
			clickable.addPoint(0, levelY+4);
			
			polyList.add(new PolyStop(triangle, clickable, stop));
		}
	}
	
	protected void updateGradient()
	{
		coloringInstance = ColoringRegistry.getInstance(gradientIdentifier);
		updatePolyList();
	}
	
	public String getGradientIdentifier()
	{
		return gradientIdentifier;
	}

	public void setGradientIdentifier(String gradientIdentifier)
	{
		
		this.gradientIdentifier = gradientIdentifier;
		updateGradient();
		repaint();
	}
	
	
	private class PolyStop
	{
		
		private Polygon polygon;
		private Polygon clickable;
		private GradientColorStop colorStop;
		
		
		public PolyStop(Polygon polygon, Polygon clickable, GradientColorStop colorStop)
		{
			this.polygon = polygon;
			this.colorStop = colorStop;
			this.clickable = clickable;
		}


		public Polygon getPolygon()
		{
			return polygon;
		}
		
		public Polygon getClickablePolygon()
		{
			return clickable;
		}

		public GradientColorStop getColorStop()
		{
			return colorStop;
		}
		
		
		
	}
	
	public void setConfigString(String configString)
	{
		if (configString == null || configString.trim().length() == 0)
			return;
		
		ModelColoring coloring = coloringInstance.getImpl();
		if (coloring == null)
			return;
		
		GradientLoader gradient = coloring.getGradientLoader();
		if (gradient == null) 
			return;
		
	
		try {
			gradient.loadJSON(configString);
		} catch (GradientLoadException ex) {
			log.warn("Failed to load gradient config string: " + ex.getMessage(), ex);
		}
	}
	
	public String getConfigString()
	{
		if (coloringInstance == null)
			return null;
		
		ModelColoring coloring = coloringInstance.getImpl();
		if (coloring == null)
			return null;
		
		GradientLoader gradient = coloring.getGradientLoader();
		if (gradient == null)
			return null;
		
		
		String configString = gradient.getConfigString();;
		return configString;
	}
	
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	protected void fireChangeListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
		}
	}
	
	public void addGradientChangedListener(GradientChangedListener gradientChangedListener)
	{
		gradientChangedListeners.add(gradientChangedListener);
	}
	
	
	public boolean removeGradientChangedListener(GradientChangedListener listener)
	{
		return gradientChangedListeners.remove(listener);
	}
	
	public void fireGradientChangedListeners()
	{
		String configString = getConfigString();

		for (GradientChangedListener listener : gradientChangedListeners) {
			listener.onGradientChanged(configString);
		}
	}
	
	
	public interface GradientChangedListener
	{
		public void onGradientChanged(String configString);
	}
	
}
