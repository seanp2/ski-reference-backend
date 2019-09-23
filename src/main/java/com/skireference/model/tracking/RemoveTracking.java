package com.skireference.model.tracking;

import com.skireference.updatedb.DBconnection;
import com.skireference.util.MailUtil;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.*;
import java.util.ArrayList;

public class RemoveTracking {
	private ArrayList<Integer> fisIDs;
	private String userEmail;
	private ArrayList<String> unremovedTrackings;

	public RemoveTracking(ArrayList<Integer> fisIDs, String userEmail) {
		this.fisIDs = fisIDs;
		this.userEmail = userEmail;
		this.unremovedTrackings = this.initUnRemovedTrackings();
	}

	public void sendRemovalConfirmation() {
		System.out.println("UNREMOVED SIZE: " + getUnremovedTrackings().size());
		System.out.println("TOTAL SIZE: " + fisIDs.size());
		if(unremovedTrackings.size() != fisIDs.size()) {
			MailUtil mailUtil = new MailUtil();
			Session session = Session.getInstance(mailUtil.getProperties(), new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailUtil.getEmail(), mailUtil.getPassword());
				}
			});

			Message message = new MimeMessage(session);
			try {
				String athleteList = "";
				for (int i = 0; i < fisIDs.size(); i++) {
					athleteList += "<br>* " + fisIDs.get(i);
				}
				message.setFrom(new InternetAddress(mailUtil.getEmail()));
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.userEmail));
				message.setSubject("Confirmation of athlete tracking removal on Ski-Reference.com");
				message.setContent("Here are the FIS codes of athletes you selected to stop receiving notifications for:"
								+ athleteList
								+ "<br> Thanks,"
								+ "<br> - Sean"
						, "text/html");
				Transport.send(message);
				System.out.println("Message Sent");
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<String> initUnRemovedTrackings() {
		Connection connection = new DBconnection().connect();
		ArrayList<String> unTrackedRemovals = new ArrayList<>();
		for (int i = 0; i < fisIDs.size(); i++ ) {
			String whereStmt = "WHERE email = "
					+ "\"" + this.userEmail + "\" "
					+ "AND fisID = "
					+ this.fisIDs.get(i);
			String existsQuery = "SELECT * FROM tracking " + whereStmt + ";";
			System.out.println(existsQuery);
			ResultSet rs = null;
			try {
				Statement stmt = connection.createStatement();
				rs = stmt.executeQuery(existsQuery);
				if (!rs.isBeforeFirst()) {
					unTrackedRemovals.add(Integer.toString(this.fisIDs.get(i)));
//					unTrackedRemovals += this.fisIDs.get(i) +", ";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return unTrackedRemovals;

	}

	public ArrayList<String> getUnremovedTrackings() {
		return this.unremovedTrackings;
	}


	/**
	 * Removes Trackings from the database.
	 * @return A string of all trackings the user does not follow
	 * each separated by a comma and space: ", "
	 *
	 */
	public void executeRemoveTracking()  {
		DBconnection dBconnection = new DBconnection();
		Connection connection = new DBconnection().connect();
		String unTrackedRemovals = "";
		for (int i = 0; i < fisIDs.size(); i++ ) {
			String whereStmt = "WHERE email = "
					+ "\"" + this.userEmail + "\" "
					+ "AND fisID = "
					+ this.fisIDs.get(i);
			String query = //"IF EXISTS (SELECT * FROM TRACKING " + whereStmt + ")"
					"DELETE FROM FIS_database.tracking " + whereStmt + ";";
			try {
				System.out.println(query);
				Statement stmt = connection.createStatement();
				stmt.executeUpdate(query);

			} catch (SQLException e){
				e.printStackTrace();
			}
		}
	}
}
