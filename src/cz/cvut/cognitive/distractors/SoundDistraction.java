package cz.cvut.cognitive.distractors;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
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
    private final AssetManager manager;
    private boolean soundOn = false;
    
  
    /**
     *Constructor for SoundDistraction
     *@param: sim - simulator
     */
    public SoundDistraction(Simulator sim) {
        this.manager = sim.getAssetManager();
        soundTest = new AudioNode(manager,"Sounds/World1.wav",false);
        soundTest.setPositional(false);
    }

    /**
     * Update function: if preset probability of Sound playing is higher than
     * random generated number (1-100), sound will play. 
     */
    @Override
    public void update(float tpf, float propability) {
        int n = (int)(Math.random() * 100) + 1;
        if (n < propability){
            soundTest.play();
            soundOn = true;
            DistractionSettings.setDistRunning(true);
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
            soundTest.stop();
            soundOn = false;
        }
    }
}
