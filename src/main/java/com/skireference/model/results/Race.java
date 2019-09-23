package com.skireference.model.results;

import com.skireference.util.Date;

import java.util.ArrayList;

public interface Race {
	/**
	 * @return the amount of points added to the FIS world ranking score
	 * for each second slower than the winner of the race an athlete is .
	 */
	double pointsPerSecond();


	/**
	 * Return all athletes for which their performace in this race will cause
	 * their world rank to drop. "Scoring" is a ski racing slang term for
	 * lowering your world rank.
	 * @return all athletes who lowered their world rank with this result
	 */
	ArrayList<RaceAthlete> getScorers();


	/**
	 * @return the venue at which the race was hosted
	 */
	String getVenue();

	/**
	 * @return the date on which the race was hosted
	 */
	Date getDate();

	/**
	 * @return the percentage of athletes who completed all runs of the race
	 */
	double getFinishRate();

	/**
	 * @return a complete list sorted by race rank of all
	 *          individual performances represented as an array
	 *          list of RaceAthlete.
	 */
	ArrayList<RaceAthlete> getResults();

	/**
	 * Returns the results of the race as a CSV string
	 * @return the CSV string of the results
	 */
	String asResultsCSV();


	/**
	 * Attack from the back is ski racing slang for the athlete
	 * who made the biggest jump in rank from their starting position.
	 * @return the athlete who made the biggest jump in results
	 * 	       from their starting position.
	 */
	RaceAthlete attackFromTheBack();

	/**
	 * Creates an array consisting of the difference between each athletes world rank points
	 * and race result points.
	 * @return The array consisting of the difference between each athletes world rank points
	 * 	 and race result points ordered by race ranking.
	 */
	Double[] getScoreMinusPoints();

	String getLowerCaseCountryInitials();
}
