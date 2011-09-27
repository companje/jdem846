package us.wthr.jdem846.kml;

/* Note: This is sandbox code... It's gonna be /really/ fugly, make little to no sense, and
 * be outright incorrect or stupid. Sorry.
 * 
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.RegistryKernel;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.gridfloat.GridFloat;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ModelDimensions2D;

/**
 * http://code.google.com/apis/kml/documentation/kmlreference.html
 * @author Kevin M. Gill
 *
 */
public class KmlTesting
{
	private static Log log = Logging.getLog(KmlTesting.class);
	
	
	public static void main(String[] args)
	{
		KmlTesting testing = new KmlTesting();
		testing.doTesting();
		
	}
	
	public void doTesting()
	{
		List<String> inputDataList = new LinkedList<String>();
		inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		//inputDataList.add("C:\\srv\\elevation\\etopo1_ice_g_f4\\etopo1_ice_g_f4.flt");
		//String outputPath = "C:\\srv\\elevation\\etopo1_ice_g_f4\\kml";
		String outputPath = "C:/srv/kml/dist";
		String tempPath = "C:/srv/kml/temp";
		
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
		KmlDocument kml = new KmlDocument();
		kml.setName("jDem846 KML Testing");
		kml.setDescription("It's Testing!");
		kml.setHideChildren(false);
		
		ModelOptions modelOptions = new ModelOptions();
		//modelOptions.setColoringType("hypsometric-etopo1-tint");
		modelOptions.setPrecacheStrategy(DemConstants.PRECACHE_STRATEGY_TILED);
		modelOptions.setTileSize(1000);
		
		DataPackage dataPackage = new DataPackage();
		
		
		for (String inputDataPath : inputDataList) {
			GridFloat previewData = new GridFloat(inputDataPath);

			
			dataPackage.addDataSource(previewData);
		}
		
		dataPackage.prepare();
		
		GriddedModel model = null;
		
		try {
			dataPackage.calculateElevationMinMax(true);
			
			
			Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
			model = generate(dem2d, dataPackage, modelOptions, tempPath);
			
			
			for (Tile tile : model.getTiles()) {
				
				//double north = dataPackage.rowToLatitude(tile.getFromRow());
				//double south = dataPackage.rowToLatitude(tile.getToRow());
				//double west = dataPackage.columnToLongitude(tile.getFromColumn());
				//double east = dataPackage.columnToLongitude(tile.getToColumn());
				
				int fromCol = tile.getFromColumn();
				int toCol = tile.getToColumn();
				int fromRow = tile.getFromRow();
				int toRow = tile.getToRow();
				
				double west = dataPackage.columnToLongitude(fromCol - 1);
				double east = dataPackage.columnToLongitude(toCol);
				
				double north = dataPackage.rowToLatitude(fromRow - 1);
				double south = dataPackage.rowToLatitude(toRow);
				
				Polygon polygon = new Polygon();
				polygon.setExtrude(false);
				polygon.setAltitudeMode("clampToGround");
				
				polygon.getOuterBoundary().addCoordinate(new Coordinate(north, west));
				polygon.getOuterBoundary().addCoordinate(new Coordinate(south, west));
				polygon.getOuterBoundary().addCoordinate(new Coordinate(south, east));
				polygon.getOuterBoundary().addCoordinate(new Coordinate(north, east));
				
				polygon.getInnerBoundary().addCoordinate(new Coordinate(north-0.001, west+0.001));
				polygon.getInnerBoundary().addCoordinate(new Coordinate(south+0.001, west+0.001));
				polygon.getInnerBoundary().addCoordinate(new Coordinate(south+0.001, east-0.001));
				polygon.getInnerBoundary().addCoordinate(new Coordinate(north-0.001, east-0.001));
				
				Placemark placemark = new Placemark("" + north + "/" + west, polygon);
				kml.addElement(placemark);
			}
			
			//List<GroundOverlay> overlays = generate(dem2d, dataPackage, modelOptions, tempPath);
			
			//Folder kmlFolder = new Folder("Digital Elevation Model");
			
			//for (GroundOverlay overlay : overlays) {
		//	kml.addElement(overlay);
			//	kmlFolder.addElement(overlay);
			//}
			
			//kml.addElement(kmlFolder);
			
		} catch (DataSourceException ex) {
			
			ex.printStackTrace();
		} catch (RenderEngineException ex) {
			
			ex.printStackTrace();
		}
		
		log.info("Total Tiles Generated: " + model.getTiles().size());
		
		
		
		log.info("North: " + model.getNorth());
		log.info("South: " + model.getSouth());
		log.info("East: " + model.getEast());
		log.info("West: " + model.getWest());
		
		double latRes = model.getLatitudeResolution();
		double lonRes = model.getLongitudeResolution();
		
		double latRange = latRes * modelOptions.getTileSize();
		double lonRange = lonRes * modelOptions.getTileSize();
		
		
		double multiple = 1.0;
		while(true) {
			
			latRes = model.getLatitudeResolution() * multiple;
			lonRes = model.getLongitudeResolution() * multiple;
			
			
			
			//latRange = model.getLatitudeResolution() * multiple;
			//lonRange = model.getLongitudeResolution() * multiple;
			
			
			
			//latRes = latRange / multiple;
			//lonRes = lonRange / multiple;
			//float fromCol = dataPackage.latitudeToRow((float)latRes);
			
			
			log.info("Multiple: " + multiple + ", Range Lat/Lon: " + latRange + "/" + lonRange + ", Resolution Lat/Lon: " + latRes + "/" + lonRes);
			
			
			for (double north = model.getNorth(); north >= model.getSouth(); north -= latRes) {
				double south = north - latRes;
				
				
				
				
				for (double west = model.getWest(); west <= model.getEast(); west += lonRes) {
					double east = west + lonRes;
					
					double fromRow = dataPackage.latitudeToRow((float)north);
					double toRow = dataPackage.latitudeToRow((float)south);
					double rows = (north - south) / latRes;
					double fromCol = dataPackage.longitudeToColumn((float)west);
					double toCol = dataPackage.longitudeToColumn((float)east);
					
					
					
					log.info("Multiple: " + multiple + ", Latitude: " + north + "/" + south + ", Longitude: " + west + "/" + east);
				
					BufferedImage image = null;
					try {
						createImageTile(dataPackage, model, north, south, east, west, 256, multiple, outputPath);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					
					/*
					Polygon polygon = new Polygon();
					polygon.setExtrude(false);
					polygon.setAltitudeMode("clampToGround");
					
					polygon.getOuterBoundary().addCoordinate(new Coordinate(north, west));
					polygon.getOuterBoundary().addCoordinate(new Coordinate(south, west));
					polygon.getOuterBoundary().addCoordinate(new Coordinate(south, east));
					polygon.getOuterBoundary().addCoordinate(new Coordinate(north, east));
					
					polygon.getInnerBoundary().addCoordinate(new Coordinate(north-0.001, west+0.001));
					polygon.getInnerBoundary().addCoordinate(new Coordinate(south+0.001, west+0.001));
					polygon.getInnerBoundary().addCoordinate(new Coordinate(south+0.001, east-0.001));
					polygon.getInnerBoundary().addCoordinate(new Coordinate(north-0.001, east-0.001));
					
					Placemark placemark = new Placemark("" + north + "/" + west, polygon);
					kml.addElement(placemark);
					*/
					
					if (east > model.getEast())
						break;
				}
				
				if (south < model.getSouth())
					break;
			}
			
			
			
			////latRes *= 2.0;
			//lonRes *= 2.0;
			multiple = multiple * 2.0;
			
			if (dataPackage.getLongitudeWidth() / lonRes < 1.0 || dataPackage.getLatitudeHeight() / latRes < 1.0) {
				break;
			}
			//if (lonRes > dataPackage.getLongitudeWidth() && latRes > dataPackage.getLatitudeHeight() || multiple > 10000) {
			//	break;
			//}
		}
		
		
		
		
		//log.info("KML:\r\n" + kml.toKml());
		
		File kmlFile = new File(outputPath + "/doc.kml");
		try {
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(kmlFile));
			
			fos.write(kml.toKml().getBytes());
			
			fos.flush();
			fos.close();
		} catch (FileNotFoundException ex) {
			
			ex.printStackTrace();
		} catch (IOException ex) {
			
			ex.printStackTrace();
		}
		
		
	}
	
