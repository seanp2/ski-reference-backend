package com.skireference;


import com.skireference.model.results.AthleteUtils;

import com.skireference.updatedb.Athlete;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class AthleteTest {

	@Test
	public void  test4() {
		try {
			Athlete athlete = new Athlete(422705);
			Double[] hilo = athlete.getPointsMadeWith(248, 1,
					new Double[]{97.55, 82.34}, "SL", new AthleteUtils());
			assertEquals(97.55, hilo[0], .01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void  test6() {
		try {
			Athlete aastroem = new Athlete(194652);
			Double[] hilo = aastroem.getPointsMadeWith(247, 1,
					new Double[]{48.94, 39.79}, "SL", new AthleteUtils());
			assertEquals(36.03, hilo[1], .01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void  test7() {
		try {
			Athlete alieu = new Athlete(163601);
			Double[] hilo = alieu.getPointsMadeWith(235, 1,
					new Double[]{85.59, 85.59}, "SL", new AthleteUtils());
			assertEquals(80.09, hilo[1], .01);
			assertEquals(88.28, hilo[0], .01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void  test8() {
		try {
			Athlete abe = new Athlete(194696);
			Double[] hilo = abe.getPointsMadeWith(235, 1,
					new Double[]{98.74, 98.74}, "SL", new AthleteUtils());
			assertEquals(91.61, hilo[1], .01);
			assertEquals(97.20 , hilo[0], .01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Tests to see that the correct hilo results are initialized after an athletes
	// FIS points increase
	@Test
	public void test9() {
		try {
			Athlete bertram = new Athlete(187642);
			Double[] hilo = bertram.getMakeUpAfterIncrease(252,  14, "GS",
					new AthleteUtils());
			assertEquals(54.29, hilo[0], 0.01);
			assertEquals(52.00 , hilo[1], 0.01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	// Tests to see that the correct hilo results are initialized after an athletes
	// FIS points increase
	@Test
	public void test10() {
		try {
			Athlete bertram = new Athlete(187642);
			Double[] hilo = bertram.getMakeUpAfterIncrease(252,  14, "SL",
					new AthleteUtils());
			assertEquals(55.64, hilo[0], 0.01);
			assertEquals(47.16 , hilo[1], 0.01);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Tests to see that the correct hilo results are initialized after an athletes
	// FIS points increase
	@Test
	public void test11() {
		try {
			Athlete kazui = new Athlete(173323);
			Double[] hilo = kazui.getMakeUpAfterIncrease(211,  13, "SL",
					new AthleteUtils());
			assertEquals(990.0, hilo[0], 0.01);
			assertEquals(141.45, hilo[1], 0.01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Tests to see that the correct hilo results are initialized after an athletes
	// FIS points increase
	@Test
	public void test12() {
		try {
			Athlete kenney = new Athlete(189087);
			Double[] hilo = kenney.getPointsMadeWith(264,  1, new Double[]{30.18, 24.12}
			,"GS",
					new AthleteUtils());
			assertEquals(25.67, hilo[0], 0.01);
			assertEquals(25.15, hilo[1], 0.01);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}