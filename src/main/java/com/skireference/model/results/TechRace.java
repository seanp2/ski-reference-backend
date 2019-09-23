package com.skireference.model.results;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a Tech Race with the discipline of either Giant Slalom (GS)
 * or Slalom (SL). Both of these events involve two competitive runs to combine
 * for the final results.
 */
public class TechRace extends AbstractRace {
	/**
	 * @param page The document of the FIS result web page\
	 * @throws IOException if the web page is invalid
	 */
	public TechRace(Document page) {
		super(page);
	}

	@Override
	protected ArrayList<Result> initResults(ArrayList<String> bibs,
	                                        ArrayList<String> differences,
	                                        ArrayList<String> resultScores) {
		ArrayList<Result> results = new ArrayList<>();
		ArrayList<String> run1Times = this.getRun1Times();
		ArrayList<String> run2Times = this.getRun2Times();
		ArrayList<String> combinedTimes = this.getCombinedTimes();
		TechFinish resultOfAthlete;
		for (int i = 0; i < combinedTimes.size(); i++) {
			resultOfAthlete = new TechFinish(i + 1, Integer.parseInt(bibs.get(i)),
					AthleteUtils.minutesToSeconds(run1Times.get(i)), AthleteUtils.minutesToSeconds(run2Times.get(i)),
					AthleteUtils.minutesToSeconds(differences.get(i)), Double.parseDouble(resultScores.get(i)));
			results.add(resultOfAthlete);
		}
		for (int i = combinedTimes.size(); i < competitorIDs.size(); i++) {
			DNF result = new DNF(Integer.parseInt(bibs.get(i)));
			results.add(result);
		}
		return results;
	}


	private ArrayList<String> getRun2Times() {
		ArrayList<String> run2Times = new ArrayList<>();
		Elements run2OnPage = page.select("#events-info-results .hidden-xs:nth-child(8)");
		for (Element run2Div : run2OnPage) {
			run2Times.add(run2Div.ownText());
		}
		return  run2Times;
	}

	private ArrayList<String> getRun1Times() {
		ArrayList<String> run1Times = new ArrayList<>();
		Elements run1OnPage = page.select("#events-info-results .hidden-xs:nth-child(7)");
		for (Element run1Div : run1OnPage) {
			run1Times.add(run1Div.ownText());
		}
		return run1Times;
	}

	private ArrayList<String> getCombinedTimes() {
		ArrayList<String> combinedTimes = new ArrayList<>();
		Elements combinedTimesOnPage = page.select("#events-info-results .hidden-xs:nth-child(9)");
		for (int i = 0; i < combinedTimesOnPage.size(); i++) {
			combinedTimes.add(combinedTimesOnPage.get(i).ownText());
		}
		return combinedTimes;
	}


	@Override
	public String asResultsCSV() {
		this.getScorers();
		String csv = "Rank , Bib , CompetitorID , Name, Year , Nation , Run 1, Run 2, " +
				"Total Time , Diff. , Prev. FIS Points, Score,\n";


		for (int i = 0; i < this.results.size(); i++) {
			RaceAthlete athlete = this.results.get(i);
			Result result = this.getResults().get(i).getResult();
			if (result instanceof TechFinish) {
				csv += result.getRank() + " , " +
						result.getBib() + ", " +
						athlete.getCompetitorID() + " , " +
						athlete.getLastfirstName() + " , " +
						athlete.getBirthyear() + " , " +
						athlete.getNation() + " , " +
						AthleteUtils.secondsToMinutes(((TechFinish) result).getFirstRun()) + " , " +
						AthleteUtils.secondsToMinutes(((TechFinish) result).getSecondRun()) + " , " +
						AthleteUtils.secondsToMinutes(result.getCombined()) + " , +" +
						result.getDifference() + " , " +
						athlete.getPreviousPoints() + " , " +
						result.getScore() + ",\n";
			} else {
				// Result is of Type DNF, first run and second run info is unavailable
				csv += " , "+ result.getBib() + ", " +
						athlete.getCompetitorID() + " , " +
						athlete.getLastfirstName() + " , " +
						athlete.getBirthyear() + " , " +
						athlete.getNation() + " , , , " + result.getCombined() +
						", ," +
						athlete.getPreviousPoints() + " , " +
						result.getScore() + ",\n";
			}
		}
		return csv;
	}
}


