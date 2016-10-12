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

package eu.opends.reactionCenter;

import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class TrialLogger 
{
	private int trialNumber = -1;
	private String vpn_age_gender_track = "-1;-1;-1;-1";
	private String vpn = "-1";
	private String age = "-1";
	private String gender = "-1";
	private int task = -1;
	private String task_detail = "not given";
	private int condition_num = -1;
	private String condition_string = "not given";
	private String track = "-1";
	
	private String reaction = "-1";
	private String additional_reaction = "0";
	private String brakeRT_noGas = "";
	private String brakeRT_StartBrake = "";
	private String brakeRT_80pcBrake = "";
	private String brakeRT_success = "";
	private String laneChangeRT_2angle = "";
	private String laneChangeRT_3angle = "";
	private String laneChangeRT_enterLane = "";
	private String laneChangeRT_success = "";
	
	
	public TrialLogger(String condition_string, String comment) 
	{
		this.trialNumber = parseTrialNumber(comment);
		
		// vpn;age;gender;track
		if((!SimulationDefaults.driverName.isEmpty()) &&
				(!SimulationDefaults.driverName.equals("default driver")))
			this.vpn_age_gender_track = SimulationDefaults.driverName;
		
		String[] splitString = vpn_age_gender_track.split(";");
		if(splitString.length >= 4)
		{
			vpn = splitString[0];
			age = splitString[1];
			gender = splitString[2];
			track = splitString[3];
		}
		
		this.condition_string = condition_string;
		this.condition_num = getConditionNum(condition_string);
	}


	private int parseTrialNumber(String comment) 
	{
		String[] splitString = comment.split(":");
		if(splitString.length >= 1)
		{
			String[] splitString2 = splitString[0].split("_");
			if(splitString2.length >= 3)
			{
				try{
					int lastElement = splitString2.length-1;
					String trialString = splitString2[lastElement].replace("Trial", "").trim();
					int trialNumber = Integer.parseInt(trialString);
					return trialNumber;
				} catch (Exception e) {
					return -1;
				}
			}
		}
		return -1;
	}
	
	
	private int getConditionNum(String condition)
	{
		if(condition.equalsIgnoreCase("control_silence"))
			return 0;
		else if(condition.equalsIgnoreCase("congruent"))
			return 1;
		else if(condition.equalsIgnoreCase("incongruent"))
			return 2;
		else if(condition.equalsIgnoreCase("control_neutral"))
			return 3;
		
		return -1;
	}

	
	public void writeLog()
	{		
		Simulator.getDrivingTaskLogger().reportText(trialNumber + ";" + vpn + ";" + age + ";" + gender + ";" + 
				task + ";" + task_detail + ";" + condition_num + ";" + condition_string + ";" + track + ";" + 
				reaction + ";" + additional_reaction + ";" + brakeRT_noGas + ";" + brakeRT_StartBrake + ";" + 
				brakeRT_80pcBrake +	";" + brakeRT_success + ";" + laneChangeRT_2angle + ";" + 
				laneChangeRT_3angle + ";" + laneChangeRT_enterLane + ";" + laneChangeRT_success);
	}

	
	/**
	 * @param task the task to set
	 */
	public void setTask(int task) 
	{
		this.task = task;
		if(task == 1)
			this.task_detail = "braking";
		else if(task >= 2)
			this.task_detail = "steering";
		else
			this.task_detail = "no";
	}


	/**
	 * @param track the track to set
	 */
	public void setTrack(String track) 
	{
		this.track = track;
	}


	/**
	 * @param reaction the reaction to set
	 */
	public void setReaction(int reaction) 
	{
		this.reaction = reaction+"";
	}


	/**
	 * @param additionalReaction the additional_reaction to set
	 */
	public void setAdditional_reaction(int additionalReaction) 
	{
		additional_reaction = additionalReaction+"";
	}


	/**
	 * @param brakeRTNoGas the brakeRT_noGas to set
	 */
	public void setBrakeRT_noGas(int brakeRTNoGas) 
	{
		if(this.brakeRT_noGas.isEmpty())
			this.brakeRT_noGas = brakeRTNoGas+"";
	}


	/**
	 * @param brakeRTStartBrake the brakeRT_StartBrake to set
	 */
	public void setBrakeRT_StartBrake(int brakeRTStartBrake) 
	{
		if(this.brakeRT_StartBrake.isEmpty())
			this.brakeRT_StartBrake = brakeRTStartBrake+"";
	}


	/**
	 * @param brakeRT_80pcBrake the brakeRT_80pcBrake to set
	 */
	public void setBrakeRT_80pcBrake(int brakeRT_80pcBrake) 
	{
		if(this.brakeRT_80pcBrake.isEmpty())
			this.brakeRT_80pcBrake = brakeRT_80pcBrake+"";
	}


	/**
	 * @param brakeRTSuccess the brakeRT_success to set
	 */
	public void setBrakeRT_success(int brakeRTSuccess) 
	{
		if(this.brakeRT_success.isEmpty())
			this.brakeRT_success = brakeRTSuccess+"";
	}


	/**
	 * @param laneChangeRT_2angle the laneChangeRT_2angle to set
	 */
	public void setLaneChangeRT_2angle(int laneChangeRT_2angle) 
	{
		if(this.laneChangeRT_2angle.isEmpty())
			this.laneChangeRT_2angle = laneChangeRT_2angle+"";
	}


	/**
	 * @param laneChangeRT_3angle the laneChangeRT_3angle to set
	 */
	public void setLaneChangeRT_3angle(int laneChangeRT_3angle) 
	{
		if(this.laneChangeRT_3angle.isEmpty())
			this.laneChangeRT_3angle = laneChangeRT_3angle+"";
	}


	/**
	 * @param laneChangeRTEnterLane the laneChangeRT_enterLane to set
	 */
	public void setLaneChangeRT_enterLane(int laneChangeRTEnterLane) 
	{
		if(this.laneChangeRT_enterLane.isEmpty())
			this.laneChangeRT_enterLane = laneChangeRTEnterLane+"";
	}


	/**
	 * @param laneChangeRTSuccess the laneChangeRT_success to set
	 */
	public void setLaneChangeRT_success(int laneChangeRTSuccess) 
	{
		if(this.laneChangeRT_success.isEmpty())
			this.laneChangeRT_success = laneChangeRTSuccess+"";
	}
	
}
