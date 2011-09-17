package com.tpinter.android.tvguide.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tpinter.android.tvguide.activity.R;
import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.viewholder.AllChannelViewHolder;

public class AllChannelListCustomAdapter extends ArrayAdapter<Channel> {

	private final int resource;

	private final LayoutInflater inflater;

	private final Context context;

	public AllChannelListCustomAdapter(Context context, int textViewResourceId, List<Channel> objects, LayoutInflater inflater) {
		super(context, textViewResourceId, objects);
		this.resource = textViewResourceId;
		this.inflater = inflater;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AllChannelViewHolder holder = null;

		Channel channel = getItem(position);

		if (convertView == null) {
			convertView = inflater.inflate(resource, parent, false);
			holder = new AllChannelViewHolder(convertView);

			convertView.setTag(holder);
		}
		holder = (AllChannelViewHolder) convertView.getTag();

		holder.getTitle().setText(channel.getTitle());
		holder.getImage().setImageResource(R.drawable.plus_icon);

		// ImageView imageview = (ImageView)
		// convertView.findViewById(R.id.all_channel_add_img);
		// imageview.setOnClickListener(new ImageViewClickListener(channel));

		return convertView;
	}

	public int getResource() {
		return resource;
	}

	// class ImageViewClickListener implements OnClickListener {
	// private final Channel channel;
	//
	// public ImageViewClickListener(Channel channel) {
	// this.channel = channel;
	// }
	//
	// public void onClick(View view) {
	// DBAdapter db = new DBAdapter(AllChannelListCustomAdapter.this.context);
	//
	// db.open();
	// if (db.insertFavorite(channel.getChannelID(), channel.getTitle(),
	// channel.getChannelGroupID(), channel.getChannelGroupTitle()) > -1) {
	// Toast.makeText(AllChannelListCustomAdapter.this.context,
	// channel.getTitle() + " added to favorites.", Toast.LENGTH_LONG).show();
	// } else {
	// Toast.makeText(AllChannelListCustomAdapter.this.context, "Failed to add "
	// + channel.getTitle() + " to favorites!", Toast.LENGTH_LONG).show();
	// }
	// db.close();
	//
	// }
	// }
}
