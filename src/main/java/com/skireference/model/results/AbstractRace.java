package com.skireference.model.results;

import com.skireference.updatedb.DBconnection;
import com.skireference.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractRace implements Race {
	protected Document page;
	protected Date date;
	protected ArrayList<RaceAthlete> results;
	private double penalty;
	private boolean twoRunRace;
	protected ArrayList<String> competitorIDs;
	private ArrayList<String> names;
	private String event;
	private String venue;
	private ArrayList<RaceAthlete> dnfs;
	private ArrayList<RaceAthlete> scorers;
	private ArrayList<Double> prepoints;
	private int pointsList;





	AbstractRace(Document page) throws IllegalArgumentException {
		this.page = page;
		this.event = getEventAcronym(page.select(".event-header__kind").first().ownText());
		if (event.equals("SG") || event.equals("DH")) {
			this.twoRunRace = false;
		} else {
			this.twoRunRace = true;
		}
		this.venue = page.select("div h1").first().ownText();
		String dateAsText = page.select("time span").first().ownText();
		this.date = Date.monthAsLetters(dateAsText);
		this.pointsList = new AthleteUtils().getPointsList(this.date);
		this.competitorIDs = new ArrayList<>();
		this.initCompetitorIDS();
		this.names = this.getNames();
		this.results = new ArrayList<>();
		this.prepoints = this.getPreviousPoints();
		this.initAthletes();
		this.scorers = this.initScorers();
		this.penalty = this.results.get(0).getResult().getScore();
		this.setScorers();


	}

	@Override
	public double getPenalty() {
		return penalty;
	}

	public String getEvent() {
		return this.event;
	}

	public static String getEventAcronym(String fullEventName) {
		String eventNamewithoutGender;
		if (fullEventName.substring(0,1).equals("M")) {
			eventNamewithoutGender = fullEventName.substring(6);
		} else if (fullEventName.substring(0,1).equals("W") || fullEventName.substring(0,1).equals("L")) {
			eventNamewithoutGender = fullEventName.substring(8);
		} else {
			eventNamewithoutGender = fullEventName;
		}
		if (eventNamewithoutGender.equals("Slalom")) {
			return "SL";
		} else if (eventNamewithoutGender.equals("Giant Slalom")) {
			return "GS";
		} else if (eventNamewithoutGender.equals("Super G"))  {
			return "SG";
		} else if (eventNamewithoutGender.equals("Downhill"))  {
			return "DH";
		}
		else {
			throw new IllegalArgumentException("Invalid event name: " + eventNamewithoutGender);
		}
	}

	public static Race buildRace(int raceId) {
		Document racePage = null;
		try {
			racePage = Jsoup.connect("https://www.fis-ski.com/" +
					"DB/general/results.html?sectorcode=AL&raceid=" + raceId).get();
		} catch (IOException e) {
			throw new IllegalArgumentException("Race does not exist");
		}
		String eventType = racePage.select(".event-header__kind").first().ownText();
		Race race = null;
		try {
			if (AbstractRace.getEventAcronym(eventType).equals("GS") ||
					AbstractRace.getEventAcronym(eventType).equals("SL")) {
				race = new TechRace(racePage);
			} else {
				race = new SpeedRace(racePage);
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return race;
	}

//	@Override
//	public ArrayList<RaceAthlete> getScorers() {
//		ArrayList<RaceAthlete> racersWhoScored = new ArrayList<>();
//		int pointsList = new AthleteUtils().getPointsList(this.date);
//		Connection connection = new DBconnection().connect();
//		ResultSet rs;
//		for (int i = 0; i < this.competitorIDs.size(); i++) {
//			try {
//				String query = "SELECT " + this.event + "points FROM FIS_database.Ranking WHERE Listid = " + pointsList + "" +
//						" AND CompetitorID =  " + competitorIDs.get(i) + ";";
//				System.out.println(query);
//				Statement stmt = connection.createStatement();
//				stmt.execute(query);
//				rs = stmt.getResultSet();
//				while(rs.next()) {
//					double points = Double.parseDouble(rs.getString(this.event + "points"));
//					RaceAthlete curAthlete = this.results.get(i);
//					if (points > curAthlete.getResult().getScore())  {
//						racersWhoScored.add(curAthlete);
//					}
//				}
//
//			} catch(SQLException e){
//					e.printStackTrace();
//			}
//		}
//		return racersWhoScored;
//	}

	@Override
	public ArrayList<RaceAthlete> getScorers() {
		return this.scorers;
	}

	public ArrayList<Integer> getScoringIndices() {
		ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < this.scorers.size(); i ++) {
			indices.add(Integer.parseInt(this.scorers.get(i).getResult().getRank()) - 1);
		}

		return indices;
	}

	private ArrayList<RaceAthlete> initScorers() {
		ArrayList<RaceAthlete> racersWhoScored = new ArrayList<>();

		int pointsList = new AthleteUtils().getPointsList(this.date);
		Connection connection = new DBconnection().connect();
		ResultSet rs;
		try {
			String query = "SELECT " + this.event + "points, " + this.event + "high, " + this.event + "low" +
					", Competitorid FROM Ranking WHERE listid = " + pointsList;
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
			ArrayList<Double> allPoints = new ArrayList<>();
			ArrayList<Double> allHighs = new ArrayList<>();
			ArrayList<String> allCompIDs = new ArrayList<>();
			int i = 0;
			ArrayList<Integer> athleteIndexes = new ArrayList<>();
			ArrayList<String> compIDS = this.competitorIDs;
			while (rs.next()) {
				if (compIDS.indexOf(rs.getString("Competitorid")) >= 0) {
					athleteIndexes.add(i);
				}
				double high = Double.parseDouble(rs.getString(event + "high"));
				double low = Double.parseDouble(rs.getString(event + "low"));
				double points = 990.0;
				if (!rs.getString(this.event + "points").equals("NA") &&
						!rs.getString(this.event + "points").equals( "")) {
					points = Double.parseDouble(rs.getString(this.event + "points"));
				}
				if (Math.abs(((high + low) / 2) - points) < 0.02) {
					allHighs.add(high);
				} else {
					allHighs.add(points);
				}
				allPoints.add(points);
				allCompIDs.add(rs.getString("Competitorid"));
				i++;
			}
			Double[] sortedPrePoints = new Double[compIDS.size()];
			Double[] sortedHighPoints = new Double[compIDS.size()];
			for (int j = 0 ; j < athleteIndexes.size(); j++) {
				String id = allCompIDs.get(athleteIndexes.get(j));
				Double points = allPoints.get(athleteIndexes.get(j));
				sortedHighPoints[compIDS.indexOf(id)] = allHighs.get(athleteIndexes.get(j));
				sortedPrePoints[compIDS.indexOf(id)] = points;
			}
			for (int m = 0; m < athleteIndexes.size() + 1; m ++) {
				try {
					if (sortedPrePoints[m] == null) {
						sortedPrePoints[m] = 990.0;
					}
					if (sortedHighPoints[m] == null) {
						sortedHighPoints[m] = 990.0;
					}
					results.get(m).setPreviousPoints(sortedPrePoints[m]);
					if (sortedHighPoints[m] > results.get(m).getResult().getScore()) {
						racersWhoScored.add(results.get(m));
					}
				} catch(IndexOutOfBoundsException e) {
					// Without this some races are s.t the last dnf does not receive prev Fis points
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(racersWhoScored.size());
		return racersWhoScored;
	}



	/**
	 * This method must be abstract because combined times have different
	 * HTML class names in tech races vs speed races.
	 *
	 *
	 * Creates a list of type Results representing all of individual results in the race.
	 * The list is sorted by race rank.
	 */

	protected abstract ArrayList<Result> initResults(ArrayList<String> bibs,
	                                                 ArrayList<String> differences,
	                                                 ArrayList<String> resultScores);


	private void setScorers() {
		System.out.println("set scorers called");

		for (int i = 0; i < this.scorers.size(); i ++) {
			System.out.println(i);
			this.scorers.get(i).setScored(true);
		}
	}

	/**
	 * Initializes the results of race.
	 * For each row in the results table on the FIS webpage,
	 * adds a RaceAthlete to represent an individual race result.
	 * A RaceAthlete contains all relevant information such as Name, result information,
	 * rank, and score.
	 * The resulting list of RaceAthlete is sorted by rank of racers in the competition.
	 */
	private void initAthletes() {
		ArrayList<String> names = this.getNames();
		ArrayList<String> bibs = this.getBibs();
		ArrayList<String> birthYears = this.getBirthyears();
		ArrayList<String> countries = this.getCountries();
		ArrayList<String> diffTimes = this.getDifferences();
		ArrayList<String> fisPoints = this.getResultPoints();
		this.dnfs = new ArrayList<>();
		ArrayList<Result> athleteFinishes = this.initResults(bibs, diffTimes, fisPoints);
		for (int i = 0; i < athleteFinishes.size(); i++) {
			try {
				int countryIndexOffset = 0;
				if (countries.size() > competitorIDs.size()) {
					countryIndexOffset = 1;
				}
				RaceAthlete athlete = new RaceAthlete(Integer.parseInt(competitorIDs.get(i)), names.get(i),
						Integer.parseInt(birthYears.get(i)), countries.get(i + countryIndexOffset), athleteFinishes.get(i));
				athlete.setPreviousPoints(this.prepoints.get(i));
				this.results.add(athlete);
				if (athleteFinishes.get(i) instanceof DNF) {
					this.dnfs.add(results.get(i));
				}
			} catch  (NumberFormatException | IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Double[] getScoreMinusPoints() {
		Double[] scoreMinusPoints = new Double[results.size()];
		for (int i = 0; i < this.results.size(); i++) {
			RaceAthlete athlete = results.get(i);
			if (athlete.getResult().getScore() == 990 ) {
				break;
			}
			if ( athlete.getPreviousPoints() != 990) {
				scoreMinusPoints[i] = athlete.getResult().getScore() - athlete.getPreviousPoints();
			}
		}
		return scoreMinusPoints;
	}

	@Override
	public ArrayList<RaceAthlete> getResults() {
		return results;
	}

	@Override
	public double pointsPerSecond() {
		ArrayList<Result> endResults = new ArrayList<>();
		for (int i = 0; i < this.results.size(); i++) {
			endResults.add(results.get(i).getResult());
		}
		ArrayList<Double> pps = new ArrayList<>();
		for (int i = 1; i < endResults.size(); i++) {
			if (endResults.get(i) instanceof Finish) {
				pps.add((endResults.get(i).getScore() - this.penalty) / ((Finish) endResults.get(i)).getDifference());
			}
		}
		double total = 0;
		for (double num : pps) {
			total += num;
		}
		return total / pps.size();
	}



	@Override
	public Date getDate() {
		return this.date;
	}


	@Override
	public String getVenue() {
		if (this.venue.contains(",")) {
			return this.venue.substring(0, venue.indexOf(","));
		} else {
			return this.venue;
		}
	}

	@Override
	public String getLowerCaseCountryInitials() {
		String venueOnPage = page.select("div h1").first().ownText();
		return venue.substring(venueOnPage.indexOf("(") + 1 , venueOnPage.length() - 1).toLowerCase();
	}

	@Override
	public double getFinishRate() {
		return (double) (results.size() - dnfs.size()) / results.size();
	}


	/**
	 * Finds the athlete that moved up in the results the farthest
	 * from their bib start position.
	 * Incredibly unlikely that no one would move up.
	 * @return the athlete who moved up the farthest
	 */
	@Override
	public RaceAthlete attackFromTheBack() throws NullPointerException {
		int biggestJump = 0;
		RaceAthlete curAthlete = null;
		for (int i = 0; i < this.results.size(); i++) {
			Result athleteResult = this.results.get(i).getResult();
			if (!athleteResult.getCombined().contains("DNF")) {
				int jump = athleteResult.getBib() - Integer.parseInt(athleteResult.getRank());
				if (jump > biggestJump) {
					biggestJump = jump;
					curAthlete = this.results.get(i);
				}
			}
		} if (curAthlete == null) {
			throw new NullPointerException("No one moved up in this race. Hmmm, interesting");
		}
		return curAthlete;
	}


	/**
	 * Retrieve all of the competitor ids of the athletes from the results page
	 */
	private void initCompetitorIDS() {
		Elements rows = this.page.select(".table-row");
		for (int i = 0; i < rows.size(); i++) {
			Element row = rows.get(i);
			if (row.hasAttr("href")) {
				String athleteLink = row.attr("href");
				if (athleteLink.contains("competitorid=")) {
					String compID = athleteLink.substring(athleteLink.indexOf("competitorid=") + "competitorid=".length());
					competitorIDs.add(compID);
				}
			}
		}
	}

	/**
	 * Retrieve all of the names ids of the athletes from the results page
	 */
	private ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<>();
		Elements namesOnPage = page.select(".justify-left.bold");
		for (int i = 0; i < namesOnPage.size(); i++) {
			String name = namesOnPage.get(i).ownText();
			if (name.substring(0, 2).equals(name.substring(0, 2).toUpperCase())) {

				names.add(namesOnPage.get(i).ownText());
			}
		}
		return names;
	}

	/**
	 * Retrieves all of the bib numbers of athletes
	 * Bib numbers represent the athletes start order
	 * @return an array list of the bibs of all of the athletes ordered by
	 *         race rank
	 */
	private ArrayList<String> getBibs() {
		ArrayList<String> bibs = new ArrayList<>();
		Elements bibOnPage = page.select(".g-sm-1.gray");
		for (Element bibDiv: bibOnPage) {
			bibs.add(bibDiv.ownText());
		}
		return bibs;
	}

	public List<RaceAthlete> getDnfs() {
		return this.dnfs;
	}

	/**
	 * Retrieves all of the countries that athletes are from
	 * @return an array list of the countries the athletes are from ordered by
	 *         race rank
	 */
	private ArrayList<String> getCountries() {
		// The first 5 countries that come up with this selector are those of race officials
		// Thus, they should not be considered when initializing the athletes
		ArrayList<String> countries = new ArrayList<>();
		Elements countryNamesOnPage = page.select(".country__name-short");
		for (int i = 0; i < countryNamesOnPage.size(); i ++) {
			if (i > 4 ) {
				countries.add(countryNamesOnPage.get(i).ownText());
			}
		}
		return countries;
	}

	/**
	 * Retrieves the birth years of all athletes
	 * @return an array list of the birth years of all of the athletes ordered by
	 *         race rank
	 */
	private ArrayList<String> getBirthyears() {
		ArrayList<String>  birthYears = new ArrayList<>();
		Elements birthYearsOnPage = page.select(".justify-sb :nth-child(5)");
		for (int i = 1; i < birthYearsOnPage.size();i++){
			birthYears.add(birthYearsOnPage.get(i).ownText());
		}
		return birthYears;
	}

	/**
	 * Retrieves the race result score of all athletes
	 * @return an array list of the race result score of all of the athletes ordered by
	 * 	        race rank
	 */
	private ArrayList<String> getResultPoints() {
		ArrayList<String> fisPoints = new ArrayList<>();
		Elements fisPointsOnPage = page.select("#events-info-results .g-xs-3.justify-right");
		for (int i = 0; i < fisPointsOnPage.size(); i++) {
			fisPoints.add(fisPointsOnPage.get(i).ownText());
		}
		return fisPoints;
	}

	private ArrayList<Double> getPreviousPoints() {
		ArrayList<Double> prevPoints = new ArrayList<>();
		Connection dBconnection = new DBconnection().connect();
		for(int i = 0; i < competitorIDs.size(); i ++) {
			try {
				String competitorID = competitorIDs.get(i);
				String query = "SELECT " + this.event + "points FROM FIS_database.Ranking WHERE Listid = "
						+ this.pointsList  + " AND competitorID = " + competitorID;
				Statement statement = dBconnection.createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					String stringPoints = rs.getString(this.event + "points");
					try {
						prevPoints.add(Double.parseDouble(stringPoints));
					} catch (NumberFormatException n) {
						prevPoints.add(990.0);

					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				prevPoints.add(990.0);
			}
		}
		return prevPoints;
	}

	private ArrayList<String> getDifferences() {
		ArrayList<String> diffTimes = new ArrayList<>();
		Elements diffTimesOnPage = page.select("#events-info-results .g-xs-5");
		// The winner has a differential time of 0.0 seconds
		// But on a FIS result page is listed as an empty string
		for (int i = 0; i < diffTimesOnPage.size(); i++) {
			if (i == 0) {
				diffTimes.add("0.00");

			} else {
				diffTimes.add(diffTimesOnPage.get(i).ownText().substring(1));
			}
		}
		return diffTimes;
	}
}
