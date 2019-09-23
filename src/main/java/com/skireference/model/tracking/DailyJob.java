package com.skireference.model.tracking;

import com.skireference.util.Date;

public class DailyJob implements Runnable {
	@Override
	public void run() {
		DailyTracking dailyTracking = new DailyTracking(Date.todaysDate());

	}
}
