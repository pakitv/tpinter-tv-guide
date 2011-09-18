package com.tpinter.android.tvguide.adapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.tpinter.android.tvguide.activity.FavoriteActivity;
import com.tpinter.android.tvguide.activity.R;
import com.tpinter.android.tvguide.rowdata.FavoriteRowData;
import com.tpinter.android.tvguide.viewholder.FavoriteViewHolder;

public class FavoriteAdapter extends ArrayAdapter<FavoriteRowData> {

	private final FavoriteActivity activity;

	private final int resource;

	public FavoriteAdapter(Context context, int resource, int textViewResourceId, List<FavoriteRowData> objects) {
		super(context, resource, textViewResourceId, objects);
		this.activity = (FavoriteActivity) context;
		this.resource = resource;
	}

	@SuppressWarnings("boxing")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FavoriteViewHolder holder = null;

		FavoriteRowData rowData = getItem(position);

		if (convertView == null) {
			convertView = activity.getInflater().inflate(resource, parent, false);
			holder = new FavoriteViewHolder(convertView);

			convertView.setTag(holder);
		}
		holder = (FavoriteViewHolder) convertView.getTag();

		holder.getTitle().setText(rowData.getTitle());
		holder.getDetailFirst().setText(rowData.getProgrammeTitleFirstWithDate());
		holder.getDetailSecond().setText(rowData.getProgrammeTitleSecondWithDate());
		holder.getDetailThird().setText(rowData.getProgrammeTitleThirdWithDate());
		holder.getImage().setImageBitmap(getImageBitmap(activity.getString(R.string.channel_logo_url, rowData.getChannelID())));
		// holder.getProgressBar().setProgress(rowData.progressBarStatus);

		return convertView;
	}

	private Bitmap getImageBitmap(String url) {
		Bitmap bitmap = null;
		try {
			URL aURL = new URL(url);
			URLConnection connection = aURL.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
			bitmap = BitmapFactory.decodeStream(bufferedInputStream);
			bufferedInputStream.close();
			inputStream.close();
		} catch (IOException e) {
			Log.e(FavoriteAdapter.class.getName() + ".getImageBitmap", "Error getting bitmap", e);
		}
		return bitmap;
	}
}
