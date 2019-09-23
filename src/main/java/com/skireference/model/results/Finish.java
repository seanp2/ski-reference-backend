package com.skireference.model.results;

/**
 * Represents a result where an athlete sucessfully completed the race
 */
public abstract class Finish implements Result {
	private int rank;
	private double score;
	private double difference;
	private int bib;


	/**
	 *
	 * @param rank The final ranking of the finish
	 * @param bib The bib (starting place) of the finish
	 * @param difference The difference of time between the winner of the race and this results
	 *                   combined time
	 * @param score the FIS race result score of the result
	 */
	Finish(int rank, int bib, double difference, double score) {
		this.rank = rank;
		this.bib = bib;
		this.difference = difference;
		this.score = score;
	}



	/**
	 * Returns a description of the individual runs of the result as a string.
	 * This will differ for speed and tech finishes, because they have a different
	 * number of runs for each race.
	 */
	protected abstract String getRunsAsString();

	@Override
	public abstract String getCombined();

	@Override
	public String toString() {
		return "Place: " + rank + " from:" + bib + this.getRunsAsString() + " Combined:"
				+ this.getCombined() + " difference = " + difference + " Score: " + score;
	}



	@Override
	public double getScore() {
		return score;
	}

	@Override
	public int getBib() {
		return this.bib;
	}

	@Override
	public double getDifference() {
		return this.difference;
  }

	@Override
	public String getRank() {
		return this.rank + "";
	}

}
