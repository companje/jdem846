package us.wthr.jdem846.project.workspace.project;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ElevationModelReader;
import us.wthr.jdem846.project.JsonProjectFileReader;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class WorkspaceProjectReader
{
	private static Log log = Logging.getLog(WorkspaceProjectReader.class);
			
			
	
	public static boolean isValidWorkspaceProject(String path)
	{
		
		File f = new File(path);
		if (f.exists() && (f.getName().equalsIgnoreCase("project.demprj") || f.isDirectory()) && f.canRead()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isValidModelPath(File base)
	{

		File imageFileJpeg = new File(base, "image.jpg");
		File imageFilePng = new File(base,  "image.png");
		
		
		if (!base.exists() && !imageFileJpeg.exists() && !imageFilePng.exists()) {
			return false;
		} else {
			return true;
		}
	}
	
	protected static void loadElevationModels(ProjectMarshall projectMarshall, File projectBase, boolean preloadElevationModels) throws IOException, FileNotFoundException, ProjectParseException
	{
		
		for (int i = 0; i < 256; i++) {
			
			File modelBase = new File(projectBase, "models/" + i);
			if (isValidModelPath(modelBase)) {
				ElevationModel elevationModel = ElevationModelReader.loadElevationModelFromPath(modelBase.getAbsolutePath());
				if (elevationModel != null) {
					
					if (preloadElevationModels) {
						elevationModel.load();
					}
					
					projectMarshall.getElevationModels().add(elevationModel);
				}
			}

		}
		
	}
	
	
	public static ProjectMarshall readProject(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		return readProject(path, true);
	}
	
	public static ProjectMarshall readProject(String path, boolean preloadElevationModels) throws IOException, FileNotFoundException, ProjectParseException
	{
		ProjectMarshall projectMarshall = null;
		
		if (!isValidWorkspaceProject(path)) {
			throw new FileNotFoundException(path);
		}
		
		File projectBase = new File(path);
		if (!projectBase.isDirectory()) {
			projectBase = projectBase.getParentFile();
		}
		
		
		
		File projectSettingsJsonFile = new File(projectBase, "project.demprj");
		
		if (projectSettingsJsonFile.exists()) {
			InputStream projectSettingsInputStream = new BufferedInputStream(new FileInputStream(projectSettingsJsonFile));
			projectMarshall = JsonProjectFileReader.readProject(projectSettingsInputStream);
			projectSettingsInputStream.close();
		} else {
			throw new ProjectParseException("Project settings file not found!");
		}
		
		
		
		File scriptFile = null;
		if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.GROOVY) {
			scriptFile = new File(projectBase, "script.groovy");
		} else if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
			scriptFile = new File(projectBase, "script.py");
		} else if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.SCALA) {
			scriptFile = new File(projectBase, "script.scala");
		} else if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.JAVASCRIPT) {
			scriptFile = new File(projectBase, "script.js");
		}
		
		if (scriptFile != null && scriptFile.exists() && scriptFile.canRead()) {
			InputStream scriptFileInputStream = new BufferedInputStream(new FileInputStream(scriptFile));
			String scriptData = IOUtils.toString(scriptFileInputStream);
			scriptFileInputStream.close();
			projectMarshall.setUserScript(scriptData);
		}
		
		
		loadElevationModels(projectMarshall, projectBase, preloadElevationModels);
		
		return projectMarshall;
	}
}
