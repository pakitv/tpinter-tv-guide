package com.tpinter.android.tvguide.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpinter.android.tvguide.activity.R;

public class FavoriteViewHolder {

	private final View view;

	private final TextView title;

	private final TextView detailFirst;

	private final TextView detailSecond;

	private final TextView detailThird;

	private ImageView image = null;

	// private final ProgressBar progressBar;

	public FavoriteViewHolder(View row) {
		this.view = row;
		this.title = (TextView) view.findViewById(R.id.title);
		this.detailFirst = (TextView) view.findViewById(R.id.detailFirst);
		this.detailSecond = (TextView) view.findViewById(R.id.detailSecond);
		this.detailThird = (TextView) view.findViewById(R.id.detailThird);
		this.image = (ImageView) row.findViewById(R.id.img);
		// this.progressBar = (ProgressBar)
		// view.findViewById(R.id.progressbar);
	}

	public TextView getTitle() {
		return title;
	}

	public TextView getDetailFirst() {
		return detailFirst;
	}

	public TextView getDetailSecond() {
		return detailSecond;
	}

	public TextView getDetailThird() {
		return detailThird;
	}

	public ImageView getImage() {
		return image;
	}

	// public ProgressBar getProgressBar() {
	// return progressBar;
	// }
}
