package com.skireference.model.results;


/**
 * Represents a complete result completed in a tech race. Consists of two competitive runs.
 */
public class TechFinish extends Finish{
	private int rank;
	private double run1Time;
	private double run2Time;
	private double score;
	private double difference;
	private int bib;

	/**
	 * @param rank The ranking within the race
	 * @param bib the bib number corresponding to where the athlete started the race
	 * @param run1Time The first run time achieved in this result
	 * @param run2Time The second run time achieved in this result
	 * @param difference The difference of time between the winner of the race and this results
	 *                   combined time
	 * @param score
	 */
	public TechFinish(int rank, int bib, double run1Time, double run2Time, double difference, double score) {
		super(rank,bib, difference,score);
		this.rank = rank;
		this.bib = bib;
		this.run1Time = run1Time;
		this.run2Time = run2Time;
		this.difference = difference;
		this.score = score;
	}


	public String getFirstRun() {
		return run1Time + "";
	}

	public String getSecondRun() {
		return run2Time + "";
	}

	@Override
	protected String getRunsAsString() {
		return " Run1:" + this.run1Time + " Run2:" + this.run2Time;
	}

	@Override
	public String getCombined() {
		return run1Time + run2Time + "";
	}
}
