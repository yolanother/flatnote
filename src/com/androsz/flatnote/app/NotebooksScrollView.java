package com.androsz.flatnote.app;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class NotebooksScrollView extends HorizontalScrollView {

	GestureDetector mGestureDetector;
	ArrayList<Button> notebooks;
	float centerX;
	private static final float ROTATION_MUFF = (float) -(Math.PI * Math.PI * 2);

	public NotebooksScrollView(Context context, AttributeSet as) {
		super(context, as);
		Rect outRect = new Rect();
		this.getWindowVisibleDisplayFrame(outRect);
		centerX = outRect.centerX();
	}

	public void setNotebooks(final ArrayList<Button> notebooks) {
		this.notebooks = notebooks;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		updateNotebookRotations();
	}

	private void updateNotebookRotations() {
		float centerX = (getScrollX() + (this.centerX));
		for (Button notebook : notebooks) {
			float distanceFromCenter = notebook.getX() + notebook.getPivotX()
					- centerX;

			notebook.setRotationY(distanceFromCenter / ROTATION_MUFF);
		}
	}
}
