package us.wthr.jdem846.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import us.wthr.jdem846.DemConstants;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.BufferedModelGrid.ModelPointChangedHandler;
import us.wthr.jdem846.model.WatchableModelPoint.ModelPointChangedListener;
import us.wthr.jdem846.rasterdata.RasterDataContext;
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
	private static final int MODEL_POINT_SIZE_FLOAT = 8; // Float + Int
	
	private static Log log = Logging.getLog(DiskCachedModelGrid.class);
	
	private boolean isDisposed = false;
	
	private File cacheFile;
	private long cacheSize = 0;
	private RandomAccessFile filePointer;
	
	
	private int bufferMarginRows = 20;
	
	private int bufferRows = 500;
	private int bufferColumns = -1;
	private int bufferSize = -1;
	private double bufferNorth = DemConstants.ELEV_UNDETERMINED;
	private double bufferSouth = DemConstants.ELEV_UNDETERMINED;
	private float[] elevationGrid;
	private int[] rgbaGrid;
	private byte[] readBuffer;
	
	private byte[] buffer4 = new byte[4];
	private byte[] buffer8 = new byte[8];

	private boolean dirty = false;


	
	public DiskCachedModelGrid(double north, double south, double east, double west, double latitudeResolution, double longitudeResolution, double minimum, double maximum) throws Exception
	{
		super(north, south, east, west, latitudeResolution, longitudeResolution, minimum, maximum);

		bufferColumns = (int) MathExt.ceil((east - west) / longitudeResolution);
		bufferSize = (int) MathExt.ceil(bufferColumns * (bufferRows + 5));
		log.info("Creating model point buffer of size " + bufferSize);
		elevationGrid = new float[bufferSize];
		rgbaGrid = new int[bufferSize];
		
		readBuffer = new byte[bufferSize * MODEL_POINT_SIZE_FLOAT];
		
		cacheFile = TempFiles.getTemporaryFile("jdem_grid_");
		log.info("Creating cache file at " + cacheFile.getAbsolutePath());
		
		cacheSize = (long)this.gridLength * (long)MODEL_POINT_SIZE_FLOAT;
		initializeCache();

	}
	
	
	protected void initializeCache() throws Exception
	{
		closeFilePointer();
		log.info("Initializing cache file to " + cacheSize + " bytes");
		
		FileOutputStream out = new FileOutputStream(cacheFile);
		
		byte[] writeBuffer = new byte[1048576];

		byte[] buffer4 = new byte[4];
		ByteConversions.floatToBytes((float)DemConstants.ELEV_UNDETERMINED, buffer4);
		
		for (int i = 0; i < 1048576; i+=8) {
			writeBuffer[i] = buffer4[0];
			writeBuffer[i+1] = buffer4[1];
			writeBuffer[i+2] = buffer4[2];
			writeBuffer[i+3] = buffer4[3];
			writeBuffer[i+4] = 0;
			writeBuffer[i+5] = 0;
			writeBuffer[i+6] = 0;
			writeBuffer[i+7] = 0;
		}
		


		for (long i = 0; i < cacheSize + 1048576; i+=1048576) {
			out.write(writeBuffer);
		}
		
		out.flush();
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
	
	@Override
	public int[] getModelTexture()
	{
		return null;
		// TODO: Complete getModelTexture() for DiskCachedModelGrid
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
	
	
	protected void fillBuffer(double north) throws Exception
	{
		bufferNorth = MathExt.min(north, this.north);
		bufferSouth = bufferNorth - (this.bufferRows * this.latitudeResolution);
		if (bufferSouth < this.south) {
			bufferSouth = this.south;
		}
		
		RandomAccessFile raf = getFilePointer(true);
		
		long index = getIndexL(this.north, bufferNorth, west);
		
		long seek = index * (long)MODEL_POINT_SIZE_FLOAT;
		
		if (seek < 0) {
			log.warn("Negative seek offset for read: " + seek + " for north: " + north + ", buffer north: " + bufferNorth + ", west: " + west + ", index: " + index);
			throw new IOException("Negative seek offset: " + seek);
		}
		
		try {
			raf.seek(seek);
		} catch (Exception ex) {
			log.warn("Error seeking in file: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		long readPoints = (long) MathExt.round(((bufferNorth - bufferSouth + this.latitudeResolution) / this.latitudeResolution) * this.bufferColumns);
		
		long readLength = (readPoints * (long)MODEL_POINT_SIZE_FLOAT);
		
		
		if (readLength > readBuffer.length)
			readLength = readBuffer.length;
		
		log.info("Reading " + readLength + " bytes for " + readPoints + " points at position " + seek + " into grid from " + bufferNorth + " to " + bufferSouth);
		
		Arrays.fill(readBuffer, (byte)0x0);
		
		try {
			raf.readFully(readBuffer, 0, (int) readLength);
		} catch (Exception ex) {
			log.warn("Error reading from file: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		for (int i = 0; i < readPoints; i++) {
			
			int idx = i * 8;
			
			float elevation = ByteConversions.bytesToFloat(readBuffer[idx], readBuffer[idx+1], readBuffer[idx+2], readBuffer[idx+3], ByteConversions.DEFAULT_BYTE_ORDER);
			int rgba = ByteConversions.bytesToInt(readBuffer[idx+4], readBuffer[idx+5], readBuffer[idx+6], readBuffer[idx+7], ByteConversions.DEFAULT_BYTE_ORDER);
			
			elevationGrid[i] = elevation;
			rgbaGrid[i] = rgba;
			
			
		}
		
		this.dirty = false;
		log.info("Done read");
	}
	
	protected void writeBuffer() throws Exception
	{
		if (!isBufferFilled()) {
			return;
		}
		
		RandomAccessFile raf = getFilePointer(true);
		
		long index = getIndexL(this.north, bufferNorth, west);
		
		long seek = index * (long)MODEL_POINT_SIZE_FLOAT;
		
		if (seek < 0) {
			log.warn("Negative seek offset for write: " + seek + " for north: " + this.north + ", buffer north: " + bufferNorth + ", west: " + west + ", index: " + index);
			throw new IOException("Negative seek offset: " + seek);
		}
		
		
		try {
			raf.seek(seek);
		} catch (Exception ex) {
			log.info("Error seeking to position " + seek);
			ex.printStackTrace();
			throw ex;
		}
		
		long writePoints = (long) MathExt.round(((bufferNorth - bufferSouth + this.latitudeResolution) / this.latitudeResolution) * this.bufferColumns);
		
		long writeLength = (writePoints * (long)MODEL_POINT_SIZE_FLOAT);
		//log.info("Writing " + writeLength + " into model grid disk cache");
		
		if (writeLength > readBuffer.length)
			writeLength = readBuffer.length;
		
		
		log.info("Writing " + writeLength + " bytes for " + writePoints + " points at position " + seek + " from grid from " + bufferNorth + " to " + bufferSouth);
		
		Arrays.fill(readBuffer, (byte)0x0);
		
		for (int i = 0; i < writePoints; i++) {
			
			int idx = i * 8;
			
			ByteConversions.floatToBytes(elevationGrid[i], buffer4, ByteConversions.DEFAULT_BYTE_ORDER);
			readBuffer[idx] = buffer4[0];
			readBuffer[idx+1] = buffer4[1];
			readBuffer[idx+2] = buffer4[2];
			readBuffer[idx+3] = buffer4[3];
			//raf.write(buffer4, 0, 4);
			
			ByteConversions.intToBytes(rgbaGrid[i], buffer4, ByteConversions.DEFAULT_BYTE_ORDER);
			readBuffer[idx+4] = buffer4[0];
			readBuffer[idx+5] = buffer4[1];
			readBuffer[idx+6] = buffer4[2];
			readBuffer[idx+7] = buffer4[3];
			//raf.write(buffer4, 0, 4);
		}
		
		
		try {
			//raf.read(readBuffer, 0, (int) writeLength);
			raf.write(readBuffer, 0, (int)writeLength);
		} catch (Exception ex) {
			log.warn("Error writing file: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		log.info("Done write");
	}

	protected boolean isBufferFilled()
	{
		return this.bufferNorth != DemConstants.ELEV_UNDETERMINED;
	}
	
	protected void checkBufferRegion(double latitude, double longitude) throws Exception
	{
		// Hmmmmm.. Best way to handle this.... hmmmmmm.
		
		if ((!isBufferFilled() || latitude > bufferNorth || latitude < bufferSouth) && latitude <= this.north && latitude >= this.south) {
			
			if (this.dirty) {
				writeBuffer();
			}
			
			double loadNorth = latitude + (this.bufferMarginRows * this.latitudeResolution);
			fillBuffer(loadNorth);
			
		} 
		
	}
	

	@Override
	public ModelPoint get(double latitude, double longitude)
	{
		WatchableModelPoint modelPoint = new WatchableModelPoint();
		
		double elevation = this.getElevation(latitude, longitude);
		int rgba = this.getRgba(latitude, longitude);
		
		modelPoint.setElevation(elevation);
		modelPoint.setRgba(rgba);
		
		modelPoint.addModelPointChangedListener(new ModelPointChangedHandler(getIndex(latitude, longitude)));
		return modelPoint;
	}

	public void set(ModelPoint modelPoint, long index) throws Exception
	{
		this.elevationGrid[(int)index] = (float) modelPoint.getElevation();
		this.rgbaGrid[(int)index] = modelPoint.getRgba();
		this.dirty = true;
	}

	@Override
	public double getElevation(double latitude, double longitude, boolean basic)
	{
		try {
			checkBufferRegion(latitude, longitude);
		} catch (Exception ex) {
			// TODO: Better handling
			log.error("Error with elevation buffer: " + ex.getMessage(), ex);
		}
		
		int index = getIndex(latitude, longitude);
		
		if (index < 0 || index >= elevationGrid.length) {
			return DemConstants.ELEV_NO_DATA;
		}
		
		double elev =  elevationGrid[index];
		
		return elev;
		
	}


	@Override
	public void setElevation(double latitude, double longitude, double elevation)
	{
		try {
			checkBufferRegion(latitude, longitude);
		} catch (Exception ex) {
			// TODO: Better handling
			log.error("Error with elevation buffer: " + ex.getMessage(), ex);
		}
		
		int index = getIndex(latitude, longitude);
		
		if (index >= 0 && index < elevationGrid.length) {
			elevationGrid[index] = (float) elevation;
			this.dirty = true;
		}
	}


	@Override
	public void getRgba(double latitude, double longitude, int[] fill)
	{
		ColorUtil.intToRGBA(getRgba(latitude, longitude), fill);
	}


	@Override
	public int getRgba(double latitude, double longitude)
	{
		try {
			checkBufferRegion(latitude, longitude);
		} catch (Exception ex) {
			// TODO: Better handling
			log.error("Error with elevation buffer: " + ex.getMessage(), ex);
		}
		
		int index = getIndex(latitude, longitude);
		
		if (index < 0 || index >= rgbaGrid.length) {
			return 0x0;
		}
		
		return rgbaGrid[index];
	}


	@Override
	public void setRgba(double latitude, double longitude, int rgba)
	{
		try {
			checkBufferRegion(latitude, longitude);
		} catch (Exception ex) {
			// TODO: Better handling
			log.error("Error with elevation buffer: " + ex.getMessage(), ex);
		}
		
		int index = getIndex(latitude, longitude);
		
		if (index >= 0 && index < rgbaGrid.length) {
			rgbaGrid[index] = rgba;
			this.dirty = true;
		}
	}


	@Override
	public void setRgba(double latitude, double longitude, int[] rgba)
	{
		setRgba(latitude, longitude, ColorUtil.rgbaToInt(rgba));
	}


	@Override
	public ElevationHistogramModel getElevationHistogramModel()
	{
		return super.getElevationHistogramModel();
	}
	
	
	protected int getIndex(double latitude, double longitude)
	{
		return (int) getIndexL(bufferNorth, latitude, longitude);
	}
	
	protected long getIndexL(double useNorth, double latitude, double longitude)
	{
		long column = (long) Math.round((longitude - west) / longitudeResolution);
		long row = (long) Math.round((useNorth - latitude) / latitudeResolution);
		
		if (column < 0 || column >= width) {
			return -1;
		}
		
		if (row < 0 || row >= height) {
			return -1;
		}
		
		
		long index = row * (long)width + column;
		return index;
	}
	
	/*
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
		
		if (index >= 0 && index < this.gridLength) {
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
	
	*/
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
