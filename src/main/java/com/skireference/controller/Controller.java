package com.skireference.controller;
import com.skireference.model.results.AbstractRace;
import com.skireference.model.results.Race;
import com.skireference.model.results.RaceAthlete;
import com.skireference.model.results.Result;
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

}
