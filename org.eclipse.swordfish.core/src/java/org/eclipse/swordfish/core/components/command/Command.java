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

import javax.jbi.messaging.MessageExchange;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;

/**
 * The command interface contains the abstract capabilities of all commands.
 */
public interface Command {

    /** The role of the command for identification reasons in the BeanFactory. */
    String ROLE = Command.class.getName();

    /**
     * makes this command to execute.
     * 
     * @throws Exception
     */
    void execute() throws Exception;

    /**
     * Failed.
     * 
     * @return -- true if this command has failed through its execution
     */
    boolean failed();

    /**
     * Gets the exchange.
     * 
     * @return -- the exchange this command is handling
     */
    MessageExchange getExchange();

    /**
     * Gets the execution begin.
     * 
     * @return -- the value of time in milisec. where the processing of this command started
     */
    long getExecutionBegin();

    /**
     * Gets the execution end.
     * 
     * @return -- the value of time in milisec. where the processing of this command finished
     */
    long getExecutionEnd();

    /**
     * Gets the throwable.
     * 
     * @return -- the throwable that caused this command to fail
     */
    Throwable getThrowable();

    /**
     * sets the unti of work for this command in the actual role and scope.
     * 
     * @param mep
     *        the message exchange to deal with
     */
    void setExchange(MessageExchange mep);

    /**
     * Sets the execution begin.
     * 
     * @param begin
     *        the value of time in milisecodns from which this exchange should count the time for
     *        its execution
     */
    void setExecutionBegin(long begin);

    /**
     * the value of the invoking PAPI operations message handler in order to provide the
     * communication path to the participant application.
     * 
     * @param handler
     *        the message handler that should be used for this command
     */
    void setMessageHandler(IncomingMessageHandlerProxy handler);

    /**
     * Sets the operation description.
     * 
     * @param desc
     *        the new operation description
     */
    void setOperationDescription(OperationDescription desc);

    /**
     * indicates if the command should catch all exceptions thrown during its procesing for later
     * evalution or not to do so but rethrow them.
     * 
     * @param flag
     *        if true than all catched throwables will be rethrown encapsulated in a
     *        RuntimeException. Default value is false.
     */
    void setRethrowException(boolean flag);

    /**
     * Sets the role.
     * 
     * @param role
     *        The role to set.
     */
    void setRole(Role role);

    // unfourtunatly we do not use the operation description in the way we
    // proccess the command

    /**
     * Sets the scope.
     * 
     * @param scope
     *        The scope to set.
     */
    void setScope(Scope scope);
}
