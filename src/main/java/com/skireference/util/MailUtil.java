package com.skireference.util;

import java.util.Properties;

public class MailUtil {
	private Properties properties;
	private String email;
	private String password;

	public MailUtil() {
		this.properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		this.email = "skireference1@gmail.com";
		this.password = "7QE-AYt-fKf-eQo";
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

	public Properties getProperties() {
		return this.properties;
	}



}
