
importPackage(us.wthr.jdem846);
importPackage(us.wthr.jdem846.logging);
importPackage(us.wthr.jdem846.image);
importPackage(us.wthr.jdem846.input);
importPackage(us.wthr.jdem846.math);
importPackage(java.io);
importPackage(javax.imageio);
importPackage(java.awt.image);
importPackage(us.wthr.jdem846.color);
importPackage(us.wthr.jdem846.gis.projections);
importPackage(us.wthr.jdem846.canvas);
importPackage(us.wthr.jdem846.canvas.util);
importPackage(us.wthr.jdem846.geom);
importPackage(us.wthr.jdem846.gis.planets);
importPackage(us.wthr.jdem846.model);
importPackage(us.wthr.jdem846.model.processing.util);
importPackage(us.wthr.jdem846.model.processing.shading);
importPackage(us.wthr.jdem846.globe);
importPackage(us.wthr.jdem846.graphics);

var Context = {
		
	log : null,
	modelContext : null,
	globalOptionModel : null,
	modelGrid : null,
	modelDimensions : null
		
};


function setLog(log)
{
	Context.log = log;
}

function setModelContext(modelContext)
{
	Context.modelContext = modelContext;
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


function preRender(renderer, view)
{

}

function postRender(renderer, view)
{

}

function destroy()
{ 

}