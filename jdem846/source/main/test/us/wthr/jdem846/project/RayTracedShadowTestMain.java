package us.wthr.jdem846.project;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.gis.Coordinate;
import us.wthr.jdem846.gis.CoordinateTypeEnum;
import us.wthr.jdem846.gis.datetime.EarthDateTime;
import us.wthr.jdem846.gis.datetime.SolarPosition;
import us.wthr.jdem846.gis.datetime.SolarUtil;
import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.render.render2d.ModelRenderer;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.scripting.ScriptProxy;
import us.wthr.jdem846.scripting.ScriptProxyFactory;

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
	
	public String readFile(String path) throws Exception
	{
		InputStream in = JDemResourceLoader.getAsInputStream(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[1024];
		int length = 0;
		
		while ((length = in.read(buffer)) > 0) {
			baos.write(buffer, 0, length);
		}
		
		return new String(baos.toByteArray());
	}
	
	public void doTesting(String[] args) throws Exception
	{
		List<String> inputDataList = new LinkedList<String>();
		
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1-3as.flt");
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		//String saveOutputTo = "C:/srv/elevation/DataRaster-Testing/output/{iter}-test-output.png";
		
		//inputDataList.add("C:/srv/elevation/Monument Valley/69484853.flt");
		//String saveOutputTo = "C:/srv/elevation/Monument Valley/output-frame-{iter}.png";
		
		inputDataList.add("C:/srv/elevation/Monument Valley/69484853.flt");
		String saveOutputTo = "C:/srv/elevation/Monument Valley/output/frame-{iter}.png";
		String orthoScript = "C:/srv/elevation/Monument Valley/ortho-script.groovy";
		
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
		//lightingContext.setLightingElevation(10);
		//lightingContext.setLightingAzimuth(100);
		
		String script = readFile(orthoScript);
		//System.out.println(script);
		ModelOptions modelOptions = new ModelOptions();
		modelOptions.setUserScript(script);
		modelOptions.setScriptLanguage(ScriptLanguageEnum.GROOVY);
		modelOptions.setTileSize(5000);
		modelOptions.setWidth(dataProxy.getDataColumns());
		modelOptions.setHeight(dataProxy.getDataRows());
		modelOptions.setDoublePrecisionHillshading(false);
		modelOptions.setUseSimpleCanvasFill(false);
		modelOptions.setAntialiased(true);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR3D);
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_NONE);
		modelOptions.setBackgroundColor("255;255;255;255");
		modelOptions.getProjection().setRotateX(30);
		
		ScriptProxy scriptProxy = ScriptProxyFactory.createScriptProxy(ScriptLanguageEnum.GROOVY, script);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, lightingContext, modelOptions, scriptProxy);
		
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
		
		/*
		lightingContext.setLightingAzimuth(180.0);
		lightingContext.setLightingElevation(5.0);
		
		
		Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		OutputProduct<ModelCanvas> product = dem2d.generate();
		ModelCanvas canvas = product.getProduct();
		String outputFileName = saveOutputTo.replace("{iter}", ""+((int)0));
		canvas.save(outputFileName);
		*/
		
		 
		double north = 37.02619;
		double south = 36.9518;
		double east = -110.05277;
		double west = -110.16406;
		
		double lightLat = (north + south) / 2.0;
		double lightLon = (east + west) / 2.0;
		
		Coordinate latitude = new Coordinate(lightLat, CoordinateTypeEnum.LATITUDE);
		Coordinate longitude = new Coordinate(lightLon, CoordinateTypeEnum.LONGITUDE);
		SolarPosition position = new SolarPosition();
		EarthDateTime datetime = new EarthDateTime(2011, 12, 22, 12, 0, 0, -7, false);
		
		
		SolarUtil.getSolarPosition(datetime, latitude, longitude, position);
		double sunrise = position.getApparentSunrise().toMinutes();
		double sunset = position.getApprentSunset().toMinutes();
		
		LinkedList<FrameRenderRunnable> frameQueue = new LinkedList<FrameRenderRunnable>();
		
		
		int frame = 1;
		for (double dayMinutes = sunrise; dayMinutes <= sunset; dayMinutes+=1.0) {
			String outputFileName = saveOutputTo.replace("{iter}", ""+(1000000 + frame));
			
			datetime.fromMinutes(dayMinutes);
			
			SolarPosition framePosition = new SolarPosition();
			SolarUtil.getSolarPosition(datetime, latitude, longitude, framePosition);
			//FrameParams params = new FrameParams(framePosition, frame);
			
			lightingContext.setLightingAzimuth(framePosition.getAzimuth());
			lightingContext.setLightingElevation(framePosition.getElevation());
			
			FrameRenderRunnable runnable = new FrameRenderRunnable(modelContext.copy(), outputFileName, frame);
			frameQueue.addLast(runnable);
			//exec.execute(runnable);
			//frameQueue.addLast(params);
			frame++;
		}
		//exec.shutdown();
		//
		ExecutorService exec = Executors.newFixedThreadPool(6);
		exec.invokeAll(frameQueue, 1000, TimeUnit.MILLISECONDS);
		
		exec.shutdown();
		exec.awaitTermination(1000, TimeUnit.MILLISECONDS);
		log.info("All Done.");
		/*
		
			log.info("Frame #" + frame + " - " + datetime.toString() + " - " + position.getAzimuth() + " - " + position.getElevation() + " - " + outputFileName);
			
			lightingContext.setLightingAzimuth(position.getAzimuth());
			lightingContext.setLightingElevation(position.getElevation());
			
			Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
			OutputProduct<ModelCanvas> product = dem2d.generate();
			ModelCanvas canvas = product.getProduct();
			canvas.save(outputFileName);
			
			modelContext.resetModelCanvas();
			frame++;
		}
		*/
		
		
		/*
		for (double elevation = 0; elevation <= 20; elevation+=1.0) {
			
			lightingContext.setLightingElevation(elevation);
			ModelCanvas canvas = ModelRenderer.render(modelContext);
			String outputFileName = saveOutputTo.replace("{iter}", ""+((int)elevation));
			canvas.save(outputFileName);
		}
		*/
	}
	
	private static class FrameRenderRunnable implements Runnable, Callable<Boolean>
	{

		private final ModelContext modelContext;
		private final int frameNumber;
		private final String outputFileName;
		
		FrameRenderRunnable(ModelContext modelContext, String outputFileName, int frameNumber)
		{
			this.modelContext = modelContext;
			this.frameNumber = frameNumber;
			this.outputFileName = outputFileName;
		}

		
		public void run()
		{
			__run();
		}
		
		public Boolean call()
		{
			__run();
			return true;
		}
		
		public void __run()
		{
			LightingContext lightingContext = modelContext.getLightingContext();
			log.info("Starting Frame #" + frameNumber + " - "
					+ lightingContext.getLightingAzimuth() + " - "
					+ lightingContext.getLightingElevation() + " - "
					+ outputFileName);
			
			
			try {
				modelContext.getRasterDataContext().fillBuffers();
				Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
				OutputProduct<ModelCanvas> product = dem2d.generate();
				modelContext.getRasterDataContext().clearBuffers();
				ModelCanvas canvas = product.getProduct();
				canvas.save(outputFileName);
			} catch (Exception ex) {
				log.error("Error generating frame #" + frameNumber + ": " + ex.getMessage(), ex);
			}
			
			log.info("Completed Frame #" + frameNumber + " - "
					+ lightingContext.getLightingAzimuth() + " - "
					+ lightingContext.getLightingElevation() + " - "
					+ outputFileName);
			
			modelContext.resetModelCanvas();
			try {
				modelContext.getRasterDataContext().dispose();
			} catch (DataSourceException ex) {
				log.error("Error disposing of frame #" + frameNumber + " raster data: " + ex.getMessage(), ex);
			}
			
		}
	}

}
