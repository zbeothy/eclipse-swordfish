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
package org.eclipse.swordfish.policy.selector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * The Class RegexNameSelector.
 */
public class RegexNameSelector extends AbstractSelector {

    /** The pattern. */
    private Pattern pattern = null;

    /**
     * Instantiates a new regex name selector.
     * 
     * @param pattern
     *        the pattern
     */
    public RegexNameSelector(final String pattern) {
        super(pattern);
        this.pattern = Pattern.compile(pattern);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.selector.Selector#isValid(java.lang.Object)
     */
    public boolean isValid(final Object obj) {
        if (!(obj instanceof PrimitiveAssertion))
            return false;
        else {
            String localName = ((PrimitiveAssertion) obj).getName().getLocalPart();
            Matcher m = this.pattern.matcher(localName);
            return m.matches();
        }
    }

}
