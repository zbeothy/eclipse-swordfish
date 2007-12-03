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
package org.eclipse.swordfish.policytrader.testing.helpers;

public interface XMLEncoder {

    /**
     * Encode a string
     * 
     * @param xmlString
     *        string to be encoded
     * @return encoded string
     */
    public String encode(String xmlString);

    /**
     * gets the encoding supported by this encoder
     * 
     * @return string
     */
    public String getEncoding();

}
