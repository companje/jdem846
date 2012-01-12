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
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.ModelCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class NewTileMethodTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		log = Logging.getLog(NewTileMethodTestMain.class);
		
		NewTileMethodTestMain main = new NewTileMethodTestMain();
		
		try {
			main.doTesting();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doTesting() throws Exception 
	{
		List<String> inputDataList = new LinkedList<String>();
		
		inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1-3as.flt");
		inputDataList.add("C:/srv/elevation/DataRaster-Testing/PresRange_1as.flt");
		String saveOutputTo = "C:/srv/elevation/DataRaster-Testing/output/frame-{iter}.png";
		
		
		//inputDataList.add("F:/Presidential Range/02167570.flt");
		//String saveOutputTo = "F:/Presidential Range/model-output.png";
		
		RasterDataContext dataProxy = new RasterDataContext();
		
		for (String inputDataPath : inputDataList) {
			log.info("Adding raster data @ '" + inputDataPath + "'");
			RasterData rasterData = RasterDataProviderFactory.loadRasterData(inputDataPath);
			dataProxy.addRasterData(rasterData);
			
		}
		
		dataProxy.calculateElevationMinMax(true);
		log.info("Raster Data Maximum Value: " + dataProxy.getDataMaximumValue());
		log.info("Raster Data Minimum Value: " + dataProxy.getDataMinimumValue());
		
		LightingContext lightingContext = new LightingContext();
		lightingContext.setLightingEnabled(true);
		ModelOptions modelOptions = new ModelOptions();
		//modelOptions.setUserScript(script);
		//modelOptions.setScriptLanguage(ScriptLanguageEnum.GROOVY);
		modelOptions.setTileSize(2000);
		modelOptions.setWidth(dataProxy.getDataColumns());
		modelOptions.setHeight(dataProxy.getDataRows());
		modelOptions.setDoublePrecisionHillshading(false);
		modelOptions.setUseSimpleCanvasFill(false);
		modelOptions.setAntialiased(false);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR3D);
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		modelOptions.setBackgroundColor("255;255;255;0");
		//modelOptions.setHillShading(false);
		//modelOptions.setConcurrentRenderPoolSize(10);
		//modelOptions.getProjection().setRotateX(0);
		double rotateY = 45;
		double rotateX = 15;
		
		//for(double rotateY = 0; rotateY < 360; rotateY++) {
			modelOptions.getProjection().setRotateY(rotateY);
			modelOptions.getProjection().setRotateX(rotateX);
			ModelContext modelContext = ModelContext.createInstance(dataProxy, lightingContext, modelOptions);
			
			
			
			double startTime = System.currentTimeMillis();
			Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
			OutputProduct<ModelCanvas> product = dem2d.generate();
			double endTime = System.currentTimeMillis();
			double renderSeconds = (endTime - startTime) / 1000;
			log.info("Completed render in " + renderSeconds + " seconds");
			
			
			ModelCanvas canvas = product.getProduct();
			canvas.save(saveOutputTo.replace("{iter}", ""+(100000+((int)rotateY))));
		//}
		
	}
	
}
