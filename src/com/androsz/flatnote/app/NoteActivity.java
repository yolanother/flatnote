package com.androsz.flatnote.app;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.androsz.flatnote.R;
import com.androsz.flatnote.handwriting.TouchPaint;

public class NoteActivity extends HostActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_camera:
			startActivity(new Intent(this, CameraActivity.class));
			return true;
		case R.id.draw:
			startActivity(new Intent(this, TouchPaint.class));
			return true;
		case R.id.save:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
