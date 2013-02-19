package us.wthr.jdem846.project.workspace.project;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.project.JsonProjectFileWriter;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class WorkspaceProjectWriter
{
	private static Log log = Logging.getLog(WorkspaceProjectWriter.class);
	
	
	
	protected static void writeJDemElevationModel(ElevationModel model, int index, File projectBase) throws IOException
	{
		
		if (!model.isLoaded()) {
			return;
		}
		
		File elevationModelBase = new File(projectBase, "models/" + index);
		if (!elevationModelBase.exists()) {
			elevationModelBase.mkdirs();
		}
		
		File imageFile = new File(elevationModelBase, "image.jpg");
		OutputStream imageOut = new BufferedOutputStream(new FileOutputStream(imageFile));
		model.writeImageData(imageOut, ImageTypeEnum.imageTypeFromFormatName(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.defaultImageFormat")));
		imageOut.flush();
		imageOut.close();
		
		
		//File dataFile = new File(elevationModelBase, "model.dat");
		//OutputStream dataOut = new BufferedOutputStream(new FileOutputStream(dataFile));
		//model.writeModelData(dataOut);
		//dataOut.flush();
		//dataOut.close();
		

		ElevationHistogramModel histogram = model.getElevationHistogramModel();
		if (histogram != null) {
			
			File histogramFile = new File(elevationModelBase, "elevation-histogram.dat");
			OutputStream histogramOut = new BufferedOutputStream(new FileOutputStream(histogramFile));
			histogram.write(histogramOut);
			histogramOut.flush();
			histogramOut.close();

		}
		
		File propertiesFile = new File(elevationModelBase, "properties.json");
		OutputStream propertiesOut = new BufferedOutputStream(new FileOutputStream(propertiesFile));
		model.writeProperties(propertiesOut);
		propertiesOut.flush();
		propertiesOut.close();
		
	}
	
	protected static void writeJDemElevationModels(List<ElevationModel> modelList, File projectBase) throws IOException
	{

		for (int i = 0; i < modelList.size(); i++) {
			ElevationModel model = modelList.get(i);
			writeJDemElevationModel(model, i, projectBase);
		}
	}
	
	public static void writeProject(ProjectMarshall projectMarshall, String path, boolean createPathIfNotExists) throws IOException
	{
		log.info("Writing project file to " + path);
		
		
		
		
		File projectBase = new File(path);
		
		
		
		if (path.toLowerCase().endsWith(".demprj")) {
			projectBase = projectBase.getParentFile();
		}
		
		if (projectBase.exists() && !projectBase.isDirectory()) {
			throw new IOException("Invalid project location: not a directory");
		}
		
		if (!projectBase.exists() && createPathIfNotExists) {
			projectBase.mkdirs();
		} else if (!projectBase.exists()) {
			throw new IOException("Project base path does not exist: '" + path + "'");
		}
		
		
		OutputStream projectFileOutputStream = new BufferedOutputStream(new FileOutputStream(new File(projectBase, "project.demprj")));
		JsonProjectFileWriter.writeProject(projectMarshall, projectFileOutputStream);
		projectFileOutputStream.flush();
		projectFileOutputStream.close();

		
		
		
		List<ElevationModel> modelList = projectMarshall.getElevationModels();
		if (modelList != null && modelList.size() > 0) {
			writeJDemElevationModels(modelList, projectBase);
		}
		
		if (projectMarshall.getUserScript() != null) {
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
			
			if (scriptFile != null) {
				
				OutputStream scriptFileOutputStream = new BufferedOutputStream(new FileOutputStream(scriptFile));
				scriptFileOutputStream.write(projectMarshall.getUserScript().getBytes());
				scriptFileOutputStream.flush();
				scriptFileOutputStream.close();
			}
		} else {
			log.info("User script is null, cannot write.");
		}

	}
	
}
