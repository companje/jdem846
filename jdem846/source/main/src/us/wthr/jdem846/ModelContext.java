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

package us.wthr.jdem846;

import java.util.UUID;

import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.ScriptCompilationFailedException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.render.CancelIndicator;
import us.wthr.jdem846.render.ElevationMinMax;
import us.wthr.jdem846.render.ElevationMinMaxCalculator;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.gis.projections.MapProjectionProviderFactory;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.scripting.ScriptProxyFactory;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.util.UniqueIdentifierUtil;

/** Provides a unique context environment for each modeling task
 * 
 * @author Kevin M. Gill
 *
 */
public class ModelContext
{
	private static final double NOT_SET = DemConstants.ELEV_NO_DATA;
	private static Log log = Logging.getLog(ModelContext.class);
	
	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ImageDataContext imageDataContext;
	private ModelOptions modelOptions;
	private ScriptProxy scriptProxy;
	private LightingContext lightingContext;
	private String contextId;
	
	private MapProjection mapProjection;
	private ModelDimensions2D modelDimensions;
	private ModelCanvas modelCanvas = null;
	
	private double northLimit = NOT_SET;
	private double southLimit = NOT_SET;
	private double eastLimit = NOT_SET;
	private double westLimit = NOT_SET;
	

	private boolean isDisposed = false;
	
	protected ModelContext(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ImageDataContext imageDataContext, LightingContext lightingContext, ModelOptions modelOptions, ScriptProxy scriptProxy, String contextId)
	{
		this.rasterDataContext = rasterDataContext;
		this.shapeDataContext = shapeDataContext;
		this.imageDataContext = imageDataContext;
		this.modelOptions = modelOptions;
		this.scriptProxy = scriptProxy;
		this.lightingContext = lightingContext;
		
		this.contextId = contextId;
	}

	public void updateContext() throws ModelContextException
	{
		updateContext(false, null);
	}
	
	
	public void updateContext(boolean updateDataMinMax) throws ModelContextException
	{
		updateContext(updateDataMinMax, null);
	}
	
