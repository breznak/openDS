/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2016 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU Lesser General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.taskDescription.tvpTask;

import eu.opends.tools.DistanceBar;

public class TVPTaskSettings 
{
	private String leadingCarName;
	
	private Float leadingCarLowerSpeedLimitSpeedReduction;
	private Float leadingCarUpperSpeedLimitSpeedReduction;
	private Float leadingCarLowerSpeedLimitEmergencyBrake;
	private Float leadingCarUpperSpeedLimitEmergencyBrake;
	private Float minSpeedForSpeedReduction;
	private Integer speedReductionDuration;
	private Integer minTimeAllConditionsMet;
	private Boolean resetBrakeLightOnReaction;
	private Boolean resetTurnSignalOnReaction;
	private Boolean resetSpeedReductionOnReaction;
	
	private Float minDistanceToLeadingCar;
	private Float maxDistanceToLeadingCar;
	private String followerCarName;
	private Float minDistanceToFollowerCar;
	private Float maxDistanceToFollowerCar;
	private Float laneOffsetX;
	private Integer brakeLightMinDuration;
	private Integer turnSignalDuration;
	private Integer maxReactionTime;
	private Float longitudinalToleranceLowerBound;
	private Float longitudinalToleranceUpperBound;
	private Float lateralToleranceLowerBound;
	private Float lateralToleranceUpperBound;
	private Float startPositionZ;
	private Float endPositionZ;
	private Integer shutDownAfterXSeconds;
	private Boolean shutDownAtEnd;
	
	private Float hideDistanceTextPositionZ;
	private Float distanceTextScale;
	private Integer distanceTextTop;
	private Integer distanceTextBottom;
	private Integer distanceTextLeft;
	private Integer distanceTextRight;
	private DistanceBar longitudinalDistanceBar;
	private DistanceBar	lateralDeviationBar;
	
