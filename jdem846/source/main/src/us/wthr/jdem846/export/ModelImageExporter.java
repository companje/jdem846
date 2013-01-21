package us.wthr.jdem846.export;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import us.wthr.jdem846.ElevationModel;
import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.project.ProjectFiles;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.ProjectMarshaller;
import us.wthr.jdem846.project.ProjectTypeEnum;
import us.wthr.jdem846.ui.SharedStatusBar;

public class ModelImageExporter
{
	private static Log log = Logging.getLog(ModelImageExporter.class);
	
	public enum ImageTypeEnum {
		JPEG,
		PNG,
		JDEMIMG,
		UNSUPPORTED
	}
	
	private ElevationModel elevationModel;
	private String path;
	private ImageTypeEnum type;
	
	
	private List<ExportCompletedListener> exportCompletedListeners = new LinkedList<ExportCompletedListener>();
	
	public ModelImageExporter(ElevationModel elevationModel, String path)
	{
		this(elevationModel, path, ImageTypeEnum.PNG, null);
	}
	
	public ModelImageExporter(ElevationModel elevationModel, String path, ImageTypeEnum type)
	{
		this(elevationModel, path, type, null);
	}
	
	public ModelImageExporter(ElevationModel elevationModel, String path, ImageTypeEnum type, ExportCompletedListener exportCompletedListener)
	{
		this.elevationModel = elevationModel;
		this.path = path;
		this.type = type;
		
		if (exportCompletedListener != null) {
			this.exportCompletedListeners.add(exportCompletedListener);
		}
	}
	
	public void save()
	{
		try {
			validateSavePath();
		} catch(Exception ex) {
			log.error("Error checking filename", ex);
			this.fireExportFailedListeners(ex);
			return;
		}
		log.info("Saving image to " + path);
		
		
		if (type == ImageTypeEnum.JDEMIMG) {
			saveModelImage();
		} else if (type == ImageTypeEnum.JPEG || type == ImageTypeEnum.PNG) {
			saveBasicImage();
		}
		
	}
	
	protected void saveModelImage()
	{
		try {
			ProjectMarshall projectMarshall = ProjectMarshaller.marshallProject(null);
			
			projectMarshall.setProjectType(ProjectTypeEnum.DEM_IMAGE);
			projectMarshall.getElevationModels().add(elevationModel);
			
			ProjectFiles.write(projectMarshall, path);
			
			this.fireExportSuccessfulListeners();
		} catch (Exception ex) {
			log.error("Failed to write image to disk: " + ex.getMessage(), ex);
			this.fireExportFailedListeners(ex);
		}
		

		log.info("Project file saved to " + path);
	}
	
	protected void saveBasicImage()
	{
		String formatName = getFormatType(type);

		File writeFile = new File(path);
		try {
			ImageIO.write((BufferedImage)elevationModel.getImage(), formatName, writeFile);
			SharedStatusBar.setStatus("Image exported to " + writeFile);
			this.fireExportSuccessfulListeners();
		} catch (IOException e) {
			log.error("Failed to write image to disk: " + e.getMessage(), e);
			this.fireExportFailedListeners(e);
		} 
	}
	
	protected void validateSavePath() throws IllegalArgumentException, InvalidFileFormatException
	{
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");
		
		String extension = null;
		if (path.lastIndexOf(".") >= 0) {
			extension = path.substring(path.lastIndexOf(".") + 1);
			this.type = getFormatTypeConstant(extension);
			if (!isSupportedExtension(extension))
				throw new InvalidFileFormatException(extension);
		} else {
			extension = getTypeExtension(type);
			if (extension == null) {
				throw new InvalidFileFormatException("null");
			}
			path = path + "." + extension;
		}
		
	}
	
	public static String getTypeExtension(ImageTypeEnum type)
	{
		if (type == ImageTypeEnum.PNG) {
			return "png";
		} else if (type == ImageTypeEnum.JPEG) {
			return "jpg";
		} else if (type == ImageTypeEnum.JDEMIMG) {
			return "jdemimg";
		} else {
			return null;
		}
	}
	
	public static String getFormatType(ImageTypeEnum type)
	{
		if (type == ImageTypeEnum.PNG) {
			return "PNG";
		} else if (type == ImageTypeEnum.JPEG) {
			return "JPEG";
		} else {
			return null;
		}
		

	}
	
	public static ImageTypeEnum getFormatTypeConstant(String extension)
	{
		if (extension == null)
			return ImageTypeEnum.UNSUPPORTED;
		
		if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
			return ImageTypeEnum.JPEG;
		
		if (extension.equalsIgnoreCase("png"))
			return ImageTypeEnum.PNG;
		
		if (extension.equalsIgnoreCase("jdemimg")) 
			return ImageTypeEnum.JDEMIMG;
		
		return ImageTypeEnum.UNSUPPORTED;
	}
	
	public static String getFormatType(String extension)
	{
		if (extension == null)
			return null;
		
		if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
			return getFormatType(ImageTypeEnum.JPEG);
		
		if (extension.equalsIgnoreCase("png"))
			return getFormatType(ImageTypeEnum.PNG);
		
		if (extension.equalsIgnoreCase("jdemimg"))
			return getFormatType(ImageTypeEnum.JDEMIMG);
		
		return null;
	}
	
	public static boolean isSupportedExtension(String extension)
	{
		if (extension == null)
			return false;
		
		if (extension.equalsIgnoreCase("png"))
			return true;
		if (extension.equalsIgnoreCase("jpg"))
			return true;
		if (extension.equalsIgnoreCase("jpeg"))
			return true;
		if (extension.equalsIgnoreCase("jdemimg"))
			return true;
		
		
		return false;
	}
	
	
	
	public void addExportCompletedListener(ExportCompletedListener listener)
	{
		this.exportCompletedListeners.add(listener);
	}
	
	public boolean removeExportCompletedListener(ExportCompletedListener listener)
	{
		return this.exportCompletedListeners.remove(listener);
	}
	
	protected void fireExportSuccessfulListeners()
	{
		for (ExportCompletedListener listener : this.exportCompletedListeners) {
			listener.onSaveSuccessful();
		}
	}
	
	protected void fireExportFailedListeners(Exception ex)
	{
		for (ExportCompletedListener listener : this.exportCompletedListeners) {
			listener.onSaveFailed(ex);
		}
	}
	
	
	
	public static void exportModelImage(ElevationModel elevationModel, String path)
	{
		exportModelImage(elevationModel, path, ImageTypeEnum.PNG, null);
	}
	
	public static void exportModelImage(ElevationModel elevationModel, String path, ImageTypeEnum defaultType)
	{
		exportModelImage(elevationModel, path, defaultType, null);
	}
	
	public static void exportModelImage(ElevationModel elevationModel, String path, ExportCompletedListener exportCompletedListener)
	{
		exportModelImage(elevationModel, path, ImageTypeEnum.PNG, exportCompletedListener);
	}
	
	public static void exportModelImage(ElevationModel elevationModel, String path, ImageTypeEnum defaultType, ExportCompletedListener exportCompletedListener)
	{
		ModelImageExporter exporter = new ModelImageExporter(elevationModel, path, defaultType, exportCompletedListener);
		exporter.save();
	}
	
}
