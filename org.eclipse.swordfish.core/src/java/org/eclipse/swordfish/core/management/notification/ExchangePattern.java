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
package org.eclipse.swordfish.core.management.notification;

/**
 * Communication style for message exchange as seen by service provider (according to WSDL 2.0
 * definition).
 * 
 */
public final class ExchangePattern extends AbstractEnum {

    /** style according to WSDL 2.0 definition. */
    public static final ExchangePattern IN_ONLY = new ExchangePattern("IN_ONLY");

    /** style according to WSDL 2.0 definition. */
    public static final ExchangePattern IN_OUT = new ExchangePattern("IN_OUT");

    /** style according to WSDL 2.0 definition. */
    public static final ExchangePattern OUT_ONLY = new ExchangePattern("OUT_ONLY");

    /** SOPware specific style for async IN_OUT exchanges. */
    public static final ExchangePattern REQUEST_CALLBACK = new ExchangePattern("REQUEST_CALLBACK");

    /**
     * Gets the instance by name.
     * 
     * @param name
     *        the name
     * 
     * @return the instance by name
     */
    public static ExchangePattern getInstanceByName(final String name) {
        return (ExchangePattern) getInstanceByNameInternal(ExchangePattern.class, name);
    }

    /**
     * The Constructor.
     * 
     * @param name
     *        to use for communication style
     */
    private ExchangePattern(final String name) {
        super(name);
    }

}
