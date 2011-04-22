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

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();

		final View contentView = View.inflate(activity,
				R.layout.dialog_new_notebook, null);

		Dialog d = new AlertDialog.Builder(activity)
				.setView(contentView)
				.setTitle(R.string.new_notebook)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								onPositiveClick(contentView);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dismiss();
							}
						}).create();

		ColorPickerView colorPicker = (ColorPickerView) contentView
				.findViewById(R.id.notebook_color);

		colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
			@Override
			public void onColorChanged(int color) {
				onPositiveClick(contentView);
			}
		});

		return d;
	}
	
	@Override public void onCancel(DialogInterface dialog)
	{
		super.onCancel(dialog);
		Activity activity = getActivity();
		final View contentView = View.inflate(activity,
				R.layout.dialog_new_notebook, null);
		onPositiveClick(contentView);
	}
	
	@Override public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
	}

	private void onPositiveClick(View contentView) {
		Activity activity = getActivity();
		ColorPickerView colorPicker = (ColorPickerView) contentView
				.findViewById(R.id.notebook_color);

		EditText editName = (EditText) contentView
				.findViewById(R.id.notebook_name);

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
