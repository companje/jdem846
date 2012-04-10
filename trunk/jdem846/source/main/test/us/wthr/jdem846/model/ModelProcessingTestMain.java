package us.wthr.jdem846.model;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.Projection;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.processing.coloring.AspectColoringOptionModel;
import us.wthr.jdem846.model.processing.coloring.AspectColoringProcessor;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorOptionModel;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorProcessor;
import us.wthr.jdem846.model.processing.coloring.RoughnessColoringOptionModel;
import us.wthr.jdem846.model.processing.coloring.RoughnessColoringProcessor;
import us.wthr.jdem846.model.processing.coloring.TerrainRuggednessIndexColoringOptionModel;
import us.wthr.jdem846.model.processing.coloring.TerrainRuggednessIndexColoringProcessor;
import us.wthr.jdem846.model.processing.coloring.TopographicPositionIndexColoringOptionModel;
import us.wthr.jdem846.model.processing.coloring.TopographicPositionIndexColoringProcessor;
import us.wthr.jdem846.model.processing.dataload.GridLoadOptionModel;
import us.wthr.jdem846.model.processing.dataload.GridLoadProcessor;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsOptionModel;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsProcessor;
import us.wthr.jdem846.model.processing.render.ModelRenderOptionModel;
import us.wthr.jdem846.model.processing.render.ModelRenderer;
import us.wthr.jdem846.model.processing.shading.AspectShadingOptionModel;
import us.wthr.jdem846.model.processing.shading.AspectShadingProcessor;
import us.wthr.jdem846.model.processing.shading.HillshadingOptionModel;
import us.wthr.jdem846.model.processing.shading.HillshadingProcessor;
import us.wthr.jdem846.model.processing.shading.SlopeShadingOptionModel;
import us.wthr.jdem846.model.processing.shading.SlopeShadingProcessor;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.scaling.ElevationScalerEnum;


