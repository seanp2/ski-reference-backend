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
	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@RequestMapping(value = "/results/{raceId}", method = RequestMethod.GET)
	public List<RaceAthlete> getRace(@PathVariable("raceId") int raceId) {
		return AbstractRace.buildRace(raceId).getResults();
	}
}
