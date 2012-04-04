package us.wthr.jdem846.animate;


import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** http://personal.cscs.ch/~mvalle/mencoder/mencoder.html
 * 
 * @author Kevin M. Gill
 *
 */
public class MencoderAnimator
{
	
	private static Log log = Logging.getLog(MencoderAnimator.class);
	
	private String frames;
	private String frameType = "png";
	private String outputFormat = "mpeg4";
	private String outputFile = "output.mp4";
	private AnimationSpecification animationSpecification;
	
	
	
	public MencoderAnimator()
	{
		
	}
	
	
	
	protected String getMencoderPath()
	{
		return JDem846Properties.getProperty("us.wthr.jdem846.general.mencoder.path");
	}
}
