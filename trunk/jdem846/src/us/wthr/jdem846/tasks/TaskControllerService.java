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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import us.wthr.jdem846.AbstractLockableService;
import us.wthr.jdem846.annotations.Destroy;
import us.wthr.jdem846.annotations.Initialize;
import us.wthr.jdem846.annotations.OnShutdown;
import us.wthr.jdem846.annotations.Service;
import us.wthr.jdem846.annotations.ServiceRuntime;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

/** Service for managing the execution of synchronous and asynchronous tasks. Synchronous tasks are executed
 * in order, one after another. Asynchronous tasks are delegated to threads and executed concurrently.
 * 
 * Note: While some tasks may be synchronous, this is in relation to the task service thread. These
 * tasks are executed asynchronously relative to the thread supplying the task.
 * 
 * @author Kevin M. Gill
 */
@Service(name="us.wthr.jdem846.tasks.taskControllerService", enabled=true, deamon=true)
public class TaskControllerService extends AbstractLockableService
{
	private static Log log = Logging.getLog(TaskControllerService.class);
	
	private static TaskControllerService instance = null;

	private TaskGroup defaultTaskGroup;
	private Map<String, TaskGroup> taskGroups = new HashMap<String, TaskGroup>();
	
	private List<TaskControllerListener> taskControllerListeners = new LinkedList<TaskControllerListener>();
	
