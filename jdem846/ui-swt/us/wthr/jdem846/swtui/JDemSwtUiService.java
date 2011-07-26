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

package us.wthr.jdem846.swtui;

import us.wthr.jdem846.AbstractLockableService;
import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

@Service(name="us.wthr.jdem846.ui.jdemswtuiservice", enabled=false)
public class JDemSwtUiService extends AbstractLockableService
{
	private static Log log = Logging.getLog(JDemSwtUiService.class);
	
	public JDemSwtUiService()
	{
		
	}
	
	
	@Initialize
	public void initialize()
	{
		log.info("JDemSwtUiService.initialize()");
		
	}
	
	
	@ServiceRuntime
	public void runtime()
	{
		log.info("JDemSwtUiService.runtime()");

		JDemShell shell = new JDemShell();
		shell.openAndWait();
		
		
	}
	
	@Destroy
	public void destroy()
	{
		log.info("JDemSwtUiService.destroy()");
		
	}
}
