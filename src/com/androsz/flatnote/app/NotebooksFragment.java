package com.androsz.flatnote.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.NotebooksScrollView;
import com.androsz.flatnote.db.NotebooksDB;

public class NotebooksFragment extends Fragment implements OnQueryTextListener {

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		loadNotebooks();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(refreshNotebooksReceiver);
		getActivity().unregisterReceiver(showNewNotebookDialog);
	}

	@Override
	public void onResume() {
		super.onPause();
		getActivity().registerReceiver(refreshNotebooksReceiver,
				new IntentFilter(Intents.REFRESH_NOTEBOOKS));
		getActivity().registerReceiver(showNewNotebookDialog,
				new IntentFilter(Intents.SHOW_NEW_NOTEBOOK_DIALOG));
	}

	private NotebooksScrollView loadNotebooks() {
		final Activity activity = this.getActivity();

		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);

		container.setNotebooks(new NotebooksDB(activity)
				.getAllNotebooks(activity));

		return container;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notebooks_menu, menu);
		SearchView sv = new SearchView(getActivity());
		sv.setOnQueryTextListener(this);
		menu.findItem(R.id.search_notebooks).setActionView(sv);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_notebook:
			showNewNotebookDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showNewNotebookDialog() {
		NewNotebookDialog newNotebookDialog = new NewNotebookDialog();
		newNotebookDialog.show(getFragmentManager(), "newNotebookDialog");
	}

	private final BroadcastReceiver refreshNotebooksReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			NotebooksScrollView container = loadNotebooks();
			container.refreshDimensions();
		}
	};

	private final BroadcastReceiver showNewNotebookDialog = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			showNewNotebookDialog();
		}
	};

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

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}
}