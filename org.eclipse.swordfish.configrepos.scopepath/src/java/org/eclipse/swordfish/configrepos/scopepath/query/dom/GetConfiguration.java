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
package org.eclipse.swordfish.configrepos.scopepath.query.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Interface GetConfiguration.
 * 
 */
public interface GetConfiguration extends GetConfigurationType {

    /**
     * Marshal.
     * 
     * @param doc
     *        the doc
     */
    void marshal(Document doc);

    /**
     * Marshal.
     * 
     * @param doc
     *        the doc
     * @param root
     *        the root
     */
    void marshal(Document doc, Element root);
}
