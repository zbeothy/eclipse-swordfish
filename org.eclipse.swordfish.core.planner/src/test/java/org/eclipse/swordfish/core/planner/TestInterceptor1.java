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
package org.eclipse.swordfish.core.planner;

import java.util.Map;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;
import org.junit.Ignore;

/**
 * @author dwolz
 *
 */
@Ignore
public class TestInterceptor1 implements Interceptor {



	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.Interceptor#getProperties()
	 */
	public Map<String, ?> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.Interceptor#process(org.apache.servicemix.nmr.api.Exchange)
	 */
	public void process(MessageExchange exchange) throws SwordfishException {
		// TODO Auto-generated method stub

	}


}
