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

/** Provides a simple means of reading primitive values from a byte array. 
 * Intentionally does little index bounds checking, so protection from IndexOutOfBoundsException, etc, would be
 * a good idea.
 * 
 * @author oracle
 *
 */
public class ByteArrayReader
{
	
	private ByteBuffer buffer;
	
	public ByteArrayReader(byte[] buffer)
	{
		this.buffer = ByteBuffer.wrap(buffer);
	}
	

	
	public byte[] getByteArray()
	{
		return buffer.array();
	}
	
	public int getNextInt()
	{
		return buffer.getInt();
	}
	
	public float getNextFloat()
	{
		return buffer.getFloat();
	}
	
	public short getNextShort()
	{
		return buffer.getShort();
	}
	
	
	public long getNextLong()
	{
		return buffer.getLong();
	}
	
	public double getNextDouble()
	{
		return buffer.getDouble();
	}
	
	public char getNextChar()
	{
		return buffer.getChar();
	}
	
	public byte getNextByte()
	{
		return buffer.get();
	}
	
	public byte[] getNextByteArray(int length)
	{
		byte[] bBuffer = new byte[length];
		for (int i = 0; i < length; i++) {
			bBuffer[i] = getNextByte();
		}
		return bBuffer;
	}
	
	public char[] getNextCharArray(int length)
	{
		char[] cBuffer = new char[length];
		for (int i = 0; i < length; i++) {
			cBuffer[i] = buffer.getChar();
		}
		return cBuffer;
	}
	
	public String getNextString(int length)
	{
		char[] cBuffer = getNextCharArray(length);
		String v = new String(cBuffer);
		return v;
	}
	
	
}
