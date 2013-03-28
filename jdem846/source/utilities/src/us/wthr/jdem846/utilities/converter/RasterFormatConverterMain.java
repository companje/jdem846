package us.wthr.jdem846.utilities.converter;

import us.wthr.jdem846.AbstractMain;
import us.wthr.jdem846.logging.Log;


public class RasterFormatConverterMain extends AbstractMain
{
	private static Log log = null;


	@Override
	public void beforeInit() throws Exception
	{
		
	}
	
	@Override
	public void afterCoreInit() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void afterInit() throws Exception
	{
		
	}


	public static void main(String[] args)
	{

		
		
		RasterFormatConverterMain main = new RasterFormatConverterMain();
		
		try {
			main.initialization(args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		
	}

	
	
	
	
}
