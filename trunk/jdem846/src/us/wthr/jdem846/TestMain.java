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

package us.wthr.jdem846;


import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;


import us.wthr.jdem846.dbase.DBaseFieldDescriptor;
import us.wthr.jdem846.dbase.DBaseFile;
import us.wthr.jdem846.dbase.DBaseLastUpdate;
import us.wthr.jdem846.dbase.DBaseRecord;
import us.wthr.jdem846.exception.DataSourceException;
import us.wthr.jdem846.exception.InvalidFileFormatException;
import us.wthr.jdem846.exception.RenderEngineException;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.input.DataSourceFactory;
import us.wthr.jdem846.input.edef.ElevationDatasetExchangeHeader;
import us.wthr.jdem846.input.edef.ElevationDatasetExchangeWriter;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.render.Dem2dGenerator;
import us.wthr.jdem846.render.DemCanvas;
import us.wthr.jdem846.render.ElevationDsFormatGenerator;
import us.wthr.jdem846.render.OutputProduct;
import us.wthr.jdem846.scaling.FloatRaster;
import us.wthr.jdem846.scaling.RasterScale;
import us.wthr.jdem846.shapefile.Shape;
import us.wthr.jdem846.shapefile.ShapeFile;
import us.wthr.jdem846.shapefile.ShapeIndexFile;
import us.wthr.jdem846.shapefile.ShapeIndexRecord;

/** Testing entry point.
 * 
 * @author Kevin M. Gill
 *
 */
@Deprecated
public class TestMain 
{
	private static Log log = Logging.getLog(TestMain.class);
	
}
