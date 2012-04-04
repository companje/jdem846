package us.wthr.jdem846;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.gis.projections.MapProjectionEnum;
import us.wthr.jdem846.lighting.LightingContext;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;
import us.wthr.jdem846.render.CanvasProjectionTypeEnum;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.simple.SimpleRenderer;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.ShapeFileRequest;

public class SimpleRendererTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(SimpleRendererTestMain.class);
		
		try {
			SimpleRendererTestMain testMain = new SimpleRendererTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	public void doTesting() throws Exception
	{
		List<String> inputDataList = new LinkedList<String>();
		List<ShapeFileRequest> inputShapeList = new LinkedList<ShapeFileRequest>();
		
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1-3as.flt");
		//inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		//inputDataList.add("F:/DataRaster-Testing/PresRange_1-3as.flt");
		//inputDataList.add("F:/DataRaster-Testing/PresRange_1as.flt");
		//inputDataList.add("F:/Hawaii 1 Arc Second/Honolulu 1-3 Arc Second.flt");
		//String saveOutputTo = "C:/srv/elevation/DataRaster-Testing/model-output.png";
		//inputDataList.add("F:\\GEBCO_08\\gebco_08.flt");
		//inputDataList.add("F:\\MarsGIS_Equi0_v17\\Mars_Images_EquiCylindrical_clon0\\MOLA\\Mola16_90Nto90S_Simp_clon0\\megt90n000eb.flt");
		
		
		inputDataList.add("F:\\mola128_88Nto88S_Simp_clon0\\mola128_88Nto88S_Simp_clon0\\mola128_oc0\\w001001.flt");
		//inputDataList.add("F:\\etopo1_ice_g_f4\\etopo1_ice_g_f4.flt");
		String saveOutputTo = JDem846Properties.getProperty("us.wthr.jdem846.testOutputPath") + "/render-test.png";
		//String saveOutputTo = "F:\\mola128_88Nto88S_Simp_clon0\\mola128_88Nto88S_Simp_clon0\\mola128_88Nto88S_Simp_clon0_jdem_simprndr_8x8.png";
		
		//inputDataList.add("C:/srv/elevation/Nashua NH/Elevation 1-3 Arc Second/Elevation 1-3 Arc Second.flt");
		//inputShapeList.add(new ShapeFileRequest("C:/srv/elevation/Nashua NH/hydrography/NHDArea.shp", "usgs-hydrography"));
		//inputShapeList.add(new ShapeFileRequest("C:/srv/elevation/Nashua NH/hydrography/NHDFlowline.shp", "usgs-hydrography"));
		//inputShapeList.add(new ShapeFileRequest("C:/srv/elevation/Nashua NH/hydrography/NHDWaterbody.shp", "usgs-hydrography"));
		//String saveOutputTo = "C:/srv/elevation/Nashua NH/model-output.png";
		
		
		//inputDataList.add("F:/Presidential Range/02167570.flt");
		//String saveOutputTo = "F:/Presidential Range/model-output.png";
		
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		
		ShapeDataContext shapeContext = new ShapeDataContext();
		for (ShapeFileRequest shapeDataReq : inputShapeList) {
			log.info("Adding shapefile '" + shapeDataReq.getPath() + "'");
			shapeContext.addShapeFile(shapeDataReq);
		}
		
		/*
2012.02.12 21:01:30.156 EST     INFO SimpleRendererTestMain Raster Data Maximum Value: 8685.0
2012.02.12 21:01:30.156 EST     INFO SimpleRendererTestMain Raster Data Minimum Value: -10977.0
		 */
		
		
		double aspect = (double)dataProxy.getDataColumns() / (double)dataProxy.getDataRows();
		
		LightingContext lightingContext = new LightingContext();
		lightingContext.setLightingEnabled(true);
		ModelOptions modelOptions = new ModelOptions();
		//modelOptions.setUserScript(script);
		modelOptions.setScriptLanguage(ScriptLanguageEnum.GROOVY);
		//modelOptions.setTileSize(500);
		
		double width = 1000;
		
		modelOptions.setWidth((int)width);
		modelOptions.setHeight((int) Math.round(width/aspect));
		//modelOptions.setDoublePrecisionHillshading(false);
		//modelOptions.setUseSimpleCanvasFill(false);
		modelOptions.setAntialiased(false);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR);
		//modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		modelOptions.setBackgroundColor("255;255;255;255");
		//modelOptions.setUsePipelineRender(false);
		modelOptions.setColoringType("hypsometric-tint-global");
		modelOptions.setElevationMultiple(1.0);
		
		/*
		 * // TODO: Replace
		dataProxy.calculateElevationMinMax(true);
		log.info("Raster Data Maximum Value: " + dataProxy.getDataMaximumValue());
		log.info("Raster Data Minimum Value: " + dataProxy.getDataMinimumValue());
		*/
		dataProxy.fillBuffers();

		//dataProxy.setDataMaximumValue(8685.0);
		//dataProxy.setDataMinimumValue(-10977.0);
		
		//modelOptions.setProject3d(true);
		//lightingContext.setRayTraceShadows(true);
		//lightingContext.setLightingAzimuth(270.0);
		//lightingContext.setLightingElevation(3.0);
		modelOptions.setModelProjection(CanvasProjectionTypeEnum.PROJECT_FLAT);
		
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.standardResolutionRetrieval", false);
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.interpolate", true);
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.data.averageOverlappedData", true);
		
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.latitudeSlices", modelOptions.getHeight());//dataProxy.getDataRows());
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.longitudeSlices", modelOptions.getWidth());//dataProxy.getDataColumns());
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintLightSourceLines", false);
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintBaseGrid", false);
		
		modelOptions.setOption("us.wthr.jdem846.modelOptions.simpleRenderer.paintRasterPreview", true);
		//lightingContext.setRelativeLightIntensity(1.0);
		//lightingContext.setLightingMultiple(0.5);
		//modelOptions.setColoringType("hypsometric-etopo1-tint");
		
		//modelOptions.setHillShading(false);
		//modelOptions.setConcurrentRenderPoolSize(10);
		//modelOptions.getProjection().setRotateX(30);
		//modelOptions.getProjection().setRotateY(20);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, shapeContext, lightingContext, modelOptions);
		modelContext.updateContext();
		
		ModelCanvas canvas = modelContext.getModelCanvas(true);
		
		double startTime = System.currentTimeMillis();
		//Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		//OutputProduct<ModelCanvas> product = dem2d.generate(false, false);
		SimpleRenderer renderer = new SimpleRenderer(modelContext);
		renderer.prepare(true);
		renderer.render();
		double endTime = System.currentTimeMillis();
		
		dataProxy.clearBuffers();
		
		canvas.save(saveOutputTo);
		
		double renderSeconds = (endTime - startTime) / 1000;
		log.info("Completed render in " + renderSeconds + " seconds");
		
	}
	
}
