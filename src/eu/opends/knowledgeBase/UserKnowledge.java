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

package eu.opends.knowledgeBase;

import de.dfki.automotive.kapcom.knowledgebase.KAPcomException;
import de.dfki.automotive.kapcom.knowledgebase.NetClient;
import de.dfki.automotive.kapcom.knowledgebase.NetClient.PropertyChangedNotificationEvent;
import de.dfki.automotive.kapcom.knowledgebase.NetClient.PropertyChangedNotificationListener;
import de.dfki.automotive.kapcom.knowledgebase.ontology.*;
import de.dfki.automotive.kapcom.knowledgebase.ontology.Demographics.Gender_Enum;

/**
 * 
 * @author Michael Feld, Rafael Math
 */
public class UserKnowledge implements PropertyChangedNotificationListener 
{
	private KnowledgeBase kb;
	private String userID;
	private Seat driverSeat = null;
	private User user = null;

	private UserName userNameConcept = null;
	private String firstName = null;
	private String lastName = null;
	private Integer age = null;
	private Gender_Enum gender = null;
	
	private long notifIdPassenger;

	UserKnowledge(KnowledgeBase kb)
	{
		this.kb = kb;
		if (kb.isConnected()) {
			// get current user
			try {
				//for (Seat seat : kb.getRoot().thisVehicle().interior().seating().seats().seat_items()) {
				//	if (seat.getShortID().equals("driverSeat")) {
				//		userID = seat.getPassenger();
				//	}
				//}
				driverSeat = new Seat(kb.getClient(), "/thisVehicle/interior/seating/seats/driverSeat");
				userID = driverSeat.getPassenger();

				// register for change notification (e.g. passenger)
				notifIdPassenger = kb.getClient().SendRegisterPropertyNotification(driverSeat.getID(), "passenger", true, true, null, this);
			} catch (Exception e) {
				System.err.println("Failed to determine driver seat passenger in knowledge base.");
			}
		} else {
			this.userID = "";
		}
		InitModel();
	}
	
	private synchronized void InitModel()
	{
		// defaults
		firstName = "Unknown"; lastName = "Driver";
		age = 30; gender = Gender_Enum.Male;
		// try to load real data
		if (kb.isConnected() && userID != null && !"".equals(userID)) {
			user = new User(kb.getClient(), userID);
			try {
				// read the static data
				BasicUserDimensions bdim = user.basicDimensions();
				firstName = userID;
				for (UserName name : bdim.names().name_items()) {
					userNameConcept = name;
					firstName = name.getGivenName();
					lastName = name.getFamilyName();
					break;
				}
				Demographics demo = bdim.demographics();
				age = (Integer)demo.getAge();
				gender = demo.getGender();
			} catch (KAPcomException e) {
				System.err.println("Failed to update user model from knowledge base.");
				e.printStackTrace();
			}
		} else {
			// no driver or unknown driver
		}
	}

	@Override
	public void propertySet(PropertyChangedNotificationEvent ev) {
		if (ev.notificationId == notifIdPassenger) {
			// Passenger has changed
			userID = (String)ev.value;
			System.out.println("KAPcom reported change of driver. New driver is: " + userID);
			(new Thread() { public void run() {
				InitModel();
			} } ).start();
		}
	}

	/**
	 * Returns the KAPcom user ID (short name).
	 * 
	 * @return
	 * 			UserID.
	 */
	public String getUserID()
	{
		return (userID == null ? null : NetClient.getShortId(userID));
	}
	
	
	/**
	 * Returns whether this knowledge is currently linked to an actual user in KAPcom.
	 * 
	 * @return
	 * 			True, if knowledge is currently linked to an actual user in KAPcom.
	 */
	public boolean isActualUser()
	{
		return user != null && userID != null && !"".equals(userID);
	}
	
	
	/**
	 * This is used to match the user name stored in KAPcom with the driver name entered in the simulator.
	 * The driver name in the simulator has precedence. It will be updated in KAPcom, but only if it points to a valid user.
	 * If the driver name is NULL, the user name from KAPcom is returned, if it is connected and if there is a user.
	 * 
	 * @param userPreference 
	 * 			The driver name entered by the user, or NULL.
	 * 
	 * @return 
	 * 			The name that should be configured as new driver name.
	 */
	public String initUserName(String userPreference)
	{
		if (userPreference == null && userID == null) return null;
		if (userPreference != null && userPreference.equals(userID)) return userPreference;
		// different names
		if (!kb.isConnected()) {
			if (userPreference != null && !"".equals(userPreference)) {
				userID = userPreference;
				firstName = userPreference;
			}
		} else {
			if (userPreference != null && !"".equals(userPreference)) {
				// use the user-defined name; try to update KAPcom
				boolean found = false; String foundID = "";
				try {
					for (User u : kb.getRoot().allUsers().user_items()) {
						if (userPreference.equals(u.getShortID())) {
							found = true;
							foundID = u.getID();
							break;
						}
					}
				} catch (KAPcomException ex) {
				}
				if (found) {
					// set user in KAPcom
					try {
						driverSeat.setPassenger(foundID);
					} catch (OntologyException e) {
					}
					// note: change is retrieved locally by KAPcom update notification
				}
			} else if (isActualUser()) {
				// use KAPcom user for local driver name
				return userID;
			}
		}
		return userPreference;
	}
	

	
	// ========================================================================================
	// Simple Properties
	// ========================================================================================
	
