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
package org.eclipse.swordfish.core.interceptor.extension;

/**
 * The Interface ExtensionProcessor.
 */
public interface ExtensionProcessor {

    /** This attribute describes the role name for Transformation component. */
    String ROLE = ExtensionProcessor.class.getName();

    // FIXME we have an issue with multiple extensions in an agreed policy.
    // currently they will
    // all be processed at the same processing position
}