	private Integer loggingRate;
	private Boolean writeToDB;
	private String databaseUrl;
	private String databaseUser;
	private String databasePassword;
	private String databaseTable;
	private String conditionName;
	private Integer conditionNumber;
	private String reportTemplate;
	private Boolean additionalTable;

	
	public TVPTaskSettings(String leadingCarName, Float leadingCarLowerSpeedLimitSpeedReduction,
			Float leadingCarUpperSpeedLimitSpeedReduction, Float leadingCarLowerSpeedLimitEmergencyBrake, 
			Float leadingCarUpperSpeedLimitEmergencyBrake, Float minSpeedForSpeedReduction, 
			Integer speedReductionDuration, Integer minTimeAllConditionsMet, 
			Boolean resetBrakeLightOnReaction, Boolean resetTurnSignalOnReaction, 
			Boolean resetSpeedReductionOnReaction, Float minDistanceToLeadingCar, 
			Float maxDistanceToLeadingCar, String followerCarName, 
			Float minDistanceToFollowerCar, Float maxDistanceToFollowerCar, Float laneOffsetX,
			Integer brakeLightMinDuration, Integer turnSignalDuration,
			Integer maxReactionTime, Float longitudinalToleranceLowerBound,
			Float longitudinalToleranceUpperBound, Float lateralToleranceLowerBound, 
			Float lateralToleranceUpperBound, Float startPositionZ, Float endPositionZ, Integer shutDownAfterXSeconds,
			Boolean shutDownAtEnd, Float hideDistanceTextPositionZ, Float distanceTextScale,
			Integer distanceTextTop, Integer distanceTextBottom, Integer distanceTextLeft,
			Integer distanceTextRight, DistanceBar longitudinalDistanceBar, 
			DistanceBar	lateralDeviationBar, Integer loggingRate, Boolean writeToDB, String databaseUrl,
			String databaseUser, String databasePassword, String databaseTable, 
			String conditionName, Integer conditionNumber, 	String reportTemplate,
			Boolean additionalTable) 
	{
		this.leadingCarName = leadingCarName;
		this.leadingCarLowerSpeedLimitSpeedReduction = leadingCarLowerSpeedLimitSpeedReduction;
		this.leadingCarUpperSpeedLimitSpeedReduction = leadingCarUpperSpeedLimitSpeedReduction;
		this.leadingCarLowerSpeedLimitEmergencyBrake = leadingCarLowerSpeedLimitEmergencyBrake;
		this.leadingCarUpperSpeedLimitEmergencyBrake = leadingCarUpperSpeedLimitEmergencyBrake;
		this.minSpeedForSpeedReduction = minSpeedForSpeedReduction;
		this.speedReductionDuration = speedReductionDuration;
		this.minTimeAllConditionsMet = minTimeAllConditionsMet;
		this.resetBrakeLightOnReaction = resetBrakeLightOnReaction;
		this.resetTurnSignalOnReaction = resetTurnSignalOnReaction;
		this.resetSpeedReductionOnReaction = resetSpeedReductionOnReaction;
		this.minDistanceToLeadingCar = minDistanceToLeadingCar;
		this.maxDistanceToLeadingCar = maxDistanceToLeadingCar;
		this.followerCarName = followerCarName;
		this.minDistanceToFollowerCar = minDistanceToFollowerCar;
		this.maxDistanceToFollowerCar = maxDistanceToFollowerCar;
		this.laneOffsetX = laneOffsetX;
		this.brakeLightMinDuration = brakeLightMinDuration;
		this.turnSignalDuration = turnSignalDuration;
		this.maxReactionTime = maxReactionTime;
		this.longitudinalToleranceLowerBound = longitudinalToleranceLowerBound;
		this.longitudinalToleranceUpperBound = longitudinalToleranceUpperBound;
		this.lateralToleranceLowerBound = lateralToleranceLowerBound;
		this.lateralToleranceUpperBound = lateralToleranceUpperBound;
		this.startPositionZ = startPositionZ;
		this.endPositionZ = endPositionZ;
		this.shutDownAfterXSeconds = shutDownAfterXSeconds;
		this.shutDownAtEnd = shutDownAtEnd;
		this.hideDistanceTextPositionZ = hideDistanceTextPositionZ;
		this.distanceTextScale = distanceTextScale;
		this.distanceTextTop = distanceTextTop;
		this.distanceTextBottom = distanceTextBottom;
		this.distanceTextLeft = distanceTextLeft;
		this.distanceTextRight = distanceTextRight;
		this.longitudinalDistanceBar = longitudinalDistanceBar;
		this.lateralDeviationBar = lateralDeviationBar;
		this.loggingRate = loggingRate;
		this.writeToDB = writeToDB;
		this.databaseUrl = databaseUrl;
		this.databaseUser = databaseUser;
		this.databasePassword = databasePassword;
		this.databaseTable = databaseTable;
		this.conditionName = conditionName;
		this.conditionNumber = conditionNumber;
		this.reportTemplate = reportTemplate;
		this.additionalTable = additionalTable;
	}


	/**
	 * @return the leadingCarName
	 */
	public String getLeadingCarName() {
		return leadingCarName;
	}


	/**
	 * @param leadingCarName the leadingCarName to set
	 */
	public void setLeadingCarName(String leadingCarName) {
		this.leadingCarName = leadingCarName;
	}


	/**
	 * @return the leadingCarLowerSpeedLimitSpeedReduction
	 */
	public Float getLeadingCarLowerSpeedLimitSpeedReduction() {
		return leadingCarLowerSpeedLimitSpeedReduction;
	}
	
	
	/**
	 * @return the leadingCarUpperSpeedLimitSpeedReduction
	 */
	public Float getLeadingCarUpperSpeedLimitSpeedReduction() {
		return leadingCarUpperSpeedLimitSpeedReduction;
	}
	

