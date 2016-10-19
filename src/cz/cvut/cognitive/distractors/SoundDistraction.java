package cz.cvut.cognitive.distractors;

import com.jme3.audio.AudioNode;
import eu.opends.main.Simulator;
import java.io.File;

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
    
    private final AudioNode soundNode;
    private boolean soundOn = false;
  
    /**
     *Constructor for SoundDistraction
     * @param sim Simulator instance
     */
    public SoundDistraction(Simulator sim, float reward, float probability, float cogDifficulty) {
        super(sim, reward, probability, cogDifficulty);
  
        soundNode = new AudioNode(manager,"Sounds"+File.separator+"TrafficDistraction.wav",false);
        soundNode.setLooping(true);
        soundNode.setPositional(false);
        }

    /**
     * Update function: if preset probability of Sound playing is higher than
     * random generated number (1-100), sound will play. 
     * @param tpf time per frame
     */
    @Override
    public void spawn(float tpf) {
            soundNode.play();
            soundOn = true;  
    }
    
    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove_local()
    {
        if(soundOn){
            soundNode.pause();
            soundOn = false;
        }
    }

    @Override
    public void collision(float tpf) {
        //FIXME can we detect if the user(car) crashed into anything during the sound playing?
        return; //NOOP
    }
}
