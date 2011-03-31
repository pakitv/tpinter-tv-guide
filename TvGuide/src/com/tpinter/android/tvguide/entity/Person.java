/**
 * 
 */
package com.tpinter.android.tvguide.entity;

import java.util.Date;
import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

/**
 * @author Bimbim
 * 
 */
public class Person implements KvmSerializable {
	public static Class PERSON_CLASS = Person.class;
	private String _name = "";
	private int _age = 0;
	private float _salary = 100000.0f;
	private Date _dob = new Date(1980, 01, 15);

	public Person() {
	}

	public Person(SoapObject obj) {
		this._name = obj.getProperty("Name").toString();
		this._age = Integer.parseInt(obj.getProperty("Age").toString());
		this._salary = Float.parseFloat(obj.getProperty("Salary").toString());

		String dob = obj.getProperty("Dob").toString();
		// Change date string according to your local
		String[] parts = dob.split("T")[0].split("-");
		this._dob = new Date(
				Date.UTC(Integer.parseInt(parts[0]),
						Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
						0, 0, 0));
	}

	/**
	 * @return the _name
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * @param name
	 *            the _name to set
	 */
	public void set_name(String name) {
		_name = name;
	}

	/**
	 * @return the _age
	 */
	public int get_age() {
		return _age;
	}

	/**
	 * @param age
	 *            the _age to set
	 */
	public void set_age(int age) {
		_age = age;
	}

	/**
	 * @return the _salary
	 */
	public float get_salary() {
		return _salary;
	}

	/**
	 * @param salary
	 *            the _salary to set
	 */
	public void set_salary(float salary) {
		_salary = salary;
	}

	/**
	 * @return the _dob
	 */
	public Date get_dob() {
		return _dob;
	}

	/**
	 * @param dob
	 *            the _dob to set
	 */
	public void set_dob(Date dob) {
		_dob = dob;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	 */
	public Object getProperty(int index) {
		Object object = null;
		switch (index) {
		case 0: {
			object = this._name;
			break;
		}
		case 1: {
			object = this._age;
			break;
		}
		case 2: {
			object = this._salary;
			break;
		}
		case 3: {
			object = this._dob;
			break;
		}
		}
		return object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
	 */
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ksoap2.serialization.KvmSerializable# getPropertyInfo(int,
	 * java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
	 */
	public void getPropertyInfo(int index, Hashtable arg1,
			PropertyInfo propertyInfo) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0: {
			propertyInfo.name = "Name";
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			break;
		}
		case 1: {
			propertyInfo.name = "Age";
			propertyInfo.type = PropertyInfo.INTEGER_CLASS;
			break;
		}
		case 2: {
			propertyInfo.name = "Salary";
			propertyInfo.type = Float.class;
			break;
		}
		case 3: {
			propertyInfo.name = "Dob";
			propertyInfo.type = Date.class;
			break;
		}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ksoap2.serialization.KvmSerializable#setProperty (int,
	 * java.lang.Object)
	 */
	public void setProperty(int index, Object obj) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0: {
			this._name = obj.toString();
			break;
		}
		case 1: {
			this._age = Integer.parseInt(obj.toString());
			break;
		}
		case 2: {
			this._salary = Float.parseFloat(obj.toString());
			break;
		}
		case 3: {
			this._dob = new Date(Date.parse(obj.toString()));
			break;
		}
		}
	}

}
