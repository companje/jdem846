/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui.projectionconfig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.gfx.Line;
import us.wthr.jdem846.render.gfx.Renderable;
import us.wthr.jdem846.render.gfx.Vector;
import us.wthr.jdem846.render.gfx.ViewportBuffer;

@SuppressWarnings("serial")
public class ProjectionPreview extends JPanel
{
	private static Log log = Logging.getLog(ProjectionPreview.class);
	
	private Dimension dimension;
	
	private Line[] lines = new Line[7];
	
	private Color lineColor = Color.YELLOW;
	private Color backgroundColor = Color.BLACK;
	
	private double rotateX = 0.0;
	private double rotateY = 0.0;
	private double rotateZ = 0.0;
	
	
	public ProjectionPreview(Dimension d)
	{
		setDimension(d);
		
	}

	public Dimension getDimension()
	{
		return dimension;
	}

	public void setDimension(Dimension dimension)
	{
		this.dimension = dimension;
		createRenderObjects();
	}
	
	
	protected void createRenderObjects()
	{

		double halfX = (double)this.dimension.width / 2.0;
		double halfZ = (double)this.dimension.height / 2.0;
		
		Vector backLeft = new Vector(-halfX, 0, -halfZ);
		Vector frontLeft = new Vector(-halfX, 0, halfZ);
		Vector frontRight = new Vector(halfX, 0, halfZ);
		Vector backRight = new Vector(halfX, 0, -halfZ);
		
		int[] color = {lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()};
		
		lines[0] = new Line(color, backLeft.copy(), frontLeft.copy());
		lines[1] = new Line(color, frontLeft.copy(), frontRight.copy());
		lines[2] = new Line(color, backRight.copy(), frontRight.copy());
		lines[3] = new Line(color, backLeft.copy(), backRight.copy());
		

		lines[4] = new Line(color, new Vector(0, 0, 0), 
									new Vector(0, 0, halfZ));
		
		lines[5] = new Line(color, new Vector(0, 0, halfZ),
									new Vector(-(halfX*.2), 0.0, halfZ*.8));
		
		lines[6] = new Line(color, new Vector(0, 0, halfZ),
									new Vector((halfX*.2), 0.0, halfZ*.8));
	}

	@Override
	public void paint(Graphics g)
	{
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		ViewportBuffer buffer = new ViewportBuffer(getWidth(), getHeight());
		
		Graphics2D g2d = (Graphics2D) g;
		
		
		Vector eye = new Vector(0, 0, getWidth());
		Vector surface = new Vector(0, 0, getWidth() / 2.0);
		
		double hypotenuse = Math.sqrt(Math.pow(getWidth()/2, 2) + Math.pow(surface.getZ(), 2));
		double viewAngle = 2 * Math.toDegrees(Math.atan((getWidth()/2) / surface.getZ()));
		double translateZ = -(dimension.width / 2.0);
		//translateZ = -78;
		log.info("View Angle: " + viewAngle + ", Hypotenuse: " + hypotenuse + ", translateZ: " + translateZ);
		
		//double nearWidth = 50;
		//double nearHeight = 50;
		//double farDistance = 50;
		
		Vector rotateXVector = new Vector(rotateX, 0, 0);
		Vector rotateYVector = new Vector(0, rotateY, 0);
		Vector rotateZVector = new Vector(0, 0, rotateZ);
		
		Vector translate = new Vector(0, 0, translateZ);
		
		for (Line line : lines) {
			Line copy = line.copy();
			
			
			copy.rotate(rotateYVector); 
			copy.rotate(rotateXVector);
			
			copy.translate(translate);
			copy.projectTo(eye, surface);//, nearWidth, nearHeight, farDistance);
			
			copy.prepareForRender(null, 1.0);

			copy.render(buffer, getWidth(), getHeight());
		}
		
		/*
		int[] color = {255, 0, 0, 0};
		double hX = (double)getWidth() / 2.0 - 4;
		Line exampleLine = new Line(color, new Vector(-hX, 0, 0), new Vector(hX, 0, 0));
		exampleLine.projectTo(eye, near);
		exampleLine.prepareForRender(null, 1);
		exampleLine.render(buffer, getWidth(), getHeight());
		*/
		
		//double viewAngle = 2 * Math.atan(1 / near.getZ());
		
		//double viewAngle = 2 * Math.toDegrees(Math.atan((getWidth()/2) / surface.getZ()));
		
		//double viewAngle = Math.toDegrees(Math.pow(Math.tan((getWidth()/2) / surface.getZ()), -1));
		
		
		buffer.paint(image, 0);
		
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(lineColor);
		g2d.drawImage(image, 0, 0, this);

		
	}
	
	
	public Color getLineColor()
	{
		return lineColor;
	}

	public void setLineColor(Color lineColor)
	{
		this.lineColor = lineColor;
	}

	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public double getRotateX()
	{
		return rotateX;
	}

	public void setRotateX(double rotateX)
	{
		this.rotateX = rotateX;
	}

	public double getRotateY()
	{
		return rotateY;
	}

	public void setRotateY(double rotateY)
	{
		this.rotateY = rotateY;
	}

	public double getRotateZ()
	{
		return rotateZ;
	}

	public void setRotateZ(double rotateZ)
	{
		this.rotateZ = rotateZ;
	}
	
	
	
	
}
