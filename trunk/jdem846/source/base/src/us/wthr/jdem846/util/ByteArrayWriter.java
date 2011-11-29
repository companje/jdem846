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

package us.wthr.jdem846.util;

import java.nio.ByteBuffer;

/** Provides a simple means of writing primitive values to a byte array. 
 * Intentionally does little index bounds checking, so protection from IndexOutOfBoundsException, etc, would be
 * a good idea.
 * 
 * @author oracle
 *
 */
public class ByteArrayWriter
{
	
	private ByteBuffer buffer;
	
	public ByteArrayWriter(byte[] buffer)
	{
		this.buffer = ByteBuffer.wrap(buffer);
	}
	
	public ByteArrayWriter(int length)
	{
		this.buffer = ByteBuffer.allocate(length);
	}
	
	public byte[] getByteArray()
	{
		return buffer.array();
	}
	
	public void putInt(int value)
	{
		buffer.putInt(value);
	}
	
	public void putFloat(float value)
	{
		buffer.putFloat(value);
	}
	
	public void putLong(long value)
	{
		buffer.putLong(value);
	}
	
	public void putDouble(double value)
	{
		buffer.putDouble(value);
	}
	
	public void putByte(byte value)
	{
		buffer.put(value);
	}
	
	public void putByteArray(byte[] values)
	{
		for (int i = 0; i < values.length; i++) {
			putByte(values[i]);
		}
	}
	
	public void putChar(char value)
	{
		buffer.putChar(value);
	}
	
	public void putCharArray(char[] values)
	{
		for (int i = 0; i < values.length; i++) {
			putChar(values[i]);
		}
	}
	
	public void putString(String value, int padTo)
	{
		if (value.length() > 0)
			putCharArray(value.toCharArray());
		
		if (value.length() < padTo) {
			for (int i = value.length(); i < padTo; i++) {
				putChar((char)0x0);
			}
		}
	}
	
	public int position()
	{
		return buffer.position();
	}
	
}
