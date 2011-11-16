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

package us.wthr.jdem846.util;

import us.wthr.jdem846.exception.ResourceLoaderException;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@Deprecated
public class ResourcePointer
{
	private static Log log = Logging.getLog(ResourcePointer.class);
	
	private String scheme;
	private int schemeType;
	private String uri;
	
	public ResourcePointer(String url) throws ResourceLoaderException
	{
		if (url.indexOf("file://") == 0) {
			scheme = "file://";
			schemeType = ResourceLoader.SCHEME_FILE;
			uri = url.substring(7);
		} else if (url.indexOf("jar://") == 0) {
			scheme = "jar://";
			schemeType = ResourceLoader.SCHEME_JAR;
			uri = url.substring(6);
		} else {
			scheme = "file://";
			schemeType = ResourceLoader.SCHEME_FILE;
			uri = url;
		}
		
		log.info("ResoucePointer: " + scheme + ", " + uri);
		
	}

	public String getScheme()
	{
		return scheme;
	}

	public int getSchemeType()
	{
		return schemeType;
	}

	public String getUri()
	{
		return uri;
	}
}
