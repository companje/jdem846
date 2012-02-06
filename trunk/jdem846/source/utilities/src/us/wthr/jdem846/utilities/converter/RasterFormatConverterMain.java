package us.wthr.jdem846.utilities.converter;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.ui.BaseUIMain;
import us.wthr.jdem846.ui.JDemUiMain;

public class RasterFormatConverterMain extends BaseUIMain
{
	private static Log log = null;


	@Override
	public void beforeInit() throws Exception
	{
		
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
