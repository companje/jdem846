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

package us.wthr.jdem846.render.kml;

import java.awt.geom.Path2D;

@SuppressWarnings("serial")
public class GridSpace extends Path2D.Double
{
	
	private int topRow = 0;
	private int bottomRow = 0;
	
	private int leftColumn = 0;
	private int rightColumn = 0;
	
	public GridSpace(int topRow, int bottomRow, int leftColumn, int rightColumn)
	{
		setTopRow(topRow);
		setBottomRow(bottomRow);
		setLeftColumn(leftColumn);
		setRightColumn(rightColumn);
		
		//
		//       LT       RT
		//       |        ^
		//       |        |
		//       |        |
		//       V        |
		//       LB------>RB
		//
		
		
		moveTo(leftColumn, topRow);
		lineTo(leftColumn, bottomRow);
		lineTo(rightColumn, bottomRow);
		lineTo(rightColumn, topRow);
		closePath();
		
		
	}

	public int getTopRow()
	{
		return topRow;
	}

	public void setTopRow(int topRow)
	{
		this.topRow = topRow;
	}

	public int getBottomRow()
	{
		return bottomRow;
	}

	public void setBottomRow(int bottomRow)
	{
		this.bottomRow = bottomRow;
	}

	public int getLeftColumn()
	{
		return leftColumn;
	}

	public void setLeftColumn(int leftColumn)
	{
		this.leftColumn = leftColumn;
	}

	public int getRightColumn()
	{
		return rightColumn;
	}

	public void setRightColumn(int rightColumn)
	{
		this.rightColumn = rightColumn;
	}
	
	
	
	
	
}
