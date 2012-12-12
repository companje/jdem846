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

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.gis.elevation.ElevationMinMax;
import us.wthr.jdem846.gis.elevation.ElevationMinMaxEstimation;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.gis.projections.MapProjection;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.CancelIndicator;
import us.wthr.jdem846.model.ModelGridDimensions;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.exceptions.ProcessContainerException;
import us.wthr.jdem846.modelgrid.ModelGridContext;
import us.wthr.jdem846.rasterdata.ElevationMinMaxCalculator;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptingContext;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.util.UniqueIdentifierUtil;

/**
 * Provides a unique context environment for each modeling task
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
	private ScriptingContext scriptingContext;
	private ModelGridContext modelGridContext;

	private ModelProcessManifest modelProcessManifest;

	private String contextId;

	private MapProjection mapProjection;
	private ModelDimensions modelDimensions;

	private double northLimit = NOT_SET;
	private double southLimit = NOT_SET;
	private double eastLimit = NOT_SET;
	private double westLimit = NOT_SET;

	private boolean isDisposed = false;

	protected ModelContext(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ImageDataContext imageDataContext, ModelGridContext modelGridContext, ModelProcessManifest modelProcessManifest, ScriptingContext scriptingContext,
			String contextId)
	{
		this.rasterDataContext = rasterDataContext;
		this.shapeDataContext = shapeDataContext;
		this.imageDataContext = imageDataContext;
		this.modelGridContext = modelGridContext;
		this.modelProcessManifest = modelProcessManifest;
		this.scriptingContext = scriptingContext;

		if (this.modelGridContext == null) {
			this.modelGridContext = new ModelGridContext();
		}

		this.contextId = contextId;
	}

	public void updateContext() throws ModelContextException
	{
		updateContext(false, false, null);
	}

	public void updateContext(boolean updateDataMinMax, boolean estimateMinMax) throws ModelContextException
	{
		updateContext(updateDataMinMax, estimateMinMax, null);
	}

	public void updateContext(boolean updateDataMinMax, boolean estimateMinMax, CancelIndicator cancelIndicator) throws ModelContextException
	{

		if (this.getModelProcessManifest() != null) {
			modelDimensions = ModelGridDimensions.getModelDimensions(this);
		}

		rasterDataContext.setEffectiveLatitudeResolution(modelDimensions.textureLatitudeResolution);
		rasterDataContext.setEffectiveLongitudeResolution(modelDimensions.textureLongitudeResolution);

		if (updateDataMinMax) {

			Planet planet = null;

			if (this.getModelProcessManifest() != null) {
				planet = PlanetsRegistry.getPlanet(getModelProcessManifest().getGlobalOptionModel().getPlanet());
			}

			if (estimateMinMax && planet != null && planet.getElevationSamplesPath() != null) {

				log.info("Fetching estimated elevation min/max");
				try {
					ElevationMinMaxEstimation est = ElevationMinMaxEstimation.load(planet);
					ElevationMinMax minMax = est.getMinMax(getNorth(), getSouth(), getEast(), getWest());

					getRasterDataContext().setDataMaximumValue(minMax.getMaximumElevation());
					getRasterDataContext().setDataMinimumValue(minMax.getMinimumElevation());

					log.info("Maximum: " + minMax.getMaximumElevation());
					log.info("Minimum: " + minMax.getMinimumElevation());

				} catch (Exception ex) {
					throw new ModelContextException("Error with elevation min/max estimation: " + ex.getMessage(), ex);
				}

			} else {

				log.info("Fetching calculated elevation min/max");

				try {

					ElevationMinMaxCalculator minMaxCalc = new ElevationMinMaxCalculator(this, cancelIndicator);
					ElevationMinMax minMax = minMaxCalc.calculateMinAndMax();

					getRasterDataContext().setDataMaximumValue(minMax.getMaximumElevation());
					getRasterDataContext().setDataMinimumValue(minMax.getMinimumElevation());

				} catch (DataSourceException ex) {
					log.error("Error determining elevation min & max: " + ex.getMessage(), ex);
				}
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

		if (!scriptingContext.isDisposed()) {
			scriptingContext.dispose();
		}
	}

	public MapProjection getMapProjection()
	{
		return mapProjection;
	}

	public ModelDimensions getModelDimensions()
	{
		return modelDimensions;
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

	public ModelProcessManifest getModelProcessManifest()
	{
		return modelProcessManifest;
	}

	public void setModelProcessManifest(ModelProcessManifest modelProcessManifest)
	{
		this.modelProcessManifest = modelProcessManifest;
	}

	public ModelGridContext getModelGridContext()
	{
		return modelGridContext;
	}

	public void setModelGridContext(ModelGridContext modelGridContext)
	{
		this.modelGridContext = modelGridContext;
	}

	public ScriptingContext getScriptingContext()
	{
		return scriptingContext;
	}

	public void setScriptingContext(ScriptingContext scriptingContext)
	{
		this.scriptingContext = scriptingContext;
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
			if (rasterDataContext != null && rasterDataContext.getRasterDataListSize() > 0) {
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
			if (rasterDataContext != null && rasterDataContext.getRasterDataListSize() > 0) {
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
			if (rasterDataContext != null && rasterDataContext.getRasterDataListSize() > 0) {
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
			if (rasterDataContext != null && rasterDataContext.getRasterDataListSize() > 0) {
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
		// ModelOptions modelOptionsCopy = (modelOptions == null) ? null :
		// modelOptions.copy();
		ImageDataContext imageDataCopy = (imageDataContext == null) ? null : imageDataContext.copy();
		ScriptingContext scriptingContextCopy = (scriptingContext == null) ? null : scriptingContext.copy();

		ModelGridContext modelGridContextCopy = (modelGridContext == null) ? null : modelGridContext.copy();

		ModelProcessManifest modelProcessManifestCopy = null;

		try {
			modelProcessManifestCopy = (modelProcessManifest == null) ? null : modelProcessManifest.copy();
		} catch (ProcessContainerException ex) {
			throw new DataSourceException("Error creating copy of model process manifest: " + ex.getMessage(), ex);
		}

		ModelContext clone = null;

		try {
			clone = ModelContext.createInstance(rasterDataCopy, shapeDataCopy, imageDataCopy, modelGridContextCopy, modelProcessManifestCopy, scriptingContextCopy);
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
		return clone;

	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ModelProcessManifest modelProcessManifest) throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, null, null, null, modelProcessManifest, null, contextId);
		modelContext.updateContext();
		return modelContext;
	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ModelProcessManifest modelProcessManifest, ScriptingContext scriptingContext) throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, null, null, null, modelProcessManifest, scriptingContext, contextId);
		modelContext.updateContext();
		return modelContext;
	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ImageDataContext imageDataContext, ModelProcessManifest modelProcessManifest) throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, null, imageDataContext, null, modelProcessManifest, null, contextId);
		modelContext.updateContext();
		return modelContext;
	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ImageDataContext imageDataContext, ModelGridContext modelGridContext, ModelProcessManifest modelProcessManifest, ScriptingContext scriptingContext)
			throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, null, imageDataContext, modelGridContext, modelProcessManifest, scriptingContext, contextId);
		modelContext.updateContext();
		return modelContext;
	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ImageDataContext imageDataContext, ModelGridContext modelGridContext, ModelProcessManifest modelProcessManifest)
			throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, shapeDataContext, imageDataContext, modelGridContext, modelProcessManifest, null, contextId);
		modelContext.updateContext();
		return modelContext;
	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ImageDataContext imageDataContext, ModelGridContext modelGridContext, ModelProcessManifest modelProcessManifest,
			ScriptingContext scriptingContext) throws ModelContextException
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, shapeDataContext, imageDataContext, modelGridContext, modelProcessManifest, scriptingContext, contextId);
		modelContext.updateContext();
		return modelContext;
	}

	protected static String generateContextId()
	{
		return UniqueIdentifierUtil.getNewIdentifier();
	}

	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ImageDataContext imageDataContext, ModelGridContext modelGridContext) throws ModelContextException
	{
		return createInstance(rasterDataContext, shapeDataContext, imageDataContext, modelGridContext, null);
	}
}
