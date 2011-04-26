package com.androsz.flatnote.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.androsz.flatnote.R;

public class HostActivity extends AnalyticActivity {

	boolean alreadyPrepared = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setTheme(R.style.Theme_flatnote);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { final
	 * MenuInflater inflater = getMenuInflater();
	 * inflater.inflate(R.menu.host_menu, menu); return
	 * super.onCreateOptionsMenu(menu); }
	 */

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!alreadyPrepared) {
			final MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.host_menu, menu);
			alreadyPrepared = true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		// ActionBar bar = getActionBar();
		// bar.setDisplayHomeAsUpEnabled(true);
		// bar.setDisplayShowHomeEnabled(true);
		super.onResume();
	}
}
