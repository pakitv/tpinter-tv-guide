package com.tpinter.android.tvguide.rowdata;

import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.entity.Programme;

public class FavoriteRowData {

	private final int channelID;

	private final String title;

	private final int progressBarStatus;

	private final Programme[] programmes;

	public FavoriteRowData(Channel channel, Programme[] programmes, int progressBarStatus) {
		this.channelID = channel.getChannelID();
		this.title = channel.getTitle();
		this.programmes = programmes;
		this.progressBarStatus = progressBarStatus;
	}

	@Override
	public String toString() {
		return title + " " + getProgrammeTitleFirst();
	}

	public int getChannelID() {
		return channelID;
	}

	public String getTitle() {
		return title;
	}

	public String getProgrammeTitleFirst() {
		return programmes[0].getTitle();
	}

	public String getProgrammeTitleFirstWithDate() {
		return programmes[0].toString();
	}

	public String getProgrammeTitleSecond() {
		return programmes[1].getTitle();
	}

	public String getProgrammeTitleSecondWithDate() {
		return programmes[1].toString();
	}

	public String getProgrammeTitleThird() {
		return programmes[2].getTitle();
	}

	public String getProgrammeTitleThirdWithDate() {
		return programmes[2].toString();
	}
}
