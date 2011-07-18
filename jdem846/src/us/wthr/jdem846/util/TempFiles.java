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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class TempFiles
{
	private static Log log = Logging.getLog(TempFiles.class);
	
	public static File getTemporaryFile(String subPrefix) throws IOException
	{
		File temp = File.createTempFile("jdem." + subPrefix + ".", ".tmp", new File(System.getProperty("java.io.tmpdir")));
		return temp;
	}
	
	public static void cleanUpTemporaryFiles()
	{
		File tempRoot = new File(getTemporaryRoot());
		log.info("Cleaning up temporary files in " + tempRoot.getAbsolutePath());
		
		File files[] = tempRoot.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name)
			{
				return name.startsWith("jdem.");
			}
		});
		
		for (File file : files) {
			if (file.exists()) {
				file.deleteOnExit();
				log.info("Cleaning file: " + file.getAbsolutePath());
			}
		}
	}
	
	public static String getTemporaryRoot()
	{
		return System.getProperty("java.io.tmpdir");
	}
}
