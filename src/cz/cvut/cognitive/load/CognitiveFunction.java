package cz.cvut.cognitive.load;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import cz.cvut.cognitive.CogMain;
import cz.cvut.cognitive.distractors.BoxDistraction;
import cz.cvut.cognitive.distractors.PedestrianDistraction;
import eu.opends.car.SteeringCar;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author Johnny
 */
public class CognitiveFunction {
    
    private final Simulator sim;
    private final SteeringCar car;
    private final BulletAppState bulletAppState;
    private final GhostControl ghost;
    public String outputFolder;
    public static String saveHere;
    private boolean firstWriting = true;
    private final Camera camera;
    private Vector3f spawn;
    
    
    public static int distScore = 0; //FIXME replace this with a better code
    public static int activeDistCount = 0;
    public static int [] activeDistNames = new int [6];
    
    public static final float COEF_NONSCENE = 0.5f; //TODO optimize coefs
    public static final float COEF_DISTACTORS = 1.0f;
    public static final float COEF_CARSPEED = 0.05f;
    public static final float COEF_ROADDIFFICULTY = 1.0f;
    
    public static final float ROAD_LOOK_RANGE = 20;
    
    
    public CognitiveFunction(Simulator sim){ //create ghostcontrol around car
    this.sim = sim;
    this.car = sim.getCar();
    this.bulletAppState = sim.getBulletAppState();
    this.camera = sim.getCamera();
    CapsuleCollisionShape capsule = new CapsuleCollisionShape(20f, 2f, 1);
    ghost = new GhostControl(capsule);
    car.getCarNode().addControl(ghost);
    bulletAppState.getPhysicsSpace().add(ghost); 
    
    Util.makeDirectory("distractionTaskDATA"); //FIXME use OpenDS logging/visualization facilities
    outputFolder = "distractionTaskDATA"+File.separator+ Util.getDateTimeString();
    saveHere = outputFolder;
    Util.makeDirectory(outputFolder);
    outputFolder = outputFolder +File.separator+"distraction_log.txt";
}
    
    /**
     * 
     * @return number of active objects on scene
     */
    private int getNumObjectsOnScene() {
        int overlappingCount = ghost.getOverlappingCount();
//        ghost.getOverlappingObjects();
        //counts nonscene objects (3 are scene models (from XML) - grass platform, city model, car model)
        if(overlappingCount > 3) { overlappingCount -= 3; }
        else overlappingCount = 0;
        return overlappingCount;
    }
    
    public float getCurrentDifficulty(){ //move control, track interactions, evaluate and score
        float cogLoadScore = (float)(
                getNumObjectsOnScene() * COEF_NONSCENE 
                + distScore * COEF_DISTACTORS 
                + car.getCurrentSpeedKmh() * COEF_CARSPEED 
                + getRoadDifficulty(ROAD_LOOK_RANGE) * COEF_ROADDIFFICULTY);
        //System.out.println(car.getSlopeDegree());
        //checkCollision();
        return cogLoadScore;
    }
    
    public void update() {
        writeToFile();
    }
    
