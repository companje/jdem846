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

package us.wthr.jdem846.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log
{
	private Logger log;
	private Class<?> clazz;
	
	private static final Level DEBUG = Level.ALL;
	
	protected Log(Class<?> clazz)
	{
		this.clazz = clazz;
		log = Logger.getLogger(clazz.getCanonicalName());
	}


	protected void log(Level level, String message)
	{
		log.logp(level, clazz.getCanonicalName(), null, message);
	}
	
	protected void log(Level level, String message, Object ... params)
	{
		log.logp(level, clazz.getCanonicalName(), null, message, params);
	}
	
	protected void log(Level level, String message, Throwable thrown)
	{
		log.logp(level, clazz.getCanonicalName(), null, message, thrown);
	}
	
	
	public void debug(String message, Throwable ex)
	{
		this.log(DEBUG, message, ex);
	}
	
	public void debug(String message, Object...params)
	{
		this.log(DEBUG, message, params);
	}
	
	public void debug(String message)
	{
		this.log(DEBUG, message);
	}	
	
	
	
	public void info(String message, Throwable ex)
	{
		this.log(Level.INFO, message, ex);
	}
	
	public void info(String message, Object...params)
	{
		this.log(Level.INFO, message, params);
	}
	
	public void info(String message)
	{
		this.log(Level.INFO, message);
	}	
	
	
	
	public void warn(String message, Throwable ex)
	{
		this.log(Level.WARNING, message, ex);
	}
	
	public void warn(String message, Object...params)
	{
		this.log(Level.WARNING, message, params);
	}
	
	public void warn(String message)
	{
		this.log(Level.WARNING, message);
	}
	
	
	
	
	public void error(String message, Throwable ex)
	{
		this.log(Level.SEVERE, message, ex);
	}
	
	public void error(String message, Object...params)
	{
		this.log(Level.SEVERE, message, params);
	}
	
	public void error(String message)
	{
		this.log(Level.SEVERE, message);
	}
	
	
	
}
