package com.androsz.flatnote.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.NotebooksScrollView;
import com.androsz.flatnote.db.NotebooksDB;

public class NotebooksFragment extends Fragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final Activity activity = this.getActivity();

		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);


		container.setNotebooks(new NotebooksDB(activity).getAllNotebooks(activity));
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