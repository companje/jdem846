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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/** Provides a simple means of log formatting.
 * 
 * @author Kevin M. Gill
 *
 */
public class SimpleEntryFormatter extends Formatter
{

	/** Provides simple formatting to the log record.
	 * 
	 */
	@Override
	public String format(LogRecord record)
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		String date = getFormattedDate(record.getMillis());
		String clazzName = record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".")+1);
		writer.printf("%s %8s %s %s\n", date, record.getLevel(), clazzName, record.getMessage());
		
		if (record.getThrown() != null) {
			Throwable thrown = record.getThrown();
			thrown.printStackTrace(writer);
		}
		
		return sw.toString();
	}

	
	/**
	 * 
	 * @param millis
	 * @return A standard formatted date string
	 */
	protected static String getFormattedDate(long millis)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS z");
		Date date = new Date(millis);
		return sdf.format(date);
	}
	
}
