package com.granadagame.sorbie;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsApplication extends Application {
    String trackingId = "UA-12352779-10";
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(trackingId);
        }
        return mTracker;
    }
}