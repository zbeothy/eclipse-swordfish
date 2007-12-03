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

import java.io.PrintWriter;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkType;

/**
 * The Class PartnerLinkTypeSerializer.
 */
public class PartnerLinkTypeSerializer implements ExtensionSerializer {

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensionSerializer#marshall(java.lang.Class,
     *      javax.xml.namespace.QName, javax.wsdl.extensions.ExtensibilityElement,
     *      java.io.PrintWriter, javax.wsdl.Definition, javax.wsdl.extensions.ExtensionRegistry)
     */
    public void marshall(final Class clzz, final QName qname, final ExtensibilityElement extElem, final PrintWriter pw,
            final Definition def, final ExtensionRegistry arg5extReg) throws WSDLException {
        PartnerLinkType toMarshall = ((PartnerLinkType) extElem);

        String str = "<plnk:partnerLinkType xmlns:plnk=\"" + qname.getNamespaceURI() + "\" name=\"" + toMarshall.getName() + "\">";
        PartnerLinkRole[] array = toMarshall.getPartnerLinkRoles();
        for (int i = 0; i < array.length; i++) {
            str = str + array[i].toString();
        }
        str = str + "</plnk:partnerLinkType>";
        pw.print(str);
    }

}
