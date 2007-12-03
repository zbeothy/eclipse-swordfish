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
package org.eclipse.swordfish.policy.exploration;

import java.util.ArrayList;
import org.apache.ws.policy.All;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.util.AbstractAssertionProcessor;
import org.eclipse.swordfish.policy.util.AssertionProcessor;
import org.eclipse.swordfish.policy.util.PrimitiveAssertionSelector;
import org.eclipse.swordfish.policy.util.TermIterator;

/**
 * The Class IdValidationAssertionProcessor.
 */
public class IdValidationAssertionProcessor extends AbstractAssertionProcessor implements AssertionProcessor {

    /** The open ids. */
    private ArrayList openIds = new ArrayList();

    /** The count. */
    private int count = 0;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#getCost(org.apache.ws.policy.All,
     *      org.apache.ws.policy.PrimitiveAssertion)
     */
    public int getCost(final All alternative, final PrimitiveAssertion assertion) {
        return 0;
    }

    /**
     * Gets the count.
     * 
     * @return Returns the count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Gets the open ids.
     * 
     * @return Returns the openIds.
     */
    public ArrayList getOpenIds() {
        return this.openIds;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#postProcessAlternative(org.apache.ws.policy.All,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean postProcessAlternative(final All alternative, final Object preProcessResultConsumer,
            final Object preProcessResultProvider) {
        TermIterator it = new TermIterator(alternative, new PrimitiveAssertionSelector(PolicyConstants.SOP_ID_ASSERTION));
        while (it.hasNext()) {
            this.openIds.remove(it.next());
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#preProcessAlternative(org.apache.ws.policy.All)
     */
    @Override
    public Object preProcessAlternative(final All alternative) {
        TermIterator it = new TermIterator(alternative, new PrimitiveAssertionSelector(PolicyConstants.SOP_ID_ASSERTION));
        while (it.hasNext()) {
            this.openIds.add(it.next());
            this.count++;
        }
        return null;
    }

}
