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

import java.util.UUID;

public class TaskIdentifier
{
	
	private String id = null;
	
	private TaskIdentifier(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return id;
	}
	
	public static TaskIdentifier generate()
	{
		return new TaskIdentifier(UUID.randomUUID().toString());
	}
	
	public String toString()
	{
		return this.id;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/** Determines object equality. If the other object is a TaskIdentifier, then the respective id strings
	 * are compared. If the other object is a string, then it is directly compared to id of the TaskIdentifier
	 * instance.
	 * 
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (obj instanceof String) {
			String sObj = (String) obj;
			return this.id.equals(sObj);
		}
		
		if (getClass() != obj.getClass())
			return false;
		TaskIdentifier other = (TaskIdentifier) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
