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
    private Object [] distractors;
    private int currentIndex = 0;
    
    public ListOfDistractions(Simulator sim){
        this.sim = sim;
    }
    
    public void initialize(){
        dropBox = new BoxDistraction(sim, 1, 1, 1, 2);
        soundDis = new SoundDistraction(sim);
        dark = new DarkeningDistraction(sim);
        pedestrian = new PedestrianDistraction(sim);
        text = new TextDistraction(sim);
        
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
    public void update(float tpf){
        if (DistractionSettings.isBox()) dropBox.update(tpf, DistractionSettings.getProbabilityBox());
        if (DistractionSettings.isSound()) soundDis.update(tpf, DistractionSettings.getProbabilitySound());
        if (DistractionSettings.isDark()) dark.update(tpf, DistractionSettings.getProbabilityDark());
        if (DistractionSettings.isPedestrian()) pedestrian.update(tpf, DistractionSettings.getProbabilityPedestrian());
        if (DistractionSettings.isText()) text.update(tpf, DistractionSettings.getProbabilityText());
 /*
        int n = (int)(Math.random() * activeDistractors) + 1; //3
       
        switch(n){
            case 1: dropBox.update(tpf, DistractionSettings.getPropabilityBox()); 
                break;
            case 2: soundDis.update(tpf, DistractionSettings.getPropabilitySound());
                break;
            case 3: dark.update(tpf, DistractionSettings.getPropabilityDark());
                break;
            case 4: pedestrian.update(tpf, DistractionSettings.getPropabilityPedestrian());
                break;
            case 5: text.update(tpf, DistractionSettings.getPropabilityText());
                break;
            default: System.out.println("Invalid distraction task number "+n);
                break;
        }
 */           
    }
    
    public void removeDist(){
        if (DistractionSettings.isBox()) dropBox.remove();
        if (DistractionSettings.isSound()) soundDis.remove();
        if (DistractionSettings.isDark()) dark.remove();
        if (DistractionSettings.isPedestrian()) pedestrian.remove();
        //if (DistractionSettings.isText()) text.remove();
    }
    
}
