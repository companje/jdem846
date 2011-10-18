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

public class DemPoint 
{
	
	private double backRightElevation;
	private double backLeftElevation;
	private double frontRightElevation;
	private double frontLeftElevation;
	private double middleElevation;
	private int condition;
	
	public DemPoint()
	{
		
	}

	
	
	public int getCondition() 
	{
		return condition;
	}



	public void setCondition(int condition) 
	{
		this.condition = condition;
	}



	public double getBackRightElevation()
	{
		return backRightElevation;
	}

	public void setBackRightElevation(double backRightElevation)
	{
		this.backRightElevation = backRightElevation;
	}

	public double getBackLeftElevation()
	{
		return backLeftElevation;
	}

	public void setBackLeftElevation(double backLeftElevation) 
	{
		this.backLeftElevation = backLeftElevation;
	}

	public double getFrontRightElevation() 
	{
		return frontRightElevation;
	}

	public void setFrontRightElevation(double frontRightElevation) 
	{
		this.frontRightElevation = frontRightElevation;
	}

	public double getFrontLeftElevation() 
	{
		return frontLeftElevation;
	}

	public void setFrontLeftElevation(double frontLeftElevation)
	{
		this.frontLeftElevation = frontLeftElevation;
	}

	public double getMiddleElevation() 
	{
		return middleElevation;
	}

	public void setMiddleElevation(double middleElevation) 
	{
		this.middleElevation = middleElevation;
	}
	
	
	
	
	
	
}
