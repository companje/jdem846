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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.image.ImageUtilities;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.Slider;
import us.wthr.jdem846.ui.panels.RoundedPanel;

@SuppressWarnings("serial")
public class ImageDisplayPanel extends Panel
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ImageDisplayPanel.class);
	
	private Image trueImage;
	private Image scaled;
	private int imageTrueWidth = -1;
	private int imageTrueHeight = -1;
	private double scalePercent = 1.0;
	
	private int scaleQuality = Image.SCALE_DEFAULT;
	
	private int lastDragMouseX = -1;
	private int lastDragMouseY = -1;
	
	private int translateX = 0;
	private int translateY = 0;
	
	private List<MousePositionListener> mousePositionListeners = new LinkedList<MousePositionListener>();
	
	private boolean isBestFit = false;
	
	private double minScalePercent;
	private Slider sldZoomLevel;
	
	private boolean allowZooming = true;
	
	public Color backgroundColor = Color.WHITE;
	
	private Rectangle paintedImageBounds = new Rectangle();
	private String status = null;
	
	public ImageDisplayPanel()
	{
		// Set Properties
		setLayout(new BorderLayout());
		this.setOpaque(false);
		
		// Create components
		
		setAlignmentX(CENTER_ALIGNMENT);
		setAlignmentY(CENTER_ALIGNMENT);
		
		sldZoomLevel = new Slider(1, 100);
		sldZoomLevel.setOpaque(false);
		sldZoomLevel.setOrientation(Slider.VERTICAL);

		// Add listeners
		
		sldZoomLevel.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				double scale = minScalePercent + (((double)sldZoomLevel.getValue() / 100.0) * (1.0 - minScalePercent));
				
				log.info("Scale To: " + scale);
				setScalePercent(scale);
			}
		});
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (isBestFit) {
					zoomFit();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if (allowZooming) {
					onMouseDragged(e.getX(), e.getY());
				}
			}
			public void mouseMoved(MouseEvent e) {
				if (allowZooming) {
					onMouseMoved(e.getX(), e.getY());
				}
			}
		});
		
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				
			}
			public void mouseEntered(MouseEvent e) {
				
			}
			public void mouseExited(MouseEvent e) {
				
			}
			public void mousePressed(MouseEvent e) {
				if (allowZooming) {
					lastDragMouseX = e.getX();
					lastDragMouseY = e.getY();
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (allowZooming) {
					lastDragMouseX = -1;
					lastDragMouseY = -1;
				}
			}
		});
		
		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (allowZooming) {
					onMouseWheelMoved(e.getUnitsToScroll(), e.getScrollAmount(), e.getScrollType(), e.getX(), e.getY());
				}
			}
		});
		
		
		this.setLayout(null);
		
		this.add(sldZoomLevel);
		
		Insets insets = this.getInsets();
		Dimension size = new Dimension(30, 150);
		sldZoomLevel.setPreferredSize(size);
		sldZoomLevel.setPaintTicks(true);
		sldZoomLevel.setBounds(insets.left + 10, insets.top + 10, size.width, size.height);
		
		
	}
	
	@Override
	public void setBackground(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}
	
	public Color getBackground()
	{
		return backgroundColor;
	}
	
	protected void updateZoomSliderValue()
	{
		double scale = ((scalePercent - minScalePercent) / (1.0 - minScalePercent)) * 100;
		sldZoomLevel.setValue((int)MathExt.round(scale));
	}
	
	
	public int getScaleQuality()
	{
		return scaleQuality;
	}



	public void setScaleQuality(int scaleQuality) 
	{
		this.scaleQuality = scaleQuality;
		createScaledImage();
	}



	public double getScalePercent() 
	{
		return scalePercent;
	}



	public void setScalePercent(double scalePercent)
	{
		if (scalePercent >= 1.0)
			scalePercent = 1.0;
		if (scalePercent <= minScalePercent)
			scalePercent = minScalePercent;
		
		this.scalePercent = scalePercent;
		
		updateZoomSliderValue();
		
		createScaledImage();
		validateImagePosition();
		repaint();
	}



	public void setImage(Image image)
	{
		trueImage = image;
		imageTrueWidth = image.getWidth(this);
		imageTrueHeight = image.getHeight(this);
		
		this.minScalePercent = getZoomToFitScalePercentage();
	}
	
	protected void onMouseWheelMoved(int units, int amount, int type, int x, int y)
	{
		zoom(units);
	}
	
	protected void onMouseDragged(int x, int y)
	{
		
		int horizStep = -1 * (x - lastDragMouseX);
		int vertStep = -1 * (y - lastDragMouseY);
		

		lastDragMouseX = x;// + horizStep;
		lastDragMouseY = y;// + vertStep;
		
		
		
		translateX -= horizStep;
		translateY -= vertStep;
		
		validateImagePosition();
		repaint();
		
	}
	
	
	protected void onMouseMoved(int x, int y)
	{
		fireMousePositionListeners(x, y);
	}
	
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (backgroundColor != null) {
			g.setColor(backgroundColor);
			g.drawRect(0, 0, getWidth(), getHeight());
		}
		
		
		Image displayImage = getDisplayImage();
		if (displayImage != null) {
			int scaleToWidth = (int) Math.floor((double)imageTrueWidth * (double) scalePercent);
			int scaleToHeight = (int) Math.floor((double)imageTrueHeight * (double) scalePercent);
			
			int x = (int) ((getWidth() / 2.0) - (scaleToWidth / 2.0)) + translateX;
			int y = (int) ((getHeight() / 2.0) - (scaleToHeight / 2.0)) + translateY;
			
			g.drawImage(displayImage, x, y, null);
			
			paintedImageBounds.x = x;
			paintedImageBounds.y = y;
			paintedImageBounds.width = displayImage.getWidth(null);
			paintedImageBounds.height = displayImage.getHeight(null);
			
		}
		
		if (sldZoomLevel.isVisible()) {
			Insets insets = this.getInsets();
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fillRoundRect(insets.left + 10, insets.top + 10, sldZoomLevel.getWidth(), sldZoomLevel.getHeight(), 10, 10);
		}
		
		Stroke origStroke = g2d.getStroke();
		Font origFont = g2d.getFont();
		if (status != null) {
			g2d.setColor(new Color(0, 0, 0, 50));
			g2d.fillRect(0, getHeight() - 20, getWidth(), 20);
			
			FontMetrics fm = g2d.getFontMetrics();

			
			Font f = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		    GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(), status);
		    Shape shape = gv.getOutline();
		    
		    g2d.setStroke(new BasicStroke(2.0f));

			int w = fm.stringWidth(status);
			int x = (int) MathExt.round(((double)getWidth() / 2.0) - ((double)w / 2.0));
			
			int h = fm.getAscent();
			int y = (int) MathExt.round(getHeight() - (10 - ((double)h / 2.0)));
			
			
			
			g2d.setColor(new Color(0, 0, 0, 180));
			g.translate(x, y); 
			g2d.draw(shape);
			g.translate(-x, -y); 
			
			g2d.setFont(f);
			g2d.setColor(Color.WHITE);
			g2d.drawString(status, x, y);
			
			
		}
		g2d.setStroke(origStroke);
		g2d.setFont(origFont);
		
		super.paint(g);
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	protected void validateImagePosition()
	{
		Image displayImage = getDisplayImage();
		if (displayImage == null)
			return;

	}
	
	protected Image getDisplayImage()
	{
		if (scaled != null)
			return scaled;
		else
			return trueImage;
	}
	
	protected void createScaledImage()
	{
		if (trueImage != null) {
			int scaleToWidth = (int) Math.floor((double)imageTrueWidth * (double) scalePercent);
			int scaleToHeight = (int) Math.floor((double)imageTrueHeight * (double) scalePercent);
			

			Object hint = (scaleQuality == Image.SCALE_FAST) ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR : RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			boolean higherQuality = (scaleQuality == Image.SCALE_SMOOTH);
			
			scaled = ImageUtilities.getScaledInstance((BufferedImage)trueImage,
					scaleToWidth,
					scaleToHeight,
					hint,
					higherQuality);
			
		}
	}
	
	protected double getZoomToFitScalePercentage()
	{
		if (trueImage == null) {
			return 0.0;
		}
		
		double imageWidth = trueImage.getWidth(this);
		double imageHeight = trueImage.getHeight(this);
		
		double panelWidth = getWidth();
		double panelHeight = getHeight();
		
		double scaleWidth = 0;
		double scaleHeight = 0;
		
		double scale = Math.max(panelHeight/imageHeight, panelWidth/imageWidth);
		scaleHeight = imageHeight * scale;
		scaleWidth = imageWidth * scale;
		
		
		if (scaleHeight > panelHeight) {
			scale = panelHeight/scaleHeight;
		    scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		if (scaleWidth > panelWidth) {
		    scale = panelWidth/scaleWidth;
		    scaleHeight = scaleHeight * scale;
			scaleWidth = scaleWidth * scale;
		}
		
		
		return (scaleWidth / imageWidth);
	}
	
	public void zoom(double units)
	{
		isBestFit = false;
		setScalePercent(scalePercent + ((units / 100.0) * -1));
	}
	
	public void zoomIn()
	{
		zoom(-3);
	}
	
	public void zoomOut()
	{
		zoom(3);
	}
	
	public void zoomFit()
	{
		isBestFit = true;
		translateX = 0;
		translateY = 0;
		
		this.setScalePercent(getZoomToFitScalePercentage());

	}
	
	public void zoomActual()
	{
		setScalePercent(1.0);
	}
	
	
	
	
	public boolean isAllowZooming()
	{
		return allowZooming;
	}

	public void setAllowZooming(boolean allowZooming)
	{
		this.allowZooming = allowZooming;
		this.sldZoomLevel.setVisible(allowZooming);
	}

	
	public Rectangle getPaintedImageBounds()
	{
		return paintedImageBounds;
	}

	public void addMousePositionListener(MousePositionListener listener)
	{
		mousePositionListeners.add(listener);
	}
	
	public void removeMousePositionListener(MousePositionListener listener)
	{
		mousePositionListeners.remove(listener);
	}
	
	protected void fireMousePositionListeners(int x, int y)
	{
		for (MousePositionListener listener : mousePositionListeners) {
			listener.onMousePositionChanged(x, y, scalePercent);
		}
	}
	
	
	public interface MousePositionListener
	{
		public void onMousePositionChanged(int x, int y, double scaledPercent);
	}
	

	
}
