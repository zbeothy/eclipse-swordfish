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
package org.eclipse.swordfish.core.components.locatorproxy.impl;

import java.io.PrintWriter;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorLocation;

/**
 * The Class LocatorAddressSerializer.
 */
public class LocatorAddressSerializer implements ExtensionSerializer {

    /**
     * Instantiates a new locator address serializer.
     */
    public LocatorAddressSerializer() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensionSerializer#marshall(java.lang.Class,
     *      javax.xml.namespace.QName, javax.wsdl.extensions.ExtensibilityElement,
     *      java.io.PrintWriter, javax.wsdl.Definition, javax.wsdl.extensions.ExtensionRegistry)
     */
    public void marshall(final Class parent, final QName elemType, final ExtensibilityElement toMarschall, final PrintWriter pw,
            final Definition def, final ExtensionRegistry er) throws WSDLException {
        String str = "<spdx:locator xmlns:spdx=\"" + elemType.getNamespaceURI() + "\">";
        List ls = ((LocatorLocation) toMarschall).getLocationList();
        for (int i = 0; i < ls.size(); i++) {
            str = str + "<spdx:location>" + (String) ls.get(i) + "<spdx:location>";
        }
        str = str + "</spdx:locator>";
        pw.print(str);
    }

}
