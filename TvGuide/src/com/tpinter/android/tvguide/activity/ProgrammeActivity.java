package com.tpinter.android.tvguide.activity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
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
		channelTitle = getIntent().getStringExtra(Constants.INTENT_CHANNEL_TITLE);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		loadData();
		// View programmeHeader =
		// getLayoutInflater().inflate(R.layout.programme_header, null, false);
		//
		// ListView list = (ListView) findViewById(R.id.list);
		// list.addHeaderView(programmeHeader);
		// TextView channelTitleTextView = (TextView)
		// findViewById(R.id.channel_title);
		// channelTitleTextView.setText(channelTitle);
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
		if (this.channelId >= 0) {
			ProgressDialog loadingDialog = new ProgressDialog(this);
			loadingDialog.setMessage("Loading. Please wait...");

			new LoadProgrammeTask(loadingDialog, this.channelId, this.channelTitle).execute();
		}
	}

	private class LoadProgrammeTask extends AsyncTask<Void, Void, CustomAdapter> {

		ProgressDialog progressDialog;

		int channelId;

		String channelTitle;

		public LoadProgrammeTask(ProgressDialog progressDialog, int id, String channelTitle) {
			this.progressDialog = progressDialog;
			this.channelId = id;
			this.channelTitle = channelTitle;
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

		public final static String ITEM_DATE = "date";

		public final static String ITEM_TITLE = "title";

		public final static String ITEM_DETAILE = "detaile";

		public Map<String, ?> createItem(String date, String title, String detaile) {
			Map<String, String> item = new HashMap<String, String>();
			item.put(ITEM_DATE, date);
			item.put(ITEM_TITLE, title);
			item.put(ITEM_DETAILE, detaile);
			return item;
		}

		/**
		 * This is where we create and connect the adapter to this activity as
		 * well as the data.
		 */
		private CustomAdapter setupListAdapter() {
			Vector<RowData> data = new Vector<RowData>();

			TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
			Programme[] programmelList = tvAnimareServiceCall.GetCurrentProgramme(this.channelId);

			List<Map<String, ?>> programmes = new LinkedList<Map<String, ?>>();
			for (Programme programme : programmelList) {
				String date = new SimpleDateFormat(Constants.HOUR_MINUTE_DATA_FORMAT).format(programme.getStartDateTime());
				String detaile = programme.getSubTitle() == "" ? programme.getCategory() : programme.getSubTitle();
				programmes.add(createItem(date, programme.getTitle(), detaile));
				data.add(new RowData(this.channelId, programme));
			}
			// return new ArrayAdapter<Programme>(ProgrammeActivity.this,
			// android.R.layout.simple_list_item_1, programmelList);

			CustomAdapter adapter = new CustomAdapter(ProgrammeActivity.this);
			adapter.addSection(this.channelTitle, new SimpleAdapter(ProgrammeActivity.this, programmes, R.layout.programme_list, new String[] { ITEM_DATE,
					ITEM_TITLE, ITEM_DETAILE }, new int[] { R.id.programme_date, R.id.programme_title, R.id.programme_detail }));

			return adapter;

			// return new CustomAdapter(ProgrammeActivity.this,
			// R.layout.programme_list, R.id.programme_title, data);
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
			this.programmeDetail = programme.getCategory() == "" ? programme.getCategory() : programme.getSubTitle();
			this.programmeDate = new SimpleDateFormat(Constants.HOUR_MINUTE_DATA_FORMAT).format(programme.getStartDateTime());
		}

		@Override
		public String toString() {
			return programmeDetail + " " + programmeTitle;
		}
	}

	private class CustomAdapter extends BaseAdapter {

		public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();

		public final ArrayAdapter<String> headers;

		public final static int TYPE_SECTION_HEADER = 0;

		public CustomAdapter(Context context) {
			this.headers = new ArrayAdapter<String>(context, R.layout.programme_header);
		}

		public void addSection(String section, Adapter adapter) {
			this.headers.add(section);
			this.sections.put(section, adapter);
		}

		public Object getItem(int position) {
			for (Object section : this.sections.keySet()) {
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if (position == 0)
					return section;
				if (position < size)
					return adapter.getItem(position - 1);

				// otherwise jump into next section
				position -= size;
			}
			return null;
		}

		public int getCount() {
			// total together all sections, plus one for each section header
			int total = 0;
			for (Adapter adapter : this.sections.values())
				total += adapter.getCount() + 1;
			return total;
		}

		@Override
		public int getViewTypeCount() {
			// assume that headers count as one, then total all sections
			int total = 1;
			for (Adapter adapter : this.sections.values())
				total += adapter.getViewTypeCount();
			return total;
		}

		@Override
		public int getItemViewType(int position) {
			int type = 1;
			for (Object section : this.sections.keySet()) {
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if (position == 0)
					return TYPE_SECTION_HEADER;
				if (position < size)
					return type + adapter.getItemViewType(position - 1);

				// otherwise jump into next section
				position -= size;
				type += adapter.getViewTypeCount();
			}
			return -1;
		}

		public boolean areAllItemsSelectable() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return (getItemViewType(position) != TYPE_SECTION_HEADER);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			int sectionnum = 0;
			for (Object section : this.sections.keySet()) {
				Adapter adapter = sections.get(section);
				int size = adapter.getCount() + 1;

				// check if position inside this section
				if (position == 0)
					return headers.getView(sectionnum, convertView, parent);
				if (position < size)
					return adapter.getView(position - 1, convertView, parent);

				// otherwise jump into next section
				position -= size;
				sectionnum++;
			}
			return null;
		}

		public long getItemId(int position) {
			return position;
		}
	}

	// private class CustomAdapter extends ArrayAdapter<RowData> {
	//
	// public CustomAdapter(Context context, int resource, int
	// textViewResourceId, List<RowData> objects) {
	// super(context, resource, textViewResourceId, objects);
	//
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder holder = null;
	//
	// RowData rowData = getItem(position);
	//
	// if (convertView == null) {
	// convertView = inflater.inflate(R.layout.programme_list, parent, false);
	// holder = new ViewHolder(convertView);
	//
	// convertView.setTag(holder);
	// }
	// holder = (ViewHolder) convertView.getTag();
	//
	// holder.getTitle().setText(rowData.programmeTitle);
	// holder.getDetail().setText(rowData.programmeDetail);
	// holder.getDate().setText(rowData.programmeDate);
	//
	// return convertView;
	// }
	// }

	private class ViewHolder {
		private final View view;

		private TextView title = null;

		private TextView detail = null;

		private TextView date = null;

		public ViewHolder(View row) {
			this.view = row;
			this.title = (TextView) view.findViewById(R.id.programme_title);
			this.detail = (TextView) view.findViewById(R.id.programme_detail);
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
