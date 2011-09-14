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
					
					
					int start = column + ((topRow + row) * (int)pack.getColumns());
					//int start = leftColumn + ((topRow + row) * totalColumns);
					
					pack.getDataSource().load(floatBuffer, start, columns);
					
					//for (int c = columnsRead; c < columns; c++) {
					//for (int c = column - leftColumn; c < columns; c++) {
					
					lastColumn = columnsRead;
					int testCol = ((column + columns) - halfColumns);
					if (pack.getColumnEnd() < testCol) {
						
						columnsRead += ((leftColumn + columns) - pack.getColumnEnd() - columnsRead + halfColumns);
					} else {
						columnsRead += columns;
					}
					
					for (int c = lastColumn; c < columns; c++) {
						buffer[row][c] = floatBuffer[c];
					}
					
					
					
					//columnsRead += (pack.getColumns() < 100);
				} else {
					
					//for (int c = 0; c < columns; c++) {
					//if (column < columns)
					//	buffer[row][column] = DemConstants.ELEV_NO_DATA;
					columnsRead += 1;
					//}
				}
				
			}
			/*
			int start = leftColumn + ((topRow + row) * totalColumns);
			Arrays.fill(floatBuffer, 0x0);
			
			
			// Not gonna work
			packagedReaderArray[0].getDataSource().load(floatBuffer, start, columns);

			for (int column = 0; column < columns; column++) {
				buffer[row][column] = floatBuffer[column];
			}
			*/
		}
		/*
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				float elevation = super.getElevation(row+topRow, column+leftColumn);
				buffer[row][column] = elevation;
				
			}
		}
		*/
	}
	
}
