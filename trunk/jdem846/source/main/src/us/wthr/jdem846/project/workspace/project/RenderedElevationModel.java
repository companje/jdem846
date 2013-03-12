package us.wthr.jdem846.project.workspace.project;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.stream.FileImageOutputStream;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.JDemElevationModel;
import us.wthr.jdem846.exception.ProjectParseException;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.project.ElevationModelReader;

public class RenderedElevationModel implements ElevationModel
{
	
	private String modelRootPath;
	private Map<String, String> modelProperties = null;
	
	private ElevationModel elevationModel = null;
	
	private Double mutex = new Double(1.0);
	
	public RenderedElevationModel(String modelRootPath) throws FileNotFoundException, IOException, ProjectParseException
	{
		this.modelRootPath = modelRootPath;
		this.modelProperties = loadElevationModelPropertiesFromPath(new File(modelRootPath));
	}
	
	@Override
	public void load() throws FileNotFoundException, IOException, ProjectParseException
	{
		synchronized(mutex) {
			if(isLoaded()) {
				return;
			}
			
			elevationModel = loadElevationModelFromPath(modelRootPath);
			
		}
	}
	
	@Override
	public void unload()
	{
		synchronized(mutex) {
			if (isLoaded()) {
				elevationModel.dispose();
				elevationModel = null;
			}
		}
	}
	
	@Override
	public boolean isLoaded()
	{
		return (elevationModel != null);
	}
	
	@Override
	public void dispose()
	{
		unload();
	}

	@Override
	public void reset()
	{
		elevationModel.reset();
	}

	@Override
	public boolean hasProperty(String key)
	{
		if (isLoaded()) {
			return elevationModel.hasProperty(key);
		} else {
			return modelProperties.containsKey(key);
		}
	}

	@Override
	public void setProperty(String key, String value)
	{
		if (isLoaded()) {
			elevationModel.setProperty(key, value);
		}
	}

	@Override
	public String getProperty(String key)
	{
		if (isLoaded()) {
			return elevationModel.getProperty(key);
		} else {
			return modelProperties.get(key);
		}
	}

	@Override
	public Map<String, String> getProperties()
	{
		if (isLoaded()) {
			return elevationModel.getProperties();
		} else {
			return modelProperties;
		}
	}

	@Override
	public int getRgba(double x, double y)
	{
		return elevationModel.getRgba(x, y);
	}

	@Override
	public void getRgba(double x, double y, int[] fill)
	{
		elevationModel.getRgba(x, y, fill);
	}

	@Override
	public double getLatitude(double x, double y)
	{
		return elevationModel.getLatitude(x, y);
	}

	@Override
	public double getLongitude(double x, double y)
	{
		return elevationModel.getLongitude(x, y);
	}

	@Override
	public double getElevation(double x, double y)
	{
		return elevationModel.getElevation(x, y);
	}

	@Override
	public boolean getMask(double x, double y)
	{
		return elevationModel.getMask(x, y);
	}

	@Override
	public BufferedImage getImage()
	{
		return elevationModel.getImage();
	}

	@Override
	public ElevationHistogramModel getElevationHistogramModel()
	{
		return elevationModel.getElevationHistogramModel();
	}

	@Override
	public int getWidth()
	{
		return elevationModel.getWidth();
	}

	@Override
	public int getHeight()
	{
		return elevationModel.getHeight();
	}

	@Override
	public void setElevationHistogramModel(ElevationHistogramModel elevationHistogramModel)
	{
		elevationModel.setElevationHistogramModel(elevationHistogramModel);
	}

	@Override
	public void writeImageData(OutputStream zos, ImageTypeEnum imageTypeFromFormatName) throws IOException
	{
		if (isLoaded()) {
			elevationModel.writeImageData(zos, imageTypeFromFormatName);
		}
	}
	
	@Override
	public void writeImageData(FileImageOutputStream os, ImageTypeEnum imageTypeFromFormatName) throws IOException
	{
		if (isLoaded()) {
			elevationModel.writeImageData(os, imageTypeFromFormatName);
		}
	}
	
	@Override
	public void writeModelData(OutputStream zos) throws IOException
	{
/*		if (!isLoaded()) {
			try {
				load();
			} catch (ProjectParseException e) {
				throw new IOException("Error writing image data: " + e.getMessage(), e);
			}
		}*/
		
		if (isLoaded()) {
			elevationModel.writeModelData(zos);
		}
	}

	@Override
	public void writeProperties(OutputStream zos) throws IOException
	{
		
		if (!isLoaded()) {
			try {
				load();
			} catch (ProjectParseException e) {
				throw new IOException("Error writing image data: " + e.getMessage(), e);
			}
		}
		
		if (isLoaded()) {
			elevationModel.writeProperties(zos);
		}
	}
	
	
	
	public static Map<String, String> loadElevationModelPropertiesFromPath(File base) throws IOException, FileNotFoundException, ProjectParseException
	{
		File propertiesFile = new File(base, "properties.json");
		InputStream propertiesInputStream = new BufferedInputStream(new FileInputStream(propertiesFile));
		String jsonPropertiesTxt = ElevationModelReader.loadProperties(propertiesInputStream);
		propertiesInputStream.close();
		
		return JDemElevationModel.readProperties(jsonPropertiesTxt);

	}
	
	public static ElevationModel loadElevationModelFromPath(String path) throws IOException, FileNotFoundException, ProjectParseException
	{
		File base = new File(path);
		if (!base.isDirectory()) {
			throw new ProjectParseException("Specified path is not a directory");
		}
		
		ElevationModel elevationModel = null;
		
		
		File imageFileJpeg = new File(base, "image.jpg");
		File imageFilePng = new File(base,  "image.png");
		
		File imageFile = (imageFilePng.exists()) ? imageFilePng : imageFileJpeg;
		if (!imageFile.exists()) {
			throw new ProjectParseException("Model image not found");
		}
		
		InputStream imageInputStream = new BufferedInputStream(new FileInputStream(imageFile));
		BufferedImage image = ElevationModelReader.loadImage(imageInputStream);
		imageInputStream.close();
		
		
		File propertiesFile = new File(base, "properties.json");
		InputStream propertiesInputStream = new BufferedInputStream(new FileInputStream(propertiesFile));
		String jsonPropertiesTxt = ElevationModelReader.loadProperties(propertiesInputStream);
		propertiesInputStream.close();
		
		
		File dataInputFile = new File(base, "model.dat");
		if (dataInputFile.exists()) {
			InputStream dataInputStream = new BufferedInputStream(new FileInputStream(dataInputFile));
			elevationModel = new JDemElevationModel(image, dataInputStream, jsonPropertiesTxt);
			dataInputStream.close();
		} else {
			elevationModel = new JDemElevationModel(image, jsonPropertiesTxt);
		}
		
		
		
		File histogramFile = new File(base, "elevation-histogram.dat");
		if (histogramFile.exists()) {
			InputStream histogramInputStream = new BufferedInputStream(new FileInputStream(histogramFile));
			ElevationHistogramModel histogramModel = new ElevationHistogramModel(histogramInputStream);
			elevationModel.setElevationHistogramModel(histogramModel);
			histogramInputStream.close();
		}

		return elevationModel;
	}
	


}
