package us.wthr.jdem846;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import us.wthr.jdem846.gis.elevation.ElevationMinMax;
import us.wthr.jdem846.gis.planets.Planet;
import us.wthr.jdem846.gis.planets.PlanetsRegistry;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ModelProcessManifest;
import us.wthr.jdem846.rasterdata.ElevationMinMaxCalculator;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataContext;
import us.wthr.jdem846.rasterdata.RasterDataProviderFactory;

public class ElevationModelJsonGeneratorMain extends AbstractTestMain
{
	private static Log log = null;
	
	
	public ElevationModelJsonGeneratorMain()
	{
		
	}
	
	
	public static void main(String[] args)
	{
		
		
		try {
			AbstractTestMain.initialize(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		ElevationModelJsonGeneratorMain main = new ElevationModelJsonGeneratorMain();
		try {
			main.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void run() throws Exception
	{
		String dataPath = "C:\\jdem\\Data\\Maui 1-3 Arc Second\\06132865.flt";
		String writePath = "C:\\Users\\kgill\\Google Drive\\htdocs\\maui\\model.js";
		
		
		RasterDataContext rasterDataContext = new RasterDataContext();
		
		ModelContext modelContext = ModelContext.createInstance(rasterDataContext, new ModelProcessManifest());
		
		RasterData rasterData = RasterDataProviderFactory.loadRasterData(dataPath, null);
		rasterDataContext.addRasterData(rasterData);
		
		double latitudeResolution = rasterData.getLatitudeResolution() / 0.05;
		double longitudeResolution = rasterData.getLongitudeResolution() / 0.05;
		
		rasterDataContext.setEffectiveLatitudeResolution(latitudeResolution);
		rasterDataContext.setEffectiveLongitudeResolution(longitudeResolution);
		
		rasterDataContext.prepare();
		
		modelContext.updateContext();
		
		ElevationMinMaxCalculator minMaxCalc = new ElevationMinMaxCalculator(modelContext, null);
		ElevationMinMax minMax = minMaxCalc.calculateMinAndMax();

		rasterDataContext.setDataMaximumValue(minMax.getMaximumElevation());
		rasterDataContext.setDataMinimumValue(minMax.getMinimumElevation());
		
		int effectiveRows = (int) MathExt.floor((rasterDataContext.getNorth() - rasterDataContext.getSouth()) / latitudeResolution);
		int effectiveColumns = (int) MathExt.floor((rasterDataContext.getEast() - rasterDataContext.getWest()) / longitudeResolution);
		
		double effectiveSouth = rasterDataContext.getNorth() - effectiveRows * latitudeResolution;
		double effectiveEast = rasterDataContext.getWest() + effectiveColumns * longitudeResolution;
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("north", rasterDataContext.getNorth());
		jsonObject.element("south", effectiveSouth);
		jsonObject.element("east", effectiveEast);
		jsonObject.element("west", rasterDataContext.getWest());
		jsonObject.element("minimumValue", rasterDataContext.getDataMinimumValue());
		jsonObject.element("maximumValue", rasterDataContext.getDataMaximumValue());
		jsonObject.element("columns", effectiveColumns);
		jsonObject.element("rows", effectiveRows);
		jsonObject.element("latitudeResolution", latitudeResolution);
		jsonObject.element("longitudeResolution", longitudeResolution);
		
		
		double midLat = (rasterDataContext.getNorth() + effectiveSouth) / 2.0;
		double midLon = (rasterDataContext.getWest() + effectiveEast) / 2.0;
		
		Planet planet = PlanetsRegistry.getPlanet("earth");
		
		double meanRadius = (planet != null) ? planet.getMeanRadius() : DemConstants.EARTH_MEAN_RADIUS;
		double metersResolution = ModelDimensions.getMetersResolution(meanRadius, midLat, midLon, latitudeResolution, longitudeResolution);
		jsonObject.element("metersResolution", metersResolution / 1000.0);

		JSONArray rowArray = new JSONArray();
		
		for (int row = 0; row < effectiveRows; row++) {
			double latitude = rasterDataContext.getNorth() - (row * latitudeResolution);
			
			JSONArray columnArray = new JSONArray();
			
			for (int column = 0; column < effectiveColumns; column++) {
				double longitude = rasterDataContext.getWest() + (column * longitudeResolution);
				
				double elevation = rasterDataContext.getData(latitude, longitude);
				
				elevation = (elevation != DemConstants.ELEV_NO_DATA) ? elevation : 0.0;
				
				columnArray.add(elevation);
				
			}
			
			
			
			rowArray.add(columnArray);
		}	
		
		jsonObject.element("data", rowArray);
		
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(writePath));
		fos.write("var modelData = ".getBytes());
		fos.write(jsonObject.toString(3).getBytes());
		
		fos.close();
	}
	
	
	
	
	
}
