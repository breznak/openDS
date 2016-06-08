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

package eu.opends.niftyGui.listBox;

import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverter;
import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverterSimple;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

import java.util.logging.Logger;

/**
 * 
 * @author Rafael Math
 */
public class TextListBoxViewConverter implements ListBoxViewConverter<TextListBoxModel> 
{
	private Logger log = Logger.getLogger(ListBoxViewConverterSimple.class.getName());
	private String message = "you're using the ListBoxViewConverterSimple but there is no " 
								+ "TextRenderer on the listBoxElement. You've probably changed " 
								+ "the item template but did not provided your own "
								+ "ListBoxViewConverter to the ListBox.";

	@Override
	public void display(final Element element, final TextListBoxModel item) 
	{
		TextRenderer renderer = element.getRenderer(TextRenderer.class);
		if (renderer == null) 
		{
			log.warning(message);
			return;
		}
		
		if (item != null) 
		{
			renderer.setText(item.getLabel());
			renderer.setColor(item.getColor());
		} 
		else 
		{
			renderer.setText("");
		}
	}

	
	@Override
	public int getWidth(final Element element, final TextListBoxModel item) 
	{
		TextRenderer renderer = element.getRenderer(TextRenderer.class);
		if (renderer == null) 
		{
			log.warning(message);
			return 0;
		}
		
		return element.getRenderer(TextRenderer.class).getFont().getWidth(item.getLabel());
	}

}
