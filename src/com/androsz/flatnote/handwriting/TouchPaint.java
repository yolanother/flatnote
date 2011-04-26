/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.androsz.flatnote.handwriting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

/**
 * Demonstrates the handling of touch screen and trackball events to implement a
 * simple painting app.
 */
public class TouchPaint extends GraphicsActivity {
	public class MyView extends View {
		private static final int FADE_ALPHA = 0x06;
		private static final int MAX_FADE_STEPS = 256 / FADE_ALPHA + 4;
		private static final int TRACKBALL_SCALE = 10;

		private Bitmap mBitmap;
		private Canvas mCanvas;
		private final Path mPath;
		private final Rect mRect = new Rect();
		private final Paint mPaint;
		private final Paint mFadePaint;
		private float mCurX;
		private float mCurY;
		private float mLastX;
		private float mLastY;
		private int mFadeSteps = MAX_FADE_STEPS;

		private static final float TOUCH_TOLERANCE = 1f;

		private boolean didntMove = true;

		public MyView(Context c) {
			super(c);
			setFocusable(true);
			mPaint = new Paint(Paint.DITHER_FLAG);
			mPaint.setDither(true);
			mPaint.setAntiAlias(true);
			mPaint.setARGB(255, 255, 255, 255);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.MITER);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(12);
			mFadePaint = new Paint();
			mFadePaint.setDither(true);
			mFadePaint.setARGB(FADE_ALPHA, 0, 0, 0);
			mPath = new Path();
		}

		public void clear() {
			if (mCanvas != null) {
				mPaint.setARGB(0xff, 0, 0, 0);
				mCanvas.drawPaint(mPaint);
				mPath.reset();
				invalidate();
				mFadeSteps = MAX_FADE_STEPS;
			}
		}

		public void fade() {
			if (mCanvas != null && mFadeSteps < MAX_FADE_STEPS) {
				mCanvas.drawPaint(mFadePaint);
				invalidate();
				mFadeSteps++;
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (mBitmap != null) {
				canvas.drawBitmap(mBitmap, 0, 0, null);
			}

			mCanvas.drawPath(mPath, mPaint);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			int curW = mBitmap != null ? mBitmap.getWidth() : 0;
			int curH = mBitmap != null ? mBitmap.getHeight() : 0;
			if (curW >= w && curH >= h)
				return;

			if (curW < w) {
				curW = w;
			}
			if (curH < h) {
				curH = h;
			}

			final Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
					Bitmap.Config.RGB_565);
			final Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (mBitmap != null) {
				newCanvas.drawBitmap(mBitmap, 0, 0, null);
			}
			mBitmap = newBitmap;
			mCanvas = newCanvas;
			mFadeSteps = MAX_FADE_STEPS;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			final int action = event.getActionMasked();
			final float x = event.getX();
			final float y = event.getY();
			final float pres = event.getPressure();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				mPaint.setStrokeWidth((float) (pres * Math.E * Math.E));
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}

		private void touch_move(float x, float y) {
			final float dx = Math.abs(x - mCurX);
			final float dy = Math.abs(y - mCurY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2);
				mCurX = x;
				mCurY = y;
				didntMove = false;
			}
		}

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mCurX = x;
			mCurY = y;
		}

		private void touch_up() {
			if (didntMove) {
				mPath.quadTo(mCurX - 1, mCurY - 1, mCurX + 1, mCurY + 1);
			}
			didntMove = true;
			mPath.lineTo(mCurX, mCurY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}
	}

	/** Used as a pulse to gradually fade the contents of the window. */
	private static final int FADE_MSG = 1;
	/** Menu ID for the command to clear the window. */
	private static final int CLEAR_ID = Menu.FIRST;

	/** Menu ID for the command to toggle fading. */
	private static final int FADE_ID = Menu.FIRST + 1;

	/** How often to fade the contents of the window (in ms). */
	private static final int FADE_DELAY = 100;
	/** The view responsible for drawing the window. */
	MyView mView;

	/** Is fading mode enabled? */
	boolean mFading;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// Upon receiving the fade pulse, we have the view perform a
			// fade and then enqueue a new message to pulse at the desired
			// next time.
			case FADE_MSG: {
				mView.fade();
				mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_MSG),
						FADE_DELAY);
				break;
			}
			default:
				super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create and attach the view that is responsible for painting.
		mView = new MyView(this);
		setContentView(mView);
		mView.requestFocus();

		// Restore the fading option if we are being thawed from a
		// previously saved state. Note that we are not currently remembering
		// the contents of the bitmap.
		mFading = savedInstanceState != null ? savedInstanceState.getBoolean(
				"fading", true) : true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, CLEAR_ID, 0, "Clear");
		menu.add(0, FADE_ID, 0, "Fade").setCheckable(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CLEAR_ID:
			mView.clear();
			return true;
		case FADE_ID:
			mFading = !mFading;
			if (mFading) {
				startFading();
			} else {
				stopFading();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Make sure to never run the fading pulse while we are paused or
		// stopped.
		stopFading();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(FADE_ID).setChecked(mFading);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// If fading mode is enabled, then as long as we are resumed we want
		// to run pulse to fade the contents.
		if (mFading) {
			startFading();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save away the fading state to restore if needed later. Note that
		// we do not currently save the contents of the display.
		outState.putBoolean("fading", mFading);
	}

	/**
	 * Start up the pulse to fade the screen, clearing any existing pulse to
	 * ensure that we don't have multiple pulses running at a time.
	 */
	void startFading() {
		mHandler.removeMessages(FADE_MSG);
		mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_MSG),
				FADE_DELAY);
	}

	/**
	 * Stop the pulse to fade the screen.
	 */
	void stopFading() {
		mHandler.removeMessages(FADE_MSG);
	}
}
