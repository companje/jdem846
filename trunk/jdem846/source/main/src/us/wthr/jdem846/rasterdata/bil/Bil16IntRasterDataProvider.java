package us.wthr.jdem846.rasterdata.bil;

import java.io.File;

import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.generic.GenericRasterDataProvider;
import us.wthr.jdem846.rasterdata.generic.RasterDefinition;

public class Bil16IntRasterDataProvider extends GenericRasterDataProvider
{
	private static Log log = Logging.getLog(Bil16IntRasterDataProvider.class);

	/*
	 * private GridFloatHeader header; private CachingGridFloatDataReader
	 * dataReader;
	 */

	private Bil16IntHeader header;
	private File dataFile;

	public Bil16IntRasterDataProvider()
	{

	}

	@Override
	public void create(String filePath) throws DataSourceException
	{
		dataFile = new File(filePath);
		if (!dataFile.exists()) {
			throw new DataSourceException("BIL16INT data file not found at " + filePath);
		}

		String headerFilePath = filePath.replace(".bil", ".hdr");
		header = new Bil16IntHeader(headerFilePath);

		RasterDefinition rasterDefinition = new RasterDefinition();
		rasterDefinition.setDataType(DataTypeEnum.Int16);
		rasterDefinition.setByteOrder(header.getByteOrder());
		rasterDefinition.setImageHeight(header.getRows());
		rasterDefinition.setImageWidth(header.getColumns());
		rasterDefinition.setSouth(header.getyLowerLeft());
		rasterDefinition.setWest(header.getxLowerLeft());
		rasterDefinition.setLatitudeResolution(header.getYdim());
		rasterDefinition.setLongitudeResolution(header.getXdim());
		rasterDefinition.setNoData(header.getNoData());

		rasterDefinition.determineNorth();
		rasterDefinition.determineEast();

		rasterDefinition.setLocked(true);

		super.create(filePath, rasterDefinition);

		log.info("Created BIL16INT raster data provider...");
		log.info("    North/South: " + getNorth() + "/" + getSouth());
		log.info("    East/West: " + getEast() + "/" + getWest());
		log.info("    Rows/Columns: " + getRows() + "/" + getColumns());
		log.info("    Latitude Resolution: " + getLatitudeResolution());
		log.info("    Longitude Resolution: " + getLongitudeResolution());
	}

}
