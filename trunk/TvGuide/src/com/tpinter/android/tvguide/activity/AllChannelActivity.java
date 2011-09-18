package com.tpinter.android.tvguide.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tpinter.android.tvguide.dbmanager.DBAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.loadtask.LoadAllChannelTask;
import com.tpinter.android.tvguide.utility.Constants;

public class AllChannelActivity extends ListActivity {

	public final static int ADD_TO_FAVORITES_CONTEXTMENU = 0;

	private DBAdapter db;

	private LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_channel_main);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		db = new DBAdapter(this);
		try {
			loadData();
		} catch (Exception e) {
			Log.e(AllChannelActivity.class.getName(), "Error loading data.", e);
		}
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
				Toast.makeText(this, getString(R.string.add_to_favorite_text, selectedChannel.getTitle()), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, getString(R.string.add_to_favorite_error_text, selectedChannel.getTitle()), Toast.LENGTH_LONG).show();
			}
			db.close();

			break;
		}
		return true;
	}

	private void loadData() {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage(getString(R.string.loading_text));
		new LoadAllChannelTask(this, loadingDialog).execute();
	}

	public LayoutInflater getInflater() {
		return inflater;
	}
}
