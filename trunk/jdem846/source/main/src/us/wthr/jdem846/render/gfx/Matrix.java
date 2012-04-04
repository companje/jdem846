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

@Deprecated
public abstract class Matrix
{
	
	private double[][] matrix;

	public Matrix()
	{
		setup();
	}
	
	
	protected abstract void setup();

	public double get(int row, int col)
	{
		return matrix[row][col];
	}
	
	public void set(int row, int col, double value)
	{
		matrix[row][col] = value;
	}
	
	public double[][] getMatrix()
	{
		return matrix;
	}


	protected void setMatrix(double[][] matrix)
	{
		this.matrix = matrix;
	}


	public int getRows()
	{
		return matrix.length;
	}


	public int getColumns()
	{
		return matrix[0].length;
	}


	
	
	
}
