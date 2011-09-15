package us.wthr.jdem846.input;

import java.util.Arrays;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class BufferedSubsetDataPackage extends SubsetDataPackage
{
	
	private static Log log = Logging.getLog(BufferedSubsetDataPackage.class);
	
	private int rows = 0;
	private int columns = 0;
	private int topRow = 0;
	private int leftColumn = 0;
	private int totalRows = 0;
	private int totalColumns = 0;
	
	private float[][] buffer = null;
	
	public BufferedSubsetDataPackage(int topRow, int leftColumn, int totalRows, int totalColumns, int rows, int columns)
	{
		super(totalRows, totalColumns);
		this.rows = rows;
		this.columns = columns;
		this.topRow = topRow;
		this.leftColumn = leftColumn;
		this.totalRows = totalRows;
		this.totalColumns = totalColumns;
	}
	
	@Override
	public void precacheData() throws DataSourceException
	{
		buffer = new float[rows][columns];
		fillBuffer();
	}
	
	@Override
	public void unloadData() throws DataSourceException
	{
		buffer = null;
	}
	
	@Override
	public float getElevation(int row, int col)
	{
		if (buffer != null) {
			int bufferRow = row - topRow;
			int bufferColumn = col - leftColumn;
			
			if (bufferRow >= rows || bufferColumn >= columns)
				return DemConstants.ELEV_NO_DATA;
			
			return buffer[bufferRow][bufferColumn];
		} else {
			return super.getElevation(row, col);
		}
	}
	
	
	
	protected void fillBuffer() throws DataSourceException
	{

		int halfColumns = (int) Math.round((double)totalColumns / 2.0);
		int halfRows = (int) Math.round((double)totalRows / 2.0);
		
		
		float[] floatBuffer = new float[columns];
		for (int row = 0; row < rows; row++) {
			Arrays.fill(buffer[row], DemConstants.ELEV_NO_DATA);
			Arrays.fill(floatBuffer, DemConstants.ELEV_NO_DATA);
			
			int columnsRead = 0;
			int lastColumn = 0;
			
			while (columnsRead < columns) {
				int column = leftColumn + columnsRead;
				PackagedReader pack = getByRowAndColumn(topRow + row, column);
				
				
				if (pack != null) {
					float noData = pack.getDataSource().getHeader().getNoData();
					int start = column + ((topRow + row) * (int)pack.getColumns());

					pack.getDataSource().load(floatBuffer, start, columns);
					
					lastColumn = columnsRead;
					int dataEndColumn = pack.getColumnEnd() + halfColumns;
					if (dataEndColumn < leftColumn + columns) {
						columnsRead += (dataEndColumn - leftColumn);
					} else {
						columnsRead += columns;
					}
					

					
					for (int c = lastColumn; c < lastColumn + columnsRead && c < columns; c++) {
						if (lastColumn > 0) {
							int v = 0;
						}
						if (floatBuffer[c] != noData) {
							if (columnsRead > columns) {
								buffer[row][c] = floatBuffer[c - (columnsRead - columns)];
							} else {
								buffer[row][c] = floatBuffer[c];
							}
						} else {
							buffer[row][c] = DemConstants.ELEV_NO_DATA;
						}
					}

				} else {
					columnsRead += 1;
				}
			}

		}
	}
	
}
