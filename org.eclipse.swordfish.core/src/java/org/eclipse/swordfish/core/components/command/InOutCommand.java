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

/**
 * The Interface InOutCommand.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public interface InOutCommand extends Command {

    /** The role of the in-out command for identification reasons in the BeanFactory. */
    String ROLE = InOutCommand.class.getName();

    /**
     * Checks if is sync.
     * 
     * @return -- if this command is intended to be used synchronously
     */
    boolean isSync();

    /**
     * indicate this inout command is intended to be used syschronously param sync if this command
     * is in sync mode.
     * 
     * @param sync
     *        the sync
     */
    void setSync(boolean sync);
}
