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
package org.eclipse.swordfish.core.components.dynamicendpointhandler;

import javax.xml.namespace.QName;
import org.w3c.dom.DocumentFragment;

/**
 * The Interface DynamicInboundEndpoint.
 */
public interface DynamicInboundEndpoint {

    /**
     * As string.
     * 
     * @return the string
     */
    String asString();

    /**
     * Gets the address fragment.
     * 
     * @return the address fragment
     */
    DocumentFragment getAddressFragment();

    /**
     * Gets the id.
     * 
     * @return the id
     */
    String getId();

    /**
     * Gets the service Q name.
     * 
     * @return the service Q name
     */
    QName getServiceQName();
}
