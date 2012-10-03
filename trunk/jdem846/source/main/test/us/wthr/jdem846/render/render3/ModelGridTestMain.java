package us.wthr.jdem846.render.render3;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptionNamesEnum;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.image.ImageDataContext;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.lighting.LightSourceSpecifyTypeEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.canvas.CanvasProjectionTypeEnum;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class ModelGridTestMain extends AbstractTestMain
{

	
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ModelGridTestMain.class);
		
		try {
			ModelGridTestMain testMain = new ModelGridTestMain();
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
		//dataProxy.fillBuffers();
		
		ImageDataContext imageDataContext = new ImageDataContext();
		for (String orthoImageDataPath : orthoImageDataList) {
			SimpleGeoImage image = new SimpleGeoImage(orthoImageDataPath, dataProxy.getNorth(), dataProxy.getSouth(), dataProxy.getEast(), dataProxy.getWest());
			imageDataContext.addImage(image);
		}
		imageDataContext.loadImageData();
		
		LightingContext lightingContext = new LightingContext();
		lightingContext.setLightingEnabled(true);
		lightingContext.setLightSourceSpecifyType(LightSourceSpecifyTypeEnum.BY_AZIMUTH_AND_ELEVATION);
		lightingContext.setLightingMultiple(3);
		//lightingContext.setLightingOnDate(1330442235000l);
		lightingContext.setRecalcLightOnEachPoint(true);
		lightingContext.setRayTraceShadows(false);
		
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setScriptLanguage(ScriptLanguageEnum.GROOVY);

		double width = 1000;//dataProxy.getDataColumns();
		double aspect = (double)dataProxy.getDataColumns() / (double)dataProxy.getDataRows();
		
		modelOptions.setWidth((int)width);//dataProxy.getDataColumns());
		modelOptions.setHeight((int) Math.round(width/aspect));//dataProxy.getDataRows());
		modelOptions.setAntialiased(false);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR);
		//modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_NONE);
		modelOptions.setBackgroundColor("255;255;255;255");
		modelOptions.setElevationMultiple(2.0);
		modelOptions.setColoringType("hypsometric-tint");
		modelOptions.setModelProjection(CanvasProjectionTypeEnum.PROJECT_3D);
		modelOptions.setOption(ModelOptionNamesEnum.ESTIMATE_ELEVATION_MIN_MAX, ""+false);
		modelOptions.setOption(ModelOptionNamesEnum.SUBPIXEL_WIDTH, ""+1);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, null, imageDataContext, lightingContext, modelOptions);
		modelContext.updateContext(true, true);
		
		
		
		ModelGrid modelGrid = new ModelGrid(modelContext.getNorth(), 
											modelContext.getSouth(), 
											modelContext.getEast(), 
											modelContext.getWest(), 
											modelContext.getModelDimensions().getTextureLatitudeResolution(), 
											modelContext.getModelDimensions().getTextureLongitudeResolution());
		
		double startTime = System.currentTimeMillis();
		
		log.info("Building in-memory model...");
		ModelBuilder modelBuilder = new ModelBuilder(modelContext, modelGrid);
		modelBuilder.prepare();
		modelBuilder.setUseScripting(false);
		modelBuilder.getGridLoadProcessor().setUseScripting(false);
		modelBuilder.process();
		
		dataProxy.clearBuffers();
		imageDataContext.unloadImageData();
		
		log.info("Rendering model to image...");
		ModelRenderer modelRenderer = new ModelRenderer(modelContext, modelGrid);
		modelRenderer.prepare();
		modelRenderer.process();
		
		modelGrid.dispose();
		
		
		double endTime = System.currentTimeMillis();
		
		ModelCanvas canvas = modelContext.getModelCanvas();
		canvas.save(saveOutputTo);
		
		double renderSeconds = (endTime - startTime) / 1000;
		log.info("Completed render in " + renderSeconds + " seconds");
	}
	


}
