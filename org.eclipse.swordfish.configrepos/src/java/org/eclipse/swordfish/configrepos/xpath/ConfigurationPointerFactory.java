/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.xpath;

import java.util.Locale;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.w3c.dom.Node;

/**
 * A factory for creating ConfigurationPointer objects.
 */
public class ConfigurationPointerFactory implements NodePointerFactory {

    /** The Constant CONFIGURATION_POINTER_FACTORY_ORDER. */
    public static final int CONFIGURATION_POINTER_FACTORY_ORDER = 100;

    /**
     * createNodePointer.
     * 
     * @param parent
     * @param name
     * @param bean
     * 
     * @return NodePointer
     */
    public NodePointer createNodePointer(final NodePointer parent, final QName name, final Object bean) {
        if (bean instanceof Node) return new DOMNodePointer(parent, ((XMLConfiguration) bean).getDocument());
        return null;
    }

    /**
     * createNodePointer.
     * 
     * @param name
     * @param bean
     * @param locale
     * 
     * @return NodePointer
     */
    public NodePointer createNodePointer(final QName name, final Object bean, final Locale locale) {
        if (bean instanceof XMLConfiguration) return new DOMNodePointer(((XMLConfiguration) bean).getDocument(), locale);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#getOrder()
     */
    public int getOrder() {
        return CONFIGURATION_POINTER_FACTORY_ORDER;
    }

}
