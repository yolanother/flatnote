package com.androsz.flatnote.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.androsz.flatnote.R;

public class HomeActivity extends HostActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_notebook:
			NewNotebookDialog newNotebookDialog = new NewNotebookDialog();
			newNotebookDialog.show(getFragmentManager(), "newNotebookDialog");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
