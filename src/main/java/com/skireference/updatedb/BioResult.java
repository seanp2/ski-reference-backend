package com.skireference.updatedb;

import com.skireference.util.Date;

/**
 * Represents a result as it is displayed in an athletes biography.
 * Only consists of metadata of a race. Contains the race date, the discipline,
 * and the race score the athlete received at the race.
 */
public class BioResult {
	private Date raceDate;
	private String discipline;
	private double score;
	private String venue;
	private String rank;
	private String raceID;

	/**
	 *
	 * @param raceDate date of the race
	 * @param discipline discipline of the race
	 * @param score athletes race score at the race
	 */
	public BioResult(Date raceDate,
	                 String discipline, String venue, String rank, double score, String raceID) {
		this.raceDate = raceDate;
		this.discipline = discipline;
		this.score = score;
		this.venue = venue;
		this.rank = rank;
		this.raceID = raceID;
	}

	/**
	 *
	 * @return the date of the race
	 */
	public Date getDate() {
		return raceDate;
	}

	/**
	 *
	 * @return the score the athlete received at the race
	 */
	public double getScore() {
		return score;
	}

	/**
	 *
	 * @return the discipline of the race
	 */
	public String getDiscipline() {
		return discipline;
	}

	public String getDescription() {
		return this.venue + " [" + this.discipline + "] Rank: " + this.rank + " FIS points: " + this.score;
	}

	public String getRaceID() {
		return this.raceID;
	}

	public String getVenue() {
		return venue;
	}

	public String getRank() {
		return this.rank;
	}

	public String getCSVRankAndScore() {
		return this.rank + "," + this.score + ",";
	}





}
