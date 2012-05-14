package us.wthr.jdem846.project;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.exception.ProjectMarshalException;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ModelProcessContainer;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapefile.ShapeFileRequest;

public class ProjectMarshaller
{
	private static Log log = Logging.getLog(ProjectMarshaller.class);
	
	private Map<String, String> globalOptions = new HashMap<String, String>();
	private List<ProcessMarshall> processes = new LinkedList<ProcessMarshall>();
	
	private List<String> inputFiles = new LinkedList<String>();
	private List<ShapeFileRequest> shapeFiles = new LinkedList<ShapeFileRequest>();
	private List<SimpleGeoImage> imageFiles = new LinkedList<SimpleGeoImage>();
	
	private String userScript = null;
	private ScriptLanguageEnum scriptLanguage = null;
	
	protected ProjectMarshaller()
	{
		
	}
	
	
	
	
	public static ProjectMarshaller marshallProject(ModelContext modelContext) throws ProjectMarshalException
	{
		ProjectMarshaller pm = new ProjectMarshaller();
		
		try {
			pm.globalOptions = modelContext.getModelProcessManifest().getGlobalOptionModelContainer().getPropertyMapById();
		} catch (ModelContainerException ex) {
			throw new ProjectMarshalException("Error marshalling global option model: " + ex.getMessage(), ex);
		}
		
		for (ModelProcessContainer processContainer : modelContext.getModelProcessManifest().getProcessList()) {
			
			ProcessMarshall processMarshall = ProcessMarshall.marshalProcess(processContainer);
			pm.processes.add(processMarshall);

		}

		// TODO: Input files...
		
		pm.userScript = modelContext.getScriptingContext().getUserScript();
		pm.scriptLanguage = modelContext.getScriptingContext().getScriptLanguage();
		
		return pm;
	}
	
}
