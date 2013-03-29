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

import junit.framework.TestCase;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class ShapeBaseTest extends TestCase
{
	
	@SuppressWarnings("unused")
	private static Log log = Logging.getLog(ShapeBaseTest.class);
	
	
	@SuppressWarnings("unused")
	public void testShapeBaseInit() throws Exception
	{
		
		ShapeFileReference shapeFileReference = new ShapeFileReference("C:/srv/elevation/Shapefiles/BP14669/Foundation/Trans_RoadSegment.shp", "usgs-transportation-roads");
		
		ShapeBase shapeBase = new ShapeBase(shapeFileReference);
	
	
	}
	
}
