package com.skireference.model.live;

public class LiveRaceListing {
	private String date;
	private int liveRaceId;
	private String raceLocation;
	private String raceEvent;

	// World Cup vs FIS vs NorAm etc
	// Used for minimum penalty calculation
	private String raceType;

	public LiveRaceListing(String date, int liveRaceId, String raceLocation, String raceType, String raceEvent) {
		this.date = date;
		this.liveRaceId = liveRaceId;
		this.raceLocation = raceLocation;
		this.raceType = raceType;
		this.raceEvent = raceEvent;
	}

	public String getDate() {
		return date;
	}

	public int getLiveRaceId() {
		return liveRaceId;
	}

	public String getRaceLocation() {
		return raceLocation;
	}

	public String getRaceEvent() {
		return raceEvent;
	}

	public String getRaceType() {
		return raceType;
	}
}
