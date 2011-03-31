package com.tpinter.android.tvguide.activity;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tpinter.android.tvguide.dbmanager.DBAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.entity.Programme;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class FavoriteActivity extends ListActivity {

	public final static int REMOVE_FROM_FAVORITES_CONTEXTMENU = 0;

	private DBAdapter db;
	private LayoutInflater inflater;
	private RowData rd;

	// private Integer[] imgid = { R.drawable.icon };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.favorite_main);

		inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		db = new DBAdapter(this);

		loadData();
		// getListView().setTextFilterEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favorite_menu, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		Channel selectedChannel = (Channel) this.getListAdapter().getItem(
				position);
		Intent intent = new Intent(this, ProgrammeActivity.class);
		intent.putExtra("ChannelId", selectedChannel.getChannelID());
		startActivityForResult(intent, 0);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		if (view.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Channel selectedChannel = (Channel) this.getListAdapter().getItem(
					info.position);
			menu.setHeaderTitle(selectedChannel.getTitle());
			String[] menuItems = getResources().getStringArray(
					R.array.favorites_menu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			loadData();
			break;
		case R.id.all_channel:
			startActivityForResult(new Intent(this, AllChannelActivity.class),
					0);
			break;
		}
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case REMOVE_FROM_FAVORITES_CONTEXTMENU:
			db.open();
			Channel selectedChannel = (Channel) this.getListAdapter().getItem(
					info.position);

			if (db.deleteFavorite(selectedChannel.getChannelID())) {
				Toast.makeText(this, "Remove successful!", Toast.LENGTH_SHORT)
						.show();
				loadData();
			} else {
				Toast.makeText(this, "Remove failed!", Toast.LENGTH_LONG)
						.show();
			}
			db.close();

			break;
		}
		return true;
	}

	private void loadData() {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage("Loading. Please wait...");
		new LoadFavoritesTask(loadingDialog).execute();
	}

	private class LoadFavoritesTask extends
			AsyncTask<Void, Void, CustomAdapter> {

		ProgressDialog progressDialog;

		public LoadFavoritesTask(ProgressDialog progressDialog) {
			this.progressDialog = progressDialog;
		}

		public void onPreExecute() {
			this.progressDialog.show();
		}

		protected CustomAdapter doInBackground(Void... params) {
			return setupListAdapter();
		}

		public void onPostExecute(CustomAdapter result) {
			this.progressDialog.dismiss();
			FavoriteActivity.this.setListAdapter(result);

			ListView list = (ListView) FavoriteActivity.this
					.findViewById(android.R.id.list);
			FavoriteActivity.this.registerForContextMenu(list);
		}

		/**
		 * This is where we create and connect the adapter to this activity as
		 * well as the data.
		 */
		private CustomAdapter setupListAdapter() {
			TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
			DBAdapter db = new DBAdapter(FavoriteActivity.this);
			db.open();

			Vector<RowData> data = new Vector<RowData>();

			Cursor cursor = db.getAllFavorites();
			if (cursor.moveToFirst()) {
				do {
					Channel channel = new Channel();
					channel.setChannelID(Integer.parseInt(cursor.getString(0)));
					channel.setTitle(cursor.getString(1));
					channel.setChannelGroupID(Integer.parseInt(cursor
							.getString(2)));
					channel.setChannelGroupTitle(cursor.getString(3));

					Programme[] programmelList = tvAnimareServiceCall
							.GetCurrentProgramme(channel.getChannelID());

					double currentTime = programmelList[0].getStartDateTime()
							.getTime();
					double nextTime = programmelList[1].getStartDateTime()
							.getTime();
					double nowTime = java.lang.System.currentTimeMillis();

					double percent = (nowTime - currentTime)
							/ (nextTime - currentTime);

					rd = new RowData(channel, programmelList[0],
							(int) Math.round(percent * 100));
					data.add(rd);
				} while (cursor.moveToNext());
			}
			db.close();

			return new CustomAdapter(FavoriteActivity.this, android.R.id.list,
					R.id.title, data);
		}
	}

	private class RowData {

		protected String title;
		protected String programmeTitle;
		protected int progressBarStatus;

		public RowData(Channel channel, Programme programme,
				int progressBarStatus) {
			this.title = channel.getTitle();
			this.programmeTitle = programme.getTitle();
			this.progressBarStatus = progressBarStatus;
		}

		public String toString() {
			return title + " " + programmeTitle;
		}
	}

	private class CustomAdapter extends ArrayAdapter<RowData> {

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, List<RowData> objects) {
			super(context, resource, textViewResourceId, objects);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			TextView title = null;
			TextView detail = null;
			ProgressBar progressBar = null;
			// ImageView i11 = null;

			RowData rowData = getItem(position);

			if (null == convertView) {
				convertView = inflater.inflate(android.R.id.list, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();

			title = holder.getTitle();
			title.setText(rowData.title);

			detail = holder.getDetail();
			detail.setText(rowData.programmeTitle);

			progressBar = holder.getProgressBar();
			progressBar.setProgress(rowData.progressBarStatus);

			// i11 = holder.getImage();
			// i11.setImageResource(imgid[0]);

			return convertView;
		}

		private class ViewHolder {
			private View row;
			private TextView title = null;
			private TextView detail = null;
			private ProgressBar progressBar = null;

			// private ImageView i11 = null;

			public ViewHolder(View row) {
				this.row = row;
			}

			public TextView getTitle() {
				if (null == title) {
					title = (TextView) row.findViewById(R.id.title);
				}
				return title;
			}

			public TextView getDetail() {
				if (null == detail) {
					detail = (TextView) row.findViewById(R.id.detail);
				}
				return detail;
			}

			public ProgressBar getProgressBar() {
				if (null == progressBar) {
					progressBar = (ProgressBar) row
							.findViewById(R.id.progressbar);
				}
				return progressBar;
			}

			// public ImageView getImage() {
			// if (null == i11) {
			// i11 = (ImageView) row.findViewById(R.id.img);
			// }
			// return i11;
			// }
		}
	}
}