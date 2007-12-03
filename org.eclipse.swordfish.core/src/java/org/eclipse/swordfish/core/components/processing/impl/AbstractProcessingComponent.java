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
package org.eclipse.swordfish.core.components.processing.impl;

import java.util.Collection;
import java.util.Iterator;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.ProcessingComponent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * implementation of an AbstractProcessingComponent.
 */
public abstract class AbstractProcessingComponent implements BeanFactoryAware, ProcessingComponent {

    /** Holder of the bean factory. */
    private BeanFactory beanFactory;

    /**
     * Gets the bean factory.
     * 
     * @return Returns the componentManager.
     */
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    /**
     * Idicates which actions this component is planing to do with the content of the message.
     * 
     * @return the ContentAction for this component, this defaults to READWRITE which is the most
     *         "disturbing" version
     */
    public ContentAction getContentAction() {
        return ContentAction.READWRITE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#getSupportedSources()
     */
    public Class[] getSupportedSources() {
        return new Class[] {DOMSource.class};
    }

    /**
     * Sets the bean factory.
     * 
     * @param factory
     *        the factory
     * 
     * @throws BeansException
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory
     *      (org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory factory) throws BeansException {
        this.beanFactory = factory;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#supportSource(java.lang.Class)
     */
    public boolean supportSource(final Class src) {
        Class[] clz = this.getSupportedSources();
        for (int i = 0; i < clz.length; i++) {
            if (src.getName().equals(clz[i].getName())) return true;
        }
        return false;
    }

    /**
     * Gets the attribute.
     * 
     * @param assertion
     *        the assertion
     * @param attributeName
     *        the attribute name
     * @param defaultValue
     *        the default value
     * 
     * @return the attribute
     */
    protected String getAttribute(final PrimitiveAssertion assertion, final String attributeName, final String defaultValue) {
        String value = assertion.getAttribute(new QName(attributeName));
        return null != value ? value : defaultValue;
    }

    /**
     * return the current exchange regarding which execution path we are taking, and takes faults
     * into account too.
     * 
     * @param exchange
     *        the exchange containing the message
     * @param scope
     *        current scope
     * 
     * @return a normalized message
     */
    protected NormalizedMessage getCurrentNormalizedMessage(final MessageExchange exchange, final Scope scope) {
        if (exchange != null) {
            if (exchange.getFault() != null) return exchange.getFault();
            if (exchange instanceof InOut) {
                if (Scope.REQUEST.equals(scope))
                    return exchange.getMessage("in");
                else
                    return exchange.getMessage("out");
            }
            if (exchange instanceof InOnly) return exchange.getMessage("in");
            return null;
        } else
            return null;

    }

    /**
     * Checks if is fault.
     * 
     * @param exchange
     *        the exchange to examine
     * 
     * @return true if the exchange indicates a fault, false otherwise
     */
    protected boolean isFault(final MessageExchange exchange) {
        return (exchange.getFault() != null);
    }

    /**
     * Narrow down.
     * 
     * @param assertions
     *        the assertions
     * @param role
     *        the role
     * @param scope
     *        the scope
     * 
     * @return the primitive assertion
     */
    protected PrimitiveAssertion narrowDown(final Collection assertions, final Role role, final Scope scope) {
        Iterator it = assertions.iterator();
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            String message = this.getAttribute(assertion, "message", null);
            String location = this.getAttribute(assertion, "location", null);
            if ((null == message) || "all".equals(message) || scope.equals(Scope.fromString(message))) {
                if ((null == location) || this.isEquivalent(location, role)) return assertion;
            }
        }
        return null;
    }

    /**
     * Checks if is equivalent.
     * 
     * @param location
     *        the location
     * @param role
     *        the role
     * 
     * @return true, if is equivalent
     */
    private boolean isEquivalent(final String location, final Role role) {
        return ("consumer".equals(location) && Role.SENDER.equals(role))
                || ("provider".equals(location) && Role.RECEIVER.equals(role));
    }

}
