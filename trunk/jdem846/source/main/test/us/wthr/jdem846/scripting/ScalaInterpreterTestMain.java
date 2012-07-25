package us.wthr.jdem846.scripting;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


import us.wthr.jdem846.scripting.scala.*;
import us.wthr.jdem846.AbstractTestMain;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ScalaInterpreterTestMain extends AbstractTestMain
{
private static Log log = null;
	
	public static void main(String[] args)
	{
		try {
			initialize(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		log = Logging.getLog(ScalaInterpreterTestMain.class);
		

		try {
			ScalaInterpreterTestMain testMain = new ScalaInterpreterTestMain();
			testMain.doTest("resources://scripting/template-dem.sc");
			//testMain.doTest("resources://scripting/template-dem2.sc");
		} catch (Exception ex) {
			log.error("Uncaught exception while running test main: " + ex.getMessage(), ex);
		}
		
	}
	
	
	protected void doTest(String testScript) throws Exception
	{
		
		String script = readFile(testScript);
		
		ScriptProxy scriptProxy = ScalaScriptLoader.parseScript(script);
		scriptProxy.initialize();
		
		/*
		//log.info("Script: \n" + script);
		EmbeddedInterpreter interpreter = new EmbeddedInterpreter();
		

		Class cls = (Class) interpreter.build(script, "JDemScript");
		
		if (cls == null) {
			log.info("cls is null!");
		}
		
		Object obj = cls.newInstance();
		
		log.info("cls -> " + obj.getClass().getName());
		
		for (Method method : obj.getClass().getMethods()) {
			//log.info("  method -> " + method.getName());
			if (method.getName().equals("sayHi") || method.getName().equals("sayHello")) {
				method.invoke(obj);
			}
		}
		
		for (Field field : obj.getClass().getDeclaredFields()) {
			log.info("Field -> " + field.getName() + ", " + field.getType());
		}
		
		
		//Class hw = Class.forName("us.wthr.jdem846.scripting.HelloWorld");

		
		//interpreter.exec(script);
		
		//ScalaTestClass test = new ScalaTestClass();
		//test.sayHi();
		//Settings s = new Settings();
		//Interpreter interpreter = new Interpreter(s);
		//interpreter.compileString(script);
		*/
		
		
	}
	
	
	
	protected String readFile(String path) throws Exception
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
}
