package us.wthr.jdem846.kml;

import java.awt.image.BufferedImage;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.ImageIcons;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.LightingDate;
import us.wthr.jdem846.model.LightingTime;
import us.wthr.jdem846.model.ModelBuilder;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.model.RgbaColor;
import us.wthr.jdem846.model.processing.coloring.HypsometricColorOptionModel;
import us.wthr.jdem846.model.processing.dataload.GridLoadOptionModel;
import us.wthr.jdem846.model.processing.dataload.SurfaceNormalsOptionModel;
import us.wthr.jdem846.model.processing.render.ModelRenderOptionModel;
import us.wthr.jdem846.model.processing.shading.HillshadingOptionModel;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.render.KmlDemGenerator;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.scaling.ElevationScalerEnum;

public class KmlExportTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	String testData = "C:\\srv\\elevation\\Earth\\etopo1_ice_g_f4.flt";
	String testImage = "C:\\srv\\elevation\\kml\\kml-testing-earth.png";
	
	String outputPath = "C:\\srv\\elevation\\kml\\kml";
	String tempPath = "C:\\srv\\elevation\\kml\\temp";
	ImageTypeEnum imageType = ImageTypeEnum.PNG;
	int overlayTileSize = 256;
	int layerMultiplier = 2;
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(KmlExportTestMain.class);
		
		try {
			KmlExportTestMain testMain = new KmlExportTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	
	
	public void doTesting() throws Exception
	{
		
		
		
		log.info("Loading test image from " + testImage);
		BufferedImage image = (BufferedImage) ImageIcons.loadImage(testImage);
		
		ModelContext modelContext = createModelContext();
		
		KmlDemGenerator generator = new KmlDemGenerator(modelContext);
		generator.setOutputPath(outputPath);
		generator.setTempPath(tempPath);
		generator.setOverlayTileSize(overlayTileSize);
		generator.setLayerMultiplier(layerMultiplier);
		generator.setImageType(imageType);
		OutputProduct<KmlDocument> product = generator.generate();
		
		log.info("Testing completed");
	}
	
	
	protected ModelContext createModelContext() throws Exception
	{
		
		
		RasterDataContext dataProxy = new RasterDataContext();
		RasterData rasterData = RasterDataProviderFactory.loadRasterData(testData);
		dataProxy.addRasterData(rasterData);
		dataProxy.prepare();
		
		ImageDataContext imageDataContext = new ImageDataContext();
		imageDataContext.loadImageData();
		
		
		GlobalOptionModel globalOptionModel = new GlobalOptionModel();
		double width = 3000;
		double aspect = (double)dataProxy.getDataColumns() / (double)dataProxy.getDataRows();
		
		globalOptionModel.setUseScripting(false);
		globalOptionModel.setWidth((int)width);
		globalOptionModel.setHeight((int) Math.round(width/aspect));
		globalOptionModel.setMaintainAspectRatio(true);
		globalOptionModel.setPlanet("Earth");
		globalOptionModel.setEstimateElevationRange(false);
		globalOptionModel.setLimitCoordinates(false);
		globalOptionModel.setNorthLimit(dataProxy.getNorth());
		globalOptionModel.setSouthLimit(dataProxy.getSouth());
		globalOptionModel.setEastLimit(dataProxy.getEast());
		globalOptionModel.setWestLimit(dataProxy.getWest());
		globalOptionModel.setBackgroundColor(RgbaColor.fromString("rgba:[255,0,0,255]"));
		globalOptionModel.setElevationMultiple(1.0);
		globalOptionModel.setElevationScale(ElevationScalerEnum.LINEAR.identifier());
		globalOptionModel.setRenderProjection(CanvasProjectionTypeEnum.PROJECT_FLAT.identifier());
		//globalOptionModel.setSubpixelGridSize(1);
		globalOptionModel.setModelQuality(1.0);
		//globalOptionModel.setLatitudeSlices(-1);
		//globalOptionModel.setLongitudeSlices(-1);
		
		
		GridLoadOptionModel gridLoadOptionModel = new GridLoadOptionModel();
		SurfaceNormalsOptionModel surfaceNormalOptionModel = new SurfaceNormalsOptionModel();
		
		
		HypsometricColorOptionModel hypsometricColorOptionModel = new HypsometricColorOptionModel();
		hypsometricColorOptionModel.setColorTint("hypsometric-etopo1-tint");
		
		HillshadingOptionModel hillshadingOptionModel = new HillshadingOptionModel();
		hillshadingOptionModel.setLightingEnabled(true);
		//hillshadingOptionModel.setSourceType(LightSourceSpecifyTypeEnum.BY_DATE_AND_TIME.optionValue());
		//hillshadingOptionModel.setSourceLocation(new AzimuthElevationAngles(270, 20));
		//hillshadingOptionModel.setRecalcLightForEachPoint(false);
		//hillshadingOptionModel.setDarkZenith(108.0);
		//hillshadingOptionModel.setLightZenith(90.0);
		//hillshadingOptionModel.setLightMultiple(6.0);
		hillshadingOptionModel.setLightIntensity(0.6);
		hillshadingOptionModel.setDarkIntensity(1.0);
		hillshadingOptionModel.setSpotExponent(1);
		hillshadingOptionModel.setRayTraceShadows(false);
		hillshadingOptionModel.setShadowIntensity(0.4);
		hillshadingOptionModel.setAdvancedLightingControl(true);
		hillshadingOptionModel.setDiffuse(1.3);
		hillshadingOptionModel.setEmmisive(0.0);
		hillshadingOptionModel.setAmbient(0.2);
		hillshadingOptionModel.setSpecular(0.6);
		hillshadingOptionModel.setSunlightDate(LightingDate.fromString("date:[1338422400000]"));
		hillshadingOptionModel.setSunlightTime(LightingTime.fromString("time:[58140000]"));
		
		
		ModelRenderOptionModel modelRenderOptionModel = new ModelRenderOptionModel();
		globalOptionModel.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR.identifier());
		
		
		ModelProcessManifest modelProcessManifest = new ModelProcessManifest();
		modelProcessManifest.setGlobalOptionModel(globalOptionModel);
		
		//modelProcessManifest.addProcessor(new GridLoadProcessor(), gridLoadOptionModel);
		//modelProcessManifest.addProcessor(new SurfaceNormalsProcessor(), surfaceNormalOptionModel);
		//modelProcessManifest.addProcessor(new HypsometricColorProcessor(), hypsometricColorOptionModel);
	//	modelProcessManifest.addProcessor(new HillshadingProcessor(), hillshadingOptionModel);
		//modelProcessManifest.addProcessor(new ModelRenderer(), modelRenderOptionModel);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, null, imageDataContext, modelProcessManifest);
		modelContext.updateContext(true, globalOptionModel.isEstimateElevationRange());
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.prepare(modelContext, modelProcessManifest);
		
		
		
		
		
		
		return modelContext;
	}
}
