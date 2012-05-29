package us.wthr.jdem846.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.WatchableModelPoint.ModelPointChangedListener;
import us.wthr.jdem846.util.ByteConversions;
import us.wthr.jdem846.util.TempFiles;

/** A grid that stores the model point data on the hard disk. This is for larger data sets that can easily
 * exceed heap memory. Depending on disk speed, it can be considerably slower.
 * 
 * @author Kevin M. Gill
 *
 */
public class DiskCachedModelGrid extends ModelPointGrid
{
	private static final long MODEL_POINT_SIZE_DOUBLE = 36; // Bytes
	private static final long MODEL_POINT_SIZE_FLOAT = 20; // Float
	
	private static Log log = Logging.getLog(DiskCachedModelGrid.class);
	
	private boolean isDisposed = false;
	
	private File cacheFile;
	private long cacheSize = 0;
	private RandomAccessFile filePointer;
	
	private byte[] buffer4 = new byte[4];
	private byte[] buffer8 = new byte[8];
	
	public DiskCachedModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution) throws Exception
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution);
		
		cacheFile = TempFiles.getTemporaryFile("jdem_grid_");
		
		cacheSize = this.gridLength * MODEL_POINT_SIZE_DOUBLE;
		initializeCache();
	}
	
	protected void initializeCache() throws Exception
	{
		closeFilePointer();
		
		FileOutputStream out = new FileOutputStream(cacheFile);
		
		byte[] writeBuffer = new byte[2048];
		Arrays.fill(writeBuffer, (byte)0x0);

		for (long i = 0; i < cacheSize; i+=2048) {
			out.write(writeBuffer);
		}
		
		out.close();
	}
	
	public void dispose()
	{
		if (isDisposed())
			return;
		
		try {
			closeFilePointer();
		} catch (Exception ex) {
			log.warn("Error closing file pointer: " + ex.getMessage(), ex);
		}
		
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
		
	}
	
	public boolean isDisposed()
	{
		return isDisposed;
	}
	
	public void reset()
	{
		
	}
	
	protected void closeFilePointer() throws Exception
	{
		RandomAccessFile filePointer = getFilePointer(false);
		if (filePointer != null) {
			filePointer.close();
			filePointer = null;
		}
	}
	
	protected RandomAccessFile getFilePointer(boolean open) throws Exception
	{
		if (filePointer == null && open) {
			filePointer = new RandomAccessFile(cacheFile, "rw");
		}
		
		return filePointer;
	}
	
	public ModelPoint get(double latitude, double longitude)
	{
		long index = getIndex(latitude, longitude);
		
		if (/*grid != null && */index >= 0 && index < this.gridLength) {
			try {
				return get(index);
			} catch (Exception ex) {
				// TODO: Add some real error handling here!
				log.error("Error fetching model point at index " + index + ": " + ex.getMessage(), ex);
				return null;
			}
		} else {
			// TODO: Throw
			return null;
		}
	}
	
	public ModelPoint get(long index) throws Exception
	{
		WatchableModelPoint modelPoint = new WatchableModelPoint();
		
		RandomAccessFile filePointer = getFilePointer(true);
		
		long fileIndex = index * MODEL_POINT_SIZE_DOUBLE;
		filePointer.seek(fileIndex);
		
		// Elevation
		filePointer.readFully(buffer8);
		double elevation = ByteConversions.bytesToDouble(buffer8);
		
		// Normal
		filePointer.readFully(buffer8);
		//normal[0] = ByteConversions.bytesToDouble(buffer8);
		
		filePointer.readFully(buffer8);
		//normal[1] = ByteConversions.bytesToDouble(buffer8);
		
		filePointer.readFully(buffer8);
		//normal[2] = ByteConversions.bytesToDouble(buffer8);
		
		filePointer.readFully(buffer4);
		int rgba = ByteConversions.bytesToInt(buffer4);
		
		modelPoint.setElevation(elevation);
		//modelPoint.setNormal(normal);
		modelPoint.setRgba(rgba);
		
		modelPoint.addModelPointChangedListener(new ModelPointChangedHandler(index));
		
		return modelPoint;
	}	
	
	public void set(ModelPoint modelPoint, long index) throws Exception
	{
		RandomAccessFile filePointer = getFilePointer(true);
		
		long fileIndex = index * MODEL_POINT_SIZE_DOUBLE;
		filePointer.seek(fileIndex);
		
		byte[] b = null;
		
		b = ByteConversions.doubleToBytes(modelPoint.getElevation());
		filePointer.write(b);
		
		//modelPoint.getNormal(normal);
		
		//b = ByteConversions.doubleToBytes(normal[0]);
		filePointer.write(b);
		
		//b = ByteConversions.doubleToBytes(normal[1]);
		filePointer.write(b);
		
		//b = ByteConversions.doubleToBytes(normal[2]);
		filePointer.write(b);
		
		b = ByteConversions.intToBytes(modelPoint.getRgba());
		filePointer.write(b);
	}
	
	
	class ModelPointChangedHandler implements ModelPointChangedListener
	{
		long index;

		public ModelPointChangedHandler(long index)
		{
			this.index = index;
		}
		
		public void onModelPointChanged(ModelPoint modelPoint)
		{
			try {
				set(modelPoint, index);
			} catch (Exception ex) {
				log.error("Error writing model point to cache: " + ex.getMessage(), ex);
			}
		}
	}
	
}
