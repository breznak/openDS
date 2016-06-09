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


import cz.cvut.cognitive.load.CognitiveFunction;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;



import cz.cvut.cognitive.distractors.DistractionSettings;
import cz.cvut.cognitive.distractors.WeatherDistraction;
import de.lessvoid.nifty.elements.Element;
import eu.opends.main.Simulator;
import eu.opends.niftyGui.InstructionScreenGUI;


/**
 * 
 * @author Johnny Marek
 */
public class DistractionScreenGUIController implements ScreenController 
{
    
        
	private final Nifty nifty;
	private final InstructionScreenGUI instructionScreenGUI;
        private CheckBox CheckBox_snow;
        private CheckBox CheckBox_rain;
        private CheckBox CheckBox_fog;
        private CheckBox CheckBox_box;
        private CheckBox CheckBox_pedestrian;
        private CheckBox CheckBox_sound;
        private CheckBox CheckBox_text;
        private CheckBox CheckBox_dark;
        private CheckBox CheckBox_collect;
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
        private Simulator sim;
        
        
	
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
		this.instructionScreenGUI = instructionScreenGUI;
		this.nifty = instructionScreenGUI.getNifty();        
                this.sim = sim;
	}

	
	@Override
	public void bind(Nifty arg0, Screen arg1) 
	{
		
	}

	
	/**
	 * Will be called when GUI is closed.
	 */
	@Override
	public void onEndScreen() 
	{

	}

	/**
	 * Will be called when GUI is started.
	 */
	@Override
	public void onStartScreen() 
	{
            Screen screen = nifty.getCurrentScreen();
            CheckBox_rain = screen.findNiftyControl("CheckBox_rain", CheckBox.class);
            CheckBox_snow = screen.findNiftyControl("CheckBox_snow", CheckBox.class);
            CheckBox_fog = screen.findNiftyControl("CheckBox_fog", CheckBox.class);
            CheckBox_box = screen.findNiftyControl("CheckBox_box", CheckBox.class);
            CheckBox_pedestrian = screen.findNiftyControl("CheckBox_pedestrian", CheckBox.class);
            CheckBox_sound = screen.findNiftyControl("CheckBox_sound", CheckBox.class);
            CheckBox_text = screen.findNiftyControl("CheckBox_text", CheckBox.class);
            CheckBox_dark = screen.findNiftyControl("CheckBox_dark", CheckBox.class);
            CheckBox_collect = screen.findNiftyControl("CheckBox_collect", CheckBox.class);
            
            Slider_snow = screen.findNiftyControl("Slider_snow", Slider.class);
            Slider_rain = screen.findNiftyControl("Slider_rain", Slider.class);
            Slider_fog = screen.findNiftyControl("Slider_fog", Slider.class);
            Slider_box = screen.findNiftyControl("Slider_box", Slider.class);
            Slider_pedestrian = screen.findNiftyControl("Slider_pedestrian", Slider.class);
            Slider_sound = screen.findNiftyControl("Slider_sound", Slider.class);
            Slider_text = screen.findNiftyControl("Slider_text", Slider.class);
            Slider_dark = screen.findNiftyControl("Slider_dark", Slider.class);
            Slider_collect = screen.findNiftyControl("Slider_collect", Slider.class);
            
            Slider_rain.setMax(100);
            Slider_rain.setStepSize(1);
            Slider_rain.disable();
            Slider_snow.setMax(100);
            Slider_snow.setStepSize(1);
            Slider_snow.disable();
            Slider_fog.setStepSize(1);
            Slider_fog.setMax(70);
            Slider_fog.disable(); 
            Slider_box.setMax(100);
            Slider_box.setStepSize(1);
            Slider_box.disable();
            Slider_sound.setMax(100);
            Slider_sound.setStepSize(1);
            Slider_sound.disable();
            Slider_pedestrian.setMax(100);
            Slider_pedestrian.setStepSize(1);
            Slider_pedestrian.disable();
            Slider_text.setMax(100);
            Slider_text.setStepSize(1);
            Slider_text.disable();
            Slider_dark.setMax(100);
            Slider_dark.setStepSize(1);
            Slider_dark.disable();
            Slider_collect.setMax(100);
            Slider_collect.setStepSize(1);
            Slider_collect.disable();
	}
        
        @NiftyEventSubscriber(id="CheckBox_rain") 
        public void toggleRain(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_rain.isChecked()) {
                Slider_rain.enable();
                DistractionSettings.setRain(true);          
            }
            else {
                Slider_rain.setValue(0);
                Slider_rain.disable();
                DistractionSettings.setRain(false);
            }
            DistractionSettings.setIntensityRain(Slider_rain.getValue());
            WeatherDistraction.setWeather();
        }
        
        @NiftyEventSubscriber(id="CheckBox_snow") 
        public void toggleSnow(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_snow.isChecked()) {
                Slider_snow.enable();
                DistractionSettings.setSnow(true);
            }
            else {
                Slider_snow.setValue(0);
                Slider_snow.disable();
                DistractionSettings.setSnow(false);
            }
            DistractionSettings.setIntensitySnow(Slider_snow.getValue());
            WeatherDistraction.setWeather();
        }
        
        @NiftyEventSubscriber(id="CheckBox_fog") 
        public void toggleFog(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_fog.isChecked()) {
                Slider_fog.enable();
                DistractionSettings.setFog(true);
            }
            else {
                Slider_fog.setValue(0);
                Slider_fog.disable();
                DistractionSettings.setFog(false);
            }
            DistractionSettings.setIntensityFog(Slider_fog.getValue());
            WeatherDistraction.setWeather();
        }
        
        @NiftyEventSubscriber(id="CheckBox_box") 
        public void toggleBox(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_box.isChecked()) {
                Slider_box.enable();
                DistractionSettings.setBox(true);
            }
            else {
                Slider_box.setValue(0);
                Slider_box.disable();
                DistractionSettings.setBox(false);
            }
            DistractionSettings.setProbabilityBox(Slider_box.getValue());
        }
        
        @NiftyEventSubscriber(id="CheckBox_sound") 
        public void toggleSound(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_sound.isChecked()) {
                Slider_sound.enable();
                DistractionSettings.setSound(true);
            }
            else {
                Slider_sound.setValue(0);
                Slider_sound.disable();
                DistractionSettings.setSound(false);
            }
            DistractionSettings.setProbabilitySound(Slider_sound.getValue());
        }
        
        @NiftyEventSubscriber(id="CheckBox_pedestrian") 
        public void togglePedestrian(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_pedestrian.isChecked()) {
                Slider_pedestrian.enable();
                DistractionSettings.setPedestrian(true);
            }
            else {
                Slider_pedestrian.setValue(0);
                Slider_pedestrian.disable();
                DistractionSettings.setPedestrian(false);
            }
            DistractionSettings.setProbabilityPedestrian(Slider_pedestrian.getValue());
        }
        
        @NiftyEventSubscriber(id="CheckBox_text") 
        public void toggleText(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_text.isChecked()) {
                Slider_text.enable();
                DistractionSettings.setText(true);
            }
            else {
                Slider_text.setValue(0);
                Slider_text.disable();
                DistractionSettings.setText(false);
            }
            DistractionSettings.setProbabilityText(Slider_text.getValue());
        }
        
        @NiftyEventSubscriber(id="CheckBox_dark") 
        public void toggleDark(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_dark.isChecked()) {
                Slider_dark.enable();
                DistractionSettings.setDark(true);
            }
            else {
                Slider_dark.setValue(0);
                Slider_dark.disable();
                DistractionSettings.setDark(false);
            }
            DistractionSettings.setProbabilityDark(Slider_dark.getValue());
        }
        
        @NiftyEventSubscriber(id="CheckBox_collect") 
        public void toggleCollect(final String id, final CheckBoxStateChangedEvent event) 
        {  
            if (CheckBox_collect.isChecked()) {
                Slider_collect.enable();
                DistractionSettings.setCollect(true);
            }
            else {
                Slider_collect.setValue(0);
                Slider_collect.disable();
                DistractionSettings.setCollect(false);
            }
            DistractionSettings.setProbabilityCollect(Slider_collect.getValue());          
        }
        
        @NiftyEventSubscriber(id="Slider_rain") 
        public void intensityRain(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setIntensityRain(Slider_rain.getValue());
                WeatherDistraction.setWeather();
        }
        
        @NiftyEventSubscriber(id="Slider_snow") 
        public void intensitySnow(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setIntensitySnow(Slider_snow.getValue());
                WeatherDistraction.setWeather();
        }
        
        @NiftyEventSubscriber(id="Slider_fog") 
        public void intensityFog(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setIntensityFog(Slider_fog.getValue());
                WeatherDistraction.setWeather();
        }
        
        @NiftyEventSubscriber(id="Slider_box") 
        public void propabilityBox(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setProbabilityBox(Slider_box.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_sound") 
        public void propabilitySound(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setProbabilitySound(Slider_sound.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_pedestrian") 
        public void propabilityPedestrian(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setProbabilityPedestrian(Slider_pedestrian.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_text") 
        public void propabilityText(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setProbabilityText(Slider_text.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_dark") 
        public void propabilityDark(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setProbabilityDark(Slider_dark.getValue());
        }
        
        @NiftyEventSubscriber(id="Slider_collect") 
        public void propabilityCollect(final String id, final SliderChangedEvent event) 
        {   
                DistractionSettings.setProbabilityCollect(Slider_collect.getValue());
        }
        
        
	public void clickStartButton()
	{
                WeatherDistraction.COG_SCORE =(float)((DistractionSettings.getIntensityFog() + 
                        DistractionSettings.getIntensityRain() + DistractionSettings.getIntensitySnow())*0.03);
                CognitiveFunction.distScore += WeatherDistraction.COG_SCORE;
                DistractionSettings.setDistScenario(true);
		instructionScreenGUI.hideDialog();
                
	}
        


}
