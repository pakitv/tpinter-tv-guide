package com.tpinter.android.tvguide.loadtask;

import java.util.Vector;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ListView;

import com.tpinter.android.tvguide.activity.FavoriteActivity;
import com.tpinter.android.tvguide.activity.R;
import com.tpinter.android.tvguide.adapter.FavoriteAdapter;
import com.tpinter.android.tvguide.dbmanager.DBAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.entity.Programme;
import com.tpinter.android.tvguide.rowdata.FavoriteRowData;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class LoadFavoritesTask extends AsyncTask<Void, Void, FavoriteAdapter> {

	private final FavoriteActivity activity;

	private final ProgressDialog progressDialog;

	public LoadFavoritesTask(FavoriteActivity favoriteActivity, ProgressDialog progressDialog) {
		this.activity = favoriteActivity;
		this.progressDialog = progressDialog;
	}

	@Override
	public void onPreExecute() {
		this.progressDialog.show();
	}

	@Override
	protected FavoriteAdapter doInBackground(Void... params) {
		return setupListAdapter();
	}

	@Override
	public void onPostExecute(FavoriteAdapter result) {
		this.progressDialog.dismiss();
		activity.setListAdapter(result);

		ListView list = (ListView) activity.findViewById(android.R.id.list);
		activity.registerForContextMenu(list);
	}

	/**
	 * This is where we create and connect the adapter to this activity as well
	 * as the data.
	 */
	private FavoriteAdapter setupListAdapter() {
		Vector<FavoriteRowData> data = new Vector<FavoriteRowData>();

		TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();

		DBAdapter db = new DBAdapter(activity);
		db.open();
		Cursor cursor = db.getAllFavorites();

		if (cursor.moveToFirst()) {
			do {
				Channel channel = new Channel();
				channel.setChannelID(Integer.parseInt(cursor.getString(0)));
				channel.setTitle(cursor.getString(1));
				channel.setChannelGroupID(Integer.parseInt(cursor.getString(2)));
				channel.setChannelGroupTitle(cursor.getString(3));

				Programme[] programmelList = tvAnimareServiceCall.GetCurrentProgramme(channel.getChannelID());

				double currentTime = programmelList[0].getStartDateTime().getTime();
				double nextTime = programmelList[1].getStartDateTime().getTime();
				double nowTime = java.lang.System.currentTimeMillis();

				double percent = (nowTime - currentTime) / (nextTime - currentTime);

				FavoriteRowData rd = new FavoriteRowData(channel, programmelList, (int) Math.round(percent * 100));
				data.add(rd);
			} while (cursor.moveToNext());
		}
		db.close();

		return new FavoriteAdapter(activity, R.layout.favorite_list, R.id.title, data);
	}
}
