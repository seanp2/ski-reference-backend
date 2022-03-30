package com.skireference.model.live;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListingFetcher {
	private Document listingPage;
	private static final String FIS_LIVE_RACE_LISTING_LINK = "https://data.fis-ski.com/fis_events/ajax/livefunctions/load_live_data.html";
	private static final String DATE_SELECTOR = ".split-row__item:nth-child(1) .clip";
	private static final String EVENT_ACRONYM_SELECTOR = ".hidden-sm-up :nth-child(2)";
	private static final String LOCATION_SELECTOR = ".reset-padding.bold";
	private static final String SPORT_ACRONYM_SELECTOR = ".bold .split-row__item:nth-child(1)";
	private static final String RACE_TYPE_SELECTOR = ".justify-left.hidden-xs .split-row__item:nth-child(1)";
	private static final String RACE_ID_SELECTOR = ".clip.gray";
	private static final String IS_LIVE_SELECTOR = ".live__content";


	public ListingFetcher() throws IOException {
		this.listingPage = Jsoup.connect(FIS_LIVE_RACE_LISTING_LINK).get();
	}

	public List<LiveRaceListing> getListings() {
		List<String> dates = getStringListFromSelector(DATE_SELECTOR);
		List<String> eventAcronyms = getStringListFromSelector(EVENT_ACRONYM_SELECTOR);
		List<String> locations = getStringListFromSelector(LOCATION_SELECTOR);
		List<String> sportAcronyms = getStringListFromSelector(SPORT_ACRONYM_SELECTOR);
		List<String> raceTypes = getStringListFromSelector(RACE_TYPE_SELECTOR);
		List<String> raceIds = getStringListFromSelector(RACE_ID_SELECTOR);
		List<String> isLive = getStringListFromSelector(IS_LIVE_SELECTOR);
		List<LiveRaceListing> raceListings = new ArrayList<>();
		int totalLength = isLive.size();
		for (int i = 0; i < totalLength; i++) {
			if (sportAcronyms.get(i).equals("AL")) {
				LiveRaceListing listing = new LiveRaceListing(dates.get(i), Integer.parseInt(raceIds.get(i)), locations.get(i), raceTypes.get(i), eventAcronyms.get(i));
				raceListings.add(listing);
			}
		}
		return raceListings;
	}

	private List<String> getStringListFromSelector(String selector) {
		return this.listingPage.select(selector).stream().map(Element::ownText).collect(Collectors.toList());
	}
}
