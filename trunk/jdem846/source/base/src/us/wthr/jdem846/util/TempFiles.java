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
import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.JDemResourceLoader;
import us.wthr.jdem846.io.LocalFile;
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
	
	private static List<LocalFile> cleanUpList = new LinkedList<LocalFile>();
	
	public static LocalFile getTemporaryFile(String prefix) throws IOException
	{
		return getTemporaryFile(prefix, ".tmp");
	}
	
	public static LocalFile getTemporaryFile(String prefix, String suffix) throws IOException
	{
		File temp = File.createTempFile("jdem." + InstanceIdentifier.getInstanceId() + "." + prefix + ".", suffix, new File(JDem846Properties.getProperty("us.wthr.jdem846.general.temp")));
		return new LocalFile(temp, true);
	}
	
	
	
	public static LocalFile getTemporaryFile(String prefix, String suffix, String copyFrom) throws Exception
	{
		LocalFile tempFile = getTemporaryFile(prefix, suffix);
		
		
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
	
	public static void releaseFile(String path)
	{
		LocalFile f = new LocalFile(path);
		releaseFile(f);
	}
	
	public static void releaseFile(LocalFile f)
	{
		synchronized(cleanUpList) {
			if (!cleanUpList.contains(f) && f.exists()) {
				cleanUpList.add(f);
			}
		}
	}
	
	public static void cleanUpReleasedFiles()
	{
		synchronized(cleanUpList) {
			
			List<File> removeList = new LinkedList<File>();
			
			for (File file : cleanUpList) {
				if (file.delete()) {
					removeList.add(file);
					log.info("Deleted file " + file.getAbsolutePath());
				}
			}
			
			for (File file : removeList) {
				cleanUpList.remove(file);
			}
		}
	}
	
	protected static int getReleaseFilesCount()
	{
		synchronized (cleanUpList) {
			return cleanUpList.size();
		}
	}
	
	public static void cleanUpTemporaryFiles(boolean wait)
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
			releaseFile(new LocalFile(file));
		}
		//	if (file.exists()) {
		//		file.deleteOnExit();
		//		log.info("Cleaning file: " + file.getAbsolutePath());
		//	}
		//}
		
		
		long waitForAllToBeDeletedMillis = 20000;
		long start = System.currentTimeMillis();
		
		while(getReleaseFilesCount() > 0 && (System.currentTimeMillis() - start < waitForAllToBeDeletedMillis)) {
			cleanUpReleasedFiles();
		}
		
		
	}
	
	public static String getTemporaryRoot()
	{
		return JDem846Properties.getProperty("us.wthr.jdem846.general.temp");
	}
}
