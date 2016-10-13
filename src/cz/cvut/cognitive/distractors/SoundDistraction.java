package cz.cvut.cognitive.distractors;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import cz.cvut.cognitive.load.CognitiveFunction;
import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 * 
 * Class for Sound-type distraction. When this option is selected in options
 * there is always (set) probability to play sound / recording
 * 
 * TODO: add diferent recording;
 * 
 */
public class SoundDistraction extends DistractionClass{
    
    private final AudioNode soundTest;
   private boolean soundOn = false;
  
    /**
     *Constructor for SoundDistraction
     *@param: sim - simulator
     */
    public SoundDistraction(Simulator sim) {
        super(sim, 2, 0.1f, 1);
  
        soundTest = new AudioNode(manager,"Sounds/TrafficDistraction.wav",false);
        soundTest.setLooping(true);
        soundTest.setPositional(false);
        }

    /**
     * Update function: if preset probability of Sound playing is higher than
     * random generated number (1-100), sound will play. 
     */
    @Override
    public void spawn(float tpf) {
        int n = (int)(Math.random() * 100) + 1;
        if (n <= this.PROBABILITY){
            soundTest.play();
            soundOn = true;
            CognitiveFunction.distScore += this.COG_DIFFICULTY;
            CognitiveFunction.activeDistCount++;
            CognitiveFunction.activeDistNames[4] = 1;
            DistractionSettings.distRunning++;
        }
        
    }
    
    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove()
    {
        if(soundOn){
            soundTest.pause();
            CognitiveFunction.distScore -= this.COG_DIFFICULTY;
            CognitiveFunction.activeDistCount--;
            CognitiveFunction.activeDistNames[4] = 0;
            soundOn = false;
            DistractionSettings.distRunning--;
        }
    }

    @Override
    public void collision(float tpf) {
        //FIXME can we detect if the user(car) crashed into anything during the sound playing?
        return; //NOOP
    }
}
