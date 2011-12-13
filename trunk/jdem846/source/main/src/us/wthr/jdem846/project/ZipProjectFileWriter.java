package us.wthr.jdem846.project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class ZipProjectFileWriter
{
	private static Log log = Logging.getLog(ZipProjectFileWriter.class);
	
	protected ZipProjectFileWriter()
	{
		
		
	}
	
	
	public static void writeProject(ProjectModel projectModel, String path) throws IOException
	{
		log.info("Writing project file to " + path);
		File file = JDemResourceLoader.getAsFile(path);
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		
		ZipEntry settingsEntry = new ZipEntry("project.json");
		zos.putNextEntry(settingsEntry);
		
		JsonProjectFileWriter.writeProject(projectModel, zos);
		
		
		if (projectModel.getUserScript() != null) {
			ZipEntry scriptEntry = null;
			if (projectModel.getScriptLanguage() == ScriptLanguageEnum.GROOVY) {
				scriptEntry = new ZipEntry("script.groovy");
			} else if (projectModel.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
				scriptEntry = new ZipEntry("script.py");
			}
			
			if (scriptEntry != null) {
				zos.putNextEntry(scriptEntry);
				zos.write(projectModel.getUserScript().getBytes());
			}
		} else {
			log.info("User script is null, cannot write.");
		}
		
		zos.close();
	}
	
	
}
