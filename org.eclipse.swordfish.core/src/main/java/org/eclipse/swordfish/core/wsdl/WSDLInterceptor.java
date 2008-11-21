package org.eclipse.swordfish.core.wsdl;

import org.apache.servicemix.nmr.api.NMR;
import org.eclipse.swordfish.api.Interceptor;


/**
 * Generic Swordfish message exchange interceptor expecting a CamelExchange as paraameter
 */

public interface WSDLInterceptor extends Interceptor {

	/**
	 * used to inject the servicemix nmr
	 */
	void setNmr(NMR nmr);

	/**
	 * used to inject the wsdl manager
	 */
	void setWSDLManager(WSDLManager wsdlManager);

}
