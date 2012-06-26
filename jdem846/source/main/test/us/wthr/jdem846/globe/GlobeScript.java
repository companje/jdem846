package us.wthr.jdem846.globe;


import us.wthr.jdem846.*;
import us.wthr.jdem846.logging.*;
import us.wthr.jdem846.image.*;
import us.wthr.jdem846.input.*;
import us.wthr.jdem846.math.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.*;
import us.wthr.jdem846.color.ColorAdjustments;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.*;
import us.wthr.jdem846.canvas.*;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ModelContextException;
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.geom.*;
import us.wthr.jdem846.gis.planets.*;
import us.wthr.jdem846.model.*;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.model.processing.util.*;
import us.wthr.jdem846.model.processing.shading.*;
import us.wthr.jdem846.scripting.ScriptProxy;



public class GlobeScript implements ScriptProxy
{

	private static Log log = Logging.getLog(GlobeScript.class);
	
    ModelContext modelContext;

    MapPoint point = new MapPoint();
    int[] rgbaBufferA = new int[4];
    int[] rgbaBufferB = new int[4];
    LightingCalculator oceanLightingCalculator;
    LightingCalculator cloudsLightingCalculator;
    LightingCalculator atmosphereLightingCalculator;
    double modelRadius = DemConstants.ELEV_NO_DATA;
    ModelDimensions modelDimensions;
    ViewPerspective viewPerspective;
    
    int[] atmosphereRgba = new int[4];

    double[] normal = new double[3];
    double[] sunsource = new double[3];
    SurfaceNormalCalculator normalsCalculator;

    double oceanShininess = 10;
    double nightStartsAt = 0.05;
    
    SunlightPositioning sunlightPosition;


    double latitudeResolution;
    double longitudeResolution;

    double cloudElevation;
    double seaLevelElevation;

    double north;
    double south;
    double east;
    double west;

    AtmosphereLighting lowerAtmosphereLighting = new AtmosphereLighting();
    AtmosphereLighting middleAtmosphereLighting = new AtmosphereLighting();
    AtmosphereLighting upperAtmosphereLighting = new AtmosphereLighting();

    

    double[] oceanEye = new double[3];
    double[] eye = new double[3];
    double[] P = new double[3];
    double[] V = new double[3];
    double[] H = new double[3];
    double[] L = new double[3];
    double[] N = new double[3];
    
    SimpleGeoImage cloudImage;
    SimpleGeoImage nightImage;
    SimpleGeoImage blueMarbleImage;
    
    
    StripRenderQueue stripQueue = null;
    
    public GlobeScript()
    {
    	
    }
    
    boolean doOceanSurfaceRender = true;
    boolean doCloudRender = true;
    boolean doLowerAtmosphereRender = true;
    boolean doMiddleAtmosphereRender = true;
    boolean doUpperAtmosphereRender = true;
    
    public GlobeScript(boolean doOceanSurfaceRender, 
    					boolean doCloudRender,
    					boolean doLowerAtmosphereRender,
    					boolean doMiddleAtmosphereRender,
    					boolean doUpperAtmosphereRender)
    {
    	this.doOceanSurfaceRender = doOceanSurfaceRender;
    	this.doCloudRender = doCloudRender;
    	this.doLowerAtmosphereRender = doLowerAtmosphereRender;
    	this.doMiddleAtmosphereRender = doMiddleAtmosphereRender;
    	this.doUpperAtmosphereRender = doUpperAtmosphereRender;
    }
    
    
    
    
    public void setStripQueue(StripRenderQueue stripQueue)
	{
		this.stripQueue = stripQueue;
	}




