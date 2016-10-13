

package cz.cvut.cognitive.distractors;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import cz.cvut.cognitive.load.CognitiveFunction;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johnny
 * 
 * Abstract class for different types of distractions.
 * Ensures that every distraction contains update (spawn) and remove method.
 * Update method parameters: tpf - time per frame of simulator
 *                           probability - preset value of every initialized
 *                                         distraction
 */
public abstract class DistractionClass {
    
    //protected
    protected final float PROBABILITY;
    protected final float REWARD;
    public final float COG_DIFFICULTY; //FIXME make protected too (WeatherD)
    //private
    private final static ArrayList<DistractionClass> activeDistractors = new ArrayList<>();
    protected final Simulator sim;
    protected final SteeringCar car;
    protected final AssetManager manager;
    protected final BulletAppState bulletAppState;
    protected final Camera camera;
    /**
     * 
     * @param reward positive(>0) means it's good to collect this, added on "hit", substracted from score on "miss"
     * @param probability 0..100, how often should the distraction occur
     * @param difficulty how "cognitively demanding" this stress factor is
     */
    protected DistractionClass(Simulator sim, float reward, float probability, float difficulty) {
        this.PROBABILITY=probability;
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
    public abstract void update(float tpf);
    public abstract void remove();
    
    //public abstract void showReward();
    //public
    public static List<DistractionClass> getDistractors() {
        return DistractionClass.activeDistractors;
    }
    
    public static void initialize(Simulator sim) {
        new BoxDistraction(sim, 1, 1, 1, 2, "Textures/DistractionTask/default_box.jpg");
        new SoundDistraction(sim);
        new DarkeningDistraction(sim);
        new PedestrianDistraction(sim,"Textures/DistractionTask/default_pedestrian.jpg");
        new TextDistraction(sim);
        new CollectObjectDistraction(sim, "Textures/DistractionTask/default_greenSphere.png", "Textures/DistractionTask/default_redSphere.png");
        new WeatherDistraction(sim);           
    }
    
}
