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

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.util.UniqueIdentifierUtil;

/** Provides a unique context environment for each modeling task
 * 
 * @author Kevin M. Gill
 *
 */
public class ModelContext
{

	private RasterDataContext rasterDataContext;
	private ShapeDataContext shapeDataContext;
	private ModelOptions modelOptions;
	private ScriptProxy scriptProxy;
	private String contextId;
	
	
	
	protected ModelContext(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ModelOptions modelOptions, ScriptProxy scriptProxy, String contextId)
	{
		this.rasterDataContext = rasterDataContext;
		this.shapeDataContext = shapeDataContext;
		this.modelOptions = modelOptions;
		this.scriptProxy = scriptProxy;
	}

	public RasterDataContext getRasterDataContext()
	{
		return rasterDataContext;
	}
	


	public ShapeDataContext getShapeDataContext()
	{
		return shapeDataContext;
	}

	public ModelOptions getModelOptions()
	{
		return modelOptions;
	}

	public ScriptProxy getScriptProxy()
	{
		return scriptProxy;
	}

	public String getContextId()
	{
		return contextId;
	}
	
	public double getNorth()
	{
		// TODO: Add shape data dimensions once supported
		if (rasterDataContext != null) {
			return rasterDataContext.getNorth();
		} else {
			return 90.0;
		}
	}
	
	public double getSouth()
	{
		// TODO: Add shape data dimensions once supported
		if (rasterDataContext != null) {
			return rasterDataContext.getSouth();
		} else {
			return -90.0;
		}	
	}
	
	public double getEast()
	{
		// TODO: Add shape data dimensions once supported
		if (rasterDataContext != null) {
			return rasterDataContext.getEast();
		} else {
			return 180.0;
		}
	}
	
	public double getWest()
	{
		// TODO: Add shape data dimensions once supported
		if (rasterDataContext != null) {
			return rasterDataContext.getWest();
		} else {
			return -180.0;
		}
	}
	
	public ModelContext copy() throws DataSourceException
	{
		
		RasterDataContext rasterDataCopy = (rasterDataContext == null) ? null : rasterDataContext.copy();
		ShapeDataContext shapeDataCopy = (shapeDataContext == null) ? null : shapeDataContext.copy();
		ModelOptions modelOptionsCopy = (modelOptions == null) ? null : modelOptions.copy();
		
		// TODO: Implement script proxy copy
		ScriptProxy scriptProxyCopy = null;//(scriptProxy == null) ? null : scriptProxy.copy();
		
		ModelContext clone = ModelContext.createInstance(rasterDataCopy, shapeDataCopy, modelOptionsCopy, scriptProxyCopy);
		return clone;

	}
	
	

	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ModelOptions modelOptions)
	{
		return ModelContext.createInstance(rasterDataContext, null, modelOptions, null);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ModelOptions modelOptions)
	{
		return ModelContext.createInstance(rasterDataContext, shapeDataContext, modelOptions, null);
	}
	
	public static ModelContext createInstance(RasterDataContext rasterDataContext, ShapeDataContext shapeDataContext, ModelOptions modelOptions, ScriptProxy scriptProxy)
	{
		String contextId = ModelContext.generateContextId();
		ModelContext modelContext = new ModelContext(rasterDataContext, shapeDataContext, modelOptions, scriptProxy, contextId);
		
		return modelContext;
	}
	

	
	
	protected static String generateContextId()
	{
		return UniqueIdentifierUtil.getNewIdentifier();
	}
}
