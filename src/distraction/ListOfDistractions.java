package distraction;

/**
 *
 * @author Johnny
 */

import eu.opends.main.Simulator;

public class ListOfDistractions {
    
    private final Simulator sim;
    private DroppingBox dropBox;
    private SoundDistraction soundDis;
    private DarkeningDistraction dark;
    
    
    public ListOfDistractions(Simulator sim){
        this.sim = sim;
    }
    
    public void initialize(){
        dropBox = new DroppingBox();
        dropBox.initialize(sim);
        soundDis = new SoundDistraction();
        soundDis.initialize(sim); 
        dark = new DarkeningDistraction();
    }
    
    public void update(float tpf){
        int n = (int)(Math.random() * 3) + 1;
        switch(n){
            case 1: dropBox.update(tpf, DistractionSettings.getPropabilityBox());
                break;
            case 2: soundDis.update(tpf, DistractionSettings.getPropabilitySound());
                break;
            case 3: dark.update(tpf, tpf);
                break;
            default: System.out.println("Invalid distraction task number "+n);
                break;
        }
            
    }
    
    public void removeDist(){
        dropBox.remove();
        soundDis.remove();
        dark.remove();
    }
    
}
