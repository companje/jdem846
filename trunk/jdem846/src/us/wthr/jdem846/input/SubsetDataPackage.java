package us.wthr.jdem846.input;

import java.util.List;

import us.wthr.jdem846.DemConstants;

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
	private PackagedReader[] packagedReaderArray;
	

	private int halfRows;
	private int halfColumns;
	
	public SubsetDataPackage(float rows, float columns)
	{

		this.halfColumns = (int) Math.round(columns / 2.0f);
		this.halfRows = (int) Math.round(rows / 2.0f);
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
	
	public float getElevation(int row, int col)
	{
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
		
	}
}
