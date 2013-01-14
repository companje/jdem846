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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Controller for managing the creation and cleanup of temporary files.
 * 
 * @author Kevin M. Gill
 *
 */
public class TempFiles
{
	private static Log log = Logging.getLog(TempFiles.class);
	
	public static File getTemporaryFile(String prefix) throws IOException
	{
		return getTemporaryFile(prefix, ".tmp");
	}
	
	public static File getTemporaryFile(String prefix, String suffix) throws IOException
	{
		File temp = File.createTempFile("jdem." + InstanceIdentifier.getInstanceId() + "." + prefix + ".", suffix, new File(JDem846Properties.getProperty("us.wthr.jdem846.temp")));
		return temp;
	}
	
	
	
	public static File getTemporaryFile(String prefix, String suffix, String copyFrom) throws Exception
	{
		File tempFile = getTemporaryFile(prefix, suffix);
		
		
		InputStream in = JDemResourceLoader.getAsInputStream(copyFrom);
		FileOutputStream out = new FileOutputStream(tempFile);
		
		byte[] buffer = new byte[1024];
		int len = 0;
		
		while((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();
		
		return tempFile;
	}
	
	public static void cleanUpTemporaryFiles()
	{
		File tempRoot = new File(getTemporaryRoot());
		log.info("Cleaning up temporary files in " + tempRoot.getAbsolutePath());
		
		File files[] = tempRoot.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name)
			{
				return name.startsWith("jdem." + InstanceIdentifier.getInstanceId() + ".");
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
		return JDem846Properties.getProperty("us.wthr.jdem846.temp");
	}
}
