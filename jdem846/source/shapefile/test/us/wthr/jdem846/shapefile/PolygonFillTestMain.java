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
	
	
	public void testIntersectsPerformance(ShapeDataContext shapeContext, final ModelCanvas modelCanvas) throws RenderEngineException
	{
		//if (isCancelled()) {
		//	log.warn("Render process cancelled, model not complete.");
		//	return;
		//}
		
		//if (this.modelCanvas == null) {
		//	this.modelCanvas = modelContext.getModelCanvas();
		//}
		
		int numLayers = shapeContext.getShapeDataListSize();
		int layerNumber = 0;
		
		for (ShapeFileRequest shapeFilePath : shapeContext.getShapeFiles()) {
			layerNumber++;
			try {
				log.info("Loading shapefile from " + shapeFilePath.getPath());
				
				ShapeBase shapeBase = shapeFilePath.open();
				ShapeLayer shapeLayer = buildShapeLayer(shapeBase, modelCanvas);
				shapeBase.close();
				
				
				testLayer(shapeLayer, modelCanvas);

				
				
				//fireTileCompletionListeners(modelCanvas, ((double)layerNumber) / ((double)numLayers));
				
			} catch (OutOfMemoryError err) {
				throw err;
			} catch(Exception ex) {
				throw new RenderEngineException("Error occured rendering shape files", ex);
			}

		}
		
		log.info("Completed shapefile rendering");
		
	}
	
	public ShapeLayer buildShapeLayer(ShapeBase shapeBase, final ModelCanvas modelCanvas) throws RenderEngineException, ShapeFileException
	{
		ShapeLayer shapeLayer = new ShapeLayer(shapeBase.getShapeType());
		
		log.info("Loading " + shapeBase.getShapeCount() + " shapes");
		for (int i = 0; i < shapeBase.getShapeCount(); i++) {
			Shape shape = shapeBase.getShape(i);
			shapeLayer.addShape(shape);
		}
		
		log.info("Translating coordinates...");
		final MapPoint mapPoint = new MapPoint();
		shapeLayer.translate(new PointTranslateHandler() {
			public boolean translatePoint(double[] coords)
			{
				try {
					double elevation = getElevationAtPoint(coords[1], coords[0]);
					modelCanvas.getMapProjection().getPoint(coords[1], coords[0], elevation, mapPoint);
					coords[0] = mapPoint.column;
					coords[1] = mapPoint.row;
					
					if (coords[0] < 0 && coords[1] < 0) {
						return true;
					} else if (coords[0] >= modelCanvas.getWidth() && coords[1] >= modelCanvas.getHeight()) {
						return true;
					} else {
						return true;
					}
					
				} catch (MapProjectionException ex) {
					log.warn("Error projecting coordinates to map: " + ex.getMessage(), ex);
					return false;
				} catch (DataSourceException ex) {
					log.warn("Error retrieving elevation for point: " + ex.getMessage(), ex);
					return false;
				}
			}
		}, false);
		
		log.info("Combining shapes...");
		
		//shapeLayer = shapeLayer.getCombinedPathsByTypes();
		return shapeLayer;
	}
	
	public void testLayer(ShapeLayer shapeLayer, ModelCanvas modelCanvas) throws RenderEngineException
	{
		int shapeType = shapeLayer.getType();
		log.info("Shape Type: " + shapeType);

		for (ShapePath path : shapeLayer.getShapePaths()) {		
			convertPath(path, modelCanvas);
			//testPath(shapeType, path, modelCanvas);

		}
	}
	
	
	public Edge[] getEdges(ShapePath path)
	{
		List<Edge> edgeList = new ArrayList<Edge>();
		double[] coords = new double[2];
		PathIterator iter = path.getPathIterator(null);
		
		int lastX = 0;
		int lastY = 0;
		
		int i = 0;
		while(!iter.isDone()) {
			iter.currentSegment(coords);
			
			int x = (int)coords[0];
			int y = (int)coords[1];
			
			if (i == 0) {
				
			} else {
				
				if (lastY < y) {
					edgeList.add(new Edge(new Vertex(lastX, lastY), new Vertex(x, y)));
				} else {
					edgeList.add(new Edge(new Vertex(x, y), new Vertex(lastX, lastY)));
				}
			}
			
			lastX = x;
			lastY = y;

			iter.next();

            i++;
		} 
        
		Edge[] edgeArray = new Edge[edgeList.size()];
		edgeList.toArray(edgeArray);
		
		return edgeArray;
		
	}
	
	public void convertPath(ShapePath path, ModelCanvas modelCanvas)
	{
		//modelCanvas.fillScanLine(scanline)
		int[] rgba = {0, 0, 255, 255};
		

		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;

		
		Edge<Integer>[] edgeArray = getEdges(path);

		
		
		Arrays.sort(edgeArray, new Comparator<Edge<Integer>>() {
			public int compare(Edge<Integer> e0, Edge<Integer> e1)
			{
				return e0.compareTo(e1);
				/*
				if (e1.p0.y > e0.p0.y)
					return -1;
				else if (e1.p0.y == e0.p0.y) 
					return 0;
				else
					return 1;
				*/
			}
		});
		
		

        for (int i = 0; i < edgeArray.length; i++)
        {
            if (maxY < edgeArray[i].p1.y)
            	maxY = edgeArray[i].p1.y;
        }
        minY = edgeArray[0].p0.y;
		
		
		if (minY >= modelCanvas.getHeight() || maxY < 0 || minY == maxY)
			return;
		
		if (minY < 0)
			minY = 0;
		if (maxY >= modelCanvas.getHeight())
			maxY = modelCanvas.getHeight() - 1;
		
		
		List<Integer> list = new ArrayList<Integer>();
		for (int y = minY; y <= maxY; y++) {
			list.clear();
			
			for (int i = 0; i < edgeArray.length; i++) {
				
				
				if (y == edgeArray[i].p0.y) 
                {
                    if (y == edgeArray[i].p1.y)
                    {
                        // the current edge is horizontal, so we add both vertices
                    	edgeArray[i].deactivate();
                        list.add((int)edgeArray[i].curX);
                    } else {
                    	edgeArray[i].activate();
                        // we don't insert it in the list cause this vertice is also
                        // the (bigger) vertice of another edge and already handled
                    }
                }
                
                // here the scanline intersects the bigger vertice
                if (y == edgeArray[i].p1.y) {
                	edgeArray[i].deactivate();
                    list.add((int)edgeArray[i].curX);
                }
                
                // here the scanline intersects the edge, so calc intersection point
                if (y > edgeArray[i].p0.y && y < edgeArray[i].p1.y) {
                	edgeArray[i].update();
                    list.add((int)edgeArray[i].curX);
                }
			}
			
			if (list.size() < 2 || list.size() % 2 != 0) 
            {
               log.warn("This should never happen! (list size: " + list.size() + ", Edge Count: " + edgeArray.length + ")");
                continue;
            } 
			
			int swaptmp;
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.size() - 1; j++) {
                    if (list.get(j) > list.get(j+1)) {
                        swaptmp = list.get(j);
                        list.set(j, list.get(j+1));
                        list.set(j+1, swaptmp);
                    }
                
                }
            }
            
            
             
            // so draw all line segments on current scanline
            for (int i = 0; i < list.size(); i+=2)
            {
            	if (i+1 < list.size()) {
            		int leftX = list.get(i);
            		int rightX = list.get(i+1);
            		//log.info("Left/Right X: " + leftX + "/" + rightX);
            		
            		modelCanvas.fillScanLine(leftX, rightX, y, 0.0, rgba);
            	}
               // g.drawLine(list.get(i), scanline, list.get(i+1), scanline);
            }
		
		}
		
		//log.info("Converted path with " + edgeArray.length + " elements");
	}
	
	
	protected void testPath(int shapeType, ShapePath path, ModelCanvas modelCanvas) throws RenderEngineException
	{

		FeatureTypeStroke featureStroke = null;
			
		if (path.getFeatureType() != null) {
			featureStroke = path.getFeatureType().getFeatureTypeStroke();
		}
			
		if (featureStroke == null) {
			featureStroke = FeatureTypeStroke.getDefaultFeatureTypeStroke();
		}
			
		List<LineStroke> lineStrokes = featureStroke.getLineStrokes();
		
		int[] color = new int[4];
		
		for (LineStroke lineStroke : lineStrokes) {
			
			Rectangle2D bounds = path.getBounds2D();
			
			//int minX = (int) Math.round(bounds.getMinX());
			//int maxX = (int) Math.round(bounds.getMaxX());
			//int minY = (int) Math.round(bounds.getMinY());
			//int maxY = (int) Math.round(bounds.getMaxY());
			
			double minX = Math.round(bounds.getMinX());
			double maxX = Math.round(bounds.getMaxX());
			double minY = Math.round(bounds.getMinY());
			double maxY = Math.round(bounds.getMaxY());
			
			if (maxX < 0 || minX >= modelCanvas.getWidth() || maxY < 0 || minY >= modelCanvas.getHeight())
				return;
			
			if (minX < 0)
				minX = 0;
			if (maxX >= modelCanvas.getWidth())
				maxX = modelCanvas.getWidth() - 1;
			if (minY < 0)
				minY = 0;
			if (minY >= modelCanvas.getHeight())
				minY = modelCanvas.getHeight() - 1;
			
			if (maxX <= minX)
				maxX = minX + 1;
			if (maxY <= minY)
				maxY = minY + 1;
			
			
			boolean inside = false;
			double leftX = 0;
			log.info("Testing polygon min x/y: " + minX + "/" + minY + ", max x/y: " + maxX + "/" + maxY);
			for (double y = minY; y <= maxY; y++) {
				

				for (double x = minX; x <= maxX; x++) {

					
					//if (shape.contains(x, y) && !inside) {
					if (path.intersects(x, y, 1, 1) && !inside) {
						leftX = x;
						inside = true;
					} else if (inside) {
						
						inside = false;
					}
				}
			}
			log.info("Completed polygon");
			

		}
		
	}
	
	
	protected double getElevationAtPoint(double latitude, double longitude) throws DataSourceException
	{
		return 0.0;
	}
}
