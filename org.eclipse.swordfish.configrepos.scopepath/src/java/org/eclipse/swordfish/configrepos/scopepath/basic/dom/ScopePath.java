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
package org.eclipse.swordfish.configrepos.scopepath.basic.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Java content class for ScopePath element declaration.
 * <p>
 * The following schema fragment specifies the expected content contained within this java content
 * object. (defined at
 * file:/D:/eclipse31/servicemix/sbb-configrepos-scopepath-dom/src/xsd/BasicScopePath.xsd line 29)
 * <p>
 * 
 * <pre>
 * &lt;element name=&quot;ScopePath&quot; type=&quot;{http://types.sopware.org/configuration/BasicScopePath/1.0}PathType&quot;/&gt;
 * </pre>
 */
public interface ScopePath extends org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType {

    /** The DEFAUL t_ NAMESPAC e_ PREFIX. */
    String DEFAULT_NAMESPACE_PREFIX = "bsp";

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
