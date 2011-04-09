package com.androsz.flatnote.app.widget;

import java.util.ArrayList;
import java.util.Random;

import com.androsz.util.MathUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class NotebooksScrollView extends HorizontalScrollView {

	GestureDetector mGestureDetector;
	// public ArrayList<NotebookButton> notebooks;
	private static final float ROTATION_Y_MUFF = (float) -(Math.PI * Math.PI * 2);
	private static final float TRANSLATION_MUFF = (float) (Math.PI * Math.PI);
	private static final float ROTATION_X_MUFF = (float) (Math.PI * Math.PI * 10);
	private static final float SCALE_MUFF = 7;

	public NotebooksScrollView(Context context, AttributeSet as) {
		super(context, as);
	}

	public void setNotebooks(final ArrayList<CharSequence> notebookIds) {
		final LinearLayout notebooksContainer = (LinearLayout) getChildAt(0);

		Context c = getContext();
		Random r = new Random();
		for (CharSequence notebookId : notebookIds) {
			notebooksContainer.addView(new NotebookButton(c,
					notebookId, Color.rgb(r.nextInt(), r.nextInt(), r.nextInt())));

		}

		// final float finCount = count/2;
		Rect outRect = new Rect();
		this.getWindowVisibleDisplayFrame(outRect);

		final float center = outRect.centerX();
		post(new Runnable() {

			@Override
			public void run() {

				// a bit of a hack to make the scrollview display few and many
				// items properly centered in the same fashion
				FrameLayout.LayoutParams lp = null;
				lp = new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				if (notebooksContainer.getWidth() > center * 2) {
					lp.gravity = Gravity.NO_GRAVITY;
				} else {
					lp.gravity = Gravity.CENTER;
				}
				notebooksContainer.setLayoutParams(lp);

				final int midway = (int) (notebooksContainer.getWidth() / 2 - center);
				if (midway <= 0) {
					updateNotebookRotations();
				} else {
					scrollTo(midway, 0);
				}

			}
		});
	}

	public void refreshDimensions() {
		post(new Runnable() {

			@Override
			public void run() {
				int scrollX = getScrollX();
				fullScroll(FOCUS_RIGHT);
				fullScroll(FOCUS_LEFT);
				scrollTo(scrollX, 0);
			}
		});
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		updateNotebookRotations();
	}

	public void updateNotebookRotations() {
		Rect outRect = new Rect();
		this.getWindowVisibleDisplayFrame(outRect);
		final LinearLayout notebooksContainer = (LinearLayout) getChildAt(0);

		float center = outRect.centerX();
		float height = outRect.height();

		float centerX = notebooksContainer.getWidth();
		if (centerX < center * 2) {
			centerX = notebooksContainer.getPivotX();
		} else {
			centerX = getScrollX() + center;
		}

		for (int i = 0; i < notebooksContainer.getChildCount(); i++) {
			View notebook = notebooksContainer.getChildAt(i);

			float offsetFromCenter = notebook.getX() + notebook.getPivotX()
					- centerX;
			float absOffsetFromCenter = Math.abs(offsetFromCenter);
			float distanceAsScale = Math.min(1, 1 - absOffsetFromCenter
					/ SCALE_MUFF / center);

			notebook.setScaleX(distanceAsScale);
			notebook.setScaleY(distanceAsScale);
			notebook.setRotationX(absOffsetFromCenter / ROTATION_X_MUFF);
			notebook.setRotationY(offsetFromCenter / ROTATION_Y_MUFF);
			notebook.setTranslationY(absOffsetFromCenter / TRANSLATION_MUFF
					- (height / 10));
		}
	}
}
