package com.tpinter.android.tvguide.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tpinter.android.tvguide.loadtask.LoadProgrammeTask;
import com.tpinter.android.tvguide.utility.Constants;

public class ProgrammeActivity extends ListActivity {

	protected int channelId;

	protected String channelTitle;

	protected LayoutInflater inflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programme_main);

		channelId = getIntent().getIntExtra(Constants.INTENT_CHANNEL_ID, -1);
		channelTitle = getIntent().getStringExtra(Constants.INTENT_CHANNEL_TITLE);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			loadData();
		} catch (Exception e) {
			Log.e(ProgrammeActivity.class.getName(), getString(R.string.loading_error_text), e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.programme_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			loadData();
			break;
		case R.id.all_channel:
			startActivityForResult(new Intent(this, AllChannelActivity.class), 0);
			break;
		case R.id.favorite:
			startActivityForResult(new Intent(this, FavoriteActivity.class), 0);
			break;
		}
		return true;
	}

	private void loadData() {
		if (channelId >= 0) {
			ProgressDialog loadingDialog = new ProgressDialog(this);
			loadingDialog.setMessage(getString(R.string.loading_text));

			new LoadProgrammeTask(this, loadingDialog).execute();
		}
	}

	public int getChannelId() {
		return channelId;
	}

	public String getChannelTitle() {
		return channelTitle;
	}
}
