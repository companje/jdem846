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

package us.wthr.jdem846.shapefile;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import us.wthr.jdem846.shapefile.modeling.LineStroke;

public enum FeatureTypeStroke {

	BasicFeature(new LineStroke(Color.BLACK)),
	BasicWater(new LineStroke(new Color(44, 73, 128, 255))),
	
	BasicRoad(new LineStroke(3.0f, Color.LIGHT_GRAY), new LineStroke(1.8f, Color.DARK_GRAY)),
	WaterCrossing(new LineStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{1.0f, 1.0f}, 1.0f, Color.BLACK)),
	BasicHighway(new LineStroke(4.0f, Color.DARK_GRAY)),
	BasicHighwayInTunnel(new LineStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{6.0f, 3.0f}, 1.0f, Color.BLACK)),
	PrimaryRoad(new LineStroke(4.5f, Color.LIGHT_GRAY), new LineStroke(3.3f, Color.DARK_GRAY)),
	SecandaryRoad(new LineStroke(3.9f, Color.LIGHT_GRAY), new LineStroke(2.7f, Color.DARK_GRAY)),
	DirtRoad(new LineStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{6.0f, 3.0f}, 1.0f, Color.BLACK)),
	Ramp(new LineStroke(1.0f, Color.DARK_GRAY)),
	Rail(new LineStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[]{6.0f, 3.0f}, 1.0f, Color.LIGHT_GRAY)),
	
	Runway(new LineStroke(13.0f, Color.LIGHT_GRAY), new LineStroke(10.0f, Color.DARK_GRAY));
	
	// TODO: Make into array for line borders, etc..
	private final LineStroke[] lineStrokes;
	
	FeatureTypeStroke(LineStroke ... lineStrokes)
	{
		this.lineStrokes = lineStrokes;
	}

	public LineStroke[] lineStrokes() { return lineStrokes; }
}
