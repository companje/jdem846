package us.wthr.jdem846.scripting;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class GroovyExecutionTestMain extends AbstractTestMain
{
	@SuppressWarnings("unused")
	private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		log = Logging.getLog(GroovyExecutionTestMain.class);
		
		GroovyExecutionTestMain testMain = new GroovyExecutionTestMain();
		try {
			testMain.doTest();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	public String readFile(String path) throws Exception
	{
		InputStream in = JDemResourceLoader.getAsInputStream(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] buffer = new byte[1024];
		int length = 0;
		
		while ((length = in.read(buffer)) > 0) {
			baos.write(buffer, 0, length);
		}
		
		return new String(baos.toByteArray());
	}
	
	public void doTest() throws Exception
	{
		String groovyCode = readFile(JDem846Properties.getProperty("us.wthr.jdem846.ui.scriptProject.defaultScriptTemplate.groovy"));
		
		ScriptShell shell = ScriptShellFactory.getScriptShell(ScriptLanguageEnum.GROOVY);
		shell.evaluate(groovyCode);
		
		//CompilerConfiguration compiler = new CompilerConfiguration();
		//GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(), new Binding(), compiler);
		
		//shell.evaluate(groovyCode);
		//shell.evaluate("System.out.println(\"Hello\")");
		
		//log.info(groovyCode);
	}
	
}
