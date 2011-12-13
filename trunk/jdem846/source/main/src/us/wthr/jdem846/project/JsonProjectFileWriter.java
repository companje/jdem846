package us.wthr.jdem846.project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeFileRequest;


public class JsonProjectFileWriter
{
	private static Log log = Logging.getLog(JsonProjectFileWriter.class);
	
	
	protected JsonProjectFileWriter()
	{
		
	}
	
	
	protected static JSONArray createSettingsObject(ProjectModel projectModel)
	{
		JSONArray settingsArray = new JSONArray();
		for (String key : projectModel.getOptionKeys()) {
			String value = projectModel.getOption(key);
			
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
	
	protected static JSONObject createShapeObject(ShapeFileRequest shapeFileReq)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("type", "shapefile");
		jsonObject.element("path", shapeFileReq.getPath());
		jsonObject.element("defId", shapeFileReq.getShapeDataDefinitionId());
		return jsonObject;
	}
	
	
	protected static JSONArray createLayersObject(ProjectModel projectModel)
	{
		JSONArray layersArray = new JSONArray();
		
		for (String path : projectModel.getInputFiles()) {
			JSONObject rasterObj = createRasterObject(path);
			layersArray.add(rasterObj);
		}
		
		for (ShapeFileRequest shapeFileReq : projectModel.getShapeFiles()) {
			JSONObject shapeObj = createShapeObject(shapeFileReq);
			layersArray.add(shapeObj);
		}
		
		return layersArray;
	}
	
	protected static JSONObject createJsonObject(ProjectModel projectModel)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.element("settings", createSettingsObject(projectModel));
		jsonObject.element("layers", createLayersObject(projectModel));
		
		return jsonObject;
	}
	
	
	public static void writeProject(ProjectModel projectModel, String path) throws IOException
	{
		log.info("Writing project file to " + path);
		File file = JDemResourceLoader.getAsFile(path);
		OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
		writeProject(projectModel, fos);
		fos.close();
		
	}
	
	
	public static void writeProject(ProjectModel projectModel, OutputStream out) throws IOException
	{
		
		
		JSONObject jsonObject = JsonProjectFileWriter.createJsonObject(projectModel);
		String json = jsonObject.toString(3);

		out.write(json.getBytes());
		
		//log.info(jsonObject.toString(3));
	}
	
}
