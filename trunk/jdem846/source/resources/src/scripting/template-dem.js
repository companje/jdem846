
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

var log = null;
var modelContext = null;
var globalOptionModel = null;
var modelGrid = null;
var modelDimensions = null;
		



function setLog(l)
{
	log = l;
}

function setModelContext(mc)
{
	modelContext = mc;
}

function initialize() 
{
	
}

function onProcessBefore()
{
		
}

function onProcessAfter() 
{

}

function onGetElevationBefore(latitude, longitude) 
{	
	return DemConstants.ELEV_UNDETERMINED
}

function onGetElevationAfter(latitude, longitude, elevation) 
{	 
	return elevation
}


function onGetPointColor(latitude, longitude, elevation, elevationMinimum, elevationMaximum, color)
{
	
}

function onLightLevels(latitude, longitude)
{	
	
}

function onBeforeVertex(latitude, longitude, elevation, renderer, view)
{
	
}

function preRender(renderer, view)
{

}

function postRender(renderer, view)
{

}



function destroy()
{ 

}