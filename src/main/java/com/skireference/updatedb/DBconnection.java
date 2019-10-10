package com.skireference.updatedb;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;




public class DBconnection {
	// init database constants

	FileInputStream input;
	Properties properties;
	private String DATABASE_DRIVER = "com.mysql.jdbc.Driver";;
	private String DATABASE_URL = "jdbc:mysql://fispoints.cmqzttoyzcdi.us-east-2.rds.amazonaws.com:" +
			"3306/FIS_database?useUnicode=true&useJDBCCompliant" +
			"TimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&allowMultiQueries=true";
	private static final String MAX_POOL = "250"; // set your own limit

	// init connection object
	private Connection connection;

	// create properties
	private Properties getProperties() {

		if (properties == null) {
			properties = new Properties();
			properties.setProperty("user", "seanp1");
			properties.setProperty("password", "Wanderu59");
			properties.setProperty("MaxPooledStatements", MAX_POOL);
		}
		return properties;
	}

	// connect database
	public Connection connect() {
		if (connection == null) {
			try {
				Class.forName(DATABASE_DRIVER);
				connection = DriverManager.getConnection(DATABASE_URL, getProperties());
				System.out.println("connected");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	// disconnect database
	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

