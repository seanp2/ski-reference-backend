package com.skireference.model.results;


public interface Result {

	/**
	 *
	 * @return the combined time of the result
	 */
	String getCombined();

	/**
	 *
	 * @return the race score of the result
	 */
	double getScore();

	/**
	 *
	 * @return the bib number of the athlete corresponding to this result
	 */
	int getBib();


	/**
	 *
	 * @return the difference between the world rank points of the athlete corresponding to this result
	 * and the race result points of this finish
	 */
	double getDifference();

	/**
	 *
	 * @return the ranking of this result
	 */
	String getRank();


}
