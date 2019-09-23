package com.skireference.model.tracking;

import com.skireference.updatedb.DBconnection;
import com.skireference.util.MailUtil;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;


public class CreateTracking {

	private ArrayList<Integer> fisIDs;
	private String userEmail;
	private int confirmID;
	private ArrayList<Integer> alreadyTracked;

	public CreateTracking(ArrayList<Integer> fisIDs, String userEmail) {
		this.fisIDs = fisIDs;
		this.userEmail = userEmail;
		this.confirmID = hashCode();
		this.alreadyTracked = this.initAlreadyTracked();
	}

	// Sends email to ask for confirmation
	public void sendConfirmTracking()  {
		MailUtil mailUtil = new MailUtil();
		Session session =   Session.getInstance(mailUtil.getProperties(), new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUtil.getEmail(), mailUtil.getPassword());
			}
		});

		Message message = new MimeMessage(session);
		try {
			String athleteList = "";
			for (int i = 0; i < fisIDs.size(); i++) {
				if(!alreadyTracked.contains(fisIDs.get(i))) {
					athleteList += "<br>* " + fisIDs.get(i);
				}
			}
			message.setFrom(new InternetAddress(mailUtil.getEmail()));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.userEmail));
			message.setSubject("Please confirm athlete tracking on Ski-Reference.com [" + confirmID +"]");
			message.setContent("Here are the FIS codes of athletes you selected to receive notifications for:"
							+ athleteList
							+ "<br>"
							+ "<a href = 'http://www.ski-reference.com/confirmTracking?confirmID="
							+ this.confirmID + "'>"
							+ " Click here link to confirm</a>"
							+ "<br> Thanks,"
							+ "<br> - Sean"
					, "text/html");
			Transport.send(message);
			System.out.println("Message Sent");
		} catch (MessagingException e) {
			e.printStackTrace();
		}


	}

	// Updates database to add all new unconfirmed trackings;
	public void updateTrackingDB() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		LocalDate localDate = LocalDate.now();
		System.out.println(dtf.format(localDate));
		DBconnection dBconnection = new DBconnection();
		Connection connection = new DBconnection().connect();
		for(int i = 0; i < fisIDs.size(); i++) {
			if (!alreadyTracked.contains(fisIDs.get(i))) {
				try {
					String query = "INSERT INTO FIS_database.tracking VALUES ( " +

							 "\"" + this.userEmail + "\" , "
							+ + fisIDs.get(i) + " , "
							+ "0, "
//							+ "\"" + dtf.format(localDate) + "\","
							+ this.confirmID
							+ ");";
					Statement stmt = connection.createStatement();
					stmt.executeUpdate(query);
					System.out.println(query);

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}



	public ArrayList<Integer> initAlreadyTracked() {
		Connection connection = new DBconnection().connect();
		ArrayList<Integer> alreadyTracked = new ArrayList<>();
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
				if (rs.isBeforeFirst()) {
					alreadyTracked.add(this.fisIDs.get(i));
//					unTrackedRemovals += this.fisIDs.get(i) +", ";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return alreadyTracked;
	}


	public static void validateFISidList(String parameterIDs) throws NumberFormatException {
		Scanner scanner = new Scanner(parameterIDs);
		while (scanner.hasNext()) {
			String next = scanner.next();
			Integer.parseInt(next);
		}
	}
}
