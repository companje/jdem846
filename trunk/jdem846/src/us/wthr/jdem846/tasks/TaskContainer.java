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

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

public class TaskContainer
{
	private static Log log = Logging.getLog(TaskContainer.class);
	
	private List<TaskStatusListener> taskStatusListeners = new LinkedList<TaskStatusListener>();
	private RunnableTask task;
	private boolean running = false;
	private boolean cancelled = false;
	private boolean completed = false;
	
	public TaskContainer(RunnableTask task)
	{
		this.task = task;
	}
	
	public RunnableTask getRunnableTask()
	{
		return task;
	}
	
	public void runTask()
	{
		if (isCancelled()) {
			return;
		}
		
		fireTaskStartingListeners();
		
		try {
			
			task.run();
			
			
			if (isCancelled()) {
				fireTaskCancelledListeners();
			} else {
				fireTaskCompletedListeners();
			}
			
		} catch (Exception ex) {
			log.info("Failure in task #" + task.getId(), ex);
			fireTaskFailedListeners(ex);
		}
		
		
	}
	
	
	protected void setRunning(boolean running)
	{
		this.running = running;
	}
	
	public boolean isRunning()
	{
		return running;
	}
	
	protected void setCompleted(boolean completed)
	{
		this.completed = completed;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
	
	protected void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	
	protected void fireTaskStartingListeners()
	{
		for (TaskStatusListener listener : taskStatusListeners) {
			listener.taskStarting(task);
		}
	}
	
	protected void fireTaskFailedListeners(Throwable thrown)
	{
		for (TaskStatusListener listener : taskStatusListeners) {
			listener.taskFailed(task, thrown);
		}
	}
	
	protected void fireTaskCancelledListeners()
	{
		for (TaskStatusListener listener : taskStatusListeners) {
			listener.taskCancelled(task);
		}
	}
	
	protected void fireTaskCompletedListeners()
	{
		for (TaskStatusListener listener : taskStatusListeners) {
			listener.taskCompleted(task);
		}
	}
	
	
	
	public void addTaskStatusListener(TaskStatusListener listener)
	{
		taskStatusListeners.add(listener);
	}
	
	public boolean removeTaskStatusListener(TaskStatusListener listener)
	{
		return taskStatusListeners.remove(listener);
	}
	
}
