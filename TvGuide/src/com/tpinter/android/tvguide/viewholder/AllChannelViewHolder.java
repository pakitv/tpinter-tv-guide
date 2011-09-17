package com.tpinter.android.tvguide.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpinter.android.tvguide.activity.R;

public class AllChannelViewHolder {

	private TextView title = null;

	private ImageView image = null;

	public AllChannelViewHolder(View view) {
		this.title = (TextView) view.findViewById(R.id.list_item_title);
		this.image = (ImageView) view.findViewById(R.id.all_channel_add_img);
	}

	public TextView getTitle() {
		return title;
	}

	public ImageView getImage() {
		return image;
	}
}