	/** Constructor.
	 * 
	 */
	public TaskControllerService()
	{

		
		
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
		
		defaultTaskGroup = new TaskGroup("DefaultTaskGroup");
		defaultTaskGroup.setPersistentThread(true);
		defaultTaskGroup.setDaemon(true);
		defaultTaskGroup.start();
		
		while (this.isLocked()) {
			
			cleanUpInactiveTaskGroups();
			
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		log.info("Leaving task controller service runtime routine");
	}
	
	
	public void cleanUpInactiveTaskGroups()
	{
		List<TaskGroup> groups = getTaskGroups();
		for (TaskGroup group : groups) {
			if (!group.isAlive()) {
				log.info("Cleaning up inactive task group '" + group.getIdentifier() + "'");
				
				taskGroups.remove(group.getIdentifier().getId());
			}
		}
		
	}
	
	@OnShutdown
	public void onShutdown()
	{
		log.info("Task controller service on shutdown");
		//defaultTaskGroup.setPersistentThread(false);
		//defaultTaskGroup.cancelRemainingTasks();
		
		
		List<TaskGroup> taskGroups = getTaskGroups();
		for (TaskGroup taskGroup : taskGroups) {
			taskGroup.setPersistentThread(false);
			taskGroup.cancelRemainingTasks();
		}
		
		this.setLocked(false);
		
	}
	
	@Destroy
	public void destroy()
	{
		log.info("Shutting down task controller service");
		TaskControllerService.instance = null;
	}
	

	
	/** Adds a task to the end of the run queue.
	 * 
	 * @param task
	 * @param listener
	 */
	public static void addTask(RunnableTask task, TaskStatusListener listener)
	{
		TaskControllerService.instance.defaultTaskGroup.addTask(task, listener);
	}
	
	/** Adds a task group to the task controller and starts it's execution thread.
	 * 
	 * @param taskGroup
	 */
	public static void addTaskGroup(TaskGroup taskGroup) 
	{
		TaskControllerService.addTaskGroup(taskGroup, true);
	}
	
	/** Adds a task group to the task controller and starts it's execution thread if 'start' is true.
	 * 
	 * @param taskGroup A new task group
	 * @param start Will start the task group thread if true.
	 */
	public static void addTaskGroup(TaskGroup taskGroup, boolean start) 
	{
		TaskControllerService.instance.taskGroups.put(taskGroup.getIdentifier().getId(), taskGroup);
		if (start) {
			//taskGroup.setPriority(Thread.MAX_PRIORITY);
			taskGroup.start();
		}
		TaskControllerService.instance.fireTaskGroupAddedListeners(taskGroup);
	}
	
	/** Adds a task listener to the container holding the task.
	 * 
	 * @param task
	 * @param listener
	 */
	public static boolean addTaskStatusListener(RunnableTask task, TaskStatusListener listener)
	{
		TaskGroup group = TaskControllerService.getTaskGroupContainingTask(task);
		if (group != null) {
			group.addTaskStatusListener(task, listener);
			return true;
		} else {
			return false;
		}
	}
	
	/** Removes a task listener from the container holding the task.
	 * 
	 * @param task
	 * @param listener
	 */
	public static boolean removeTaskStatusListener(RunnableTask task, TaskStatusListener listener)
	{
		TaskGroup group = TaskControllerService.getTaskGroupContainingTask(task);
		if (group != null) {
			return group.removeTaskStatusListener(task, listener);
		} else {
			return false;
		}
	}
	
	/** Retrieves a list of all active task groups.
	 * 
	 * @return
	 */
	public static List<TaskGroup> getTaskGroups()
	{
		List<TaskGroup> groupList = new LinkedList<TaskGroup>();
		groupList.add(TaskControllerService.instance.defaultTaskGroup);
		
		synchronized (TaskControllerService.instance.taskGroups) {
			for (String key : TaskControllerService.instance.taskGroups.keySet()) {
				groupList.add(TaskControllerService.instance.taskGroups.get(key));
			}
		}
		
		return groupList;
	}
	
	
	/** Requests the specified task be canceled mid-execution. It is up to the task whether it will/can 
	 * stop.
	 * @param task
	 */
	public static boolean cancelTask(RunnableTask task)
	{
		TaskGroup group = TaskControllerService.getTaskGroupContainingTask(task);
		if (group != null) {
			boolean result = group.cancelTask(task);
			if (result) {
				TaskControllerService.instance.fireTaskCancelledListeners(task);
			}
			return result;
		} else {
			return false;
		}
	}
	
	public static boolean pauseTask(RunnableTask task)
	{
		TaskGroup group = TaskControllerService.getTaskGroupContainingTask(task);
		if (group != null) {
			boolean result = group.pauseTask(task);
			if (result) {
				TaskControllerService.instance.fireTaskPausedListeners(task);
			}
			return result;
		} else {
			return false;
		}
	}
	
	public static boolean resumeTask(RunnableTask task)
	{
		TaskGroup group = TaskControllerService.getTaskGroupContainingTask(task);
		if (group != null) {
			boolean result = group.resumeTask(task);
			if (result) {
				TaskControllerService.instance.fireTaskResumedListeners(task);
			}
			return result;
		} else {
			return false;
		}
	}
	
	/** Retrieves the task group containing the specified task. 
	 * 
	 * @param task
	 * @return The task group containing the specified task, or null if none is found.
	 */
	public static TaskGroup getTaskGroupContainingTask(RunnableTask task)
	{
		List<TaskGroup> groups = TaskControllerService.getTaskGroups();
		
		for (TaskGroup group : groups) {
			if (group.containsTask(task))
				return group;
		}
		
		return null;
	}
	
	
	public static TaskGroup getTaskGroup(String identifier)
	{
		return TaskControllerService.instance.taskGroups.get(identifier);
	}
	
	
	/*
	 * public void taskAdded(RunnableTask task);
	public void taskGroupAdded(TaskGroup taskGroup);
	public void taskCancelled(RunnableTask task);
	 */
	
	protected void fireTaskAddedListeners(RunnableTask task)
	{
		for (TaskControllerListener listener : taskControllerListeners) {
			listener.taskAdded(task);
		}
	}
	
	protected void fireTaskGroupAddedListeners(TaskGroup taskGroup)
	{
		for (TaskControllerListener listener : taskControllerListeners) {
			listener.taskGroupAdded(taskGroup);
		}
	}
	
	protected void fireTaskCancelledListeners(RunnableTask task)
	{
		for (TaskControllerListener listener : taskControllerListeners) {
			listener.taskCancelled(task);
		}
	}
	
	protected void fireTaskPausedListeners(RunnableTask task)
	{
		for (TaskControllerListener listener : taskControllerListeners) {
			listener.taskPaused(task);
		}
	}
	
	protected void fireTaskResumedListeners(RunnableTask task)
	{
		for (TaskControllerListener listener : taskControllerListeners) {
			listener.taskResumed(task);
		}
	}
	
	public static void addDefaultTaskGroupListener(TaskGroupListener listener)
	{
		TaskControllerService.instance.defaultTaskGroup.addTaskGroupListener(listener);
	}
	
	public static void addTaskControllerListener(TaskControllerListener listener)
	{
		TaskControllerService.instance.taskControllerListeners.add(listener);
	}
	
	public static boolean removeTaskControllerListener(TaskControllerListener listener)
	{
		return TaskControllerService.instance.taskControllerListeners.remove(listener);
	}
	
}
