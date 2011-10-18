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
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import us.wthr.jdem846.project.ProjectModel;
import us.wthr.jdem846.util.ColorSerializationUtil;
import us.wthr.jdem846.util.NumberFormattingUtil;

/** Options for model processing.
 * 
 * @author Kevin M. Gill
 *
 */
public class ModelOptions
{
	public static final int SPOT_EXPONENT_MINIMUM = 1;
	public static final int SPOT_EXPONENT_MAXIMUM = 5;
	

	
	private Map<String, String> optionsMap = new HashMap<String, String>();
	
	/*
	private String engine = "dem2d-gen";
	private boolean hillShading = true;
	private int hillShadeType = DemConstants.HILLSHADING_COMBINED;
	private String coloringType = "hypsometric-tint";
	
	private String backgroundColor = "Blue";
	
	private int width = 3000;
	private int height = 3000;
	private int gridSize = 1;
	private double elevationMultiple = 1.0;
	private int tileSize = 1000;
	private double lightingMultiple = 0.5f;
	
	private double lightingAzimuth = 135;
	private double lightingElevation = 45;
	
	private double relativeLightIntensity = 1.0;
	private double relativeDarkIntensity = 1.0;
	*/
	
	/** Sets the spot exponent for the intensity distribution of the lighting. 
	 * Should be a value between 0.4 and 10.0 (default: 1.0)
	 */
	/*
	private int spotExponent = 1;
	
	private String gradientLevels = null;
	
	private Projection projection = new Projection();
	private String writeTo = null;
	

	private String precacheStrategy;
	private boolean antialiased;
	*/
	
	private Projection projection = new Projection();
	private String writeTo = null;
	
	public ModelOptions()
	{
		//JDem846Properties properties = new JDem846Properties(JDem846Properties.CORE_PROPERTIES);
		///JDem846Properties properties = JDem846Properties.getInstance();
		
		for (ModelOptionNamesEnum optionName : ModelOptionNamesEnum.values()) {
			String property = JDem846Properties.getProperty(optionName.optionName());
			if (property != null) {
				setOption(optionName.optionName(), property);
			}
		}
		
		
		
		/*
		String property = null;
		
		property = JDem846Properties.getProperty(ModelOptions.OPTION_ENGINE);
		if (property != null) {
			setEngine(property);
		}
		
		property = JDem846Properties.getProperty(OPTION_GRADIENT_LEVELS);
		if (property != null) {
			setGradientLevels(property);
		}
		
		property = JDem846Properties.getProperty(OPTION_WRITE_TO);
		if (property != null) {
			setWriteTo(property);
		}
		
		property = JDem846Properties.getProperty(OPTION_WIDTH);
		if (property != null) {
			setWidth(Integer.parseInt(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_GRID_SIZE);
		if (property != null) {
			setGridSize(Integer.parseInt(property));
		}

		property = JDem846Properties.getProperty(OPTION_HEIGHT);
		if (property != null) {
			setHeight(Integer.parseInt(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_ELEVATION_MULTIPLE);
		if (property != null) {
			setElevationMultiple(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_BACKGROUND_COLOR);
		if (property != null) {
			setBackgroundColor(property);
		}
		
		property = JDem846Properties.getProperty(OPTION_HILLSHADING);
		if (property != null) {
			setHillShading(Boolean.parseBoolean(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_HILLSHADE_TYPE);
		if (property != null) {
			setHillShadeType(Integer.parseInt(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_COLORING_TYPE);
		if (property != null) {
			setColoringType(property);
		}
		
		
		
		
		
		property = JDem846Properties.getProperty(OPTION_LIGHTING_AZIMUTH);
		if (property != null) {
			setLightingAzimuth(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_LIGHTING_ELEVATION);
		if (property != null) {
			setLightingElevation(Double.parseDouble(property));
		}
		
		
		property = JDem846Properties.getProperty(OPTION_TILE_SIZE);
		if (property != null) {
			setTileSize(Integer.parseInt(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_LIGHTING_MULTIPLE);
		if (property != null) {
			setLightingMultiple(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_RELATIVE_LIGHT_INTENSITY);
		if (property != null) {
			setRelativeLightIntensity(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_RELATIVE_DARK_INTENSITY);
		if (property != null) {
			setRelativeDarkIntensity(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_SPOT_EXPONENT);
		if (property != null) {
			setSpotExponent(Integer.parseInt(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_PRECACHE_STRATEGY);
		if (property != null) {
			setPrecacheStrategy(property);
		}

		property = JDem846Properties.getProperty(OPTION_ANTIALIASED);
		if (property != null) {
			setAntialiased(Boolean.parseBoolean(property));
		}
		
		
		
		property = JDem846Properties.getProperty(OPTION_PROJECTION_ROTATE_X);
		if (property != null) {
			projection.setRotateX(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_PROJECTION_ROTATE_Y);
		if (property != null) {
			projection.setRotateY(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_PROJECTION_ROTATE_Z);
		if (property != null) {
			projection.setRotateZ(Double.parseDouble(property));
		}
		
		
		property = JDem846Properties.getProperty(OPTION_PROJECTION_SHIFT_X);
		if (property != null) {
			projection.setShiftX(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_PROJECTION_SHIFT_Y);
		if (property != null) {
			projection.setShiftY(Double.parseDouble(property));
		}
		
		property = JDem846Properties.getProperty(OPTION_PROJECTION_SHIFT_Z);
		if (property != null) {
			projection.setShiftZ(Double.parseDouble(property));
		}
		*/
		
		
		
	}
	
