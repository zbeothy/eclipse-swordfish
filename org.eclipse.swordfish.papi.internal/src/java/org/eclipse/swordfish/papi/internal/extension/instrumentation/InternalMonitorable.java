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
package org.eclipse.swordfish.papi.internal.extension.instrumentation;

import java.util.List;

/**
 * Interface that participants need to implement if they want to provide monitored components that
 * are integrated in the isAlive monitoring of the SBB management component.
 * 
 */
public interface InternalMonitorable {

    /**
     * getChildren().
     * 
     * @return <code>List</code> containing the <code>InternalMonitorable</code> children of
     *         this <code>InternalMonitorable</code>
     */
    List getChildren();

    /**
     * Provides access to the current state of the component.<br>
     * Note that this attribute should only be used directly, the <code>InternalState</code>
     * attributes of it's children should not be used to generate a "compound state".
     * 
     * @return the current <code>InternalState</code> of the component
     */
    InternalState getState();

}
