package us.wthr.jdem846;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.stream.FileImageOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import us.wthr.jdem846.buffers.BufferFactory;
import us.wthr.jdem846.buffers.IFloatBuffer;
import us.wthr.jdem846.buffers.IIntBuffer;
import us.wthr.jdem846.canvas.AbstractBuffer;
import us.wthr.jdem846.exception.ImageException;
import us.wthr.jdem846.graphics.ImageCapture;
import us.wthr.jdem846.image.ImageTypeEnum;
import us.wthr.jdem846.image.ImageWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.util.ByteConversions;
import us.wthr.jdem846.util.ColorUtil;

public class JDemElevationModel extends AbstractBuffer implements ElevationModel
{
	private static Log log = Logging.getLog(JDemElevationModel.class);
	
	public final static float NO_VALUE = (float) DemConstants.ELEV_NO_DATA;
	
	
	private static int defaultWriteBufferSize = 1048576; // 1MB
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	private boolean[] maskBuffer;
	//private int[] rgbaBuffer;
	//private float[] longitudeBuffer;
	//private float[] latitudeBuffer;
	//private float[] elevationBuffer;
	
	private IIntBuffer rgbaBuffer;
	private IFloatBuffer longitudeBuffer;
	private IFloatBuffer latitudeBuffer;
	private IFloatBuffer elevationBuffer;
	
	
	private ElevationHistogramModel elevationHistogramModel;

	public JDemElevationModel(BufferedImage image, InputStream dataIn, String properties) throws IOException
	{
		this(image.getWidth(), image.getHeight(), true, true, true, true, true);
		
		//this.readModelData(dataIn);
		this.loadImageData(image);
		this.properties = JDemElevationModel.readProperties(properties);
		
	}
	
	public JDemElevationModel(BufferedImage image, String properties) throws IOException
	{
		this(image.getWidth(), image.getHeight(), true, true, false, false, false);
		
		this.loadImageData(image);
		this.properties = JDemElevationModel.readProperties(properties);
		
	}
	
