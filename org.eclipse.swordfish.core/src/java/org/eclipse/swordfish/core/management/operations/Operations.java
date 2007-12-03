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
package org.eclipse.swordfish.core.management.operations;

/**
 * Interface to trigger operational messages.<br/> This redundant implementation is necessary so
 * that the internal users of InternalOperations do not depend on the presence of the papi extension
 * jars at runtime.<br/> Instances of this interface enable the notification of operators by
 * sending operational messages. An operational message describes some event inside the application,
 * which might be important to operators. Typical these events report about significant status
 * changes of the application or about errorneous situation detected during the execution of the
 * application. <p/> Whether an event is really reported to the operator depends on current
 * configuration of the sbb internal management. <p/> An instance implementing this interface can be
 * obtained via the environment of an existing InternalSBB by
 * 
 * <pre>
 * InternalOperations myOperations =  mySbb.getEnvironment().getComponent(
 * org.eclipse.swordfish.papi.adapter.extensions.operations.Operations.class, null)
 * </pre>
 * 
 * This instance remains valid until the InternalSBB is released. As best practice it is recommended
 * to obtain such an instance only once and store it in some variable.
 * 
 */
public interface Operations {

    /**
     * Send a notification to the operator. The notification is described by <code>anEvent</code>
     * and has no specific further environment information.
     * 
     * @param anEvent
     *        description of the occured operational event
     */
    void notify(OperationalMessage anEvent);

    /**
     * Send a notification to the operator. The notification is described by <code>anEvent</code>
     * and has one specific environment information.
     * 
     * @param anEvent
     *        description of the occured operational event
     * @param param1
     *        environment information of event
     */
    void notify(OperationalMessage anEvent, Object param1);

    /**
     * Send a notification to the operator. The notification is described by <code>anEvent</code>
     * and has two specific environment informations.
     * 
     * @param anEvent
     *        description of the occured operational event
     * @param param1
     *        first environment information of event
     * @param param2
     *        second environment information of event
     */
    void notify(OperationalMessage anEvent, Object param1, Object param2);

    /**
     * Send a notification to the operator. The notification is described by <code>anEvent</code>
     * and has three specific environment informations.
     * 
     * @param anEvent
     *        description of the occured operational event
     * @param param1
     *        first environment information of event
     * @param param2
     *        second environment information of event
     * @param param3
     *        third environment information of event
     */
    void notify(OperationalMessage anEvent, Object param1, Object param2, Object param3);

    /**
     * Send a notification to the operator. The notification is described by <code>anEvent</code>
     * and has some specific environment informations.
     * 
     * @param anEvent
     *        description of the occured operational event
     * @param params
     *        environment informations of event
     */
    void notify(OperationalMessage anEvent, Object[] params);

}
