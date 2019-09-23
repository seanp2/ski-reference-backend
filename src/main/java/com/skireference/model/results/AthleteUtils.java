package com.skireference.model.results;
import com.skireference.util.Date;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.Scanner;


public class AthleteUtils {
	private Document fispages;

	
	public AthleteUtils(){}


	public static double minutesToSeconds(String time) {
		if (time.contains(":")) {
			double seconds = Double.parseDouble(time.substring(time.indexOf(":") + 1));
			seconds = seconds + Double.parseDouble(time.substring(0, time.indexOf(":"))) * 60;
			return seconds;
		}
		else {
			return Double.parseDouble(time);
		}
	}



	public static String secondsToMinutes(String time) {
		try {
			double inseconds = Double.parseDouble(time);
			int minutes = 0;
			while (inseconds > 60) {
				inseconds -= 60.0;
				minutes++;
			}
			DecimalFormat myFormat = new DecimalFormat("00.00");
			return minutes + ":" + myFormat.format(inseconds);
		}
		catch (Exception e) {
			return time;
		}
	}

	/**
	 * Visit https://data.fis-ski.com/alpine-skiing/fis-points-lists.html
	 * which displays all of the time durations and the lists they correspond to
	 * @param date the date
	 * @return the FIS points list of which the given date corresponds to
	 */
	public int getPointsList(Date date) {
		try {
			this.fispages = Jsoup.connect("https://www.fis-ski.com/DB/alpine-skiing/fis-points-lists.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assert fispages != null;

//		Elements rows = fispages.select("#fis-points-list-body .g-lg-3 , :nth-child(6) " +
//				".split-row__item:nth-child(1) " +
//				":nth-child(2) a, #fis-points-list-body .g-lg-3");
		Elements rows = fispages.select("#fis-points-list-body .g-lg-3, .split-row__item:nth-child(1) :nth-child(2) .link__text, #fis-points-list-body .g-lg-3");
		// index mod 3 = 0 of rows is a start date of a list
		// index mod 3 = 1 of rows is an end date of a list
		// index mod 3 = 2 of rows is the csv link for the list
		String[] rowAccu = new String[2];
		for (int i = 0; i < rows.size(); i++) {
			if (i % 3 == 0 ) {
				rowAccu[0] = rows.get(i).ownText();
			}
			else if (i % 3 == 1) {
				rowAccu[1] = rows.get(i).ownText();
			}
			else if (i % 3 == 2) {
				if (date.sameOrLaterThan(new Date(rowAccu[0], true)) && new Date(rowAccu[1], true).sameOrLaterThan(date)) {
					String pointsListAsMalformedString = rows.get(i).attr("onclick").substring(42);
					return Integer.parseInt(pointsListAsMalformedString
							.substring(0, pointsListAsMalformedString.length() - 2));
				} else {
					rowAccu = new String[2];
				}
			}

		}
		return 1;
	}


	/**
	 *
	 * @param url the url of the FIS result on the FIS page
	 * @return the race id of the race
	 */
	public String  getRaceID(String url) {
		String raceid = "-1";
		Scanner scanner = new Scanner(new StringReader(url));
		scanner.useDelimiter("&");
		while (scanner.hasNext()) {
			String parameter = scanner.next();
			if (parameter.substring(0, 7).equals("raceid=")) {
				raceid = parameter.substring(7);
			}
		}
		if (raceid.equals("-1")) {
			throw new IllegalArgumentException("Invalid URL");
		}
		return raceid;
	}

}
