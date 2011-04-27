package com.androsz.flatnote.app.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androsz.flatnote.Extras;
import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.NotebookActivity;
import com.androsz.flatnote.db.NotebooksDB;
import com.androsz.util.MathUtils;

public class NotebookButton extends Button {

	private final int color;

	int lastDrawableId;

	public NotebookButton(Context context, CharSequence title, int color) {
		super(context);
		this.color = color;
		// this mess is why android uses xml to design layouts.
		// unfortunately, this was the best solution I could find!
		setBackgroundResource(android.R.color.transparent);

		setTextAppearance(context, R.style.NotebookButtonText);
		// for some reason these don't get set by the style...
		setSingleLine(true);
		setEllipsize(TruncateAt.END);

		// set the notebook drawable to normal initially
		final Drawable notebookDrawable = refreshDrawable(R.drawable.notebook_normal);

		setPadding(0, MathUtils.calculatePxFromDip(context, 32), 0, 0);
		setText(title);

		final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				notebookDrawable.getIntrinsicWidth(),
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		setLayoutParams(lp);
		this.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub

			}
		});

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				open();
			}
		});
	}

	public void delete() {
		final Context c = getContext();
		final NotebooksDB db = new NotebooksDB(c);
		db.deleteNotebook(getText());
		c.sendBroadcast(new Intent(Intents.REFRESH_NOTEBOOKS));
	}

	public void edit() {
		final CharSequence notebookName = getText();
		Intent intent = new Intent(Intents.SHOW_EDIT_NOTEBOOK_DIALOG);
		intent.putExtra(Extras.NOTEBOOK_NAME, notebookName);
		intent.putExtra(Extras.NOTEBOOK_COLOR, color);
		getContext().sendBroadcast(intent);
	}

	@Override
	public void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		int drawableId = R.drawable.notebook_normal;
		if (focused) {
			drawableId = R.drawable.notebook_focused;
		}
		refreshDrawable(drawableId);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();

		if (action == MotionEvent.ACTION_UP
				|| action == MotionEvent.ACTION_CANCEL) {
			post(new refreshDrawableRunnable(R.drawable.notebook_normal));
		} else {
			post(new refreshDrawableRunnable(R.drawable.notebook_pressed));
		}

		return super.onTouchEvent(event);
	}

	public void open() {
		final Context c = getContext();
		final Intent i = new Intent(c, NotebookActivity.class);
		final CharSequence notebookName = getText();
		if (notebookName == c.getText(R.string.tap_to_create_a_new_notebook)) {
			c.sendBroadcast(new Intent(Intents.SHOW_NEW_NOTEBOOK_DIALOG));
		} else {
			i.putExtra(Extras.NOTEBOOK_NAME, notebookName);
			c.startActivity(i);
		}
	}

	private final class refreshDrawableRunnable implements Runnable {
		final int drawableId;

		refreshDrawableRunnable(int drawableId) {
			this.drawableId = drawableId;
		}

		@Override
		public void run() {
			refreshDrawable(drawableId);
		}
	}

	private Drawable refreshDrawable(int drawableId) {

		if (drawableId != lastDrawableId) {
			// need to use decodeResource and then create a new BitmapDrawable,
			// otherwise the color filter will be applied to all NotebookButtons
			final Bitmap b = BitmapFactory.decodeResource(getContext()
					.getResources(), drawableId);
			final Drawable notebookDrawable = new BitmapDrawable(b);
			notebookDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
			setCompoundDrawablesWithIntrinsicBounds(null, notebookDrawable,
					null, null);
			lastDrawableId = drawableId;
			return notebookDrawable;
		}
		return null;
	}
}
