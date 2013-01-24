package us.wthr.jdem846;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.wthr.jdem846.logging.Log;

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
		
		List<String> interpolations = new LinkedList<String>();
		String template = "dfhdfusd ${foo.bar} fdfdush ${bar.foo[0]} fdfdf ${mega.deth?metal}";
		
		Pattern pattern = Pattern.compile("\\$\\{[a-zA-Z0-9.\\[\\]\\?]+\\}");
		Matcher matcher = pattern.matcher(template);
		while (matcher.find()) {
			String var = template.substring(matcher.start(), matcher.end());
			String varStripped = var.substring(2, var.length() - 1);
			interpolations.add(varStripped);
		}
		
		
		for (String interpolation : interpolations) {
			System.err.println("Found '" + interpolation + "'");
		}
		
	}
	
	public SandboxTestMain() 
	{

	}
	
	
	
	

}
