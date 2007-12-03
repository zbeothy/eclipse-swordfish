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
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.xml.sax.SAXException;

public class InstantiatedAgreedPolicyTest extends TestCase {

    private static final String VALID_SINCE = "Thu Dec 14 15:35:05 CET 2006";

    private static final String VALID_THROUGH = "Thu Dec 14 16:35:05 CET 2006";

    public InstantiatedAgreedPolicyTest(final String name) {
        super(name);
    }

    public void testExpiryDates() throws SAXException, IOException, ParserConfigurationException {
        InputStream is =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/policytrader/testing/LibraryProvider.agreedpolicy");
        AgreedPolicy result = AgreedPolicyFactory.getInstance().createFrom(is);
        Date validSince = result.validSince();
        Date validThrough = result.validThrough();
        assertEquals(VALID_SINCE, validSince.toString());
        assertEquals(VALID_THROUGH, validThrough.toString());
    }

}