	public void createImageTile(DataPackage dataPackage, GriddedModel model, double north, double south, double east, double west, int scaleTo, double multiple, String outputPath) throws IOException
	{
		
		int fromRow = Math.round(dataPackage.latitudeToRow((float)north));
		int toRow = Math.round(dataPackage.latitudeToRow((float)south));
		int fromCol = Math.round(dataPackage.longitudeToColumn((float)west));
		int toCol = Math.round(dataPackage.longitudeToColumn((float)east));
		
		//(int fromRow, int fromColumn, int toRow, int toColumn)
		List<Tile> tilesIntersecting = model.getTilesIntersecting(fromRow, fromCol, toRow, toCol);
		
		log.info("Tile " + north + "/" + west + " - " + south + "/" + east + ", Tiles Intersecting: " + tilesIntersecting.size());
		
		BufferedImage image = new BufferedImage(scaleTo, scaleTo, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, scaleTo, scaleTo);
		
		for (Tile tile : tilesIntersecting) {
			BufferedImage subtile = tile.loadImage();
			
			double xScale = ((double)scaleTo / (double)subtile.getWidth());
			double yScale = ((double)scaleTo / (double)subtile.getHeight());
			
			double _x = tile.getFromColumn() - fromCol;
			double _y = tile.getFromRow() - fromRow;
			
			double _x2 = _x + (tile.getToColumn() - tile.getFromColumn());
			double _y2 = _y + (tile.getToRow() - tile.getFromRow());
			
			
			int x = (int) Math.round(_x * xScale);
			int y = (int) Math.round(_y * yScale);
			
			int x2 = (int) Math.round(_x2 * xScale);
			int y2 = (int) Math.round(_y2 * yScale);
			
			//int __ = 0;
			
			//double xScale = ((double)scaleTo / (double)subtile.getWidth());
			//double yScale = ((double)scaleTo / (double)subtile.getHeight());
			
			//double x = ((double)(tile.getFromColumn() - fromCol)) * xScale;
			//double y = ((double)(tile.getFromRow() - fromRow)) * yScale;
			
			
			//double width = (double)tile.getWidth() * xScale;
			//double height = (double)tile.getHeight() * yScale;
			
			//double x2 = ((double)(tile.getToColumn() - toCol)) * xScale;
			//double y2 = ((double)(tile.getToRow() - toRow)) * yScale;
			
			int width = Math.abs(x2 - x);
			int height = Math.abs(y2 - y);
			
			
			log.info("x/y: " + x + "/" + y + ", width/height: " + width + "/" + height);
			
			g2d.drawImage(subtile, (int)x, (int)y, (int)width, (int)height, new ImageObserver() {
				public boolean imageUpdate(Image img, int infoflags, int x,
						int y, int width, int height)
				{
					return true;
				}
			});
			
			
		}
		
		
		//g2d.dispose();
		
		String fileName = "tile-" + fromRow + "-" + toRow + "-" + fromCol + "-" + toCol + "-" + ((int)multiple) + ".png";
		
		String path = outputPath + "/" + fileName;
		log.info("Writing image to " + path);
		
		ImageIO.write(image, "png", new File(path));
		
		//return image;
	}
	
