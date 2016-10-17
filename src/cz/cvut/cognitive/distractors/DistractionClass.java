

package cz.cvut.cognitive.distractors;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;
import cz.cvut.cognitive.load.CognitiveFunction;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johnny
 
 Abstract class for different types of distractions.
 Ensures that every distraction contains spawn (spawn) and remove method.
 Update method parameters: tpf - time per frame of simulator
                           probability - preset value of every initialized
                                         distraction
 */
public abstract class DistractionClass implements Runnable {

    private static ArrayList<DistractionClass> activeDistractors = new ArrayList<>();
    //protected
    protected float probability;
    protected final float REWARD;
    public final float COG_DIFFICULTY; //FIXME make protected too (WeatherD)
    //private
    private final AtomicInteger  tpf_atomic = new AtomicInteger();
    private final int DURATION; //ms 
    /**
     * if the Distraction is currently active/used
     */
    private boolean isActive = false;
    private Thread runner;
    
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
        this.probability=probability;
        this.REWARD=reward;
        this.COG_DIFFICULTY = difficulty;
        DistractionClass.activeDistractors.add(this); //append to available distractors
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        this.bulletAppState = sim.getBulletAppState();
        this.camera = sim.getCamera();
        
        if(this.getClass().equals(TextDistraction.class)) {
            DURATION=20000; //20sec
        }
        else {
            DURATION = 5000; //TODO random/param
        }
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
        System.out.println("PROB="+probability+" n="+n+" active="+isActive+" "+this.getClass().getSimpleName());
        if (n > this.probability || isActive || probability==0.0f) { return; } 
        
        CognitiveFunction.distScore += this.COG_DIFFICULTY;
        CognitiveFunction.activeDistCount++;
        CognitiveFunction.activeDistNames[0] = 1; //FIXME remove 
        
        tpf_atomic.set((int)f);
        runner = new Thread(this);
        runner.start();
    }
    
    /**
     * 
     * @return whether this distraction is currently active/occuring 
     */
    public boolean isActive() {
        return isActive;
    }
    
    private void remove() {
        if(!isActive) return; //do nothing on inactive
        
        remove_local();
        
        CognitiveFunction.distScore -= this.COG_DIFFICULTY;
        CognitiveFunction.activeDistCount--;
        CognitiveFunction.activeDistNames[0] = 0;

        isActive=false;
    }
    
    @Override
    public void run() {
        // start
        float step = (float)this.tpf_atomic.get();
        spawn(step);
        this.isActive = true;
        System.out.println(this.getClass().getSimpleName()+" RUNNING "+step);
        try {
            //wait
            System.out.println("SLEEP");
            TimeUnit.MILLISECONDS.sleep(DURATION);
        } catch (InterruptedException ex) {
            Logger.getLogger(DistractionClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        //collisions?
        collision(step);
        //remove
        remove();
    }
}
