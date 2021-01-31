package com.skireference.model.live;

public class LiveResultRow {
	private String rank;
	private String bib;
	private String name;
	// For now, only care about combined time
	// if only first run is complete, this value
	// will be set to first run time
	private String currentCombinedTime;
	private String diff;
	private float score;

	public LiveResultRow(String rank, String bib, String name, String currentCombinedTime, String diff, float score) {
		this.rank = rank;
		this.bib = bib;
		this.name = name;
		this.currentCombinedTime = currentCombinedTime;
		this.diff = diff;
		this.score = score;
	}

	public String getRank() {
		return rank;
	}

	public String getBib() {
		return bib;
	}

	public String getName() {
		return name;
	}

	public String getCurrentCombinedTime() {
		return currentCombinedTime;
	}

	public String getDiff() {
		return diff;
	}

	public float getScore() {
		return score;
	}
}