	/**
	 * @param leadingCarLowerSpeedLimitSpeedReduction the leadingCarLowerSpeedLimitSpeedReduction to set
	 */
	public void setLeadingCarLowerSpeedLimitSpeedReduction(Float leadingCarLowerSpeedLimitSpeedReduction) {
		this.leadingCarLowerSpeedLimitSpeedReduction = leadingCarLowerSpeedLimitSpeedReduction;
	}

	
	/**
	 * @param leadingCarUpperSpeedLimitSpeedReduction the leadingCarUpperSpeedLimitSpeedReduction to set
	 */
	public void setLeadingCarUpperSpeedLimitSpeedReduction(Float leadingCarUpperSpeedLimitSpeedReduction) {
		this.leadingCarUpperSpeedLimitSpeedReduction = leadingCarUpperSpeedLimitSpeedReduction;
	}

	
	/**
	 * @return the leadingLowerCarSpeedLimitEmergencyBrake
	 */
	public Float getLeadingCarLowerSpeedLimitEmergencyBrake() {
		return leadingCarLowerSpeedLimitEmergencyBrake;
	}
	

	/**
	 * @return the leadingUpperCarSpeedLimitEmergencyBrake
	 */
	public Float getLeadingCarUpperSpeedLimitEmergencyBrake() {
		return leadingCarUpperSpeedLimitEmergencyBrake;
	}


	/**
	 * @param leadingCarLowerSpeedLimitEmergencyBrake the leadingCarLowerSpeedLimitEmergencyBrake to set
	 */
	public void setLeadingCarLowerSpeedLimitEmergencyBrake(Float leadingCarLowerSpeedLimitEmergencyBrake) {
		this.leadingCarLowerSpeedLimitEmergencyBrake = leadingCarLowerSpeedLimitEmergencyBrake;
	}
	
	
	/**
	 * @param leadingCarUpperSpeedLimitEmergencyBrake the leadingCarUpperSpeedLimitEmergencyBrake to set
	 */
	public void setLeadingCarUpperSpeedLimitEmergencyBrake(Float leadingCarUpperSpeedLimitEmergencyBrake) {
		this.leadingCarUpperSpeedLimitEmergencyBrake = leadingCarUpperSpeedLimitEmergencyBrake;
	}
	
	
	/**
	 * @return the minSpeedForSpeedReduction
	 */
	public Float getMinSpeedForSpeedReduction() {
		return minSpeedForSpeedReduction;
	}


	/**
	 * @param minSpeedForSpeedReduction the minSpeedForSpeedReduction to set
	 */
	public void setMinSpeedForSpeedReduction(
			Float minSpeedForSpeedReduction) {
		this.minSpeedForSpeedReduction = minSpeedForSpeedReduction;
	}
	

	/**
	 * @return the speedReductionDuration
	 */
	public Integer getSpeedReductionDuration() {
		return speedReductionDuration;
	}


	/**
	 * @param speedReductionDuration the speedReductionDuration to set
	 */
	public void setSpeedReductionDuration(Integer speedReductionDuration) {
		this.speedReductionDuration = speedReductionDuration;
	}


	/**
	 * @return the minTimeAllConditionsMet
	 */
	public Integer getMinTimeAllConditionsMet() {
		return minTimeAllConditionsMet;
	}


	/**
	 * @param minTimeAllConditionsMet the minTimeAllConditionsMet to set
	 */
	public void setMinTimeAllConditionsMet(Integer minTimeAllConditionsMet) {
		this.minTimeAllConditionsMet = minTimeAllConditionsMet;
	}


	/**
	 * @return the resetBrakeLightOnReaction
	 */
	public Boolean isResetBrakeLightOnReaction() {
		return resetBrakeLightOnReaction;
	}


	/**
	 * @param resetBrakeLightOnReaction the resetBrakeLightOnReaction to set
	 */
	public void setResetBrakeLightOnReaction(Boolean resetBrakeLightOnReaction) {
		this.resetBrakeLightOnReaction = resetBrakeLightOnReaction;
	}


	/**
	 * @return the resetTurnSignalOnReaction
	 */
	public Boolean isResetTurnSignalOnReaction() {
		return resetTurnSignalOnReaction;
	}


