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
/*
 * (C) DPWN
 */
package org.eclipse.swordfish.core.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.planner.impl.PlannerBean;
import org.eclipse.swordfish.core.components.processing.impl.ProcessingUnitImpl;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * The Class ProcessorTest.
 */
public class ProcessorTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() {
        try {
            super.setUp();
            this.ctx =
                    new FileSystemXmlApplicationContext(
                            new String[] {"src/test/org/eclipse/swordfish/core/processor/ProcessorTestConfig.xml"});

            this.ctx.getBean("org.eclipse.swordfish.core.components.processing.PolicyRouter");

            List list = new ArrayList();
            list.add(new ProcessingUnitImpl("", "") {

                @Override
                public String getAssertionName() {
                    return "Dummy";
                }

                @Override
                public String getComponentName() {
                    return "test.processor.DummyComponent";
                }
            });
            HashMap unitMap = new HashMap();
            unitMap.put("senderrequest", list);

            PlannerBean planBean = (PlannerBean) this.ctx.getBean("org.eclipse.swordfish.core.components.planner.Planner");
            planBean.setUnitMap(unitMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Test request.
     */
    public void testRequest() {
        /*
         * try { MessageExchangeFactory mef = new MessageExchangeFactoryImpl(); MessageExchange me =
         * mef.createInOutExchange(); me.setOperation(new QName("dummyOperation")); AgreedPolicy
         * agPolicy = new AgreedPolicyImpl(new FileInputStream(
         * "./src/test/test/processor/dummyAgreedPolicy.xml")); policyRouter.handleRequest(me,
         * Role.SENDER, agPolicy); } catch (Exception e) { e.printStackTrace(); }
         */}

}
