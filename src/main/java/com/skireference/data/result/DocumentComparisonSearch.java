package com.skireference.data.result;

import com.skireference.updatedb.DBconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class DocumentComparisonSearch implements DocumentSearch {
	private ArrayList<Integer> fiscodes;
	private String ip;

	public DocumentComparisonSearch(ArrayList<Integer> fiscodes, String ip) {
		this.fiscodes = fiscodes;
		this.ip = ip;
	}

	@Override
	public void document() {
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < fiscodes.size(); i++) {
			try {
				String createRecord = "INSERT INTO comparison_search (comparison_id, fiscode, ip, `date`)" +
						"VALUES (\"" + uuid + "\"," + fiscodes.get(i) + ",\"" + ip + "\", NOW())";
				System.out.println(createRecord);
				Connection connection = new DBconnection().connect();
				Statement statement = connection.createStatement();
				statement.executeUpdate(createRecord);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
