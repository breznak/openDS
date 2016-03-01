/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2015 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.oculusRift;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This Control maintains a reference to a Camera,
 * which will be synched with the position (worldTranslation)
 * of the current spatial.
 * @author tim, reden
 */
public class StereoCameraControl extends CameraControl {

    private Quaternion lookDirection = new Quaternion();
    protected Camera camera2;
    private float camHalfDistance = 0f;
    private Vector3f cameraOffset = new Vector3f(), spatialOffset;

    public StereoCameraControl(){
        super();
        cameraOffset.setX(camHalfDistance);
    }
    
    public StereoCameraControl(Camera camera, Camera camera2) {
        super(camera);
        this.camera2 = camera2;
        cameraOffset.setX(camHalfDistance);
    }
    
    public StereoCameraControl(Camera camera, Camera camera2, ControlDirection controlDir) {
        super(camera, controlDir);
        this.camera2 = camera2;
        cameraOffset.setX(camHalfDistance);
    }
    
    public void setSpatialOffset(Vector3f pos) {
        if( spatialOffset == null ) spatialOffset = new Vector3f();
        spatialOffset.x = pos.x;
        spatialOffset.y = pos.y;
        spatialOffset.z = pos.z;
    }
    
    public void setSpatialOffset(float x, float y, float z) {
        if( spatialOffset == null ) spatialOffset = new Vector3f();
        spatialOffset.x = x;
        spatialOffset.y = y;
        spatialOffset.z = z;
    }
    
    public void swapCameras() {
        Camera mycam = getCamera();
        setCamera(camera2);
        setCamera2(mycam);
    }
    
    // fields used, when inversing ControlDirection:
    @Override
    protected void controlUpdate(float tpf) {
        Camera camera = getCamera();
        ControlDirection controlDir = this.getControlDir();
                
        if (spatial != null && camera != null) {
            TempVars vars = TempVars.get();
            switch (controlDir) {
                case SpatialToCamera:
                    vars.vect4.set(spatial.getWorldTranslation());
                    if( spatialOffset != null ) {
                        vars.vect4.x += spatialOffset.x;
                        vars.vect4.y += spatialOffset.y;
                        vars.vect4.z += spatialOffset.z;
                    }

                    // positional tracking
                    vars.vect4.addLocal(OculusRift.getPosition());
                    
                    lookDirection.set(OculusRift.getOrientation());                    
                    vars.quat1.set(spatial.getWorldRotation()).multLocal(lookDirection);
                    camera.setRotation(vars.quat1); //spatial.getWorldRotation().mult(lookDirection));
                    vars.vect1.set(vars.vect4).addLocal(vars.quat1.mult(cameraOffset, vars.vect2)); //camera.setLocation(spatial.getWorldTranslation().add(camera.getRotation().mult(cameraOffset)));                    
                    camera.setLocation(vars.vect1);
                    
                    // negate cameraOffset
                    vars.vect3.x = -cameraOffset.x;
                    vars.vect3.y = -cameraOffset.y;
                    vars.vect3.z = -cameraOffset.z;
                    
                    camera2.setLocation(vars.vect1.set(vars.vect4).addLocal(camera.getRotation().mult(vars.vect3, vars.vect2))); //spatial.getWorldTranslation().add(camera.getRotation().mult(cameraOffset.negate())));
                    camera2.setRotation(camera.getRotation());
                    break;
                case CameraToSpatial:
                    // set the localtransform, so that the worldtransform would be equal to the camera's transform.
                    // Location:

                    Vector3f vecDiff = vars.vect1.set(camera.getLocation()).subtractLocal(spatial.getWorldTranslation());
                    spatial.setLocalTranslation(vecDiff.addLocal(spatial.getLocalTranslation()));

                    // Rotation:
                    Quaternion worldDiff = vars.quat1.set(camera.getRotation()).subtractLocal(spatial.getWorldRotation());
                    spatial.setLocalRotation(worldDiff.addLocal(spatial.getLocalRotation()));
                    break;
            }
            vars.release();
        }else if (spatial == null){
            throw new NullPointerException("Spatial can't be null!");
        } 
    }

    public Quaternion getLookDirection() {
        return lookDirection;
    }

    public void setLookDirection(Quaternion lookDirection) {
        this.lookDirection = lookDirection;
    }
    
    private static final String CONTROL_DIR_NAME = "controlDir";
    private static final String CAMERA_NAME = "camera";
    
    
    public void increaseDistance(){
        camHalfDistance += 0.001f;
        cameraOffset.setX(camHalfDistance);
        Logger.getLogger(StereoCameraControl.class.getName()).log(Level.INFO, "Cam offset: " + camHalfDistance);
    }
    
    public void decreaseDistance(){
        camHalfDistance -= 0.001f;
        cameraOffset.setX(camHalfDistance);
        Logger.getLogger(StereoCameraControl.class.getName()).log(Level.INFO, "Cam offset: " + camHalfDistance);
    }

    public float getCamHalfDistance() {
        return camHalfDistance;
    }

    public void setCamHalfDistance(float camHalfDistance) {
        this.camHalfDistance = camHalfDistance;
        cameraOffset.setX(camHalfDistance);
    }

    public Camera getCamera2(){
        return camera2;
    }
    
    public void setCamera2(Camera cam2){
        this.camera2 = cam2;
    }
    
    @Override
    public Control cloneForSpatial(Spatial newSpatial) {
        StereoCameraControl control = new StereoCameraControl(getCamera(), camera2, getControlDir());
        control.setSpatial(newSpatial);
        control.setEnabled(isEnabled());
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        setControlDir(ic.readEnum(CONTROL_DIR_NAME, ControlDirection.class, ControlDirection.SpatialToCamera));
        setCamera((Camera)ic.readSavable(CAMERA_NAME, null));
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(getControlDir(), CONTROL_DIR_NAME, ControlDirection.SpatialToCamera);
        oc.write(getCamera(), CAMERA_NAME, null);
    }
    
}