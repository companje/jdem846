package us.wthr.jdem846.project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeFileRequest;


public class JsonProjectFileWriter
{
	private static Log log = Logging.getLog(JsonProjectFileWriter.class);
	
	
	protected JsonProjectFileWriter()
	{
		
	}
	
	
	protected static JSONArray createSettingsObject(Map<String, String> globalOptions)
	{
		JSONArray settingsArray = new JSONArray();
		for (String key : globalOptions.keySet()) {
			String value = globalOptions.get(key);
			
			JSONObject settingsObject = new JSONObject();
			settingsObject.element("key", key);
			settingsObject.element("value", value);
			settingsArray.add(settingsObject);
		}
		return settingsArray;
	}
	
	
	
	
	protected static JSONObject createRasterObject(String path)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("type", "raster");
		jsonObject.element("path", path);
		return jsonObject;
	}
	
	protected static JSONObject createImageObject(SimpleGeoImage image)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("type", "image");
		jsonObject.element("path", image.getImageFile());
		jsonObject.element("north", image.getNorth());
		jsonObject.element("south", image.getSouth());
		jsonObject.element("east", image.getEast());
		jsonObject.element("west", image.getWest());
		return jsonObject;
	}
	
	protected static JSONObject createShapeObject(ShapeFileRequest shapeFileReq)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("type", "shapefile");
		jsonObject.element("path", shapeFileReq.getPath());
		jsonObject.element("defId", shapeFileReq.getShapeDataDefinitionId());
		return jsonObject;
	}
	
	
	protected static JSONArray createLayersObject(ProjectMarshall projectMarshall)
	{
		JSONArray layersArray = new JSONArray();
		
		for (String path : projectMarshall.getRasterFiles()) {
			JSONObject rasterObj = createRasterObject(path);
			layersArray.add(rasterObj);
		}
		
		for (ShapeFileRequest shapeFileReq : projectMarshall.getShapeFiles()) {
			JSONObject shapeObj = createShapeObject(shapeFileReq);
			layersArray.add(shapeObj);
		}
		
		for (SimpleGeoImage image : projectMarshall.getImageFiles()) {
			JSONObject imageObj = createImageObject(image);
			layersArray.add(imageObj);
		}
		
		return layersArray;
	}
	
	protected static JSONObject createProcessObject(ProcessMarshall processMarshall)
	{
		JSONObject processObject = new JSONObject();
		
		processObject.element("id", processMarshall.getId());
		processObject.element("options", createSettingsObject(processMarshall.getOptions()));
		
		return processObject;
	}
	
	protected static JSONArray createProcessesArray(ProjectMarshall projectMarshall)
	{
		JSONArray processesArray = new JSONArray();
		
		for (ProcessMarshall processMarshall : projectMarshall.getProcesses()) {
			
			processesArray.add(createProcessObject(processMarshall));
			
		}
		
		return processesArray;
	}
	
	
	protected static JSONObject createJsonObject(ProjectMarshall projectMarshall)
	{
		JSONObject jsonObject = new JSONObject();
		
		if (projectMarshall.getProjectType() != null) {
			jsonObject.element("type", projectMarshall.getProjectType().identifier());
		}
		
		if (projectMarshall.getScriptLanguage() != null) {
			jsonObject.element("scriptLanguage", projectMarshall.getScriptLanguage().text());
		}
		
		jsonObject.element("global", createSettingsObject(projectMarshall.getGlobalOptions()));
		jsonObject.element("processes", createProcessesArray(projectMarshall));
		jsonObject.element("layers", createLayersObject(projectMarshall));
		
		return jsonObject;
	}
	
	
	public static void writeProject(ProjectMarshall projectMarshall, String path) throws IOException
	{
		log.info("Writing project file to " + path);
		File file = JDemResourceLoader.getAsFile(path);
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
		writeProject(projectMarshall, fos);
		fos.close();
		
	}
	
	
	public static void writeProject(ProjectMarshall projectMarshall, OutputStream out) throws IOException
	{
		
		
		JSONObject jsonObject = JsonProjectFileWriter.createJsonObject(projectMarshall);
		String json = jsonObject.toString(3);

		out.write(json.getBytes());
		
		//log.info(jsonObject.toString(3));
	}
	
}
