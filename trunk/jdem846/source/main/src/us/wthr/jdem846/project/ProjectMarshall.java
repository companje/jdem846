package us.wthr.jdem846.project;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapefile.ShapeFileRequest;

public class ProjectMarshall
{
	private static Log log = Logging.getLog(ProjectMarshall.class);
	
	private ProjectTypeEnum projectType = ProjectTypeEnum.STANDARD_PROJECT;
	
	private Map<String, String> globalOptions = new HashMap<String, String>();
	private List<ProcessMarshall> processes = new LinkedList<ProcessMarshall>();
	
	private List<String> rasterFiles = new LinkedList<String>();
	private List<ShapeFileRequest> shapeFiles = new LinkedList<ShapeFileRequest>();
	private List<SimpleGeoImage> imageFiles = new LinkedList<SimpleGeoImage>();
	
	private List<ElevationModel> modelList = new LinkedList<ElevationModel>();
	
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = ScriptLanguageEnum.JAVASCRIPT;
	
	private String loadedFrom = null;
	
	public ProjectMarshall()
	{
		
	}
	
	public ProjectTypeEnum getProjectType()
	{
		return projectType;
	}
	
	public void setProjectType(ProjectTypeEnum projectType)
	{
		this.projectType = projectType;
	}
	
	public void setProjectType(String identifier)
	{
		this.projectType = ProjectTypeEnum.getProjectTypeFromIdentifier(identifier);
	}
	
	
	
	
	public Map<String, String> getGlobalOptions()
	{
		return globalOptions;
	}

	public void setGlobalOptions(Map<String, String> globalOptions)
	{
		this.globalOptions = globalOptions;
	}

	public List<ProcessMarshall> getProcesses()
	{
		return processes;
	}

	public void setProcesses(List<ProcessMarshall> processes)
	{
		this.processes = processes;
	}

	public List<String> getRasterFiles()
	{
		return rasterFiles;
	}

	public void setRasterFiles(List<String> rasterFiles)
	{
		this.rasterFiles = rasterFiles;
	}

	public List<ShapeFileRequest> getShapeFiles()
	{
		return shapeFiles;
	}
	
	public void setElevationModels(List<ElevationModel> modelList)
	{
		this.modelList = modelList;
	}
	
	public List<ElevationModel> getElevationModels()
	{
		return this.modelList;
	}

	public void setShapeFiles(List<ShapeFileRequest> shapeFiles)
	{
		this.shapeFiles = shapeFiles;
	}

	public List<SimpleGeoImage> getImageFiles()
	{
		return imageFiles;
	}

	public void setImageFiles(List<SimpleGeoImage> imageFiles)
	{
		this.imageFiles = imageFiles;
	}

	public String getUserScript()
	{
		return userScript;
	}

	public void setUserScript(String userScript)
	{
		this.userScript = userScript;
	}

	public ScriptLanguageEnum getScriptLanguage()
	{
		return scriptLanguage;
	}

	public void setScriptLanguage(ScriptLanguageEnum scriptLanguage)
	{
		this.scriptLanguage = scriptLanguage;
	}
	
	public String getLoadedFrom() 
	{
		return loadedFrom;
	}
	
	public void setLoadedFrom(String loadedFrom) 
	{
		this.loadedFrom = loadedFrom;
	}
	
	public boolean containsProcess(String processId)
	{
		return (getProcessMarshall(processId) != null);
	}
	
	
	public ProcessMarshall getProcessMarshall(String processId)
	{
		for (ProcessMarshall processMarshall : this.processes) {
			if (processMarshall.getId() != null && processMarshall.getId().equals(processId)) {
				return processMarshall;
			}
		}
		
		return null;
	}
	
	
	public void loadDefaults() throws Exception
	{
		String scriptTemplatePath = JDem846Properties.getProperty("us.wthr.jdem846.userScript.javascript.template");
		
		
		String scriptTemplate = loadTemplateFile(scriptTemplatePath);
		this.setUserScript(scriptTemplate);
	}
	
	protected String loadTemplateFile(String path) throws IOException
	{
		if (path == null) {
			log.warn("Cannot load template file: path is null");
			return null;
		}

		log.info("Loading script template file from path '" + path + "'");
		StringBuffer templateBuffer = new StringBuffer();

		BufferedInputStream in = new BufferedInputStream(JDemResourceLoader.getAsInputStream(path));
		
		
		String script = IOUtils.toString(in);
		return script;
/*		
		int length = 0;
		byte[] buffer = new byte[1024];

		while ((length = in.read(buffer)) > 0) {
			templateBuffer.append(new String(buffer, 0, length));
		}

		return templateBuffer.toString();*/
	}
	
}
