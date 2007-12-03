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
package org.eclipse.swordfish.policytrader.testing;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.callback.PolicyResolver;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;

/**
 */
public class PolicyResolverImpl implements PolicyResolver {

    /**
     * Comment for <code>policyFactory</code>
     */
    private final PolicyFactory policyFactory;

    private Listener listener;

    private String[] operationNames;

    /**
     * @param policyFactory
     */
    public PolicyResolverImpl(final PolicyFactory policyFactory) {
        super();
        this.policyFactory = policyFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveOperationPolicy(org.eclipse.swordfish.policytrader.OperationPolicyIdentity)
     */
    public OperationPolicy resolveOperationPolicy(final OperationPolicyIdentity identity) throws BackendException {
        final InputStream is;
        if (this.nonzero(identity.getLocation())) {
            is = this.getClass().getResourceAsStream(identity.getLocation());
        } else {
            try {
                final URL u = new URL(identity.getKeyName());
                is = u.openStream();
            } catch (MalformedURLException e) {
                throw new BackendException(e);
            } catch (IOException e) {
                throw new BackendException(e);
            }
        }
        final OperationPolicy res;
        try {
            res = this.policyFactory.createOperationPolicy(is);
        } catch (UnreadableSourceException e) {
            throw new BackendException(e);
        } catch (CorruptedSourceException e) {
            throw new BackendException(e);
        }
        if (this.listener != null) {
            this.listener.operationPolicyResolved(identity, res);
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveParticipantPolicy(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public ParticipantPolicy resolveParticipantPolicy(final ParticipantPolicyIdentity identity) throws BackendException {
        final InputStream is;
        if (this.nonzero(identity.getLocation())) {
            is = this.getClass().getResourceAsStream(identity.getLocation());
        } else {
            try {
                final URL u = new URL(identity.getKeyName());
                is = u.openStream();
            } catch (MalformedURLException e) {
                throw new BackendException(e);
            } catch (IOException e) {
                throw new BackendException(e);
            }
        }
        final ParticipantPolicy res;
        try {
            res = this.policyFactory.createParticipantPolicy(is);
        } catch (UnreadableSourceException e) {
            throw new BackendException(e);
        } catch (CorruptedSourceException e) {
            throw new BackendException(e);
        }
        if (this.listener != null) {
            this.listener.participantPolicyResolved(identity, res);
        }
        return res;
    }

    public ServiceDescriptor resolveServiceDescriptor(final ServiceDescriptionIdentity identity) throws BackendException {
        if (null == this.operationNames) return null;
        final List l = new LinkedList();
        for (int i = 0; i < this.operationNames.length; i++) {
            l.add(this.operationNames[i]);
        }
        return new ServiceDescriptor() {

            public List getOperationNames() {
                return l;
            }
        };
    }

    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    public void setOperationNames(final String[] operationNames) {
        this.operationNames = operationNames;
    }

    private boolean nonzero(final String s) {
        return ((null != s) && (s.length() > 0));
    }

    public static interface Listener {

        void operationPolicyResolved(final OperationPolicyIdentity id, final OperationPolicy result);

        void participantPolicyResolved(final ParticipantPolicyIdentity id, final ParticipantPolicy result);
    }
}
