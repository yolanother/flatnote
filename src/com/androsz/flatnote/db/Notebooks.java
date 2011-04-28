package com.androsz.flatnote.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.androsz.flatnote.app.widget.NotebookButton;

public final class Notebooks {
	public static final String AUTHORITY = "com.androsz.flatnote.db.Notebooks";

	private final static class Helper extends SQLiteOpenHelper {

		private static final String DB_NAME = "notebooks.db";

		private static final int DB_VERSION = 1;

		public Helper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			final StringBuilder sb = new StringBuilder();

			sb.append("CREATE TABLE ");
			sb.append(MainTable.TABLE_NAME);
			sb.append(" (");
			sb.append(MainTable._ID);
			sb.append("INTEGER PRIMARY KEY,");
			sb.append(MainTable.KEY_NAME);
			sb.append(" TEXT,");
			sb.append(MainTable.KEY_COLOR);
			sb.append(" INTEGER);");

			db.execSQL(sb.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MainTable.TABLE_NAME);
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
		public static final String TABLE_NAME = "notebooks";

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);

		/**
		 * The content URI base for a single row of data. Callers must append a
		 * numeric row id to this Uri to retrieve a row
		 */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME + "/");

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
		 * Keys for the columns names
		 */
		public static final String KEY_NAME = "KEY_NAME";
		private static final String KEY_COLOR = "KEY_COLOR";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = KEY_NAME
				+ " COLLATE LOCALIZED ASC";

		public static final String[] COLUMN_PROJECTION = new String[] { KEY_NAME,
				KEY_COLOR };
	}

	/**
	 * A very simple implementation of a content provider.
	 */
	public static class Provider extends ContentProvider {
		// A projection map used to select columns from the database
		private final HashMap<String, String> projectionMap;
		// Uri matcher to decode incoming URIs.
		private final UriMatcher uriMatcher;

		// The incoming URI matches the main table URI pattern
		private static final int MAIN = 1;
		// The incoming URI matches the main table row ID URI pattern
		private static final int MAIN_ID = 2;

		// Handle to a new DatabaseHelper.
		private Helper helper;

		/**
		 * Global provider initialization.
		 */
		public Provider() {
			// Create and initialize URI matcher.
			uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
			uriMatcher.addURI(AUTHORITY, MainTable.TABLE_NAME, MAIN);
			uriMatcher.addURI(AUTHORITY, MainTable.TABLE_NAME + "/#", MAIN_ID);

			// Create and initialize projection map for all columns. This is
			// simply an identity mapping.
			projectionMap = new HashMap<String, String>();
			projectionMap.put(MainTable._ID, MainTable._ID);
			projectionMap.put(MainTable.KEY_NAME, MainTable.KEY_NAME);
			projectionMap.put(MainTable.KEY_COLOR, MainTable.KEY_COLOR);
		}

		/**
		 * Perform provider creation.
		 */
		@Override
		public boolean onCreate() {
			helper = new Helper(getContext());
			// Assumes that any failures will be reported by a thrown exception.
			return true;
		}

		/**
		 * Handle incoming queries.
		 */
		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {

			// Constructs a new query builder and sets its table name
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(MainTable.TABLE_NAME);

			switch (uriMatcher.match(uri)) {
			case MAIN:
				// If the incoming URI is for main table.
				qb.setProjectionMap(projectionMap);
				break;

			case MAIN_ID:
				// The incoming URI is for a single row.
				qb.setProjectionMap(projectionMap);
				qb.appendWhere(MainTable._ID + "=?");
				selectionArgs = DatabaseUtils.appendSelectionArgs(
						selectionArgs,
						new String[] { uri.getLastPathSegment() });
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			if (TextUtils.isEmpty(sortOrder)) {
				sortOrder = MainTable.DEFAULT_SORT_ORDER;
			}

			SQLiteDatabase db = helper.getReadableDatabase();

			Cursor c = qb.query(db, projection, selection, selectionArgs,
					null /* no group */, null /* no filter */, sortOrder);

			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		}

		/**
		 * Return the MIME type for an known URI in the provider.
		 */
		@Override
		public String getType(Uri uri) {
			switch (uriMatcher.match(uri)) {
			case MAIN:
				return MainTable.CONTENT_TYPE;
			case MAIN_ID:
				return MainTable.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		}

		/**
		 * Handler inserting new data.
		 */
		@Override
		public Uri insert(Uri uri, ContentValues initialValues) {
			if (uriMatcher.match(uri) != MAIN) {
				// Can only insert into to main URI.
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			ContentValues values;

			if (initialValues != null) {
				values = new ContentValues(initialValues);
			} else {
				values = new ContentValues();
			}

			if (!values.containsKey(MainTable.KEY_NAME)) {
				values.put(MainTable.KEY_NAME, "");
			}

			if (!values.containsKey(MainTable.KEY_COLOR)) {
				values.put(MainTable.KEY_COLOR, Color.CYAN);
			}

			SQLiteDatabase db = helper.getWritableDatabase();

			long rowId = db.insert(MainTable.TABLE_NAME, null, values);

			// If the insert succeeded, the row ID exists.
			if (rowId > 0) {
				Uri noteUri = ContentUris.withAppendedId(
						MainTable.CONTENT_ID_URI_BASE, rowId);
				notifyChange(noteUri);
				return noteUri;
			}

			throw new SQLException("Failed to insert row into " + uri);
		}

		/**
		 * Handle deleting data.
		 */
		@Override
		public int delete(Uri uri, String where, String[] whereArgs) {
			SQLiteDatabase db = helper.getWritableDatabase();
			String finalWhere;

			int count;

			switch (uriMatcher.match(uri)) {
			case MAIN:
				// If URI is main table, delete uses incoming where clause and
				// args.
				count = db.delete(MainTable.TABLE_NAME, where, whereArgs);
				break;

			// If the incoming URI matches a single note ID, does the delete
			// based on the
			// incoming data, but modifies the where clause to restrict it to
			// the
			// particular note ID.
			case MAIN_ID:
				// If URI is for a particular row ID, delete is based on
				// incoming
				// data but modified to restrict to the given ID.
				finalWhere = DatabaseUtils.concatenateWhere(MainTable._ID
						+ " = " + ContentUris.parseId(uri), where);
				count = db.delete(MainTable.TABLE_NAME, finalWhere, whereArgs);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			notifyChange(uri);

			return count;
		}

		private void notifyChange(Uri uri) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		/**
		 * Handle updating data.
		 */
		@Override
		public int update(Uri uri, ContentValues values, String where,
				String[] whereArgs) {
			SQLiteDatabase db = helper.getWritableDatabase();
			int count;
			String finalWhere;

			switch (uriMatcher.match(uri)) {
			case MAIN:
				// If URI is main table, update uses incoming where clause and
				// args.
				count = db.update(MainTable.TABLE_NAME, values, where,
						whereArgs);
				break;

			case MAIN_ID:
				// If URI is for a particular row ID, update is based on
				// incoming
				// data but modified to restrict to the given ID.
				finalWhere = DatabaseUtils.concatenateWhere(MainTable._ID
						+ " = " + ContentUris.parseId(uri), where);
				count = db.update(MainTable.TABLE_NAME, values, finalWhere,
						whereArgs);
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}

			notifyChange(uri);

			return count;
		}
	}

	/**
	 * This class cannot be instantiated
	 */
	Notebooks(){}

	public static Uri createNotebook(Context context, String name, int color) {
		final ContentResolver contentResolver = context.getContentResolver();

		final ContentValues values = new ContentValues();
		values.put(MainTable.KEY_NAME, name);
		values.put(MainTable.KEY_COLOR, color);

		final Uri uri = contentResolver.insert(MainTable.CONTENT_URI, values);

		return uri;
	}

	public static int updateNotebook(final Context context,String oldName, String newName, int newColor) {
		final ContentResolver contentResolver = context.getContentResolver();

		final ContentValues values = new ContentValues();
		values.put(MainTable.KEY_NAME, newName);
		values.put(MainTable.KEY_COLOR, newColor);

		int numRowsUpdated = contentResolver.update(MainTable.CONTENT_URI, values,
				MainTable.KEY_NAME + "='" + oldName + "'", null);

		return numRowsUpdated;
	}

	public static int deleteNotebook(final Context context,
			CharSequence notebookName) {
		final ContentResolver contentResolver = context.getContentResolver();

		final int numRowsDeleted = contentResolver.delete(
				MainTable.CONTENT_URI, MainTable.KEY_NAME + "='" + notebookName
						+ "'", null);
		
		return numRowsDeleted;
	}

	public static ArrayList<NotebookButton> getNotebooksFromCursor(Context context,
			Cursor c) {

		ArrayList<NotebookButton> notebooks = new ArrayList<NotebookButton>();
		if (c != null && c.moveToFirst()) {
			
			do {
				notebooks.add(new NotebookButton(context, c.getString(c.getColumnIndex(MainTable.KEY_NAME)), c
						.getInt(c.getColumnIndex(MainTable.KEY_COLOR))));
			} while (c.moveToNext());
			c.close();
		}
		return notebooks;
	}
}