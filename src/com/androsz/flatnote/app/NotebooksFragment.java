package com.androsz.flatnote.app;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.NotebookButton;
import com.androsz.flatnote.app.widget.NotebooksScrollView;

public class NotebooksFragment extends Fragment {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final Activity activity = this.getActivity();

		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);

		final ArrayList<CharSequence> notebooks = new ArrayList<CharSequence>();
		Random rando = new Random();
		int cnt = 8;
		for(int i = 0; i < cnt; i++)
		{
			notebooks.add(i+"");//rando.nextInt(9)+""+rando.nextInt(9)+""+rando.nextInt(9)+""+rando.nextInt(9));
		}
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