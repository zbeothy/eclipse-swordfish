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
package org.eclipse.swordfish.core.management.adapter;

/**
 * The Interface ExtensionAdapter.
 */
public interface ExtensionAdapter extends ManagementAdapter {

    /**
     * Destroy.
     */
    void destroy();

    /**
     * Init.
     */
    void init();

    /**
     * Sets the property.
     * 
     * @param name
     *        the name
     * @param value
     *        the value
     */
    void setProperty(String name, Object value);

}
