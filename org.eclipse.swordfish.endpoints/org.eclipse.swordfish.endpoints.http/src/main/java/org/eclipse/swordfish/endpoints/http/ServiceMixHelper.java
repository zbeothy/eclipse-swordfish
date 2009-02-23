package org.eclipse.swordfish.endpoints.http;

import java.util.Iterator;
import java.util.Map;

import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.ServiceMixException;
import org.apache.servicemix.nmr.api.internal.InternalEndpoint;
import org.apache.servicemix.nmr.api.internal.InternalReference;

public class ServiceMixHelper {
	public static InternalEndpoint getEndpoint(NMR nmr, Map<String, ?> props) {
		InternalReference reference = (InternalReference) nmr
				.getEndpointRegistry().lookup(props);
		Iterator<InternalEndpoint> endpointsIterator = reference.choose()
				.iterator();
		if (!endpointsIterator.hasNext()) {
			throw new ServiceMixException(
					"Coult not set the destination for the messageExchange");
		}
		return endpointsIterator.next();
	}
	public static java.io.InputStream convertStringToIS(String xml,String encoding){
		if(xml==null) return null;
		xml = xml.trim();
		java.io.InputStream in = null;
		try{
		in = new java.io.ByteArrayInputStream(xml.getBytes(encoding));
		}catch(Exception ex){
		}
		return in;
		}

}
