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
package org.eclipse.swordfish.core.components.command;

import org.eclipse.swordfish.core.components.iapi.OperationDescription;

/**
 * The Interface CommandFactory.
 * 
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface CommandFactory {

    /** this classes role. */
    String ROLE = CommandFactory.class.getName();

    /**
     * Will create a command based on the interactionStyle in use.
     * 
     * @param desc
     *        the desc
     * 
     * @return --- the specified command
     */
    Command createCommand(final OperationDescription desc);
}
