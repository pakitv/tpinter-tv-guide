package com.tpinter.android.tvguide.webservice;

import java.io.IOException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.tpinter.android.tvguide.entity.Person;

public class ServiceCall
{
	private static final String SOAP_ACTION = 
		"http://tempuri.org/";
	private static final String NAMESPACE = 
		"http://tempuri.org/";
	private static final String URL = 
		"http://bimbim.in/Sample/TestService.asmx";

	private boolean isResultVector = false;

	protected Object call(String soapAction, 
			SoapSerializationEnvelope envelope)
	{
		Object result = null;
		
		final HttpTransportSE transportSE = new HttpTransportSE(URL);
		
		transportSE.debug = false;

		// call and Parse Result.
		try
		{
			transportSE.call(soapAction, envelope);
			if (!isResultVector)
			{
				result = envelope.getResponse();
			} else
			{
				result = envelope.bodyIn;
			}
		} catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final XmlPullParserException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Person CallGetSingle()
	{
		final String sGetSingle = "GetSingle";

		// Create the outgoing message
		final SoapObject requestObject = 
			new SoapObject(NAMESPACE, sGetSingle);
		// Create soap envelop .use version 1.1 of soap
		final SoapSerializationEnvelope envelope = 
			new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		// add the outgoing object as the request
		envelope.setOutputSoapObject(requestObject);
		envelope.addMapping(NAMESPACE, 
				Person.PERSON_CLASS.getSimpleName(),
				Person.PERSON_CLASS);

		// call and Parse Result.
		final Object response = this.call(
				SOAP_ACTION + sGetSingle, envelope);
		Person result = null;
		if (response != null)
		{
			result = new Person((SoapObject) response);
		}

		return result;
	}

	public Boolean CallSetValue(Person param)
	{
		final String sSetValue = "SetValue";
		final String svalue = "value";
		// Create the outgoing message
		final SoapObject requestObject = 
			new SoapObject(NAMESPACE, sSetValue);
		// Set Parameter type String
		requestObject.addProperty(svalue, param);
		// Create soap envelop .use version 1.1 of soap
		final SoapSerializationEnvelope envelope = 
			new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		// add the outgoing object as the request
		envelope.setOutputSoapObject(requestObject);
		envelope.addMapping(NAMESPACE, 
				Person.PERSON_CLASS.getSimpleName(),
				Person.PERSON_CLASS);
		// Register Marshaler
		// For date marshaling
		Marshal dateMarshal = new MarshalDate();
		dateMarshal.register(envelope);
		// For float marshaling
		Marshal floatMarshal = new MarshalFloat();
		floatMarshal.register(envelope);
		// call and Parse Result.
		final Object response = this.call(
				SOAP_ACTION + sSetValue, envelope);
		Boolean result = null;
		if (response != null)
		{
			try
			{
				if (response != null
						&& response.getClass() == 
							org.ksoap2.serialization.SoapPrimitive.class)
				{
					result = Boolean.parseBoolean(response.toString());
				}
			} catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return result;
	}
}
