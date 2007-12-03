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
package org.eclipse.swordfish.core.components.srproxy;

/**
 * This exception is thrown when an error occured while talking to the remote Service Registry
 * Adapter.
 * 
 */
public class SrProxyCommunicationException extends SrProxyException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 367250399000085927L;

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        the resource key
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException(java.lang.String)
     */
    public SrProxyCommunicationException(final String resourceKey) {
        super(resourceKey);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        the resource key
     * @param exMsgParam1
     *        the ex msg param1
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException(java.lang.String,
     *      java.lang.String)
     */
    public SrProxyCommunicationException(final String resourceKey, final String exMsgParam1) {
        super(resourceKey, exMsgParam1);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        the resource key
     * @param exMsgParam1
     *        the ex msg param1
     * @param exMsgParam2
     *        the ex msg param2
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException( java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public SrProxyCommunicationException(final String resourceKey, final String exMsgParam1, final String exMsgParam2) {
        super(resourceKey, exMsgParam1, exMsgParam2);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        the resource key
     * @param exMsgParam1
     *        the ex msg param1
     * @param exMsgParam2
     *        the ex msg param2
     * @param exMsgParam3
     *        the ex msg param3
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException( java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SrProxyCommunicationException(final String resourceKey, final String exMsgParam1, final String exMsgParam2,
            final String exMsgParam3) {
        super(resourceKey, exMsgParam1, exMsgParam2, exMsgParam3);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        the resource key
     * @param exMsgParamArray
     *        the ex msg param array
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException(java.lang.String,
     *      java.lang.String[])
     */
    public SrProxyCommunicationException(final String resourceKey, final String[] exMsgParamArray) {
        super(resourceKey, exMsgParamArray);
    }

    /**
     * The Constructor.
     * 
     * @param t
     *        throwable
     */
    public SrProxyCommunicationException(final Throwable t) {
        super(t);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     * @param resourceKey
     *        the resource key
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException(java.lang.Throwable,
     *      java.lang.String)
     */
    public SrProxyCommunicationException(final Throwable cause, final String resourceKey) {
        super(cause, resourceKey);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     * @param resourceKey
     *        the resource key
     * @param exMsgParam1
     *        the ex msg param1
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException( java.lang.Throwable,
     *      java.lang.String, java.lang.String)
     */
    public SrProxyCommunicationException(final Throwable cause, final String resourceKey, final String exMsgParam1) {
        super(cause, resourceKey, exMsgParam1);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     * @param resourceKey
     *        the resource key
     * @param exMsgParam1
     *        the ex msg param1
     * @param exMsgParam2
     *        the ex msg param2
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException( java.lang.Throwable,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SrProxyCommunicationException(final Throwable cause, final String resourceKey, final String exMsgParam1,
            final String exMsgParam2) {
        super(cause, resourceKey, exMsgParam1, exMsgParam2);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     * @param resourceKey
     *        the resource key
     * @param exMsgParam1
     *        the ex msg param1
     * @param exMsgParam2
     *        the ex msg param2
     * @param exMsgParam3
     *        the ex msg param3
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException( java.lang.Throwable,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public SrProxyCommunicationException(final Throwable cause, final String resourceKey, final String exMsgParam1,
            final String exMsgParam2, final String exMsgParam3) {
        super(cause, resourceKey, exMsgParam1, exMsgParam2, exMsgParam3);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     * @param resourceKey
     *        the resource key
     * @param exMsgParamArray
     *        the ex msg param array
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxyException#SrProxyException( java.lang.Throwable,
     *      java.lang.String, java.lang.String[])
     */
    public SrProxyCommunicationException(final Throwable cause, final String resourceKey, final String[] exMsgParamArray) {
        super(cause, resourceKey, exMsgParamArray);
    }
}