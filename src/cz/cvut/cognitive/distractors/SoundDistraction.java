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
 * TODO: add different recording;
 * 
 */
public class SoundDistraction extends DistractionClass{
    
    private final AudioNode soundDistractionNode;
    private final AssetManager manager;
    private boolean soundOn = false;
    public float COG_SCORE;
  
    /**
     *Constructor for SoundDistraction
     * @param sim Simulator instance
     */
    public SoundDistraction(Simulator sim) {
        this.manager = sim.getAssetManager();
        soundDistractionNode = new AudioNode(manager,"Sounds/TrafficDistraction.wav",false);
        soundDistractionNode.setLooping(true);
        soundDistractionNode.setPositional(false);
        COG_SCORE = 1;
    }

    /**
     * Update function: if preset probability of Sound playing is higher than
     * random generated number (1-100), sound will play. 
     * @param tpf time per frame
     */
    @Override
    public void update(float tpf) {
            soundDistractionNode.play();
            soundOn = true;
            CognitiveFunction.distScore += COG_SCORE;
            CognitiveFunction.activeDistCount++;
            CognitiveFunction.activeDistNames[4] = 1;
            DistractionSettings.distRunning++;

    }
    
    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove()
    {
        if(soundOn){
            soundDistractionNode.pause();
            CognitiveFunction.distScore -= COG_SCORE;
            CognitiveFunction.activeDistCount--;
            CognitiveFunction.activeDistNames[4] = 0;
            soundOn = false;
            DistractionSettings.distRunning--;
        }
    }
}
