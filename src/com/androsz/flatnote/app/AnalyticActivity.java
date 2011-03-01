package com.androsz.flatnote.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;

import com.androsz.flatnote.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AnalyticActivity extends Activity {
	protected GoogleAnalyticsTracker analytics;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		analytics = GoogleAnalyticsTracker.getInstance();
		analytics.start(getString(R.string.analytics_ua_number), this);
		analytics.trackPageView("/" + getLocalClassName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		final SharedPreferences userPrefs = getPreferences(MODE_PRIVATE);

		final boolean analyticsEnabled = userPrefs.getBoolean(
				getString(R.string.key_pref_analytics), true);
		if (analyticsEnabled) {
			analytics.dispatch();
		}
		analytics.stop();
	}

	protected void trackEvent(final String label, final int value) {
		String version = "?";
		try {
			version = getPackageManager().getPackageInfo(this.getPackageName(),
					0).versionName;
		} catch (final NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String phoneAndApiLevel = Build.MODEL + "-" + VERSION.SDK_INT;
		analytics.trackEvent(version, phoneAndApiLevel, label, value);
	}

	protected void trackPageView(final String pageUrl) {
		analytics.trackPageView(pageUrl);
	}
}
