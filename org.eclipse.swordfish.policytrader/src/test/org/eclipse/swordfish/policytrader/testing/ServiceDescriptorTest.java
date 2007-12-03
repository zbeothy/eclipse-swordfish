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

import java.io.InputStream;
import java.util.List;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;

public class ServiceDescriptorTest extends TestBase {

    public ServiceDescriptorTest(final String name) {
        super(name);
    }

    public void testServiceDescriptorMultiplePortTypes() throws Exception {
        final InputStream is = this.getClass().getResourceAsStream("CarRentalPT.sdx");
        final ServiceDescriptor sd = this.policyFactory.createServiceDescriptor(is);
        final List l = sd.getOperationNames();
        assertEquals(l.size(), 8);
        assertEquals("rentCar", l.get(0));
        assertEquals("returnCar", l.get(1));
        assertEquals("noticeBoardMessage", l.get(2));
        assertEquals("addCar", l.get(3));
        assertEquals("deleteCar", l.get(4));
        assertEquals("listAllCars", l.get(5));
        assertEquals("getCarInfo", l.get(6));
        assertEquals("oneWayTest", l.get(7));
    }

    public void testServiceDescriptorSinglePortType() throws Exception {
        final InputStream is = this.getClass().getResourceAsStream("Library.sdx");
        final ServiceDescriptor sd = this.policyFactory.createServiceDescriptor(is);
        final List l = sd.getOperationNames();
        assertEquals(l.size(), 4);
        assertEquals("seekBook", l.get(0));
        assertEquals("newBooks", l.get(1));
        assertEquals("createLending", l.get(2));
        assertEquals("seekBookInBasement", l.get(3));
    }

}
