

package cz.cvut.cognitive.distractors;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import cz.cvut.cognitive.load.CognitiveFunction;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Johnny
 
 Abstract class for different types of distractions.
 Ensures that every distraction contains spawn (spawn) and remove method.
 Update method parameters: tpf - time per frame of simulator
                           probability - preset value of every initialized
                                         distraction
 */
public abstract class DistractionClass {
    //protected
    protected final float PROBABILITY;
    protected final float REWARD;
    public final float COG_DIFFICULTY; //FIXME make protected too (WeatherD)
    //private
    private final static ArrayList<DistractionClass> activeDistractors = new ArrayList<>();
    /**
     * if the Distraction is currently active/used
     */
    private boolean isActive = false;
    
    protected final Simulator sim;
    protected final SteeringCar car;
    protected final AssetManager manager;
    protected final BulletAppState bulletAppState;
    protected final Camera camera;
    /**
     * settings from GUI
     */
    protected final HashMap<String, String> settings = new HashMap<>();
    /**
     * 
     * @param reward positive(>0) means it's good to collect this, added on "hit", substracted from score on "miss"
     * @param probability 0..100, how often should the distraction occur
     * @param difficulty how "cognitively demanding" this stress factor is
     */
    protected DistractionClass(Simulator sim, float reward, float probability, float difficulty) {
        this.PROBABILITY=DistractionSettings.probabilityCollect; //probability;
        this.REWARD=reward;
        this.COG_DIFFICULTY = difficulty;
        DistractionClass.activeDistractors.add(this); //append to available distractors
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        this.bulletAppState = sim.getBulletAppState();
        this.camera = sim.getCamera();
    }
    
    //abstract
    /**
     * compute if & handle situation on collision
     * @param tpf 
     */
    public abstract void collision(float tpf);
    protected abstract void spawn(float tpf);
    protected abstract void remove_local();
    
    //public abstract void showReward();
    //public
    public static List<DistractionClass> getDistractors() {
        return DistractionClass.activeDistractors;
    }
    
    public void update(float f) {
        float n = (float)Math.random();
        System.out.println("PROB="+PROBABILITY+" n="+n+" active="+isActive+" "+this.getClass().getSimpleName());
        if (n > this.PROBABILITY || isActive || PROBABILITY==0.0f) { return; } 
        
        spawn(f);
        
        CognitiveFunction.distScore += this.COG_DIFFICULTY;
        CognitiveFunction.activeDistCount++;
        CognitiveFunction.activeDistNames[0] = 1; //FIXME remove
        DistractionSettings.distRunning++;
        
        this.isActive = true;
   }
    
    /**
     * 
     * @return whether this distraction is currently active/occuring 
     */
    public boolean isActive() {
        return isActive;
    }
    
    public void remove() {
        if(!isActive) return; //do nothing on inactive
        
        remove_local();
        
        CognitiveFunction.distScore -= this.COG_DIFFICULTY;
        CognitiveFunction.activeDistCount--;
        CognitiveFunction.activeDistNames[0] = 0;
        DistractionSettings.distRunning--;
        
        isActive=false;
    }
    
    public static void initialize(Simulator sim) {
        new BoxDistraction(sim, 1, 1, 1, 2, "Textures"+File.separator+"DistractionTask"+File.separator+"default_box.jpg");
        new SoundDistraction(sim);
        new DarkeningDistraction(sim);
        new PedestrianDistraction(sim,"Textures"+File.separator+"DistractionTask"+File.separator+"default_pedestrian.jpg");
        new TextDistraction(sim);
        new CollectObjectDistraction(sim, "Textures"+File.separator+"DistractionTask"+File.separator+"default_greenSphere.png", "Textures"+File.separator+"DistractionTask"+File.separator+"default_redSphere.png");
        new WeatherDistraction(sim);           
    }   
}
