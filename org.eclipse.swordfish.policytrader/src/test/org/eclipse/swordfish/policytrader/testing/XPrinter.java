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

import org.eclipse.swordfish.policytrader.testing.helpers.XPrinterBase;
import org.w3c.dom.Node;

/**
 * Collection of methods for debug output
 */
public final class XPrinter extends XPrinterBase {

    /**
     * Print a node for debugging
     * 
     * @param n
     *        node to be printed
     * @return output String
     */
    public static String toString(final Node n) {
        String prefix = null;
        switch (n.getNodeType()) {
            case Node.DOCUMENT_NODE:
                prefix = "<!-- Document: " + n.getNodeName() + " -->\n";
                break;
            case Node.DOCUMENT_FRAGMENT_NODE:
                prefix = "<!-- DocumentFragment: " + n.getNodeName() + " -->\n";
                break;
            case Node.ELEMENT_NODE:
                prefix = "<!-- Element: " + n.getNodeName() + " -->\n";
                break;
            default:
                prefix = "<!-- Unspecified Node: " + n.getNodeName() + " -->\n";
                break;
        }
        return prefix + nodeToString(n, true, true);
    }

    /**
     * Hidden constructor
     */
    private XPrinter() {
        super();
    }
}
