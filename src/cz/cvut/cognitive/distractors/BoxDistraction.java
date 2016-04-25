package cz.cvut.cognitive.distractors;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
/**
 *
 * @author Johnny Marek
 * 
 * Class for Dropping-Box-type distraction. When this option is selected in
 * program, there is always (set) probability to spawn physical Box in front of
 * the car.
 * 
 */
public class BoxDistraction extends DistractionClass{
    
    private final Simulator sim;
    private final SteeringCar car;
    private final AssetManager manager;
    private final Geometry DropBox;
    private final Spatial droppingBox;
    private final BulletAppState bulletAppState;
    private final RigidBodyControl box_phy;
    private final float y_offSet;
    private boolean boxOn = false;
    private Camera camera;
    

    /**
     *Constructor for DroppingBox
     *@param: sim, 
     * x - sizesOfBox(x)
     * y - sizesOfBox(y)
     * z - sizesOfBox(y)
     * mass - mass of the box.
     *          
     */
    public BoxDistraction(Simulator sim, float x, float y, float z, float mass) {
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        this.bulletAppState = sim.getBulletAppState();
        this.camera = sim.getCamera();
        
        //Creates an offset for placing box in world
        y_offSet = y;
        
        //initialization of box and its physics
        box_phy = new RigidBodyControl(mass);
        DropBox = new Geometry("DropBox", new Box(x, y, z));
        Material box_mat = new Material(manager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        box_mat.setColor("Color", ColorRGBA.Red);
        DropBox.setMaterial(box_mat);
        DropBox.addControl(box_phy);
        droppingBox = DropBox;
        
        
        
    }

    /**
     * Update function: if preset probability of Box spawning is higher than
     * random generated number (1-100), box will spawn. 
     * Physical properties of box are set to default 0 at the beginning of every
     * new spawn to prevent it to remember properties from earlier call (box 
     * won't be flying around as soon as it spawns if it was hit right before
     * despawn in earlier call).
     */
    @Override
    public void update (float tpf, float propability){
        int n = (int)(Math.random() * 100) + 1;
            if (n < propability){ 
                //generates offset for box spawn in front of the car
                int distance_offset = (int)(Math.random() * 20) + 1;
                
                //add node to scene
                sim.getSceneNode().attachChild(droppingBox);
                bulletAppState.getPhysicsSpace().add(box_phy); 
                box_phy.setLinearVelocity(new Vector3f(0,0,0));
                box_phy.setAngularVelocity(new Vector3f(0,0,0));
                box_phy.setPhysicsRotation(Matrix3f.IDENTITY);
                
                /*creates spawn position of car - always in front of the car 
                * TODO: fix for all camera modes (works best for first camera position)
                */
                Vector3f carPosition = new Vector3f(car.getPosition().x,car.getPosition().y+y_offSet+1.1f,car.getPosition().z );
                Vector3f carHeading = new Vector3f(camera.getDirection());
                float distance = 5+distance_offset;
                Vector3f spawn = new Vector3f(carPosition.add(carHeading.mult(distance)));
                
                //box_phy.setPhysicsLocation(new Vector3f (car.getPosition().x+(float)Math.cos((double)degree),car.getPosition().y+y_offSet+0.1f,car.getPosition().z+(float)Math.sin((double)degree)-(5+z_offset)));   
                box_phy.setPhysicsLocation(spawn);
                boxOn = true;
                DistractionSettings.setDistRunning(true);
             }
        
    }
    
    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove (){
        if(boxOn){
            sim.getSceneNode().detachChild(droppingBox);
            bulletAppState.getPhysicsSpace().remove(box_phy); 
            boxOn = false;
        }
    }



    
}
