package com.androsz.flatnote.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.ColorPickerView;
import com.androsz.flatnote.app.widget.ColorPickerView.OnColorChangedListener;

public class ColorPickerDialog extends Dialog {

	private OnColorChangedListener mListener;
	private int mInitialColor;

	public ColorPickerDialog(Context context, OnColorChangedListener listener,
			int initialColor) {
		super(context);

		mListener = listener;
		mInitialColor = initialColor;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		OnColorChangedListener l = new OnColorChangedListener() {
			public void onColorChanged(int color) {
				mListener.onColorChanged(color);
				dismiss();
			}
		};

		setContentView(new ColorPickerView(getContext(), l, mInitialColor));
		setTitle(R.string.pick_a_color);
	}
}