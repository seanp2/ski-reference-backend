package com.skireference.model.comparison;

import com.skireference.updatedb.Athlete;
import com.skireference.updatedb.BioResult;
import com.skireference.updatedb.DBconnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Comparison {
	private ArrayList<Athlete> athletesToCompare;
	private ArrayList<ArrayList<BioResult>> sharedResults;

	/**
	 *
	 * @param athletesToCompare list of athletes
	 */
	public Comparison(ArrayList<Athlete> athletesToCompare) throws IllegalArgumentException {
		if (athletesToCompare.size() == 0 ) {
			throw new IllegalArgumentException("ArrayList of Athletes must have a size greater than 0");
		}
		this.athletesToCompare = athletesToCompare;
		this.sharedResults = initSharedResults();
	}

	public Comparison(ArrayList<Integer> fisIDs, boolean useFISids) {
		Connection connection = new DBconnection().connect();
		ArrayList<Integer> competitorIDs = new ArrayList<>();
		for (int i = 0; i < fisIDs.size(); i++) {
			String query = "SELECT Competitorid FROM Ranking WHERE Fiscode = " + fisIDs.get(i) +";";
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				rs.next();
				String compIdAsString = rs.getString("Competitorid");
				int competitorID = Integer.parseInt(compIdAsString);
				competitorIDs.add(competitorID);
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}
		}

		this.athletesToCompare = new ArrayList<>();
		for(int j = 0; j < competitorIDs.size(); j++) {
			try {
				athletesToCompare.add(new Athlete(competitorIDs.get(j)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.sharedResults = initSharedResults();
	}


	public ArrayList<Athlete> getAthletes() {
		return athletesToCompare;
	}


	public static Comparison buildByFisID(ArrayList<Integer> fisIDs) {
		Connection connection = new DBconnection().connect();
		ArrayList<Integer> competitorIDs = new ArrayList<>();
		for (int i = 0; i < fisIDs.size(); i++) {
			String query = "SELECT Competitorid FROM Ranking WHERE Fiscode = " + fisIDs.get(i) +";";
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				rs.next();
				String compIdAsString = rs.getString("Competitorid");
				int competitorID = Integer.parseInt(compIdAsString);
				competitorIDs.add(competitorID);
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
			}
		}

		ArrayList<Athlete> athletes = new ArrayList<>();
		for(int j = 0; j < competitorIDs.size(); j++) {
			try {
				athletes.add(new Athlete(competitorIDs.get(j)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new Comparison(athletes);
	}

	public ArrayList<ArrayList<BioResult>> getSharedResults() {
		return sharedResults;
	}

	private ArrayList<ArrayList<BioResult>> initSharedResults() {
		ArrayList<ArrayList<BioResult>> sharedResults = new ArrayList<>();
		Athlete firstAthlete = athletesToCompare.get(0);
		ArrayList<BioResult> allRacesOfFirstAthlete = firstAthlete.getAllResults();

		for (int i = 0; i < allRacesOfFirstAthlete.size(); i++ ) {
			BioResult curRace =  allRacesOfFirstAthlete.get(i);
			SharedRace sharedRace = new SharedRace(this.athletesToCompare, curRace);
			if (sharedRace.getIsShared()) {
				sharedResults.add(sharedRace.getIndividualResults());
			}
		}
		return sharedResults;
	}

	/**
	 * Returns an array of a csv's such that each String in the array list contains
	 * a csv that contains info on all athletes performances in that shared race.
	 * As an example, if we have two athletes, A and B, that both raced at race 1 and race 2.
	 * Lets say the results are:
	 *   * Athlete A, race 1-- Rank: 3 Score: 15
	 *   * Athlete B, race 1-- Rank: 4 Score : 20
	 *   * Athlete A, race 2-- Rank: 5 Score: 30
	 * 	 * Athlete B, race 1-- Rank: 6 Score : 35
	 * The array would look as follows:
	 * 	 * Index 0: 3, 15, 4, 20
	 * 	 * Index 1: 5, 30, 6, 35
	 * @return the array
	 */
	public ArrayList<String> sharedResultsAsCSVs() {
		ArrayList<String> listOfCSV = new ArrayList<>();
		for(int i = 0; i < this.sharedResults.size(); i++) {
			ArrayList<BioResult> individualPerformancesAtThisRace = sharedResults.get(i);
			String csvForThisRace = "Athlete, Rank, Score,\n";
			for(int j= 0; j < individualPerformancesAtThisRace.size(); j++) {
				csvForThisRace += athletesToCompare.get(j).getName().replace(",", " ") + "," + individualPerformancesAtThisRace.get(j).getCSVRankAndScore();
				csvForThisRace += "\n";
			}
			listOfCSV.add(csvForThisRace);
		}
		return listOfCSV;
	}



}
