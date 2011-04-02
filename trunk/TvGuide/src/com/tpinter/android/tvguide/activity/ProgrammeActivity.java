package com.tpinter.android.tvguide.activity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tpinter.android.tvguide.entity.Programme;
import com.tpinter.android.tvguide.utility.Constants;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class ProgrammeActivity extends ListActivity {

	protected Integer channelId;
	protected String channelTitle;
	protected LayoutInflater inflater;

	@SuppressWarnings("boxing")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programme_main);

		channelId = getIntent().getIntExtra(Constants.INTENT_CHANNEL_ID, -1);
		channelTitle = getIntent().getStringExtra(
				Constants.INTENT_CHANNEL_TITLE);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		loadData();
		TextView channelTitleTextView = (TextView) findViewById(R.id.channel_title);
		channelTitleTextView.setText(channelTitle);
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
		if (this.channelId >= 0) {
			ProgressDialog loadingDialog = new ProgressDialog(this);
			loadingDialog.setMessage("Loading. Please wait...");

			new LoadProgrammeTask(loadingDialog, this.channelId).execute();
		}
	}

	private class LoadProgrammeTask extends
			AsyncTask<Void, Void, CustomAdapter> {

		ProgressDialog progressDialog;
		int id;

		public LoadProgrammeTask(ProgressDialog progressDialog, int id) {
			this.progressDialog = progressDialog;
			this.id = id;
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
			ProgrammeActivity.this.setListAdapter(result);
		}

		/**
		 * This is where we create and connect the adapter to this activity as
		 * well as the data.
		 */
		private CustomAdapter setupListAdapter() {
			Vector<RowData> data = new Vector<RowData>();

			TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
			Programme[] programmelList = tvAnimareServiceCall
					.GetCurrentProgramme(this.id);

			for (Programme programme : programmelList) {
				data.add(new RowData(this.id, programme));
			}
			// return new ArrayAdapter<Programme>(ProgrammeActivity.this,
			// android.R.layout.simple_list_item_1, programmelList);

			return new CustomAdapter(ProgrammeActivity.this,
					R.layout.programme_list, R.id.programme_title, data);
		}
	}

	private class RowData {

		protected int channelID;
		protected String programmeDetail;
		protected String programmeTitle;
		protected String programmeDate;

		public RowData(int channelId, Programme programme) {
			this.channelID = channelId;
			this.programmeTitle = programme.getTitle();
			this.programmeDetail = programme.getSubTitle();
			this.programmeDate = new SimpleDateFormat(
					Constants.HOUR_MINUTE_DATA_FORMAT).format(programme
					.getStartDateTime());
		}

		@Override
		public String toString() {
			return programmeDetail + " " + programmeTitle;
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

			RowData rowData = getItem(position);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.programme_list, parent,
						false);
				holder = new ViewHolder(convertView);

				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();

			holder.getTitle().setText(rowData.programmeDetail);
			holder.getDetail().setText(rowData.programmeTitle);
			holder.getDate().setText(rowData.programmeDate);

			return convertView;
		}

		private class ViewHolder {
			private View view;
			private TextView title = null;
			private TextView detail = null;
			private TextView date = null;

			public ViewHolder(View row) {
				this.view = row;
				this.title = (TextView) view.findViewById(R.id.programme_title);
				this.detail = (TextView) view
						.findViewById(R.id.programme_detail);
				this.date = (TextView) view.findViewById(R.id.programme_date);
			}

			public TextView getTitle() {
				return title;
			}

			public TextView getDetail() {
				return detail;
			}

			public TextView getDate() {
				return date;
			}
		}
	}
}