    private int getRoadDifficulty(float range){
        int roadDifficulty =0;
        CollisionResults results = new CollisionResults();
            Ray ray = new Ray(camera.getLocation(), camera.getDirection());
            sim.getSceneNode().collideWith(ray, results);
            Vector3f carPosition = new Vector3f(car.getPosition().x,car.getPosition().y+1.3f,car.getPosition().z );
            Vector3f carHeading = new Vector3f(camera.getDirection());
            if (results.size() <= 0 || results.getClosestCollision().getDistance() > 5) {
                spawn = new Vector3f(carPosition.add(carHeading.mult(range))); //FIXME is this "range"?, was 20
               // Vector3f spawnUp = new Vector3f(spawn.add(carHeading.mult(distance + 5)));
                
                CollisionResults resultsLava = new CollisionResults();
                Ray ray2 = new Ray(spawn, camera.getLeft());
                sim.getSceneNode().collideWith(ray2, resultsLava);
                CollisionResults resultsPrava = new CollisionResults();
                Ray ray3 = new Ray(spawn, camera.getLeft().negate());
                sim.getSceneNode().collideWith(ray3, resultsPrava);
                if (resultsLava.size() > 0) {
                    if (resultsLava.getClosestCollision().getDistance() < ROAD_LOOK_RANGE/2){
                        if (resultsPrava.size() > 0) {
                            if(resultsPrava.getClosestCollision().getDistance() < ROAD_LOOK_RANGE/2){
                                roadDifficulty=0; //System.out.println("ROVINKA LPr");
                            } else roadDifficulty=2; //System.out.println("DOPRAVA Lp");
                        } else roadDifficulty=0; //System.out.println("ROVINKA L");
                                    
                    } else { 
                        if (resultsPrava.size() > 0) {
                            if(resultsPrava.getClosestCollision().getDistance() < ROAD_LOOK_RANGE/2){
                                roadDifficulty=2; //System.out.println("DOLAVA Prl");
                            } else roadDifficulty=4; //System.out.println("KRIZOVATKA");
                        } else roadDifficulty=2; //System.out.println("DOLAVA l");
                             
                    }
                } else {
                    if (resultsPrava.size() > 0) {
                        if(resultsPrava.getClosestCollision().getDistance() < ROAD_LOOK_RANGE/2){
                            roadDifficulty=0; //System.out.println("ROVINKA Pr");
                        } else roadDifficulty=2; //System.out.println("DOPRAVA p");
                    } else roadDifficulty=1; //System.out.println("NIC"); //FIXME what is "nic"? should be =0? 
                }
            }
            return roadDifficulty;
    }
    
    /*
    public void checkCollision(){ //TODO remove?
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(camera.getLocation(), camera.getDirection());
        sim.getSceneNode().collideWith(ray, results);
        //System.out.println(results.size());
        
        if (results.size() > 0
                && results.getClosestCollision().getDistance() <= 10) {
            //System.out.println(results.getCollision(2).getGeometry().getName());
            String name = results.getClosestCollision().getGeometry().getName();
            //System.out.println(results.getFarthestCollision().getGeometry().getName());
            //System.out.println(name);
            //System.out.println("I SEE YOU");
    }
    } 
*/
        
    private void writeToFile(){ //store info into text (speed, obstacles, score, health, turnings)
        Date curDate = new Date();
        if(firstWriting){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFolder))){ 
                writer.write("Distraction Task log from " + Util.getDateTimeString());
                writer.newLine();
                writer.write("Driver: " + SimulationDefaults.driverName );
                writer.newLine();
//                writer.write("Rain intensity: " + DistractionSettings.getIntensityRain() + " Snow intensity: " + DistractionSettings.getIntensitySnow() + " Fog intensity: " + DistractionSettings.getIntensityFog());
                writer.newLine();
                writer.write("Time (ms),   Cog.load Score, Road Score,  Current speed (Km/H),   Current car \"HealthPoints\",   Number of dropboxhit,"
                        + "   Number of pedestrian hits,   Number of nonScene objects,   Number of active distractors,   Names of active distractors ");
                firstWriting = false;
            } 
                catch (IOException e) {
                    e.printStackTrace();
            }
        }
        try (BufferedWriter writer2 = new BufferedWriter(new FileWriter(outputFolder, true))) {
            writer2.write(curDate.getTime() + "," + getCurrentDifficulty() + "," + getRoadDifficulty(ROAD_LOOK_RANGE) + "," + car.getCurrentSpeedKmh() + "," + CogMain.playerHealth + "," 
                    + BoxDistraction.boxHitCount + "," + PedestrianDistraction.pedestrianHitCount + "," + getNumObjectsOnScene()  + "," + activeDistCount  
                    + "," + activeDistNames[0] + "," + activeDistNames[1] + "," + activeDistNames[2] + "," + activeDistNames[3] + "," + activeDistNames[4]
             + "," + activeDistNames[5]);
            writer2.newLine();
           
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }    
}
