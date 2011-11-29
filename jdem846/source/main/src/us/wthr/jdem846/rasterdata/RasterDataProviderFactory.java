package us.wthr.jdem846.rasterdata;

import java.io.File;

import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class RasterDataProviderFactory
{
	
	private static Log log = Logging.getLog(RasterDataProviderFactory.class);
	
	protected RasterDataProviderFactory()
	{
		
	}
	
	
	public static RasterData loadRasterData(String filePath) throws DataSourceException
	{
		
		String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
		
		if (extension == null) {
			throw new DataSourceException("Cannot determine file type: no extension");
		}
		
		RasterDataFormatEnum useFormatType = null;
		
		for (RasterDataFormatEnum formatType : RasterDataFormatEnum.values()) {
			if (formatType.extension().equalsIgnoreCase(extension)) {
				useFormatType = formatType;
				break;
			}
		}
		
		if (useFormatType == null) {
			throw new DataSourceException("Cannot determine file type: unrecognized extension: '" + extension + "'");
		}
		
		return loadRasterData(useFormatType, filePath);
	}
	
	public static RasterData loadRasterData(RasterDataFormatEnum formatType, String filePath) throws DataSourceException
	{
		
		if (formatType == null) {
			throw new DataSourceException("Cannot load data: Format type is null.");
		}
		
		log.info("Loading raster data of type '" + formatType.formatName() + "' from file path '" + filePath + "'");

		File dataFile = new File(filePath);
		if (!dataFile.exists() || !dataFile.canRead()) {
			throw new DataSourceException("Data file '" + filePath + "' cannot be found or cannot be read");
		}
		
		RasterData rasterData = null;
		try {
			rasterData = (RasterData) formatType.provider().newInstance();
		} catch (Exception ex) {
			throw new DataSourceException("Failed to create instance of data provider class '" + formatType.provider().getName() + "': " + ex.getMessage(), ex);
		}
		
		rasterData.create(filePath);
		
		return rasterData;
	}
	
}
