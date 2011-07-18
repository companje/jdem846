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

public class ByteConversions
{
	public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LSBFIRST;
	
	protected ByteConversions()
	{
		
	}
	
	public static float bytesToFloat(byte[] bytes)
	{
		return bytesToFloat(bytes,  DEFAULT_BYTE_ORDER);
	}
	
	public static float bytesToFloat(byte[] bytes, ByteOrder byteOrder)
	{
		return bytesToFloat(bytes[0], bytes[1], bytes[2], bytes[3], byteOrder);
	}
	
	public static float bytesToFloat(byte b00, byte b01, byte b10, byte b11, ByteOrder byteOrder)
	{
		int intBits = 0;
		if (byteOrder == ByteOrder.MSBFIRST) {
			intBits = ((b00 & 0xFF) << 24) |
				((b01 & 0xFF) << 16) |
				((b10 & 0xFF) << 8) |
				(b11 & 0xFF);
		} else if (byteOrder == ByteOrder.LSBFIRST) {
			intBits = ((b11 & 0xFF) << 24) |
				((b10 & 0xFF) << 16) |
				((b01 & 0xFF) << 8) |
				(b00 & 0xFF);
		} else if (byteOrder == ByteOrder.INTEL_OR_MOTOROLA) {
			intBits = ((b10 & 0xFF) << 24) |
				((b11 & 0xFF) << 16) |
				((b00 & 0xFF) << 8) |
				(b01 & 0xFF);
		}
		
		return Float.intBitsToFloat(intBits);

	}
	
	public static String toHex(byte b00)
	{
		String s = Integer.toString(b00 & 0xFF, 16).toUpperCase();
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
	
	public static byte[] floatToBytes(float value)
	{
		return floatToBytes(value, DEFAULT_BYTE_ORDER);
	}
	
	public static byte[] floatToBytes(float value, ByteOrder byteOrder)
	{
		int bits = Float.floatToIntBits(value);

		byte[] buffer = null;
		
		if (byteOrder == ByteOrder.MSBFIRST) {
			buffer = new byte[] {
                (byte)(bits >>> 24),
                (byte)(bits >>> 16),
                (byte)(bits >>> 8),
                (byte)bits};
		} else {
			buffer = new byte[] {
	                (byte)(bits),
	                (byte)(bits >>> 8),
	                (byte)(bits >>> 16),
	                (byte)(bits >>> 24)};
		}
		
		return buffer;
	}
	
	public static int bytesToInt(byte[] bytes)
	{
		return bytesToInt(bytes, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	public static int bytesToInt(byte[] bytes, ByteOrder byteOrder)
	{
		/*
		int intBits = 0;
		if (byteOrder == ByteOrder.MSBFIRST) {
			for(int i = 0; i < 4; i++){      
				intBits <<= 8;  
				intBits ^= (int)bytes[i] & 0xFF;      
			}  
		} else if (byteOrder == ByteOrder.LSBFIRST) {
			for(int i = 3; i >= 0; i--){      
				intBits <<= 8;  
				intBits ^= (int)bytes[i] & 0xFF;      
			}  
		}
		return intBits;
		*/
		
		return bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3], byteOrder);
	}
	
	public static int bytesToInt(byte b00, byte b01, byte b10, byte b11)
	{
		return bytesToInt(b00, b01, b10, b11, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	public static int byteToInt(byte b00)
	{
		int intBits = 0;
		intBits = (b00 & 0xFF);
		return intBits;
	}
	
	public static int bytesToInt(byte b00, byte b01, byte b10, byte b11, ByteOrder byteOrder)
	{
		int intBits = 0;
		if (byteOrder == ByteOrder.MSBFIRST) {
			intBits = ((b11 & 0xFF) << 24) |
				((b10 & 0xFF) << 16) |
				((b01 & 0xFF) << 8) |
				(b00 & 0xFF);
		} else if (byteOrder == ByteOrder.LSBFIRST) {
			intBits = ((b00 & 0xFF) << 24) |
				((b01 & 0xFF) << 16) |
				((b10 & 0xFF) << 8) |
				(b11 & 0xFF);
		}
		return intBits;
	}
	
	public static double bytesToDouble(byte[] bytes)
	{
		return bytesToDouble(bytes, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	
	public static double bytesToDouble(byte[] bytes, ByteOrder byteOrder)
	{
		return Double.longBitsToDouble(bytesToLong(bytes, byteOrder));
	}
	
	
	public static long bytesToLong(byte[] bytes)
	{
		return bytesToLong(bytes, ByteConversions.DEFAULT_BYTE_ORDER);
	}
	

	public static long bytesToLong(byte[] bytes, ByteOrder byteOrder)
	{
		long longBits = 0;
		
		if (byteOrder == ByteOrder.MSBFIRST) {
			for(int i =0; i < 8; i++){      
				longBits <<= 8;  
				longBits ^= (long)bytes[i] & 0xFF;      
			}  
		} else if (byteOrder == ByteOrder.LSBFIRST) {
			for(int i = 7; i >= 0; i--){      
				longBits <<= 8;  
				longBits ^= (long)bytes[i] & 0xFF;      
			}  
		}
		return longBits;

	}	
	
	
}
