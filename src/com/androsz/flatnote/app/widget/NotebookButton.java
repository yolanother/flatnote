package com.androsz.flatnote.app.widget;

import com.androsz.flatnote.R;
import com.androsz.util.MathUtils;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class NotebookButton extends Button {

	public NotebookButton(Context context, CharSequence title, int color) {
		super(context);
		//this mess is why android uses xml to design layouts.
		//unfortunately, this was the best solution I could find!
		setBackgroundResource(android.R.color.transparent);
		
		setTextSize(MathUtils.calculatePxFromDip(context, 24));
		this.setTextAppearance(context, R.style.NotebookButton);
		Drawable notebookDrawable = context.getResources().getDrawable(R.drawable.notebook);
		notebookDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		setCompoundDrawablesWithIntrinsicBounds(null, notebookDrawable, null, null);
		setPadding(0, 32, 0, 0);
		setText(title);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		setLayoutParams(lp);
	}
}
