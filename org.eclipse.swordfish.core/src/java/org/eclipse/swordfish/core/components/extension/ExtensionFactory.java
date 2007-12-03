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
package org.eclipse.swordfish.core.components.extension;

/**
 * Specification for factories that create instances of papi extensions <br/> Implement this
 * interface if you want to provide a custom factory for your extension. If you only want return a
 * singleton bean, you can use the default implementation in
 * org.eclipse.swordfish.core.components.extension.impl.DefaultExtensionFactory
 * 
 * The actual instance of factory to be used is specified in the Spring config and looked up by
 * org.eclipse.swordfish.core.components.iapi.impl.KernelBean.getComponent()
 * 
 */
public interface ExtensionFactory {

    /**
     * Provides an instance of the papi extension.
     * 
     * @param diversifier
     *        optional additional indicator for a specific required implementation. May be null.
     *        Concrete usage depends on required interface and will be defined with the extension
     * 
     * @return an instance of the papi extension
     */
    Object getInstance(String diversifier);

}
