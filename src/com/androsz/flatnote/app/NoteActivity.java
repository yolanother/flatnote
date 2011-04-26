package com.androsz.flatnote.app;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.androsz.flatnote.R;
import com.androsz.flatnote.handwriting.TouchPaint;

public class NoteActivity extends HostActivity {

	private static final int REQUEST_CODE_CAMERA = 1;
	private static final String TEMP_IMG_FILE_NAME = "tmp.jpg";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CAMERA:
			switch (resultCode) {
			case Activity.RESULT_CANCELED:
				// picFileName = null;
				break;
			case Activity.RESULT_OK:
				final File file = new File(TEMP_IMG_FILE_NAME);
				if (file.exists()) {
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					BitmapFactory.decodeFile(TEMP_IMG_FILE_NAME, options);
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_camera:
			final Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
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