	public void initialize()
    {
    	lowerAtmosphereLighting.elevation = modelContext.getRasterDataContext().getElevationScaler().scale(4500);
    	lowerAtmosphereLighting.shininess = 10;
    	lowerAtmosphereLighting.glowExtent = 0.3;
    	lowerAtmosphereLighting.color = new int[]{104, 138, 176, 120};
    	
    	
    	middleAtmosphereLighting.elevation = modelContext.getRasterDataContext().getElevationScaler().scale(8000);
    	middleAtmosphereLighting.shininess = 2;
    	middleAtmosphereLighting.glowExtent = 0.37;
    	middleAtmosphereLighting.color = new int[]{209, 220, 232, 200};
    	
    	
    	upperAtmosphereLighting.elevation = modelContext.getRasterDataContext().getElevationScaler().scale(13000);
    	upperAtmosphereLighting.shininess = 2;
    	upperAtmosphereLighting.glowExtent = 0.37;
    	upperAtmosphereLighting.color = new int[]{104, 138, 176, 255};

        
    	GlobalOptionModel globalOptionModel = modelContext.getModelProcessManifest().getGlobalOptionModel();
        modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);
        
        viewPerspective = globalOptionModel.getViewAngle();
        
        Planet planet = PlanetsRegistry.getPlanet(globalOptionModel.getPlanet());
        modelRadius = planet.getMeanRadius() * 1000;

        oceanLightingCalculator = new LightingCalculator(0.0, 0.4, 1.0, 0.40, 0.0, viewPerspective, modelContext);
        cloudsLightingCalculator = new LightingCalculator(0.0, 0.0, 2.0, 1.5, 0.0, viewPerspective);
        atmosphereLightingCalculator = new LightingCalculator(0.2, 0.2, 1.5, 0.0, 0.0, viewPerspective);
        
        long lightOnTime = 0;
		try {
			lightOnTime = ((LightingTime)modelContext.getModelProcessManifest().getPropertyById("us.wthr.jdem846.model.HillshadingOptionModel.sunlightTime")).getTime();
		} catch (ModelContainerException ex) {
			ex.printStackTrace();
		}
		
        long lightOnDate = 0;
		try {
			lightOnDate = ((LightingDate)modelContext.getModelProcessManifest().getPropertyById("us.wthr.jdem846.model.HillshadingOptionModel.sunlightDate")).getDate();
		} catch (ModelContainerException ex) {
			ex.printStackTrace();
		}
        lightOnDate = lightOnDate + lightOnTime;

        sunlightPosition = new SunlightPositioning(modelContext, null, lightOnDate, viewPerspective);
        sunlightPosition.getLightPositionByCoordinates(0.0, 0.0, sunsource);

        normalsCalculator = new SurfaceNormalCalculator(planet,
                               modelDimensions.getOutputLatitudeResolution(),
                               modelDimensions.getOutputLongitudeResolution(),
                               viewPerspective);

        latitudeResolution = modelDimensions.getOutputLatitudeResolution();
        longitudeResolution = modelDimensions.getOutputLongitudeResolution();

        cloudElevation = modelContext.getRasterDataContext().getElevationScaler().scale(3000);
        seaLevelElevation = modelContext.getRasterDataContext().getElevationScaler().scale(0);
  
        north = modelContext.getModelProcessManifest().getGlobalOptionModel().getNorthLimit();
        south = modelContext.getModelProcessManifest().getGlobalOptionModel().getSouthLimit();
        east = modelContext.getModelProcessManifest().getGlobalOptionModel().getEastLimit();
        west = modelContext.getModelProcessManifest().getGlobalOptionModel().getWestLimit();

        
        Spheres.getPoint3D(-90.0, 0, modelRadius*10, eye);

        //String cloudImagePath = "F:\\bluemarble\\cloud_combined_8192.png";
        //String cloudImagePath = "F:\\bluemarble\\cloud_combined_5000.png";
        //String cloudImagePath = "C:\\srv\\elevation\\Earth\\cloud_combined_trans_2048.png";
        String cloudImagePath = "C:\\srv\\elevation\\Earth\\cloud_combined_trans_8192.png";
        
