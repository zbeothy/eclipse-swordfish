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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.Iterator;
import javax.wsdl.Definition;
import junit.framework.TestCase;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkType;

/**
 * The Class PartnerLinkTypeWSDLTest.
 */
public class PartnerLinkTypeWSDLTest extends TestCase {

    /**
     * Instantiates a new partner link type WSDL test.
     */
    public PartnerLinkTypeWSDLTest() {
        super();
    }

    /**
     * Test read success.
     * 
     * @throws Exception
     */
    public void testReadSuccess() throws Exception {
        Definition def;

        DefinitionHelper helper = DefinitionHelper.getInstance();
        def = helper.fileTodefinition("src/test/org/eclipse/swordfish/core/components/srproxy/impl/baseDir/pl.sdx");
        Iterator iter = def.getExtensibilityElements().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof PartnerLinkType) {
                PartnerLinkTypeImpl pl = (PartnerLinkTypeImpl) o;
                System.out.println(pl.toString());
                assertNotNull(pl.getPartnerLinkRole("service"));
                assertNotNull(pl.getPartnerLinkRole("callback"));
            }
        }

    }

}
