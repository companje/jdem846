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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.ComponentException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.math.Spheres;
import us.wthr.jdem846.model.AzimuthElevationAngles;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorOptionModel;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorProcessor;
import us.wthr.jdem846.model.processing.coloring.TopographicPositionIndexColoringProcessor;
import us.wthr.jdem846.model.processing.dataload.GridLoadOptionModel;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsOptionModel;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor;
import us.wthr.jdem846.model.processing.render.ModelRenderOptionModel;
import us.wthr.jdem846.model.processing.render.ModelRenderer;
import us.wthr.jdem846.model.processing.shading.HillshadingOptionModel;
import us.wthr.jdem846.model.processing.shading.HillshadingProcessor;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.render.simple.SimpleRenderer;
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
	

	private RasterDataContext rasterDataContext;
	private ModelContext modelContext;
	private ModelBuilder modelBuilder;
	private ModelProcessManifest modelProcessManifest;

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
				//onMouseLocation(e.getX(), e.getY(), true);
			}
			
		};
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);

		try {
			File tmpGridFloatData = TempFiles.getTemporaryFile("lghtprv", ".flt", JDem846Properties.getProperty("us.wthr.jdem846.previewData") + "/raster-data.flt");
			
			File tmpTempGridFloatHeader = TempFiles.getTemporaryFile("lghtprv", ".hdr", JDem846Properties.getProperty("us.wthr.jdem846.previewData") + "/raster-data.hdr");
			
			String tmpHdrPath = tmpGridFloatData.getAbsolutePath();
			tmpHdrPath = tmpHdrPath.replaceAll("\\.flt", ".hdr");
			log.info("New Header Path: " + tmpHdrPath);
			File tmpGridFloatHeader = new File(tmpHdrPath);
			
			tmpTempGridFloatHeader.renameTo(tmpGridFloatHeader);
			
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(tmpGridFloatData.getAbsolutePath());
			rasterDataContext = new RasterDataContext();
			rasterDataContext.addRasterData(rasterData);
			rasterDataContext.prepare();
			rasterDataContext.fillBuffers();
			
			
			
			GlobalOptionModel globalOptionModel = new GlobalOptionModel();
			
			GridLoadOptionModel gridLoadOptionModel = new GridLoadOptionModel();
			SurfaceNormalsOptionModel surfaceNormalOptionModel = new SurfaceNormalsOptionModel();
			HypsometricColorOptionModel hypsometricColorOptionModel = new HypsometricColorOptionModel();
			HillshadingOptionModel hillshadingOptionModel = new HillshadingOptionModel();
			ModelRenderOptionModel modelRenderOptionModel = new ModelRenderOptionModel();
			
			double quality = JDem846Properties.getDoubleProperty("us.wthr.jdem846.ui.lightingPreviewPanel.previewQuality");
			double latitudeSlices = quality * (double) rasterData.getRows();
			double longitudeSlices = quality * (double) rasterData.getColumns();
			
			globalOptionModel.setUseScripting(false);
			globalOptionModel.setBackgroundColor(new RgbaColor(0, 0, 0, 0));
			globalOptionModel.setWidth(300);
			globalOptionModel.setHeight(300);
			globalOptionModel.setLatitudeSlices(latitudeSlices);
			globalOptionModel.setLongitudeSlices(longitudeSlices);
			globalOptionModel.setRenderProjection(CanvasProjectionTypeEnum.PROJECT_FLAT.identifier());
			globalOptionModel.setElevationMultiple(3.0);
			
			hypsometricColorOptionModel.setColorTint(JDem846Properties.getProperty("us.wthr.jdem846.ui.lightingPreviewPanel.previewColoring"));
			
			hillshadingOptionModel.setLightIntensity(0.75);
			hillshadingOptionModel.setDarkIntensity(1.0);
			hillshadingOptionModel.setRayTraceShadows(false);
			hillshadingOptionModel.setLightMultiple(5.0);
			
			modelRenderOptionModel.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR.identifier());
			
			
			modelProcessManifest = new ModelProcessManifest();
			modelProcessManifest.setGlobalOptionModel(globalOptionModel);
			
			modelProcessManifest.addProcessor(new GridLoadProcessor(), gridLoadOptionModel);
			modelProcessManifest.addProcessor(new SurfaceNormalsProcessor(), surfaceNormalOptionModel);
			modelProcessManifest.addProcessor(new HypsometricColorProcessor(), hypsometricColorOptionModel);
			modelProcessManifest.addProcessor(new HillshadingProcessor(), hillshadingOptionModel);
			modelProcessManifest.addProcessor(new ModelRenderer(), modelRenderOptionModel);
			
			modelContext = ModelContext.createInstance(rasterDataContext, modelProcessManifest);
			modelContext.updateContext(true, globalOptionModel.isEstimateElevationRange());

			log.info("Initializing model builder...");
			modelBuilder = new ModelBuilder();
			modelBuilder.prepare(modelContext, modelProcessManifest);

			modelContext.updateContext(true, false);

		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		
	}
	
	
	protected void onMouseLocation(int x, int y, boolean updatePreview)
	{
		if (!isEnabled())
			return;
		
		
		int size = (getWidth() < getHeight()) ? getWidth() : getHeight();
		
		int xMid = (int)MathExt.round(((double)getWidth()/2.0));
		int yMid = (int)MathExt.round(((double)getHeight()/2.0));
		
		double mX = x - xMid;
		double mY = y - yMid;
		double radius = MathExt.sqrt(MathExt.sqr(MathExt.abs(mX)) + MathExt.sqr(MathExt.abs(mY)));
		double angle = MathExt.degrees(MathExt.asin(MathExt.abs(mY) / radius));
		
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

			
			try {
				log.info("Updating lighting preview model image");
				log.info("****************************************");
	
				modelBuilder.prepare(modelContext, modelProcessManifest);
				modelBuilder.process();
				
				prerendered = (BufferedImage) modelContext.getModelCanvas().getImage();

			} catch (Exception e) {
				log.warn("Failed to render preview image: " + e.getMessage(), e);
				e.printStackTrace();
			} finally {
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

			int backgroundSize = (getWidth() > getHeight()) ? getWidth() : getHeight();
			int size = (getWidth() < getHeight()) ? getWidth() : getHeight();
			size -= 4;
			
			if (prerendered != null) {
				g2d.drawImage(prerendered,
								-2, -2,
								getWidth() + 4, getHeight() + 4,
								null);
			}
			
			
			int drawX = (int) (((double)getWidth() / 2.0) - (size / 2.0));
			int drawY = (int) (((double)getHeight() / 2.0) - (size / 2.0));
			
			int xMid = (int)MathExt.round(((double)getWidth()/2.0));
			int yMid = (int)MathExt.round(((double)getHeight()/2.0));
			
			double radius = ((double)(size) / 2.0);
			
			
			double angle = this.getSolarAzimuth();

			double[] points = {0.0, 0.0};
			
			Spheres.getPoints2D(angle, radius, points);
			double x = points[0];
			double y = points[1];
			
			double xP = x + xMid;
			double yP = drawY + y;
			
			// Line Shadows
			g2d.setColor(shadowColor);
			g2d.drawOval(drawX+1, drawY+1, (int) size,  (int)size);
			g2d.drawLine((int)xP+1, (int)yP+1, xMid+1, yMid+1);
			g2d.fillOval((int)MathExt.round(xMid - 5)+1, (int)MathExt.round(yMid - 5)+1, 10, 10);
			g2d.fillOval((int)MathExt.round(xP - 5)+1, (int)MathExt.round(yP - 5)+1, 10, 10);
			
			// Lines
			g2d.setColor(arcColor);
			g2d.drawOval(drawX, drawY, (int) size,  (int)size);
			g2d.drawLine((int)xP, (int)yP, xMid, yMid);
			g2d.fillOval((int)MathExt.round(xMid - 5), (int)MathExt.round(yMid - 5), 10, 10);
			g2d.fillOval((int)MathExt.round(xP - 5), (int)MathExt.round(yP - 5), 10, 10);
			
			double pctElev = 1.0 - ( this.getSolarElevation() / 90.0);
			
			
			double elevRadius = radius*pctElev;
			size = (int) MathExt.round((double) size * pctElev);

			Spheres.getPoints2D(angle, elevRadius , points);
			
			
			x = points[0];
			y = points[1];
			
			xP = x + xMid;
			yP = y + (((double)getHeight() / 2.0) - (size / 2.0));

			g2d.setColor(shadowColor);
			g2d.drawOval((int)MathExt.round(xP - 5), (int)MathExt.round(yP - 5), 10, 10);
			
			g2d.setColor(pointColor);
			g2d.fillOval((int)MathExt.round(xP - 5), (int)MathExt.round(yP - 5), 10, 10);


			int iAzimuth = (int)MathExt.round(getSolarAzimuth());
			int iElevation = (int) MathExt.round(getSolarElevation());
			
			String label = "" + iAzimuth + "\u00B0, " + iElevation + "\u00B0";
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int lblWidth = fontMetrics.stringWidth(label);
			int lblHeight = fontMetrics.getHeight();
			
			int lblX = 0;
			int lblY = (int)MathExt.round(yP + 5);
			if (xP < xMid) {
				lblX = (int)MathExt.round(xP + 7);
			} else {
				lblX = (int)MathExt.round(xP - 7 - lblWidth - 8);
			}
			
			g2d.setColor(shadowColor);
			g2d.drawRoundRect(lblX, (int)MathExt.round(yP - (lblHeight / 2.0)), lblWidth + 8, lblHeight, 7, 7);
			
			g2d.setColor(bubbleColor);
			g2d.fillRoundRect(lblX, (int)MathExt.round(yP - (lblHeight / 2.0)), lblWidth + 8, lblHeight, 7, 7);
			
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
		updateAzimuthElevationAngles();
	}


	public double getSolarElevation()
	{
		return solarElevation;
	}


	public void setSolarElevation(double solarElevation)
	{
		this.solarElevation = solarElevation;
		updateAzimuthElevationAngles();
	}

	protected void updateAzimuthElevationAngles()
	{
		AzimuthElevationAngles angles = new AzimuthElevationAngles(solarAzimuth, solarElevation);
		
		try {
			modelProcessManifest.setPropertyById("us.wthr.jdem846.model.HillshadingOptionModel.sourceLocation", angles);
		} catch (ModelContainerException ex) {
			log.error("Error setting azimuth/elevation angle options: " + ex.getMessage(), ex);
		}
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


	@Override
	public void dispose() throws ComponentException
	{
		if (isDisposed) {
			throw new ComponentException("Object already disposed of");
		}
		
		log.info("Disposing of Lighting Preview Pane");
		
		try {
			rasterDataContext.dispose();
		} catch (DataSourceException e) {
			e.printStackTrace();
			throw new ComponentException("Data source already disposed of", e);
			
		}
		rasterDataContext = null;
		
		isDisposed = true;
		
		super.dispose();
	}
}
