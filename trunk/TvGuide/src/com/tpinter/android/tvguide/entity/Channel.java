package com.tpinter.android.tvguide.entity;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class Channel implements KvmSerializable {
	public static Class<Channel> CHANNEL_CLASS = Channel.class;

	private String channelGroupTitle;

	private int channelID;

	private int channelGroupID;

	private String title;

	private String url;

	private String urlWebSite;

	public Channel() {
	}

	public Channel(SoapObject obj) {
		this.channelGroupTitle = getValue(obj.toString(), "ChannelGroupTitle");
		this.channelID = Integer.parseInt(getValue(obj.toString(), "ChannelID"));
		this.channelGroupID = Integer.parseInt(getValue(obj.toString(), "ChannelGroupID"));
		this.title = getValue(obj.toString(), "Title");
		this.url = getValue(obj.toString(), "Url");
		this.urlWebSite = getValue(obj.toString(), "UrlWebSite");
	}

	public Object getProperty(int index) {
		Object object = null;
		switch (index) {
		case 0: {
			object = this.channelGroupTitle;
			break;
		}
		case 1: {
			object = this.channelID;
			break;
		}
		case 2: {
			object = this.channelGroupID;
			break;
		}
		case 3: {
			object = this.title;
			break;
		}
		case 4: {
			object = this.urlWebSite;
			break;
		}
		case 5: {
			object = this.url;
			break;
		}
		}
		return object;
	}

	public int getPropertyCount() {
		return 6;
	}

	public void getPropertyInfo(int index, Hashtable hastable, PropertyInfo propertyInfo) {
		switch (index) {
		case 0: {
			propertyInfo.name = "ChannelGroupTitle";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 1: {
			propertyInfo.name = "ChannelID";
			propertyInfo.type = PropertyInfo.INTEGER_CLASS;
			break;
		}
		case 2: {
			propertyInfo.name = "ChannelGroupID";
			propertyInfo.type = PropertyInfo.INTEGER_CLASS;
			break;
		}
		case 3: {
			propertyInfo.name = "Title";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 4: {
			propertyInfo.name = "UrlWebSite";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 5: {
			propertyInfo.name = "Url";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		}
	}

	public void setProperty(int index, Object obj) {
		switch (index) {
		case 0: {
			this.channelGroupTitle = obj.toString();
			break;
		}
		case 1: {
			this.channelID = Integer.parseInt(obj.toString());
			break;
		}
		case 2: {
			this.channelGroupID = Integer.parseInt(obj.toString());
			break;
		}
		case 3: {
			this.title = obj.toString();
			break;
		}
		case 4: {
			this.urlWebSite = obj.toString();
			break;
		}
		case 5: {
			this.url = obj.toString();
			break;
		}
		}
	}

	private String getValue(String text, String propertyName) {
		text = text.substring(8);
		String[] properties = text.split(";");
		for (String property : properties) {
			String[] keyValue = property.trim().split("=");
			if (keyValue[0].equals(propertyName))
				return keyValue[1];
		}

		return "";
	}

	@Override
	public String toString() {
		return title;
	}

	public String getChannelGroupTitle() {
		return channelGroupTitle;
	}

	public void setChannelGroupTitle(String channelGroupTitle) {
		this.channelGroupTitle = channelGroupTitle;
	}

	public int getChannelID() {
		return channelID;
	}

	public void setChannelID(int channelID) {
		this.channelID = channelID;
	}

	public int getChannelGroupID() {
		return channelGroupID;
	}

	public void setChannelGroupID(int channelGroupID) {
		this.channelGroupID = channelGroupID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrlWebSite() {
		return urlWebSite;
	}

	public void setUrlWebSite(String urlWebSite) {
		this.urlWebSite = urlWebSite;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
