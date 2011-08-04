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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Perspectives;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.GridFloat;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.render.gfx.Renderable;
import us.wthr.jdem846.render.gfx.Square;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.gfx.ViewportBuffer;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.util.TempFiles;

@SuppressWarnings("serial")
public class LightingPreviewPanel extends Panel
{
	
	private static Log log = Logging.getLog(LightingPreviewPanel.class);
	
	private BufferedImage prerendered = null;
	//private List<Renderable> renderObjects = new LinkedList<Renderable>();

	private double solarAzimuth = 183.0;
	private double solarElevation = 71.0;
	
	private double renderedAzimuth = -1;
	private double renderedElevation = -1;
	
	private ModelOptions modelOptions;
	private DataPackage dataPackage;
	
	private List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	private boolean isDisposed = false;
	
	public LightingPreviewPanel()
	{
		this.setOpaque(false);
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mouseDragged(MouseEvent e)
			{
				onMouseLocation(e.getX(), e.getY(), false);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				onMouseLocation(e.getX(), e.getY(), true);
				fireChangeListeners();
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				onMouseLocation(e.getX(), e.getY(), true);
			}
			
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		
		
		
		modelOptions = new ModelOptions();
		modelOptions.setColoringType(JDem846Properties.getProperty("us.wthr.jdem846.ui.lightingPreviewPanel.previewColoring"));
		
		try {
			File tmpGridFloatData = TempFiles.getTemporaryFile("lghtprv", ".flt", "jar://" + JDem846Properties.getProperty("us.wthr.jdem846.previewData") + "/raster-data.flt");
			
			File tmpTempGridFloatHeader = TempFiles.getTemporaryFile("lghtprv", ".hdr", "jar://" + JDem846Properties.getProperty("us.wthr.jdem846.previewData") + "/raster-data.hdr");
			
			String tmpHdrPath = tmpGridFloatData.getAbsolutePath();
			tmpHdrPath = tmpHdrPath.replaceAll("\\.flt", ".hdr");
			log.info("New Header Path: " + tmpHdrPath);
			File tmpGridFloatHeader = new File(tmpHdrPath);
			
			tmpTempGridFloatHeader.renameTo(tmpGridFloatHeader);
			
			GridFloat previewData = new GridFloat(tmpGridFloatData.getAbsolutePath());
			
			modelOptions.setBackgroundColor(I18N.get("us.wthr.jdem846.color.transparent"));
			modelOptions.setWidth(previewData.getHeader().getColumns());
			modelOptions.setHeight(previewData.getHeader().getRows());
			
			dataPackage = new DataPackage();
			dataPackage.addDataSource(previewData);
			dataPackage.prepare();
			dataPackage.calculateElevationMinMax(true);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		
	}
	
	
	protected void onMouseLocation(int x, int y, boolean updatePreview)
	{
		if (!isEnabled())
			return;
		
		
		int size = (getWidth() < getHeight()) ? getWidth() : getHeight();
		
		int xMid = (int)Math.round(((double)getWidth()/2.0));
		int yMid = (int)Math.round(((double)getHeight()/2.0));
		
		double mX = x - xMid;
		double mY = y - yMid;
		double radius = sqrt(pow(Math.abs(mX), 2) + pow(Math.abs(mY), 2));
		double angle = Math.toDegrees(asin(Math.abs(mY) / radius));
		
		if (mX >= 0 && mY < 0) { 			// Top right
			angle = 90 - angle;
		} else if (mX >= 0 && mY >= 0) { 	// Bottom right
			angle += 90;
		} else if (mX < 0 && mY >= 0) { 	// bottom left
			angle = 180 +  90 - angle;
		} else { 							// Top right
			angle = 270 + angle;
		}

		double pctElev = radius / ((double) size / 2.0);
		double newElev = 90.0 * (1.0 - pctElev);
		
		if (newElev < 0)
			newElev = 0;
		if (newElev > 90)
			newElev = 90;
		
		this.setSolarAzimuth(angle);
		this.setSolarElevation(newElev);
		
		update(updatePreview);
	}
	

	public void update(boolean rerenderPreview)
	{

		if (rerenderPreview && (renderedAzimuth != solarAzimuth || renderedElevation != solarElevation)) {
			
			//JdemFrame.getInstance().setGlassVisible(I18N.get("us.wthr.jdem846.ui.modelPreviewPane.working"), this, true);

			
			try {
				log.info("Updating lighting preview model image");
				Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
				OutputProduct<DemCanvas> product = dem2d.generate();
				
				prerendered = (BufferedImage) product.getProduct().getImage();
				
			} catch (RenderEngineException e) {
				log.warn("Failed to render preview image: " + e.getMessage(), e);
				e.printStackTrace();
			} finally {
				//JdemFrame.getInstance().setGlassVisible(false);
				
				renderedAzimuth = solarAzimuth;
				renderedElevation = solarElevation;
			}
		}
		
		repaint();
	}
	

	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
	}
	
