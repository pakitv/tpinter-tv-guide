package com.tpinter.android.tvguide.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tpinter.android.tvguide.dbmanager.DBAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.entity.Programme;
import com.tpinter.android.tvguide.utility.Constants;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class FavoriteActivity extends ListActivity {

	public final static int REMOVE_FROM_FAVORITES_CONTEXTMENU = 0;

	protected DBAdapter db;

	protected LayoutInflater inflater;

	// private Integer[] imgid = { R.drawable.icon };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.favorite_main);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		db = new DBAdapter(this);
		try {
			db.open();
			Cursor cursor = db.getAllFavorites();
			if (cursor.getCount() < 1) {
				startActivityForResult(new Intent(this, AllChannelActivity.class), 0);
			} else {
				try {
					loadData();
				} catch (Exception e) {
					Log.e(FavoriteActivity.class.getName(), "Error loading data.", e);
				}
				getListView().setTextFilterEnabled(true);
			}
		} finally {
			db.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favorite_menu, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		RowData selectedRowData = (RowData) this.getListAdapter().getItem(position);
		Intent intent = new Intent(this, ProgrammeActivity.class);
		intent.putExtra(Constants.INTENT_CHANNEL_ID, selectedRowData.getChannelID());
		intent.putExtra(Constants.INTENT_CHANNEL_TITLE, selectedRowData.getTitle());
		startActivityForResult(intent, 0);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == android.R.id.list) {
			try {
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				Channel selectedChannel = (Channel) this.getListAdapter().getItem(info.position);
				menu.setHeaderTitle(selectedChannel.getTitle());
				String[] menuItems = getResources().getStringArray(R.array.favorites_menu);
				for (int i = 0; i < menuItems.length; i++) {
					menu.add(Menu.NONE, i, i, menuItems[i]);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			try {
				loadData();
			} catch (Exception e) {
				Log.e(FavoriteActivity.class.getName(), "Error loading data.", e);
			}
			break;
		case R.id.all_channel:
			startActivityForResult(new Intent(this, AllChannelActivity.class), 0);
			break;
		}
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case REMOVE_FROM_FAVORITES_CONTEXTMENU:
			db.open();
			Channel selectedChannel = (Channel) this.getListAdapter().getItem(info.position);

			if (db.deleteFavorite(selectedChannel.getChannelID())) {
				Toast.makeText(this, "Remove successful!", Toast.LENGTH_SHORT).show();
				loadData();
			} else {
				Toast.makeText(this, "Remove failed!", Toast.LENGTH_LONG).show();
			}
			db.close();

			break;
		}
		return true;
	}

	private void loadData() {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage(getString(R.string.loading_text));
		new LoadFavoritesTask(loadingDialog).execute();
	}

	private class LoadFavoritesTask extends AsyncTask<Void, Void, CustomAdapter> {

		ProgressDialog progressDialog;

		public LoadFavoritesTask(ProgressDialog progressDialog) {
			this.progressDialog = progressDialog;
		}

		@Override
		public void onPreExecute() {
			this.progressDialog.show();
		}

		@Override
		protected CustomAdapter doInBackground(Void... params) {
			return setupListAdapter();
		}

		@Override
		public void onPostExecute(CustomAdapter result) {
			this.progressDialog.dismiss();
			FavoriteActivity.this.setListAdapter(result);

			ListView list = (ListView) FavoriteActivity.this.findViewById(android.R.id.list);
			FavoriteActivity.this.registerForContextMenu(list);
		}

		/**
		 * This is where we create and connect the adapter to this activity as
		 * well as the data.
		 */
		private CustomAdapter setupListAdapter() {
			Vector<RowData> data = new Vector<RowData>();

			TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();

			DBAdapter db = new DBAdapter(FavoriteActivity.this);
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

					RowData rd = new RowData(channel, programmelList, (int) Math.round(percent * 100));
					data.add(rd);
				} while (cursor.moveToNext());
			}
			db.close();

			return new CustomAdapter(FavoriteActivity.this, R.layout.favorite_list, R.id.title, data);
		}
	}

	private class RowData {

		protected int channelID;

		protected String title;

		protected int progressBarStatus;

		protected Programme[] programmes;

		public RowData(Channel channel, Programme[] programmes, int progressBarStatus) {
			this.channelID = channel.getChannelID();
			this.title = channel.getTitle();
			this.programmes = programmes;
			this.progressBarStatus = progressBarStatus;
		}

		@Override
		public String toString() {
			return title + " " + getProgrammeTitleFirst();
		}

		public int getChannelID() {
			return channelID;
		}

		public String getTitle() {
			return title;
		}

		public String getProgrammeTitleFirst() {
			return programmes[0].getTitle();
		}

		public String getProgrammeTitleFirstWithDate() {
			return programmes[0].toString();
		}

		public String getProgrammeTitleSecond() {
			return programmes[1].getTitle();
		}

		public String getProgrammeTitleSecondWithDate() {
			return programmes[1].toString();
		}

		public String getProgrammeTitleThird() {
			return programmes[2].getTitle();
		}

		public String getProgrammeTitleThirdWithDate() {
			return programmes[2].toString();
		}
	}

	private class CustomAdapter extends ArrayAdapter<RowData> {

		private final int resource;

		public CustomAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {
			super(context, resource, textViewResourceId, objects);
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			RowData rowData = getItem(position);

			if (convertView == null) {
				convertView = inflater.inflate(resource, parent, false);
				holder = new ViewHolder(convertView);

				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();

			holder.getTitle().setText(rowData.title);
			holder.getDetailFirst().setText(rowData.getProgrammeTitleFirstWithDate());
			holder.getDetailSecond().setText(rowData.getProgrammeTitleSecondWithDate());
			holder.getDetailThird().setText(rowData.getProgrammeTitleThirdWithDate());
			// holder.getProgressBar().setProgress(rowData.progressBarStatus);

			holder.getImage().setImageBitmap(getImageBitmap("http://tv.animare.hu/i/logo/" + rowData.getChannelID() + ".gif"));

			return convertView;
		}

		private Bitmap getImageBitmap(String url) {
			Bitmap bitmap = null;
			try {
				URL aURL = new URL(url);
				URLConnection connection = aURL.openConnection();
				connection.connect();
				InputStream inputStream = connection.getInputStream();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
				bitmap = BitmapFactory.decodeStream(bufferedInputStream);
				bufferedInputStream.close();
				inputStream.close();
			} catch (IOException e) {
				Log.e("CustomAdapter.getImageBitmap", "Error getting bitmap", e);
			}
			return bitmap;
		}

		private class ViewHolder {
			private final View view;

			private TextView title = null;

			private TextView detailFirst = null;

			private TextView detailSecond = null;

			private TextView detailThird = null;

			private final ProgressBar progressBar = null;

			private ImageView image = null;

			public ViewHolder(View row) {
				this.view = row;
				this.title = (TextView) view.findViewById(R.id.title);
				this.detailFirst = (TextView) view.findViewById(R.id.detailFirst);
				this.detailSecond = (TextView) view.findViewById(R.id.detailSecond);
				this.detailThird = (TextView) view.findViewById(R.id.detailThird);
				// this.progressBar = (ProgressBar)
				// view.findViewById(R.id.progressbar);
				this.image = (ImageView) row.findViewById(R.id.img);
			}

			public TextView getTitle() {
				return title;
			}

			public TextView getDetailFirst() {
				return detailFirst;
			}

			public TextView getDetailSecond() {
				return detailSecond;
			}

			public TextView getDetailThird() {
				return detailThird;
			}

			public ProgressBar getProgressBar() {
				return progressBar;
			}

			public ImageView getImage() {
				return image;
			}
		}
	}
}