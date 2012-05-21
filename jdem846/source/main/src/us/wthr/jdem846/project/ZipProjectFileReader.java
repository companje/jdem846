package us.wthr.jdem846.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class ZipProjectFileReader
{
	private static Log log = Logging.getLog(ZipProjectFileReader.class);
	
	
	protected ZipProjectFileReader()
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
	
	
	protected static void loadScriptFromZip(ProjectMarshall projectMarshall, InputStream in) throws IOException
	{
		byte[] buffer = new byte[1024];
		int len = 0;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		while((len = in.read(buffer)) > 0) {
			baos.write(buffer, 0, len);
		}
		
		String script = new String(baos.toByteArray());
		projectMarshall.setUserScript(script);
	}
	
	public static ProjectMarshall readProject(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		log.info("Opening project file: " + path);
		ProjectMarshall projectMarshall = null;
		
		if (!fileExists(path)) {
			throw new FileNotFoundException(path);
		}
		
		File file = JDemResourceLoader.getAsFile(path);
		ZipFile zipFile = new ZipFile(file);
		
		ZipEntry jsonSettingsEntry = zipFile.getEntry("project.json");
		
		if (jsonSettingsEntry != null) {
			projectMarshall = JsonProjectFileReader.readProject(zipFile.getInputStream(jsonSettingsEntry));
		} else {
			throw new ProjectParseException("Project settings file not found!");
		}
		
		ZipEntry scriptEntry = null;
		if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.GROOVY) {
			scriptEntry = zipFile.getEntry("script.groovy");
		} else if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
			scriptEntry = zipFile.getEntry("script.py");
		}
		
		if (scriptEntry != null) {
			loadScriptFromZip(projectMarshall, zipFile.getInputStream(scriptEntry));
		} else {
			log.info("Script file not found, cannot load");
		}
		
		
		return projectMarshall;
	}
}
