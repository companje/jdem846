package us.wthr.jdem846;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import us.wthr.jdem846.canvas.AbstractBuffer;
import us.wthr.jdem846.canvas.GeoRasterBuffer3d;
import us.wthr.jdem846.canvas.util.ColorUtil;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.MathExt;
import us.wthr.jdem846.model.ElevationHistogramModel;
import us.wthr.jdem846.util.ByteConversions;

public class JDemElevationModel extends AbstractBuffer
{
	private static Log log = Logging.getLog(JDemElevationModel.class);
	
	public final static float NO_VALUE = (float) DemConstants.ELEV_NO_DATA;
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	private boolean[] maskBuffer;
	private int[] rgbaBuffer;
	private float[] longitudeBuffer;
	private float[] latitudeBuffer;
	private float[] elevationBuffer;
	
	private ElevationHistogramModel elevationHistogramModel;
	
	public JDemElevationModel(BufferedImage image, InputStream dataIn, String properties) throws IOException
	{
		this(image.getWidth(), image.getHeight());
		
		this.readModelData(dataIn);
		this.loadImageData(image);
		this.readProperties(properties);
		
	}
	
	public JDemElevationModel(GeoRasterBuffer3d geoRasterBuffer)
	{
		this(geoRasterBuffer.getWidth(), geoRasterBuffer.getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int index = getIndex(x, y);
				
				rgbaBuffer[index] = geoRasterBuffer.get(x, y);
				maskBuffer[index] = geoRasterBuffer.isPixelFilled(x, y);
				
				latitudeBuffer[index] = (float) geoRasterBuffer.getLatitude(x, y);
				longitudeBuffer[index] = (float) geoRasterBuffer.getLongitude(x, y);
				elevationBuffer[index] = (float) geoRasterBuffer.getElevation(x, y);
			}
		}
		
		
	}
	
	public JDemElevationModel(int width, int height)
	{
		super(width, height, 1);
		
		maskBuffer = new boolean[getBufferLength()];
		rgbaBuffer = new int[getBufferLength()];
		longitudeBuffer = new float[getBufferLength()];
		latitudeBuffer = new float[getBufferLength()];
		elevationBuffer = new float[getBufferLength()];
		
		reset();
	}
	
	
	

	public void dispose()
	{
		maskBuffer = null;
		rgbaBuffer = null;
		longitudeBuffer = null;
		latitudeBuffer = null;
		elevationBuffer = null;
	}


	@Override
	public void reset()
	{
		for (int i = 0; i < this.getBufferLength(); i++) {
			maskBuffer[i] = false;
			rgbaBuffer[i] = 0x0;
			longitudeBuffer[i] = JDemElevationModel.NO_VALUE;
			latitudeBuffer[i] = JDemElevationModel.NO_VALUE;
			elevationBuffer[i] = (float) DemConstants.ELEV_NO_DATA;
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
		if (index >= 0 && index < getBufferLength()) {
			return rgbaBuffer[index];
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
		if (index >= 0 && index < getBufferLength()) {
			return latitudeBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getLongitude(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return longitudeBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public double getElevation(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
			return elevationBuffer[index];
		} else {
			return NO_VALUE;
			// TODO: Throw
		}
	}
	
	public boolean getMask(double x, double y)
	{
		int index = this.getIndex(x, y);
		if (index >= 0 && index < getBufferLength()) {
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

		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				raster.getPixel(x, y, rgbaBuffer);
				
				int i = getIndex(x, y);
				
				if (i >= 0 && i < this.rgbaBuffer.length) {
					this.rgbaBuffer[i] = ColorUtil.rgbaToInt(rgbaBuffer);
				}
			}
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
				latitudeBuffer[i] = fltValue;
				
				if ((len = bais.read(buffer4, 0, 4)) != 4) {
					throw new IOException("Did not read 4 bytes as expected (read: " + len + ", total: " + (ttlRead + len) + ")");
				} else {
					ttlRead += 4;
				}
				fltValue = ByteConversions.bytesToFloat(buffer4);
				longitudeBuffer[i] = fltValue;
				
				if ((len = bais.read(buffer4, 0, 4)) != 4) {
					throw new IOException("Did not read 4 bytes as expected (read: " + len + ", total: " + (ttlRead + len) + ")");
				} else {
					ttlRead += 4;
				}
				fltValue = ByteConversions.bytesToFloat(buffer4);
				elevationBuffer[i] = fltValue;
				
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
				
				for (int j = i; j < i + skipLength && j < latitudeBuffer.length; j++) {
					latitudeBuffer[j] = NO_VALUE;
					longitudeBuffer[j] = NO_VALUE;
					elevationBuffer[j] = (float) DemConstants.ELEV_NO_DATA;
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
		
		for (int i = 0; i < this.getBufferLength(); i++) {
			
			if (maskBuffer[i] == true) {
			
				out.write(0x01);
				
				ByteConversions.floatToBytes(latitudeBuffer[i], buffer4);
				out.write(buffer4, 0, 4);
				
				ByteConversions.floatToBytes(longitudeBuffer[i], buffer4);
				out.write(buffer4, 0, 4);
				
				ByteConversions.floatToBytes(elevationBuffer[i], buffer4);
				out.write(buffer4, 0, 4);
	
				
	
				pointsWritten++;
				if (maskBuffer[i]) {
					validWritten++;
				}
				
			} else {
				out.write(0x00);
				int skipLength = 0;
				
				for (int j = i; j < getBufferLength(); j++) {
					if (maskBuffer[j]) {
						break;
					} else {
						skipLength++;
					}
				}
				
				ByteConversions.intToBytes(skipLength, buffer4);
				out.write(buffer4, 0, 4);
				
				i += skipLength;
				
			}
		}
		
		out.flush();
		
		log.info("JDEMELEVMDL Wrote " + pointsWritten + " with " + validWritten + " valid points");
		
	}
	
	
	public void readProperties(String propertiesJson) throws IOException
	{
		if (propertiesJson == null) {
			return;
		}
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON( propertiesJson );
		
		JSONObject propertiesObject = json.getJSONObject("properties");

		JSONArray namesArray = propertiesObject.names();

		for (Object o : namesArray) {
			
			String s = (String) o;
			String v = propertiesObject.getString(s);
			
			properties.put(s, v);
		}
		
		
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
