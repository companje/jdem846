package us.wthr.jdem846.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.ByteOrder;
import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.InterleavingTypeEnum;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.RasterDataSource;
import us.wthr.jdem846.rasterdata.generic.IRasterDefinition;
import us.wthr.jdem846.rasterdata.generic.RasterDefinition;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.shapefile.exception.ShapeFileException;

public class JsonProjectFileReader
{
	private static Log log = Logging.getLog(JsonProjectFileReader.class);
	
	protected JsonProjectFileReader()
	{
		
	}
	
	protected static boolean fileExists(String path) throws FileNotFoundException
	{
		File f = new File(path);
		if (!f.exists()) {
			throw new FileNotFoundException(path);
		}
		return true;
	}
	
	
	protected static JSONObject loadProjectFile(InputStream in)  throws IOException
	{
		
		String jsonTxt = IOUtils.toString( in );
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
		return json;
	}
	
	
	
	protected static void parseOptions(Map<String, String> optionsMap, JSONArray optionsArray)
	{
		for (int i = 0; i < optionsArray.size(); i++) {
			JSONObject settingsObj = optionsArray.getJSONObject(i);

			String key = settingsObj.getString("key");
			String value = settingsObj.getString("value");
			
			if (key != null && value != null) {
				optionsMap.put(key, value);
			}
			
		}
		
		
	}
	
	protected static ProcessMarshall parseProcess(JSONObject jsonProcess) throws ProjectParseException
	{
		ProcessMarshall processMarshall = new ProcessMarshall();
		processMarshall.setId(jsonProcess.getString("id"));
		
		JSONArray optionsArray = jsonProcess.getJSONArray("options");
		parseOptions(processMarshall.getOptions(), optionsArray);
		
		return processMarshall;
	}
	
	protected static void parseProcesses(ProjectMarshall projectMarshall, JSONArray processArray) throws ProjectParseException
	{
		
		for (int i = 0; i < processArray.size(); i++) {
			JSONObject processObj = processArray.getJSONObject(i);
			
			ProcessMarshall processMarshall = parseProcess(processObj);
			projectMarshall.getProcesses().add(processMarshall);
		}
		
		
	}
	
	
	protected static void parseRasterLayer(ProjectMarshall projectMarshall, JSONObject layerObj) throws ProjectParseException
	{
		String path = layerObj.getString("path");
		
		IRasterDefinition rasterDefinition = new RasterDefinition();
		
		if (layerObj.has("north")) {
			double north = layerObj.getDouble("north");
			rasterDefinition.setNorth(north);
		}
		
		if (layerObj.has("south")) {
			double south = layerObj.getDouble("south");
			rasterDefinition.setSouth(south);
		}
		
		if (layerObj.has("east")) {
			double east = layerObj.getDouble("east");
			rasterDefinition.setEast(east);
		}
		
		if (layerObj.has("west")) {
			double west = layerObj.getDouble("west");
			rasterDefinition.setWest(west);
		}
		
		if (layerObj.has("latitudeResolution")) {
			double latitudeResolution = layerObj.getDouble("latitudeResolution");
			rasterDefinition.setLatitudeResolution(latitudeResolution);
		}
		
		if (layerObj.has("longitudeResolution")) {
			double longitudeResolution = layerObj.getDouble("longitudeResolution");
			rasterDefinition.setLongitudeResolution(longitudeResolution);
		}
		
		if (layerObj.has("noData")) {
			double noData = layerObj.getDouble("noData");
			rasterDefinition.setNoData(noData);
		}
		
		if (layerObj.has("imageWidth")) {
			int imageWidth = layerObj.getInt("imageWidth");
			rasterDefinition.setImageWidth(imageWidth);
		}
		
		if (layerObj.has("imageHeight")) {
			int imageHeight = layerObj.getInt("imageHeight");
			rasterDefinition.setImageHeight(imageHeight);
		}
		
		if (layerObj.has("numBands")) {
			int numBands = layerObj.getInt("numBands");
			rasterDefinition.setNumBands(numBands);
		}
		
		if (layerObj.has("headerSize")) {
			int headerSize = layerObj.getInt("headerSize");
			rasterDefinition.setHeaderSize(headerSize);
		}
		
		if (layerObj.has("dataType")) {
			DataTypeEnum dataType = DataTypeEnum.valueOf(layerObj.getString("dataType"));
			rasterDefinition.setDataType(dataType);
		}
		
		if (layerObj.has("byteOrder")) {
			ByteOrder byteOrder = ByteOrder.valueOf(layerObj.getString("byteOrder"));
			rasterDefinition.setByteOrder(byteOrder);
		}
		
		if (layerObj.has("interleavingType")) {
			InterleavingTypeEnum interleavingType = InterleavingTypeEnum.valueOf(layerObj.getString("interleavingType"));
			rasterDefinition.setInterleavingType(interleavingType);
		}
		
		if (layerObj.has("locked")) {
			boolean locked = layerObj.getBoolean("locked");
			rasterDefinition.setLocked(locked);
		}
		
		projectMarshall.getRasterFiles().add(new RasterDataSource(path, rasterDefinition));
	}
	
