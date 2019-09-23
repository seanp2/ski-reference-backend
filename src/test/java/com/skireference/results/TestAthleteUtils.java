package com.skireference.results;

import com.skireference.model.results.AthleteUtils;
import com.skireference.util.Date;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestAthleteUtils {

	@Test
	public void testDate() {
		AthleteUtils myUtil = new AthleteUtils();
		Date mydate = new Date(6, 2, 2018);
		assertEquals(260,  myUtil.getPointsList(mydate));
		Date mydate2 = new Date(1,7,2018);
		assertEquals(266, myUtil.getPointsList(mydate2));
		Date mydate3 = new Date(25,1,2010);
		assertEquals(153, myUtil.getPointsList(mydate3));
	}


	@Test
	public void testRaceID1() {
		assertEquals("92892",
				new AthleteUtils().getRaceID("https://data.fis-ski.com/dynamic/" +
						"results.html?sector=AL&raceid=92892"));
	}


	@Test
	public void testRaceID2() {
		assertEquals("95367",
				new AthleteUtils().getRaceID("https://data.fis-ski.com/dynamic/" +
						"results.html?sector=AL&competitorid=145581&raceid=95367"));
	}

	@Test
	public void testRaceID3() {
		assertEquals("95367",
				new AthleteUtils().getRaceID("https://data.fis-ski.com/dynamic/" +
						"results.html?sector=AL&raceid=95367&competitorid=145581"));
	}

	@Test
	public void testRaceID4() {
		assertEquals("95367",
				new AthleteUtils().getRaceID("https://data.fis-ski.com/dynamic/results.html?se" +
						"ctor=AL&raceid=95367&competitorid=145581"));
	}
}
