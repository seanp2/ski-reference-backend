package com.skireference.model.results;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a race who's discipline is either Super G (SG)
 * or Downhill (DH). Both of these disciplines consist of only one
 * competitive run.
 */
public class SpeedRace extends AbstractRace {


	/**
	 *
	 * @param page The document of the FIS result web page
	 * @throws IOException if the web page is invalid
	 */
//	public SpeedRace(String url, String event) throws IOException {
	public SpeedRace(Document page) {
//		super(url, event);
		super(page);

	}

	@Override
	protected ArrayList<Result> initResults(ArrayList<String> bibs,
	                                        ArrayList<String> differences,
	                                        ArrayList<String> resultScores) {
		ArrayList<Result> results = new ArrayList<>();
		ArrayList<String> combinedTimes = this.getCombinedTimes();
		for (int i = 0; i < combinedTimes.size(); i++) {
			SpeedFinish resultOfAthlete;
			resultOfAthlete = new SpeedFinish(i + 1, Integer.parseInt(bibs.get(i)),
					AthleteUtils.minutesToSeconds(combinedTimes.get(i)),
					AthleteUtils.minutesToSeconds(differences.get(i)), Double.parseDouble(resultScores.get(i)));
			results.add(resultOfAthlete);
			}

		for (int i = combinedTimes.size(); i < competitorIDs.size(); i++) {
			DNF result = new DNF(Integer.parseInt(bibs.get(i)));

			results.add(result);
		}
		return results;
	}

	@Override
	public String asResultsCSV() {
		this.getScorers();
		String csv = "Rank , Bib , CompetitorID , Name , Year , Nation ,  Total Time , Diff. , Prev. FIS Points, Score,\n";
		for (int i = 0; i < this.results.size() ; i++) {
			RaceAthlete athlete = this.results.get(i);
			Result result = this.getResults().get(i).getResult();
			if (result instanceof  Finish) {
				csv += result.getRank() + " , " +
						result.getBib() + ", " +
						athlete.getCompetitorID() + " , " +
						athlete.getLastfirstName() + " , " +
						athlete.getBirthyear() + " , " +
						athlete.getNation() + " , " +
						AthleteUtils.secondsToMinutes(result.getCombined()) + " , +" +
						result.getDifference() + " , " +
						athlete.getPreviousPoints() + " , " +
						result.getScore() + ",\n";
			} else {
				csv += " , " +
						result.getBib() + ", " +
						athlete.getCompetitorID() + " , " +
						athlete.getLastfirstName() + " , " +
						athlete.getBirthyear() + " , " +
						athlete.getNation() + " , " + AthleteUtils.secondsToMinutes(result.getCombined()) +
						", ," + athlete.getPreviousPoints() + " , " +
						result.getScore() + ",\n";
			}
		}
		return csv;
	}


	/**
	 * Retrieves all of the athletes combined times sorted in order of race rank
	 * @return an array list of strings representing the athletes combined times in order of race rank
	 */
	private ArrayList<String> getCombinedTimes() {
		ArrayList<String> combinedTimes = new ArrayList<>();
		Elements run1OnPage = page.select("#events-info-results .hidden-xs:nth-child(7)");
		for (Element run1Div : run1OnPage) {
			combinedTimes.add(run1Div.ownText());
		}
		return combinedTimes;
	}
}