public class ModelProcessingTestMain extends AbstractTestMain
{
	
	
	
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ModelProcessingTestMain.class);
		
		try {
			ModelProcessingTestMain testMain = new ModelProcessingTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	public void doTesting() throws Exception
	{
		List<String> inputDataList = new LinkedList<String>();
		//List<ShapeFileRequest> inputShapeList = new LinkedList<ShapeFileRequest>();
		List<String> orthoImageDataList = new LinkedList<String>();
		
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1-3as.flt");
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		
		inputDataList.add("C:/srv/elevation/Presidential Range/Elevation 1-3as.flt");
		//orthoImageDataList.add("C:/srv/elevation/Presidential Range/CombinedNaip4Band.jpg");
		
		String saveOutputTo = JDem846Properties.getProperty("us.wthr.jdem846.testOutputPath") + "/render-test.png";
		
		
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		dataProxy.prepare();
		
		ImageDataContext imageDataContext = new ImageDataContext();
		for (String orthoImageDataPath : orthoImageDataList) {
			SimpleGeoImage image = new SimpleGeoImage(orthoImageDataPath, dataProxy.getNorth(), dataProxy.getSouth(), dataProxy.getEast(), dataProxy.getWest());
			imageDataContext.addImage(image);
		}
		imageDataContext.loadImageData();
		
		
		GlobalOptionModel globalOptionModel = new GlobalOptionModel();
		double width = 1000;//dataProxy.getDataColumns();
		double aspect = (double)dataProxy.getDataColumns() / (double)dataProxy.getDataRows();
		

		globalOptionModel.setWidth((int)width);//dataProxy.getDataColumns());
		globalOptionModel.setHeight((int) Math.round(width/aspect));
		globalOptionModel.setMaintainAspectRatio(true);
		globalOptionModel.setPlanet("Earth");
		globalOptionModel.setEstimateElevationRange(false);
		globalOptionModel.setLimitCoordinates(false);
		globalOptionModel.setNorthLimit(dataProxy.getNorth());
		globalOptionModel.setSouthLimit(dataProxy.getSouth());
		globalOptionModel.setEastLimit(dataProxy.getEast());
		globalOptionModel.setWestLimit(dataProxy.getWest());
		globalOptionModel.setBackgroundColor("0;0;0;255");
		globalOptionModel.setElevationMultiple(3.0);
		globalOptionModel.setElevationScale(ElevationScalerEnum.LINEAR.identifier());
		globalOptionModel.setRenderProjection(CanvasProjectionTypeEnum.PROJECT_FLAT.identifier());
		globalOptionModel.setSubpixelGridSize(1);
		
		GridLoadOptionModel gridLoadOptionModel = new GridLoadOptionModel();
		SurfaceNormalsOptionModel surfaceNormalOptionModel = new SurfaceNormalsOptionModel();
		
		HypsometricColorOptionModel hypsometricColorOptionModel = new HypsometricColorOptionModel();
		hypsometricColorOptionModel.setColorTint("hypsometric-tint");
		
		AspectColoringOptionModel aspectColoringOptionModel = new AspectColoringOptionModel();
		
		TerrainRuggednessIndexColoringOptionModel terrainRuggednessIndexColoringOptionModel = new TerrainRuggednessIndexColoringOptionModel();
		TopographicPositionIndexColoringOptionModel topographicPositionIndexColoringOptionModel = new TopographicPositionIndexColoringOptionModel();
		RoughnessColoringOptionModel roughnessColoringOptionModel = new RoughnessColoringOptionModel();
		
		HillshadingOptionModel hillshadingOptionModel = new HillshadingOptionModel();
		hillshadingOptionModel.setLightingEnabled(true);
		hillshadingOptionModel.setSourceType(LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION.optionValue());
		hillshadingOptionModel.setRecalcLightForEachPoint(false);
		hillshadingOptionModel.setDarkZenith(108.0);
		hillshadingOptionModel.setLightZenith(90.0);
		hillshadingOptionModel.setLightMultiple(3.0);
		hillshadingOptionModel.setLightIntensity(0.6);
		hillshadingOptionModel.setDarkIntensity(1.0);
		hillshadingOptionModel.setSpotExponent(1);
		hillshadingOptionModel.setRayTraceShadows(false);
		hillshadingOptionModel.setShadowIntensity(0.4);
		
		SlopeShadingOptionModel slopeShadingOptionModel = new SlopeShadingOptionModel();
		
		AspectShadingOptionModel aspectShadingOptionModel = new AspectShadingOptionModel();
		
		
		ModelRenderOptionModel modelRenderOptionModel = new ModelRenderOptionModel();
		modelRenderOptionModel.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR.identifier());
		
		Projection viewAngle = new Projection();
		viewAngle.setRotateX(30);
		viewAngle.setRotateY(0);
		viewAngle.setRotateZ(0);
		viewAngle.setShiftX(0);
		viewAngle.setShiftY(0);
		viewAngle.setShiftZ(0);
		viewAngle.setZoom(1.0);
		modelRenderOptionModel.setViewAngle(viewAngle);
		
		
		
		
		
		
		
		ModelProcessList modelProcessList = new ModelProcessList();
		
		
		modelProcessList.addProcessor(new GridLoadProcessor(), gridLoadOptionModel);
		modelProcessList.addProcessor(new SurfaceNormalsProcessor(), surfaceNormalOptionModel);
		//modelProcessList.addProcessor(new HypsometricColorProcessor(), hypsometricColorOptionModel);
		//modelProcessList.addProcessor(new AspectColoringProcessor(), aspectColoringOptionModel);
		//modelProcessList.addProcessor(new TerrainRuggednessIndexColoringProcessor(), terrainRuggednessIndexColoringOptionModel);
		//modelProcessList.addProcessor(new TopographicPositionIndexColoringProcessor(), topographicPositionIndexColoringOptionModel);
		modelProcessList.addProcessor(new RoughnessColoringProcessor(), roughnessColoringOptionModel);
		modelProcessList.addProcessor(new HillshadingProcessor(), hillshadingOptionModel);
		//modelProcessList.addProcessor(new SlopeShadingProcessor(), slopeShadingOptionModel);
		//modelProcessList.addProcessor(new AspectShadingProcessor(), aspectShadingOptionModel);
		modelProcessList.addProcessor(new ModelRenderer(), modelRenderOptionModel);
		
		
		
		
		
		ModelOptions modelOptions = new ModelOptions();
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, null, imageDataContext, null, modelOptions);
		modelContext.updateContext(true, globalOptionModel.isEstimateElevationRange());
		
		ModelGridDimensions modelDimensions = ModelGridDimensions.getModelDimensions(modelContext, globalOptionModel);
		
		log.info("Initializing model builder...");
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.setAndPrepare(modelContext, null, modelDimensions, globalOptionModel, null);
		
		log.info("Processing...");
		double startTime = System.currentTimeMillis();
		modelBuilder.process(modelProcessList);
		double endTime = System.currentTimeMillis();
		
		ModelCanvas canvas = modelContext.getModelCanvas();
		canvas.save(saveOutputTo);
		
		double renderSeconds = (endTime - startTime) / 1000;
		log.info("Completed render in " + renderSeconds + " seconds");
		
	}
}
