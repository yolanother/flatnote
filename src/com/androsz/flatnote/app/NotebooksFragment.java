package com.androsz.flatnote.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.androsz.flatnote.Extras;
import com.androsz.flatnote.Intents;
import com.androsz.flatnote.R;
import com.androsz.flatnote.app.widget.NotebookButton;
import com.androsz.flatnote.app.widget.NotebooksScrollView;
import com.androsz.flatnote.db.Notebooks;

public class NotebooksFragment extends Fragment implements OnQueryTextListener,
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int NOTEBOOKS_LOADER_ID = 0;

	NotebookButton contextMenuNotebook;

	private final BroadcastReceiver showNewNotebookDialogReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			showNewNotebookDialog();
		}
	};

	private final BroadcastReceiver showEditNotebookDialogReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			showEditNotebookDialog(intent.getStringExtra(Extras.NOTEBOOK_NAME),
					intent.getIntExtra(Extras.NOTEBOOK_COLOR, Color.CYAN));
		}
	};

	private NotebooksScrollView loadNotebooks(Cursor c) {
		final Activity activity = this.getActivity();

		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);

		container.setNotebooks(this,
				Notebooks.getNotebooksFromCursor(activity, c));
		container.refreshDimensions();
		return container;
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		notebooksObserver = new NotebooksChangeObserver();
		
		getLoaderManager().initLoader(NOTEBOOKS_LOADER_ID, null, this);
	}

	private NotebooksChangeObserver notebooksObserver;

	private class NotebooksChangeObserver extends ContentObserver {

		public NotebooksChangeObserver() {
			super(new Handler());
		}

		public void onChange(boolean selfChange) {
			
			getLoaderManager().getLoader(NOTEBOOKS_LOADER_ID).forceLoad();
			super.onChange(selfChange);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		final Activity activity = this.getActivity();
		final NotebooksScrollView container = (NotebooksScrollView) activity
				.findViewById(R.id.notebooks_scroll);
		container.refreshDimensions();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if (contextMenuNotebook != null) {
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		contextMenuNotebook = (NotebookButton) v;
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.open);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.delete);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.edit_name_and_color);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notebooks_menu, menu);
		final SearchView sv = new SearchView(getActivity());
		sv.setOnQueryTextListener(this);
		menu.findItem(R.id.search_notebooks).setActionView(sv);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_notebooks,
				container);
		return view;
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

	@Override
	public void onPause() {
		super.onPause();
		Activity a = getActivity();
		a.unregisterReceiver(showNewNotebookDialogReceiver);
		a.unregisterReceiver(showEditNotebookDialogReceiver);
		a.getContentResolver().unregisterContentObserver(notebooksObserver);
	}

	String mCurFilter;

	@Override
	public boolean onQueryTextChange(String newText) {
		mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
		getLoaderManager().restartLoader(0, null, this);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		Activity a = getActivity();
		a.getContentResolver().registerContentObserver(
				Notebooks.MainTable.CONTENT_URI, true, notebooksObserver);
		a.registerReceiver(showNewNotebookDialogReceiver, new IntentFilter(
				Intents.SHOW_NEW_NOTEBOOK_DIALOG));
		a.registerReceiver(showEditNotebookDialogReceiver, new IntentFilter(
				Intents.SHOW_EDIT_NOTEBOOK_DIALOG));
	}

	private void showNewNotebookDialog() {
		final NewNotebookDialog newNotebookDialog = new NewNotebookDialog();
		newNotebookDialog.show(getFragmentManager(), "newNotebookDialog");
	}

	private void showEditNotebookDialog(String oldName, int oldColor) {
		final EditNotebookDialog editNotebookDialog = new EditNotebookDialog(
				oldName, oldColor);
		editNotebookDialog.show(getFragmentManager(), "editNotebookDialog");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cl = new CursorLoader(getActivity(),
				Notebooks.MainTable.CONTENT_URI,
				Notebooks.MainTable.COLUMN_PROJECTION, null, null, null);
		return cl;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		loadNotebooks(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		loadNotebooks(null);
	}
}