	/**
	 * @param resetTurnSignalOnReaction the resetTurnSignalOnReaction to set
	 */
	public void setResetTurnSignalOnReaction(Boolean resetTurnSignalOnReaction) {
		this.resetTurnSignalOnReaction = resetTurnSignalOnReaction;
	}


	/**
	 * @return the resetSpeedReductionOnReaction
	 */
	public Boolean isResetSpeedReductionOnReaction() {
		return resetSpeedReductionOnReaction;
	}


	/**
	 * @param resetSpeedReductionOnReaction the resetSpeedReductionOnReaction to set
	 */
	public void setResetSpeedReductionOnReaction(
			Boolean resetSpeedReductionOnReaction) {
		this.resetSpeedReductionOnReaction = resetSpeedReductionOnReaction;
	}


	/**
	 * @return the minDistanceToLeadingCar
	 */
	public Float getMinDistanceToLeadingCar() {
		return minDistanceToLeadingCar;
	}


	/**
	 * @param minDistanceToLeadingCar the minDistanceToLeadingCar to set
	 */
	public void setMinDistanceToLeadingCar(Float minDistanceToLeadingCar) {
		this.minDistanceToLeadingCar = minDistanceToLeadingCar;
	}


	/**
	 * @return the maxDistanceToLeadingCar
	 */
	public Float getMaxDistanceToLeadingCar() {
		return maxDistanceToLeadingCar;
	}


	/**
	 * @param maxDistanceToLeadingCar the maxDistanceToLeadingCar to set
	 */
	public void setMaxDistanceToLeadingCar(Float maxDistanceToLeadingCar) {
		this.maxDistanceToLeadingCar = maxDistanceToLeadingCar;
	}


	/**
	 * @return the followerCarName
	 */
	public String getFollowerCarName() {
		return followerCarName;
	}


	/**
	 * @param followerCarName the followerCarName to set
	 */
	public void setFollowerCarName(String followerCarName) {
		this.followerCarName = followerCarName;
	}


	/**
	 * @return the minDistanceToFollowerCar
	 */
	public Float getMinDistanceToFollowerCar() {
		return minDistanceToFollowerCar;
	}


	/**
	 * @param minDistanceToFollowerCar the minDistanceToFollowerCar to set
	 */
	public void setMinDistanceToFollowerCar(Float minDistanceToFollowerCar) {
		this.minDistanceToFollowerCar = minDistanceToFollowerCar;
	}


	/**
	 * @return the maxDistanceToFollowerCar
	 */
	public Float getMaxDistanceToFollowerCar() {
		return maxDistanceToFollowerCar;
	}


	/**
	 * @param maxDistanceToFollowerCar the maxDistanceToFollowerCar to set
	 */
	public void setMaxDistanceToFollowerCar(Float maxDistanceToFollowerCar) {
		this.maxDistanceToFollowerCar = maxDistanceToFollowerCar;
	}


	/**
	 * @return the laneOffsetX
	 */
	public Float getLaneOffsetX() {
		return laneOffsetX;
	}


	/**
	 * @param laneOffsetX the laneOffsetX to set
	 */
	public void setLaneOffsetX(Float laneOffsetX) {
		this.laneOffsetX = laneOffsetX;
	}


	/**
	 * @return the brakeLightMinDuration
	 */
	public Integer getBrakeLightMinDuration() {
		return brakeLightMinDuration;
	}


	/**
	 * @param brakeLightMinDuration the brakeLightMinDuration to set
	 */
	public void setBrakeLightMinDuration(Integer brakeLightMinDuration) {
		this.brakeLightMinDuration = brakeLightMinDuration;
	}


	/**
	 * @return the turnSignalDuration
	 */
	public Integer getTurnSignalDuration() {
		return turnSignalDuration;
	}


	/**
	 * @param turnSignalDuration the turnSignalDuration to set
	 */
	public void setTurnSignalDuration(Integer turnSignalDuration) {
		this.turnSignalDuration = turnSignalDuration;
	}


