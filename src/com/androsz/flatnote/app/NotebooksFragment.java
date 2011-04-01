package com.androsz.flatnote.app;

import java.util.ArrayList;

import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.NotebooksScrollView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class NotebooksFragment extends Fragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final Activity activity = this.getActivity();

		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);

		final ArrayList<Button> notebooks = new ArrayList<Button>();

		final Button b1 = (Button) activity.findViewById(R.id.rotatingButton);

		notebooks.add(b1);
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton2));
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton3));
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton4));
		notebooks.add((Button) activity.findViewById(R.id.rotatingButton5));
		container.setNotebooks(notebooks);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		final Activity activity = this.getActivity();
		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);
		container.refreshDimensions();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notebooks, container);
		return view;
	}
}