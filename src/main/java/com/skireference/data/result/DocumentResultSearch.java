package com.skireference.data.result;

import com.skireference.updatedb.DBconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DocumentResultSearch implements DocumentSearch {
	private int raceId;
	private String ip;

	public DocumentResultSearch(int raceId, String ip) {
		this.raceId = raceId;
		this.ip = ip;
	}
	@Override
	public void document() {
		try {
			String createRecord = "INSERT INTO FIS_database.result_search (race_id, ip_address, `date`) " +
					"VALUES (" + this.raceId + ",\"" + this.ip + "\", NOW());";
			Connection connection = new DBconnection().connect();
			Statement statement = connection.createStatement();
			statement.executeUpdate(createRecord);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
