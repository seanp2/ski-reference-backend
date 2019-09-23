package com.skireference.model.tracking;


import com.skireference.model.results.AthleteUtils;
import com.skireference.updatedb.Athlete;
import com.skireference.updatedb.BioResult;
import com.skireference.updatedb.DBconnection;
import com.skireference.util.Date;
import com.skireference.util.MailUtil;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 */
public class DailyTracking {
	private HashMap<Integer, ArrayList<String>> fisCodeToFollowersMap;
	private HashMap<Athlete, ArrayList<String>> athleteToFollowersMap;
	private HashMap<Integer, Integer> competitorIdToResult;
	private int pointsListNumber;
	private Date date;



	public DailyTracking(Date date) {
		this.date = date;
		this.pointsListNumber = new AthleteUtils().getPointsList(date);
		this.fisCodeToFollowersMap = new HashMap<>();
		this.athleteToFollowersMap = new HashMap<>();
		this.competitorIdToResult = new HashMap<>();

		ArrayList<Integer> fisIDs = new ArrayList<>();
		Connection connection = new DBconnection().connect();
		String query = "SELECT * FROM tracking;";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				int fisCode = Integer.parseInt(rs.getString("fisID"));
				String email = rs.getString("email");
				if (!this.fisCodeToFollowersMap.containsKey(fisCode)) {
					ArrayList<String> emailsList = new ArrayList<>();
					this.fisCodeToFollowersMap.put(fisCode, new ArrayList<>());
				}
				if (Integer.parseInt(rs.getString("confirmed")) == 1) {
					this.fisCodeToFollowersMap.get(fisCode).add(email);
				}
			}



			// Find CompetitorIDs using the FIS codes
			for (HashMap.Entry<Integer, ArrayList<String>> entry : fisCodeToFollowersMap.entrySet()) {
				String getCompetitorIDQuery = "SELECT CompetitorID FROM Ranking WHERE Listid = " + pointsListNumber
						+ " AND Fiscode = " + entry.getKey() + ";";
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(getCompetitorIDQuery);
				int competitorID = 0;
				while (resultSet.next()) {
					competitorID = Integer.parseInt(resultSet.getString("Competitorid"));
				}
				if (competitorID > 0) {
					try {
						Athlete athlete = new Athlete(competitorID);
						int raceIndex = this.athleteRaceIndex(athlete);
						if(raceIndex >= 0) {
							athleteToFollowersMap.put(athlete, fisCodeToFollowersMap.get(entry.getKey()));
							competitorIdToResult.put(competitorID, raceIndex);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param athlete
	 * @return -1 if the athlete did not race on the date.
	 *        Otherwise, return the index within the athletes results list
	 *        of the race completed on the day.
	 */
	private int athleteRaceIndex(Athlete athlete) {
			for(int i = 0; i < athlete.getAllResults().size(); i++) {
				BioResult result = athlete.getAllResults().get(i);
				if(result.getDate().equals(this.date)) {
					return i;
				}
			}
		return -1;
	}

	public HashMap<Integer, Integer> getCompetitorIdToResult() {
		return competitorIdToResult;
	}

	public void sendNotifications() {
		MailUtil mailUtil = new MailUtil();
		HashMap<String, StringBuilder> emailToMessageMap = new HashMap<>();
		Message message = null;
		String timeStamp = new SimpleDateFormat("MM/dd/yyyy")
				.format(Calendar.getInstance().getTime());
		try {
			for (HashMap.Entry<Athlete, ArrayList<String>> entry : athleteToFollowersMap.entrySet()) {
				ArrayList<String> followerEmails = entry.getValue();
				for(int i = 0; i < followerEmails.size(); i++) {
					String email = followerEmails.get(i);
					if(!emailToMessageMap.containsKey(email)) {
						emailToMessageMap.put(email, new StringBuilder("<div " +
								"style=\"font-size:larger;font-color:black;\">Here are updates regarding races completed on " +
								timeStamp +
								" by athletes " +
								"you track" + "<br><br>"));
					}
					BioResult result = entry.getKey().getAllResults().get(competitorIdToResult
							.get(entry.getKey().getCompID()));
					emailToMessageMap.get(email).append("* " + entry.getKey().getName() + ": "
							+ result.getDescription() + "<br>" +
							"<a href=\"ski-reference.com/ResultSearch?raceid="
							+ result.getRaceID()
							+"\">Click here for analysis</a><br><br></div>");
				}
			}

			for(HashMap.Entry<String, StringBuilder> entry : emailToMessageMap.entrySet()) {
				Session session =   Session.getInstance(mailUtil.getProperties(), new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailUtil.getEmail(), mailUtil.getPassword());
					}
					});
				  System.out.println(entry.getKey());
				  message = new MimeMessage(session);
					message.setFrom(new InternetAddress(mailUtil.getEmail()));
					message.setRecipient(Message.RecipientType.TO, new InternetAddress(entry.getKey()));
					message.setSubject("Result Update for " + timeStamp + " from Ski-Reference.com");
					entry.getValue().append("<a " +
							"href=\"ski-reference.com/removeTracking?email=" + entry.getKey() + "\">" +
							"Click here to remove a tracking</a>");
					message.setContent(entry.getValue().toString()
							, "text/html");
				Transport.send(message);
				System.out.println("Message Sent");
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		}


	}
}
