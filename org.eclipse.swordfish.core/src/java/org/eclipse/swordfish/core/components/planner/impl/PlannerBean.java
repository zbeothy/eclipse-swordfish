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
package org.eclipse.swordfish.core.components.planner.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.ws.policy.All;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.planner.Planner;
import org.eclipse.swordfish.core.components.processing.impl.ProcessingUnit;
import org.eclipse.swordfish.core.components.processing.impl.ProcessingUnitImpl;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.NameSelector;
import org.eclipse.swordfish.policy.util.TermIterator;

/**
 * TODO make this to be a component which can be reused, without being created each time.
 */
public class PlannerBean implements Planner {

    /** list to store the components for receiver response. */
    private HashMap unitMap = new HashMap();

    /** The context. */
    private ComponentContextAccess context = null;

    /**
     * destroy method.
     */
    public void destroy() {
        if (this.unitMap != null) {
            this.unitMap.clear();
            this.unitMap = null;
        }
        this.context = null;
    }

    /**
     * Gets the context.
     * 
     * @return ComponentContextAccess context
     */
    public ComponentContextAccess getContext() {
        return this.context;
    }

    /**
     * TODO need to feed this ,ethod with the list of processing steps.
     * 
     * @throws Exception
     *         if a loaded component does not implement proper interfaces
     */
    public void init() throws Exception {

        Properties props = new Properties();
        try {
            props.load(Planner.class.getResourceAsStream("plan.properties"));
        } catch (Exception exception) {
            throw exception;
        }

        int maxCount = props.size();
        String[] tempConfig = new String[] {"sender.request", "sender.response", "receiver.request", "receiver.response"};

        for (int x = 0; x < tempConfig.length; x++) {
            ArrayList compList = new ArrayList();
            for (int i = 1; i <= maxCount; i++) {
                String val = props.getProperty(tempConfig[x] + "." + i);
                if (val != null) {
                    String[] temp = val.split("=");
                    compList.add(new ProcessingUnitImpl(temp[0], temp[1]));

                } else {
                    this.unitMap.put(tempConfig[x], compList);
                    break;
                }
            }
        }

        props.clear();

    }

    /**
     * this implementation of the planner assumes the right ordering of interceptors to be defined
     * in the plan.properties resource.
     * 
     * @param operationPolicy
     *        the operation policy
     * @param role
     *        the role
     * @param scope
     *        the scope
     * 
     * @return a List of ProcessingUnit descriptors indicationg component names to lookup
     * 
     * @see org.eclipse.swordfish.processing.Planner#plan(java.util.List,
     *      org.eclipse.swordfish.framework.configuration.Configuration)
     */
    public List plan(final Policy operationPolicy, final Role role, final Scope scope) {

        List target = new ArrayList();
        List listToBeUsed = null;
        listToBeUsed = (List) this.unitMap.get(role.toString() + "." + scope.toString());
        if (listToBeUsed != null) {
            for (int i = 0; i < listToBeUsed.size(); i++) {
                ProcessingUnit unit = (ProcessingUnit) listToBeUsed.get(i);
                if (this.hasAssertion(operationPolicy, unit.getAssertionName())) {
                    target.add(unit);
                }
            }
        }
        return target;
    }

    /**
     * Sets the context.
     * 
     * @param context
     *        the context
     */
    public void setContext(final ComponentContextAccess context) {
        this.context = context;
    }

    /**
     * TODO remove this method when configuration is available.
     * 
     * @param unitMap
     *        map
     */
    public void setUnitMap(final HashMap unitMap) {
        this.unitMap = unitMap;
    }

    /**
     * Checks for assertion.
     * 
     * @param policy
     *        the policy
     * @param assertionName
     *        the assertion name
     * 
     * @return true, if successful
     */
    private boolean hasAssertion(final Policy policy, final String assertionName) {
        ExactlyOne eo = (ExactlyOne) policy.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        TermIterator iterPrimitives = new TermIterator(all, new NameSelector(assertionName));
        return iterPrimitives.hasNext();
    }
}
