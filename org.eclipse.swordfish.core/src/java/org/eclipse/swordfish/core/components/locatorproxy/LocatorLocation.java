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

import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * The Interface LocatorLocation.
 */
public interface LocatorLocation extends ExtensibilityElement {

    /**
     * Gets the location list.
     * 
     * @return the location list
     */
    List getLocationList();
}
