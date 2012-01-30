package us.wthr.jdem846.shapefile;

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.exception.CanvasException;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.geom.Edge;
import us.wthr.jdem846.geom.Vertex;
import us.wthr.jdem846.gis.exceptions.MapProjectionException;
import us.wthr.jdem846.gis.projections.MapPoint;
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
import us.wthr.jdem846.render.shapelayer.ShapeFill;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapedata.ShapeDataContext;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;
import us.wthr.jdem846.shapefile.modeling.FeatureTypeStroke;
import us.wthr.jdem846.shapefile.modeling.LineStroke;

public class PolygonFillTestMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(PolygonFillTestMain.class);
		
		try {
			PolygonFillTestMain testMain = new PolygonFillTestMain();
			testMain.doTesting();
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	public void doTesting() throws Exception
	{
		List<String> inputDataList = new LinkedList<String>();
		List<ShapeFileRequest> inputShapeList = new LinkedList<ShapeFileRequest>();
		
		inputDataList.add("C:/srv/elevation/Nashua NH/Elevation 1-3 Arc Second/Elevation 1-3 Arc Second.flt");
		inputShapeList.add(new ShapeFileRequest("C:/srv/elevation/Nashua NH/hydrography/NHDArea.shp", "usgs-hydrography"));
		//inputShapeList.add(new ShapeFileRequest("C:/srv/elevation/Nashua NH/hydrography/NHDFlowline.shp", "usgs-hydrography"));
		inputShapeList.add(new ShapeFileRequest("C:/srv/elevation/Nashua NH/hydrography/NHDWaterbody.shp", "usgs-hydrography"));
		
		
		String saveOutputTo = "C:/srv/elevation/Nashua NH/model-output.png";
		
		
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
		
		dataProxy.calculateElevationMinMax(true);
		log.info("Raster Data Maximum Value: " + dataProxy.getDataMaximumValue());
		log.info("Raster Data Minimum Value: " + dataProxy.getDataMinimumValue());
		
		LightingContext lightingContext = new LightingContext();
		lightingContext.setLightingEnabled(true);
		ModelOptions modelOptions = new ModelOptions();
		//modelOptions.setUserScript(script);
		modelOptions.setScriptLanguage(ScriptLanguageEnum.GROOVY);
		modelOptions.setTileSize(1000);
		modelOptions.setWidth(700);//dataProxy.getDataColumns());
		modelOptions.setHeight(700);//dataProxy.getDataRows());
		modelOptions.setDoublePrecisionHillshading(false);
		modelOptions.setUseSimpleCanvasFill(false);
		modelOptions.setAntialiased(true);
		modelOptions.setMapProjection(MapProjectionEnum.EQUIRECTANGULAR);
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		modelOptions.setBackgroundColor("255;255;255;0");
		//modelOptions.setHillShading(false);
		//modelOptions.setConcurrentRenderPoolSize(10);
		//modelOptions.getProjection().setRotateX(30);
		
		ModelContext modelContext = ModelContext.createInstance(dataProxy, shapeContext, lightingContext, modelOptions);
		ModelCanvas modelCanvas = modelContext.getModelCanvas();
		
		
		
		double startTime = System.currentTimeMillis();
		Dem2dGenerator dem2d = new Dem2dGenerator(modelContext);
		OutputProduct<ModelCanvas> product = dem2d.generate(true, false);
		//testIntersectsPerformance(shapeContext, modelCanvas);
		double endTime = System.currentTimeMillis();
		//ModelCanvas modelCanvas = product.getProduct();
		modelCanvas.save(saveOutputTo);
		
		double renderSeconds = (endTime - startTime) / 1000;
		log.info("Completed render in " + renderSeconds + " seconds");
	}
	
	
}
