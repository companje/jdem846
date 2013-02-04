package us.wthr.jdem846.project;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.project.workspace.project.RenderedElevationModel;

public class ElevationModelReader
{
	private static Log log = Logging.getLog(ElevationModelReader.class);
	
	
	public static BufferedImage loadImage(InputStream in) throws IOException
	{
		BufferedImage image = ImageIO.read(in);
		return image;
	}
	
	public static String loadProperties(InputStream in) throws IOException
	{
		return IOUtils.toString(in);
	}
	
	
	public static ElevationModel loadElevationModelFromZip(ZipFile zipFile, int index) throws IOException
	{
		ZipEntry imageFile = zipFile.getEntry("models/" + index + "/image.png");
		
		if (imageFile == null) {
			imageFile = zipFile.getEntry("models/" + index + "/image.jpg");
		}
		
		if (imageFile == null) {
			return null;
		}
		
		log.info("Loading from " + imageFile.getName() + ". Compressed Size: " + imageFile.getCompressedSize() + ". Size: " + imageFile.getSize());
		InputStream imageInStream = zipFile.getInputStream(imageFile);
		BufferedImage image = loadImage(imageInStream);
		imageInStream.close();


		String jsonPropertiesTxt = null;
		ZipEntry propertiesFile = zipFile.getEntry("models/" + index + "/properties.json");
			if (propertiesFile != null) {
			log.info("Loading from " + propertiesFile.getName() + ". Compressed Size: " + propertiesFile.getCompressedSize() + ". Size: " + propertiesFile.getSize());
			InputStream propertiesInStream = zipFile.getInputStream(propertiesFile);
			jsonPropertiesTxt = loadProperties(propertiesInStream);
			propertiesInStream.close();
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
	
	
	
	public static ElevationModel loadElevationModelFromPath(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		return new RenderedElevationModel(path);
	}
	
	
	
}
