package us.wthr.jdem846;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import us.wthr.jdem846.geom.util.TriangleInterpolator;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.ByteConversions;

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
		
		TriangleInterpolator interpolator = new TriangleInterpolator();
		interpolator.set(2, 0, 0,
						0, 3, 1,
						4, 3, 2);
		
		double v00 = interpolator.getInterpolatedValue(0, 0);
		System.out.println("0, 0: " + v00);
		
		double v01 = interpolator.getInterpolatedValue(4, 0);
		System.out.println("4, 0: " + v01);
		
		double v10 = interpolator.getInterpolatedValue(0, 4);
		System.out.println("0, 4: " + v10);
		
		double v11 = interpolator.getInterpolatedValue(4, 4);
		System.out.println("4, 4: " + v11);
		/*
		SandboxTestMain sandbox = new SandboxTestMain();
		
		File inputFile = new File("F:\\GEBCO_08\\gebco_08.asc");
		File outputFile = new File("F:\\GEBCO_08\\gebco_08.flt");
		
		
		try {
			sandbox.doConversion(inputFile, outputFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public SandboxTestMain() 
	{

	}
	
	
	public void doConversion(File inputFile, File outputFile) throws Exception
	{
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
		
		BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
		for (int i = 0; i < 6; i++)
			inputReader.readLine();
		
		int lines = 0;
		
		String line = null;
		while ((line = inputReader.readLine()) != null) {
			String parts[] = line.split(" ");
			for (int i = 0; i < parts.length; i++) {
				float value = Float.parseFloat(parts[i]);
				byte[] buffer = ByteConversions.floatToBytes(value, ByteOrder.LSBFIRST);
				out.write(buffer);
			}
			
			lines++;
			if (lines % 100 == 0)
				log.info("Wrote line #" + lines);
		}
		out.close();
	}
	

}
