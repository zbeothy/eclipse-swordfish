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
package org.eclipse.swordfish.core.components.command.impl;

import org.eclipse.swordfish.core.components.command.CallbackCommand;
import org.eclipse.swordfish.core.components.command.Command;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.command.InOnlyCommand;
import org.eclipse.swordfish.core.components.command.InOutCommand;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * this class is intended to produce commands that fit a particular message exchange.
 */
public class CommandFactoryBean implements ApplicationContextAware, CommandFactory {

    /** the application context this command factory lifes in. */
    private ApplicationContext appCtx;

    /**
     * Creates the command.
     * 
     * @param desc
     *        the desc
     * 
     * @return the command
     * 
     * @see org.eclipse.swordfish.core.components.command.CommandFactory#createCommand(javax.jbi.messaging.MessageExchange)
     */
    public Command createCommand(final OperationDescription desc) {

        Command command;
        String commandName = null;
        if (MessageExchangePattern.OUT_ONLY_URI.equals(desc.getExchangePattern())) {
            commandName = InOnlyCommand.ROLE;
        } else if (MessageExchangePattern.IN_ONLY_URI.equals(desc.getExchangePattern())) {
            if (desc.hasCallbackOperation() || desc.isCallbackOperation()) {
                commandName = CallbackCommand.ROLE;
            } else {
                commandName = InOnlyCommand.ROLE;
            }
        } else if (MessageExchangePattern.IN_OUT_URI.equals(desc.getExchangePattern())) {
            commandName = InOutCommand.ROLE;
        }

        if (commandName != null) {
            ClassLoader currClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(CommandFactoryBean.class.getClassLoader());
                command = (Command) this.appCtx.getBean(commandName);
                command.setOperationDescription(desc);
            } finally {
                Thread.currentThread().setContextClassLoader(currClassLoader);
            }
            return command;
        } else
            throw new RuntimeException("cannot create the correct command for " + desc.getName());
    }

    /**
     * Sets the application context.
     * 
     * @param anAppCtx
     *        the an app ctx
     * 
     * @throws BeansException
     * 
     * @see org.springframework.context.ApplicationContextAware#
     *      setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(final ApplicationContext anAppCtx) throws BeansException {
        this.appCtx = anAppCtx;
    }
}
