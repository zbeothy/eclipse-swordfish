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
package org.eclipse.swordfish.core.management.components;

/**
 * public interface of management controller.
 * 
 */
public interface ManagementController {

    /**
     * Register component that should be shut down as part of the controller's shutdown.
     * 
     * @param component
     *        to be shut down when the controller shuts down
     */
    void addLifecycleComponent(LifecycleComponent component);

    /**
     * destroy the management controller - called by container takes care of dependencies between
     * registered components.
     */
    void destroy();

    /**
     * set up management infrastructure - called by container.
     */
    void init();

}
