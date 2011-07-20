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

import us.wthr.jdem846.project.ProjectModel;

/** Options for model processing.
 * 
 * @author Kevin M. Gill
 *
 */
public class ModelOptions
{
	public static final int SPOT_EXPONENT_MINIMUM = 1;
	public static final int SPOT_EXPONENT_MAXIMUM = 5;
	
	public static final String OPTION_ENGINE = "us.wthr.jdem846.modelOptions.engine";
	public static final String OPTION_GRADIENT_LEVELS = "us.wthr.jdem846.modelOptions.gradientLevels";
	public static final String OPTION_WRITE_TO = "us.wthr.jdem846.modelOptions.writeTo";
	public static final String OPTION_WIDTH = "us.wthr.jdem846.modelOptions.width";
	public static final String OPTION_HEIGHT = "us.wthr.jdem846.modelOptions.height";
	public static final String OPTION_BACKGROUND_COLOR = "us.wthr.jdem846.modelOptions.backgroundColor";
	public static final String OPTION_HILLSHADING = "us.wthr.jdem846.modelOptions.hillShading";
	public static final String OPTION_HILLSHADE_TYPE = "us.wthr.jdem846.modelOptions.hillShadeType";
	public static final String OPTION_COLORING_TYPE = "us.wthr.jdem846.modelOptions.coloringType";
	public static final String OPTION_LIGHTING_AZIMUTH = "us.wthr.jdem846.modelOptions.lightingAzimuth";
	public static final String OPTION_LIGHTING_ELEVATION = "us.wthr.jdem846.modelOptions.lightingElevation";
	public static final String OPTION_TILE_SIZE = "us.wthr.jdem846.modelOptions.tileSize";
	public static final String OPTION_LIGHTING_MULTIPLE = "us.wthr.jdem846.modelOptions.lightingMultiple";
	public static final String OPTION_SPOT_EXPONENT = "us.wthr.jdem846.modelOptions.spotExponent";
	public static final String OPTION_ELEVATION_MULTIPLE = "us.wthr.jdem846.modelOptions.elevationMultiple";
	
	public static final String OPTION_PROJECTION_ROTATE_X = "us.wthr.jdem846.modelOptions.projection.rotateX";
	public static final String OPTION_PROJECTION_ROTATE_Y = "us.wthr.jdem846.modelOptions.projection.rotateY";
	public static final String OPTION_PROJECTION_ROTATE_Z = "us.wthr.jdem846.modelOptions.projection.rotateZ";
	public static final String OPTION_PROJECTION_SHIFT_X = "us.wthr.jdem846.modelOptions.projection.shiftX";
	public static final String OPTION_PROJECTION_SHIFT_Y = "us.wthr.jdem846.modelOptions.projection.shiftY";
	public static final String OPTION_PROJECTION_SHIFT_Z = "us.wthr.jdem846.modelOptions.projection.shiftZ";
	
	
	private String engine = "dem2d-gen";
	private boolean hillShading = true;
	private int hillShadeType = DemConstants.HILLSHADING_COMBINED;
	private String coloringType = "hypsometric-tint";
	
	private String backgroundColor = "Blue";
	
	private int width = 3000;
	private int height = 3000;
	private double elevationMultiple = 1.0;
	private int tileSize = 1000;
	private double lightingMultiple = 0.5f;
	
	private double lightingAzimuth = 135;
	private double lightingElevation = 45;
	
	/** Sets the spot exponent for the intensity distribution of the lighting. 
	 * Should be a value between 0.4 and 10.0 (default: 1.0)
	 */
	private int spotExponent = 1;
	
	private String gradientLevels = null;
	
	private Projection projection = new Projection();
	private String writeTo = null;
	
	
	
