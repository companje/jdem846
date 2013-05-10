package us.wthr.jdem846.graphics.opengl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.media.opengl.GL2;

import org.apache.commons.io.IOUtils;

public class OpenGlShaderProgram
{
	
	private int vertexShader = 0;
	private int fragmentShader = 0;
	
	private int shaderProgram = 0;
	
	public OpenGlShaderProgram(GL2 gl, String vertexShaderPath, String fragmentShaderPath) throws IOException
	{
		vertexShader = compileShader(gl, GL2.GL_VERTEX_SHADER, vertexShaderPath);
		fragmentShader = compileShader(gl, GL2.GL_FRAGMENT_SHADER, fragmentShaderPath);
	}
	
	
	public void attachShaders(GL2 gl)
	{
		
		shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vertexShader);
		gl.glAttachShader(shaderProgram, fragmentShader);
		gl.glLinkProgram(shaderProgram);
		gl.glValidateProgram(shaderProgram);
		gl.glUseProgram(shaderProgram);
		
	}
	
	protected int compileShader(GL2 gl, int type, String path) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(path));
		List<String> lines = IOUtils.readLines(br);
		return compileShader(gl, type, (String[]) lines.toArray());
	}
	
	protected int compileShader(GL2 gl, int type, String[] src)
	{
		int i = gl.glCreateShader(type);

		gl.glShaderSource(i, 1, src, (int[])null, 0);
		gl.glCompileShader(i);
		
		return i;
	}
	
}
