package cz.cvut.cognitive.distractors;

import com.jme3.animation.LoopMode;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Matrix3f;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import cz.cvut.cognitive.CogMain;
import eu.opends.main.Simulator;

/**
 *
 * @author Johnny
 * 
 * Class for Pedestrian-type distraction. When this option is selected in
 * options, there is always (set) probability to spawn pedestrian box next to
 * the car.
 * 
 * TODO: add ghostcontrol with physics.
 * 
 */
public class PedestrianDistraction extends DistractionClass{
    //public static Node pedestrianNode = new Node();
    private final Geometry pedestrianGeometry;
    private final MotionPath path;
    private MotionEvent motionControl;
    private final Spatial pedestrianSpatial;
     private final RigidBodyControl pedestrianPhysics;
    private float Timer;
    private boolean pedestrianHit;
    public static int pedestrianHitCount;
    private Vector3f spawn;
    private float distanceLeft;
    private float distanceRight;

    /**
     *Constructor for PedestrianDistraction
     *@param sim - simulator.
     */
      public PedestrianDistraction(Simulator sim, float reward, float probability, float cogDifficulty, String texturePath) {
        super(sim, reward, probability, cogDifficulty);

        //initialize box node
        Material mat = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
        pedestrianGeometry = new Geometry("Pedestrian", new Box(1, 1, 1));
        try{
            TextureKey textureKey = new TextureKey(texturePath, false);
            Texture texture = sim.getAssetManager().loadTexture(textureKey);
            texture.setWrap(Texture.WrapMode.Repeat); //This should set the texture to repeat.
            mat.setTexture("ColorMap",texture);
			
	} catch (Exception e){
            e.printStackTrace();
            System.err.println("Error loading texture file " + texturePath);
	}
        pedestrianGeometry.setMaterial(mat);
        //pedestrianNode.attachChild(boxGeometry);
        
     
        //initialize physics
        pedestrianPhysics = new RigidBodyControl(2f);
        pedestrianGeometry.addControl(pedestrianPhysics);
        pedestrianPhysics.setKinematic(false);
        
        
        pedestrianSpatial = pedestrianGeometry;
        
        pedestrianHit = false;
        //initialize path of pedestrian        
        path = new MotionPath();
        path.addListener(new MotionPathListener() {
            
            @Override
            public void onWayPointReach(MotionEvent me, int i) {

            }
        });
        
        
      }

    /**
     * Update function: if preset probability of Pedestrian is higher than
     * random generated number (1-100), pedestrian will spawn. 
     * Always generates new motionpath of preset positions (TODO: called posit)
     * 
     * TODO: like box add position always next to car / in front (random?)
     * 
     */
    @Override
    public void spawn(float tpf) {
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(camera.getLocation(), camera.getDirection());
            sim.getSceneNode().collideWith(ray, results);
        
            if (results.size() <= 0 || results.getClosestCollision().getDistance() > 21) {
                
                
                int distance_offset = (int)(Math.random() * 5) + 1;
                

                
                Vector3f carPosition = new Vector3f(car.getPosition().x,car.getPosition().y+1.3f,car.getPosition().z );
                Vector3f carHeading = new Vector3f(camera.getDirection());
                float distance = 11+distance_offset;
                spawn = new Vector3f(carPosition.add(carHeading.mult(distance)));
               // Vector3f spawnUp = new Vector3f(spawn.add(carHeading.mult(distance + 5)));
                
                CollisionResults results2 = new CollisionResults();
                Ray ray2 = new Ray(spawn, camera.getLeft());
                sim.getSceneNode().collideWith(ray2, results2);
                CollisionResults results3 = new CollisionResults();
                Ray ray3 = new Ray(spawn, camera.getLeft().negate());
                sim.getSceneNode().collideWith(ray3, results3);
                if (results2.size() > 0) {
                    distanceLeft = results2.getClosestCollision().getDistance() - 1.2f;
                    if (distanceLeft > 10) distanceLeft = 5;
                    if (results3.size() > 0) {
                        distanceRight = results3.getClosestCollision().getDistance() - 1.2f;
                        if (distanceRight > 10) distanceRight = 5;
                    } else {
                        distanceRight = 5;
                    }
                    createPath();
                       
                } else {
                    if (results3.size() > 0) {
                        distanceRight = results3.getClosestCollision().getDistance() - 1.2f;
                        if (distanceRight > 10) distanceRight = 5;
                        if (results2.size() > 0) {
                            distanceLeft = results2.getClosestCollision().getDistance() - 1.2f;
                            if (distanceLeft > 10) distanceLeft = 5;
                        } else {
                            distanceLeft = 5;
                        }
                        createPath();
                    }
                }
            }
        } 
    
