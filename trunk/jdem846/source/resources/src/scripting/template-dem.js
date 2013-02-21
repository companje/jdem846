
importPackage(Packages.us.wthr.jdem846);
importPackage(Packages.us.wthr.jdem846.logging);
importPackage(Packages.us.wthr.jdem846.image);
importPackage(Packages.us.wthr.jdem846.input);
importPackage(Packages.us.wthr.jdem846.math);
importPackage(Packages.java.io);
importPackage(Packages.javax.imageio);
importPackage(Packages.java.awt.image);
importPackage(Packages.us.wthr.jdem846.color);
importPackage(Packages.us.wthr.jdem846.gis.projections);
importPackage(Packages.us.wthr.jdem846.canvas);
importPackage(Packages.us.wthr.jdem846.canvas.util);
importPackage(Packages.us.wthr.jdem846.geom);
importPackage(Packages.us.wthr.jdem846.gis.planets);
importPackage(Packages.us.wthr.jdem846.model);
importPackage(Packages.us.wthr.jdem846.model.processing.util);
importPackage(Packages.us.wthr.jdem846.model.processing.shading);
importPackage(Packages.us.wthr.jdem846.globe);
importPackage(Packages.us.wthr.jdem846.graphics);
importPackage(Packages.us.wthr.jdem846.util);


/** Provides an interface into the global storage container which allows resources
 * to be loaded and stored by scripts, freeing them from having to reload resources
 * on each and every rendering. Objects put into the container will persist during the 
 * lifetime of the application instance (they will be released when you close jDem) and will
 * be available to other projects and/or scripts loaded into the program.
 * 
 */
var StorageContext = (function() {
	
	function StorageContext() {
		
		
	};
	
	/** Checks whether the specified name exists in the global storage container.
	 * 
	 */
	StorageContext.prototype.hasResource = function(name) {
		return GlobalStorageContainer.hasResource(name);
	};
	
	/** Fetches an object by name from the global storage container. If the object
	 * does not exist in the container the callback, if supplied, will be called to create it. 
	 * The callback created object will automatically be added to the storage container
	 * with the supplied name. If the object is null and no callback was supplied, this 
	 * will return null;
	 * 
	 */
	StorageContext.prototype.get = function(name, ifNullCallback, forceReload) {
		var obj = null;
		if (forceReload === undefined)
			forceReload = false;
		
		if ((forceReload || !this.hasResource(name)) && ifNullCallback !== undefined) {
			obj = ifNullCallback();
			this.put(name, obj);
		} else if (this.hasResource(name)) {
			obj = GlobalStorageContainer.get(name);
		}

		return obj;
	};
	
	/** Puts an object into the global storage container addressable with the supplied name.
	 * If an object already exists with the given name then it will be replaced.
	 * 
	 */
	StorageContext.prototype.put = function(name, obj) {
		GlobalStorageContainer.put(name, obj);
	};
	
	return StorageContext;
})();

/** Provides a API layer into the application components to be used by the user-provided script.
 * This layer is light-weight and provides mostly convienence and fetch methods. The script
 * author has the option to directly access the jDem API by importing classes and/or using static
 * and global resources.
 */
