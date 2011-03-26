package com.androsz.flatnote.app.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.HorizontalScrollView;

public class NotebooksScrollView extends HorizontalScrollView {

	GestureDetector mGestureDetector;
	public ArrayList<Button> notebooks;
	float centerX, width, height;
	private static final float ROTATION_Y_MUFF = (float) -(Math.PI * Math.PI * 2);
	private static final float TRANSLATION_MUFF = (float) (Math.PI * Math.PI);
	private static final float ROTATION_X_MUFF = (float) (Math.PI * Math.PI * 10);
	private static final float SCALE_MUFF = 7;

	public NotebooksScrollView(Context context, AttributeSet as) {
		super(context, as);
	}

	public void setNotebooks(final ArrayList<Button> notebooks) {
		this.notebooks = notebooks;
		refreshDimensions();
	}

	public void refreshDimensions() {
		
		//updateNotebookRotations();
		post(new Runnable() {

			@Override
			public void run() {
				int scrollX = getScrollX();
				fullScroll(FOCUS_RIGHT);
				fullScroll(FOCUS_LEFT);
				scrollTo(scrollX,0);
			}
		});
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		updateNotebookRotations();
	}

	public void updateNotebookRotations() {
		Rect outRect = new Rect();
		this.getWindowVisibleDisplayFrame(outRect);
		centerX = outRect.centerX();
		width = outRect.width();
		height = outRect.height();
		float centerX = (getScrollX() + (this.centerX));
		for (Button notebook : notebooks) {
			float offsetFromCenter = notebook.getX() + notebook.getPivotX()
					- centerX;
			float absOffsetFromCenter = Math.abs(offsetFromCenter);
			float distanceAsScale = Math.min(1, 1 - absOffsetFromCenter
					/ SCALE_MUFF / this.centerX);

			notebook.setScaleX(distanceAsScale);
			notebook.setScaleY(distanceAsScale);
			notebook.setRotationX(absOffsetFromCenter / ROTATION_X_MUFF);
			notebook.setRotationY(offsetFromCenter / ROTATION_Y_MUFF);
			notebook.setTranslationY(absOffsetFromCenter / TRANSLATION_MUFF
					- (height / 10));
		}
	}
}
