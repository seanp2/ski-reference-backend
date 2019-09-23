package com.skireference.model.comparison;

import com.skireference.updatedb.Athlete;
import com.skireference.updatedb.BioResult;

import java.util.ArrayList;

public class SharedRace {
	ArrayList<Athlete> athletes;
	private ArrayList<BioResult> individualResults;
	private boolean isShared;

	public SharedRace(ArrayList<Athlete> athletes, BioResult result) {
		this.individualResults = new ArrayList<>();
		this.athletes = athletes;
		this.isShared = isSharedRace(result);
	}

	/**
	 * Checks if the given race is shared by all athletes
	 * Also initializes the individualResults list by adding each athletes
	 * result at the given race into a list.
	 * @param result
	 * @return
	 */
	private boolean isSharedRace(BioResult result) {
		for (int j = 0; j < athletes.size(); j++) {
			ArrayList<BioResult> athleteRaces = athletes.get(j).getAllResults();
			boolean shared = false;
			for (int i = 0; i < athleteRaces.size(); i++) {
				BioResult resultToCompare = athleteRaces.get(i);
				if (resultToCompare.getRaceID().equals(result.getRaceID())) {
					shared = true;
					this.individualResults.add(resultToCompare);
					break;
				}
			}
			if (!shared) {
				return false;
			}
		}
		return true;
	}


	public boolean getIsShared() {
		return isShared;
	}

	public ArrayList<BioResult> getIndividualResults() {
		return this.individualResults;
	}




}
