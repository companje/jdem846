package us.wthr.jdem846.project;

import java.io.BufferedInputStream;
import java.io.IOException;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.ModelOptions;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;
import us.wthr.jdem846.shapefile.ShapeFileRequest;

public class ProjectFileReadWriteTesting extends AbstractTestMain
{
	private static Log log = null;
	
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ProjectFileReadWriteTesting.class);
		
		
		ProjectFileReadWriteTesting tester = new ProjectFileReadWriteTesting();
		try {
			tester.doTesting();
		} catch (Exception ex) {
			log.error("Error running test: " + ex.getMessage(), ex);
		}
		
	}
	
	public void doTesting() throws Exception
	{
		String jsonTestPath = "C:/srv/workspaces/wthr.us/jdem846/resources/savefiles/project-test.jdem";
		String zipTestPath = "C:/srv/workspaces/wthr.us/jdem846/resources/savefiles/project-test.zdem";
		String xmlTestPath = "C:/srv/workspaces/wthr.us/jdem846/resources/savefiles/project-test.xdem";
		
		ModelOptions options = new ModelOptions();
		options.setUserScript(loadTemplateFile("resources://scripting/template-dem.groovy"));
		options.setScriptLanguage(ScriptLanguageEnum.GROOVY);
		ProjectModel projectModel = new ProjectModel();
		options.syncToProjectModel(projectModel);
		projectModel.setScriptLanguage(ScriptLanguageEnum.GROOVY);
		projectModel.getInputFiles().add("C:\\srv\\elevation\\Shapefiles\\Nashua NH\\Elevation 1-3 Arc Second\\Elevation 1-3 Arc Second.flt");
		projectModel.getShapeFiles().add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Nashua NH\\hydrography\\NHDArea.shp", "usgs-hydrography", false));
		projectModel.getShapeFiles().add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Nashua NH\\hydrography\\NHDFlowline.shp", "usgs-hydrography", false));
		projectModel.getShapeFiles().add(new ShapeFileRequest("C:\\srv\\elevation\\Shapefiles\\Nashua NH\\hydrography\\NHDWaterbody.shp", "usgs-hydrography", false));
		
		
		
		doJsonTesting(projectModel, jsonTestPath);
		doZipTesting(projectModel, zipTestPath);
		
		
		doTesting(projectModel, jsonTestPath);
		doTesting(projectModel, zipTestPath);
		doTesting(projectModel, xmlTestPath);
	}
	
	
	public void doTesting(ProjectModel projectModel, String projectTestPath) throws Exception
	{
		doWriteTesting(projectModel, projectTestPath);
		doReadTesting(projectTestPath);
	}
	
	public void doWriteTesting(ProjectModel projectModel, String projectTestPath) throws Exception
	{
		//ProjectFiles.write(projectModel, projectTestPath);
	}
	
	public ProjectModel doReadTesting(String projectTestPath) throws Exception
	{
		
		
		/*
		ProjectModel projectModel = ProjectFiles.read(projectTestPath);
		
		if (projectModel != null) {
			for (String key : projectModel.getOptionKeys()) {
				log.info("Setting: " + key + ": '" + projectModel.getOption(key) + "'");
			}
			
			log.info("Raster Data Layer Count: " + projectModel.getInputFiles().size());
			log.info("Shape Layer Count: " + projectModel.getShapeFiles().size());
			
			log.info("User Script: \n" + projectModel.getUserScript());
			
		} else {
			log.error("Project model is null!");
		}
		
		return projectModel;
		*/
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void doZipTesting(ProjectModel projectModel, String zipTestPath) throws Exception
	{
		doZipWriteTesting(projectModel, zipTestPath);
		doZipReadTesting(zipTestPath);
	}
	
	public void doZipWriteTesting(ProjectModel projectModel, String zipTestPath) throws Exception
	{
		//ZipProjectFileWriter.writeProject(projectModel, zipTestPath);
	}
	
	public ProjectModel doZipReadTesting(String zipTestPath) throws Exception
	{
		
		
		/*
		ProjectModel projectModel = ZipProjectFileReader.readProject(zipTestPath);
		
		if (projectModel != null) {
			for (String key : projectModel.getOptionKeys()) {
				log.info("Setting: " + key + ": '" + projectModel.getOption(key) + "'");
			}
			
			log.info("Raster Data Layer Count: " + projectModel.getInputFiles().size());
			log.info("Shape Layer Count: " + projectModel.getShapeFiles().size());
			
			log.info("User Script: \n" + projectModel.getUserScript());
			
		} else {
			log.error("Project model is null!");
		}
		
		return projectModel;
		*/
		return null;
	}
	
	public void doJsonTesting(ProjectModel projectModel, String jsonTestPath) throws Exception
	{
		doJsonWriteTesting(projectModel, jsonTestPath);
		doJsonReadTesting(jsonTestPath);
	}
	
	
	
	
	public void doJsonWriteTesting(ProjectModel projectModel, String jsonTestPath) throws Exception
	{
		//JsonProjectFileWriter.writeProject(projectModel, jsonTestPath);
	}
	
	public ProjectModel doJsonReadTesting(String jsonTestPath) throws Exception
	{
		
		
		/*
		ProjectModel projectModel = JsonProjectFileReader.readProject(jsonTestPath);
		
		if (projectModel != null) {
			for (String key : projectModel.getOptionKeys()) {
				log.info("Setting: " + key + ": '" + projectModel.getOption(key) + "'");
			}
			
			log.info("Raster Data Layer Count: " + projectModel.getInputFiles().size());
			log.info("Shape Layer Count: " + projectModel.getShapeFiles().size());
			
		} else {
			log.error("Project model is null!");
		}
		
		return projectModel;
		*
		*/
		return null;
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
		
		int length = 0;
		byte[] buffer = new byte[1024];
		
		while((length = in.read(buffer)) > 0) {
			templateBuffer.append(new String(buffer, 0, length));
		}
		
		return templateBuffer.toString();
	}
	
}
