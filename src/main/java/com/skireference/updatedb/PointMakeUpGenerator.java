package com.skireference.updatedb;

import com.skireference.model.results.AthleteUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * The main method of this class is used to update the database.
 */
public class PointMakeUpGenerator {
	private static int startList = 285;
	private static int endList = 285;
	private static String[] pointsNames = new String[]{"SL", "GS" , "SG" , "DH"};
	private static int listsBackForIncrease = 19;
	private static int startAtAthleteIndex = 0;


	/**
	 * This method is used to update the database every time a new FIS points list
	 * comes out. Updates each High and Low scores which are averaged to create an
	 * athletes world ranking FIS points in a discipline.
	 * Does this for each discipline, for every athlete in any given FIS points list.
	 * Ultimately this database update is used such that the higher of the two scores being
	 * averaged for the world rank is used to gage whether an athlete has lowered their
	 * world ranking at any given FIS alpine race competition.
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String,Athlete> pastAthletes = new HashMap<>();
		Connection dBconnection = new DBconnection().connect();
		AthleteUtils athleteUtils = new AthleteUtils();
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		for (int pointsList = startList; pointsList <= endList; pointsList++) {

			if (pointsList == 231) {
				pointsList++;
			}
			String query = "SELECT " + "GSpoints, SLpoints, SGpoints, DHpoints, " +
					"CompetitorID";
			query += " FROM Ranking WHERE Listid = " + pointsList + " ;";
			System.out.println(query);
			try {
				stmt = dBconnection.createStatement();
				stmt.execute(query);
				rs = stmt.getResultSet();
				int j = 0;
				System.out.println("HI");
				while (rs.next()) {
					j++;
					if (j >= startAtAthleteIndex) {
						String competitorID = rs.getString("Competitorid");
						System.out.println("Points List: " + pointsList + " Athlete: " + j + " Competitor ID: " + competitorID);
						ArrayList<String> accuPoints = new ArrayList<>();
						String gsPoints = rs.getString("GSpoints");
						String slPoints = rs.getString("SLpoints");
						String sgPoints = rs.getString("SGpoints");
						String dhPoints = rs.getString("DHpoints");
						accuPoints.add(slPoints);
						accuPoints.add(gsPoints);
						accuPoints.add(sgPoints);
						accuPoints.add(dhPoints);
						ArrayList<Double> curPoints = pointsToDouble(accuPoints);

						query = "SELECT " + "GSpoints, SLpoints, SGpoints, DHpoints, " +
								"Competitorid ";
						query += ", GShigh, GSlow, SLhigh, SLlow,  SGhigh, SGlow, DHhigh, DHlow ";
						int listsBack;
						if (pointsList == 232) {
							listsBack = 2;
						} else {
							listsBack = 1;
						}
						query += "FROM Ranking WHERE Listid = " + (pointsList - listsBack) + " AND Competitorid='" + competitorID + "';";
						stmt = dBconnection.createStatement();
						rs2 = stmt.executeQuery(query);
						accuPoints = new ArrayList<>();
						ArrayList<Double> prevPoints = new ArrayList<>();
						ArrayList<Double[]> prevMakeUp = new ArrayList<>();
						int count = 0;
						while (rs2.next()) {
							count++;
							String prevGS = rs2.getString("GSpoints");
							String prevSL = rs2.getString("SLpoints");
							String prevSG = rs2.getString("SGpoints");
							String prevDH = rs2.getString("DHpoints");
							accuPoints.add(prevSL);
							accuPoints.add(prevGS);
							accuPoints.add(prevSG);
							accuPoints.add(prevDH);
							prevPoints = pointsToDouble(accuPoints);
							prevMakeUp.add(new Double[]{Double.parseDouble(rs2.getString("SLhigh")),
									Double.parseDouble(rs2.getString("SLlow"))});
							prevMakeUp.add(new Double[]{Double.parseDouble(rs2.getString("GShigh")),
									Double.parseDouble(rs2.getString("GSlow"))});
							prevMakeUp.add(new Double[]{Double.parseDouble(rs2.getString("SGhigh")),
									Double.parseDouble(rs2.getString("SGlow"))});
							prevMakeUp.add(new Double[]{Double.parseDouble(rs2.getString("DHhigh")),
									Double.parseDouble(rs2.getString("DHlow"))});
						}
						boolean isFirstList = (count == 0);
						if (isFirstList) {
							prevPoints.add(990.0);
							prevPoints.add(990.0);
							prevPoints.add(990.0);
							prevPoints.add(990.0);
						}
						ArrayList<Double[]> hiLoPoints = new ArrayList<>();
						if (isFirstList) {
							for (int eventCode = 0; eventCode < curPoints.size(); eventCode++) {
								double points = curPoints.get(eventCode);
								if (points < prevPoints.get(eventCode)) {
									hiLoPoints.add(new Double[]{points, points});
								} else {
									hiLoPoints.add(new Double[]{990.0, 990.0});
								}
							}
							try {
								pastAthletes.put(competitorID, new Athlete(Integer.parseInt(competitorID)));
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							Athlete athlete = pastAthletes.get(competitorID);
							if (athlete == null) {
								try {
									athlete = new Athlete(Integer.parseInt(competitorID));
									pastAthletes.put(competitorID, athlete);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							for (int eventCode = 0; eventCode < curPoints.size(); eventCode++) {
								if (curPoints.get(eventCode) < prevPoints.get(eventCode)) {
									System.out.println("POINTS DECREASED");
									Double[] sup = athlete.getPointsMadeWith(pointsList, listsBack,
											prevMakeUp.get(eventCode), pointsNames[eventCode], athleteUtils);
									hiLoPoints.add(sup);
								} else if (curPoints.get(eventCode) > prevPoints.get(eventCode)) {
									System.out.println("POINTS INCREASED");
									Double[] makeup = athlete.getMakeUpAfterIncrease(pointsList, listsBackForIncrease,
											pointsNames[eventCode], athleteUtils);
									System.out.println("HIGH: " + makeup[0]);
									System.out.println("LOW: " + makeup[1]);
									hiLoPoints.add(makeup);

								} else if (curPoints.get(eventCode).equals(prevPoints.get(eventCode))) {
									hiLoPoints.add(prevMakeUp.get(eventCode));
									// Points are equal, get the HiLo from the event in the last list
								}
							}
						}
						query = "UPDATE Ranking " +
								"    SET SLhigh = " + hiLoPoints.get(0)[0] +
								"       , SLlow = " + hiLoPoints.get(0)[1] +
								"       , GShigh = " + hiLoPoints.get(1)[0] +
								"       , GSlow = " + hiLoPoints.get(1)[1] +
								"       , SGhigh = " + hiLoPoints.get(2)[0] +
								"       , SGlow = " + hiLoPoints.get(2)[1] +
								"       , DHhigh = " + hiLoPoints.get(3)[0] +
								"       , DHlow = " + hiLoPoints.get(3)[1] +
								"    WHERE Listid =" + pointsList  + " AND Competitorid = " + competitorID + ";";
						stmt = dBconnection.createStatement();
						stmt.executeUpdate(query);
					}
				}
				} catch(SQLException e){
					e.printStackTrace();
				}
			}

	}

	private static ArrayList<Double> pointsToDouble(ArrayList<String> stringPoints) {
		ArrayList<Double> doublePoints = new ArrayList<>();
		for (int i = 0; i < stringPoints.size(); i ++) {
			try {
				doublePoints.add(Double.parseDouble(stringPoints.get(i)));
			} catch (NumberFormatException e) {
				doublePoints.add(990.0);
			}
		}
		return doublePoints;
	}
}



