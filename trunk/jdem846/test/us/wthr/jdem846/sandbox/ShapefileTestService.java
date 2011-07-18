/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.sandbox;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.AbstractLockableService;
import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.input.DataSourceFactory;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.shapefile.modeling.FeatureTypeStroke;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeBase;
import us.wthr.jdem846.shapefile.ShapeConstants;
import us.wthr.jdem846.shapefile.ShapeFile;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.ShapeIndexFile;
import us.wthr.jdem846.shapefile.ShapeIndexRecord;
import us.wthr.jdem846.shapefile.ShapeLayer;
import us.wthr.jdem846.shapefile.ShapePath;
import us.wthr.jdem846.shapefile.PointTranslateHandler;
import us.wthr.jdem846.shapefile.modeling.LineStroke;

@Service(name="us.wthr.jdem846.sandbox.shapefiletest", enabled=false)
public class ShapefileTestService extends AbstractLockableService
{
	private static Log log = Logging.getLog(ShapefileTestService.class);
	
	private List<String> elevationPaths = new LinkedList<String>();
	private List<ShapeFileRequest> shapeFilePaths = new LinkedList<ShapeFileRequest>();
	private String saveToPath;
	
	
	public ShapefileTestService()
	{
		
	}
	
	@Initialize
	public void init()
	{
		
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Nashua NH Streams\\65865040.shp", Color.BLUE));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Nashua NH Waterbodies\\94902250.shp", Color.BLUE));
		//shapeFilePaths.add("C:\\srv\\elevation\\Shapefile Testing\\roads.shp");
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\DEM\\Shapefiles\\tl_2010_33011_areawater\\tl_2010_33011_areawater.shp", Color.BLUE));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\DEM\\Shapefiles\\tl_2010_33011_linearwater\\tl_2010_33011_linearwater.shp", Color.BLUE));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\DEM\\Shapefiles\\tl_2010_33011_roads\\tl_2010_33011_roads.shp", Color.BLACK));
		//
		
		//saveToPath = "C:\\srv\\elevation\\Shapefiles\\Boston MA\\";
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Boston MA\\hydrography\\NHDWaterbody.shp", Color.BLUE));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Boston MA\\hydrography\\NHDArea.shp", Color.BLUE));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Boston MA\\hydrography\\NHDFlowline.shp", Color.BLUE));
		
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Nashua NH Roads\\94115067.shp", Color.BLACK));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Boston MA\\transportation\\Trans_RailFeature.shp", Color.DARK_GRAY));
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Boston MA\\transportation\\Trans_AirportRunway.shp", Color.DARK_GRAY));
		
		//shapeFilePaths.add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Boston MA\\transportation\\Trans_RoadSegment.shp", Color.BLACK));
		
		
		//elevationPaths.add("C:\\srv\\elevation\\Shapefiles\\Boston MA\\35932159\\35932159.flt");
		
		//elevationPaths.add("C:\\srv\\elevation\\Pawtuckaway\\74339812.flt");
		//elevationPaths.add("C:\\srv\\elevation\\Shapefile Testing\\ned_64087130.flt");
		//elevationPaths.add("C:\\srv\\DEM\\Nashua NH 1-3 Arc Second\\78522096.flt");
	
		saveToPath = "C:/srv/elevation/Shapefiles/Nashua NH/";
		
		try {
			shapeFilePaths.add(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/hydrography/NHDArea.shp", "usgs-hydrography"));
			shapeFilePaths.add(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/hydrography/NHDFlowline.shp", "usgs-hydrography"));
			shapeFilePaths.add(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/hydrography/NHDWaterbody.shp", "usgs-hydrography"));
			
			shapeFilePaths.add(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_RailFeature.shp", "usgs-transportation-rail"));
			shapeFilePaths.add(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_AirportRunway.shp", "usgs-transportation-runways"));
			shapeFilePaths.add(new ShapeFileRequest("C:/srv/elevation/Shapefiles/Nashua NH/transportation/Trans_RoadSegment.shp", "usgs-transportation-roads"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		elevationPaths.add("C:/srv/elevation/Shapefiles/Nashua NH/Elevation 1-3 Arc Second/21440075.flt");
	}
	
	@ServiceRuntime
	public void runtime()
	{
		this.setLocked(true);
		

		final DataPackage dataPackage = new DataPackage(null);
		for (String inputPath : elevationPaths) {
			log.info("Adding elevation data: " + inputPath);
			DataSource input;
			try {
				input = DataSourceFactory.loadDataSource(inputPath);
			} catch (InvalidFileFormatException e) {
				e.printStackTrace();
				continue;
			}
			
			input.calculateDataStats();
			dataPackage.addDataSource(input);
		}
		
		for (ShapeFileRequest shapeFilePath : shapeFilePaths) {
			dataPackage.addShapeFile(shapeFilePath);
		}
		log.info("Preparing dataPackage");
		dataPackage.prepare();
		
		ModelOptions modelOptions = new ModelOptions();
		//modelOptions.setWidth((int)dataPackage.getColumns());
		//modelOptions.setHeight((int)dataPackage.getRows());
		//modelOptions.setColoringType("hypsometric-tint");
		modelOptions.setColoringType("green-tint");
		//modelOptions.setHillShadeType(DemConstants.HILLSHADING_DARKEN);
		modelOptions.setBackgroundColor("Blue");
		Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
		
		log.info("Calculating elevation min/max");
		dataPackage.calculateElevationMinMax(true);
		
		log.info("Generating DEM2D image");
		OutputProduct<DemCanvas> output = dem2d.generate();
		
		DemCanvas canvas = output.getProduct();
		canvas.save(saveToPath + "dem2d.png");
		
		for (ShapeFileRequest shapeFilePath : dataPackage.getShapeFiles()) {
			try {
				log.info("Loading shapefile from " + shapeFilePath.getPath());
				ShapeBase shapeBase = new ShapeBase(shapeFilePath.getPath(), shapeFilePath.getShapeDataDefinitionId());
				ShapeLayer shapeLayer = new ShapeLayer(shapeBase.getShapeType());
				
				log.info("Loading " + shapeBase.getShapeCount() + " shapes");
				for (int i = 0; i < shapeBase.getShapeCount(); i++) {
					Shape shape = shapeBase.getShape(i);
					shapeLayer.addShape(shape);
				}
				
				shapeLayer.translate(new PointTranslateHandler() {
					public void translatePoint(double[] coords)
					{
						double x = dataPackage.longitudeToColumn((float) coords[0]);
						double y = dataPackage.latitudeToRow((float) coords[1]);
						coords[0] = x;
						coords[1] = y;
					}
				}, false);
				
				shapeLayer = shapeLayer.getCombinedPathsByTypes();
				
				Image layerImage = renderLayer(dataPackage, shapeLayer);
				layerImage = layerImage.getScaledInstance(canvas.getWidth(), canvas.getHeight(), Image.SCALE_SMOOTH);
				canvas.overlay(layerImage, 0, 0);
	
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
		log.info("Saving image");
		canvas.save(saveToPath + "dem2d-filled-polygons.png");
		log.info("Complete");
		
		this.setLocked(false);
	}
	
	public Image renderLayer(DataPackage dataPackage, ShapeLayer shapeLayer)
	{
		//Image image = canvas.getImage();
		BufferedImage image = new BufferedImage((int)dataPackage.getColumns(), (int)dataPackage.getRows(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int shapeType = shapeLayer.getType();
		log.info("Shape Type: " + shapeType);
		fillShapes(shapeLayer, g2d);
		//DemCanvas filledCanvas = new DemCanvas(image);
		
		g2d.dispose();
		return image;

	}
	

	public void fillShapes(ShapeLayer shapeLayer, Graphics2D g2d)
	{
		log.info("Creating shape paths");

		log.info("Drawing " + shapeLayer.size() + " polylines");
		int shapeType = shapeLayer.getType();

		for (ShapePath path : shapeLayer.getShapePaths()) {		
			
			FeatureTypeStroke featureStroke = null;//path.getFeatureType().featureStroke();
			
			if (path.getFeatureType() != null) {
				featureStroke = path.getFeatureType().getFeatureTypeStroke();
			}
			
			if (featureStroke == null) {
				featureStroke = FeatureTypeStroke.getDefaultFeatureTypeStroke();
			}
			
			List<LineStroke> lineStrokes = featureStroke.getLineStrokes();

			for (LineStroke lineStroke : lineStrokes) {
			
				g2d.setStroke(lineStroke);
				g2d.setColor(lineStroke.getColor());
				
				if (shapeType == ShapeConstants.TYPE_POLYGON ||
						shapeType == ShapeConstants.TYPE_POLYGONM ||
						shapeType == ShapeConstants.TYPE_POLYGONZ) {
					
					g2d.fill(path);
					
				} else if (shapeType == ShapeConstants.TYPE_POLYLINE ||
							shapeType == ShapeConstants.TYPE_POLYLINEM ||
							shapeType == ShapeConstants.TYPE_POLYLINEZ) {
					
					g2d.draw(path);

				}					
			}	
		}

	}

	@Destroy
	public void destroy()
	{
		
	}

}
