package com.androsz.flatnote.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.ColorPickerView;
import com.androsz.flatnote.db.NotebooksDB;

public class EditNotebookDialog extends NewNotebookDialog {
	protected String oldName;
	protected int oldColor;

	EditNotebookDialog(String oldName, int oldColor) {
		super();
		this.oldName = oldName;
		this.oldColor = oldColor;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// set the old color and name
		((ColorPickerView) contentView.findViewById(R.id.notebook_color))
				.setColor(oldColor);

		EditText name = (EditText) contentView.findViewById(R.id.notebook_name);
		name.setText(oldName);
		name.selectAll();

		return  super.onCreateDialog(savedInstanceState);
	}

	@Override
	protected void onPositiveClick() {
		final Activity activity = getActivity();
		final ColorPickerView colorPicker = (ColorPickerView) contentView
				.findViewById(R.id.notebook_color);

		final EditText editName = (EditText) contentView
				.findViewById(R.id.notebook_name);

		final int color = colorPicker.getColor();
		final String name = editName.getText().toString();

		int num = new NotebooksDB(activity)
				.updateNotebook(oldName, name, color);
		if (num > 0)
			activity.sendBroadcast(new Intent(Intents.REFRESH_NOTEBOOKS));

		dismiss();
	}
}
