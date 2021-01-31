package com.skireference.model.live;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class LiveRaceResult {
	private static String BIB_SELECTOR = ".cell.bib";
	private static String RANK_SELECTOR = ".cell.rank span";
	private static String NAME_SELECTOR = ".racer-name";
	private static String TIME_SELECTOR = ".totalvalue";
	private static String DIFF_SELECTOR = ".cell.col-totaldiff";


	private List<LiveResultRow> resultRows;
	private int raceId;

	public LiveRaceResult(int raceId) throws IOException {
		 this.raceId = raceId;
		 this.resultRows = this.initResultRows();

	}

	private String getRaceLink() {
		return "http://live.fis-ski.com/lv-al" + raceId + ".htm#/short";
	}

	private List<LiveResultRow> initResultRows() throws IOException {
		System.setProperty("webdriver.chrome.driver", "/Users/seanpomerantz/WebDriver/bin/chromedriver");
		WebDriver chromeDriver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(chromeDriver, 15);
		chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		try {
			chromeDriver.get(getRaceLink());
			List<String> bibs = wait.until(driver -> driver.findElements(By.cssSelector(BIB_SELECTOR))).stream().map(n -> n.getText()).collect(Collectors.toList());
			List<String> ranks = wait.until(driver -> driver.findElements(By.cssSelector(RANK_SELECTOR))).stream().map(n -> n.getText()).collect(Collectors.toList());
			List<String> names = wait.until(driver -> driver.findElements(By.cssSelector(NAME_SELECTOR))).stream().map(n -> n.getText()).collect(Collectors.toList());
			List<String> times = wait.until(driver -> driver.findElements(By.cssSelector(TIME_SELECTOR))).stream().map(n -> n.getText()).collect(Collectors.toList());
			List<String> diffs = wait.until(driver -> driver.findElements(By.cssSelector(DIFF_SELECTOR))).stream().map(n -> n.getText()).collect(Collectors.toList());
			List<LiveResultRow> results = new ArrayList<>();
			for (int i = 0; i < times.size(); i++) {
				LiveResultRow row = new LiveResultRow(ranks.get(i), bibs.get(i), names.get(i), times.get(i), diffs.get(i), 0);
				results.add(row);
			}
			for (int i = times.size(); i < names.size(); i++) {
				LiveResultRow row = new LiveResultRow("", bibs.get(i), names.get(i), "DNF", "", 990);
				results.add(row);
			}
			return results;
		} finally {
			chromeDriver.quit();
		}

	}

	public List<LiveResultRow> getResultRows() {
		return this.resultRows;
	}
}
