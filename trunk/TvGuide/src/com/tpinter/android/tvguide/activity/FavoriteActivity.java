package com.tpinter.android.tvguide.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tpinter.android.tvguide.dbmanager.DBAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.loadtask.LoadFavoritesTask;
import com.tpinter.android.tvguide.rowdata.FavoriteRowData;
import com.tpinter.android.tvguide.utility.Constants;

public class FavoriteActivity extends ListActivity {

	public final static int REMOVE_FROM_FAVORITES_CONTEXTMENU = 0;

	private DBAdapter db;

	private LayoutInflater inflater;

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
					Log.e(FavoriteActivity.class.getName(), getString(R.string.loading_error_text), e);
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

		FavoriteRowData selectedRowData = (FavoriteRowData) this.getListAdapter().getItem(position);
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
				Log.e(FavoriteActivity.class.getName(), getString(R.string.loading_error_text), e);
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
				Toast.makeText(this, getString(R.string.remove_from_favorite_text), Toast.LENGTH_SHORT).show();
				loadData();
			} else {
				Toast.makeText(this, getString(R.string.remove_from_favorite_error_text), Toast.LENGTH_LONG).show();
			}
			db.close();

			break;
		}
		return true;
	}

	private void loadData() {
		ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage(getString(R.string.loading_text));
		new LoadFavoritesTask(this, loadingDialog).execute();
	}

	public LayoutInflater getInflater() {
		return inflater;
	}
}