	protected void getPoints2D(double angle, double radius, double[] points)
	{
		double b = radius * cos(angle);
        double x = sqrt(pow(radius, 2) - pow(b, 2));
        double y = sqrt(pow(radius, 2) - pow(x, 2));
        
        if (angle <= 90.0) {
        	y = radius - y;
        } else if (angle  <= 180.0) {
        	y = y + radius;
        } else if (angle  <= 270.0) {
        	x = -1 * x;
        	y = y + radius;
        } else if (angle <= 360.0) {
        	x = -1 * x;
        	y = radius - y;
        }
        points[0] = x;
        points[1] = y;

	}
	
	@Override
	public void paint(Graphics g)
	{
		
		Color shadowColor = Color.DARK_GRAY;
		Color arcColor = Color.RED;
		Color pointColor = Color.YELLOW;
		Color fontColor = Color.BLACK;
		Color bubbleColor = new Color(255, 255, 255, 180);
		
		if (!isEnabled()) {
			shadowColor = Color.GRAY;
			arcColor = Color.DARK_GRAY;
			pointColor = Color.DARK_GRAY;
		}
		
		
		Graphics2D g2d = (Graphics2D)g;
		if (prerendered != null) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
			//g2d.setColor(background);
			//g2d.fillRect(0, 0, getWidth(), getHeight());
			
			int backgroundSize = (getWidth() > getHeight()) ? getWidth() : getHeight();
			int size = (getWidth() < getHeight()) ? getWidth() : getHeight();

			
			
			//g2d.drawImage(prerendered, drawX, drawY, size, size, this);
			if (prerendered != null) {
				g2d.drawImage(prerendered, (int) (((double)getWidth() / 2.0) - (backgroundSize / 2.0)),
						(int) (((double)getHeight() / 2.0) - (backgroundSize / 2.0)),
						backgroundSize, 
						backgroundSize, 
						this);
			}
			
			
			int drawX = (int) (((double)getWidth() / 2.0) - (size / 2.0));
			int drawY = (int) (((double)getHeight() / 2.0) - (size / 2.0));
			
			int xMid = (int)Math.round(((double)getWidth()/2.0));
			int yMid = (int)Math.round(((double)getHeight()/2.0));
			
			double radius = ((double)(size) / 2.0);
			
			//g2d.setColor(Color.DARK_GRAY);
			//g2d.drawOval(drawX+1, drawY+1, (int) size,  (int)size);
			
			
			
			
			double angle = this.getSolarAzimuth();

			double[] points = {0.0, 0.0};
			
			getPoints2D(angle, radius, points);
			double x = points[0];
			double y = points[1];
			
			double xP = x + xMid;
			double yP = drawY + y;
			
			// Line Shadows
			g2d.setColor(shadowColor);
			g2d.drawOval(drawX+1, drawY+1, (int) size,  (int)size);
			g2d.drawLine((int)xP+1, (int)yP+1, xMid+1, yMid+1);
			g2d.fillOval((int)Math.round(xMid - 5)+1, (int)Math.round(yMid - 5)+1, 10, 10);
			g2d.fillOval((int)Math.round(xP - 5)+1, (int)Math.round(yP - 5)+1, 10, 10);
			
			// Lines
			g2d.setColor(arcColor);
			g2d.drawOval(drawX, drawY, (int) size,  (int)size);
			g2d.drawLine((int)xP, (int)yP, xMid, yMid);
			g2d.fillOval((int)Math.round(xMid - 5), (int)Math.round(yMid - 5), 10, 10);
			g2d.fillOval((int)Math.round(xP - 5), (int)Math.round(yP - 5), 10, 10);
			
			double pctElev = 1.0 - ( this.getSolarElevation() / 90.0);
			
			
			double elevRadius = radius*pctElev;
			size = (int) Math.round((double) size * pctElev);

			getPoints2D(angle, elevRadius , points);
			
			
			x = points[0];
			y = points[1];
			
			xP = x + xMid;
			yP = y + (((double)getHeight() / 2.0) - (size / 2.0));

			g2d.setColor(shadowColor);
			g2d.drawOval((int)Math.round(xP - 5), (int)Math.round(yP - 5), 10, 10);
			
			g2d.setColor(pointColor);
			g2d.fillOval((int)Math.round(xP - 5), (int)Math.round(yP - 5), 10, 10);


			int iAzimuth = (int)Math.round(getSolarAzimuth());
			int iElevation = (int) Math.round(getSolarElevation());
			
			String label = "" + iAzimuth + "\u00B0, " + iElevation + "\u00B0";
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int lblWidth = fontMetrics.stringWidth(label);
			int lblHeight = fontMetrics.getHeight();
			
			int lblX = 0;
			int lblY = (int)Math.round(yP + 5);
			if (xP < xMid) {
				lblX = (int)Math.round(xP + 7);
			} else {
				lblX = (int)Math.round(xP - 7 - lblWidth - 8);
			}
			
			g2d.setColor(shadowColor);
			g2d.drawRoundRect(lblX, (int)Math.round(yP - (lblHeight / 2.0)), lblWidth + 8, lblHeight, 7, 7);
			
			g2d.setColor(bubbleColor);
			g2d.fillRoundRect(lblX, (int)Math.round(yP - (lblHeight / 2.0)), lblWidth + 8, lblHeight, 7, 7);
			
			g2d.setColor(fontColor);
			g2d.drawString(label, lblX + 4, lblY);
			
			
			
			
			
		} else {
			log.warn("No preview to render");
		}
	}
	
	public double getSolarAzimuth()
	{
		return solarAzimuth;
	}


	public void setSolarAzimuth(double solarAzimuth)
	{
		this.solarAzimuth = solarAzimuth;
		modelOptions.setLightingAzimuth(solarAzimuth);
	}


	public double getSolarElevation()
	{
		return solarElevation;
	}


	public void setSolarElevation(double solarElevation)
	{
		this.solarElevation = solarElevation;
		modelOptions.setLightingElevation(solarElevation);
	}

	
	
	public void addChangeListener(ChangeListener listener)
	{
		changeListeners.add(listener);
	}
	
	public boolean removeChangeListener(ChangeListener listener)
	{
		return changeListeners.remove(listener);
	}
	
	public void fireChangeListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		
		for (ChangeListener listener : changeListeners) {
			listener.stateChanged(event);
		}
	}

	protected double asin(double a)
	{
		return Math.asin(a);
	}
	
	protected double atan2(double a, double b)
	{
		return Math.atan2(a, b);
	}
	
	protected double sqr(double a)
	{
		return (a*a);
	}
	
	protected double abs(double a)
	{
		return Math.abs(a);
	}
	
	protected double pow(double a, double b)
	{
		return Math.pow(a, b);
	}
	
	protected double sqrt(double d)
	{
		return Math.sqrt(d);
	}
	
	protected double cos(double d)
	{
		return Math.cos(Math.toRadians(d));
	}
	
	protected double sin(double d)
	{
		return Math.sin(Math.toRadians(d));
	}


	@Override
	public void dispose() throws ComponentException
	{
		if (isDisposed) {
			throw new ComponentException("Object already disposed of");
		}
		
		log.info("Disposing of Lighting Preview Pane");
		
		try {
			dataPackage.dispose();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new ComponentException("Data source already disposed of", e);
			
		}
		dataPackage = null;
		
		isDisposed = true;
		
		super.dispose();
	}
}
