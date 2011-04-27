package com.androsz.flatnote.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {

	public interface OnColorChangedListener {
		void onColorChanged(int color);
	}

	private Paint mPaint;
	private Paint mCenterPaint;
	private int[] mColors;
	private OnColorChangedListener mListener;

	private boolean mTrackingCenter;

	private boolean mHighlightCenter;

	private static final int CENTER_X = 100;

	private static final int CENTER_Y = 100;
	private static final int CENTER_RADIUS = 32;

	private static final float PI = 3.1415926f;

	public ColorPickerView(Context c, AttributeSet as) {
		super(c, as);
		init(Color.CYAN);
	}

	public ColorPickerView(Context c, OnColorChangedListener l, int color) {
		super(c);
		mListener = l;
		init(color);
	}

	private int ave(int s, int d, float p) {
		return s + java.lang.Math.round(p * (d - s));
	}

	public int getColor() {
		return mCenterPaint.getColor();
	}

	public void init(int color) {
		// layer type is software, renders all red in hardware mode
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF,
				0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
		final Shader s = new SweepGradient(0, 0, mColors, null);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setShader(s);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(32);

		mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterPaint.setColor(color);
		mCenterPaint.setStrokeWidth(5);
	}

	private int interpColor(int colors[], float unit) {
		if (unit <= 0)
			return colors[0];
		if (unit >= 1)
			return colors[colors.length - 1];

		float p = unit * (colors.length - 1);
		final int i = (int) p;
		p -= i;

		// now p is just the fractional part [0...1) and i is the index
		final int c0 = colors[i];
		final int c1 = colors[i + 1];
		final int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		final int r = ave(Color.red(c0), Color.red(c1), p);
		final int g = ave(Color.green(c0), Color.green(c1), p);
		final int b = ave(Color.blue(c0), Color.blue(c1), p);

		return Color.argb(a, r, g, b);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		final float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;

		canvas.translate(CENTER_X, CENTER_X);

		canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
		canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);

		if (mTrackingCenter) {
			final int c = mCenterPaint.getColor();
			mCenterPaint.setStyle(Paint.Style.STROKE);

			if (mHighlightCenter) {
				mCenterPaint.setAlpha(0xFF);
			} else {
				mCenterPaint.setAlpha(0x80);
			}
			canvas.drawCircle(0, 0,
					CENTER_RADIUS + mCenterPaint.getStrokeWidth(), mCenterPaint);

			mCenterPaint.setStyle(Paint.Style.FILL);
			mCenterPaint.setColor(c);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final float x = event.getX() - CENTER_X;
		final float y = event.getY() - CENTER_Y;
		final boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= CENTER_RADIUS;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTrackingCenter = inCenter;
			if (inCenter) {
				mHighlightCenter = true;
				invalidate();
				break;
			}
		case MotionEvent.ACTION_MOVE:
			if (mTrackingCenter) {
				if (mHighlightCenter != inCenter) {
					mHighlightCenter = inCenter;
					invalidate();
				}
			} else {
				final float angle = (float) java.lang.Math.atan2(y, x);
				// need to turn angle [-PI ... PI] into unit [0....1]
				float unit = angle / (2 * PI);
				if (unit < 0) {
					unit += 1;
				}
				mCenterPaint.setColor(interpColor(mColors, unit));
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTrackingCenter) {
				if (inCenter) {
					if (mListener != null) {
						mListener.onColorChanged(mCenterPaint.getColor());
					}
				}
				mTrackingCenter = false; // so we draw w/o halo
				invalidate();
			}
			break;
		}
		return true;
	}

	public void setColor(int color) {
		mCenterPaint.setColor(color);
	}

	public void setOnColorChangedListener(OnColorChangedListener l) {
		this.mListener = l;
	}
}