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

import us.wthr.jdem846.ByteOrder;
import junit.framework.TestCase;

public class ByteConversionsTest extends TestCase
{

	public void testDefault() 
	{
		byte[] bytes = ByteConversions.floatToBytes(300.0f);
		
		if (ByteConversions.DEFAULT_BYTE_ORDER == ByteOrder.LSBFIRST) {
			assert bytes[3] == (byte)0x43;
			assert bytes[2] == (byte)-106;
			assert bytes[1] == (byte)0x0;
			assert bytes[0] == (byte)0x0;
		} else if (ByteConversions.DEFAULT_BYTE_ORDER == ByteOrder.MSBFIRST) {
			assert bytes[0] == (byte)0x43;
			assert bytes[1] == (byte)-106;
			assert bytes[2] == (byte)0x0;
			assert bytes[3] == (byte)0x0;
		} else {
			assert false;
		}

		float value = ByteConversions.bytesToFloat(bytes);
		assert value == 300.0f;
		
	}
	
	public void testMsbFirst()
	{
		byte[] bytes = ByteConversions.floatToBytes(300.0f, ByteOrder.MSBFIRST);
		
		assert bytes[0] == (byte)0x43;
		assert bytes[1] == (byte)-106;
		assert bytes[2] == (byte)0x0;
		assert bytes[3] == (byte)0x0;

		float value = ByteConversions.bytesToFloat(bytes, ByteOrder.MSBFIRST);
		assert value == 300.0f;
	}
  
	
	public void testLsbFirst()
	{
		byte[] bytes = ByteConversions.floatToBytes(300.0f, ByteOrder.LSBFIRST);
		
		assert bytes[3] == (byte)0x43;
		assert bytes[2] == (byte)-106;
		assert bytes[1] == (byte)0x0;
		assert bytes[0] == (byte)0x0;

		float value = ByteConversions.bytesToFloat(bytes, ByteOrder.LSBFIRST);
		assert value == 300.0f;
	}
}