	/**
	 * @return the maxReactionTime
	 */
	public Integer getMaxReactionTime() {
		return maxReactionTime;
	}


	/**
	 * @param maxReactionTime the maxReactionTime to set
	 */
	public void setMaxReactionTime(Integer maxReactionTime) {
		this.maxReactionTime = maxReactionTime;
	}


	/**
	 * @return the longitudinalToleranceLowerBound
	 */
	public Float getLongitudinalToleranceLowerBound() {
		return longitudinalToleranceLowerBound;
	}


	/**
	 * @param longitudinalToleranceLowerBound the longitudinalToleranceLowerBound to set
	 */
	public void setLongitudinalToleranceLowerBound(
			Float longitudinalToleranceLowerBound) {
		this.longitudinalToleranceLowerBound = longitudinalToleranceLowerBound;
	}


	/**
	 * @return the longitudinalToleranceUpperBound
	 */
	public Float getLongitudinalToleranceUpperBound() {
		return longitudinalToleranceUpperBound;
	}


	/**
	 * @param longitudinalToleranceUpperBound the longitudinalToleranceUpperBound to set
	 */
	public void setLongitudinalToleranceUpperBound(
			Float longitudinalToleranceUpperBound) {
		this.longitudinalToleranceUpperBound = longitudinalToleranceUpperBound;
	}


	/**
	 * @return the lateralToleranceLowerBound
	 */
	public Float getLateralToleranceLowerBound() {
		return lateralToleranceLowerBound;
	}


	/**
	 * @param lateralToleranceLowerBound the lateralToleranceLowerBound to set
	 */
	public void setLateralToleranceLowerBound(Float lateralToleranceLowerBound) {
		this.lateralToleranceLowerBound = lateralToleranceLowerBound;
	}


	/**
	 * @return the lateralToleranceUpperBound
	 */
	public Float getLateralToleranceUpperBound() {
		return lateralToleranceUpperBound;
	}


	/**
	 * @param lateralToleranceUpperBound the lateralToleranceUpperBound to set
	 */
	public void setLateralToleranceUpperBound(Float lateralToleranceUpperBound) {
		this.lateralToleranceUpperBound = lateralToleranceUpperBound;
	}


	/**
	 * @return the startPositionZ
	 */
	public Float getStartPositionZ() {
		return startPositionZ;
	}


	/**
	 * @param startPositionZ the startPositionZ to set
	 */
	public void setStartPositionZ(Float startPositionZ) {
		this.startPositionZ = startPositionZ;
	}


	/**
	 * @return the hideDistanceTextPositionZ
	 */
	public Float getHideDistanceTextPositionZ() {
		return hideDistanceTextPositionZ;
	}


	/**
	 * @param hideDistanceTextPositionZ the hideDistanceTextPositionZ to set
	 */
	public void setHideDistanceTextPositionZ(Float hideDistanceTextPositionZ) {
		this.hideDistanceTextPositionZ = hideDistanceTextPositionZ;
	}


	/**
	 * @return the distanceTextScale
	 */
	public Float getDistanceTextScale() {
		return distanceTextScale;
	}


	/**
	 * @param distanceTextScale the distanceTextScale to set
	 */
	public void setDistanceTextScale(Float distanceTextScale) {
		this.distanceTextScale = distanceTextScale;
	}


	/**
	 * @return the distanceTextTop
	 */
	public Integer getDistanceTextTop() {
		return distanceTextTop;
	}


	/**
	 * @param distanceTextTop the distanceTextTop to set
	 */
	public void setDistanceTextTop(Integer distanceTextTop) {
		this.distanceTextTop = distanceTextTop;
	}


	/**
	 * @return the distanceTextBottom
	 */
	public Integer getDistanceTextBottom() {
		return distanceTextBottom;
	}


