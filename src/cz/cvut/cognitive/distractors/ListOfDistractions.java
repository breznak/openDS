package cz.cvut.cognitive.distractors;

/**
 *
 * @author Johnny
 * 
 * Class calling all constructors of distractions after option menu is closed.
 * Also handles update and remove functions of distractors.
 * 
 * TODO: consider removing class.
 * 
 */

import eu.opends.main.Simulator;

public class ListOfDistractions {
    
    private int activeDistractors = 0;
    private final Simulator sim;
    private BoxDistraction dropBox;
    private SoundDistraction soundDis;
    private DarkeningDistraction dark;
    private PedestrianDistraction pedestrian;
    private TextDistraction text;
    private CollectObjectDistraction collect;
    private Object [] distractors;
    private int currentIndex = 0;
    
    
    public ListOfDistractions(Simulator sim){
        this.sim = sim;
    }
    
    public void initialize(){
        dropBox = new BoxDistraction(sim, 1, 1, 1, 2, "Textures/DistractionTask/default_box.jpg");
        soundDis = new SoundDistraction(sim);
        dark = new DarkeningDistraction(sim);
        pedestrian = new PedestrianDistraction(sim,"Textures/DistractionTask/default_pedestrian.jpg");
        text = new TextDistraction(sim);
        collect = new CollectObjectDistraction(sim, "Textures/DistractionTask/default_greenSphere.png", "Textures/DistractionTask/default_redSphere.png");
/*      distractors = new Object[activeDistractors];
        if (DistractionSettings.isBox()){
            dropBox = new BoxDistraction(sim, 1, 1, 1, 2);
            distractors[currentIndex] = dropBox;
            currentIndex++;
        }
        if (DistractionSettings.isBox()){
            soundDis = new SoundDistraction(sim);
            distractors[currentIndex] = soundDis;
            currentIndex++;
        }
        if (DistractionSettings.isBox()){
            dark = new DarkeningDistraction(sim);
            distractors[currentIndex] = dark;
            currentIndex++;
        }
        if (DistractionSettings.isBox()){
            pedestrian = new PedestrianDistraction(sim);
            distractors[currentIndex] = pedestrian;
            currentIndex++;
        }
        if (DistractionSettings.isBox()){
            text = new TextDistraction(sim);
            distractors[currentIndex] = text;
            currentIndex++;
        }  
*/
    }
    
/*
    public void countAllowedDistractions(){
        if (DistractionSettings.isBox()) activeDistractors++;
        if (DistractionSettings.isSound()) activeDistractors++;
        if (DistractionSettings.isDark()) activeDistractors++;
        if (DistractionSettings.isPedestrian()) activeDistractors++;
        if (DistractionSettings.isText()) activeDistractors++;   
    }
*/  
    
}
