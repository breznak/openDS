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

package eu.opends.niftyGui.dropDown;

import de.lessvoid.nifty.controls.DropDown.DropDownViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 * 
 * @author Rafael Math
 */
public class SamplesDropDownViewConverter implements DropDownViewConverter<SamplesDropDownModel> 
{
	@Override
	public void display(Element element, SamplesDropDownModel item) 
	{
		TextRenderer renderer = element.getRenderer(TextRenderer.class);
		if (renderer == null) 
		{
			return;
		}
		
		if (item != null) 
		{
			renderer.setText(item.getLabel());
		} 
		else 
		{
			renderer.setText("");
		}
	}
	

	@Override
	public int getWidth(Element element, SamplesDropDownModel item) 
	{
		TextRenderer renderer = element.getRenderer(TextRenderer.class);
		if (renderer == null) 
		{
			return 0;
		}
		
		return element.getRenderer(TextRenderer.class).getFont().getWidth(item.getLabel());
	}
	
}