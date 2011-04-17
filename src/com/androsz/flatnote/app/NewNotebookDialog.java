package com.androsz.flatnote.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.androsz.flatnote.Extras;
import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.ColorPickerView;
import com.androsz.flatnote.app.widget.ColorPickerView.OnColorChangedListener;
import com.androsz.flatnote.db.NotebooksDB;

public class NewNotebookDialog extends DialogFragment {

	ColorPickerView colorPicker;
	EditText editName;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();

		View contentView = View.inflate(activity, R.layout.dialog_new_notebook,
				null);

		Dialog d = new AlertDialog.Builder(activity)
				.setView(contentView)
				.setTitle(R.string.new_notebook)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								onPositiveClick();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dismiss();
							}
						}).create();

		colorPicker = (ColorPickerView) contentView
				.findViewById(R.id.notebook_color);
		editName = (EditText) contentView.findViewById(R.id.notebook_name);

		colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
			@Override
			public void onColorChanged(int color) {
				onPositiveClick();
			}
		});

		return d;
	}

	private void onPositiveClick() {
		Activity activity = getActivity();
		
		int color = colorPicker.getColor();
		String name = editName.getText().toString();
		
		new NotebooksDB(activity).createNotebook(name, color);
		activity.sendBroadcast(new Intent(Intents.REFRESH_NOTEBOOKS));

		Intent i = new Intent(activity, NotebookActivity.class);
		CharSequence notebookName = name;
		i.putExtra(Extras.NOTEBOOK_NAME, notebookName);
		activity.startActivity(i);

		
		dismiss();

	}
}
