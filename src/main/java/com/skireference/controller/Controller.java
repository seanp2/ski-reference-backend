package com.skireference.controller;
import com.skireference.model.comparison.Comparison;
import com.skireference.model.results.AbstractRace;
import com.skireference.model.results.Race;
import com.skireference.model.results.RaceAthlete;
import com.skireference.model.results.Result;
import com.skireference.updatedb.Athlete;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@EnableAutoConfiguration
@CrossOrigin
public class Controller {
	@RequestMapping(value = "/results/{raceId}", method = RequestMethod.GET)
	public Race getRace(@PathVariable("raceId") int raceId) {
		return AbstractRace.buildRace(raceId);
	}

	@RequestMapping(value = "/results/{raceId}/scorers")
	public List<RaceAthlete> getScorers(@PathVariable("raceId") int raceId) {
		return AbstractRace.buildRace(raceId).getScorers();
	}


	@RequestMapping(value = "/comparison/{athleteIds}", method = RequestMethod.GET)
	public Comparison getComparison(@PathVariable("athleteIds") String[] athleteIds) {
		ArrayList<Integer> fisIds = new ArrayList<>();
		System.out.println(athleteIds);
		for (int i = 0; i < athleteIds.length; i ++) {
			try {
				System.out.println(athleteIds[i]);
				fisIds.add(Integer.parseInt(athleteIds[i]));
			} catch (NumberFormatException e ) {
				throw new IllegalArgumentException("Invalid FIS ID given: " + athleteIds[i]);
			}
		}
		return Comparison.buildByFisID(fisIds);
	}

}
