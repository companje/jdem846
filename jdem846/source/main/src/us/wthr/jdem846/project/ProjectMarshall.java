package us.wthr.jdem846.project;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ProjectMarshalException;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.rasterdata.RasterData;
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
	
	private List<JDemElevationModel> modelList = new LinkedList<JDemElevationModel>();
	
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = null;
	
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
	
	public void setElevationModels(List<JDemElevationModel> modelList)
	{
		this.modelList = modelList;
	}
	
	public List<JDemElevationModel> getElevationModels()
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
	
}
