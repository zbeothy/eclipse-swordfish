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
package org.eclipse.swordfish.core.components.srproxy.impl;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionDeserializer;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

/**
 * The Class PartnerLinkTypeDeserializer.
 */
public class PartnerLinkTypeDeserializer implements ExtensionDeserializer {

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensionDeserializer#unmarshall(java.lang.Class,
     *      javax.xml.namespace.QName, org.w3c.dom.Element, javax.wsdl.Definition,
     *      javax.wsdl.extensions.ExtensionRegistry)
     */
    public ExtensibilityElement unmarshall(final Class clzz, final QName name, final Element elem, final Definition def,
            final ExtensionRegistry extReg) throws WSDLException {
        PartnerLinkTypeImpl partnerLink = new PartnerLinkTypeImpl();
        partnerLink.setElement(elem);
        partnerLink.setElementType(name);
        return partnerLink;
    }

}
