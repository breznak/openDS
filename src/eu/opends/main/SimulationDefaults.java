/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2016 Rafael Math
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

package eu.opends.main;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * 
 * @author Rafael Math
 */
public class SimulationDefaults 
{
	public static String driverName = "";
	public static String drivingTaskFileName = "assets/DrivingTasks/Projects/Stadtmitte22/stadtmitte22.xml";
	
	public static Boolean General_pauseAfterStartup = false;
	
	public static boolean Lightning_enableConnection = false;
	public static String Lightning_ip = "192.168.0.1";
	public static int Lightning_port = 1234;
	public static int Lightning_updateRate = 25;
	public static float Lightning_scalingFactor = 1.0f;
	public static boolean sendPosOriAsOneString = false;
	
	public static Boolean KnowledgeManager_enableConnection = true;
	public static String KnowledgeManager_ip = "127.0.0.1";
	public static Integer KnowledgeManager_port = 55432;
	
	public static Boolean Simphynity_enableConnection = false;
	public static String Simphynity_ip = "127.0.0.1";
	public static Integer Simphynity_port = 20777;
	
	public static boolean CANInterface_enableConnection = false;
	public static String CANInterface_ip = "192.168.0.2";
	public static int CANInterface_port = 5678;
	public static int CANInterface_updateRate = 20;
	public static float CANInterface_maxSteeringAngle = 270;
	
	public static boolean MultiDriver_enableConnection = false;
	public static String MultiDriver_ip = "127.0.0.1";
	public static int MultiDriver_port = 1234;
	public static int MultiDriver_updateRate = 20;
	
	public static boolean Eyetracker_enableConnection = false;
	public static int Eyetracker_port = 2010;
	public static int Eyetracker_smoothingFactor = 10;
	public static boolean Eyetracker_crossHairs_show = false;
	public static String Eyetracker_crossHairs_color = "White";
	public static float Eyetracker_crossHairs_scalingFactor = 2;
	public static boolean Eyetracker_gazeSphere_show = false;
	public static String Eyetracker_gazeSphere_color = "Red";
	public static String Eyetracker_highlightObjects_mode = "None";
	public static String Eyetracker_highlightObjects_color = "Orange";
	public static boolean Eyetracker_warningFrame_show = false;
	public static int Eyetracker_warningFrame_threshold = 3000;
	public static int Eyetracker_warningFrame_flashingInterval = 500;
	
	public static String OculusRift_isAttached = "auto";
	
	public static boolean vsimrtiServer_startServer = false;
	public static int vsimrtiServer_port = 1234;
	
	public static boolean SettingsControllerServer_startServer = false;
	public static int SettingsControllerServer_port = 1000;
	
	public static boolean Analyzer_suppressPDFPopup = false;
	
	public static float gravity = 9.81f;
	
	public static Boolean engine_engineOn = true;
	public static Float engine_minSpeed = 0f;
	public static Float engine_maxSpeed = 130f;
	public static Float engine_acceleration = 3.3f;
	public static Float brake_decelerationBrake = 8.7f;
	public static Float brake_decelerationFreeWheel = 0.4f;
	public static Float wheel_frictionSlip = 50.0f;
	public static Float suspension_stiffness = 120.0f;
	public static Float suspension_compression = 0.2f;
	public static Float suspension_damping = 0.3f;
	public static Float light_intensity = 0.0f;
	public static Boolean transmission_automatic = true;
	public static Float transmission_reverseGear = 3.182f;
	public static Float[] transmission_forwardGears = new Float[]{3.615f, 1.955f, 1.281f, 0.973f, 0.778f, 0.646f};
	public static Float engine_minRPM = 750f;
	public static Float engine_maxRPM = 7500f;
	public static Vector3f initialCarPosition = new Vector3f(-36,0,0);
	public static Quaternion initialCarRotation = new Quaternion(0, 0, 0, 1);
	public static String skyTexture = "Textures/Sky/Bright/mountain.dds";
	
	public static Boolean cruiseControl_acc = false;
	public static Float cruiseControl_safetyDistance_lateral = 20f;
	public static Float cruiseControl_safetyDistance_forward = 5f;
	public static Float cruiseControl_emergencyBrakeDistance = 20f;
	public static Boolean cruiseControl_suppressDeactivationByBrake = false;
	public static Float cruiseControl_initialSpeed = 0f;

}