	public GriddedModel generate(Dem2dGenerator dem2d, DataPackage dataPackage, ModelOptions modelOptions, String tempPath) throws RenderEngineException, DataSourceException
	{
		ModelDimensions2D modelDimensions = ModelDimensions2D.getModelDimensions(dataPackage, modelOptions);
		//dataPackage.setAvgXDim(modelDimensions.getxDim());
		//dataPackage.setAvgYDim(modelDimensions.getyDim());
		
		int tileRow = 0;
		int tileCol = 0;
		int tileNum = 0;
		int dataRows = modelDimensions.getDataRows();
		int dataCols = modelDimensions.getDataColumns();
		int tileSize = modelDimensions.getTileSize();
		
		DemCanvas tileCanvas = new DemCanvas(new Color(0x0, 0x0, 0x0, 0x0), (int)modelDimensions.getTileSize(), (int)modelDimensions.getTileSize());
		
		//List<GroundOverlay> overlays = new LinkedList<GroundOverlay>();
		//List<Tile> tiles = new LinkedList<Tile>();
		
		
		
		double latResolution = tileSize * modelDimensions.getyDim();
		double lonResolution = tileSize * modelDimensions.getxDim();
		
		
		GriddedModel model = new GriddedModel(latResolution, lonResolution);
		model.setNorth(dataPackage.getMaxLatitude());
		model.setSouth(dataPackage.getMinLatitude());
		model.setWest(dataPackage.getMinLongitude());
		model.setEast(dataPackage.getMaxLongitude());
		
		
		
		if (dataPackage.getDataSources().size() > 0) {
			for (int fromRow = 0; fromRow < dataRows; fromRow+=tileSize) {
				int toRow = fromRow + tileSize - 1;
				if (toRow > dataRows)
					toRow = dataRows;
			
				tileCol = 0;
				for (int fromCol = 0; fromCol < dataCols; fromCol+=tileSize) {
					int toCol = fromCol + tileSize - 1;
					if (toCol > dataCols)
						toCol = dataCols;
					
					dem2d.loadDataSubset((int) fromCol, (int) fromRow, (int) tileSize, (int) tileSize);
					dem2d.precacheData();
					
					tileCanvas.reset();
					
					dem2d.generate(fromRow, toRow, fromCol, toCol, tileCanvas);
					
					//saveTileImage(DemCanvas canvas, int fromRow, int fromCol, int toRow, int toCol, String outputPath)
					Tile tile = saveTileImage(tileCanvas, fromRow, fromCol, toRow, toCol, tempPath);
					model.addTile(tile);
					//tiles.add(tile);
					//GroundOverlay groundOverlay = createGroundOverlay(dataPackage, tileImagePath, fromRow, fromCol, toRow, toCol, tileRow, tileCol);
					//overlays.add(groundOverlay);
					
					dem2d.unloadData();
					
					tileCol++;
				}
				
				tileRow++;
			}
		}
		
		return model;
		//return tiles;
		//return overlays;
	}
	