	protected static void parseImageLayer(ProjectMarshall projectMarshall, JSONObject layerObj) throws ProjectParseException
	{
		
		String path = layerObj.getString("path");
		double north = layerObj.getDouble("north");
		double south = layerObj.getDouble("south");
		double east = layerObj.getDouble("east");
		double west = layerObj.getDouble("west");
		
		SimpleGeoImage image = null;
		
		try {
			image = new SimpleGeoImage(path, north, south, east, west);
		} catch (DataSourceException ex) {
			throw new ProjectParseException("Failed to load image: " + ex.getMessage(), ex);
		}
		
		
		projectMarshall.getImageFiles().add(image);
		
	}
	
	protected static void parseShapeLayer(ProjectMarshall projectMarshall, JSONObject layerObj) throws ProjectParseException
	{
		String path = layerObj.getString("path");
		String definitionId = null;
		
		if (layerObj.has("defId")) {
			definitionId = layerObj.getString("defId");
		}
		
		if (path != null) {
			try {
				projectMarshall.getShapeFiles().add(new ShapeFileRequest(path, definitionId, false));
			} catch (ShapeFileException ex) {
				throw new ProjectParseException("Failed to load shapefile data when parsing project file", ex);
			}
		} else {
			throw new ProjectParseException("Incomplete value for loading shapefile: " + path + ", " + definitionId);
		}
		
	}
	
	protected static void parseModelGridLayer(ProjectMarshall projectMarshall, JSONObject layerObj) throws ProjectParseException
	{
		String path = layerObj.getString("path");
		if (path != null) {
			projectMarshall.setModelGrid(path);
		} else {
			throw new ProjectParseException("Incomplete information for model grid layer: missing path");
		}
		
	}
	
	protected static void parseLayers(ProjectMarshall projectMarshall, JSONArray layersArray) throws ProjectParseException
	{
		for (int i = 0; i < layersArray.size(); i++) {
			JSONObject layersObj = layersArray.getJSONObject(i);

			String type = layersObj.getString("type");
			
			if (type.equalsIgnoreCase("raster")) {
				JsonProjectFileReader.parseRasterLayer(projectMarshall, layersObj);
			} else if (type.equalsIgnoreCase("shapefile")) {
				JsonProjectFileReader.parseShapeLayer(projectMarshall, layersObj);
			} else if (type.equalsIgnoreCase("image")) {
				JsonProjectFileReader.parseImageLayer(projectMarshall, layersObj);
			} else if (type.equalsIgnoreCase("modelGrid")) {
				JsonProjectFileReader.parseModelGridLayer(projectMarshall, layersObj);
			} else {
				throw new ProjectParseException("Unrecognized layer type: " + type);
			}
			
		}
	}
	
	
	protected static ProjectMarshall parseProject(JSONObject json) throws ProjectParseException
	{
		ProjectMarshall projectMarshall = new ProjectMarshall();
		
		if (json.has("type")) {
			String projectTypeIdentifier = json.getString("type");
			if (projectTypeIdentifier != null) {
				ProjectTypeEnum projectType = ProjectTypeEnum.getProjectTypeFromIdentifier(projectTypeIdentifier);
				projectMarshall.setProjectType(projectType);
			}
		}
		
		JSONArray globalOptionsArray = json.getJSONArray("global");
		JSONArray processesArray = json.getJSONArray("processes");
		JSONArray layersArray = json.getJSONArray("layers");
		
		String scriptLanguage = null;
		if (json.has("scriptLanguage")) {
			scriptLanguage = json.getString("scriptLanguage");
		}
		
		
		if (scriptLanguage != null) {
			
			projectMarshall.setScriptLanguage(ScriptLanguageEnum.getLanguageFromString(scriptLanguage));
		}
		
		if (globalOptionsArray != null) {
			JsonProjectFileReader.parseOptions(projectMarshall.getGlobalOptions(), globalOptionsArray);
		}
		
		if (processesArray != null) {
			JsonProjectFileReader.parseProcesses(projectMarshall, processesArray);
		}
		
		if (layersArray != null) {
			JsonProjectFileReader.parseLayers(projectMarshall, layersArray);
		}
		
		return projectMarshall;
	}
	
	
	public static ProjectMarshall readProject(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		log.info("Opening project file: " + path);
		
		if (!fileExists(path)) {
			throw new FileNotFoundException(path);
		}
		
		InputStream in = JDemResourceLoader.getAsInputStream(path);
		return readProject(in);
	}
	
	
	public static ProjectMarshall readProject(InputStream in) throws IOException, FileNotFoundException, ProjectParseException
	{
		ProjectMarshall projectMarshall = null;
		
		JSONObject json = JsonProjectFileReader.loadProjectFile(in);
		projectMarshall = JsonProjectFileReader.parseProject(json);
		
		return projectMarshall;
		
	}
}
