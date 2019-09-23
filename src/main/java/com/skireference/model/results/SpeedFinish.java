package com.skireference.model.results;


/**
 * Represents a finished speed race which consists of only one competitive run
 */
public class SpeedFinish extends Finish {
	private int rank;
	private double score;
	private double difference;
	private int bib;
	private double run1Time;

	/**
	 *
	 * @param rank
	 * @param bib
	 * @param run1Time
	 * @param difference The difference of time between the winner of the race and this results
	 * 	                 combined time
	 * @param score
	 */
	public SpeedFinish(int rank, int bib, double run1Time,  double difference, double score) {
		super(rank,bib, difference,score);
		this.rank = rank;
		this.bib = bib;
		this.run1Time = run1Time;
		this.difference = difference;
		this.score = score;
	}



	@Override
	protected String getRunsAsString() {
		return "Run1:" + this.run1Time;
	}

	@Override
	public String getCombined() {
		return run1Time + "";
	}
}
