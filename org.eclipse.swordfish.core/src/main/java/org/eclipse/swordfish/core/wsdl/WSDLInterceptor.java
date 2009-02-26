/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
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
