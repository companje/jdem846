package us.wthr.jdem846.scripting.scala;


import java.io.StringWriter;
import java.io.PrintWriter;

import javax.script.ScriptException;

import scala.tools.nsc.*;

public class EmbeddedScalaInterpreter
{
	
	private StringWriter writer;
	private Interpreter interpreter;
	
	public EmbeddedScalaInterpreter()
	{
		this.writer = new java.io.StringWriter();
		this.interpreter = new Interpreter(new Settings(), new PrintWriter(writer));
		interpreter.settings().classpath().append(System.getProperty("java.class.path"));
	}
	
	
	public void bind(String name, Object value) {
	    interpreter.bind(name, value.getClass().getName(), value);
	}
	
	public Object build(String code, String loadClass) throws ScriptException
	{
		// Clear the previous output buffer
		writer.getBuffer().setLength(0);
		
		//interpreter.settings().usejavacp().value = true;
		//	    interpreter.settings.usejavacp.value = true
			    		
		boolean res = interpreter.compileString(code);
		if (res) {
			return interpreter.classLoader().findClass(loadClass);
		} else {
			throw new ScriptException("error in: '" + code + "'\n" + writer.toString());
		}
	}
	
	
	
	
	
}
