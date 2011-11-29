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

package us.wthr.jdem846.tasks;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** A RunnableTask extending class contains logic which is to be executed within a TaskGroup by the 
 * task controller service.
 * 
 * @author Kevin M. Gill
 * 
 * @see TaskGroup
 * @see TaskStatusListener
 * @see TaskControllerService
 */
public abstract class RunnableTask
{
	private static Log log = Logging.getLog(RunnableTask.class);
	
	private boolean async = false;
	private boolean stoppable = false;
	private TaskIdentifier identifier = TaskIdentifier.generate();
	private String name;
	
	
	public RunnableTask()
	{
		this(null);
	}
	
	public RunnableTask(String name)
	{
		if (name == null) {
			this.name = identifier.getId();
		} else {
			this.name = name;
		}
	}

	public abstract void run() throws Exception;
	
	public void cancel()
	{
		log.info("Default cancel signal caught within Runnable Task");
	}
	
	public void pause()
	{
		log.info("Default pause signal caught within Runnable Task");
	}
	
	public void resume()
	{
		log.info("Default resume signal caught within Runnable Task");
	}
	
	public String getName()
	{
		return name;
	}

	protected void setName(String name)
	{
		this.name = name;
	}

	public TaskIdentifier getIdentifier()
	{
		return identifier;
	}

	public boolean isAsync()
	{
		return async;
	}

	public void setAsync(boolean async)
	{
		this.async = async;
	}

	public boolean isStoppable()
	{
		return stoppable;
	}

	public void setStoppable(boolean stoppable)
	{
		this.stoppable = stoppable;
	}
	
	
	
}
