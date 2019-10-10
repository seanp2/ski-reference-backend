package com.skireference.model.results;



/**
 * @author seanpomerantz
 * Represents an athlete's performance in a race. Contains their results in the race, their 
 * start position, as well as the information stored in the Athlete class.
 */
public class RaceAthlete {
	private final int birthyear;
	private final String nation;
	private final String lastfirstName;
	private final int competitorID;
	private final Result result;
	private  double previousPoints;
	private boolean scored;


	public RaceAthlete(int competitorID, String lastfirstName,
			int birthyear, String nation, Result result)
			throws IllegalArgumentException {
		this.lastfirstName = lastfirstName;
		this.competitorID =  competitorID;
		this.birthyear = birthyear;
		this.nation = nation;
		this.result = result;
		this.scored = false;

	}


	public double getPreviousPoints() {
		return previousPoints;
	}


	public void setPreviousPoints(double previousPoints) {
		this.previousPoints = previousPoints;
	}

	public Result getResult() {
		return result;
	}

	public int getCompetitorID() {
		return competitorID;
	}

	public boolean getScored() {
		return scored;
	}


	public String getLastfirstName() {
		return lastfirstName;
	}

	@Override
	public String toString() {
		return lastfirstName + " " + nation + " " + birthyear + " " + this.result.toString() + " Original Points: " + previousPoints;
	}

	public String getNation() {
		return this.nation;
	}

	public String getBirthyear() {
		return this.birthyear + "";
	}

	public void setScored(boolean b) {
		System.out.println("SET");
		this.scored = b;
	}
}
