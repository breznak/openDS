/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
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

package cz.cvut.cognitive.override;


import cz.cvut.cognitive.distractors.BoxDistraction;
import cz.cvut.cognitive.distractors.CollectObjectDistraction;
import cz.cvut.cognitive.distractors.DarkeningDistraction;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;



import cz.cvut.cognitive.distractors.PedestrianDistraction;
import cz.cvut.cognitive.distractors.SoundDistraction;
import cz.cvut.cognitive.distractors.TextDistraction;
import cz.cvut.cognitive.distractors.WeatherDistraction;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import eu.opends.main.Simulator;
import eu.opends.niftyGui.InstructionScreenGUI;
import eu.opends.niftyGui.InstructionScreenGUIController;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


/**
 * 
 * @author Johnny Marek
 */
public class DistractionScreenGUIController extends InstructionScreenGUIController //FIXME remove this class, just override sections from ScreenGUIController
{
    
        
	private final Nifty nifty;

        private Slider Slider_rain;
        private Slider Slider_snow;
        private Slider Slider_fog;
        private Slider Slider_box;
        private Slider Slider_pedestrian;
        private Slider Slider_sound;
        private Slider Slider_text;
        private Slider Slider_dark;
        private Slider Slider_collect;
        private Element infoRain;     
        private final Simulator sim;
        private ListBox Value_rain;
        private ListBox Value_snow;
        private ListBox Value_fog;
        private ListBox Value_box;
        private ListBox Value_pedestrian;
        private ListBox Value_sound;
        private ListBox Value_text;
        private ListBox Value_dark;
        private ListBox Value_collect;
        private ListBox instructionBox_1;
        private ListBox instructionBox_2;
        private ListBox instructionBox_3;
        
	/**
	 * Creates a new controller instance for the key mapping and graphic 
	 * settings nifty-gui.
	 * 
	 * 
	 * @param instructionScreenGUI
	 * 			Instance of the key mapping and graphic settings GUI.
	 */
	public DistractionScreenGUIController(Simulator sim, InstructionScreenGUI instructionScreenGUI) 
	{
            super(sim, instructionScreenGUI);
            
		this.nifty = instructionScreenGUI.getNifty();        
                this.sim = sim;
	}

	/**
	 * Will be called when GUI is started.
	 */
	@Override
	public void onStartScreen() 
	{
            super.onStartScreen();
            
            Screen screen = nifty.getCurrentScreen();
//            CheckBox_rain = screen.findNiftyControl("CheckBox_rain", CheckBox.class); //example of checkbox use in GUI
            
            Slider_snow = screen.findNiftyControl("Slider_snow", Slider.class);
            Slider_rain = screen.findNiftyControl("Slider_rain", Slider.class);
            Slider_fog = screen.findNiftyControl("Slider_fog", Slider.class);
            Slider_box = screen.findNiftyControl("Slider_box", Slider.class);
            Slider_pedestrian = screen.findNiftyControl("Slider_pedestrian", Slider.class);
            Slider_sound = screen.findNiftyControl("Slider_sound", Slider.class);
            Slider_text = screen.findNiftyControl("Slider_text", Slider.class);
            Slider_dark = screen.findNiftyControl("Slider_dark", Slider.class);
            Slider_collect = screen.findNiftyControl("Slider_collect", Slider.class);
            
            Slider_fog.setMax(70); //different than default 100
            
            Value_rain = (ListBox) screen.findNiftyControl("Value_rain", ListBox.class);
            Value_snow = (ListBox) screen.findNiftyControl("Value_snow", ListBox.class);
            Value_fog = (ListBox) screen.findNiftyControl("Value_fog", ListBox.class);
            Value_box = (ListBox) screen.findNiftyControl("Value_box", ListBox.class);
            Value_pedestrian = (ListBox) screen.findNiftyControl("Value_pedestrian", ListBox.class);
            Value_collect = (ListBox) screen.findNiftyControl("Value_collect", ListBox.class);
            Value_dark = (ListBox) screen.findNiftyControl("Value_dark", ListBox.class);
            Value_text = (ListBox) screen.findNiftyControl("Value_text", ListBox.class);
            Value_sound = (ListBox) screen.findNiftyControl("Value_sound", ListBox.class);
            
            instructionBox_1 = (ListBox) screen.findNiftyControl("Instruction_box_1", ListBox.class);
            instructionBox_1.changeSelectionMode(ListBox.SelectionMode.Disabled, false);	
            instructionBox_1.setFocusable(false);
            instructionBox_2 = (ListBox) screen.findNiftyControl("Instruction_box_2", ListBox.class);
            instructionBox_2.changeSelectionMode(ListBox.SelectionMode.Disabled, false);	
            instructionBox_2.setFocusable(false);
            instructionBox_3 = (ListBox) screen.findNiftyControl("Instruction_box_3", ListBox.class);
            instructionBox_3.changeSelectionMode(ListBox.SelectionMode.Disabled, false);	
            instructionBox_3.setFocusable(false);
            sendToScreen(instructionBox_1, "assets"+File.separator+"Interface"+File.separator+"Text_questions"+File.separator+"Instructions_1.txt");
            sendToScreen(instructionBox_2, "assets"+File.separator+"Interface"+File.separator+"Text_questions"+File.separator+"Instructions_2.txt");
            sendToScreen(instructionBox_3, "assets"+File.separator+"Interface"+File.separator+"Text_questions"+File.separator+"Instructions_3.txt");

            // set default values
            clickDefButton();
	}
        
