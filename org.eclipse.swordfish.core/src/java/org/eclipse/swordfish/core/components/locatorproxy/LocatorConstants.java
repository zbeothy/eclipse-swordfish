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
package org.eclipse.swordfish.core.components.locatorproxy;

import javax.xml.namespace.QName;

/**
 * The Interface LocatorConstants.
 */
public interface LocatorConstants {

    /** The SPD x_ SO p_ LOCATO r_ NS. */
    String SPDX_SOP_LOCATOR_NS = "http://www.sopware.org/definitions/ServiceProviderDescriptionFormat/2.0";

    /** The SPD x_ SO p_ LOCATOR. */
    QName SPDX_SOP_LOCATOR = new QName(SPDX_SOP_LOCATOR_NS, "locator");
}
