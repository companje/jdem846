package us.wthr.jdem846;

import us.wthr.jdem846.buffers.impl.HighCapacityMappedByteBuffer;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.TempFiles;

public class SandboxTestMain extends AbstractTestMain
{
	private static Log log = null;
	

	
	public static void main(String[] args)
	{
		try {
			AbstractTestMain.initialize(false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		log = Logging.getLog(SandboxTestMain.class);
		
		long capacity = 894784853 * (long)(Integer.SIZE / 8);
		
		HighCapacityMappedByteBuffer bigBuffer = new HighCapacityMappedByteBuffer(capacity);
		
		log.info("Writing junk data...");
		for (long i = 0; i < capacity; i++) {
			bigBuffer.put(i, (byte)0xFF);
		}
		
		
		log.info("Verifying junk data...");
		long bytesWithWrongData = 0;
		for (long i = 0; i < capacity; i++) {
			byte b = bigBuffer.get(i);
			if (b != (byte)0xFF) {
				bytesWithWrongData++;
			}
		}
		
		log.info("" + bytesWithWrongData + " bytes had an incorrect value");
		
		log.info("Closing buffers...");
		
		bigBuffer.dispose();
		
		TempFiles.cleanUpTemporaryFiles(true);
	}
	
	public SandboxTestMain() 
	{

	}
	
	
	
	

}
