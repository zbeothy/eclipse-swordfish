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
package org.eclipse.swordfish.core.components.srproxy;

import javax.wsdl.Definition;
import org.w3c.dom.Element;

/**
 * The Interface ServiceInfo.
 */
public interface ServiceInfo {

    /**
     * Gets the service description.
     * 
     * @return Definition wsdl definition for service description
     */
    Definition getServiceDescription();

    /**
     * Gets the service description as element.
     * 
     * @return Element wsdl definition for consumer description as DOM element
     */
    Element getServiceDescriptionAsElement();

}
