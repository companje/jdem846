package us.wthr.jdem846.rasterdata.generic;

import java.io.File;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.rasterdata.AbstractRasterDataProvider;
import us.wthr.jdem846.rasterdata.RasterData;
import us.wthr.jdem846.rasterdata.RasterDataLatLongBox;

public class GenericRasterDataProvider extends AbstractRasterDataProvider
{
	private static Log log = Logging.getLog(GenericRasterDataProvider.class);

	private IRasterDefinition rasterDefinition;

	private File dataFile;

	private boolean isDisposed = false;

	private CachingGenericRasterDataReader dataReader;

	public GenericRasterDataProvider()
	{
		setRasterDefinition(new RasterDefinition());
	}

	@Override
	public void create(String file) throws DataSourceException
	{
		create(file, new RasterDefinition());
	}

	public void create(String file, IRasterDefinition rasterDefinition) throws DataSourceException
	{
		dataFile = new File(file);
		setRasterDefinition(rasterDefinition);
		refreshDefinitionData();
		this.dataReader = new CachingGenericRasterDataReader(dataFile, this.rasterDefinition);
	}

	@Override
	public void dispose() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Raster data provider already disposed.");
		}

		if (!dataReader.isDisposed()) {
			dataReader.dispose();
		}

		isDisposed = true;
	}

	@Override
	public boolean isDisposed()
	{
		return isDisposed;
	}

	@Override
	public RasterData copy() throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Cannot copy object: already disposed");
		}

		GenericRasterDataProvider clone = new GenericRasterDataProvider();
		clone.create(getFilePath(), getRasterDefinition().copy());

		return clone;
	}

	@Override
	public String getFilePath()
	{
		return dataFile.getAbsolutePath();
	}

	@Override
	public double getData(int row, int column) throws DataSourceException
	{
		if (isDisposed()) {
			throw new DataSourceException("Data raster provider has been disposed.");
		}

		if (row >= this.getRows()) {
			return DemConstants.ELEV_NO_DATA;
			// throw new
			// DataSourceException("Specified row exceeds data limits: " + row);
		}

		if (column >= this.getColumns()) {
			return DemConstants.ELEV_NO_DATA;
			// throw new
			// DataSourceException("Specified column exceeds data limits: " +
			// column);
		}

		double data = dataReader.get(row, column);

		if (data == this.rasterDefinition.getNoData()) {
			data = DemConstants.ELEV_NO_DATA;
		}

		return data;
	}

	@Override
	public boolean fillBuffer(double north, double south, double east, double west) throws DataSourceException
	{
		RasterDataLatLongBox bufferBox = new RasterDataLatLongBox(north, south, east, west);
		if (!this.intersects(bufferBox)) {
			return false;
		}

		// Adjust the range to fit what this data supports

		// TODO: Too simplistic
		if (north > this.getNorth())
			north = this.getNorth();
		if (south < this.getSouth())
			south = this.getSouth();
		if (east > this.getEast())
			east = this.getEast();
		if (west < this.getWest())
			west = this.getWest();

		// TODO: Too simplistic
		int x = (int) Math.floor(this.longitudeToColumn(west));
		int y = (int) Math.floor(this.latitudeToRow(north));

		int x2 = (int) Math.ceil(this.longitudeToColumn(east));
		int y2 = (int) Math.ceil(this.latitudeToRow(south));

		// We add 2 columns & 2 rows to support data point interpolation (done
		// in AbstractRasterDataProvider)
		int columns = x2 - x + 2;
		int rows = y2 - y + 2;

		if (columns <= 0 || rows <= 0) {
			return false;
		}

		if (x + columns > rasterDefinition.getImageWidth()) {
			columns = rasterDefinition.getImageWidth() - x;
		}

		if (y + rows > rasterDefinition.getImageHeight()) {
			rows = rasterDefinition.getImageHeight() - y;
		}

		log.info("Filling raster buffer...");
		boolean status;
		try {
			if ((status = dataReader.fillBuffer(x, y, columns, rows))) {
				log.info("Filled raster buffer");
			} else {
				log.info("Raster buffer not filled.");
			}
		} catch (Exception ex) {
			throw new DataSourceException("Error attempting to cache more data than actually exists: " + ex.getMessage(), ex);
		}
		return status;
	}

	@Override
	public boolean isBufferFilled()
	{
		return dataReader.isBufferFilled();
	}

	@Override
	public void clearBuffer() throws DataSourceException
	{
		log.info("Clearing Buffer!");
		dataReader.clearBuffer();
	}

	public IRasterDefinition getRasterDefinition()
	{
		return rasterDefinition;
	}

	public void setRasterDefinition(IRasterDefinition rasterDefinition)
	{
		this.rasterDefinition = rasterDefinition;
		if (this.rasterDefinition != null) {
			this.rasterDefinition.addDefinitionChangeListener(new DefinitionChangeListener()
			{

				@Override
				public void onDefinitionChanged(IRasterDefinition rasterDefinition)
				{
					refreshDefinitionData();
				}

			});
		}

	}

	protected void refreshDefinitionData()
	{
		if (this.rasterDefinition == null) {
			return;
		}

		this.setColumns(this.rasterDefinition.getImageWidth());
		this.setRows(this.rasterDefinition.getImageHeight());

		this.setNorth(this.rasterDefinition.getNorth());
		this.setSouth(this.rasterDefinition.getSouth());
		this.setEast(this.rasterDefinition.getEast());
		this.setWest(this.rasterDefinition.getWest());

		this.setLatitudeResolution(this.rasterDefinition.getLatitudeResolution());
		this.setLongitudeResolution(this.rasterDefinition.getLongitudeResolution());

	}

}
