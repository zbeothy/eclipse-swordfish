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
package org.eclipse.swordfish.core.components.internalproxy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceController;
import org.eclipse.swordfish.core.components.internalproxy.ServiceOperationProxy;
import org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.w3c.dom.Document;

/**
 * The Class RequestResponseServiceOperationProxyBean.
 * 
 */
public class RequestResponseServiceOperationProxyBean implements ServiceOperationProxy {

    /** Logger used by this class. */
    private static final Log LOG = SBBLogFactory.getLog(RequestResponseServiceOperationProxyBean.class);

    /** The delivery channel send to be used by this controller. */
    private DeliveryChannelSender deliveryChannelSender;

    /**
     * Instantiates a new request response service operation proxy bean.
     */
    public RequestResponseServiceOperationProxyBean() {
        super();

    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ServiceOperationProxy#destroy()
     */
    public void destroy() throws Exception {
        this.deliveryChannelSender = null;
    }

    /**
     * Gets the delivery channel sender.
     * 
     * @return Returns the deliveryChannelSender.
     */
    public DeliveryChannelSender getDeliveryChannelSender() {
        return this.deliveryChannelSender;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aMessageExchangeBag
     *        the a message exchange bag
     * @param aServiceOperation
     *        the a service operation
     * @param aController
     *        the a controller
     * @param aPayload
     *        the a payload
     * 
     * @return the object
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ServiceOperationProxy#invokeServiceOperation(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
     *      javax.xml.namespace.QName, java.lang.String)
     */
    public Object invokeServiceOperation(final Collection aMessageExchangeBag, final QName aServiceOperation,
            final ResilienceController aController, final Document aPayload) throws Exception {

        MessageExchange mex = null;
        List exceptionList = new ArrayList();

        while (null != (mex = aController.createInOutExchange(aMessageExchangeBag, aServiceOperation))) {
            boolean errorState = false;
            try {
                // prepare message
                // FIXME we use a DOMSource explicitly here
                NormalizedMessage nm = mex.createMessage();
                nm.setContent(new DOMSource(aPayload));
                mex.setMessage(nm, "in");

                // allow preprocessing to controller
                aController.preprocessExchange(mex);
                aController.preprocessMessage(nm);

                long timeout = aController.getCallTimeout();
                if ((null != LOG) && LOG.isDebugEnabled()) {
                    LOG.debug("Service invocation ongoing" + (timeout > 0 ? " with a timeout of " + timeout + " seconds." : "."));
                }
                if (timeout <= 1) {
                    this.deliveryChannelSender.sendSync(mex);
                } else {
                    this.deliveryChannelSender.sendSync(mex, timeout);
                }
                if ((null != LOG) && LOG.isDebugEnabled()) {
                    LOG.debug("Service invocation done.");
                }

                if (mex.getStatus().equals(ExchangeStatus.ERROR)) {
                    if ((null != LOG) && LOG.isDebugEnabled()) {
                        LOG.debug("Service invocation exchange return an error.");
                    }
                    aController.handleError(mex);
                }

                nm = mex.getMessage("out");

                // allow postprocessing to controller
                Fault fault = null;
                if (null == nm) {
                    fault = mex.getFault();
                    if ((null != LOG) && LOG.isDebugEnabled()) {
                        LOG.debug("Service invocation signaling an fault ['{0}'].", fault);
                    }
                }
                aController.postprocessExchange(mex);
                return aController.postprocessMessage(nm, fault);
            } catch (Exception e) {
                e = aController.handleException(e);
                if (null != e) {
                    exceptionList.add(e);
                }
            } finally {
                if ((null != mex) && errorState) {
                    aController.finalizeExchange(mex);
                }
            }
        }
        return aController.handleFailure(exceptionList);
    }

    /**
     * Sets the delivery channel sender.
     * 
     * @param deliveryChannelSender
     *        The deliveryChannelSender to set.
     */
    public void setDeliveryChannelSender(final DeliveryChannelSender deliveryChannelSender) {
        this.deliveryChannelSender = deliveryChannelSender;
    }
}