	public ModelOptions()
	{
		JDem846Properties properties = new JDem846Properties(JDem846Properties.CORE_PROPERTIES);
		String property = null;
		
		property = properties.getProperty(ModelOptions.OPTION_ENGINE);
		if (property != null) {
			setEngine(property);
		}
		
		property = properties.getProperty(OPTION_GRADIENT_LEVELS);
		if (property != null) {
			setGradientLevels(property);
		}
		
		property = properties.getProperty(OPTION_WRITE_TO);
		if (property != null) {
			setWriteTo(property);
		}
		
		property = properties.getProperty(OPTION_WIDTH);
		if (property != null) {
			setWidth(Integer.parseInt(property));
		}
		
		
		property = properties.getProperty(OPTION_HEIGHT);
		if (property != null) {
			setHeight(Integer.parseInt(property));
		}
		
		property = properties.getProperty(OPTION_ELEVATION_MULTIPLE);
		if (property != null) {
			setElevationMultiple(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_BACKGROUND_COLOR);
		if (property != null) {
			setBackgroundColor(property);
		}
		
		property = properties.getProperty(OPTION_HILLSHADING);
		if (property != null) {
			setHillShading(Boolean.parseBoolean(property));
		}
		
		property = properties.getProperty(OPTION_HILLSHADE_TYPE);
		if (property != null) {
			setHillShadeType(Integer.parseInt(property));
		}
		
		property = properties.getProperty(OPTION_COLORING_TYPE);
		if (property != null) {
			setColoringType(property);
		}
		
		
		
		
		
		property = properties.getProperty(OPTION_LIGHTING_AZIMUTH);
		if (property != null) {
			setLightingAzimuth(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_LIGHTING_ELEVATION);
		if (property != null) {
			setLightingElevation(Double.parseDouble(property));
		}
		
		
		property = properties.getProperty(OPTION_TILE_SIZE);
		if (property != null) {
			setTileSize(Integer.parseInt(property));
		}
		
		property = properties.getProperty(OPTION_LIGHTING_MULTIPLE);
		if (property != null) {
			setLightingMultiple(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_SPOT_EXPONENT);
		if (property != null) {
			setSpotExponent(Integer.parseInt(property));
		}
		
		property = properties.getProperty(OPTION_PROJECTION_ROTATE_X);
		if (property != null) {
			projection.setRotateX(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_PROJECTION_ROTATE_Y);
		if (property != null) {
			projection.setRotateY(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_PROJECTION_ROTATE_Z);
		if (property != null) {
			projection.setRotateZ(Double.parseDouble(property));
		}
		
		
		property = properties.getProperty(OPTION_PROJECTION_SHIFT_X);
		if (property != null) {
			projection.setShiftX(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_PROJECTION_SHIFT_Y);
		if (property != null) {
			projection.setShiftY(Double.parseDouble(property));
		}
		
		property = properties.getProperty(OPTION_PROJECTION_SHIFT_Z);
		if (property != null) {
			projection.setShiftZ(Double.parseDouble(property));
		}
		
	}

	
	/** Synchronizes values from this object to a ProjectModel object.
	 * 
	 * @param projectModel A ProjectModel to synchronize to
	 */
	public void syncToProjectModel(ProjectModel projectModel)
	{
		projectModel.setOption(OPTION_ENGINE, this.getEngine());
		projectModel.setOption(OPTION_GRADIENT_LEVELS, this.getGradientLevels());
		projectModel.setOption(OPTION_WRITE_TO, this.getWriteTo());
		projectModel.setOption(OPTION_WIDTH, this.getWidth());
		projectModel.setOption(OPTION_HEIGHT, this.getHeight());
		projectModel.setOption(OPTION_ELEVATION_MULTIPLE, this.getElevationMultiple());
		projectModel.setOption(OPTION_BACKGROUND_COLOR, this.getBackgroundColor());
		projectModel.setOption(OPTION_HILLSHADING, this.isHillShading());
		projectModel.setOption(OPTION_HILLSHADE_TYPE, this.getHillShadeType());
		projectModel.setOption(OPTION_COLORING_TYPE, this.getColoringType());
		projectModel.setOption(OPTION_LIGHTING_AZIMUTH, this.getLightingAzimuth());
		projectModel.setOption(OPTION_LIGHTING_ELEVATION, this.getLightingElevation());
		projectModel.setOption(OPTION_TILE_SIZE, this.getTileSize());
		projectModel.setOption(OPTION_LIGHTING_MULTIPLE, this.getLightingMultiple());
		projectModel.setOption(OPTION_SPOT_EXPONENT, this.getSpotExponent());
		
		projectModel.setOption(OPTION_PROJECTION_ROTATE_X, projection.getRotateX());
		projectModel.setOption(OPTION_PROJECTION_ROTATE_Y, projection.getRotateY());
		projectModel.setOption(OPTION_PROJECTION_ROTATE_Z, projection.getRotateZ());
		
		projectModel.setOption(OPTION_PROJECTION_SHIFT_X, projection.getShiftX());
		projectModel.setOption(OPTION_PROJECTION_SHIFT_Y, projection.getShiftY());
		projectModel.setOption(OPTION_PROJECTION_SHIFT_Z, projection.getShiftZ());
	}
	
	/** Synchronizes values from a ProjectModel object to this object.
	 * 
	 * @param projectModel A ProjectModel to synchronize from.
	 */
	public void syncFromProjectModel(ProjectModel projectModel)
	{
		
		this.setEngine(projectModel.getOption(OPTION_ENGINE));
		this.setGradientLevels(projectModel.getOption(OPTION_GRADIENT_LEVELS));
		this.setWriteTo(projectModel.getOption(OPTION_WRITE_TO));
		this.setWidth(projectModel.getIntegerOption(OPTION_WIDTH));
		this.setHeight(projectModel.getIntegerOption(OPTION_HEIGHT));
		this.setElevationMultiple(projectModel.getDoubleOption(OPTION_ELEVATION_MULTIPLE));
		this.setBackgroundColor(projectModel.getOption(OPTION_BACKGROUND_COLOR));
		this.setHillShading(projectModel.getBooleanOption(OPTION_HILLSHADING));
		this.setHillShadeType(projectModel.getIntegerOption(OPTION_HILLSHADE_TYPE));
		this.setColoringType(projectModel.getOption(OPTION_COLORING_TYPE));
		this.setLightingAzimuth(projectModel.getDoubleOption(OPTION_LIGHTING_AZIMUTH));
		this.setLightingElevation(projectModel.getDoubleOption(OPTION_LIGHTING_ELEVATION));
		this.setTileSize(projectModel.getIntegerOption(OPTION_TILE_SIZE));
		this.setLightingMultiple(projectModel.getDoubleOption(OPTION_LIGHTING_MULTIPLE));
		this.setSpotExponent(projectModel.getIntegerOption(OPTION_SPOT_EXPONENT));
	
		this.projection.setRotateX(projectModel.getDoubleOption(OPTION_PROJECTION_ROTATE_X));
		this.projection.setRotateY(projectModel.getDoubleOption(OPTION_PROJECTION_ROTATE_Y));
		this.projection.setRotateZ(projectModel.getDoubleOption(OPTION_PROJECTION_ROTATE_Z));
		
		this.projection.setShiftX(projectModel.getDoubleOption(OPTION_PROJECTION_SHIFT_X));
		this.projection.setShiftY(projectModel.getDoubleOption(OPTION_PROJECTION_SHIFT_Y));
		this.projection.setShiftZ(projectModel.getDoubleOption(OPTION_PROJECTION_SHIFT_Z));
		
		
	}
	
	
	public String getEngine() 
	{
		return engine;
	}

	public void setEngine(String engine)
	{
		this.engine = engine;
	}


	public double getLightingMultiple() 
	{
		return lightingMultiple;
	}


	public void setLightingMultiple(double lightingMultiple)
	{
		this.lightingMultiple = lightingMultiple;
	}

	
	
	
	public int getSpotExponent()
	{
		return spotExponent;
	}
	
	/** Sets the spot exponent for the intensity distribution of the lighting. 
	 * 
	 * @param spotExponent A value between 1.0 and 10.0 (default: 1.0)
	 */
	public void setSpotExponent(int spotExponent)
	{
		this.spotExponent = spotExponent;
	}

	public double getLightingAzimuth()
	{
		return lightingAzimuth;
	}


	public void setLightingAzimuth(double lightingAzimuth)
	{
		this.lightingAzimuth = lightingAzimuth;
	}

	public double getLightingElevation()
	{
		return lightingElevation;
	}




	public void setLightingElevation(double lightingElevation)
	{
		this.lightingElevation = lightingElevation;
	}




	public int getTileSize() 
	{
		return tileSize;
	}

	public void setTileSize(int tileSize) 
	{
		this.tileSize = tileSize;
	}

	


	public String getBackgroundColor()
	{
		return backgroundColor;
	}




	public void setBackgroundColor(String backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}




	public boolean isHillShading() 
	{
		return hillShading;
	}


	public void setHillShading(boolean hillShading) 
	{
		this.hillShading = hillShading;
	}


	public int getHillShadeType()
	{
		return hillShadeType;
	}


	public void setHillShadeType(int hillShadeType) 
	{
		this.hillShadeType = hillShadeType;
	}


	public String getColoringType()
	{
		return coloringType;
	}


	public void setColoringType(String coloringType)
	{
		this.coloringType = coloringType;
	}


	public int getWidth() 
	{
		return width;
	}


	public void setWidth(int width) 
	{
		this.width = width;
	}


	public int getHeight()
	{
		return height;
	}


	public void setHeight(int height) 
	{
		this.height = height;
	}

	
	
	
	public double getElevationMultiple()
	{
		return elevationMultiple;
	}



	public void setElevationMultiple(double elevationMultiple)
	{
		this.elevationMultiple = elevationMultiple;
	}



	public String getGradientLevels()
	{
		return gradientLevels;
	}

	public void setGradientLevels(String gradientLevels)
	{
		this.gradientLevels = gradientLevels;
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
		
		clone.engine = this.engine;
		clone.hillShading = this.hillShading;
		clone.hillShadeType = this.hillShadeType;
		clone.coloringType = this.coloringType;
		clone.backgroundColor = this.backgroundColor;
		clone.width = this.width;
		clone.height = this.height;
		clone.elevationMultiple = this.elevationMultiple;
		clone.tileSize = this.tileSize;
		clone.lightingMultiple = this.lightingMultiple;
		clone.lightingAzimuth = this.lightingAzimuth;
		clone.lightingElevation = this.lightingElevation;
		clone.spotExponent = this.spotExponent;
		clone.gradientLevels = this.gradientLevels;
		clone.writeTo = this.writeTo;
		if (projection != null) {
			clone.projection = this.projection.copy();
		}
		
		return clone;
	}
}
