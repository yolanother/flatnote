package com.androsz.flatnote.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.NotebookButton;
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

		container.setNotebooks(this,
				new NotebooksDB(activity).getAllNotebooks(activity));

		return container;
	}

	NotebookButton contextMenuNotebook;
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		contextMenuNotebook = (NotebookButton) v;
		menu.add(Menu.NONE, 1, Menu.NONE,
				R.string.open);
		menu.add(Menu.NONE, 2, Menu.NONE,
				R.string.delete);
		menu.add(Menu.NONE, 3, Menu.NONE,
				R.string.edit_name_and_color);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		if(contextMenuNotebook != null)
		{
			switch (item.getItemId()) {
			case 1:
				contextMenuNotebook.open();
				contextMenuNotebook = null;
				return true;
			case 2:
				contextMenuNotebook.delete();
				contextMenuNotebook = null;
				return true;
			case 3:
				contextMenuNotebook.edit();
				contextMenuNotebook = null;
				return true;
			}
		}
		return super.onContextItemSelected(item);
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