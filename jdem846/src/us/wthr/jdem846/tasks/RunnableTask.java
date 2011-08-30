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

public abstract class RunnableTask
{
	private boolean async = false;
	private boolean stoppable = false;
	private int id = -1;
	
	public RunnableTask()
	{
		
	}

	public abstract void run();
	
	
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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
