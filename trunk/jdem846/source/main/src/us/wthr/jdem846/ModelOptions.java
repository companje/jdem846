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

import java.awt.Color;

import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.render.CanvasProjectionTypeEnum;
import us.wthr.jdem846.render.scaling.ElevationScalerEnum;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.util.ColorSerializationUtil;

/** Options for model processing.
 * 
 * @author Kevin M. Gill
 *
 */
public class ModelOptions extends MappedOptions
{
	public static final int SPOT_EXPONENT_MINIMUM = 1;
	public static final int SPOT_EXPONENT_MAXIMUM = 5;
	
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = null;
	
	private Projection projection = new Projection();
	private String writeTo = null;
	
	public ModelOptions()
	{
		
		addOptionPrefix("us.wthr.jdem846.modelOptions");
		
		for (ModelOptionNamesEnum optionName : ModelOptionNamesEnum.values()) {
			String property = JDem846Properties.getProperty(optionName.optionName());
			if (property != null) {
				setOption(optionName.optionName(), property);
			}
		}

		String scriptLanguageString = JDem846Properties.getProperty(ModelOptionNamesEnum.USER_SCRIPT_LANGUAGE.optionName());
		setScriptLanguage(scriptLanguageString);
		
		this.projection.setRotateX(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X));
		this.projection.setRotateY(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y));
		this.projection.setRotateZ(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z));
		
		this.projection.setShiftX(getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X));
		this.projection.setShiftY(getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y));
		this.projection.setShiftZ(getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z));
		
		this.projection.setZoom(getDoubleOption(ModelOptionNamesEnum.PROJECTION_ZOOM));
		
	}
	

	
	public void setOption(ModelOptionNamesEnum name, Object value)
	{
		setOption(name.optionName(), value);
	}
	

	
	public String getOption(ModelOptionNamesEnum name)
	{
		return getOption(name.optionName());
	}

	
	public boolean hasOption(ModelOptionNamesEnum name)
	{
		return hasOption(name.optionName());
	}

	
	public String removeOption(ModelOptionNamesEnum name)
	{
		return removeOption(name.optionName());
	}

	
	public boolean getBooleanOption(ModelOptionNamesEnum name)
	{
		return getBooleanOption(name.optionName());
	}
	

	
	public int getIntegerOption(ModelOptionNamesEnum name)
	{
		return getIntegerOption(name.optionName());
	}

	
	public double getDoubleOption(ModelOptionNamesEnum name)
	{
		return getDoubleOption(name.optionName());
	}

	
	public float getFloatOption(ModelOptionNamesEnum name)
	{
		return getFloatOption(name.optionName());
	}

	
	public long getLongOption(ModelOptionNamesEnum name)
	{
		return getLongOption(name.optionName());
	}

	
	/** Synchronizes values from this object to a ProjectModel object.
	 * 
	 * @param projectModel A ProjectModel to synchronize to
	 */
	public void syncToProjectModel(ProjectModel projectModel)
	{
		super.syncToProjectModel(projectModel);
		
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X, projection.getRotateX());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y, projection.getRotateY());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z, projection.getRotateZ());
		
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X, projection.getShiftX());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y, projection.getShiftY());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z, projection.getShiftZ());
		
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ZOOM, projection.getZoom());
		
		projectModel.setUserScript(getUserScript());
		projectModel.setScriptLanguage(getScriptLanguage());
		
	}
	
	/** Synchronizes values from a ProjectModel object to this object.
	 * 
	 * @param projectModel A ProjectModel to synchronize from.
	 */
	public void syncFromProjectModel(ProjectModel projectModel)
	{
		super.syncFromProjectModel(projectModel);
		
		if (projectModel.getUserScript() != null) {
			this.setUserScript(projectModel.getUserScript());
		}
		
		if (projectModel.getScriptLanguage() != null) {
			this.setScriptLanguage(projectModel.getScriptLanguage());
		}
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X))
			this.projection.setRotateX(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X));
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y))
			this.projection.setRotateY(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y));
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y))
			this.projection.setRotateZ(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z));
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X))
			this.projection.setShiftX(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X));
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y))
			this.projection.setShiftY(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y));
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z))
			this.projection.setShiftZ(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z));
		
		if (projectModel.hasOption(ModelOptionNamesEnum.PROJECTION_ZOOM))
			this.projection.setZoom(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ZOOM));
	}
	
	
	public String getEngine() 
	{
		return getOption(ModelOptionNamesEnum.ENGINE);
	}

	public void setEngine(String engine)
	{
		setOption(ModelOptionNamesEnum.ENGINE, engine);
	}

	/*
	public double getLightingMultiple() 
	{
		return getDoubleOption(ModelOptionNamesEnum.LIGHTING_MULTIPLE);
	}


	public void setLightingMultiple(double lightingMultiple)
	{
		setOption(ModelOptionNamesEnum.LIGHTING_MULTIPLE, lightingMultiple);
	}


	public double getRelativeLightIntensity()
	{
		return getDoubleOption(ModelOptionNamesEnum.RELATIVE_LIGHT_INTENSITY);
	}


	public void setRelativeLightIntensity(double relativeLightIntensity)
	{
		setOption(ModelOptionNamesEnum.RELATIVE_LIGHT_INTENSITY, relativeLightIntensity);
	}


	public double getRelativeDarkIntensity()
	{
		return getDoubleOption(ModelOptionNamesEnum.RELATIVE_DARK_INTENSITY);
	}


	public void setRelativeDarkIntensity(double relativeDarkIntensity)
	{
		setOption(ModelOptionNamesEnum.RELATIVE_DARK_INTENSITY, relativeDarkIntensity);
	}


	public int getSpotExponent()
	{
		return getIntegerOption(ModelOptionNamesEnum.SPOT_EXPONENT);
	}
	
	
	public void setSpotExponent(int spotExponent)
	{
		setOption(ModelOptionNamesEnum.SPOT_EXPONENT, spotExponent);
	}

	public double getLightingAzimuth()
	{
		return getDoubleOption(ModelOptionNamesEnum.LIGHTING_AZIMUTH);
	}


	public void setLightingAzimuth(double lightingAzimuth)
	{
		setOption(ModelOptionNamesEnum.LIGHTING_AZIMUTH, lightingAzimuth);
	}

	public double getLightingElevation()
	{
		return getDoubleOption(ModelOptionNamesEnum.LIGHTING_ELEVATION);
	}




	public void setLightingElevation(double lightingElevation)
	{
		setOption(ModelOptionNamesEnum.LIGHTING_ELEVATION, lightingElevation);
	}
	*/


	/*
	public int getTileSize() 
	{
		return getIntegerOption(ModelOptionNamesEnum.TILE_SIZE);
	}

	public void setTileSize(int tileSize) 
	{
		setOption(ModelOptionNamesEnum.TILE_SIZE, tileSize);
	}
	*/
	


	public String getBackgroundColor()
	{
		return getOption(ModelOptionNamesEnum.BACKGROUND_COLOR);
	}

	public Color getBackgroundColorInstance()
	{
		String colorString = getBackgroundColor();
		if (colorString != null) {
			return ColorSerializationUtil.stringToColor(colorString);
		} else {
			return null;
		}
	}


	public void setBackgroundColor(String backgroundColor)
	{
		setOption(ModelOptionNamesEnum.BACKGROUND_COLOR, backgroundColor);
	}

	public void setBackgroundColor(Color backgroundColor)
	{
		setOption(ModelOptionNamesEnum.BACKGROUND_COLOR, ColorSerializationUtil.colorToString(backgroundColor));
	}


	public boolean isHillShading() 
	{
		return getBooleanOption(ModelOptionNamesEnum.HILLSHADING);
	}


	public void setHillShading(boolean hillShading) 
	{
		setOption(ModelOptionNamesEnum.HILLSHADING, hillShading);
	}

	/*
	public int getHillShadeType()
	{
		return getIntegerOption(ModelOptionNamesEnum.HILLSHADE_TYPE);
	}


	public void setHillShadeType(int hillShadeType) 
	{
		setOption(ModelOptionNamesEnum.HILLSHADE_TYPE, hillShadeType);
	}
	*/

	/*
	public boolean getDoublePrecisionHillshading()
	{
		return getBooleanOption(ModelOptionNamesEnum.DOUBLE_PRECISION_HILLSHADING);
	}

	public void setDoublePrecisionHillshading(boolean doublePrecisionHillshading)
	{
		setOption(ModelOptionNamesEnum.DOUBLE_PRECISION_HILLSHADING, doublePrecisionHillshading);
	}
	*/
	
	public String getColoringType()
	{
		return getOption(ModelOptionNamesEnum.COLORING_TYPE);
	}


	public void setColoringType(String coloringType)
	{
		setOption(ModelOptionNamesEnum.COLORING_TYPE, coloringType);
	}


	public int getWidth() 
	{
		return getIntegerOption(ModelOptionNamesEnum.WIDTH);
	}


	public void setWidth(int width) 
	{
		setOption(ModelOptionNamesEnum.WIDTH, width);
	}


	public int getHeight()
	{
		return getIntegerOption(ModelOptionNamesEnum.HEIGHT);
	}


	public void setHeight(int height) 
	{
		setOption(ModelOptionNamesEnum.HEIGHT, height);
	}


	public double getGridSize()
	{
		return getDoubleOption(ModelOptionNamesEnum.GRID_SIZE);
	}

	public void setGridSize(double gridSize)
	{
		setOption(ModelOptionNamesEnum.GRID_SIZE, gridSize);
	}

	public double getElevationMultiple()
	{
		return getDoubleOption(ModelOptionNamesEnum.ELEVATION_MULTIPLE);
	}



	public void setElevationMultiple(double elevationMultiple)
	{
		setOption(ModelOptionNamesEnum.ELEVATION_MULTIPLE, elevationMultiple);
	}
	
	
	public ElevationScalerEnum getElevationScaler()
	{
		return ElevationScalerEnum.getElevationScalerEnumFromIdentifier(getOption(ModelOptionNamesEnum.ELEVATION_SCALER));
	}

	public void setElevationScaler(String elevationScaler)
	{
		setOption(ModelOptionNamesEnum.ELEVATION_SCALER, elevationScaler);
	}
	
	public void setElevationScaler(ElevationScalerEnum elevationScaler)
	{
		setElevationScaler(elevationScaler.identifier());
	}
	
	
	

	public String getGradientLevels()
	{
		return getOption(ModelOptionNamesEnum.GRADIENT_LEVELS);
	}

	public void setGradientLevels(String gradientLevels)
	{
		setOption(ModelOptionNamesEnum.GRADIENT_LEVELS, gradientLevels);
	}

	/*
	public String getPrecacheStrategy() 
	{
		return getOption(ModelOptionNamesEnum.PRECACHE_STRATEGY);
	}


	public void setPrecacheStrategy(String precacheStrategy) 
	{
		setOption(ModelOptionNamesEnum.PRECACHE_STRATEGY, precacheStrategy);
	}
	*/

	public boolean isAntialiased() 
	{
		return getBooleanOption(ModelOptionNamesEnum.ANTIALIASED);
	}


	public void setAntialiased(boolean antialiased) 
	{
		setOption(ModelOptionNamesEnum.ANTIALIASED, antialiased);
	}

	/*
	public boolean getUseSimpleCanvasFill()
	{
		return getBooleanOption(ModelOptionNamesEnum.USE_SIMPLE_CANVAS_FILL);
	}
	
	public void setUseSimpleCanvasFill(boolean useSimpleCanvasFill)
	{
		setOption(ModelOptionNamesEnum.USE_SIMPLE_CANVAS_FILL, useSimpleCanvasFill);
	}
	*/
	
	public int getConcurrentRenderPoolSize()
	{
		return getIntegerOption(ModelOptionNamesEnum.CONCURRENT_RENDER_POOL_SIZE);
	}
	
	public void setConcurrentRenderPoolSize(int concurrentRenderPoolSize)
	{
		setOption(ModelOptionNamesEnum.CONCURRENT_RENDER_POOL_SIZE, concurrentRenderPoolSize);
	}
	
	/*
	public boolean usePipelineRender()
	{
		return getBooleanOption(ModelOptionNamesEnum.PIPELINE_RENDER);
	}
	
	public void setUsePipelineRender(boolean usePipelineRender)
	{
		setOption(ModelOptionNamesEnum.PIPELINE_RENDER, usePipelineRender);
	}
	*/
	
	
	public CanvasProjectionTypeEnum getModelProjection()
	{
		String identifier = getOption(ModelOptionNamesEnum.MODEL_PROJECTION);
		if (identifier == null) {
			return null;
		}

		return CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(identifier);
	}
	
	public void setModelProjection(CanvasProjectionTypeEnum modelProjection)
	{
		setOption(ModelOptionNamesEnum.MODEL_PROJECTION, modelProjection.projectionName());
	}
	
	
	public void setModelProjection(String modelProjection)
	{
		setOption(ModelOptionNamesEnum.MODEL_PROJECTION, modelProjection);
	}
	
	public String getWriteTo()
	{
		return writeTo;
	}

	public void setWriteTo(String writeTo)
	{
		this.writeTo = writeTo;
	}
	
	
	
	public Projection getProjection()
	{
		return projection;
	}


	public void setProjection(Projection projection)
	{
		this.projection = projection;
	}

	public String getUserScript()
	{
		return userScript;
	}

	public void setUserScript(String userScript)
	{
		this.userScript = userScript;
	}

	public ScriptLanguageEnum getScriptLanguage()
	{
		return scriptLanguage;
	}

	public void setScriptLanguage(ScriptLanguageEnum scriptLanguage)
	{
		this.scriptLanguage = scriptLanguage;
	}
	
	public void setScriptLanguage(String scriptLanguageString)
	{
		ScriptLanguageEnum scriptLanguage = ScriptLanguageEnum.getLanguageFromString(scriptLanguageString);
		this.setScriptLanguage(scriptLanguage);
	}
	
	public MapProjectionEnum getMapProjection()
	{
		String identifier = getOption(ModelOptionNamesEnum.MAP_PROJECTION);
		if (identifier == null) {
			return null;
		}
		
		return MapProjectionEnum.getMapProjectionEnumFromIdentifier(identifier);
		
	}
	
	public void setMapProjection(String identifier)
	{
		setOption(ModelOptionNamesEnum.MAP_PROJECTION, identifier);
	}
	
	public void setMapProjection(MapProjectionEnum projectionEnum)
	{
		setMapProjection(projectionEnum.identifier());
	}
	
	public void setUseScripting(boolean useScripting)
	{
		setOption(ModelOptionNamesEnum.USE_SCRIPTING, useScripting);
	}
	
	public boolean useScripting()
	{
		return getBooleanOption(ModelOptionNamesEnum.USE_SCRIPTING);
	}
	
	
	
	/** Creates a value-by-value copy of this object.
	 * 
	 * @return A value-by-value copy of this object.
	 */
	public ModelOptions copy()
	{
		ModelOptions clone = new ModelOptions();
		
		for (String optionName : getOptionNames()) {
			clone.setOption(optionName, getOption(optionName).toString());
		}
		
		
		if (this.writeTo != null) {
			clone.writeTo = this.writeTo.toString();
		}
		
		if (projection != null) {
			clone.projection = this.projection.copy();
		}
		
		if (scriptLanguage != null) {
			clone.scriptLanguage = this.scriptLanguage;
		}
		
		if (userScript != null) {
			clone.userScript = this.userScript.toString();
		}
		
		return clone;
	}
}
