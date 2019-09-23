package com.skireference.model.results;


/**
 * Represents a result in which an athlete did not finish the race
 */
public class DNF implements Result {
	int bib;

	public DNF(int bib) {
		this.bib = bib;
	}


	@Override
	public String getCombined() {
		return "DNF";
	}


	@Override
	public double getScore() {
		return 990.0;
	}

	@Override
	public int getBib() {
		return this.bib;
	}

	@Override
	public double getDifference() {
		return 0.0;
	}


	@Override
	public String getRank() {
		return "NA";
	}
}
