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
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.xml.sax.SAXException;

public class AgreedPolicyTest extends TestCase {

    public static final String CONFIGURATION_AGREEDPOLICY =
            "/org/eclipse/swordfish/policytrader/testing/configuration.agreedpolicy";

    public void testInstantiationFromDOM() throws ParserConfigurationException, SAXException, IOException {
        AgreedPolicyFactory factory = AgreedPolicyFactory.getInstance();
        final InputStream is = this.getClass().getResourceAsStream(CONFIGURATION_AGREEDPOLICY);
        AgreedPolicy policy = factory.createFrom(is);
        assertNotNull(policy);
    }

}
