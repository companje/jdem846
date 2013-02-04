package us.wthr.jdem846.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.workspace.project.WorkspaceProjectReader;
import us.wthr.jdem846.project.workspace.project.WorkspaceProjectWriter;

public class ProjectFiles
{
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ProjectFiles.class);
	
	
	protected ProjectFiles()
	{
		
	}
	
	public static void write(ProjectMarshall projectMarshall, String path) throws IOException, ProjectParseException
	{
		String ext = path.substring(path.lastIndexOf("."));
		if (ext == null) {
			WorkspaceProjectWriter.writeProject(projectMarshall, path, true);
			//throw new ProjectParseException("Invalid file format: " + path);
		} else if (ext.equalsIgnoreCase(".jdem") || ext.equalsIgnoreCase(".json")) {
			JsonProjectFileWriter.writeProject(projectMarshall, path);
		} else if (ext.equalsIgnoreCase(".jdemimg") || ext.equalsIgnoreCase(".jdemprj") || ext.equalsIgnoreCase(".zdem") || ext.equalsIgnoreCase(".zip")) {
			ZipProjectFileWriter.writeProject(projectMarshall, path);
		} else if (ext.equalsIgnoreCase(".demprj")) {
			WorkspaceProjectWriter.writeProject(projectMarshall, path, true);
		} else {
			WorkspaceProjectWriter.writeProject(projectMarshall, path, true);
			//throw new ProjectParseException("Unrecognized Extension: " + ext);
		}
	}
	
	public static ProjectMarshall read(String path, boolean preloadElevationModels) throws IOException, FileNotFoundException, ProjectParseException
	{
		
		File projectFile = new File(path);
		if (projectFile.canRead() && projectFile.isDirectory()) {
			return WorkspaceProjectReader.readProject(path, preloadElevationModels);
		} else {
			String ext = path.substring(path.lastIndexOf("."));
			if (ext == null) {
				throw new ProjectParseException("Invalid file format: " + path);
			} else if (ext.equalsIgnoreCase(".jdem") || ext.equalsIgnoreCase(".json")) {
				return JsonProjectFileReader.readProject(path);
			} else if (ext.equalsIgnoreCase(".jdemimg") || ext.equalsIgnoreCase(".jdemprj") || ext.equalsIgnoreCase(".zdem") || ext.equalsIgnoreCase(".zip")) {
				return ZipProjectFileReader.readProject(path);
			} else if (ext.equalsIgnoreCase(".demprj")) {
				return WorkspaceProjectReader.readProject(path, preloadElevationModels);
			} else {
				throw new ProjectParseException("Unrecognized Extension: " + ext);
			}
		}
		
		

	}
	
}