	public Set<String> getOptionNames()
	{
		return optionsMap.keySet();
	}
	
	public void setOption(String name, Object value)
	{
		String sValue = null;
		
		if (value == null) {
			return;
		}
		
		if (value instanceof String) {
			sValue = (String) value;
		} else if (value instanceof Integer ||
					value instanceof Double ||
					value instanceof Long ||
					value instanceof Float) {
			sValue = NumberFormattingUtil.format(value);
		} else if (value instanceof Boolean){
			sValue = Boolean.toString((Boolean)value);
		} else {
			throw new InvalidParameterException("Invalid parameter type: " + value.getClass().getName());
		}
		
		optionsMap.put(name, sValue);
	}
	
	public void setOption(ModelOptionNamesEnum name, Object value)
	{
		setOption(name.optionName(), value);
	}
	

	
	public String getOption(String name)
	{
		return optionsMap.get(name);
	}
	
	public String getOption(ModelOptionNamesEnum name)
	{
		return getOption(name.optionName());
	}
	
	public boolean hasOption(String name)
	{
		return (optionsMap.containsKey(name));
	}
	
	public boolean hasOption(ModelOptionNamesEnum name)
	{
		return hasOption(name.optionName());
	}
	
	public String removeOption(String name)
	{
		return optionsMap.remove(name);
	}
	
	public String removeOption(ModelOptionNamesEnum name)
	{
		return removeOption(name.optionName());
	}
	
	public boolean getBooleanOption(String name)
	{
		if (hasOption(name))
			return Boolean.parseBoolean(getOption(name));
		else
			return false;
	}
	
	public boolean getBooleanOption(ModelOptionNamesEnum name)
	{
		return getBooleanOption(name.optionName());
	}
	
	public int getIntegerOption(String name)
	{
		if (hasOption(name))
			return Integer.parseInt(getOption(name));
		else
			return 0;
	}
	
	public int getIntegerOption(ModelOptionNamesEnum name)
	{
		return getIntegerOption(name.optionName());
	}
	
	public double getDoubleOption(String name)
	{
		if (hasOption(name))
			return Double.parseDouble(getOption(name));
		else
			return 0.0;
	}
	
	public double getDoubleOption(ModelOptionNamesEnum name)
	{
		return getDoubleOption(name.optionName());
	}
	
	public float getFloatOption(String name)
	{
		if (hasOption(name))
			return Float.parseFloat(getOption(name));
		else
			return 0.0f;
	}
	
	public float getFloatOption(ModelOptionNamesEnum name)
	{
		return getFloatOption(name.optionName());
	}
	
