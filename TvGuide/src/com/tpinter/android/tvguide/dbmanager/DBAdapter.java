package com.tpinter.android.tvguide.dbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	public static final String KEY_CHANNEL_ID = "channel_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_GROUP_ID = "group_id";
	public static final String KEY_GROUP_TITLE = "group_title";
	private static final String TAG = "DBAdapter";

	private static final String DATABASE_NAME = "tpinter.tvguide";
	private static final String DATABASE_TABLE = "favorites";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table favorites (channel_id integer primary key, "
			+ "title text not null,"
			+ "group_id integer,"
			+ "group_title text);";

	private final Context context;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS titles");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert a favorite into the database---
	public long insertFavorite(int channelId, String title, int groupId,
			String groupTitle) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CHANNEL_ID, channelId);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_GROUP_ID, groupId);
		initialValues.put(KEY_GROUP_TITLE, groupTitle);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	// ---deletes a particular favorite---
	public boolean deleteFavorite(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_CHANNEL_ID + "=" + rowId, null) > 0;
	}

	// ---retrieves all the favorite---
	public Cursor getAllFavorites() {
		return db.query(DATABASE_TABLE, new String[] { KEY_CHANNEL_ID,
				KEY_TITLE, KEY_GROUP_ID, KEY_GROUP_TITLE }, null, null, null,
				null, null);
	}

	// ---retrieves a particular favorite---
	public Cursor getFavorite(long rowId) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
				KEY_CHANNEL_ID, KEY_TITLE, KEY_GROUP_ID, KEY_GROUP_TITLE },
				KEY_CHANNEL_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	// ---updates a favorite---
	public boolean updateFavorite(long rowId, String isbn, String title,
			String publisher) {
		ContentValues args = new ContentValues();
		args.put(KEY_CHANNEL_ID, isbn);
		args.put(KEY_TITLE, title);
		args.put(KEY_GROUP_TITLE, publisher);
		return db.update(DATABASE_TABLE, args, KEY_CHANNEL_ID + "=" + rowId,
				null) > 0;
	}
}
