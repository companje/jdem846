package us.wthr.jdem846.buffers;

import junit.framework.TestCase;
import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.TempFiles;

public class HighCapacityBuffersTest extends TestCase
{
	
	private static Log log = null;

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
		long capacity = 894784853 * (long)(Integer.SIZE / 8);
		byte testValue = (byte)0xFF;
		
		IByteBuffer bigBuffer = BufferFactory.allocateByteBuffer(capacity);
		
		log.info("Writing junk data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying junk data...");
		for (long i = 0; i < capacity; i++) {
			byte b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
	
	public void testIntBuffer()
	{
		long capacity = 894784853;
		int testValue = 12345;
		
		IIntBuffer bigBuffer = BufferFactory.allocateIntBuffer(capacity);
		
		log.info("Writing junk data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying junk data...");
		for (long i = 0; i < capacity; i++) {
			int b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
	
	
	public void testFloatBuffer()
	{
		long capacity = 894784853;
		float testValue = 12345.4321f;
		
		IFloatBuffer bigBuffer = BufferFactory.allocateFloatBuffer(capacity);
		
		log.info("Writing junk data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying junk data...");
		for (long i = 0; i < capacity; i++) {
			float b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
	
	public void testDoubleBuffer()
	{
		long capacity = 447392426;
		double testValue = 12345.4321;
		
		IDoubleBuffer bigBuffer = BufferFactory.allocateDoubleBuffer(capacity);
		
		log.info("Writing junk data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, testValue);
		}
		
		
		log.info("Verifying junk data...");
		for (long i = 0; i < capacity; i++) {
			double b = bigBuffer.get(i);
			assertEquals(b, testValue);
		}

		log.info("Closing buffers...");
		
		bigBuffer.dispose();
	}
	
}
