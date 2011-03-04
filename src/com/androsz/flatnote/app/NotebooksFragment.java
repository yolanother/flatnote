/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androsz.flatnote.app;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

import java.util.ArrayList;

import com.androsz.flatnote.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * This application demonstrates the ability to transform views in 2D and 3D,
 * scaling them, translating them, and rotating them (in 2D and 3D). Use the
 * seek bars to set the various transform properties of the button.
 */
public class NotebooksFragment extends Fragment implements OnGestureListener {

	ArrayList<Button> notebooks = new ArrayList<Button>();
	int windowWidth;
	int windowHeight;
	GestureDetector mGestureDetector;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// go "up"
		// one activity in the app's Activity heirarchy.
		Activity activity = this.getActivity();

		mGestureDetector = new GestureDetector(this);/*
													 * new GestureDetector.
													 * SimpleOnGestureListener()
													 * {
													 * 
													 * @Override public boolean
													 * onScroll(MotionEvent e1,
													 * MotionEvent e2, float
													 * distanceX, float
													 * distanceY) { return true;
													 * }
													 * 
													 * @Override public boolean
													 * onFling(final MotionEvent
													 * e1, final MotionEvent e2,
													 * final float velocityX,
													 * final float velocityY) {
													 * 
													 * float
													 * minScaledFlingVelocity =
													 * 1; //if
													 * (Math.abs(velocityX) >
													 * minScaledFlingVelocity //
													 * && Math.abs(velocityY) <
													 * minScaledFlingVelocity *
													 * 2) {
													 * //touch_move(e1.getX(),
													 * e1.getY()); //} return
													 * super.onFling(e1, e2,
													 * velocityX, velocityY); }
													 * });
													 */

		LinearLayout container = (LinearLayout) activity
				.findViewById(R.id.notebooks_container);

		OnTouchListener onTouchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				mGestureDetector.onTouchEvent(event);
				/*
				 * // if (mGestureDetector.onTouchEvent(event)) // return true;
				 * int action = event.getActionMasked(); final float x =
				 * event.getX(); final float y = event.getY(); if (action ==
				 * MotionEvent.ACTION_MOVE) { touch_move(x, y); }
				 */
				return true;
			}
		};

		container.setOnTouchListener(onTouchListener);

		Button b1 = (Button) activity.findViewById(R.id.rotatingButton);
		for (int i = 0; i < 10; i++) {
			b1.setBackgroundColor(Color.BLACK);// (Color.CYAN,
												// PorterDuff.Mode.MULTIPLY);
			b1.setOnTouchListener(onTouchListener);
			notebooks.add(b1);
		}
		b1 = (Button) activity.findViewById(R.id.rotatingButton2);
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton2));
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton3));
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton4));
	}

	private float lastX = 0;
	private void touch_move(float x, float y) {
		float centerX = windowWidth / 2;
		float funConst = (float) -(Math.PI * Math.PI);
		for (Button notebook : notebooks) {
			float distanceFromCenter = notebook.getX() - centerX;
			// float distanceAsPercent = distanceFromCenter/windowWidth*90;
			notebook.setRotationY(distanceFromCenter / funConst);
			notebook.setTranslationX(x);
			lastX = x;
		}
	}
	private void touch_move_old(float x, float y) {
		float centerX = windowWidth / 2;
		float funConst = (float) -(Math.PI * Math.PI);
		for (Button notebook : notebooks) {
			float distanceFromCenter = notebook.getX() - centerX;
			// float distanceAsPercent = distanceFromCenter/windowWidth*90;
			notebook.setRotationY(distanceFromCenter / funConst);
			notebook.setTranslationX(x);
			lastX = x;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Activity activity = this.getActivity();

		Display defaultDisplay = activity.getWindow().getWindowManager()
				.getDefaultDisplay();
		windowWidth = defaultDisplay.getWidth();
		windowHeight = defaultDisplay.getHeight();
		View view = inflater.inflate(R.layout.fragment_notebooks, container);
		return view;
	}

	private float mCurX = 0;
	@Override
	public boolean onDown(MotionEvent e) {
		mCurX = e.getX();
		return false;
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	int notebookFocusIndex = 0;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (isScrollingLeft(e1, e2)) { // Check if scrolling left
			if (notebookFocusIndex > 0) {
				notebookFocusIndex--;
			}
		} else { // Otherwise scrolling right
			if (notebookFocusIndex < notebooks.size()) {
				notebookFocusIndex++;
			}
		}
		scrollTo(notebookFocusIndex);
		return true;
	}

	private void scrollTo(int index) {
		Button notebook = notebooks.get(index);
		touch_move(notebook.getPivotX(), 0);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}