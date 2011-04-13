package com.androsz.flatnote.app.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androsz.flatnote.R;
import com.androsz.flatnote.app.NotebookActivity;
import com.androsz.util.MathUtils;

public class NotebookButton extends Button {

	public NotebookButton(Context context, CharSequence title, int color) {
		super(context);

		// this mess is why android uses xml to design layouts.
		// unfortunately, this was the best solution I could find!
		setBackgroundResource(android.R.color.transparent);

		setTextSize(MathUtils.calculatePxFromDip(context, 24));
		setTextAppearance(context, R.style.NotebookButton);
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

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(NotebookButton.this.getContext())
						.setSingleChoiceItems(R.array.notebook_longclick_list,
								0, new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();
				return true;
			}
		});

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Context c = NotebookButton.this.getContext();
				Intent i = new Intent(c, NotebookActivity.class);
				// i.putExtra("", value)
				c.startActivity(i);
			}
		});
	}
}
