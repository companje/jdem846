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

package us.wthr.jdem846;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UtilitiesTestSuite extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for utility classes in us.wthr.jdem846");
		//$JUnit-BEGIN$
		
		suite.addTestSuite(us.wthr.jdem846.util.ByteConversionsTest.class);
		suite.addTestSuite(us.wthr.jdem846.scaling.FloatRasterTest.class);
		suite.addTestSuite(us.wthr.jdem846.scaling.ResizeDimensionsTest.class);
		suite.addTestSuite(us.wthr.jdem846.ModelOptionsTest.class);
		
		//$JUnit-END$
		return suite;
	}
}
