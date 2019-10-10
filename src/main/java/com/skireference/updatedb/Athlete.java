package com.skireference.updatedb;

import com.skireference.model.results.AthleteUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
import com.skireference.util.Date;


/**
 * @author seanpomerantz

 * A class to represent an athlete. Contains all relevant information to an athlete.
 * This class may be used to get an athletes information from a specific time in the past.
 * Specifically, this means that an athletes can be initialized with FIS points which were once
 * valid in the past, if desired.
 * 
 */
public class Athlete {
	private ArrayList<BioResult> allResults;
	private String name;
	private int compID;

	/**
	 * Represents an FIS alpine ski racing athlete.
	 * Keeps track of all FIS races the athlete has ever competed in
	 * @param competitorID competitor id of the athlete
	 * @throws IOException
	 */
	public Athlete(int competitorID) throws IOException {
		this.compID = competitorID;
		Document bioPage = Jsoup.connect("https://www.fis-ski.com/DB/general/athlete-biography.html?sectorcode=AL" +
				"&limit=10000&type=result&competitorid=" + competitorID).get();
		this.allResults = new ArrayList<>();
		this.name = bioPage.select(".athlete-profile__lastname").first().ownText()
				+ ", " + bioPage.select(".athlete-profile__name").first().ownText();
		System.out.println(name);


		ArrayList<Date> dates = new ArrayList<>();
		Elements datesOnPage = bioPage.select("#results-body .g-lg-4");
		for (int i = 0; i < datesOnPage.size(); i++) {
			try {
				dates.add(new Date(datesOnPage.get(i).ownText(), true));
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		ArrayList<String> disciplines = new ArrayList<>();
		Elements disciplinesOnPage = bioPage.select("#results-body .g-lg-3");
		for (int i = 0; i < disciplinesOnPage.size(); i++) {
			disciplines.add(disciplinesOnPage.get(i).ownText());
		}

		ArrayList<String> ranks = new ArrayList<>();
//		Elements ranksOnPage = bioPage.select("#results-body .g-lg");
//		for (int i = 0; i < ranksOnPage.size(); i++) {
//			ranks.add(ranksOnPage.get(i).ownText());
//		}

		ArrayList<String> venues = new ArrayList<>();
		Elements venuesOnPage = bioPage.select("#results-body .g-lg.hidden-sm-down");
		for (int i = 0; i < venuesOnPage.size(); i++) {
			venues.add(venuesOnPage.get(i).ownText());
		}

		ArrayList<Double> points = new ArrayList<>();
		Elements positionsOnPage = bioPage.select("#results-body .justify-right");
		for (int i = 0; i < positionsOnPage.size(); i++ ) {
			if (positionsOnPage.get(i).children().size() > 1 ) {
				ranks.add(positionsOnPage.get(i).child(0).ownText());
				if (positionsOnPage.get(i).child(0).ownText().contains("D")
				|| positionsOnPage.get(i).child(1).ownText().equals("")) {
					points.add(990.0);
				} else {
					try {
						points.add(Double.parseDouble(positionsOnPage.get(i).child(1).ownText()));
					} catch (NumberFormatException e) {
						//
					}
				}
			}
		}

		Elements racesOnPage = bioPage.select(".table-row");
		ArrayList<String> raceIDs = new ArrayList<>();
		for(int i = 0 ; i < racesOnPage.size(); i++) {
			String href = racesOnPage.get(i).attr("href");
			try {
				String raceid = href.substring(href.indexOf("raceid=") + 7);
				raceIDs.add(raceid);
			} catch(StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}


		}

		for (int i = 0; i < dates.size(); i++) {
			try {
				allResults.add(new BioResult(dates.get(i), disciplines.get(i),
						venues.get(i), ranks.get(i), points.get(i), raceIDs.get(i)));
			}
			catch(IndexOutOfBoundsException e) {
				e.printStackTrace();

			}
		}
	}





	/**
	 * Given a discipline acronym, expands the acronym and returns
	 * the disciplines full name.
	 * @param discipline the discipline acronym
	 * @return the full name of the discipline
	 * @throws IllegalArgumentException if the argument discipline is an invalid
	 *              discipline acronym
	 */
	private String expandEventAcronym(String discipline) throws IllegalArgumentException {
		String event;
		switch (discipline) {
			case "SL":
				event = "Slalom";
				break;
			case "GS":
				event = "Giant Slalom";
				break;
			case "SG":
				event = "Super G";
				break;
			case "DH":
				event = "Downhill";
				break;
			case "AC":
				event = "Alpine Combined";
				break;
			default:
				throw new IllegalArgumentException("Invalid event");
		}
		return event;
	}


	/**
	 * This method should be used when an athletes points go down
	 * at the given points list. Finds the two lowest scores between
	 * the scores of all races within the validity duration of the previous
	 * points list, and the two doubles given in the hiLo array.
	 * The resulting double array represents the athletes new
	 * best two scores in the event which are being averaged (mean)
	 * to create the athletes world ranking FIS points in the discipline.
	 * @param pointsList the points list in which the athletes points
	 *                   are lesser than the one before it
	 * @param lastListDifference the amount of lists back. Will need be > 1
	 *                           if there is a gap between valid FIS points list
	 *                           numbers.
	 * @param hiLo the past scores used to average and create FIS world ranking points
	 * @param eventAcronym discipline acronym
	 * @param aUtil athlete utils used to correlate a results with its points
	 *                      list duration
	 * @return the new two scores used to average to get FIS world ranking points
	 */
	public Double[] getPointsMadeWith(int pointsList, int lastListDifference, Double[] hiLo,
	                                  String eventAcronym, AthleteUtils aUtil) {
		ArrayList<BioResult> racesByEvent = racesByDiscipline(eventAcronym);
		ArrayList<Double> accuScores = new ArrayList<>();
		ArrayList<Double> accuTotal = new ArrayList<>();
 		for (int j = 0; j < racesByEvent.size(); j++) {
			BioResult result = racesByEvent.get(j);
			accuTotal.add(result.getScore());
			if (aUtil.getPointsList(result.getDate()) == pointsList - lastListDifference ||
					aUtil.getPointsList(result.getDate()) == pointsList - lastListDifference - 1) {
				if (result.getScore() < hiLo[0] && hiLo[0] != result.getScore() && hiLo[1] != result.getScore()) {
					accuScores.add(result.getScore());
				}
			} else if (aUtil.getPointsList(result.getDate()) < pointsList - lastListDifference ) {
				break;
			}
		}
		accuScores.add(hiLo[0]);
 		accuScores.add(hiLo[1]);
		accuTotal.add(hiLo[0]);
		accuTotal.add(hiLo[1]);
 		Double[] pointsMadeWith = new Double[2];
 		pointsMadeWith[1] = Collections.min(accuScores);
 		accuScores.remove(pointsMadeWith[1]);
		accuTotal.remove(pointsMadeWith[1]);
		pointsMadeWith[0] = Collections.min(accuScores);
		accuTotal.remove(pointsMadeWith[0]);

		return pointsMadeWith;
	}


	/**
	 * Returns a double array of size two.
	 * These two doubles represent an athletes best two results
	 * during the duration between the dates corresponding to the
	 * pointsList argument and the listsBack argument.
	 * The resulting array represents the two numbers that are
	 * averaged (mean) to form FIS points for world ranking.
	 * @param pointsList last list to consider results from
	 * @param listsBack first list to consider results from
	 * @param eventAcronym discipline acronym
	 * @param athleteUtils athlete utils used to correlate a result with its points
	 *                     list duration
	 * @return the double array of length two with the greater score
	 *         at index 0, and the lesser score at index 1
	 */
	public Double[] getMakeUpAfterIncrease(int pointsList, int listsBack, String eventAcronym,
	                                       AthleteUtils athleteUtils) {
		ArrayList<BioResult> racesByDiscipline = this.racesByDiscipline(eventAcronym);
		System.out.println("SIZE: " + racesByDiscipline.size());
		ArrayList<Double> accuScores = new ArrayList<>();
		for (int i = 0; i < racesByDiscipline.size(); i ++) {
			int list = athleteUtils.getPointsList(racesByDiscipline.get(i).getDate());
			if (list > pointsList - listsBack && list < pointsList) {
				accuScores.add(racesByDiscipline.get(i).getScore());
			} else if (list < pointsList - listsBack && list != 1) {
				break;
			}
		}
		Double[] pointsMadeWith = new Double[2];
		accuScores.add(990.0);
		accuScores.add(990.0);
		pointsMadeWith[1] = Collections.min(accuScores);
		accuScores.remove(pointsMadeWith[1]);
		pointsMadeWith[0] = Collections.min(accuScores);
		return pointsMadeWith;
	}




	/**
	 * Returns a list off all BioResults that are of the given
	 * discipline.
	 * @param discipline the discipline as its acronym
	 * @return the list of results
	 */
	private ArrayList<BioResult> racesByDiscipline(String discipline) {
		String event = expandEventAcronym(discipline);
		ArrayList<BioResult> accu = new ArrayList<>();
		for (BioResult result : this.allResults) {
			if (result.getDiscipline().equals(event)) {
				accu.add(result);
			}
		}
		return accu;
	}

	public ArrayList<BioResult> getAllResults() {
		return allResults;
	}

	public String getName() {
		return this.name;
	}

	public int getCompID() {
		return this.compID;
	}
}
