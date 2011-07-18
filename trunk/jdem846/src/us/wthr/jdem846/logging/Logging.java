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

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;


public class Logging
{
	private static Formatter defaultFormatter = new SimpleEntryFormatter();
	
	static {
		
		Logger log = Logger.getLogger("");
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(defaultFormatter);
		
		Handler handlers[] = log.getHandlers();
		for (Handler handler : handlers) {
			log.removeHandler(handler);
		}
		
		log.addHandler(consoleHandler);
	}
	
	public static void addHandler(Handler handler)
	{
		Logger log = Logger.getLogger("");
		handler.setFormatter(defaultFormatter);
		log.addHandler(handler);
	}
	
	
	public static Log getLog(Class<?> clazz)
	{
		return new Log(clazz);
	}
}