	public String getUserFirstName() {return firstName;}
	public void setUserFirstName(String value)
	{
		firstName = value;
		if (userNameConcept != null) {
			try {
				userNameConcept.setGivenName(firstName);
			} catch (OntologyException e) {
				System.err.println("Failed to change user name in KAPcom.");
			}
		}
	}
	public String getUserLastName() {return lastName;}
	public String getUserDisplayName()
	{
		return getUserFirstName() + " " + getUserLastName();
	}
	public int getAge()	{return (age == null ? 0 : age.intValue());}
	public boolean isFemale()
	{
		return (gender == Gender_Enum.Female);
	}

	// ========================================================================================
	// Adaptation Rules
	// ========================================================================================
	//
	// Right now, these rules are simple and hardcoded. This might later be changed to use adaptation strategies in KAPcom.
	
	/**
	 * Returns whether the HMI should adapt for "bad sight", e.g. as a result of age or an explicitly stated visual weakness.
	 * 
	 * @return
	 * 			True, if the HMI should adapt for "bad sight"
	 */
	public boolean getAdaptForBadSight()
	{
		return getAge() > 65;
	}
	
	
	/**
	 * Returns how the speaking rate should be adapted. This should be considered a scaling factor.
	 * 
	 * @return
	 * 			Value denoting how the speaking rate should be adapted.
	 */
	public double getAdaptSpeakingRate()
	{
		final int ageMin = 50; final int ageMax = 85; final double scaleMin = 1.15; final double scaleMax = 2.0;
		double r = linearScaling((double)getAge(), ageMin, ageMax, scaleMin, scaleMax, true);
		return r;
	}
	
	
	/**
	 * Returns the adapted speaking rate which can be directly sent to the SVOX TTS.
	 * 
	 * @return
	 * 			Adapted speaking rate.
	 */
	public int getAdaptedSpeakingRate()
	{
		return (int)(getAdaptSpeakingRate() * 100.0);
	}
	
	
	/**
	 * Returns whether TTS speech should be added to all warnings.
	 * For demonstration purposes, this is currently enabled manually in the KAPcom.
	 * 
	 * @return
	 * 			True, if TTS speech should be added to all warnings.
	 */
	public boolean getAdaptAddWarningsSpeech()
	{
		if (user != null) {
			return true;
			//try {
			//	return "1".equals((String)kb.getClient().sendGetProperty(user.getID(), "SpeakWarnings").Value);
			//} catch (KAPcomException e) {
			//	return false;
			//}
		} else {
			return false;
		}
	}
	
	
	/**
	 * Returns how small-scale (milliseconds/seconds range) estimates for reaction time should be adjusted to the current user
	 * as a result of degraded reactions, e.g. because of age. The returned number can be treated as a scaling factor.
	 * It is 1 when no adaptation should be performed.
	 * 
	 * @return
	 * 			Scale of estimates.
	 */
	public double getAdaptReactionTime()
	{
		// currently, we use a simple linear scaling that is not based on any empirical data
		final int ageMin = 60; final int ageMax = 85; final double scaleMin = 1.0; final double scaleMax = 3.0;
		double r = linearScaling((double)getAge(), ageMin, ageMax, scaleMin, scaleMax, true);
		return r;
	}
	
	
	/**
	 * Adapts a display duration for warnings or other messages to the current user. For example, it might increase the time span
	 * for older drivers.
	 * 
	 * @param originalMillis 
	 * 			Time duration to be adjusted (in milliseconds)
	 * 
	 * @return 
	 * 			New duration (in milliseconds)
	 */
	public long getAdaptedDisplayTimeMillis(long originalMillis)
	{
		return (long)((double)originalMillis * getAdaptReactionTime());
	}
	
	
	public int getAdaptedDisplayTimeMillis(int originalMillis)
	{
		return (int)getAdaptedDisplayTimeMillis((long)originalMillis);
	}
	
	
	/**
	 * Returns the voice to be used for TTS output. This value can be passed directly to the SVOX TTS server.
	 * 
	 * @return
	 * 			The voice to be used for TTS output.
	 */
	public String getAdaptedTtsVoice()
	{
		if (KnowledgeBase.CULTURE_GERMAN.equals(kb.getCulture())) {
			// always use female voice (we have only that anyway)
			return "svox-gl0co0de-DE22";
		} else if (gender == Gender_Enum.Female) {
			return "svox-tb5co0en-GB22"; // male voice
		} else {
			return "svox-kh0co0en-GB22"; // female voice
		}
	}
	
	
	private static double linearScaling(double val, double srcMin, double srcMax, double tgtMin, double tgtMax, boolean truncate)
	{
		val = ((val - srcMin) / (srcMax - srcMin) * (tgtMax - tgtMin)) + tgtMin;
		if (truncate) val = Math.max(tgtMin, Math.min(tgtMax, val));
		return val;
	}


}
