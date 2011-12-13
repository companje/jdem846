package us.wthr.jdem846.project;

import java.io.FileNotFoundException;
import java.io.IOException;

import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ProjectFiles
{
	private static Log log = Logging.getLog(ProjectFiles.class);
	
	
	protected ProjectFiles()
	{
		
	}
	
	public static void write(ProjectModel projectModel, String path) throws IOException, ProjectParseException
	{
		String ext = path.substring(path.lastIndexOf("."));
		if (ext == null) {
			throw new ProjectParseException("Invalid file format: " + path);
		} else if (ext.equalsIgnoreCase(".jdem") || ext.equalsIgnoreCase(".json")) {
			JsonProjectFileWriter.writeProject(projectModel, path);
		} else if (ext.equalsIgnoreCase(".zdem") || ext.equalsIgnoreCase(".zip")) {
			ZipProjectFileWriter.writeProject(projectModel, path);
		} else if (ext.equalsIgnoreCase(".xdem") || ext.equalsIgnoreCase(".xml")) {
			XmlProjectFileWriter.writeProject(projectModel, path);
		} else {
			throw new ProjectParseException("Unrecognized Extension: " + ext);
		}
	}
	
	public static ProjectModel read(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		
		String ext = path.substring(path.lastIndexOf("."));
		if (ext == null) {
			throw new ProjectParseException("Invalid file format: " + path);
		} else if (ext.equalsIgnoreCase(".jdem") || ext.equalsIgnoreCase(".json")) {
			return JsonProjectFileReader.readProject(path);
		} else if (ext.equalsIgnoreCase(".zdem") || ext.equalsIgnoreCase(".zip")) {
			return ZipProjectFileReader.readProject(path);
		} else if (ext.equalsIgnoreCase(".xdem") || ext.equalsIgnoreCase(".xml")) {
			return XmlProjectFileReader.readProject(path);
		} else {
			throw new ProjectParseException("Unrecognized Extension: " + ext);
		}

	}
	
}
