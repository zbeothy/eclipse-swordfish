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
package org.eclipse.swordfish.core.interceptor.authentication;

import org.eclipse.swordfish.core.components.processing.ProcessingComponent;

/**
 * The Interface AuthenticationProcessor.
 * 
 */
public interface AuthenticationProcessor extends ProcessingComponent {

    /** This attribute describes the role name for Authentication component. */
    String ROLE = AuthenticationProcessor.class.getName();

}
