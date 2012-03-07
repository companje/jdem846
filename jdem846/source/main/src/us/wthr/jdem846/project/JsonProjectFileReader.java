package us.wthr.jdem846.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
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
	
	
	
	protected static void parseSettings(ProjectModel projectModel, JSONArray settingsArray) throws ProjectParseException
	{
		for (int i = 0; i < settingsArray.size(); i++) {
			JSONObject settingsObj = settingsArray.getJSONObject(i);

			String key = settingsObj.getString("key");
			String value = settingsObj.getString("value");
			
			if (key != null && value != null) {
				projectModel.setOption(key, value);
				
				if (key.equals("us.wthr.jdem846.modelOptions.userScript.language")) {
					projectModel.setScriptLanguage(value);
				}
				
			}
			
		}
	}
	
	protected static void parseRasterLayer(ProjectModel projectModel, JSONObject layerObj) throws ProjectParseException
	{
		String path = layerObj.getString("path");
		projectModel.getInputFiles().add(path);
	}
	
	protected static void parseImageLayer(ProjectModel projectModel, JSONObject layerObj) throws ProjectParseException
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
		
		
		projectModel.getImageFiles().add(image);
		
	}
	
	protected static void parseShapeLayer(ProjectModel projectModel, JSONObject layerObj) throws ProjectParseException
	{
		String path = layerObj.getString("path");
		String definitionId = layerObj.getString("defId");
		
		if (path != null && definitionId != null) {
			try {
				projectModel.getShapeFiles().add(new ShapeFileRequest(path, definitionId, false));
			} catch (ShapeFileException ex) {
				throw new ProjectParseException("Failed to load shapefile data when parsing project file", ex);
			}
		} else {
			throw new ProjectParseException("Incomplete value for loading shapefile: " + path + ", " + definitionId);
		}
		
	}
	
	protected static void parseLayers(ProjectModel projectModel, JSONArray layersArray) throws ProjectParseException
	{
		for (int i = 0; i < layersArray.size(); i++) {
			JSONObject layersObj = layersArray.getJSONObject(i);

			String type = layersObj.getString("type");
			
			if (type.equalsIgnoreCase("raster")) {
				JsonProjectFileReader.parseRasterLayer(projectModel, layersObj);
			} else if (type.equalsIgnoreCase("shapefile")) {
				JsonProjectFileReader.parseShapeLayer(projectModel, layersObj);
			} else if (type.equalsIgnoreCase("image")) {
				JsonProjectFileReader.parseImageLayer(projectModel, layersObj);
			} else {
				throw new ProjectParseException("Unrecognized layer type: " + type);
			}
			
		}
	}
	
	
	protected static ProjectModel parseProject(JSONObject json) throws ProjectParseException
	{
		ProjectModel projectModel = new ProjectModel();
		
		if (json.has("type")) {
			String projectTypeIdentifier = json.getString("type");
			if (projectTypeIdentifier != null) {
				ProjectTypeEnum projectType = ProjectTypeEnum.getProjectTypeFromIdentifier(projectTypeIdentifier);
				projectModel.setProjectType(projectType);
			}
		}
		
		JSONArray settingsArray = json.getJSONArray("settings");
		JSONArray layersArray = json.getJSONArray("layers");
		
		JsonProjectFileReader.parseSettings(projectModel, settingsArray);
		JsonProjectFileReader.parseLayers(projectModel, layersArray);
		
		return projectModel;
	}
	
	
	public static ProjectModel readProject(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		log.info("Opening project file: " + path);
		
		if (!fileExists(path)) {
			throw new FileNotFoundException(path);
		}
		
		InputStream in = JDemResourceLoader.getAsInputStream(path);
		return readProject(in);
	}
	
	
	public static ProjectModel readProject(InputStream in) throws IOException, FileNotFoundException, ProjectParseException
	{
		ProjectModel projectModel = null;
		
		JSONObject json = JsonProjectFileReader.loadProjectFile(in);
		projectModel = JsonProjectFileReader.parseProject(json);
		
		return projectModel;
		
	}
}
