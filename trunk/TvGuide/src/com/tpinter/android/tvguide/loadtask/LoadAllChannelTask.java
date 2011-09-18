package com.tpinter.android.tvguide.loadtask;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;

import com.tpinter.android.tvguide.activity.AllChannelActivity;
import com.tpinter.android.tvguide.activity.R;
import com.tpinter.android.tvguide.adapter.AllChannelListAdapter;
import com.tpinter.android.tvguide.adapter.AllChannelListChannelAdapter;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.webservice.TvAnimareWebService;

public class LoadAllChannelTask extends AsyncTask<Void, Void, AllChannelListAdapter> {

	private final AllChannelActivity activity;

	private final ProgressDialog progressDialog;

	public LoadAllChannelTask(AllChannelActivity allChannelActivity, ProgressDialog progressDialog) {
		this.activity = allChannelActivity;
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
		activity.setListAdapter(result);

		ListView list = (ListView) activity.findViewById(android.R.id.list);
		activity.registerForContextMenu(list);
	}

	/**
	 * This is where we create and connect the adapter to this activity as well
	 * as the data.
	 */
	private AllChannelListAdapter setupListAdapter() {
		TvAnimareWebService tvAnimareServiceCall = new TvAnimareWebService();
		Channel[] channelList = tvAnimareServiceCall.GetChannelList();
		AllChannelListAdapter adapter = new AllChannelListAdapter(activity);

		String groupTitle = channelList[0].getChannelGroupTitle();
		List<Channel> group = new ArrayList<Channel>();
		for (Channel channel : channelList) {
			if (!channel.getChannelGroupTitle().equals(groupTitle)) {
				adapter.addSection(groupTitle, new AllChannelListChannelAdapter(activity, R.layout.all_channel_list_item, group, activity.getInflater()));
				group = new ArrayList<Channel>();
			}
			group.add(channel);
			groupTitle = channel.getChannelGroupTitle();
		}

		return adapter;
	}
}
