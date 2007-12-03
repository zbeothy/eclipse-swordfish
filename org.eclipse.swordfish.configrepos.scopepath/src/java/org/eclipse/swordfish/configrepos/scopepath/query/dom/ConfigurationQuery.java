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
 * Java content class for ConfigurationQuery element declaration.
 * <p>
 * The following schema fragment specifies the expected content contained within this java content
 * object. (defined at
 * file:/D:/eclipse31/servicemix/sbb-configrepos-scopepath-dom/src/xsd/ConfigurationQuery.xsd line
 * 23)
 * <p>
 * 
 * <pre>
 * &lt;element name=&quot;ConfigurationQuery&quot; type=&quot;{http://types.sopware.org/configuration/ConfigurationQuery/1.0}ConfigurationQueryType&quot;/&gt;
 * </pre>
 */
public interface ConfigurationQuery extends org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQueryType {

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
