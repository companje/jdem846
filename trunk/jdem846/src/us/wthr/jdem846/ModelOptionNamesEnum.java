package us.wthr.jdem846;

public enum ModelOptionNamesEnum
{
	
	ENGINE("us.wthr.jdem846.modelOptions.engine"),
	GRADIENT_LEVELS("us.wthr.jdem846.modelOptions.gradientLevels"),
	WRITE_TO("us.wthr.jdem846.modelOptions.writeTo"),
	WIDTH("us.wthr.jdem846.modelOptions.width"),
	HEIGHT("us.wthr.jdem846.modelOptions.height"),
	GRID_SIZE("us.wthr.jdem846.modelOptions.gridSize"),
	BACKGROUND_COLOR("us.wthr.jdem846.modelOptions.backgroundColor"),
	HILLSHADING("us.wthr.jdem846.modelOptions.hillShading"),
	HILLSHADE_TYPE("us.wthr.jdem846.modelOptions.hillShadeType"),
	DOUBLE_PRECISION_HILLSHADING("us.wthr.jdem846.modelOptions.doublePrecisionHillshading"),
	COLORING_TYPE("us.wthr.jdem846.modelOptions.coloringType"),
	LIGHTING_AZIMUTH("us.wthr.jdem846.modelOptions.lightingAzimuth"),
	LIGHTING_ELEVATION("us.wthr.jdem846.modelOptions.lightingElevation"),
	TILE_SIZE("us.wthr.jdem846.modelOptions.tileSize"),
	LIGHTING_MULTIPLE("us.wthr.jdem846.modelOptions.lightingMultiple"),
	SPOT_EXPONENT("us.wthr.jdem846.modelOptions.spotExponent"),
	ELEVATION_MULTIPLE("us.wthr.jdem846.modelOptions.elevationMultiple"),
	
	RELATIVE_LIGHT_INTENSITY("us.wthr.jdem846.modelOptions.relativeLightIntensity"),
	RELATIVE_DARK_INTENSITY("us.wthr.jdem846.modelOptions.relativeDarkIntensity"),
	
	PROJECTION_ROTATE_X("us.wthr.jdem846.modelOptions.projection.rotateX"),
	PROJECTION_ROTATE_Y("us.wthr.jdem846.modelOptions.projection.rotateY"),
	PROJECTION_ROTATE_Z("us.wthr.jdem846.modelOptions.projection.rotateZ"),
	PROJECTION_SHIFT_X("us.wthr.jdem846.modelOptions.projection.shiftX"),
	PROJECTION_SHIFT_Y("us.wthr.jdem846.modelOptions.projection.shiftY"),
	PROJECTION_SHIFT_Z("us.wthr.jdem846.modelOptions.projection.shiftZ"),
	
	PRECACHE_STRATEGY("us.wthr.jdem846.modelOptions.precacheStrategy"),
	ANTIALIASED("us.wthr.jdem846.modelOptions.antialiased"),
	
	USER_SCRIPT_LANGUAGE("us.wthr.jdem846.modelOptions.userScript.language"),
	USER_SCRIPT_GROOVY_TEMPLATE("us.wthr.jdem846.modelOptions.userScript.groovy.template"),
	USER_SCRIPT_JYTHON_TEMPLATE("us.wthr.jdem846.modelOptions.userScript.jython.template"),
	
	MAP_PROJECTION("us.wthr.jdem846.modelOptions.mapProjection");
	
	private final String optionName;
	
	ModelOptionNamesEnum(String optionName)
	{
		this.optionName = optionName;
	}
	
	public String optionName() { return optionName; }
}
