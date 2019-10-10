package com.skireference.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Date {
	int day;
	int month;
	int year;


	public Date(int day , int month, int year) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public Date(String date, boolean hasDashes) {
		if (date.substring(2,3).equals( "-") && date.substring(5,6).equals( "-")) {
				this.day = Integer.parseInt(date.substring(0,2));
				this.month = Integer.parseInt(date.substring(3,5));
				this.year = Integer.parseInt(date.substring(6,10));
		}
		else {
			throw new IllegalArgumentException("Date String in incorrect format");
		}
	}
	public Date(String date) {
		if (!date.substring(2, 3).equals("-") && !date.substring(5, 6).equals("-")) {
			this.day = Integer.parseInt(date.substring(0,2));
			this.month = Integer.parseInt(date.substring(3,5));
			this.year = Integer.parseInt(date.substring(6,10));
		}
		else throw new IllegalArgumentException("Date String in incorrect format");
	}

	/**
	 * Returns true if this date represents the same or a later time
	 * than the other Date
	 * @param otherDate
	 * @return
	 */
	public boolean sameOrLaterThan(Date otherDate) {
		GregorianCalendar thisCal = new GregorianCalendar();
		thisCal.set(this.year, this.month, this.day);
		GregorianCalendar otherCal = new GregorianCalendar();
		otherCal.set(otherDate.getYear(), otherDate.getMonth(),
				otherDate.getDay());
		return thisCal.after(otherCal) || this.equals(otherDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Date) {
			return this.day == ((Date) obj).getDay() &&
					this.year == ((Date) obj).getYear() &&
					this.month == ((Date) obj).getMonth();
		} else {
			return false;
		}
	}


	public static Date monthAsLetters(String date) {
		String monthAsLetters = "";
		String day = "";
		String year = "";
		for (int i = 0; i < date.length(); i ++) {
			if (date.substring(i, i+1).equals(" ")) {
				monthAsLetters = date.substring(0, i);
        day = date.substring(i + 1, i + 3);
        year = date.substring(i + 5);
				break;
			}
		}

		int monthAsInt = -1;
		switch (monthAsLetters) {
			case "January": monthAsInt = 1; break;
			case "February" : monthAsInt = 2; break;
			case "March" : monthAsInt = 3; break;
			case "April" : monthAsInt = 4; break;
			case "May" : monthAsInt = 5; break;
			case "June" : monthAsInt = 6; break;
			case "July" : monthAsInt = 7; break;
			case "August" : monthAsInt = 8; break;
			case "September" : monthAsInt = 9; break;
			case "October" : monthAsInt = 10; break;
			case "November" : monthAsInt = 11; break;
			case "December" : monthAsInt = 12; break;
		}

		if (monthAsInt == -1) {
			throw new IllegalArgumentException("Invalid month");
		}
		return new Date(Integer.parseInt(day), monthAsInt, Integer.parseInt(year));
	}

	@Override
	public int hashCode() {
		return this.year * this.day / this.month;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	@Override
	public String toString() {
		return String.format("%02d-%02d-%d", this.day, this.month, this.year);
	}

	public static Date todaysDate() {

		String days = new SimpleDateFormat("dd")
				.format(Calendar.getInstance().getTime());
		String months = new SimpleDateFormat("MM")
				.format(Calendar.getInstance().getTime());
		String years = new SimpleDateFormat("yyyy")
				.format(Calendar.getInstance().getTime());
		return new Date(Integer.parseInt(days), Integer.parseInt(months), Integer.parseInt(years) );
	}
}