	/**
	 * @param distanceTextBottom the distanceTextBottom to set
	 */
	public void setDistanceTextBottom(Integer distanceTextBottom) {
		this.distanceTextBottom = distanceTextBottom;
	}


	/**
	 * @return the distanceTextLeft
	 */
	public Integer getDistanceTextLeft() {
		return distanceTextLeft;
	}


	/**
	 * @param distanceTextLeft the distanceTextLeft to set
	 */
	public void setDistanceTextLeft(Integer distanceTextLeft) {
		this.distanceTextLeft = distanceTextLeft;
	}


	/**
	 * @return the distanceTextRight
	 */
	public Integer getDistanceTextRight() {
		return distanceTextRight;
	}


	/**
	 * @param distanceTextRight the distanceTextRight to set
	 */
	public void setDistanceTextRight(Integer distanceTextRight) {
		this.distanceTextRight = distanceTextRight;
	}


	/**
	 * @return the endPositionZ
	 */
	public Float getEndPositionZ() {
		return endPositionZ;
	}


	/**
	 * @param endPositionZ the endPositionZ to set
	 */
	public void setEndPositionZ(Float endPositionZ) {
		this.endPositionZ = endPositionZ;
	}

	
	/**
	 * @return the longitudinalDistanceBar
	 */
	public DistanceBar getLongitudinalDistanceBar() {
		return longitudinalDistanceBar;
	}
	
	
	/**
	 * @return the lateralDeviationBar
	 */
	public DistanceBar getLateralDeviationBar() {
		return lateralDeviationBar;
	}
	
	
	/**
	 * @return the shutDownAfterXSeconds
	 */
	public Integer isShutDownAfterXSeconds() {
		return shutDownAfterXSeconds;
	}
	
	
	/**
	 * @param shutDownAfterXSeconds the shutDownAfterXSeconds to set
	 */
	public void setShutDownAfterXSeconds(Integer shutDownAfterXSeconds) {
		this.shutDownAfterXSeconds = shutDownAfterXSeconds;
	}
	
	
	/**
	 * @return the shutDownAtEnd
	 */
	public Boolean isShutDownAtEnd() {
		return shutDownAtEnd;
	}


	/**
	 * @param shutDownAtEnd the shutDownAtEnd to set
	 */
	public void setShutDownAtEnd(Boolean shutDownAtEnd) {
		this.shutDownAtEnd = shutDownAtEnd;
	}


	/**
	 * @return the loggingRate
	 */
	public Integer getLoggingRate() {
		return loggingRate;
	}


	/**
	 * @param loggingRate the loggingRate to set
	 */
	public void setLoggingRate(Integer loggingRate) {
		this.loggingRate = loggingRate;
	}


	/**
	 * @return the writeToDB
	 */
	public Boolean isWriteToDB() {
		return writeToDB;
	}


	/**
	 * @param writeToDB the writeToDB to set
	 */
	public void setWriteToDB(Boolean writeToDB) {
		this.writeToDB = writeToDB;
	}


	/**
	 * @return the databaseUrl
	 */
	public String getDatabaseUrl() {
		return databaseUrl;
	}


