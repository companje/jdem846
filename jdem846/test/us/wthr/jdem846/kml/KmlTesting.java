package us.wthr.jdem846.kml;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
		//inputDataList.add("C:/srv/elevation/Maui/15749574.flt");
		//inputDataList.add("C:/srv/elevation/Maui/58273983.flt");
		inputDataList.add("C:\\srv\\elevation\\etopo1_ice_g_f4\\etopo1_ice_g_f4.flt");
		String outputPath = "C:\\srv\\elevation\\etopo1_ice_g_f4\\kml";
		//String outputPath = "C:/srv/kml";
		
		
		try {
			RegistryKernel regKernel = new RegistryKernel();
			regKernel.init();
		} catch (Exception ex) {
			log.error("Failed to initialize registry configuration: " + ex.getMessage(), ex);
			return;
		}
		
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
		
		KmlDocument kml = new KmlDocument();
		try {
			dataPackage.calculateElevationMinMax(true);
			
			
			Dem2dGenerator dem2d = new Dem2dGenerator(dataPackage, modelOptions);
			List<GroundOverlay> overlays = generate(dem2d, dataPackage, modelOptions, outputPath);
			
			Folder kmlFolder = new Folder("Digital Elevation Model");
			
			for (GroundOverlay overlay : overlays) {
				kmlFolder.addElement(overlay);
			}
			
			kml.addElement(kmlFolder);
			
		} catch (DataSourceException ex) {
			
			ex.printStackTrace();
		} catch (RenderEngineException ex) {
			
			ex.printStackTrace();
		}
		
		log.info("KML:\r\n" + kml.toKml());
		
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
	
	public List<GroundOverlay> generate(Dem2dGenerator dem2d, DataPackage dataPackage, ModelOptions modelOptions, String outputPath) throws RenderEngineException, DataSourceException
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
		
		List<GroundOverlay> overlays = new LinkedList<GroundOverlay>();
		
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
					
					String tileImagePath = saveTileImage(tileCanvas, tileRow, tileCol, outputPath);
					
					GroundOverlay groundOverlay = createGroundOverlay(dataPackage, tileImagePath, fromRow, fromCol, toRow, toCol, tileRow, tileCol);
					overlays.add(groundOverlay);
					
					dem2d.unloadData();
					
					tileCol++;
				}
				
				tileRow++;
			}
		}
		
		
		
		return overlays;
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
	
	
	public String saveTileImage(DemCanvas canvas, int tileRow, int tileCol, String outputPath)
	{
		String fileName = "tile-" + tileRow + "-" + tileCol + ".png";
		String path = outputPath + "/" + fileName;
		log.info("Writing image to " + path);
		
		canvas.save(path);
		
		return fileName;
	}
	
}