var ScriptContext = (function() {
	
	function ScriptContext() {
		this.storage = new StorageContext();
		this.log = null;
		this.modelContext = null;
		this.modelGrid = null;
		this.modelDimensions = null;
		this.globalOptionModel = null;
	}
	
	/** Scales an elevation according to the configured multiple and scaling formula.
	 * 
	 */
	ScriptContext.prototype.scaleElevation = function(elevation) {
		return this.modelContext.getRasterDataContext().getElevationScaler().scale(elevation);
	};
	
	/** Fetches an option model from the model context by the process id.
	 * 
	 */
	ScriptContext.prototype.getOptionModel = function(processId) {
		return this.modelContext.getModelProcessManifest().getOptionModelByProcessId(processId);
	};
	
	/** Fetches the lighting option model from the model context.
	 * 
	 */
	ScriptContext.prototype.getLightingOptionModel = function() {
		return this.getOptionModel("us.wthr.jdem846.model.processing.lighting.RenderLightingProcessor");
	};
	
	/** Opens an image from disk with the path an provided coordinates. The image
	 * is returned loaded. If the coordinates are not specified (undefined) they 
	 * will be defaulted to 90.0N, 90.0S, 180.0E, 180.0W.
	 * 
	 */
	ScriptContext.prototype.loadImage = function(imagePath, north, south, east, west) {
		north = (north === undefined) ? 90 : north;
		south = (south === undefined) ? -90 : south;
		east = (east === undefined) ? 180 : east;
		west = (west === undefined) ? -180 : west;

		var image = new SimpleGeoImage(imagePath, north, south, east, west) ;
	    image.load()
		return image;
	};
	
	/** Loads are returns a texture object. Source image is automically garbage-collected
	 * once the texture is loaded into memory.
	 * 
	 */
	ScriptContext.prototype.loadTexture = function(imagePath, north, south, east, west) {
		var image = this.loadImage(imagePath, north, south, east, west);
		var texture = image.getAsTexture();
		image.unload();
		return texture;
	};
	
	/** Retrieves the configured lighting time in milliseconds since midnight, January 1st, 1970.
	 * Note: This is an Earth-based time.
	 * 
	 */
	ScriptContext.prototype.getLightingTime = function() {
		var lightingOptionModel = this.getLightingOptionModel();
	      
	    var lightingTime = lightingOptionModel.getSunlightTime().getTime();
	    var lightingDate = lightingOptionModel.getSunlightDate().getDate();
		
	    return lightingTime + lightingDate;
	};
	
	/** Retrieves the minimum elevation found in the raster data set in meters.
	 * 
	 */
	ScriptContext.prototype.getMinimumElevation = function() {
		return this.modelContext.getRasterDataContext().getDataMinimumValue();
	};
	
	/** Retrieves the maximum elevation found in the raster data set in meters.
	 * 
	 */
	ScriptContext.prototype.getMaximumElevation = function() {
		return this.modelContext.getRasterDataContext().getDataMaximumValue();
	};
	
	/** Returns the sun position in X/Y/Z (a Vector object) coordinates, relative to the
	 * position of the Earth orbit and rotation given the configured lighting time.
	 * 
	 */
	ScriptContext.prototype.getSolarPosition = function() {
		return SunlightPositioning.calculate(this.getLightingTime());
	};
	
	/** Creates and returns a render view object.
	 * 
	 */
	ScriptContext.prototype.createView = function() {
		var view = ViewFactory.getViewInstance(this.modelContext, this.globalOptionModel, this.modelDimensions, null, this.modelContext.getScriptingContext().getScriptProxy(), this.modelGrid);
	    return view;
	};
	
	return ScriptContext;
})();

var scriptContext = new ScriptContext();

/** Function handler for initializing the script prior to the model being processed.
 * 
 */
function initialize() 
{
	
}

/** Function handler for operations that may need to be performed just prior to the model being processed
 * 
 */
function onProcessBefore()
{
		
}

/** Function handler for operations that may need to be performed just after the model completes processing
 * 
 */
function onProcessAfter() 
{

}

/** Function handler for providing an arbitrary elevation given the latitude and longitude. Any floating point
 * value that is returned that is not equal to DemConstants.ELEV_UNDETERMINED will be returned to the model engine
 * causing the raster fetch and onGetElevationAfter() to be skipped.
 * 
 * @param latitude
 * @param longitude
 * @returns
 */
function onGetElevationBefore(latitude, longitude) 
{	
	return DemConstants.ELEV_UNDETERMINED
}

/** 
 * 
 * @param latitude
 * @param longitude
 * @param elevation
 * @returns
 */
function onGetElevationAfter(latitude, longitude, elevation) 
{	 
	return elevation
}

/**
 * 
 * @param latitude
 * @param longitude
 * @param elevation
 * @param elevationMinimum
 * @param elevationMaximum
 * @param color
 * @returns
 */
function onGetPointColor(latitude, longitude, elevation, elevationMinimum, elevationMaximum, color)
{
	return color;
}

/**
 * 
 * @param latitude
 * @param longitude
 */
function onLightLevels(latitude, longitude)
{	
	
}

/**
 * 
 * @param latitude
 * @param longitude
 * @param elevation
 * @param renderer
 * @param view
 */
function onBeforeVertex(latitude, longitude, elevation, renderer, view)
{
	
}

/**
 * 
 * @param renderer
 * @param view
 */
function preRender(renderer, view)
{

}

/**
 * 
 * @param renderer
 * @param view
 */
function postRender(renderer, view)
{

}

/**
 * 
 */
function destroy()
{ 

}