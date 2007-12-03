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
package org.eclipse.swordfish.core.interceptor.security;

import java.util.Date;
import org.opensaml.SAMLAssertion;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;

/**
 * The Class AssertionRefreshPolicy.
 * 
 */
public final class AssertionRefreshPolicy implements EntryRefreshPolicy {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2779503348534282107L;

    /**
     * (non-Javadoc).
     * 
     * @param entry
     *        the entry
     * 
     * @return true, if needs refresh
     * 
     * @see com.opensymphony.oscache.base.EntryRefreshPolicy#needsRefresh(com.opensymphony.oscache.base.CacheEntry)
     */
    public boolean needsRefresh(final CacheEntry entry) {
        Date notBefore = null;
        Date notOnOrAfter = null;
        Date currentDate = new Date();

        SAMLAssertion samlAssertion = (SAMLAssertion) entry.getContent();

        notBefore = samlAssertion.getNotBefore();
        notOnOrAfter = samlAssertion.getNotOnOrAfter();

        boolean validDate = notBefore.before(currentDate) && notOnOrAfter.after(currentDate);

        return !validDate;

    }

}
