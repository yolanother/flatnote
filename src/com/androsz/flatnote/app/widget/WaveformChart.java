package com.androsz.flatnote.app.widget;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.androsz.achartengine.ChartView;
import com.androsz.achartengine.chart.AbstractChart;
import com.androsz.achartengine.chart.TimeChart;
import com.androsz.achartengine.model.XYMultipleSeriesDataset;
import com.androsz.achartengine.model.XYSeries;
import com.androsz.achartengine.renderer.XYMultipleSeriesRenderer;
import com.androsz.achartengine.renderer.XYSeriesRenderer;

public class WaveformChart extends ChartView implements Serializable {

	private static final long serialVersionUID = -5692853786456847694L;

	private static final int MAX_POINTS_IN_A_GRAPH = 200;

	public XYMultipleSeriesDataset xyMultipleSeriesDataset;

	public XYMultipleSeriesRenderer xyMultipleSeriesRenderer;

	public XYSeries xySeriesMovement;

	public XYSeriesRenderer xySeriesMovementRenderer;

	public XYSeries xySeriesCalibration;
	public XYSeriesRenderer xySeriesCalibrationRenderer;

	protected double calibrationLevel;// =
										// SettingsActivity.DEFAULT_ALARM_SENSITIVITY;

	public int rating;

	public WaveformChart(final Context context) {
		super(context);
	}

	public WaveformChart(final Context context, final AttributeSet as) {
		super(context, as);
	}

	@Override
	protected AbstractChart buildChart() {
		if (xySeriesMovement == null) {
			// set up sleep movement series/renderer
			xySeriesMovement = new XYSeries("hahblah");
			xySeriesMovementRenderer = new XYSeriesRenderer();
			xySeriesMovementRenderer.setFillBelowLine(true);
			xySeriesMovementRenderer.setFillBelowLineColor(Color.GREEN);
			xySeriesMovementRenderer.setColor(Color.CYAN);

			// set up calibration line series/renderer
			xySeriesCalibration = new XYSeries("hahbleh");
			xySeriesCalibrationRenderer = new XYSeriesRenderer();
			xySeriesCalibrationRenderer.setFillBelowLine(true);
			xySeriesCalibrationRenderer.setFillBelowLineColor(Color.GRAY);
			xySeriesCalibrationRenderer.setColor(Color.WHITE);

			// add series to the dataset
			xyMultipleSeriesDataset = new XYMultipleSeriesDataset();
			xyMultipleSeriesDataset.addSeries(xySeriesMovement);
			xyMultipleSeriesDataset.addSeries(xySeriesCalibration);

			// set up the dataset renderer
			xyMultipleSeriesRenderer = new XYMultipleSeriesRenderer();
			xyMultipleSeriesRenderer
					.addSeriesRenderer(xySeriesMovementRenderer);
			xyMultipleSeriesRenderer
					.addSeriesRenderer(xySeriesCalibrationRenderer);

			xyMultipleSeriesRenderer.setPanEnabled(false, false);
			xyMultipleSeriesRenderer.setZoomEnabled(false, false);
			final float textSize = 18;
			xyMultipleSeriesRenderer.setAxisTitleTextSize(textSize);
			xyMultipleSeriesRenderer.setLabelsTextSize(textSize);
			xyMultipleSeriesRenderer.setLegendHeight(60);
			xyMultipleSeriesRenderer.setLegendTextSize(textSize);
			xyMultipleSeriesRenderer.setShowLegend(true);
			xyMultipleSeriesRenderer.setShowLabels(true);
			xyMultipleSeriesRenderer.setXLabels(6);
			xyMultipleSeriesRenderer.setYLabels(5);
			xyMultipleSeriesRenderer.setShowGrid(true);
			xyMultipleSeriesRenderer.setAxesColor(Color.MAGENTA);
			xyMultipleSeriesRenderer.setLabelsColor(xyMultipleSeriesRenderer
					.getAxesColor());
			xyMultipleSeriesRenderer.setApplyBackgroundColor(false);
			final TimeChart timeChart = new TimeChart(xyMultipleSeriesDataset,
					xyMultipleSeriesRenderer);
			timeChart.setDateFormat("h:mm a");
			return timeChart;
		}
		return null;
	}

	public double getCalibrationLevel() {
		return calibrationLevel;
	}

	public boolean makesSenseToDisplay() {
		return xySeriesMovement.getItemCount() > 1;
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		/*
		 * if (rating < 6 && rating > 0) { final Drawable dStarOn =
		 * getResources().getDrawable( R.drawable.rate_star_small_on); final
		 * Drawable dStarOff = getResources().getDrawable(
		 * R.drawable.rate_star_small_off); final int width =
		 * dStarOn.getMinimumWidth(); final int height =
		 * dStarOn.getMinimumHeight(); final int numOffStars = 5 - rating; final
		 * int centerThemDangStarz = (canvas.getWidth() - width * 5) / 2; for
		 * (int i = 0; i < rating; i++) { dStarOn.setBounds(width * i +
		 * centerThemDangStarz, height, width * i + width + centerThemDangStarz,
		 * height * 2); dStarOn.draw(canvas); } for (int i = 0; i < numOffStars;
		 * i++) { dStarOff.setBounds(width * (i + rating) + centerThemDangStarz,
		 * height, width * (i + rating) + width + centerThemDangStarz, height *
		 * 2); dStarOff.draw(canvas); } }
		 */
	}

	public void reconfigure() {
		if (makesSenseToDisplay()) {
			final double firstX = xySeriesMovement.xyList.get(0).x;
			final double lastX = xySeriesMovement.xyList.get(xySeriesMovement
					.getItemCount() - 1).x;

			if (makesSenseToDisplay()) {
				// reconfigure the calibration line..
				xySeriesCalibration.clear();

				xySeriesCalibration.add(firstX, calibrationLevel);
				xySeriesCalibration.add(lastX, calibrationLevel);
			}

			final int HOUR_IN_MS = 1000 * 60 * 60;
			if (lastX - firstX > HOUR_IN_MS) {
				((TimeChart) mChart).setDateFormat("h");
			}

			xyMultipleSeriesRenderer.setXAxisMin(firstX);
			xyMultipleSeriesRenderer.setXAxisMax(lastX);

			xyMultipleSeriesRenderer.setYAxisMin(-100);
			xyMultipleSeriesRenderer.setYAxisMax(100);
		}
	}

	public void setCalibrationLevel(final double calibrationLevel) {
		this.calibrationLevel = calibrationLevel;
	}

	public void sync(final Cursor cursor) throws StreamCorruptedException,
			IllegalArgumentException, IOException, ClassNotFoundException {
		// sync(new SleepRecord(cursor));
	}

	public void sync(final Double x, final Double y, final double alarm) {
		if (xySeriesMovement.xyList.size() >= MAX_POINTS_IN_A_GRAPH) {
			xySeriesMovement.add(x, y);
			xySeriesMovement.remove(0);
		} else {
			xySeriesMovement.add(x, y);
		}
		reconfigure();
		repaint();
	}

	/*
	 * public void sync(final SleepRecord sleepRecord) { xySeriesMovement.xyList
	 * = sleepRecord.chartData; calibrationLevel = sleepRecord.alarm;
	 * 
	 * rating = sleepRecord.rating;
	 * 
	 * xyMultipleSeriesRenderer.setChartTitle(sleepRecord.title); reconfigure();
	 * repaint(); }
	 */
}
