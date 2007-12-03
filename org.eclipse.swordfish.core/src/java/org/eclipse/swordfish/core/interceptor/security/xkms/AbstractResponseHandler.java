/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.interceptor.security.xkms;

/**
 * The Class AbstractResponseHandler.
 * 
 */
public abstract class AbstractResponseHandler {

    /** the response. */
    private String strResponse = null;

    /**
     * The Constructor.
     * 
     * @param xkmsResponse
     *        response
     */
    public AbstractResponseHandler(final String xkmsResponse) {
        this.strResponse = xkmsResponse;
    }

    public String getStrResponse() {
        return this.strResponse;
    }

    /**
     * Parses the response.
     * 
     * @throws XKMSResponseException
     *         exception
     */
    public abstract void parseResponse() throws XKMSResponseException;

    public void setStrResponse(final String strResponse) {
        this.strResponse = strResponse;
    }

}
