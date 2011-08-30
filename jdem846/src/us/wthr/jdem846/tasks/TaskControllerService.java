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

import us.wthr.jdem846.AbstractLockableService;
import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Service for managing the execution of synchronous and asynchronous tasks. Synchronous tasks are executed
 * in order, one after another. Asynchronous tasks are delegated to threads and executed concurrently.
 * 
 * @author Kevin M. Gill
 */
@Service(name="us.wthr.jdem846.tasks.taskControllerService", enabled=true, deamon=true)
public class TaskControllerService extends AbstractLockableService
{
	private static Log log = Logging.getLog(TaskControllerService.class);
	
	private static TaskControllerService instance = null;
	
	private TaskContainer activeSynchronousTask = null;
	private TaskStatusListener activeSynchronousTaskListener = null;
	
	private List<TaskContainer> tasks = new LinkedList<TaskContainer>();
	
	/** Constructor.
	 * 
	 */
	public TaskControllerService()
	{
		activeSynchronousTaskListener = new TaskStatusListener() {
			public void taskCancelled(RunnableTask task)
			{
				
			}
			public void taskCompleted(RunnableTask task)
			{
				
			}
			public void taskFailed(RunnableTask task, Throwable thrown)
			{
				
			}
			public void taskStarting(RunnableTask task)
			{
				
			}
		};
		
		
		
	}
	
	
	@Initialize
	public void initialize()
	{
		log.info("Initializing task controller service");
		if (TaskControllerService.instance == null) {
			TaskControllerService.instance = this;
		}
	}
	
	
	@ServiceRuntime
	public void runtime()
	{
		log.info("Entering task controller service runtime routine");
		
		/*
		while(true) {
			
			log.info("Task controller service PING! -- Is Daemon: " + Thread.currentThread().isDaemon());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
		log.info("Leaving task controller service runtime routine");
	}
	
	@Destroy
	public void destroy()
	{
		log.info("Shutting down task controller service");
		TaskControllerService.instance = null;
	}
	
	/** Starts the next pending synchronous task.
	 * 
	 */
	protected void fireNextSynchronousTask()
	{
		
	}
	
	/** Adds a task to the end of the run queue.
	 * 
	 * @param task
	 * @param listener
	 */
	public static void addTask(RunnableTask task, TaskStatusListener listener)
	{
		
	}
	
	/** Adds a task listener to the container holding the task.
	 * 
	 * @param task
	 * @param listener
	 */
	public static void addTaskStatusListener(RunnableTask task, TaskStatusListener listener)
	{
		
	}
	
	/** Removes a task listener from the container holding the task.
	 * 
	 * @param task
	 * @param listener
	 */
	public static void removeTaskStatusListener(RunnableTask task, TaskStatusListener listener)
	{
		
	}
	
	/** Retrieves the currently active synchronous task.
	 * 
	 * @return The currently active synchronous task, or null if no task is currently running.
	 */
	public static RunnableTask getActiveSynchronousTask()
	{
		return null;
	}
	
	/** Retrieves the currently active synchronous and asynchronous tasks.
	 * 
	 * @return The currently active synchronous and asynchronous tasks. List will be empty if no tasks
	 * are currently running.
	 */
	public static List<RunnableTask> getActiveTasks()
	{
		return null;
	}
	
	/** Retrieves a list of pending (not yet executed) tasks.
	 * 
	 * @return
	 */
	public static List<RunnableTask> getPendingTasks()
	{
		return null;
	}
	
	/** Retrieves a list of all pending and active synchronous and asynchronous tasks.
	 * 
	 * @return
	 */
	public static List<RunnableTask> getTasks()
	{
		return null;
	}
	
	/** Requests the specified task be canceled mid-execution. It is up to the task whether it will/can 
	 * stop.
	 * @param task
	 */
	public void cancelTask(RunnableTask task)
	{
		
	}
	
	/** Retrieves the task container holding the specified task.
	 * 
	 * @param task
	 * @return The task container or null if not found.
	 */
	protected TaskContainer getTaskContainer(RunnableTask task)
	{
		for (TaskContainer taskContainer : tasks) {
			if (taskContainer.getRunnableTask() == task) { // Pointer address comparison
				return taskContainer;
			}
		}
		return null;
	}
}
