package com.tpinter.android.tvguide.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.tpinter.android.tvguide.entity.Programme;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class ProgrammeActivity extends ListActivity {

	private class LoadProgrammeTask extends
			AsyncTask<Void, Void, ArrayAdapter<Programme>> {

		ProgressDialog progressDialog;
		int id;

		public LoadProgrammeTask(ProgressDialog progressDialog, int id) {
			this.progressDialog = progressDialog;
			this.id = id;
		}

		public void onPreExecute() {
			this.progressDialog.show();
		}

		protected ArrayAdapter<Programme> doInBackground(Void... params) {
			return setupListAdapter();
		}

		public void onPostExecute(ArrayAdapter<Programme> result) {
			this.progressDialog.dismiss();
			ProgrammeActivity.this.setListAdapter(result);
		}

		/**
		 * This is where we create and connect the adapter to this activity as
		 * well as the data.
		 */
		private ArrayAdapter<Programme> setupListAdapter() {
			TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
			Programme[] programmelList = tvAnimareServiceCall
					.GetCurrentProgramme(this.id);

			return new ArrayAdapter<Programme>(ProgrammeActivity.this,
					android.R.layout.simple_list_item_1, programmelList);

		}
	}

	private Integer channelId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programme_main);

		channelId = getIntent().getIntExtra("ChannelId", -1);

		loadData();
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
			startActivityForResult(new Intent(this, AllChannelActivity.class),
					0);
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
			loadingDialog.setMessage("Loading. Please wait...");

			new LoadProgrammeTask(loadingDialog, channelId).execute();
		}
	}
}
