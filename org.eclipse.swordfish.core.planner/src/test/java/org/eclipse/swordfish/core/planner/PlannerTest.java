/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Volodymyr Zhabiuk - initial implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.planner;

import junit.framework.TestCase;

import org.eclipse.swordfish.api.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PlannerTest extends TestCase {

	private Logger logger = LoggerFactory.getLogger(PlannerTest.class);

	public void testInterceptor() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("test.xml");
        Interceptor ic = (Interceptor) ctx.getBean("testInterceptor1");
     	assertNotNull(ctx);
    	assertNotNull(ic);
    }

}