	public void updateContext(boolean updateDataMinMax, CancelIndicator cancelIndicator) throws ModelContextException
	{
		modelDimensions = ModelDimensions2D.getModelDimensions(this);
		
		try {
			
			mapProjection = MapProjectionProviderFactory.getMapProjection(this);
			
		} catch (MapProjectionException ex) {
			log.error("Failed to create map projection: " + ex.getMessage(), ex);
		}
		
		if (updateDataMinMax) {
			try {
				
				ElevationMinMaxCalculator minMaxCalc = new ElevationMinMaxCalculator(this, cancelIndicator);
				ElevationMinMax minMax = minMaxCalc.calculateMinAndMax();
				
				getRasterDataContext().setDataMaximumValue(minMax.maximum);
				getRasterDataContext().setDataMinimumValue(minMax.minimum);
				
			} catch (DataSourceException ex) {
				log.error("Error determining elevation min & max: " + ex.getMessage(), ex);
			}
		}
		
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	public void dispose(boolean disposeSubContexts) throws DataSourceException
	{
		log.info("Disposing model context");
		
		if (isDisposed()) {
			throw new DataSourceException("Model context already disposed.");
		}
		
		if (!rasterDataContext.isDisposed()) {
			rasterDataContext.dispose();
		}
		
		if (!imageDataContext.isDisposed()) {
			imageDataContext.dispose();
		}
		
		if (!shapeDataContext.isDisposed()) {
			shapeDataContext.dispose();
		}
		
	}
	
	
	public MapProjection getMapProjection()
	{
		return mapProjection;
	}
	
	public ModelDimensions2D getModelDimensions()
	{
		return modelDimensions;
	}
	
	public ModelCanvas createModelCanvas()
	{
		////log.info("****************************");
		//log.info("Creating Model Canvas!!");
		//log.info("****************************");
		return new ModelCanvas(this);
	}
	
	public ModelCanvas getModelCanvas()
	{
		return getModelCanvas(true);
	}
	
	public ModelCanvas getModelCanvas(boolean create)
	{
		if (this.modelCanvas == null && create) {
			this.modelCanvas = createModelCanvas();
		}
		return this.modelCanvas;
	}
	
	public void resetModelCanvas()
	{
		this.modelCanvas = null;
	}
	
	public void setRasterDataContext(RasterDataContext rasterDataContext)
	{
		this.rasterDataContext = rasterDataContext;
	}
	
	public RasterDataContext getRasterDataContext()
	{
		return rasterDataContext;
	}

	public ShapeDataContext getShapeDataContext()
	{
		return shapeDataContext;
	}

	public ImageDataContext getImageDataContext()
	{
		return imageDataContext;
	}

	public void setImageDataContext(ImageDataContext imageDataContext)
	{
		this.imageDataContext = imageDataContext;
	}


	public ModelOptions getModelOptions()
	{
		return modelOptions;
	}

	public ScriptProxy getScriptProxy()
	{
		return scriptProxy;
	}

	
	public LightingContext getLightingContext()
	{
		return lightingContext;
	}


	public String getContextId()
	{
		return contextId;
	}
	
	public void setNorthLimit(double northLimit)
	{
		this.northLimit = northLimit;
	}
	
	public double getNorth()
	{
		if (northLimit == NOT_SET) {
			// TODO: Add shape data dimensions once supported
			if (rasterDataContext != null) {
				return rasterDataContext.getNorth();
			} else {
				return 90.0;
			}
		} else {
			return northLimit;
		}
	}
	
	public void setSouthLimit(double southLimit)
	{
		this.southLimit = southLimit;
	}
	
	public double getSouth()
	{
		if (southLimit == NOT_SET) {
			// TODO: Add shape data dimensions once supported
			if (rasterDataContext != null) {
				return rasterDataContext.getSouth();
			} else {
				return -90.0;
			}	
		} else {
			return southLimit;
		}
	}
	
	public void setEastLimit(double eastLimit)
	{
		this.eastLimit = eastLimit;
	}
	
	public double getEast()
	{
		if (eastLimit == NOT_SET) {
			// TODO: Add shape data dimensions once supported
			if (rasterDataContext != null) {
				return rasterDataContext.getEast();
			} else {
				return 180.0;
			}
		} else {
			return eastLimit;
		}
	}
	
	public void setWestLimit(double westLimit)
	{
		this.westLimit = westLimit;
	}
	
	public double getWest()
	{
		if (westLimit == NOT_SET) {
			// TODO: Add shape data dimensions once supported
			if (rasterDataContext != null) {
				return rasterDataContext.getWest();
			} else {
				return -180.0;
			}
		} else {
			return westLimit;
		}
	}
	
	



	public ModelContext copy() throws DataSourceException
	{
		return copy(false);
	}
	
	
	public ModelContext copy(boolean withDependentCanvas) throws DataSourceException
	{
		
		RasterDataContext rasterDataCopy = (rasterDataContext == null) ? null : rasterDataContext.copy();
		ShapeDataContext shapeDataCopy = (shapeDataContext == null) ? null : shapeDataContext.copy();
		ModelOptions modelOptionsCopy = (modelOptions == null) ? null : modelOptions.copy();
		ImageDataContext imageDataCopy = (imageDataContext == null) ? null : imageDataContext.copy();
		
		LightingContext lightingContextCopy = (lightingContext == null) ? null : lightingContext.copy();
		ModelCanvas modelCanvasCopy = null;
		
		try {
			if (withDependentCanvas) {
				modelCanvasCopy = getModelCanvas(true).getDependentHandle();
			}
		} catch (CanvasException ex) {
			throw new DataSourceException("Error creating dependent canvas handle: " + ex.getMessage(), ex);
		}
		
		// TODO: Implement script proxy copy
		//ScriptProxy scriptProxyCopy = null;//(scriptProxy == null) ? null : scriptProxy.copy();
		ScriptProxy scriptProxyCopy = null;
		
		if (modelOptions.getUserScript() != null && modelOptions.getUserScript().length() > 0) {
			try {
				scriptProxyCopy = ScriptProxyFactory.createScriptProxy(modelOptions.getScriptLanguage(), modelOptions.getUserScript());
			} catch (Exception ex) {
				throw new DataSourceException("Failed to recompile script: " + ex.getMessage(), ex);
			} 
		}
		
		
		ModelContext clone = null;
		
		
		try {
			clone = ModelContext.createInstance(rasterDataCopy, shapeDataCopy, imageDataCopy, lightingContextCopy, modelOptionsCopy, scriptProxyCopy);
		} catch (ModelContextException ex) {
			throw new DataSourceException("Error creating model context: " + ex.getMessage(), ex);
		}
		
		
		
		clone.northLimit = this.northLimit;
		clone.southLimit = this.southLimit;
		clone.eastLimit = this.eastLimit;
		clone.westLimit = this.westLimit;
		if (this.modelDimensions != null) {
			clone.modelDimensions = this.modelDimensions.copy();
		}
		clone.modelCanvas = modelCanvasCopy;
		return clone;

	}
	
	

	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ModelOptions modelOptions) throws ModelContextException
	{
		return ModelContext.createInstance(rasterDataContext, null, null, null, modelOptions, null);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, LightingContext lightingContext, ModelOptions modelOptions) throws ModelContextException
	{
		return ModelContext.createInstance(rasterDataContext, null, null, lightingContext, modelOptions, null);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, LightingContext lightingContext, ModelOptions modelOptions, ScriptProxy scriptProxy) throws ModelContextException
	{
		return ModelContext.createInstance(rasterDataContext, null, null, lightingContext, modelOptions, scriptProxy);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ModelOptions modelOptions) throws ModelContextException
	{
		return ModelContext.createInstance(rasterDataContext, shapeDataContext, null, null, modelOptions, null);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, LightingContext lightingContext, ModelOptions modelOptions) throws ModelContextException
	{
		return ModelContext.createInstance(rasterDataContext, shapeDataContext, null, lightingContext, modelOptions, null);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ImageDataContext imageDataContext, LightingContext lightingContext, ModelOptions modelOptions, ScriptProxy scriptProxy) throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, shapeDataContext, imageDataContext, lightingContext, modelOptions, scriptProxy, contextId);
		modelContext.updateContext();
		return modelContext;
	}
	

	
	
	protected static String generateContextId()
	{
		return UniqueIdentifierUtil.getNewIdentifier();
	}


	public static ModelContext createInstance(
			RasterDataContext rasterDataContext,
			ShapeDataContext shapeDataContext,
			ImageDataContext imageDataContext,
			LightingContext lightingContext, 
			ModelOptions modelOptions) throws ModelContextException
	{
		return createInstance(rasterDataContext, 
								shapeDataContext, 
								imageDataContext, 
								lightingContext, 
								modelOptions, 
								null);
	}
}
