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
package org.eclipse.swordfish.core.management;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.swordfish.core.interceptor.correlation.CorrelationInterceptorTest;
import org.eclipse.swordfish.core.interceptor.monitor.MonitorInterceptorTest;
import org.eclipse.swordfish.core.management.components.impl.LoggerControllerTest;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManagerTest;
import org.eclipse.swordfish.core.management.instrumentation.ParticipantMonitorTest;
import org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorTest;
import org.eclipse.swordfish.core.management.messaging.impl.OperationMonitorTest;
import org.eclipse.swordfish.core.management.messaging.impl.OperationRegistrationTest;
import org.eclipse.swordfish.core.management.messaging.impl.ProcessingNotificationTest;
import org.eclipse.swordfish.core.management.messaging.impl.SbbMonitorTest;
import org.eclipse.swordfish.core.management.operations.NotificationHandlingTest;
import org.eclipse.swordfish.core.management.operations.OperationalMessageTest;
import org.eclipse.swordfish.core.management.operations.OperationsBeanTest;

/**
 * The Class ManagementTests.
 */
public class ManagementTests {

    /**
     * Suite.
     * 
     * @return the test
     */
    public static final Test suite() {
        TestSuite ts = new TestSuite();
        ts.addTest(new SbbMonitorTest("testSbbState"));
        ts.addTest(new SbbMonitorTest("testParticipantRegistration"));
        ts.addTest(new SbbMonitorTest("testNumParticipants"));
        ts.addTest(new MessagingMonitorTest("testConsumerBlockingInOut"));
        ts.addTest(new MessagingMonitorTest("testProviderBlockingInOut"));
        ts.addTest(new OperationRegistrationTest("testOperationAddRemove"));
        ts.addTest(new ProcessingNotificationTest("testDummyConsumerBlockingInOut"));
        ts.addTest(new ProcessingNotificationTest("testInstantiation"));
        ts.addTest(new ProcessingNotificationTest("testProcessing"));
        ts.addTest(new ProcessingNotificationTest("testTimedProcessing"));
        ts.addTest(new OperationMonitorTest("testConsumerBlockingInOut"));
        ts.addTest(new OperationMonitorTest("testProviderBlockingInOut"));
        ts.addTest(new InstrumentationManagerTest("testExternalRegistration"));
        ts.addTest(new InstrumentationManagerTest("testInternalRegistration"));
        ts.addTest(new InstrumentationManagerTest("testInternalInterface"));
        ts.addTest(new InstrumentationManagerTest("testExternalInterface"));
        ts.addTest(new OperationalMessageTest("testOperationalMessageRecord"));
        ts.addTest(new OperationalMessageTest("testWrapper"));
        ts.addTest(new OperationsBeanTest("testLogging"));
        ts.addTest(new NotificationHandlingTest("testNotificationFiltering"));
        ts.addTest(new ParticipantMonitorTest("testMonitorableInstantiation"));
        ts.addTest(new CorrelationInterceptorTest("testRequest"));
        ts.addTest(new MonitorInterceptorTest("testConsumerOk"));
        ts.addTest(new MonitorInterceptorTest("testConsumerLate"));
        ts.addTest(new MonitorInterceptorTest("testProviderOk"));
        ts.addTest(new MonitorInterceptorTest("testProviderLate"));
        ts.addTest(new LoggerControllerTest("testLoggerRegistration"));
        ts.addTest(new OperationsBeanTest("testLogging"));
        ts.addTest(new OperationsBeanTest("testMessageInstantiation"));
        return ts;
    }

}
