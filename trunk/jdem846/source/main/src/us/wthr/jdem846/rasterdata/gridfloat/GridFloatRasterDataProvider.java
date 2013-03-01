package us.wthr.jdem846.rasterdata.gridfloat;

import java.io.File;

import us.wthr.jdem846.DataTypeEnum;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.generic.GenericRasterDataProvider;
import us.wthr.jdem846.rasterdata.generic.IRasterDefinition;
import us.wthr.jdem846.rasterdata.generic.RasterDefinition;

public class GridFloatRasterDataProvider extends GenericRasterDataProvider
{
	private static Log log = Logging.getLog(GridFloatRasterDataProvider.class);
	
	private GridFloatHeader header;
	private File dataFile;

	
	public GridFloatRasterDataProvider()
	{
		
	}


	@Override
	public void create(String filePath, IRasterDefinition rasterDefinition) throws DataSourceException
	{
		dataFile = new File(filePath);
		if (!dataFile.exists()) {
			throw new DataSourceException("GridFloat data file not found at " + filePath);
		}
		
		String headerFilePath = filePath.replace(".flt", ".hdr");
		header = new GridFloatHeader(headerFilePath);
		
		// Override the provided raster data. Believe the header file...
		rasterDefinition = new RasterDefinition();
		rasterDefinition.setDataType(DataTypeEnum.Float32);
		rasterDefinition.setByteOrder(header.getByteOrder());
		rasterDefinition.setImageHeight(header.getRows());
		rasterDefinition.setImageWidth(header.getColumns());
		rasterDefinition.setSouth(header.getyLowerLeft());
		rasterDefinition.setWest(header.getxLowerLeft());
		rasterDefinition.setLatitudeResolution(header.getCellSize());
		rasterDefinition.setLongitudeResolution(header.getCellSize());
		rasterDefinition.setNoData(header.getNoData());
		
		rasterDefinition.determineNorth();
		rasterDefinition.determineEast();
		
		rasterDefinition.setLocked(true);
		
		super.create(filePath, rasterDefinition);
		
		log.info("Created GridFloat raster data provider...");
		log.info("    North/South: " + getNorth() + "/" + getSouth());
		log.info("    East/West: " + getEast() + "/" + getWest());
		log.info("    Rows/Columns: " + getRows() + "/" + getColumns());
		log.info("    Latitude Resolution: " + getLatitudeResolution());
		log.info("    Longitude Resolution: " + getLongitudeResolution());
	}

}
