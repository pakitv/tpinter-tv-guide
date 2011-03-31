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

import com.tpinter.android.tvguide.entity.Channel;
import com.tpinter.android.tvguide.entity.Programme;

public class TvAnimareWebService {

	private static final String SOAP_ACTION = "http://tv.animare.hu/webservice/";
	private static final String NAMESPACE = "http://tv.animare.hu/webservice/";
	private static final String URL = "http://tv.animare.hu/webservice/tvguide.asmx";

	private boolean isResultVector = false;

	protected Object call(String soapAction, SoapSerializationEnvelope envelope) {
		Object result = null;

		final HttpTransportSE transportSE = new HttpTransportSE(URL);

		transportSE.debug = true;

		try {
			transportSE.call(soapAction, envelope);
			if (!isResultVector) {
				result = envelope.getResponse();
			} else {
				result = envelope.bodyIn;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final XmlPullParserException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Channel[] GetChannelList() {
		final String methodName = "ChannelList";

		// Create the outgoing message
		final SoapObject requestObject = new SoapObject(NAMESPACE, methodName);
		// Create soap envelop .use version 1.1 of soap
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		// add the outgoing object as the request
		envelope.setOutputSoapObject(requestObject);
		envelope.addMapping(NAMESPACE, Channel.CHANNEL_CLASS.getSimpleName(),
				Channel.CHANNEL_CLASS);

		// call and Parse Result.
		SoapObject response = (SoapObject) this.call(SOAP_ACTION + methodName,
				envelope);

		SoapObject channels = (SoapObject) ((SoapObject) response
				.getProperty(1)).getProperty(0);
		Channel[] channelList = new Channel[channels.getPropertyCount()];
		for (int i = 0; i < channelList.length; i++) {
			SoapObject property = (SoapObject) channels.getProperty(i);
			Channel channel = new Channel(property);
			channelList[i] = channel;
		}

		return channelList;
	}

	public Programme[] GetCurrentProgramme(int id) {

		final String methodName = "CurrentProgramme";
		final String parameter = "ChannelID";
		// Create the outgoing message
		final SoapObject requestObject = new SoapObject(NAMESPACE, methodName);
		// Set Parameter type String
		requestObject.addProperty(parameter, id);
		// Create soap envelop .use version 1.1 of soap
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		// add the outgoing object as the request
		envelope.setOutputSoapObject(requestObject);
		envelope.addMapping(NAMESPACE,
				Programme.PROGRAMME_CLASS.getSimpleName(),
				Programme.PROGRAMME_CLASS);
		// Register Marshaler
		// For date marshaling
		Marshal dateMarshal = new MarshalDate();
		dateMarshal.register(envelope);
		// For float marshaling
		Marshal floatMarshal = new MarshalFloat();
		floatMarshal.register(envelope);
		// call and Parse Result.

		SoapObject response = (SoapObject) this.call(SOAP_ACTION + methodName,
				envelope);
		SoapObject programmes = (SoapObject) ((SoapObject) response
				.getProperty(1)).getProperty(0);

		Programme[] programmeList = new Programme[programmes.getPropertyCount()];
		for (int i = 0; i < programmeList.length; i++) {
			SoapObject property = (SoapObject) programmes.getProperty(i);
			Programme programme = new Programme(property);
			programmeList[i] = programme;
		}

		return programmeList;
	}
}
