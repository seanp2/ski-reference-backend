package com.skireference.data.vistor;

import com.skireference.updatedb.DBconnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CusomizeableVisitorIncrementer {
	private String columnName;


	public CusomizeableVisitorIncrementer(String columnName) {
		this.columnName = columnName;
		System.out.println(this.columnName);
	}


	public void increment() {
		try {
			System.out.println(this.columnName);

			String query = "SET SQL_SAFE_UPDATES = 0; " +
					"UPDATE VisitorTrack SET " + this.columnName + " = " + this.columnName + " + 1  WHERE ID=0;";
			System.out.println(query);
			Connection connection = new DBconnection().connect();
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