    private void createPath(){
        sim.getSceneNode().attachChild(pedestrianSpatial);
        pedestrianSpatial.setLocalTranslation(new Vector3f (car.getPosition().x-10,car.getPosition().y+1.1f,car.getPosition().z-10));
        pedestrianSpatial.setLocalRotation(Matrix3f.IDENTITY);
        
        Vector3f waypointLeft = new Vector3f(spawn.add(camera.getLeft().mult(distanceLeft)));
        Vector3f waypointRight = new Vector3f(spawn.add(camera.getLeft().negate().mult(distanceRight)));;

        path.addWayPoint(waypointRight);
        path.addWayPoint(waypointLeft);
        path.addWayPoint(waypointRight);
        motionControl = new MotionEvent(pedestrianSpatial,path);
        //motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
        //motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
        motionControl.setSpeed(0.3f); 
        motionControl.play();
        motionControl.setLoopMode(LoopMode.Loop);
    }

    /**
     * Remove function: if distractor was spawned before this call, it will be
     * removed from the scene.
     */
    @Override
    public void remove_local() {
            motionControl.stop();
            path.clearWayPoints();
           
            pedestrianPhysics.setLinearVelocity(new Vector3f(0,0,0));
            pedestrianPhysics.setAngularVelocity(new Vector3f(0,0,0));
            pedestrianPhysics.setPhysicsRotation(Matrix3f.IDENTITY);
            bulletAppState.getPhysicsSpace().remove(pedestrianPhysics); 
            bulletAppState.getPhysicsSpace().removeAll(pedestrianSpatial);
            pedestrianSpatial.setLocalRotation(Matrix3f.IDENTITY);
            sim.getSceneNode().detachChild(pedestrianSpatial);
    }
    
    public void collision(float tpf){
            if(!pedestrianHit){
                //pedestrianPhysics.setPhysicsLocation(pedestrianSpatial.getLocalTranslation());
                CollisionResults results = new CollisionResults();

                    car.getCarNode().collideWith(pedestrianSpatial.getWorldBound(), results);
                    if(results.size()>0 && results.getClosestCollision().getDistance() <= 1){
                        motionControl.stop();
                        path.clearWayPoints();
                        
                        bulletAppState.getPhysicsSpace().addAll(pedestrianSpatial);
                        bulletAppState.getPhysicsSpace().add(pedestrianPhysics); 
                        pedestrianPhysics.setLinearVelocity(new Vector3f(0,0,0));
                        pedestrianPhysics.setAngularVelocity(new Vector3f(0,0,0));
                        pedestrianPhysics.setPhysicsRotation(Matrix3f.IDENTITY);
                        pedestrianPhysics.setPhysicsLocation(new Vector3f (pedestrianSpatial.getLocalTranslation())); 
                        
                        CogMain.playerHealth -= ((int)this.REWARD + (int)(car.getCurrentSpeedKmhRounded()*0.1));
                        sim.taskCogLoad.updateHealth();
                        pedestrianHitCount++;
                        pedestrianHit = true;
                    }

            }else{
                CollisionResults results = new CollisionResults();
                car.getCarNode().collideWith(pedestrianSpatial.getWorldBound(), results);
                if(results.size()<=0){
                    Timer = Timer + tpf;
                    if (Timer > 2) {
                        Timer = 0;
                        pedestrianHit = false;
                    }
                } else Timer = 0;
            }    
    }
     
}