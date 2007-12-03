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
package org.eclipse.swordfish.configrepos.spring;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.commons.jxpath.JXPathContextFactoryConfigurationError;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.eclipse.swordfish.configrepos.xpath.ConfigurationPointerFactory;

/**
 * .
 * 
 */
public class JXPathContextFactoryBean extends JXPathContextFactory {

    /** The namespace map. */
    private Map namespaceMap;

    /**
     * JXPathContextFactoryBean.
     */
    public JXPathContextFactoryBean() {
        JXPathContextReferenceImpl.addNodePointerFactory(new ConfigurationPointerFactory());
    }

    /**
     * Gets the namespace map.
     * 
     * @return the namespace map
     */
    public Map getNamespaceMap() {
        return this.namespaceMap;
    }

    /**
     * (non-Javadoc).
     * 
     * @param parentContext
     * @param contextBean
     * 
     * @return JXPathContext
     * 
     * @throws JXPathContextFactoryConfigurationError
     * 
     * @see org.eclipse.swordfish.configrepos.spring.JXPathContextFactory#newContext(java.lang.Object)
     */
    @Override
    public JXPathContext newContext(final JXPathContext parentContext, final Object contextBean)
            throws JXPathContextFactoryConfigurationError {
        JXPathContext context = JXPathContext.newContext(contextBean);
        this.applyNamespaces(context);
        return context;
    }

    /**
     * Sets the namespace map.
     * 
     * @param namespaceMap
     *        the new namespace map
     */
    public void setNamespaceMap(final Map namespaceMap) {
        this.namespaceMap = namespaceMap;
    }

    /**
     * Apply namespaces.
     * 
     * @param context
     *        the context
     */
    private void applyNamespaces(final JXPathContext context) {
        Iterator it = this.namespaceMap.keySet().iterator();
        while (it.hasNext()) {
            String prefix = (String) it.next();
            String namespaceUri = (String) this.namespaceMap.get(prefix);
            context.registerNamespace(prefix, namespaceUri);
        }

    }

}
