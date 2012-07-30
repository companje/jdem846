package us.wthr.jdem846.project;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.scripting.ScriptLanguageEnum;

public class ZipProjectFileWriter
{
	private static Log log = Logging.getLog(ZipProjectFileWriter.class);
	
	protected ZipProjectFileWriter()
	{
		
		
	}
	
	protected static void writeJDemElevationModel(JDemElevationModel model, int index, ZipOutputStream zos) throws IOException
	{
		//ZipEntry imageEntry = new ZipEntry("models/" + index + "/image.png");
		ZipEntry imageEntry = new ZipEntry("models/" + index + "/image.jpg");
		zos.putNextEntry(imageEntry);
		model.writeImageData(zos, ImageTypeEnum.imageTypeFromFormatName(JDem846Properties.getProperty("us.wthr.jdem846.general.ui.defaultImageFormat")));
		zos.closeEntry();
		
		
		
		ZipEntry dataEntry = new ZipEntry("models/" + index + "/model.dat");
		zos.putNextEntry(dataEntry);
		model.writeModelData(zos);
		zos.closeEntry();
		
		ElevationHistogramModel histogram = model.getElevationHistogramModel();
		if (histogram != null) {
			ZipEntry histogramEntry = new ZipEntry("models/" + index + "/elevation-histogram.dat");
			zos.putNextEntry(histogramEntry);
			histogram.write(zos);
			zos.closeEntry();
		}
		
		
		ZipEntry propertiesEntry = new ZipEntry("models/" + index + "/properties.json");
		zos.putNextEntry(propertiesEntry);
		model.writeProperties(zos);
		zos.closeEntry();
		
	}
	
	protected static void writeJDemElevationModels(List<JDemElevationModel> modelList, ZipOutputStream zos) throws IOException
	{

		for (int i = 0; i < modelList.size(); i++) {
			JDemElevationModel model = modelList.get(i);
			writeJDemElevationModel(model, i, zos);
		}
	}
	
	public static void writeProject(ProjectMarshall projectMarshall, String path) throws IOException
	{
		log.info("Writing project file to " + path);
		File file = JDemResourceLoader.getAsFile(path);
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		
		ZipEntry settingsEntry = new ZipEntry("project.json");
		zos.putNextEntry(settingsEntry);
		
		JsonProjectFileWriter.writeProject(projectMarshall, zos);
		zos.closeEntry();
		
		List<JDemElevationModel> modelList = projectMarshall.getElevationModels();
		if (modelList != null && modelList.size() > 0) {
			writeJDemElevationModels(modelList, zos);
		}
		
		if (projectMarshall.getUserScript() != null) {
			ZipEntry scriptEntry = null;
			if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.GROOVY) {
				scriptEntry = new ZipEntry("script.groovy");
			} else if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.JYTHON) {
				scriptEntry = new ZipEntry("script.py");
			} else if (projectMarshall.getScriptLanguage() == ScriptLanguageEnum.SCALA) {
				scriptEntry = new ZipEntry("script.scala");
			}
			
			if (scriptEntry != null) {
				zos.putNextEntry(scriptEntry);
				zos.write(projectMarshall.getUserScript().getBytes());
				zos.closeEntry();
			}
		} else {
			log.info("User script is null, cannot write.");
		}
		
		zos.close();
	}
	
	
}
