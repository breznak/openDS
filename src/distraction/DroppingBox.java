package distraction;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import eu.opends.car.SteeringCar;
import eu.opends.main.Simulator;
/**
 *
 * @author Johnny Marek
 */
public class DroppingBox extends DistractionClass{
    
    private Simulator sim;
    private SteeringCar car;
    private AssetManager manager;
    private Geometry DropBox;
    private Spatial dropingBox;
    private BulletAppState bulletAppState;
    private RigidBodyControl box_phy;
    private float time;
    private float m = 0;
    private Vector3f carPos;
    
    
    public DroppingBox(){
        
}
    
    @Override
    public void initialize(Simulator sim) {
        this.sim = sim;
        this.car = sim.getCar();
        this.manager = sim.getAssetManager();
        this.bulletAppState = sim.getBulletAppState();
        
        box_phy = new RigidBodyControl(2f);
           
        Box box = new Box(1, 1, 1f);  
        DropBox = new Geometry("gula", box);
        Material portal_mat = new Material(manager,
        "Common/MatDefs/Misc/Unshaded.j3md");
        portal_mat.setColor("Color", ColorRGBA.Red);
        DropBox.setMaterial(portal_mat);
        dropingBox = DropBox;
        this.carPos = car.getPosition();
        dropingBox.setLocalTranslation(carPos.x,carPos.y+1,carPos.z-5);
        
        DropBox.addControl(box_phy);
        bulletAppState.getPhysicsSpace().add(box_phy); 
       // box_phy.setLinearVelocity(new Vector3f(0f,0f,-10f));
        
        
        
    }
    
    private Vector3f carPosition(){
        Vector3f position;
        position = car.getPosition();
        return position;
    }
    
    @Override
    public void update (float tpf, float propability){
        int n = (int)(Math.random() * 10) + 1;
        //System.out.println("Box clicked");
        time += tpf;
//        dropingBox.setLocalTranslation(car.getPosition().x, car.getPosition().y, car.getPosition().z-5);
//        sim.getSceneNode().attachChild(dropingBox);
        //if (time > m+5){
            m = time;
            if (n < propability){
                
                System.out.println("Box placed");
                
                
                //dropingBox.setLocalTranslation(carPos.getX(), carPos.getY()+2, carPos.getZ()-5-1*tpf);
                //dropingBox.setLocalTranslation(carPos.x,carPos.y+1.2f,carPos.z-6);
                sim.getSceneNode().attachChild(dropingBox);
                box_phy.setLinearVelocity(new Vector3f(0,0,0));
                box_phy.setAngularVelocity(new Vector3f(0,0,0));
                box_phy.setPhysicsLocation(new Vector3f(0,0,0));
                box_phy.setPhysicsLocation(new Vector3f (car.getPosition().x,car.getPosition().y+1.1f,car.getPosition().z-5));               
             }
        //}
        
    }
    
    @Override
    public void remove (){
        sim.getSceneNode().detachChild(dropingBox);
        System.out.println("Box is REMOVED");
    }



    
}
