package us.wthr.jdem846.input;

import java.util.List;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;

/** Collection of a subset of data sources. Allows for data sources to be matched faster when a subset
 * is gathered for an individual tile.
 * 
 * @author Kevin M. Gill
 *
 * @see DataPackage
 * @see DataSource
 * @see PackagedReader
 */
public class SubsetDataPackage 
{
	
	// Done as a static array to avoid List/Collection traversal overhead when processing data
	protected PackagedReader[] packagedReaderArray;
	

	private int halfRows;
	private int halfColumns;
	
	public SubsetDataPackage(int rows, int columns)
	{

		this.halfColumns = (int) Math.round((double)columns / 2.0);
		this.halfRows = (int) Math.round((double)rows / 2.0);
	}

	public void setPackagedReaders(List<PackagedReader> packagedReaders)
	{
		packagedReaderArray = new PackagedReader[packagedReaders.size()];
		packagedReaders.toArray(packagedReaderArray);
	}
	
	public int getDataSourceCount()
	{
		return packagedReaderArray.length;
	}
	
	public boolean containsData()
	{
		return (getDataSourceCount() > 0);
	}
	
	
	protected PackagedReader getByRowAndColumn(int row, int col)
	{
		int testCol = (col - halfColumns);
		int testRow = (row - halfRows);

		
		int length = packagedReaderArray.length;
		for (int i = 0; i < length; i++) {
			PackagedReader pack = packagedReaderArray[i];
			if (pack.getColumnStart() <= testCol && pack.getColumnEnd() > testCol && pack.getRowStart() <= testRow && pack.getRowEnd() >= testRow) {
				return pack;
			}
		}
		return null;
	}
	
	public float getElevation(int row, int col)
	{
		PackagedReader pack = getByRowAndColumn(row, col);
		if (pack != null) {
			return pack.getElevation(row - halfRows, col - halfColumns);
		} else {
			return DemConstants.ELEV_NO_DATA;
		}
		
		/*
		int testCol = (col - halfColumns);
		int testRow = (row - halfRows);

		
		int length = packagedReaderArray.length;
		for (int i = 0; i < length; i++) {
			PackagedReader pack = packagedReaderArray[i];
			if (pack.getColumnStart() <= testCol && pack.getColumnEnd() > testCol && pack.getRowStart() <= testRow && pack.getRowEnd() >= testRow) {
				return pack.getElevation(testRow, testCol);
			}
		}
		
		return DemConstants.ELEV_NO_DATA;
		*/
		
	}
	
	
	public void precacheData() throws DataSourceException
	{
		for (int i = 0; i < packagedReaderArray.length; i++) {
			packagedReaderArray[i].setDataPrecached(true);
		}
	}
	
	public void unloadData() throws DataSourceException
	{
		for (int i = 0; i < packagedReaderArray.length; i++) {
			packagedReaderArray[i].setDataPrecached(false);
		}
	}
	
	/*
	public void fillBuffer(float[][] buffer, int topRow, int leftColumn, int rows, int columns)
	{
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				float elevation = getElevation(row+topRow, column+leftColumn);
				buffer[row][column] = elevation;
				
			}
		}
		
	}
	*/
}
