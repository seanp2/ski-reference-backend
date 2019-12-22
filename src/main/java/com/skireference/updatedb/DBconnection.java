package com.skireference.updatedb;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
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
		DBCredsDTO creds = getSecret();
		if (properties == null) {
			properties = new Properties();
			properties.setProperty("user", creds.getUsername());
			properties.setProperty("password", creds.getPassword());
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


	/**
	 * Mostly provided by AWS
	 */
	private static DBCredsDTO getSecret() {

		String secretName = "dbcreds1";
		String region = "us-east-2";

		// Create a Secrets Manager client
		AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
				.withRegion(region)
				.build();

		String secret, decodedBinarySecret;
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
				.withSecretId(secretName);
		GetSecretValueResult getSecretValueResult = null;

		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
		} catch (DecryptionFailureException e) {
			// Secrets Manager can't decrypt the protected secret text using the provided KMS key.
			// Deal with the exception here, and/or rethrow at your discretion.
			throw e;
		} catch (InternalServiceErrorException e) {
			// An error occurred on the server side.
			// Deal with the exception here, and/or rethrow at your discretion.
			throw e;
		} catch (InvalidParameterException e) {
			// You provided an invalid value for a parameter.
			// Deal with the exception here, and/or rethrow at your discretion.
			throw e;
		} catch (InvalidRequestException e) {
			// You provided a parameter value that is not valid for the current state of the resource.
			// Deal with the exception here, and/or rethrow at your discretion.
			throw e;
		} catch (ResourceNotFoundException e) {
			// We can't find the resource that you asked for.
			// Deal with the exception here, and/or rethrow at your discretion.
			throw e;
		}

		DBCredsDTO creds = null;
		secret = getSecretValueResult.getSecretString();
		ObjectMapper mapper = new ObjectMapper();
		try {
			creds = mapper.readValue(secret, DBCredsDTO.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return creds;
	}
}

