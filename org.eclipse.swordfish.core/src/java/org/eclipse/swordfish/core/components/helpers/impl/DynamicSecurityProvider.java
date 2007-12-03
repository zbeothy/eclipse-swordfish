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
package org.eclipse.swordfish.core.components.helpers.impl;

import java.security.Provider;
import java.security.Security;
import java.util.Iterator;
import java.util.Set;

public class DynamicSecurityProvider {

    private static String keyManagerAlgorithm = "SunX509";

    private static String trustManagerAlgorithm = "SunX509";

    private static final String keyManagerFactoryService = "KeyManagerFactory";

    private static final String trustManagerFactoryService = "TrustManagerFactory";

    static {
        Set set = Security.getAlgorithms(keyManagerFactoryService);
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String algorithm = (String) it.next();
            Provider[] provList = Security.getProviders(trustManagerFactoryService + "." + algorithm);
            if ((provList != null) && (provList.length != 0)) {
                // we have a provider for this algorithm, we can go ahead and use this algorithm.
                keyManagerAlgorithm = algorithm;
                trustManagerAlgorithm = algorithm;
                break;
            }

        }
    }

    public static String getKeyManagerAlgorithm() {
        return keyManagerAlgorithm;
    }

    public static String getTrustManagerAlgorithm() {
        return trustManagerAlgorithm;
    }

    public static void setKeyManagerAlgorithm(final String managerAlgorithm) {
        keyManagerAlgorithm = managerAlgorithm;
    }

    public static void setTrustManagerAlgorithm(final String managerAlgorithm) {
        trustManagerAlgorithm = managerAlgorithm;
    }

}
