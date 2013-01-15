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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/**	A group of synchronously executed tasks.
 * 
 * @author Kevin M. Gill
 *
 */
public class TaskGroup extends Thread
{
	
	private static Log log = Logging.getLog(TaskGroup.class);
	
	private TaskContainer activeTask = null;
	private String taskGroupName;
	private TaskIdentifier identifier;
	private Queue<TaskContainer> tasks = new ConcurrentLinkedQueue<TaskContainer>();
	private boolean persistentThread = false;
	private long pollDelay = 500;

	private List<TaskGroupListener> taskGroupListeners = new LinkedList<TaskGroupListener>();
	
	/** Default constructor.
	 * 
	 */
	public TaskGroup()
	{
		this(null);
	}
	
	/** Constructor.
	 * 
	 * @param taskGroupName Name for task group. If null, a unique identifier will be used.
	 */
	public TaskGroup(String taskGroupName)
	{
		identifier = TaskIdentifier.generate();
		if (taskGroupName != null) {
			this.taskGroupName = taskGroupName;
		} else {
			this.taskGroupName = identifier.getId();
		}
		
		this.setName("us.wthr.jdem846.tasks.group."+this.taskGroupName);
		

		
	}

	/** Main task controller method
	 * 
	 */
	public void run()
	{
		log.info("Starting task group '" + identifier.getId() + "'");
		fireTaskGroupStartedListeners();
		
		while (tasks.size() > 0 || this.isPersistentThread()) {
			TaskContainer nextTask = null;

			nextTask = tasks.peek();

			
			if (nextTask != null) {
				activeTask = nextTask;
				
				fireTaskStartingListeners(activeTask.getRunnableTask());
				activeTask.runTask();
				
				tasks.remove(activeTask);
				activeTask = null;
			}
			
			try {
				Thread.sleep(this.pollDelay);
			} catch (InterruptedException ex) {
				log.warn("Interruption caught on thread group sleep delay: " + ex.getMessage(), ex);
			}
		}
		
		fireTaskGroupCompletedListeners();
		log.info("Leaving task group '" + identifier.getId() + "'");
	}
	

	
	/** Adds a task to the end of the run queue. 
	 * 
	 * @param task
	 * @param listener
	 * @return True if the task has been accepted into the queue.
	 */
	public boolean addTask(RunnableTask task, TaskStatusListener listener)
	{
		boolean result = this.tasks.add(new TaskContainer(task, listener));
		if (result) {
			fireTaskAddedListeners(task);
		}
		return result;

	}
	
	/** Adds a task listener to the container holding the task.
	 * 
	 * @param task
	 * @param listener
	 */
	public void addTaskStatusListener(RunnableTask task, TaskStatusListener listener)
	{
		TaskContainer taskContainer = this.getTaskContainer(task);
		if (taskContainer != null) {
			taskContainer.addTaskStatusListener(listener);
		}
	}
	
	/** Removes a task listener from the container holding the task.
	 * 
	 * @param task
	 * @param listener
	 */
	public boolean removeTaskStatusListener(RunnableTask task, TaskStatusListener listener)
	{
		TaskContainer taskContainer = this.getTaskContainer(task);
		if (taskContainer != null) {
			return taskContainer.removeTaskStatusListener(listener);
		} else {
			return false;
		}
	}
	
	
	/** Retrieves a list of all pending and active synchronous and asynchronous tasks.
	 * 
	 * @return
	 */
	public List<RunnableTask> getTasks()
	{
		List<RunnableTask> taskList = new LinkedList<RunnableTask>();
		for (TaskContainer taskContainer : tasks) {
			taskList.add(taskContainer.getRunnableTask());
		}
		return taskList;

	}
	
	/** Retrieves the currently active task or null if no task is active.
	 * 
	 * @return
	 */
	public RunnableTask getActiveTask()
	{
		return activeTask.getRunnableTask();
	}
	
	/** Requests the currently active task be canceled mid-execution. It is up to the task whether it will/can 
	 * stop.
	 * @param task
	 * @return True if the task was found and sent a cancellation signal.
	 */
	public boolean cancelActiveTask()
	{
		return cancelTask(activeTask);
	}
	
	public boolean pauseActiveTask()
	{
		return pauseTask(activeTask);
	}
	
	public boolean resumeActiveTask()
	{
		return resumeTask(activeTask);
	}
	
	/** Requests the specified task be canceled mid-execution. It is up to the task whether it will/can 
	 * stop.
	 * @param task
	 * @return True if the task was found and sent a cancellation signal.
	 */
	public boolean cancelTask(String id)
	{
		return cancelTask(getTaskContainer(id));
	}
	
