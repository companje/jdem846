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

import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.OnShutdown;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.util.TempFiles;

@Service(name="us.wthr.jdem846.tempfileservice", enabled=true)
public class TemporaryFileService extends AbstractLockableService
{
	
	private static Log log = Logging.getLog(TemporaryFileService.class);
	
	public TemporaryFileService()
	{
		
	}
	
	
	@Initialize
	public void initialize()
	{
		log.info("Initialize Temporary File Service");
	}
	
	
	@ServiceRuntime
	public void runtime()
	{
		log.info("Temporary File Service Runtime (short-lived)");

	}
	
	
	@Destroy
	public void destroy()
	{
		log.info("Destroying Temporary File Service");
	}
	
	@OnShutdown
	public void onShutdown()
	{
		log.info("Temporary File Service - On Shutdown");
		
		// Clean Up
		TempFiles.cleanUpTemporaryFiles();
	}
	
}
