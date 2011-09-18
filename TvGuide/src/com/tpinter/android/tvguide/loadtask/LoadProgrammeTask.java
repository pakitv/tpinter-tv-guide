package com.tpinter.android.tvguide.loadtask;

import static com.tpinter.android.tvguide.utility.StringUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.SimpleAdapter;

import com.tpinter.android.tvguide.activity.ProgrammeActivity;
import com.tpinter.android.tvguide.activity.R;
import com.tpinter.android.tvguide.adapter.ProgrammeAdapter;
import com.tpinter.android.tvguide.entity.Programme;
import com.tpinter.android.tvguide.utility.Constants;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class LoadProgrammeTask extends AsyncTask<Void, Void, ProgrammeAdapter> {

	private final ProgrammeActivity activity;

	private final ProgressDialog progressDialog;

	private static final String[] ITEM_KEYS = new String[] { Constants.ITEM_DATE, Constants.ITEM_TITLE, Constants.ITEM_DETAIL };

	private static final int[] RESOURCE_IDS = new int[] { R.id.programme_date, R.id.programme_title, R.id.programme_detail };

	public LoadProgrammeTask(ProgrammeActivity programmeActivity, ProgressDialog progressDialog) {
		this.activity = programmeActivity;
		this.progressDialog = progressDialog;
	}

	@Override
	public void onPreExecute() {
		this.progressDialog.show();
	}

	@Override
	protected ProgrammeAdapter doInBackground(Void... params) {
		return setupListAdapter();
	}

	@Override
	public void onPostExecute(ProgrammeAdapter result) {
		this.progressDialog.dismiss();
		activity.setListAdapter(result);
	}

	/**
	 * This is where we create and connect the adapter to this activity as well
	 * as the data.
	 */
	private ProgrammeAdapter setupListAdapter() {
		TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
		Programme[] programmelList = tvAnimareServiceCall.GetCurrentProgramme(activity.getChannelId());

		List<Map<String, ?>> programmes = new LinkedList<Map<String, ?>>();
		for (Programme programme : programmelList) {
			String date = new SimpleDateFormat(Constants.HOUR_MINUTE_DATA_FORMAT).format(programme.getStartDateTime());
			String detail = !isEmpty(programme.getCategory()) ? programme.getCategory() : programme.getSubTitle();
			programmes.add(createItem(date, programme.getTitle(), detail));
		}

		ProgrammeAdapter adapter = new ProgrammeAdapter(activity);
		adapter.addSection(activity.getChannelTitle(), new SimpleAdapter(activity, programmes, R.layout.programme_list, ITEM_KEYS, RESOURCE_IDS));

		return adapter;
	}

	public Map<String, String> createItem(String date, String title, String detail) {
		Map<String, String> item = new HashMap<String, String>();
		item.put(Constants.ITEM_DATE, date);
		item.put(Constants.ITEM_TITLE, title);
		item.put(Constants.ITEM_DETAIL, detail);
		return item;
	}
}
