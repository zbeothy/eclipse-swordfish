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

public class XMLEncoderFactory {

    public static XMLEncoder createInstance(final String encoding) {
        if ("UTF-8".equals(encoding)) return new UTF8Encoder();
        if ("UTF-16".equals(encoding)) return new UTF16Encoder();
        throw new RuntimeException("unknown encoding to create an encoder for: " + encoding);
    }
}
