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
package org.eclipse.swordfish.core.components.instancemanager.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.instancemanager.AssociationException;
import org.eclipse.swordfish.core.components.instancemanager.InstanceManager;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.InternalSBB;

/**
 * implementation of an instance manager.
 */
public class InstanceManagerBean extends Observable implements InstanceManager {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(InstanceManagerBean.class);

    /** used to check if the key for an association is unique or not. */
    private Set uniqueTest;

    /** content of the associations. */
    private ConcurrentHashMap associations;

    /**
     * constructor for this bean.
     */
    public InstanceManagerBean() {
        this.uniqueTest = new HashSet();
        this.associations = new ConcurrentHashMap();
    }

    /**
     * Associate.
     * 
     * @param sbb
     *        the sbb
     * @param identity
     *        the identity
     * 
     * @throws AssociationException
     * 
     * @see org.eclipse.swordfish.core.components.instancemanager.InstanceManager#associate(org.eclipse.swordfish.papi.InternalSBB,
     *      org.eclipse.swordfish.papi.InternalParticipantIdentity)
     */
    public void associate(final InternalSBB sbb, final InternalParticipantIdentity identity) throws AssociationException {
        this.associate(sbb, this.buildKey(identity));
    }

    /**
     * returns a string representation of the association map used for management purposes when this
     * bean is registered in an MBean server.
     * 
     * @return -- a String representing the contents of the association map
     */
    public String getAssociations() {
        String ret = new String();
        Iterator it = this.associations.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ret += "Key: " + key + ", InternalSBB instance: " + this.associations.get(key).toString() + "\n";
        }
        return ret;
    }

    /**
     * Query.
     * 
     * @param ident
     *        the ident
     * 
     * @return the InternalSBB extension
     * 
     * @see org.eclipse.swordfish.core.components.instancemanager.InstanceManager#query(org.eclipse.swordfish.papi.InternalParticipantIdentity)
     */
    public SBBExtension query(final InternalParticipantIdentity ident) {
        MultiKey key = this.buildKey(ident);
        return (SBBExtension) this.associations.get(key);
    }

    // TODO add a retrival method for ALL InternalSBB extensions for the shutdown stuff

    /**
     * Removes the all associations.
     * 
     * @param sbb
     *        the sbb
     * 
     * @see org.eclipse.swordfish.core.components.instancemanager.InstanceManager#removeAllAssociations(org.eclipse.swordfish.papi.InternalSBB)
     */
    public void removeAllAssociations(final InternalSBB sbb) {
        Iterator iter = this.uniqueTest.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (this.associations.get(key) == sbb) {
                this.associations.remove(key);
                this.uniqueTest.remove(key);
            }
        }
    }

    /**
     * Removes the association.
     * 
     * @param identity
     *        the identity
     * 
     * @see org.eclipse.swordfish.core.components.instancemanager.InstanceManager#removeAssociation(org.eclipse.swordfish.papi.InternalSBB,
     *      org.eclipse.swordfish.papi.InternalParticipantIdentity)
     */
    public void removeAssociation(final InternalParticipantIdentity identity) {
        this.removeAssociation(this.buildKey(identity));
        LOG.debug("released association for " + identity);

    }

    /**
     * internal method to do the associations.
     * 
     * @param sbb
     *        the sbb to associate
     * @param key
     *        an string used as the key
     * 
     * @throws AssociationException
     *         if the association cannot be made as the key is already associated with a different
     *         InternalSBB
     */
    private synchronized void associate(final InternalSBB sbb, final MultiKey key) throws AssociationException {
        // this is going to be a bad case
        if (this.uniqueTest.contains(key) && (this.associations.get(key) != sbb))
            // FIXME add the right resource ref
            throw new AssociationException("123", "trying to reassociate a different sbb instance with a same key " + key);
        else {
            this.uniqueTest.add(key);
            this.associations.put(key, sbb);
        }
    }

    /**
     * returns a String based on the participant identity.
     * 
     * @param ident
     *        participant identity
     * 
     * @return -- an String that is usable as a key
     */
    private MultiKey buildKey(final InternalParticipantIdentity ident) {
        MultiKey multiKey = new MultiKey("identity", new UnifiedParticipantIdentity(ident));
        return multiKey;
    }

    /**
     * removes the association of the given key.
     * 
     * @param key
     *        the key to remove the association from
     */
    private void removeAssociation(final MultiKey key) {
        this.uniqueTest.remove(key);
        this.associations.remove(key);
        this.setChanged();
        this.notifyObservers(new Integer(this.associations.size()));
    }

}