        if (GlobalStorageContainer.hasResource("global-clouds")) {
            log.info("Loading cloud image from global storage");
            cloudImage = (SimpleGeoImage)  GlobalStorageContainer.get("global-clouds");
        } else {
            log.info("Loading cloud image from disk");
            
            try {
				cloudImage = new SimpleGeoImage(cloudImagePath, 90, -90, 180, -180);
				cloudImage.load();
			} catch (DataSourceException ex) {
				ex.printStackTrace();
			}
            
            GlobalStorageContainer.put("global-clouds", cloudImage);
        }
        
        
        String nightEarthImage = "C:\\srv\\elevation\\Earth\\earth_lights_transparent_8192x4096.png";
        //String nightEarthImage = "F:\\bluemarble\\earth_lights_transparent_8192x4096.png";
        boolean forceReloadNightImage = false;
        if (!forceReloadNightImage && GlobalStorageContainer.hasResource("global-lights")) {
            log.info("Loading night lights image from global storage");
            nightImage = (SimpleGeoImage)  GlobalStorageContainer.get("global-lights");
        } else {
            log.info("Loading night lights image from disk");
            
            try {
				nightImage = new SimpleGeoImage(nightEarthImage, 90, -90, 180, -180);
				nightImage.load();
			} catch (DataSourceException ex) {
				ex.printStackTrace();
			}
            
            GlobalStorageContainer.put("global-lights", nightImage);
        }
        
        
        String blueMarbleImagePath = "C:\\srv\\elevation\\Earth\\world.200407.3x5400x2700.png";
        //String blueMarbleImagePath = "F:\\bluemarble\\august\\world.200408.3x21600x10800.png";
        boolean forceReloadBlueMarbleImage = false;
        if (!forceReloadBlueMarbleImage && GlobalStorageContainer.hasResource("blue-marble")) {
            log.info("Loading blue marble image from global storage");
            blueMarbleImage = (SimpleGeoImage) GlobalStorageContainer.get("blue-marble");
        } else {
            log.info("Loading blue marble image from disk");
            
            try {
				blueMarbleImage = new SimpleGeoImage(blueMarbleImagePath, 90, -90, 180, -180);
				blueMarbleImage.load();
			} catch (DataSourceException ex) {
				ex.printStackTrace();
			}
            
            GlobalStorageContainer.put("blue-marble", blueMarbleImage);
        }
        
    }
    
    /*
    def onModelBefore = {


    }

    def onProcessBefore = { modelProcessContainer ->


    }
    */
    

    public void onLightLevels(double latitude, double longitude, LightingValues lightingValues)
    {
        double dot = getPointDotProduct(latitude, longitude, modelRadius + seaLevelElevation);
        if (dot <= nightStartsAt && isUnderCityLights(latitude, longitude)) {
            
        	double emmisive = 1.0;
            if (dot >= 0) {
                emmisive = emmisive * (1.0 - (dot / nightStartsAt));
            }
            
            lightingValues.emmisiveLight = emmisive;
        } else {
            lightingValues.emmisiveLight = 0.0;
        }
        
        /*if (dot < -0.05) {
            lightingValues.specularLight = 0;
            lightingValues.diffuseLight = 0;
        } else */if (doCloudRender) {
        	try {
        		cloudImage.getColor(latitude, longitude, latitudeResolution, longitudeResolution, rgbaBufferB);
        	} catch (DataSourceException ex) {
        		ex.printStackTrace();
        	}
	     
        	if (rgbaBufferB[3] > 25) {
        		lightingValues.specularLight = lightingValues.specularLight * (1.0 - (((double)(rgbaBufferB[3] - 25) / 230.0) * 0.85));
        		lightingValues.diffuseLight = lightingValues.diffuseLight * (1.0 - (((double)(rgbaBufferB[3] - 25) / 230.0) * 0.7));
        	}
        }
	}

    public void onProcessAfter(ModelProcessContainer modelProcessContainer)
    {
        if (!modelProcessContainer.getProcessId().equals("us.wthr.jdem846.model.processing.render.ModelRenderer")) {
            return;
        }
        
        double resolutionReduction = 1.0;


        final CanvasProjection modelProjection = makeModelProjection(modelProcessContainer);

        
        //List<RenderMethod> renderMethods = new ArrayList<RenderMethod>();
        
        int methodCount = 0;
        methodCount = methodCount + ((doLowerAtmosphereRender) ? 1 : 0);
        methodCount = methodCount + ((doMiddleAtmosphereRender) ? 1 : 0);
        methodCount = methodCount + ((doUpperAtmosphereRender) ? 1 : 0);
        methodCount = methodCount + ((doOceanSurfaceRender) ? 1 : 0);
        methodCount = methodCount + ((doCloudRender) ? 1 : 0);
        

        //LayerRenderThread[] renderThreads = new LayerRenderThread[methodCount];
        RenderMethod[] renderMethods = new RenderMethod[methodCount];
        
        
        int methodIndex = 0;
        
        if (doLowerAtmosphereRender) {
        	log.info("Adding lower atmospheric rendering method");
        	renderMethods[methodIndex++] = new RenderMethod() {
	        	public void renderPoint(double lat, double lon, TriangleStrip strip)
	        	{
	        		renderAtmosphere(lat, lon, strip, modelProjection, lowerAtmosphereLighting);
	        	}
	        };
        }
        
        if (doMiddleAtmosphereRender) {
        	log.info("Adding middle atmospheric rendering method");
        	renderMethods[methodIndex++] = new RenderMethod() {
	        	public void renderPoint(double lat, double lon, TriangleStrip strip)
	        	{
	        		renderAtmosphere(lat, lon, strip, modelProjection, middleAtmosphereLighting);
	        	}
	        };
        }
	    
        if (doUpperAtmosphereRender) {
        	log.info("Adding upper atmospheric rendering method");
        	renderMethods[methodIndex++] = new RenderMethod() {
	        	public void renderPoint(double lat, double lon, TriangleStrip strip)
	        	{
	        		renderAtmosphere(lat, lon, strip, modelProjection, upperAtmosphereLighting);
	        	}
	        };
        }
	        

        if (doOceanSurfaceRender) {
        	log.info("Adding ocean surface rendering method");
        	renderMethods[methodIndex++] = new RenderMethod() {
	        	public void renderPoint(double lat, double lon, TriangleStrip strip)
	        	{
	        		renderOceanSurface(lat, lon, seaLevelElevation, strip, modelProjection);
	        	}
        	};
        }
        
        if (doCloudRender) {
        	log.info("Adding cloud rendering method");
        	
        	renderMethods[methodIndex++] = new RenderMethod() {
	        	public void renderPoint(double lat, double lon, TriangleStrip strip)
	        	{
	        		renderClouds(lat, lon, cloudElevation, strip, modelProjection, cloudImage);
	        	}
        	};
        }
        

        
        latitudeResolution = modelDimensions.getOutputLatitudeResolution();
        longitudeResolution = modelDimensions.getOutputLongitudeResolution();
        cycleCoordinates(renderMethods);
        
        /*
        for (LayerRenderThread layerThread : renderThreads) {
        	log.info("Starting layer thread");
        	layerThread.start();
        }
        
        
        boolean keepRunning = true;
        
        while (keepRunning) {
        	
        	keepRunning = false;
        	for (LayerRenderThread layerThread : renderThreads) {
        		if (!layerThread.isCompleted()) {
        			keepRunning = true;
        			break;
        		}
        	}
        	
        	
        	try {
				Thread.sleep(500);
				Thread.yield();
			} catch (InterruptedException ez) {
				ez.printStackTrace();
			}
        }
        
        
        log.info("Layer threads completed");
    	*/

    }

    //def cycleCoordinates = { renderMethods ->
    protected void cycleCoordinates(RenderMethod[] renderMethods)
    {
    	
        double maxLon = east;
        double minLat = south;
        
        TriangleStrip[] strips = new TriangleStrip[renderMethods.length];
        


        for (double lat = north; lat > minLat; lat-=latitudeResolution) {
        	
        	for (int i = 0; i < renderMethods.length; i++) {
            	strips[i] = new TriangleStrip();
            }
        	
            for (double lon = west; lon < maxLon; lon+=longitudeResolution) {

            	for (int i = 0; i < renderMethods.length; i++) {
            		RenderMethod method = renderMethods[i];
            		TriangleStrip strip = strips[i];
	
            		method.renderPoint(lat, lon, strip);
            		method.renderPoint(lat-latitudeResolution, lon, strip);
            	}

                
            }
            
            for (TriangleStrip strip : strips) {
            	
            	if (stripQueue != null) {
            		stripQueue.add(strip);
            	} else {
            		try {
						modelContext.getModelCanvas().fillShape(strip);
					} catch (ModelContextException ex) {
						log.error("Error fetching model canvas for rending: " + ex.getMessage(), ex);
					}
            	}

            }

        }
    }


    int[] atmosphereRgbaBuffer = new int[4];
    //def renderAtmosphere = { latitude, longitude, atmosphereStrip, modelProjection, atmosphereLighting ->
    protected void renderAtmosphere(double latitude, double longitude, TriangleStrip atmosphereStrip, CanvasProjection modelProjection, AtmosphereLighting atmosphereLighting) 
    {
    	double glowExtent = atmosphereLighting.glowExtent;
    	double shininess = atmosphereLighting.shininess;
    	double atmosphereElevation = atmosphereLighting.elevation;
        
        atmosphereRgbaBuffer[0] = atmosphereLighting.color[0];
        atmosphereRgbaBuffer[1] = atmosphereLighting.color[1];
        atmosphereRgbaBuffer[2] = atmosphereLighting.color[2];
        atmosphereRgbaBuffer[3] = atmosphereLighting.color[3];
        int atmosphereAlpha = atmosphereLighting.color[3];


        Spheres.getPoint3D(longitude, latitude, modelRadius, P);
        Vectors.rotate(0.0, viewPerspective.getRotateY(), 0, P);
        Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0, P);

        normalsCalculator.calculateNormalSpherical(latitude, longitude, atmosphereElevation, N);

        Vectors.inverse(eye, L);
		Vectors.subtract(L, P, L);
		Vectors.normalize(L, L);

		double dot = Vectors.dotProduct(L, N);
        

		dot = MathExt.pow(dot, shininess);
		double alpha = 0;
        if (dot >= -0.005 && dot < glowExtent) {
           alpha =  ( 1.0 - (MathExt.abs(dot)) / glowExtent) * (double)atmosphereAlpha;
        } else if (dot < -0.005 || dot >= glowExtent) {
           alpha = 0.0;
        }

        atmosphereLightingCalculator.calculateColor(N,
                                                  latitude,
                                                  longitude,
                                                  modelRadius,
                                                  1.0,
                                                  0.0,
                                                  sunsource,
                                                  atmosphereRgbaBuffer);
                                                  
        atmosphereRgbaBuffer[3] = (int) alpha;
        

        Vertex v0 = createVertex(modelProjection, latitude, longitude, atmosphereElevation, atmosphereRgbaBuffer);
        atmosphereStrip.addVertex(v0);
    }

    
    int[] cloudRgbaBuffer = new int[4];
    //def renderClouds = { latitude, longitude, cloudElevation, cloudStrip, modelProjection, image ->
    protected void renderClouds(double latitude, double longitude, double cloudElevation, TriangleStrip cloudStrip, CanvasProjection modelProjection, SimpleGeoImage image) 
    {
        try {
			image.getColor(latitude, longitude, latitudeResolution, longitudeResolution, cloudRgbaBuffer);
		} catch (DataSourceException ex) {
			ex.printStackTrace();
		}
        
        
        //def cloudElevation = modelContext.getRasterDataContext().getElevationScaler().scale(3000)
        int alpha = cloudRgbaBuffer[3];

        normalsCalculator.calculateNormalSpherical(latitude, longitude, cloudElevation, normal);

        cloudsLightingCalculator.calculateColor(normal,
                                                  latitude,
                                                  longitude,
                                                  modelRadius,
                                                  20.0,
                                                  0.0,
                                                  sunsource,
                                                  cloudRgbaBuffer);
        cloudRgbaBuffer[3] = alpha;                               
        Vertex v0 = createVertex(modelProjection, latitude, longitude, cloudElevation, cloudRgbaBuffer);
        cloudStrip.addVertex(v0);
        

    }
    
    
    
    
    int[] oceanRgbaBuffer = new int[4];
    //def renderOceanSurface = { latitude, longitude, seaLevelElevation, oceanStrip, modelProjection ->
    protected void renderOceanSurface(double latitude, double longitude, double seaLevelElevation, TriangleStrip oceanStrip, CanvasProjection modelProjection) 
    {
        getBlueMarbleColor(latitude, longitude, oceanRgbaBuffer, 1.0);
        //def seaLevelElevation = modelContext.getRasterDataContext().getElevationScaler().scale(0)

        normalsCalculator.calculateNormalSpherical(latitude, longitude, seaLevelElevation, normal);

        oceanLightingCalculator.calculateColor(normal,
                                                  latitude,
                                                  longitude,
                                                  modelRadius,
                                                  oceanShininess,
                                                  0.0,
                                                  sunsource,
                                                  oceanRgbaBuffer);
        oceanRgbaBuffer[3] = 0xFF;                     

        Vertex v0 = createVertex(modelProjection, latitude, longitude, seaLevelElevation, oceanRgbaBuffer);
        oceanStrip.addVertex(v0);
        

    }


    //def getPointDotProduct = { latitude, longitude, elevation ->
    public double getPointDotProduct(double latitude, double longitude, double elevation)
    {
        //sunlightPosition.getLightPositionByCoordinates(latitude, longitude, sunsource);
    	
    	
    	if (elevation != DemConstants.ELEV_NO_DATA) {
    		normalsCalculator.calculateNormalSpherical(latitude, longitude, elevation, N);
    	} else {
    		normalsCalculator.calculateNormalSpherical(latitude, longitude, N);
    	}
        
        
        Spheres.getPoint3D(longitude, latitude, elevation, P);
        Vectors.rotate(0.0, viewPerspective.getRotateY(), 0.0, P);
	    Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0.0, P);
        
        Vectors.inverse(sunsource, L);
		Vectors.subtract(L, P, L);
		Vectors.normalize(L, L);

		double dot = Vectors.dotProduct(L, N);
        return dot;
    }

    //def onGetPointColor = { latitude, longitude, elevation, elevationMinimum, elevationMaximum, color ->
    public void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color)
    {
    	double dot = getPointDotProduct(latitude, longitude, modelRadius + elevation);

        if (dot <= nightStartsAt) {
        	double f = 1.0;
            if (dot >= 0) {
                f = 1.0 - (dot / nightStartsAt);
            }
            getCityLightsColor(latitude, longitude, color, f);
        }
    

    }
    
    int[] cityLightsColorBuffer = new int[4];
    protected boolean isUnderCityLights(double latitude,double  longitude)
    {
        try {
			nightImage.getColor(latitude, longitude, latitudeResolution, longitudeResolution, cityLightsColorBuffer);
		} catch (DataSourceException ex) {
			ex.printStackTrace();
		}
        if (cityLightsColorBuffer[3] > 100) {
            return true;
        } else {
            return false;
        }
    }
    
    //def getCityLightsColor = { latitude, longitude, rgba, frac ->
    protected void getCityLightsColor(double latitude,double  longitude, int[] rgba, double frac)
    {
        getImageColor(latitude, longitude, rgba, frac, nightImage);
    }
    
    //def getBlueMarbleColor = { latitude, longitude, rgba, frac ->
    protected void getBlueMarbleColor(double latitude, double longitude, int[] rgba, double frac)
    {
        getImageColor(latitude, longitude, rgba, frac, blueMarbleImage);
    }
    
    
    int[] imageColorBuffer = new int[4];
    protected void getImageColor(double latitude,double longitude, int[] rgba, double frac, SimpleGeoImage image)
    {
        try {
			image.getColor(latitude, longitude, latitudeResolution, longitudeResolution, imageColorBuffer);
		} catch (DataSourceException ex) {
			ex.printStackTrace();
		}
        if (imageColorBuffer[3] > 0) {
            double alphaRatio = (((double)imageColorBuffer[3] / 255.0) * frac) / 2.0;
            ColorAdjustments.interpolateColor(rgba, imageColorBuffer, rgba, alphaRatio);   
        }
        rgba[3] = 0xFF;
    }


    //def makeModelProjection = { modelProcessContainer ->
    public CanvasProjection makeModelProjection(ModelProcessContainer modelProcessContainer)
    {
    	GlobalOptionModel globalOptionModel = modelContext.getModelProcessManifest().getGlobalOptionModel();

    	MapProjection mapProjection = null;
    	ModelDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext);
        try {
            mapProjection = globalOptionModel.getMapProjectionInstance();
        } catch (Exception ex) {
            log.warn("Error creating map projection: " + ex.getMessage(), ex);
            return null;
        }

        CanvasProjection projection = CanvasProjectionFactory.create(
                            CanvasProjectionTypeEnum.getCanvasProjectionEnumFromIdentifier(globalOptionModel.getRenderProjection()),
                            mapProjection,
                            globalOptionModel.getNorthLimit(),
                            globalOptionModel.getSouthLimit(),
                            globalOptionModel.getEastLimit(),
                            globalOptionModel.getWestLimit(),
                            modelDimensions.getOutputWidth(),
                            modelDimensions.getOutputHeight(),
                            PlanetsRegistry.getPlanet(globalOptionModel.getPlanet()),
                            globalOptionModel.getElevationMultiple(),
                            modelContext.getRasterDataContext().getDataMinimumValue(),
                            modelContext.getRasterDataContext().getDataMaximumValue(),
                            (ModelDimensions) modelDimensions,
                            globalOptionModel.getViewAngle());

        return projection;
    }


    //def getPixelColor = { projection, raster, latitude, longitude, cloudCeiling, rgba ->
    protected void getPixelColor(MapProjection projection, Raster raster, double  latitude, double longitude, double cloudCeiling, int[] rgba)
    {
        try {
			projection.getPoint(latitude, longitude, cloudCeiling, point);
		} catch (MapProjectionException ex) {
			ex.printStackTrace();
		}

        if (point.column < 0 || point.column >= raster.getWidth())
            return;
        if (point.row < 0 || point.row >= raster.getHeight())
            return;

        raster.getPixel((int)point.column, (int)point.row, rgba);

    }

    //def createVertex = { canvasProjection, lat, lon, elev, rgba ->
    protected Vertex createVertex(CanvasProjection canvasProjection, double lat, double lon, double elev, int[] rgba)
    {
        try {
			canvasProjection.getPoint(lat, lon, elev, point);
		} catch (MapProjectionException ex) {
			ex.printStackTrace();
		}

        double x = point.column;
        double y = point.row;
        double z = point.z;

        Vertex v = new Vertex(x, y, z, rgba);
        return v;
    }
    
    
    


	@Override
	public void setModelContext(ModelContext modelContext)
	{
		this.modelContext = modelContext;
	}

	@Override
	public void destroy() throws ScriptingException
	{
		
	}

	@Override
	public void onModelBefore() throws ScriptingException
	{
		
	}

	@Override
	public void onModelAfter() throws ScriptingException
	{
		
	}

	@Override
	public void onProcessBefore(ModelProcessContainer modelProcessContainer)
			throws ScriptingException
	{
		
	}

	@Override
	public Object onGetElevationBefore(double latitude, double longitude)
			throws ScriptingException
	{
		
		if (!isPointFacingViewer(latitude, longitude)) {
			return DemConstants.ELEV_NO_DATA;
		} else {
			return null;
		}
		
		
	}

	
	protected boolean isPointFacingViewer(double latitude, double longitude)
	{
		Spheres.getPoint3D(longitude, latitude, modelRadius, P);
        Vectors.rotate(0.0, viewPerspective.getRotateY(), 0, P);
        Vectors.rotate(viewPerspective.getRotateX(), 0.0, 0, P);

        normalsCalculator.calculateNormalSpherical(latitude, longitude, seaLevelElevation, N);

        Vectors.inverse(eye, L);
		Vectors.subtract(L, P, L);
		Vectors.normalize(L, L);

		double dot = Vectors.dotProduct(L, N);
		
		if (dot < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	@Override
	public Object onGetElevationAfter(double latitude, double longitude,
			double elevation) throws ScriptingException
	{
		return null;
	}


    

}