	public long getLongOption(String name)
	{
		if (hasOption(name))
			return Long.parseLong(getOption(name));
		else
			return 0;
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
		for (String optionName : getOptionNames()) {
			projectModel.setOption(optionName, optionsMap.get(optionName).toString());
		}

		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X, projection.getRotateX());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y, projection.getRotateY());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z, projection.getRotateZ());
		
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X, projection.getShiftX());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y, projection.getShiftY());
		projectModel.setOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z, projection.getShiftZ());
	}
	
	/** Synchronizes values from a ProjectModel object to this object.
	 * 
	 * @param projectModel A ProjectModel to synchronize from.
	 */
	public void syncFromProjectModel(ProjectModel projectModel)
	{
		
		for (String optionName : projectModel.getOptionKeys()) {
			this.setOption(optionName, projectModel.getOption(optionName));
		}
		
		

		this.projection.setRotateX(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_X));
		this.projection.setRotateY(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Y));
		this.projection.setRotateZ(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_ROTATE_Z));
		
		this.projection.setShiftX(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_X));
		this.projection.setShiftY(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Y));
		this.projection.setShiftZ(projectModel.getDoubleOption(ModelOptionNamesEnum.PROJECTION_SHIFT_Z));
		
		
	}
	
	
	public String getEngine() 
	{
		return getOption(ModelOptionNamesEnum.ENGINE);
	}

	public void setEngine(String engine)
	{
		setOption(ModelOptionNamesEnum.ENGINE, engine);
	}


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
	
	/** Sets the spot exponent for the intensity distribution of the lighting. 
	 * 
	 * @param spotExponent A value between 1.0 and 10.0 (default: 1.0)
	 */
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




	public int getTileSize() 
	{
		return getIntegerOption(ModelOptionNamesEnum.TILE_SIZE);
	}

	public void setTileSize(int tileSize) 
	{
		setOption(ModelOptionNamesEnum.TILE_SIZE, tileSize);
	}

	


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


	public int getHillShadeType()
	{
		return getIntegerOption(ModelOptionNamesEnum.HILLSHADE_TYPE);
	}


	public void setHillShadeType(int hillShadeType) 
	{
		setOption(ModelOptionNamesEnum.HILLSHADE_TYPE, hillShadeType);
	}

	public boolean getDoublePrecisionHillshading()
	{
		return getBooleanOption(ModelOptionNamesEnum.DOUBLE_PRECISION_HILLSHADING);
	}

	public void setDoublePrecisionHillshading(boolean doublePrecisionHillshading)
	{
		setOption(ModelOptionNamesEnum.DOUBLE_PRECISION_HILLSHADING, doublePrecisionHillshading);
	}
	
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


	public int getGridSize()
	{
		return getIntegerOption(ModelOptionNamesEnum.GRID_SIZE);
	}

	public void setGridSize(int gridSize)
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



	public String getGradientLevels()
	{
		return getOption(ModelOptionNamesEnum.GRADIENT_LEVELS);
	}

	public void setGradientLevels(String gradientLevels)
	{
		setOption(ModelOptionNamesEnum.GRADIENT_LEVELS, gradientLevels);
	}


	public String getPrecacheStrategy() 
	{
		return getOption(ModelOptionNamesEnum.PRECACHE_STRATEGY);
	}


	public void setPrecacheStrategy(String precacheStrategy) 
	{
		setOption(ModelOptionNamesEnum.PRECACHE_STRATEGY, precacheStrategy);
	}


	public boolean isAntialiased() 
	{
		return getBooleanOption(ModelOptionNamesEnum.ANTIALIASED);
	}


	public void setAntialiased(boolean antialiased) 
	{
		setOption(ModelOptionNamesEnum.ANTIALIASED, antialiased);
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


	/** Creates a value-by-value copy of this object.
	 * 
	 * @return A value-by-value copy of this object.
	 */
	public ModelOptions copy()
	{
		ModelOptions clone = new ModelOptions();
		
		for (String optionName : getOptionNames()) {
			clone.setOption(optionName, optionsMap.get(optionName).toString());
		}
		
		
		if (this.writeTo != null) {
			clone.writeTo = this.writeTo.toString();
		}
		
		if (projection != null) {
			clone.projection = this.projection.copy();
		}
		
		return clone;
	}
}
