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

/** Collection of common constants.
 * 
 * @author Kevin M. Gill
 *
 */
public class DemConstants {
	
	public static final String APPLICATION_SPEC_VERSION = "0.1";
	
	public static final double EARTH_MEAN_RADIUS = 6371.0;
	public static final double MARS_MEAN_RADIUS = 3396.2;
	public static final double MOON_MEAN_RADIUS = 1737.1;
	
	
	public static final double ELEV_NO_DATA = -1000000;
	
	
	// Status Codes
	public static final int STAT_SUCCESSFUL           = 100;


	// Elevation Conditions
	public static final int STAT_INVALID_ELEVATION    = 200;
	public static final int STAT_FLAT_SEA_LEVEL       = 201;

	
	

	// Errors
	public static final int STAT_GENERAL_FAIL         = 300;
	public static final int STAT_MEMORY_ERROR         = 301;
	public static final int STAT_INVALID_PARAMETER    = 302;
	public static final int STAT_NO_DATA_PACKAGE      = 303;
	public static final int STAT_INVALID_COLORING     = 304;
	public static final int STAT_CREATE_FILE_FAILED   = 305;
	public static final int STAT_TIFF_WRITE_ERROR     = 306;
	public static final int STAT_INVALID_XY_POINT     = 307;
	public static final int STAT_NOT_IMPLEMENTED      = 308;
	
	
	public static final int HILLSHADING_LIGHTEN       = 1000;
	public static final int HILLSHADING_DARKEN        = 1001;
	public static final int HILLSHADING_COMBINED      = 1002;
	public static final int HILLSHADING_NONE          = 1003;
	
	public static final int COLORING_TYPE_HYPSOMETRIC = 1050;
	public static final int COLORING_TYPE_GRAY        = 1051;
	
	
	public static final int BACKGROUND_WHITE          = 2000;
	public static final int BACKGROUND_BLACK          = 2001;
	public static final int BACKGROUND_BLUE           = 2002;
	public static final int BACKGROUND_TRANSPARENT    = 2003;
	
	public static final int PIXELTYPE_SIGNED_INT      = 3000;
	public static final int PIXELTYPE_UNSIGNED_INT    = 3001;
	
	public static final String PRECACHE_STRATEGY_NONE = "none";
	public static final String PRECACHE_STRATEGY_TILED = "tiled";
	public static final String PRECACHE_STRATEGY_FULL = "full";
}