	public boolean pauseTask(String id)
	{
		return pauseTask(getTaskContainer(id));
	}
	
	public boolean resumeTask(String id)
	{
		return resumeTask(getTaskContainer(id));
	}
	
	/** Requests the specified task be canceled mid-execution. It is up to the task whether it will/can 
	 * stop.
	 * @param task
	 * @return True if the task was found and sent a cancellation signal.
	 */
	public boolean cancelTask(RunnableTask task)
	{
		return cancelTask(getTaskContainer(task));
	}
	
	public boolean pauseTask(RunnableTask task)
	{
		return pauseTask(getTaskContainer(task));
	}
	
	public boolean resumeTask(RunnableTask task)
	{
		return resumeTask(getTaskContainer(task));
	}
	
	/** Requests the specified task be canceled mid-execution. It is up to the task whether it will/can 
	 * stop.
	 * @param task
	 * @return True if the task was found and sent a cancellation signal.
	 */
	protected boolean cancelTask(TaskContainer taskContainer)
	{
		if (taskContainer != null) {
			taskContainer.cancel();
			this.fireTaskCancelledListeners(taskContainer.getRunnableTask());
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean pauseTask(TaskContainer taskContainer)
	{
		if (taskContainer != null) {
			taskContainer.pause();
			this.fireTaskPausedListeners(taskContainer.getRunnableTask());
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean resumeTask(TaskContainer taskContainer)
	{
		if (taskContainer != null) {
			taskContainer.resume();
			this.fireTaskResumedListeners(taskContainer.getRunnableTask());
			return true;
		} else {
			return false;
		}
	}
	
	/** Requests that all remaining active and pending tasks are cancelled. It is up to the task 
	 * whether it will/can stop.
	 * 
	 */
	public void cancelRemainingTasks()
	{
		for (TaskContainer taskContainer : tasks) {
			cancelTask(taskContainer);
		}
		this.fireAllRemainingTasksCancelledListeners();
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
	
	/** Retrieves the task container with the specified identifier string.
	 * 
	 * @param task
	 * @return The task container or null if not found.
	 */
	protected TaskContainer getTaskContainer(String id)
	{
		for (TaskContainer taskContainer : tasks) {
			if (taskContainer.getRunnableTask().getIdentifier().equals(id)) { // TaskIdentifier allows for String comparison
				return taskContainer;
			}
		}
		return null;
	}
	
	
	public boolean containsTask(String id)
	{
		return (getTaskContainer(id) != null);
	}
	
	public boolean containsTask(RunnableTask task)
	{
		return (getTaskContainer(task) != null);
	}
	
	public long getPollDelay()
	{
		return pollDelay;
	}

	/** Sets the delay between task completion polling.
	 * 
	 * @param pollDelay The delay in milliseconds.
	 */
	public void setPollDelay(long pollDelay)
	{
		this.pollDelay = pollDelay;
	}

	public boolean isPersistentThread()
	{
		return persistentThread;
	}

	/** Sets whether the task group will stay alive when the task queue becomes empty (persistent is true) 
	 * or exits (persistent is false)
	 * 
	 * @param persistentThread
	 */
	public void setPersistentThread(boolean persistentThread)
	{
		this.persistentThread = persistentThread;
	}

	public String getTaskGroupName()
	{
		return taskGroupName;
	}

	public void setTaskGroupName(String taskGroupName)
	{
		this.taskGroupName = taskGroupName;
	}

	public TaskIdentifier getIdentifier()
	{
		return identifier;
	}

	protected void fireTaskGroupStartedListeners()
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskGroupStarted(this);
		}
	}
	
	
	protected void fireTaskAddedListeners(RunnableTask task)
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskAdded(this, task);
		}
	}
	
	
	protected void fireTaskCancelledListeners(RunnableTask task)
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskCancelled(this, task);
		}
	}
	
	protected void fireTaskPausedListeners(RunnableTask task)
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskPaused(this, task);
		}
	}
	
	protected void fireTaskResumedListeners(RunnableTask task)
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskResumed(this, task);
		}
	}
	
	protected void fireAllRemainingTasksCancelledListeners()
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.allRemainingTasksCancelled(this);
		}
	}
	
	protected void fireTaskGroupCompletedListeners()
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskGroupCompleted(this);
		}
	}
	
	protected void fireTaskStartingListeners(RunnableTask task)
	{
		for (TaskGroupListener listener : taskGroupListeners) {
			listener.taskStarting(this, task);
		}
	}
	
	public void addTaskGroupListener(TaskGroupListener listener)
	{
		taskGroupListeners.add(listener);
	}
	
	public boolean removeTaskGroupListener(TaskGroupListener listener)
	{
		return taskGroupListeners.remove(listener);
	}
	
}
