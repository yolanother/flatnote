package com.androsz.flatnote.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import com.androsz.flatnote.Extras;
import com.androsz.flatnote.app.widget.NotebookButton;

public class NotebooksDB {
	public static final String AUTHORITY = Extras.class.getCanonicalName();

	private final static class Helper extends SQLiteOpenHelper {

		private static final String NAME = "notebooks.db";

		private static final int VERSION = 1;

		public Helper(Context context) {
			super(context, NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			final StringBuilder sb = new StringBuilder();

			sb.append("CREATE TABLE ");
			sb.append(MainTable.NAME);
			sb.append(" (");
			sb.append(MainTable.KEY_ID);
			sb.append("INTEGER PRIMARY KEY,");
			sb.append(MainTable.KEY_NAME);
			sb.append(" TEXT,");
			sb.append(MainTable.KEY_COLOR);
			sb.append(" INTEGER);");

			db.execSQL(sb.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MainTable.NAME);
			onCreate(db);
		}
	}

	/**
	 * Definition of the contract for the main table of our provider.
	 */
	public static final class MainTable implements BaseColumns {

		// This class cannot be instantiated
		private MainTable() {
		}

		/**
		 * The table name offered by this provider
		 */
		public static final String NAME = "notebooks";

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + NAME);

		/**
		 * The content URI base for a single row of data. Callers must append a
		 * numeric row id to this Uri to retrieve a row
		 */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse("content://"
				+ AUTHORITY + "/" + NAME + "/");

		/**
		 * The MIME type of {@link #CONTENT_URI}.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.androsz.flatnote.notebooks";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * row.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.androsz.flatnote.notebooks";
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "data COLLATE LOCALIZED ASC";

		/*
		 * Keys for the columns names
		 */
		private static final String KEY_ID = "_id";
		private static final String KEY_NAME = "KEY_NAME";
		private static final String KEY_COLOR = "KEY_COLOR";
		
		private static final String[] ALL_COLUMNS = new String[] { KEY_NAME,
			KEY_COLOR };
	}

	private final Helper helper;

	public NotebooksDB(Context context) {
		helper = new Helper(context);
	}

	public long createNotebook(String name, int color) {
		final SQLiteDatabase db = helper.getWritableDatabase();

		final ContentValues values = new ContentValues();
		values.put(MainTable.KEY_NAME, name);
		values.put(MainTable.KEY_COLOR, color);

		final long id = db.insert(MainTable.NAME, null, values);

		db.close();

		return id;
	}
	
	public int updateNotebook(String oldName, String newName, int newColor) {
		final SQLiteDatabase db = helper.getWritableDatabase();

		final ContentValues values = new ContentValues();
		values.put(MainTable.KEY_NAME, newName);
		values.put(MainTable.KEY_COLOR, newColor);

		int numRowsUpdated = db.update(MainTable.NAME, values, MainTable.KEY_NAME + "='" + oldName + "'", null);
		db.close();
		
		return numRowsUpdated;
	}

	public int deleteNotebook(CharSequence notebookName) {
		final SQLiteDatabase db = helper.getWritableDatabase();

		final int numRowsDeleted = db.delete(MainTable.NAME,
				MainTable.KEY_NAME + "='" + notebookName + "'", null);

		db.close();

		return numRowsDeleted;
	}

	public ArrayList<NotebookButton> getAllNotebooks(Context context) {
		final SQLiteDatabase db = helper.getReadableDatabase();
		final Cursor c = db.query(MainTable.NAME, MainTable.ALL_COLUMNS, null,
				null, null, null, null);
		ArrayList<NotebookButton> notebooks = null;
		if (c.moveToFirst()) {
			notebooks = new ArrayList<NotebookButton>(c.getCount());

			do {
				notebooks.add(new NotebookButton(context, c.getString(0), c
						.getInt(1)));
			} while (c.moveToNext());
			c.close();
			db.close();
		}
		return notebooks;
	}
}