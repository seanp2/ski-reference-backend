package com.skireference.results;

import com.skireference.model.results.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RaceTest {



	@Test
	public void test0() {
		try {
			Document page = Jsoup.connect("https://data.fis-ski.com/dynamic/results.html?sector=AL&raceid=83689").get();
			Race sugarbowl = new TechRace(page);
			assertEquals(113, sugarbowl.getResults().size());
			assertEquals("OVERING Robert", sugarbowl.getResults().get(0).getLastfirstName());
			assertEquals("Sugar Bowl Ski Resort", sugarbowl.getVenue());
			assertEquals("23-03-2016", sugarbowl.getDate().toString());
			assertEquals(60, sugarbowl.getScorers().size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void test2() {
		try {
			Document page = Jsoup.connect("https://data.fis-ski.com/dynamic/results.html?sector=AL&raceid=87524").get();
			Race stowe2017 = new TechRace(page);
			RaceAthlete rcs = new RaceAthlete(139503,
					"COCHRAN-SIEGLE Ryan", 1992,  "USA",new TechFinish(1, 1, 59.65,	 59.71, 0.0,9.57));
			assertEquals(rcs.toString(), stowe2017.getResults().get(0).toString());
			assertEquals(8.2, stowe2017.pointsPerSecond(), 0.1);
			assertEquals(3, stowe2017.getScorers().size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// Tests race methods on a giant slalom race
	@Test
	public void test3() {
		try {
			Document page = Jsoup.connect("https://data.fis-ski.com/dynamic/results.html?sector=AL&raceid=87526").get();
			Race stowe2017SL = new TechRace(page);
			assertEquals(8.2, stowe2017SL.pointsPerSecond(), 0.1);
			assertEquals(0.46, stowe2017SL.getFinishRate(), 0.1);
			assertEquals(new TechFinish(13, 34, 44.88, 44.98, 2.77, 36.11).toString(),
					stowe2017SL.getResults().get(12).getResult().toString());
			assertEquals("Stowe Mountain Resort / Spruce Peak (USA)" , stowe2017SL.getVenue());
			assertEquals("KASPER Masen", stowe2017SL.attackFromTheBack().getLastfirstName());
			assertEquals(126, stowe2017SL.getResults().size());
			assertEquals((126 - 68.0) / 126, stowe2017SL.getFinishRate(), 0.01);
			assertEquals(8, stowe2017SL.getScorers().size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// Tests AbstractResult methods on a giant slalom race
	@Test
	public void test4() {
		try {
			Document page = Jsoup.connect("https://data.fis-ski.com/dynamic/results.html?sector=AL&raceid=87408").get();
			Race attitash2018GS = new TechRace(page);
			assertEquals(7.8, attitash2018GS.pointsPerSecond(), 0.1);
			assertEquals(new TechFinish(12, 20, 63.43, 64.91, 2.89, 45.15).toString(),
					attitash2018GS.getResults().get(11).getResult().toString());
			assertEquals("Attitash Ski Area" , attitash2018GS.getVenue());
			assertEquals("KENOSH Tommy", attitash2018GS.attackFromTheBack().getLastfirstName());
			assertEquals(117, attitash2018GS.getResults().size());
			assertEquals((117 - 19.0) / 117, attitash2018GS.getFinishRate(), 0.01);
			assertEquals(42, attitash2018GS.getScorers().size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	// Tests AbstractResult methods on a speed race
	@Test
	public void test6() {
		try {
			Document page = Jsoup.connect("https://data.fis-ski.com/dynamic/results.html?sector=AL&raceid=87353").get();
			SpeedRace sugarloaf2017 = new SpeedRace(page);
			assertEquals((63 - 6.0) / 63.0, sugarloaf2017.getFinishRate(), 0.01);
			assertEquals("RYAN Bobby", sugarloaf2017.getResults().get(0).getLastfirstName());
			assertEquals(new SpeedFinish(12, 49, 65.87, 2.06, 124.88).toString(),
					sugarloaf2017.getResults().get(11).getResult().toString());
			assertEquals("Sugarloaf (USA)", sugarloaf2017.getVenue());
			assertEquals("ALPERT John", sugarloaf2017.attackFromTheBack().getLastfirstName());
			assertEquals(54, sugarloaf2017.getScorers().size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		Race race = AbstractRace.buildRace(87524);
		System.out.println(race.getResults());
	}














}