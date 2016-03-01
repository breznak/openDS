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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.opends.oculusRift;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.oculusvr.capi.OvrLibrary;
import com.oculusvr.capi.OvrLibrary.ovrHmd;
import com.oculusvr.capi.OvrQuaternionf;
import com.oculusvr.capi.OvrVector3f;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rickard
 */
public class OculusRift 
{	
    private static HMDInfo info = new HMDInfo();
    
    private static OvrLibrary ovrLib;
    
    private static boolean initHMDSuccess;
    
    public static boolean initialize(){
        ovrLib = OvrLibrary.INSTANCE;
        ovrLib.ovr_Initialize();
        loadedHmd = ovrHmd.create(0);
        initHMDSuccess = loadedHmd != null;
        info.createFakeValues();
        if( initHMDSuccess ){
            updateHMDInfo();
            System.out.println(info);
            loadedHmd.startSensor(OvrLibrary.ovrSensorCaps.ovrSensorCap_Orientation | OvrLibrary.ovrSensorCaps.ovrSensorCap_YawCorrection |
                                  OvrLibrary.ovrSensorCaps.ovrSensorCap_Position | OvrLibrary.ovrDistortionCaps.ovrDistortionCap_NoSwapBuffers,
                                  OvrLibrary.ovrSensorCaps.ovrSensorCap_Orientation);
        } 		
        
        return initHMDSuccess;
    }
    
    public static HMDInfo updateHMDInfo(){
        info.HResolution = OculusRift.getHResolution();
        info.VResolution = OculusRift.getVResolution();
        //info.HScreenSize = OculusRift.getHScreenSize();
        //info.VScreenSize = OculusRift.getVScreenSize();
        //info.VScreenCenter = OculusRift.getVScreenCenter();
        //info.EyeToScreenDistance = OculusRift.getEyeToScreenDistance();
        //info.LensSeparationDistance = OculusRift.getLensSeparationDistance();
        info.InterpupillaryDistance = OculusRift.getInterpupillaryDistance();
        //info.DistortionK = OculusRift.getDistortionK();
        info.DesktopX = OculusRift.getDesktopX();
        info.DesktopY = OculusRift.getDesktopY();
        //info.DisplayDeviceName = OculusRift.getDisplayDeviceName();
        //info.DisplayId = OculusRift.getDisplayId();
        return info;
    }

    public static HMDInfo getHMDInfo() {
        return info;
    }
    
    public static void main(String[] args){
        try {
            initialize();
            System.out.println(isInitialized());
            destroy();
        } catch (Exception ex) {
            Logger.getLogger(OculusRift.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void destroy(){
        Logger.getLogger(OculusRift.class.getName()).log(Level.INFO, "Cleaning up");
        info = null;
        if( loadedHmd == null ) return;
        loadedHmd.stopSensor();
        loadedHmd.destroy();
        loadedHmd = null;
    }
    
    public static boolean isInitialized(){
        return initHMDSuccess;
    }
    
    /*public static float[] latencyTestColor(){
        return OculusRift.latencyTestColor();
    }
    
    public static String latencyTestResult(){
        return OculusRift.latencyTestResult();
    }*/

    public static ovrHmd loadedHmd;
    
    private static Quaternion orientation = new Quaternion();
    private static Vector3f position = new Vector3f(), posAcc = new Vector3f(), angAcc = new Vector3f();
    
    private static float predictValue;
    
    // HMDInfo
    public static int getHResolution() { return loadedHmd==null?1280:loadedHmd.getDesc().Resolution.w; }
    public static int getVResolution() { return loadedHmd==null?800:loadedHmd.getDesc().Resolution.h; }
    //public static float getHScreenSize() { return loadedHmd.getFloat(OvrLibrary., defaultVal)
    //public static float getVScreenSize();
    //public static float getVScreenCenter();
    //public static float getEyeToScreenDistance();
    //public static float getLensSeparationDistance() { return loadedHmd.getDesc().
    public static float getInterpupillaryDistance() { return loadedHmd==null?0.0624f:loadedHmd.getFloat(OvrLibrary.OVR_KEY_IPD, 0.0624f); }
    //public static float[] getDistortionK();
    public static int getDesktopX() { return loadedHmd==null?0:loadedHmd.getDesc().WindowsPos.x; }
    public static int getDesktopY() { return loadedHmd==null?0:loadedHmd.getDesc().WindowsPos.y; }
    //public static String getDisplayDeviceName();
    //public static long getDisplayId();

    public static Quaternion getOrientation() {
        if( loadedHmd == null ) return Quaternion.DIRECTION_Z;
        OvrQuaternionf rot = loadedHmd.getSensorState(ovrLib.ovr_GetTimeInSeconds() + predictValue).Predicted.Pose.Orientation;
        orientation.set(-rot.x, rot.y, -rot.z, rot.w);
        return orientation;
    }
    
    public static Vector3f getPosition() {
        if( loadedHmd == null ) return Vector3f.ZERO;
        OvrVector3f pos = loadedHmd.getSensorState(ovrLib.ovr_GetTimeInSeconds() + predictValue).Predicted.Pose.Position;
        
        position.set(pos.x, pos.y, pos.z);
        return position;     
    }
    
    public static Vector3f getAngularAcceleration() {
        if( loadedHmd == null ) return Vector3f.ZERO;
        OvrVector3f pos = loadedHmd.getSensorState(ovrLib.ovr_GetTimeInSeconds() + predictValue).Predicted.AngularAcceleration;
        angAcc.set(pos.x, pos.y, pos.z);
        return angAcc;                
    }    
    
    public static Vector3f getPositionalAcceleration() {
        if( loadedHmd == null ) return Vector3f.ZERO;
        OvrVector3f pos = loadedHmd.getSensorState(ovrLib.ovr_GetTimeInSeconds() + predictValue).Predicted.LinearAcceleration;
        posAcc.set(pos.x, pos.y, pos.z);
        return posAcc;                
    }
    
    public static void reset() {
        if( loadedHmd == null ) return;
        loadedHmd.resetSensor();
    }
    
    public static void predictive(float value, boolean on) {
        if( !on ) {
            predictValue = 0f;
        } else {
            predictValue = value;
        }
    }
    //public static float[] latencyTestColor();
    //public static String latencyTestResult();

}
