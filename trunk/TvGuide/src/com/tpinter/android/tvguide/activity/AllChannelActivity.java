package com.tpinter.android.tvguide.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tpinter.android.tvguide.adapter.AllChannelListAdapter;
import com.tpinter.android.tvguide.dbmanager.DBAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.utility.Constants;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class AllChannelActivity extends ListActivity {

	public final static int ADD_TO_FAVORITES_CONTEXTMENU = 0;

	private DBAdapter db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_channel_main);
		db = new DBAdapter(this);

		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_channel_menu, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		if (view.getId() == android.R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Channel selectedChannel = (Channel) this.getListAdapter().getItem(info.position);
			menu.setHeaderTitle(selectedChannel.getTitle());
			String[] menuItems = getResources().getStringArray(R.array.all_channel_menu);
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
		case R.id.favorite:
			startActivityForResult(new Intent(this, FavoriteActivity.class), 0);
			break;
		}
		return true;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Channel selectedChannel = (Channel) this.getListAdapter().getItem(position);
		Intent intent = new Intent(this, ProgrammeActivity.class);
		intent.putExtra(Constants.INTENT_CHANNEL_ID, selectedChannel.getChannelID());
		intent.putExtra(Constants.INTENT_CHANNEL_TITLE, selectedChannel.getTitle());
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case ADD_TO_FAVORITES_CONTEXTMENU:
			db.open();
			Channel selectedChannel = (Channel) this.getListAdapter().getItem(info.position);

			if (db.insertFavorite(selectedChannel.getChannelID(), selectedChannel.getTitle(), selectedChannel.getChannelGroupID(),
					selectedChannel.getChannelGroupTitle()) > -1) {
				Toast.makeText(this, selectedChannel.getTitle() + " added to favorites.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Failed to add " + selectedChannel.getTitle() + " to favorites!", Toast.LENGTH_LONG).show();
			}
			db.close();

			break;
		}
		return true;
	}

	private void loadData() {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage("Loading. Please wait...");
		new LoadAllChannelTask(loadingDialog).execute();
	}

	private class LoadAllChannelTask extends AsyncTask<Void, Void, AllChannelListAdapter> {

		ProgressDialog progressDialog;

		public LoadAllChannelTask(ProgressDialog progressDialog) {
			this.progressDialog = progressDialog;
		}

		@Override
		public void onPreExecute() {
			this.progressDialog.show();
		}

		@Override
		protected AllChannelListAdapter doInBackground(Void... params) {
			return setupListAdapter();
		}

		@Override
		public void onPostExecute(AllChannelListAdapter result) {
			this.progressDialog.dismiss();
			AllChannelActivity.this.setListAdapter(result);

			ListView list = (ListView) AllChannelActivity.this.findViewById(android.R.id.list);
			AllChannelActivity.this.registerForContextMenu(list);
		}

		/**
		 * This is where we create and connect the adapter to this activity as
		 * well as the data.
		 */
		private AllChannelListAdapter setupListAdapter() {
			TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
			Channel[] channelList = tvAnimareServiceCall.GetChannelList();
			AllChannelListAdapter adapter = new AllChannelListAdapter(AllChannelActivity.this);

			String groupTitle = channelList[0].getChannelGroupTitle();
			List<Channel> group = new ArrayList<Channel>();
			for (Channel channel : channelList) {
				if (!channel.getChannelGroupTitle().equals(groupTitle)) {
					adapter.addSection(groupTitle, new ArrayAdapter<Channel>(AllChannelActivity.this, R.layout.all_channel_list_item, group));
					group = new ArrayList<Channel>();
				}
				group.add(channel);
				groupTitle = channel.getChannelGroupTitle();
			}

			return adapter;

			// return new ArrayAdapter<Channel>(AllChannelActivity.this,
			// android.R.layout.simple_list_item_1, channelList);

		}
	}
}
