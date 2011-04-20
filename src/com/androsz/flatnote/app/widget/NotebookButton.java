package com.androsz.flatnote.app.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androsz.flatnote.Extras;
import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.NewNotebookDialog;
import com.androsz.flatnote.app.NotebookActivity;
import com.androsz.flatnote.db.NotebooksDB;
import com.androsz.util.MathUtils;

public class NotebookButton extends Button {

	public NotebookButton(Context context, CharSequence title, int color) {
		super(context);

		// this mess is why android uses xml to design layouts.
		// unfortunately, this was the best solution I could find!
		setBackgroundResource(android.R.color.transparent);

		setTextAppearance(context, R.style.NotebookButtonText);
		//for some reason these don't get set by the style...
		setSingleLine(true);
		setEllipsize(TruncateAt.END);

		// need to use decodeResource and then create a new BitmapDrawable,
		// otherwise the color filter will be applied to all NotebookButtons
		Bitmap b = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.notebook);
		Drawable notebookDrawable = new BitmapDrawable(b);
		notebookDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		setCompoundDrawablesWithIntrinsicBounds(null, notebookDrawable, null,
				null);

		setPadding(0, MathUtils.calculatePxFromDip(context, 32), 0, 0);
		setText(title);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				notebookDrawable.getIntrinsicWidth(),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		setLayoutParams(lp);
		this.setOnCreateContextMenuListener(new OnCreateContextMenuListener(){

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				
			}});

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				open();
			}
		});
	}

	public void open() {
		Context c = getContext();
		Intent i = new Intent(c, NotebookActivity.class);
		CharSequence notebookName = getText();
		if (notebookName == c.getText(R.string.tap_to_create_a_new_notebook)) {
			c.sendBroadcast(new Intent(Intents.SHOW_NEW_NOTEBOOK_DIALOG));
		} else {
			i.putExtra(Extras.NOTEBOOK_NAME, notebookName);
			c.startActivity(i);
		}
	}

	public void delete() {
		Context c = getContext();
		NotebooksDB db = new NotebooksDB(c);
		db.deleteNotebook(getText());
		c.sendBroadcast(new Intent(Intents.REFRESH_NOTEBOOKS));
	}
	
	public void edit()
	{
		// TODO: implement
	}
}
