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
import us.wthr.jdem846.exception.ScriptingException;
import us.wthr.jdem846.graphics.GraphicsRenderer;
import us.wthr.jdem846.graphics.View;
import us.wthr.jdem846.model.processing.util.LightingValues;

/** Describes a proxy interface for modeling engines to call user-provided scripts
 * 
 * @author Kevin M. Gill
 *
 */
public interface ScriptProxy
{
	public void setProperty(String name, Object value) throws ScriptingException;
	public void setModelContext(ModelContext modelContext) throws ScriptingException;
	
	public void initialize() throws ScriptingException;
	public void destroy() throws ScriptingException;
	
	public void onProcessBefore() throws ScriptingException;
	public void onProcessAfter() throws ScriptingException;

	public Object onGetElevationBefore(double latitude, double longitude) throws ScriptingException;
	public Object onGetElevationAfter(double latitude, double longitude, double elevation) throws ScriptingException;
	
	public void preRender(GraphicsRenderer renderer, View view) throws ScriptingException;
	public void postRender(GraphicsRenderer renderer, View view) throws ScriptingException;
	
	public void onGetPointColor(double latitude, double longitude, double elevation, double elevationMinimum, double elevationMaximum, int[] color) throws ScriptingException;
	
	public void onLightLevels(double latitude, double longitude, double elevation, LightingValues lightingValues) throws ScriptingException;
	
	// TODO: Add copy()
	
	// getGradientColor
	// onException
}
