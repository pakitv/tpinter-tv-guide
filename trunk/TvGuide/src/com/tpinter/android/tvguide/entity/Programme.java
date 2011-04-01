package com.tpinter.android.tvguide.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class Programme implements KvmSerializable {
	public static Class<Programme> PROGRAMME_CLASS = Programme.class;

	public final static String DATA_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";

	private Date startDateTime;
	private String title;
	private String subTitle;
	private String category;
	private String url;
	private SimpleDateFormat format = new SimpleDateFormat(DATA_FORMAT);

	public Programme() {

	}

	public Programme(SoapObject obj) {
		this.title = getValue(obj.toString(), "Title");
		this.url = getValue(obj.toString(), "Url");
		this.subTitle = getValue(obj.toString(), "SubTitle");
		this.category = getValue(obj.toString(), "Category");

		// String[] parts = getValue(obj.toString(),
		// "StartDateTime").split("T")[1]
		// .split(":");
		// this.startDateTime = parts[0] + ":" + parts[1];

		try {
			this.startDateTime = format.parse(getValue(obj.toString(),
					"StartDateTime").substring(0, 19));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Object getProperty(int index) {
		Object object = null;
		switch (index) {
		case 0: {
			object = this.startDateTime;
			break;
		}
		case 1: {
			object = this.title;
			break;
		}
		case 2: {
			object = this.subTitle;
			break;
		}
		case 3: {
			object = this.category;
			break;
		}
		case 4: {
			object = this.url;
			break;
		}
		}
		return object;
	}

	public int getPropertyCount() {
		return 5;
	}

	public void getPropertyInfo(int index, Hashtable hastable,
			PropertyInfo propertyInfo) {
		switch (index) {
		case 0: {
			propertyInfo.name = "StartDateTime";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 1: {
			propertyInfo.name = "Title";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 2: {
			propertyInfo.name = "SubTitle";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 3: {
			propertyInfo.name = "Category";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 4: {
			propertyInfo.name = "Url";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		}
	}

	public void setProperty(int index, Object obj) {
		switch (index) {
		case 0: {
			try {
				this.startDateTime = format.parse(getValue(obj.toString(),
						"StartDateTime").substring(0, 19));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
		}
		case 1: {
			this.title = obj.toString();
			break;
		}
		case 2: {
			this.subTitle = obj.toString();
			break;
		}
		case 3: {
			this.title = obj.toString();
			break;
		}
		case 4: {
			this.category = obj.toString();
			break;
		}
		case 5: {
			this.url = obj.toString();
			break;
		}
		}
	}

	private String getValue(String text, String propertyName) {
		int startPosition = text.indexOf(propertyName) + propertyName.length()
				+ 1;
		int endPosition = text.indexOf(";", text.indexOf(propertyName));

		String result = startPosition == 0 ? "" : text.substring(startPosition,
				endPosition);

		return result;
	}

	public String toString() {
		return startDateTime.toLocaleString() + " " + title;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
