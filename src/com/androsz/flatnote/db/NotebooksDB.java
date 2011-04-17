package com.androsz.flatnote.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androsz.flatnote.app.widget.NotebookButton;

public class NotebooksDB {

	private Helper helper;

	public NotebooksDB(Context context) {
		helper = new Helper(context);
	}

	public long createNotebook(String name, int color) {
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(Helper.KEY_NAME, name);
		cv.put(Helper.KEY_COLOR, color);

		long id = db.insert(Helper.TABLE_NOTEBOOKS, null, cv);
		
		db.close();
		
		return id;
	}

	public int deleteNotebook(CharSequence charSequence) {
		SQLiteDatabase db = helper.getWritableDatabase();

		int numRowsDeleted = db.delete(Helper.TABLE_NOTEBOOKS, Helper.KEY_NAME + "='" + charSequence+"'", null);

		db.close();
		
		return numRowsDeleted;
	}

	public ArrayList<NotebookButton> getAllNotebooks(Context context) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query(Helper.TABLE_NOTEBOOKS, Helper.COLUMNS, null, null,
				null, null, null);
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

	private final static class Helper extends SQLiteOpenHelper {

		private static final String NAME = "notebooks.db";
		private static final String TABLE_NOTEBOOKS = "notebooks";

		private static final String KEY_ID = "_id";
		private static final String KEY_NAME = "KEY_NAME";
		private static final String KEY_COLOR = "KEY_COLOR";

		private static final String[] COLUMNS = new String[] { KEY_NAME,
				KEY_COLOR };

		private static final int VERSION = 1;

		public Helper(Context context) {
			super(context, NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder sb = new StringBuilder();

			sb.append("CREATE TABLE ");
			sb.append(TABLE_NOTEBOOKS);
			sb.append(" (");
			sb.append(KEY_ID);
			sb.append("INTEGER PRIMARY KEY,");
			sb.append(KEY_NAME);
			sb.append(" TEXT,");
			sb.append(KEY_COLOR);
			sb.append(" INTEGER);");

			db.execSQL(sb.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTEBOOKS);
			onCreate(db);
		}
	}
}