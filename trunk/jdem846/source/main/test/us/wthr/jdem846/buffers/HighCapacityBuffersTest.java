package us.wthr.jdem846.buffers;

import junit.framework.TestCase;
import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.TempFiles;

public class HighCapacityBuffersTest extends TestCase
{
	
	private static Log log = null;
	
	private long bytesCapacityPerTest = 2684354560l; // About 2.5GB
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		try {
			AbstractTestMain.initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		log = Logging.getLog(HighCapacityBuffersTest.class);
	}
	
	
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		TempFiles.cleanUpTemporaryFiles();
	}



	public void testByteBuffer()
	{
		
		long capacity = bytesCapacityPerTest;
		byte testValue = (byte)0xFF;
		
		IByteBuffer bigBuffer = BufferFactory.allocateByteBuffer(capacity);
		
		log.info("Testing Byte Buffer with a capacity of " + bigBuffer.capacityBytes() + " bytes");
		
		log.info("Writing test data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying test data...");
		for (long i = 0; i < capacity; i++) {
			byte b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
	
	public void testIntBuffer()
	{
		long capacity = bytesCapacityPerTest / (Integer.SIZE / 8);
		int testValue = 12345;
		
		IIntBuffer bigBuffer = BufferFactory.allocateIntBuffer(capacity);
		log.info("Testing Integer Buffer with a capacity of " + bigBuffer.capacityBytes() + " bytes");
		
		log.info("Writing test data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying test data...");
		for (long i = 0; i < capacity; i++) {
			int b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
	
	
	public void testFloatBuffer()
	{
		long capacity = bytesCapacityPerTest / (Float.SIZE / 8);
		float testValue = 12345.4321f;
		
		IFloatBuffer bigBuffer = BufferFactory.allocateFloatBuffer(capacity);
		log.info("Testing Float Buffer with a capacity of " + bigBuffer.capacityBytes() + " bytes");
		
		log.info("Writing test data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying test data...");
		for (long i = 0; i < capacity; i++) {
			float b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
	
	public void testDoubleBuffer()
	{
		long capacity = bytesCapacityPerTest / (Double.SIZE / 8);
		double testValue = 12345.4321;
		
		IDoubleBuffer bigBuffer = BufferFactory.allocateDoubleBuffer(capacity);
		log.info("Testing Double Buffer with a capacity of " + bigBuffer.capacityBytes() + " bytes");
		
		log.info("Writing test data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying test data...");
		for (long i = 0; i < capacity; i++) {
			double b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
}
