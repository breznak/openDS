/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2016 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.input;

import com.jme3.input.controls.AnalogListener;

import eu.opends.main.DriveAnalyzer;

/**
 * 
 * @author Rafael Math
 */
public class DriveAnalyzerAnalogListener implements AnalogListener 
{
	DriveAnalyzer analyzer;
	
	public DriveAnalyzerAnalogListener(DriveAnalyzer analyzer) 
	{
		this.analyzer = analyzer;
	}

	@Override
	public void onAnalog(String binding, float value, float tpf) 
	{

		if (binding.equals(KeyMapping.GO_FORWARD.getID())) 
		{
			analyzer.moveFocus(1);
		} 
		
		else if (binding.equals(KeyMapping.GO_BACKWARD.getID())) 
		{
			analyzer.moveFocus(-1);
		} 
	}
}
