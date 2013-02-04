package us.wthr.jdem846;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.project.ProjectMarshall;
import us.wthr.jdem846.project.workspace.project.WorkspaceProjectReader;

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
		
		ProjectMarshall project = null;
		
		
		try {
			project = WorkspaceProjectReader.readProject("C:/jdem/Data/Saturn/saturn_render_testing");
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		
		
		
/*		for (int i = 0; i < 256; i++) {
			Color c0 = new Color(i, i, i, i);
			String hex = c0.toString();
			
			try {
				Color c1 = new Color(hex);
			
				System.err.println("Color: " + c0.toString() + ", " + c1.toString());
			} catch (Exception ex) {
				System.err.println("Hex: " + hex);
				ex.printStackTrace();
			}
			
			
		}*/
		
		
		
		//MessageDialog md = new MessageDialog("Hello", JDem846Properties.getProperty("us.wthr.jdem846.ui.notifications.error"), "This is a message", null);
		//md.setVisible(true);
//		
//		List<String> interpolations = new LinkedList<String>();
//		String template = "dfhdfusd ${foo.bar} fdfdush ${bar.foo[0]} fdfdf ${mega.deth?metal}";
//		
//		Pattern pattern = Pattern.compile("\\$\\{[a-zA-Z0-9.\\[\\]\\?]+\\}");
//		Matcher matcher = pattern.matcher(template);
//		while (matcher.find()) {
//			String var = template.substring(matcher.start(), matcher.end());
//			String varStripped = var.substring(2, var.length() - 1);
//			interpolations.add(varStripped);
//		}
//		
//		
//		for (String interpolation : interpolations) {
//			System.err.println("Found '" + interpolation + "'");
//		}
		
	}
	
	public SandboxTestMain() 
	{

	}
	
	
	
	

}
