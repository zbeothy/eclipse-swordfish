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
package org.eclipse.swordfish.core.interceptor.authentication.impl;

import java.lang.reflect.Method;
import java.util.Date;
import javax.security.auth.Subject;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;

/**
 * The Class AssertionCacheEntryRefreshPolicy.
 */
public class AssertionCacheEntryRefreshPolicy implements EntryRefreshPolicy {

    /**
     * 
     */
    private static final long serialVersionUID = 9135376248820032670L;

    /**
     * {@inheritDoc}
     * 
     * @see com.opensymphony.oscache.base.EntryRefreshPolicy#needsRefresh(com.opensymphony.oscache.base.CacheEntry)
     */
    public boolean needsRefresh(final CacheEntry entry) {
        Subject subject = (Subject) entry.getContent();
        return this.isExpired(subject);
    }

    /**
     * Checks if is expired.
     * 
     * @param subject
     *        the subject
     * 
     * @return true, if is expired
     */
    protected boolean isExpired(final Subject subject) {
        Object[] credentials = subject.getPublicCredentials().toArray();

        for (int objSize = 0; objSize < credentials.length; objSize++) {
            Method recMethod = null;
            try {
                recMethod = credentials[objSize].getClass().getMethod("getExpiryDate", (Class[]) null);
            } catch (NoSuchMethodException nsme) {
                // if we get this exception move on with other
                // subjects in the set, dont throw an error here.
                continue;
            }
            if (recMethod != null) {
                Object recObj;
                try {
                    recObj = recMethod.invoke(credentials[objSize], (Object[]) null);
                } catch (Exception e) {
                    continue;
                }
                if (recObj instanceof Date) return ((Date) recObj).before(new Date());
            }
        }
        return false;
    }

}
