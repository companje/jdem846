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

public interface TaskStatusListener
{
	public void taskStarting(RunnableTask task);
	public void taskFailed(RunnableTask task, Throwable thrown);
	public void taskCompleted(RunnableTask task);
	public void taskCancelled(RunnableTask task);
	public void taskPaused(RunnableTask task);
	public void taskResumed(RunnableTask task);
}