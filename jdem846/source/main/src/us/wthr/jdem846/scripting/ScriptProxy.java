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

package us.wthr.jdem846.scripting;

import us.wthr.jdem846.ModelContext;
import us.wthr.jdem846.canvas.ModelCanvas;
import us.wthr.jdem846.model.ModelProcessContainer;

/** Describes a proxy interface for modeling engines to call user-provided scripts
 * 
 * @author Kevin M. Gill
 *
 */
public interface ScriptProxy
{
	
	public void initialize(ModelContext modelContext);
	public void destroy(ModelContext modelContext);
	
	public void onModelBefore(ModelContext modelContext);
	public void onModelAfter(ModelContext modelContext);
	
	public void onProcessBefore(ModelContext modelContext, ModelProcessContainer modelProcessContainer);
	public void onProcessAfter(ModelContext modelContext, ModelProcessContainer modelProcessContainer);

	public Object onGetElevationBefore(ModelContext modelContext, double latitude, double longitude);
	public Object onGetElevationAfter(ModelContext modelContext, double latitude, double longitude, double elevation);

	public void onGetPointColor(ModelContext modelContext, double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color);
	// TODO: Add copy()
	
	// getGradientColor
	// onException
}