	public JDemElevationModel(ImageCapture imageCapture)
	{
		this(imageCapture.getWidth(), imageCapture.getHeight(), true, true, false, false, false);
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int index = getIndex(x, y);
				
				
				rgbaBuffer.put(index, imageCapture.get(x, y));
				maskBuffer[index] = rgbaBuffer.get(index) != 0x0;

			}
			
		}
	}
	
	public JDemElevationModel(String properties) throws IOException
	{
		this(1, 1, false, false, false, false, false);
		this.properties = JDemElevationModel.readProperties(properties);
	}
	
	
	public JDemElevationModel(int width, int height)
	{
		this(width, height, true, true, true, true, true);
	}
	
	
	public JDemElevationModel(int width, int height, boolean mask, boolean rgba, boolean longitude, boolean latitude, boolean elevation)
	{
		super(width, height, 1);
		
		initializeBuffers(mask, rgba, longitude, latitude, elevation);
		
	}
	
	
	protected void initializeBuffers(boolean mask, boolean rgba, boolean longitude, boolean latitude, boolean elevation)
	{
		// Disable lat/lon/elev buffers for now...
		longitude = false;
		latitude = false;
		elevation = false;
		
		
		if (mask) {
			maskBuffer = new boolean[getBufferLength()];
		}
		
		long capacity = getBufferLength();
		if (rgba) {
			rgbaBuffer = BufferFactory.allocateIntBuffer(capacity);
		}
		
		if (longitude) {
			longitudeBuffer = BufferFactory.allocateFloatBuffer(capacity);
		}
		
		if (latitude) {
			latitudeBuffer = BufferFactory.allocateFloatBuffer(capacity);
		}
		
		if (elevation) {
			elevationBuffer = BufferFactory.allocateFloatBuffer(capacity);
		}
		
		
		reset();
	}
	
	@Override
	public void load()
	{
		
	}
	
	@Override
	public void unload()
	{
		
	}
	
	@Override
	public boolean isLoaded()
	{
		return maskBuffer != null;
	}

	public void dispose()
	{
		maskBuffer = null;
		
		if (rgbaBuffer != null) {
			rgbaBuffer.dispose();
			rgbaBuffer = null;
		}
		
		if (longitudeBuffer != null) {
			longitudeBuffer.dispose();
			longitudeBuffer = null;
		}
		
		if (latitudeBuffer != null) {
			latitudeBuffer.dispose();
			latitudeBuffer = null;
		}
		
		if (elevationBuffer != null) {
			elevationBuffer.dispose();
			elevationBuffer = null;
		}
	}


	@Override
	public void reset()
	{
		for (int i = 0; i < this.getBufferLength(); i++) {
			
			if (maskBuffer != null) 
				maskBuffer[i] = false;
			
			if (rgbaBuffer != null) 
				rgbaBuffer.put(i, 0x0);
			
			if (longitudeBuffer != null)
				longitudeBuffer.put(i, JDemElevationModel.NO_VALUE);
			
			if (latitudeBuffer != null)
				latitudeBuffer.put(i, JDemElevationModel.NO_VALUE);
			
			if (elevationBuffer != null)
				elevationBuffer.put(i, (float) DemConstants.ELEV_NO_DATA);
		}
	}
	
	public boolean hasProperty(String key)
	{
		return properties.containsKey(key);
	}
	
	public void setProperty(String key, String value)
	{
		properties.put(key, value);
	}
	
	public String getProperty(String key)
	{
		return properties.get(key);
	}
	
	public Map<String, String> getProperties()
	{
		return properties;
	}
	

	public int getRgba(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (rgbaBuffer != null && index >= 0 && index < getBufferLength()) {
			return rgbaBuffer.get(index);
		} else {
			return 0x0;
			// TODO: Throw
		}
	}
	
	public void getRgba(double x, double y, int[] fill)
	{
		int rgba = getRgba(x, y);
		ColorUtil.intToRGBA(rgba, fill);
	}
	
	
	
	public double getLatitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (latitudeBuffer != null && index >= 0 && index < getBufferLength()) {
			return latitudeBuffer.get(index);
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getLongitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (longitudeBuffer != null && index >= 0 && index < getBufferLength()) {
			return longitudeBuffer.get(index);
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getElevation(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (elevationBuffer != null && index >= 0 && index < getBufferLength()) {
			return elevationBuffer.get(index);
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public boolean getMask(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (maskBuffer != null && index >= 0 && index < getBufferLength()) {
			return maskBuffer[index];
		} else {
			return false;
			// TODO: Throw
		}
	}
	
	public BufferedImage getImage()
	{
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();

		int[] rgba = new int[4];
		rgba[3] = 0xFF;
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				getRgba(x, y, rgba);
				raster.setPixel(x, y, rgba);
			}
		}
		
		return image;
	}
	
	
	
	public ElevationHistogramModel getElevationHistogramModel()
	{
		return elevationHistogramModel;
	}

	public void setElevationHistogramModel(
			ElevationHistogramModel elevationHistogramModel)
	{
		this.elevationHistogramModel = elevationHistogramModel;
	}

	protected void loadImageData(BufferedImage image)
	{
		Raster raster = image.getRaster();
		
		int w = raster.getWidth();
		int h = raster.getHeight();
		
		int[] rgbaBuffer = new int[4];
		
		boolean supportsAlpha = image.getColorModel().hasAlpha();
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				raster.getPixel(x, y, rgbaBuffer);
				
				if (!supportsAlpha) {
					rgbaBuffer[3] = 0xFF;
				}
				
				int i = getIndex(x, y);
				
				if (i >= 0 && i < this.rgbaBuffer.capacity()) {
					this.rgbaBuffer.put(i, ColorUtil.rgbaToInt(rgbaBuffer));
				}
			}
		}
		
	}
	
	public void writeImageData(OutputStream out, ImageTypeEnum imageFormat) throws IOException
	{
		BufferedImage image = getImage();
		
		try {
			ImageWriter.saveImage(image, out, imageFormat);
		} catch (ImageException ex) {
			throw new IOException("Error writing image to output stream: " + ex.getMessage(), ex);
		}

	}
	
	
	public void writeImageData(FileImageOutputStream out, ImageTypeEnum imageFormat) throws IOException
	{
		BufferedImage image = getImage();
		
		try {
			ImageWriter.saveImage(image, out, imageFormat);
		} catch (ImageException ex) {
			throw new IOException("Error writing image to output stream: " + ex.getMessage(), ex);
		}

	}
	
	protected void readModelData(InputStream in) throws IOException
	{
		byte b = 0x0;
		byte[] buffer4 = new byte[4];
		byte[] buffer1024 = new byte[1024];
		
		float fltValue = 0;
		int intValue = 0;
		boolean boolValue = false;
		
		int pointsRead = 0;
		int validRead = 0;
		
		int len = 0;
		int ttlRead = 0;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		while((len = in.read(buffer1024)) != -1) {
			ttlRead+=len;
			baos.write(buffer1024, 0, len);
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				
		for (int i = 0; i < this.getBufferLength(); i++) {
			
			intValue = bais.read();
			
			if (intValue == 0x01) {
				if ((len = bais.read(buffer4, 0, 4)) != 4) {
					throw new IOException("Did not read 4 bytes as expected (read: " + len + ", total: " + (ttlRead + len) + ")");
				} else {
					ttlRead += 4;
				}
				fltValue = ByteConversions.bytesToFloat(buffer4);
				latitudeBuffer.put(i, fltValue);
				
				if ((len = bais.read(buffer4, 0, 4)) != 4) {
					throw new IOException("Did not read 4 bytes as expected (read: " + len + ", total: " + (ttlRead + len) + ")");
				} else {
					ttlRead += 4;
				}
				fltValue = ByteConversions.bytesToFloat(buffer4);
				longitudeBuffer.put(i, fltValue);
				
				if ((len = bais.read(buffer4, 0, 4)) != 4) {
					throw new IOException("Did not read 4 bytes as expected (read: " + len + ", total: " + (ttlRead + len) + ")");
				} else {
					ttlRead += 4;
				}
				fltValue = ByteConversions.bytesToFloat(buffer4);
				elevationBuffer.put(i, fltValue);
				
				maskBuffer[i] = true;
				ttlRead += 1;
		
				pointsRead++;
				if (maskBuffer[i]) {
					validRead++;
				}
			} else {
				
				
				
				if ((len = bais.read(buffer4, 0, 4)) != 4) {
					throw new IOException("Did not read 4 bytes as expected (read: " + len + ", total: " + (ttlRead + len) + ")");
				} else {
					ttlRead += 4;
				}
				int skipLength = ByteConversions.bytesToInt(buffer4);
				
				//log.info("READ SKIP: " + skipLength);
				
				for (int j = i; j < i + skipLength && j < latitudeBuffer.capacity(); j++) {
					latitudeBuffer.put(j, NO_VALUE);
					longitudeBuffer.put(j, NO_VALUE);
					elevationBuffer.put(j, (float) DemConstants.ELEV_NO_DATA);
					maskBuffer[j] = false;
				}
				
				i += skipLength;
			}
		}
		
		log.info("JDEMELEVMDL Read " + pointsRead + " points with " + validRead + " valid");
		
	}
	
	
	
	public void writeModelData(OutputStream out) throws IOException
	{

		byte b = 0x0;
		byte[] buffer4 = new byte[4];

		int pointsWritten = 0;
		int validWritten = 0;
		
		
		BufferedOutputStream bufferedOut = new BufferedOutputStream(out, defaultWriteBufferSize);
		
		
		for (int i = 0; i < this.getBufferLength(); i++) {
			
			if (maskBuffer[i] == true) {
			
				bufferedOut.write(0x01);
				
				if (latitudeBuffer != null) {
					ByteConversions.floatToBytes(latitudeBuffer.get(i), buffer4);
				} else {
					ByteConversions.floatToBytes(0.0f, buffer4);
				}
				bufferedOut.write(buffer4, 0, 4);
				
				if (longitudeBuffer != null) {
					ByteConversions.floatToBytes(longitudeBuffer.get(i), buffer4);
				} else {
					ByteConversions.floatToBytes(0.0f, buffer4);
				}
				bufferedOut.write(buffer4, 0, 4);
				
				if (elevationBuffer != null) {
					ByteConversions.floatToBytes(elevationBuffer.get(i), buffer4);
				} else {
					ByteConversions.floatToBytes(0.0f, buffer4);
				}
				bufferedOut.write(buffer4, 0, 4);
	
				
	
				pointsWritten++;
				if (maskBuffer[i]) {
					validWritten++;
				}
				
			} else {
				bufferedOut.write(0x00);
				int skipLength = 0;
				
				for (int j = i; j < getBufferLength(); j++) {
					if (maskBuffer[j]) {
						break;
					} else {
						skipLength++;
					}
				}
				
				ByteConversions.intToBytes(skipLength, buffer4);
				bufferedOut.write(buffer4, 0, 4);
				
				i += skipLength;
				
			}
		}
		
		bufferedOut.flush();
		out.flush();
		log.info("JDEMELEVMDL Wrote " + pointsWritten + " with " + validWritten + " valid points");
		
	}
	
	
	public static Map<String, String> readProperties(String propertiesJson) throws IOException
	{
		Map<String, String> propertiesMap = new HashMap<String, String>();
		
		if (propertiesJson == null) {
			return propertiesMap;
		}
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON( propertiesJson );
		
		JSONObject propertiesObject = json.getJSONObject("properties");

		JSONArray namesArray = propertiesObject.names();

		for (Object o : namesArray) {
			
			String s = (String) o;
			String v = propertiesObject.getString(s);
			
			propertiesMap.put(s, v);
		}
		
		return propertiesMap;
	}
	
	public void writeProperties(OutputStream out) throws IOException
	{
		JSONObject jsonObject = new JSONObject();
		
		JSONObject propertiesObject = new JSONObject();
		
		for (String key : properties.keySet()) {
			
			String value = properties.get(key);
			
			if (value != null) {
				propertiesObject.element(key, value);
			}
			
		}
		
		jsonObject.element("properties", propertiesObject);
		
		String json = jsonObject.toString(3);

		out.write(json.getBytes());
	}

	
	
	
	
}
