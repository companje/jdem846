package us.wthr.jdem846.jogl.view;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import us.wthr.jdem846.graphics.ExamineView;

public interface Renderable
{
	public void render(GL2 gl, GLU glu, ExamineView view);
}
