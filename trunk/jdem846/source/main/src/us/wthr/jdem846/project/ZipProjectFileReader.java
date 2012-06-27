package us.wthr.jdem846.project;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ElevationHistogramModel;
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
	
	
	protected static JDemElevationModel loadElevationModelFromZip(int index, ZipFile zipFile) throws IOException
	{
		boolean supportsAlpha = true;
		ZipEntry imageFile = zipFile.getEntry("models/" + index + "/image.png");
		
		
		
		if (imageFile == null) {
			imageFile = zipFile.getEntry("models/" + index + "/image.jpg");
			supportsAlpha = false;
		}
		
		if (imageFile == null) {
			return null;
		}
		
		log.info("Loading from " + imageFile.getName() + ". Compressed Size: " + imageFile.getCompressedSize() + ". Size: " + imageFile.getSize());
		InputStream imageInStream = zipFile.getInputStream(imageFile);
		BufferedImage image = ImageIO.read(imageInStream);
		imageInStream.close();

		
		// Properties JSON
		//String jsonPropertiesTxt = IOUtils.toString( in );
		String jsonPropertiesTxt = null;
		ZipEntry propertiesFile = zipFile.getEntry("models/" + index + "/properties.json");
			if (propertiesFile != null) {
			log.info("Loading from " + propertiesFile.getName() + ". Compressed Size: " + propertiesFile.getCompressedSize() + ". Size: " + propertiesFile.getSize());
			InputStream propertiesInStream = zipFile.getInputStream(propertiesFile);
			jsonPropertiesTxt = IOUtils.toString( propertiesInStream );
		}
		
		
		ZipEntry dataFile = zipFile.getEntry("models/" + index + "/model.dat");
		log.info("Loading from " + dataFile.getName() + ". Compressed Size: " + dataFile.getCompressedSize() + ". Size: " + dataFile.getSize());
		InputStream dataInStream = zipFile.getInputStream(dataFile);
		JDemElevationModel jdemElevationModel = new JDemElevationModel(image, dataInStream, jsonPropertiesTxt);
		dataInStream.close();
		
		
		ZipEntry histogramEntry = zipFile.getEntry("models/" + index + "/elevation-histogram.dat");
		if (histogramEntry != null) {
			log.info("Loading from " + histogramEntry.getName() + ". Compressed Size: " + histogramEntry.getCompressedSize() + ". Size: " + histogramEntry.getSize());
			InputStream histogramInStream = zipFile.getInputStream(histogramEntry);
			ElevationHistogramModel histogramModel = new ElevationHistogramModel(histogramInStream);
			jdemElevationModel.setElevationHistogramModel(histogramModel);
		}
		
		
		
		return jdemElevationModel;
	}
	
	protected static void loadElevationModelsFromZip(ProjectMarshall projectMarshall, ZipFile zipFile) throws IOException
	{
		
		for (int i = 0; i < 256; i++) {
			
			JDemElevationModel jdemElevationModel = loadElevationModelFromZip(i, zipFile);
			if (jdemElevationModel != null) {
				projectMarshall.getElevationModels().add(jdemElevationModel);
			} else {
				break;
			}
			
		}
		
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
		
		loadElevationModelsFromZip(projectMarshall, zipFile);
		
		if (scriptEntry != null) {
			loadScriptFromZip(projectMarshall, zipFile.getInputStream(scriptEntry));
		} else {
			log.info("Script file not found, cannot load");
		}
		
		
		
		
		return projectMarshall;
	}
}