	public GroundOverlay createGroundOverlay(DataPackage dataPackage, String tileImagePath, int fromRow, int fromCol, int toRow, int toCol, int tileRow, int tileCol)
	{
		//LatLonBox(double west, double south, double cellsize, int rows, int columns)
		//Icon(String href)
		//GroundOverlay(String name, Icon icon, LatLonBox latLonBox)
		
		String overlayName = "Overlay-" + tileRow + "-" + tileCol;
		
		//double cols = toCol - fromCol;
		//double rows = toRow - fromRow;
		
		double west = dataPackage.columnToLongitude(fromCol - 1);
		double east = dataPackage.columnToLongitude(toCol);
		
		double north = dataPackage.rowToLatitude(fromRow - 1);
		double south = dataPackage.rowToLatitude(toRow);
		
		//double west = dataPackage.getMinLongitude() + (fromCol * dataPackage.getAverageResolution());
		//double south = dataPackage.getMaxLatitude() - (toRow * dataPackage.getAverageResolution());
		//
		
		//double west = (dataPackage.getMinLongitude() + (((double)tileCol * (double)tileSize + tileSize) * dataPackage.getAvgXDim()));
		//double south = (dataPackage.getMinLatitude() + (((double)tileRow * (double)tileSize + tileSize) * dataPackage.getAvgYDim()));
		
		LatLonBox latLonBox = new LatLonBox(east, west, north, south);
		Icon icon = new Icon(tileImagePath);
		
		GroundOverlay groundOverlay = new GroundOverlay(overlayName, icon, latLonBox);
		return groundOverlay;
		
	}
	
	
	//saveTileImage(tileCanvas, fromRow, toRow, fromCol, toCol, tempPath)
	public Tile saveTileImage(DemCanvas canvas, int fromRow, int fromCol, int toRow, int toCol, String outputPath)
	{
		//String fileName = "tile-" + tileRow + "-" + tileCol + ".png";
		String fileName = "tile-" + fromRow + "-" + toRow + "-" + fromCol + "-" + toCol + ".png";
		
		String path = outputPath + "/" + fileName;
		log.info("Writing image to " + path);
		
		//DemCanvas scaled = canvas.getScaled(scaleSize, scaleSize);
		
		
		canvas.save(path);
		
		Tile tile = new Tile(new File(path), fromRow, fromCol, toRow, toCol);
		
		return tile;
	}
	
	


}
