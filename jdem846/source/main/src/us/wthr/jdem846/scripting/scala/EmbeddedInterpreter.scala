package us.wthr.jdem846.scripting.scala

import java.io.{ StringWriter, PrintWriter }

import javax.script.ScriptException;

import scala.tools.nsc.InterpreterResults._
import scala.tools.nsc.{Interpreter => ScalaInterpreter, Settings}

class ResultHolder(var value : Any)

class EmbeddedInterpreter {
   // http://lampsvn.epfl.ch/trac/scala/ticket/874
   // http://lampsvn.epfl.ch/svn-repos/scala/scala/trunk/src/compiler/scala/tools/nsc/Interpreter.scala

  private val writer = new java.io.StringWriter()
  private val interpreter = new ScalaInterpreter(new Settings(), new PrintWriter(writer));

  /**
   * Bind the given value to the given variable name in the interpreter
   *
   * @param name the variable name
   * @param value the value
   */
  def bind(name : String, value : AnyRef) {
    interpreter.bind(name, value.getClass.getName, value)
  }

  
  def build(code : String, loadClass : String) : Any = {
    // Clear the previous output buffer
    writer.getBuffer.setLength(0)
    
    interpreter.settings.usejavacp.value = true
    
    val res = interpreter.compileString(code)
    
    res match {
      //case false => null
      case true => interpreter.classLoader.findClass(loadClass)
      case false => throw new ScriptException("error in: '" + code + "'\n" + writer toString)
    }

  }
  
  /**
   * Execute a string of Scala code and ignore its result.
   *
   * @param code the code to execute 
   * @throws ScriptException if the code fails to parse
   */
  def exec(code : String) {
    // Clear the previous output buffer
    writer.getBuffer.setLength(0)
    
    
    
    // Execute the code and catch the result
    val ir = interpreter.interpret(code);

    // Return value or throw an exception based on result
    ir match {
      case Success => ()
      case Error => throw new ScriptException("error in: '" + code + "'\n" + writer toString)
      case Incomplete => throw new ScriptException("incomplete in :'" + code + "'\n" + writer toString)
    }
  }

  /**
   * Evaluate the given string of Scala code and return the result. The code
   * must evaluate to a value.
   *
   * @param code the code to evaluate
   * @return the result
   * @throws ScriptException if the code fails to parse
   */
  def eval(code : String) : Any = {
    //println("-------\n" + code)
    // Clear the previous output buffer
    writer.getBuffer.setLength(0)

    // Create an object to hold the result and bind in the interpreter
    val holder = new ResultHolder(null)
    bind("$result__", holder);

    // Execute the code and catch the result
    val ir = interpreter.interpret("$result__.value = " + code);

    // Return value or throw an exception based on result
    ir match {
      case Success => holder.value
      case Error => throw new ScriptException("error in: '" + code + "'\n" + writer toString)
      case Incomplete => throw new ScriptException("incomplete in :'" + code + "'\n" + writer toString)
    }
  }
}