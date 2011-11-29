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

package us.wthr.jdem846.render.gfx;

import java.awt.Color;

/** Defines an object that which can be rendered in three-dimensional space
 * 
 * @author Kevin M. Gill
 *
 */
public interface Renderable
{
	public static final int COUNTERCLOCKWISE = -1;
	public static final int UNDEFINED = 0;
	public static final int CLOCKWISE = 1;
	
	
	public int[] getColor();
	
	public Renderable copy();
	
	public void rotate(Vector angles);
	public void rotate(double angle, int axis);
	
	public void translate(Vector trans);
	
	public void projectTo(Vector eye, Vector near);//, double nearWidth, double nearHeight, double farDistance);
	
	public void setNormal(double[] normal);
	public void prepareForRender(double[] lightSource, double specularExponent);
	public void render(ViewportBuffer buffer, int viewportWidth, int viewportHeight);
}