	/**
	 * @param databaseUrl the databaseUrl to set
	 */
	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}


	/**
	 * @return the databaseUser
	 */
	public String getDatabaseUser() {
		return databaseUser;
	}


	/**
	 * @param databaseUser the databaseUser to set
	 */
	public void setDatabaseUser(String databaseUser) {
		this.databaseUser = databaseUser;
	}


	/**
	 * @return the databasePassword
	 */
	public String getDatabasePassword() {
		return databasePassword;
	}


	/**
	 * @param databasePassword the databasePassword to set
	 */
	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}


	/**
	 * @return the databaseTable
	 */
	public String getDatabaseTable() {
		return databaseTable;
	}


	/**
	 * @param databaseTable the databaseTable to set
	 */
	public void setDatabaseTable(String databaseTable) {
		this.databaseTable = databaseTable;
	}


	/**
	 * @return the conditionName
	 */
	public String getConditionName() {
		return conditionName;
	}


	/**
	 * @param conditionName the conditionName to set
	 */
	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}


	/**
	 * @return the conditionNumber
	 */
	public Integer getConditionNumber() {
		return conditionNumber;
	}


	/**
	 * @param conditionNumber the conditionNumber to set
	 */
	public void setConditionNumber(Integer conditionNumber) {
		this.conditionNumber = conditionNumber;
	}


	/**
	 * @return the reportTemplate
	 */
	public String getReportTemplate() {
		return reportTemplate;
	}


	/**
	 * @param reportTemplate the reportTemplate to set
	 */
	public void setReportTemplate(String reportTemplate) {
		this.reportTemplate = reportTemplate;
	}


	/**
	 * @return the additionalTable
	 */
	public Boolean getUseAdditionalTable() {
		return additionalTable;
	}


	/**
	 * @param additionalTable the additionalTable to set
	 */
	public void setUseAdditionalTable(Boolean additionalTable) {
		this.additionalTable = additionalTable;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TVPTaskSettings [additionalTable=" + additionalTable
				+ ", brakeLightMinDuration=" + brakeLightMinDuration
				+ ", conditionName=" + conditionName + ", conditionNumber="
				+ conditionNumber + ", databasePassword=" + databasePassword
				+ ", databaseTable=" + databaseTable + ", databaseUrl="
				+ databaseUrl + ", databaseUser=" + databaseUser
				+ ", distanceTextBottom=" + distanceTextBottom
				+ ", distanceTextLeft=" + distanceTextLeft
				+ ", distanceTextRight=" + distanceTextRight
				+ ", distanceTextScale=" + distanceTextScale
				+ ", distanceTextTop=" + distanceTextTop + ", endPositionZ="
				+ endPositionZ + ", followerCarName=" + followerCarName
				+ ", hideDistanceTextPositionZ=" + hideDistanceTextPositionZ
				+ ", laneOffsetX=" + laneOffsetX
				+ ", lateralToleranceLowerBound=" + lateralToleranceLowerBound
				+ ", lateralToleranceUpperBound=" + lateralToleranceUpperBound
				+ ", leadingCarName=" + leadingCarName
				+ ", leadingCarSpeedLimitEmergencyBrake="
				+ leadingCarLowerSpeedLimitEmergencyBrake
				+ ", leadingCarSpeedLimitSpeedReduction="
				+ leadingCarLowerSpeedLimitSpeedReduction + ", loggingRate="
				+ loggingRate + ", longitudinalToleranceLowerBound="
				+ longitudinalToleranceLowerBound
				+ ", longitudinalToleranceUpperBound="
				+ longitudinalToleranceUpperBound
				+ ", maxDistanceToFollowerCar=" + maxDistanceToFollowerCar
				+ ", maxDistanceToLeadingCar=" + maxDistanceToLeadingCar
				+ ", maxReactionTime=" + maxReactionTime
				+ ", minDistanceToFollowerCar=" + minDistanceToFollowerCar
				+ ", minDistanceToLeadingCar=" + minDistanceToLeadingCar
				+ ", minSpeedForSpeedReduction=" + minSpeedForSpeedReduction
				+ ", minTimeAllConditionsMet=" + minTimeAllConditionsMet
				+ ", reportTemplate=" + reportTemplate
				+ ", resetBrakeLightOnReaction=" + resetBrakeLightOnReaction
				+ ", resetSpeedReductionOnReaction="
				+ resetSpeedReductionOnReaction
				+ ", resetTurnSignalOnReaction=" + resetTurnSignalOnReaction
				+ ", shutDownAfterXSeconds=" + shutDownAfterXSeconds 
				+ ", shutDownAtEnd=" + shutDownAtEnd
				+ ", speedReductionDuration=" + speedReductionDuration
				+ ", startPositionZ=" + startPositionZ
				+ ", turnSignalDuration=" + turnSignalDuration + ", writeToDB="
				+ writeToDB + "]";
	}

	

}