        @NiftyEventSubscriber(id="Slider_rain") 
        public void intensityRain(final String id, final SliderChangedEvent event) 
        {   
                Value_rain.clear();
                Value_rain.addItem(Slider_rain.getValue());                
        }
        
        @NiftyEventSubscriber(id="Slider_snow") 
        public void intensitySnow(final String id, final SliderChangedEvent event) 
        {   
                Value_snow.clear();
                Value_snow.addItem(Slider_snow.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_fog") 
        public void intensityFog(final String id, final SliderChangedEvent event) 
        {   
                Value_fog.clear();
                Value_fog.addItem(Slider_fog.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_box") 
        public void propabilityBox(final String id, final SliderChangedEvent event) 
        {   
                Value_box.clear();
                Value_box.addItem(Slider_box.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_sound") 
        public void propabilitySound(final String id, final SliderChangedEvent event) 
        {   
                Value_sound.clear();
                Value_sound.addItem(Slider_sound.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_pedestrian") 
        public void propabilityPedestrian(final String id, final SliderChangedEvent event) 
        {   
                Value_pedestrian.clear();
                Value_pedestrian.addItem(Slider_pedestrian.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_text") 
        public void propabilityText(final String id, final SliderChangedEvent event) 
        {   
                Value_text.clear();
                Value_text.addItem(Slider_text.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_dark") 
        public void propabilityDark(final String id, final SliderChangedEvent event) 
        {   
                Value_dark.clear();
                Value_dark.addItem(Slider_dark.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_collect") 
        public void propabilityCollect(final String id, final SliderChangedEvent event) 
        {   
                Value_collect.clear();
                Value_collect.addItem(Slider_collect.getValue());
        }
        
        
        @Override
	public void clickStartButton()
	{
		// Initialize all distractors here: 
                new BoxDistraction(sim, 5f, Slider_box.getValue(), 2f, 1, 1, 1, 2, "Textures"+File.separator+"DistractionTask"+File.separator+"default_box.jpg");
                new SoundDistraction(sim, 2f, Slider_sound.getValue(), 1f);
                new DarkeningDistraction(sim, 1f, Slider_dark.getValue(), 3f);
                new PedestrianDistraction(sim, 30f, Slider_pedestrian.getValue(), 5f, "Textures"+File.separator+"DistractionTask"+File.separator+"default_pedestrian.jpg");
                new TextDistraction(sim, 4f, Slider_text.getValue(), 3f);
                new CollectObjectDistraction(sim, 5f, Slider_collect.getValue(), 3f, "Textures"+File.separator+"DistractionTask"+File.separator+"default_greenSphere.png", "Textures"+File.separator+"DistractionTask"+File.separator+"default_redSphere.png");
                new WeatherDistraction(sim, 1f, Slider_rain.getValue(), WeatherDistraction.Type.RAIN); 
                new WeatherDistraction(sim, 1f, Slider_snow.getValue(), WeatherDistraction.Type.SNOW); 
                new WeatherDistraction(sim, 1f, Slider_fog.getValue(), WeatherDistraction.Type.FOG); 
                
                super.clickStartButton();
	}
        
        @NiftyEventSubscriber(id="DefaultButton")
          private void clickDefButton() //FIXME I broke the default button - GUI is not reset
	{
                resetOptions();
                
                Slider_collect.setValue(30);
                Slider_rain.setValue(20);
                Slider_box.setValue(30);
                
                nifty.update();
 	}
          
          private void resetOptions(){
                Slider_collect.setValue(0);
                Slider_rain.setValue(0);
                Slider_box.setValue(0);
                Slider_pedestrian.setValue(0);
                Slider_text.setValue(0);
                Slider_sound.setValue(0);
                Slider_dark.setValue(0);
                Slider_fog.setValue(0);
                Slider_snow.setValue(0);
          }
          
         /**
         * Fills listBox with text from a file
         * @param instructionBox listBox which should be filled
         * @param filePath path to the file .txt file
         *
         */
        @SuppressWarnings("unchecked")
        public void sendToScreen(ListBox instructionBox, String filePath){
        
            // clear list box
            instructionBox.clear();		
            int charactersPerLine = (int) (instructionBox.getWidth()/6.5f);
             try (Scanner scan = new Scanner(new File(filePath)))
            {
                while (scan.hasNextLine()) {
                    String[] words = scan.nextLine().trim().split(" ");;
                    if (words[0].equals("\\n")){
                        instructionBox.addItem('\n');
                    }else{

                        int indexOfCurrentWord = 0;
                        while(true) {

                            // initialize line
                            String line = "";

                            // try to get characters for one line
                            try 
                            {		
                                // fill word by word into a line, until the maximum number of characters 
                                // per line has been reached

                                // length of first word in line
                                int length = words[indexOfCurrentWord].length()+1;
                                while(length <= charactersPerLine) 
                                {

                                    // add current word
                                    line += words[indexOfCurrentWord] + " ";

                                    // go to next word
                                    indexOfCurrentWord++;

                                    // add length of next word for next loop
                                    length += words[indexOfCurrentWord].length()+1;
                                }

                            } catch(Exception e){
                                // ArrayIndexOutOfBoundsException will be caught, if not all lines filled
                            }
                        
                            // add line to message box
                            line = line.trim();
                            if(!line.isEmpty())
                                instructionBox.addItem(line);
                            else 
                                break;
                        }
                    }
                }
            } 
            catch (IOException e) {
                e.printStackTrace();
            } 
        }   
}