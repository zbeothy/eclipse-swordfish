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
package org.eclipse.swordfish.core.components.helpers;

import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.components.helpers.impl.HeaderSupportBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A factory for creating HeaderSupportProxy objects.
 */
public class HeaderSupportProxyFactory implements ExtensionFactory, ApplicationContextAware {

    // -------------------------------------------------------------- Constants

    /**
     * (R)evision (C)ontrol (S)ystem (Id)entifier
     */
    public static final String RCS_ID = "@(#) $Id: HeaderSupportProxyFactory.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    /** The Constant INTERNAL_IMPL. */
    private static final String INTERNAL_IMPL = "org.eclipse.swordfish.core.components.helpers.HeaderSupport";

    // ----------------------------------------------------- Instance Variables

    /** The instance. */
    private Object instance = null;

    /** The context. */
    private ApplicationContext context;

    // ------------------------------------------------------------- Properties

    /**
     * {@inheritDoc}
     */
    public Object getInstance(final String diversifier) {
        if (null == this.instance) {
            this.instance = this.createInstance();
        }
        return this.instance;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Creates a new HeaderSupportProxy object.
     * 
     * @return the object
     */
    private Object createInstance() {
        HeaderSupportBean wrapped = (HeaderSupportBean) this.context.getBean(INTERNAL_IMPL);
        HeaderSupportProxy ret = new HeaderSupportProxy();
        ret.setWrapped(wrapped);
        return ret;
    }

}
