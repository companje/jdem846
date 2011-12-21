package us.wthr.jdem846.project;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.render2d.ModelRenderer;

public class RayTracedShadowTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(RayTracedShadowTestMain.class);
				
		RayTracedShadowTestMain tester = new RayTracedShadowTestMain();
		try {
			tester.doTesting(args);
		} catch(Exception ex) {
			log.error("Error running test: " + ex.getMessage(), ex);
		}
	}
	
	public void doTesting(String[] args) throws Exception
	{
		List<String> inputDataList = new LinkedList<String>();
		
		inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		String saveOutputTo = "C:/srv/elevation/DataRaster-Testing/output/{iter}-test-output.jpg";
		
		
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		dataProxy.calculateElevationMinMax(true);
		dataProxy.fillBuffers();
		log.info("Raster Data Maximum Value: " + dataProxy.getDataMaximumValue());
		log.info("Raster Data Minimum Value: " + dataProxy.getDataMinimumValue());
		
		LightingContext lightingContext = new LightingContext();
		lightingContext.setRayTraceShadows(true);
		lightingContext.setShadowIntensity(0.40);
		lightingContext.setLightingElevation(10);
		//lightingContext.setLightingAzimuth(100);
		
		ModelOptions modelOptions = new ModelOptions();
		
		modelOptions.setTileSize(5000);
		modelOptions.setWidth(dataProxy.getDataColumns());
		modelOptions.setHeight(dataProxy.getDataRows());
		modelOptions.setDoublePrecisionHillshading(false);
		modelOptions.setUseSimpleCanvasFill(false);
		modelOptions.setAntialiased(true);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR);
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_NONE);
		ModelContext modelContext = ModelContext.createInstance(dataProxy, lightingContext, modelOptions);
		
		/*
		lightingContext.setLightingAzimuth(0);
		ModelCanvas canvas = ModelRenderer.render(modelContext);
		String outputFileName = saveOutputTo.replace("{iter}", ""+((int)0));
		canvas.save(outputFileName);
		*/
		
		/*
		for (double azimuth = 0; azimuth < 360; azimuth += 20) {
			lightingContext.setLightingAzimuth(azimuth);
			ModelCanvas canvas = ModelRenderer.render(modelContext);
			String outputFileName = saveOutputTo.replace("{iter}", ""+((int)azimuth));
			canvas.save(outputFileName);
		}
		*/
		
		lightingContext.setLightingElevation(0);
		ModelCanvas canvas = ModelRenderer.render(modelContext);
		String outputFileName = saveOutputTo.replace("{iter}", ""+((int)0));
		canvas.save(outputFileName);
		/*
		for (double elevation = 0; elevation <= 20; elevation+=1.0) {
			
			lightingContext.setLightingElevation(elevation);
			ModelCanvas canvas = ModelRenderer.render(modelContext);
			String outputFileName = saveOutputTo.replace("{iter}", ""+((int)elevation));
			canvas.save(outputFileName);
		}
		*/
	}
	
}
