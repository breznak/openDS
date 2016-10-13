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
    public void update(float tpf){
        if (DistractionSettings.isBox()) dropBox.update(tpf); //FIXME use DistractionSettings.getProbabilitySound() to set selected Prob
        if (DistractionSettings.isSound()) soundDis.update(tpf);
        if (DistractionSettings.isDark()) dark.update(tpf);
        if (DistractionSettings.isPedestrian()) pedestrian.update(tpf);
        if (DistractionSettings.isText()) text.update(tpf);
        if (DistractionSettings.isCollect()) collect.update(tpf);
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
    
    public void collide(float tpf){
        if(DistractionSettings.isBox()) dropBox.collision(tpf);
        if (DistractionSettings.isPedestrian()) pedestrian.collision(tpf);
        if (DistractionSettings.isCollect()) collect.collision(tpf);
        
    }
    
    public void removeDist(){
        if (DistractionSettings.isBox()) dropBox.remove();
        if (DistractionSettings.isSound()) soundDis.remove();
        if (DistractionSettings.isDark()) dark.remove();
        if (DistractionSettings.isPedestrian()) pedestrian.remove();
        if (DistractionSettings.isCollect()) collect.remove();
        //if (DistractionSettings.isText()) text.remove();
    }
    
}
