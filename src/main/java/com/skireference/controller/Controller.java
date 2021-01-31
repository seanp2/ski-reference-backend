package com.skireference.controller;
import com.skireference.data.result.DocumentComparisonSearch;
import com.skireference.data.result.DocumentResultSearch;
import com.skireference.data.result.DocumentSearch;
import com.skireference.data.vistor.CusomizeableVisitorIncrementer;
import com.skireference.model.comparison.Comparison;
import com.skireference.model.live.ListingFetcher;
import com.skireference.model.live.LiveRaceListing;
import com.skireference.model.live.LiveRaceResult;
import com.skireference.model.live.LiveResultRow;
import com.skireference.model.results.AbstractRace;
import com.skireference.model.results.Race;
import com.skireference.model.results.RaceAthlete;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@EnableAutoConfiguration
@CrossOrigin
public class Controller {
	@RequestMapping(value = "/results/{raceId}", method = RequestMethod.GET)
	public Race getRace(@PathVariable("raceId") int raceId) {
		String ip = getClientIpAddressIfServletRequestExist();
		DocumentResultSearch documentResultSearch = new DocumentResultSearch(raceId, ip);
		documentResultSearch.document();
		return AbstractRace.buildRace(raceId);
	}

	@RequestMapping(value = "/results/{raceId}/scorers")
	public List<RaceAthlete> getScorers(@PathVariable("raceId") int raceId) {
		return AbstractRace.buildRace(raceId).getScorers();
	}

	@RequestMapping(value = "/live/listings",  method = RequestMethod.GET)
	public List<LiveRaceListing> getLiveListings() {
		try {
			return new ListingFetcher().getListings();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	@RequestMapping(value = "/live/results/{raceId}",  method = RequestMethod.GET)
	public List<LiveResultRow> getLiveResults(@PathVariable("raceId") int raceId) {
		try {
			return new LiveRaceResult(raceId).getResultRows();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}



	@RequestMapping(value = "/comparison/{athleteIds}", method = RequestMethod.GET)
	public Comparison getComparison(@PathVariable("athleteIds") String[] athleteIds) {
		ArrayList<Integer> fisIds = new ArrayList<>();
		for (int i = 0; i < athleteIds.length; i ++) {
			try {
				System.out.println(athleteIds[i]);
				fisIds.add(Integer.parseInt(athleteIds[i]));
			} catch (NumberFormatException e ) {
				throw new IllegalArgumentException("Invalid FIS ID given: " + athleteIds[i]);
			}
		}
		String ip = getClientIpAddressIfServletRequestExist();
		DocumentComparisonSearch dcs = new DocumentComparisonSearch(fisIds, ip);
		dcs.document();
		return Comparison.buildByFisID(fisIds);
	}


	@RequestMapping(value = "/visitor/increment/{columnName}", method = RequestMethod.POST)
	public void incrementVisitor(@PathVariable(value = "columnName")  String columnName) {
		String officialColumnName = "";
		if (columnName.equals("homePage")) {
			officialColumnName = "home";
		} else if (columnName.equals("resultHome")) {
			officialColumnName = "result_home";
		} else if (columnName.equals("comparisonHome")) {
			officialColumnName = "comparison_home";
		} else if (columnName.equals("resultView")) {
			officialColumnName = "result_view";
		} else if (columnName.equals("comparisonView")) {
			officialColumnName = "comparison_view";
		}
		CusomizeableVisitorIncrementer incrementer = new CusomizeableVisitorIncrementer(officialColumnName);
		incrementer.increment();
	}




	private static final String[] IP_HEADER_CANDIDATES = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR"
	};

	private static String getClientIpAddressIfServletRequestExist() {

		if (RequestContextHolder.getRequestAttributes() == null) {
			return "0.0.0.0";
		}

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		for (String header: IP_HEADER_CANDIDATES) {
			String ipList = request.getHeader(header);
			if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
				String ip = ipList.split(",")[0];
				return ip;
			}
		}

		return request.getRemoteAddr();
	}

}
