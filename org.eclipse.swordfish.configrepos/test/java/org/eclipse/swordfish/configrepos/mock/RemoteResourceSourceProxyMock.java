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
package org.eclipse.swordfish.configrepos.mock;

import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.swordfish.configrepos.resource.sources.RemoteResourceSourceProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * The Class RemoteResourceSourceProxyMock.
 * 
 */
public class RemoteResourceSourceProxyMock implements RemoteResourceSourceProxy {

    /** The Constant PAYLOAD. */
    public static final String PAYLOAD =
            "<![CDATA[<body><p>This package provides the library for defining basic scope paths exchangeable via XML.</p><p>The PAPI developer can create a ScopePath and insert a number of PathParts into it, which will allow the SOP Configuration Repository feature to lookup configurations / resources according to the structure defined for the related tree in the repository.</p></body>]]>";

    /** The Constant NAMESPACE. */
    public static final String NAMESPACE = "http://types.sopware.org/configuration/ConfigurationRuntime/1.0";

    /** The factory. */
    private DocumentBuilderFactory factory = null;

    /** The logger. */
    private Logger logger = Logger.getLogger(RemoteResourceSourceProxyMock.class.getName());

    /**
     * Instantiates a new remote resource source proxy mock.
     */
    public RemoteResourceSourceProxyMock() {
        super();
        this.factory = DocumentBuilderFactory.newInstance();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aRequest
     *        the request
     * @param aIdentity
     *        the a identity
     * 
     * @return the resource data
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.eclipse.swordfish.configrepos.resource.sources.RemoteResourceSourceProxy#getResourceData(org.eclipse.swordfish.papi.ParticipantIdentity,
     *      org.w3c.dom.Document)
     * @exception Exception
     *            passed through
     */
    public Document getResourceData(final String appID, final String instID, final Document aRequest) throws Exception {
        this.logger.entering("RemoteResourceSourceProxyMock", "getResourceData", new Object[] {appID + "/" + instID, aRequest});
        try {
            Document doc = this.factory.newDocumentBuilder().newDocument();
            Element root = doc.createElementNS(NAMESPACE, "resourceResponse");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NAMESPACE);
            if (null != PAYLOAD) {
                Text text = doc.createTextNode(new String(Base64.encodeBase64(PAYLOAD.getBytes())));
                root.appendChild(text);
            }
            doc.appendChild(root);
            return doc;
        } finally {
            this.logger.exiting("RemoteResourceSourceProxyMock", "getResourceData");
        }
    }
}
