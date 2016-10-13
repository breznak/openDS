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
    
    private final Simulator sim;
    private BoxDistraction dropBox;
    private SoundDistraction soundDis;
    private DarkeningDistraction dark;
    private PedestrianDistraction pedestrian;
    private TextDistraction text;
    private CollectObjectDistraction collect;
    private DistractionClass [] distractors;
    private int [] dist_index;
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
               
        distractors = new DistractionClass[6];
        
        if (DistractionSettings.isBox()){
            dropBox.setDisProbability(DistractionSettings.probabilityBox);
            dropBox.setDisProbability(100);
            distractors[currentIndex] = dropBox; 
            currentIndex++;
            System.out.println("box done");
        }
        if (DistractionSettings.isSound()){
            distractors[currentIndex] = soundDis;
            soundDis.setDisProbability(DistractionSettings.probabilitySound);
            currentIndex++;
        }
        if (DistractionSettings.isDark()){
            distractors[currentIndex] = dark;
            dark.setDisProbability(DistractionSettings.probabilityDark);
            currentIndex++;
        }
        if (DistractionSettings.isPedestrian()){
            distractors[currentIndex] = pedestrian;
            pedestrian.setDisProbability(DistractionSettings.probabilityPedestrian);
            currentIndex++;
        }
        if (DistractionSettings.isText()){
            distractors[currentIndex] = text;
            text.setDisProbability(DistractionSettings.probabilityText);
            currentIndex++;
        }  
        if (DistractionSettings.isCollect()){
            distractors[currentIndex] = collect;
            collect.setDisProbability(DistractionSettings.probabilityCollect);
            currentIndex++;
            System.out.println("Collect there");
        }  
        dist_index = new int [currentIndex + 1];
        System.out.println(currentIndex);
        
    }
    
  
    public void update(float tpf){
        int k = 0;
        for (int i = 0; i < currentIndex; i++) {
            int n = (int)(Math.random() * 100) + 1;
            System.out.println(distractors[i].getDisProbability()+ " vs " + n);
            if (distractors[i].getDisProbability() > n) dist_index[k++]=i;
            
        }
        if (k != 0){
            int n = (int)(Math.random() * k) + 1;
            distractors[dist_index[n]].update(tpf);
        }
          
           
    }
    
    public void collide(float tpf){
        if (DistractionSettings.isBox()) dropBox.collision(tpf);
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
