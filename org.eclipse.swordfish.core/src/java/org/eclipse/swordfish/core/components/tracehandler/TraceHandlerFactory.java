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
package org.eclipse.swordfish.core.components.tracehandler;

/**
 * A factory for creating TraceHandler objects.
 */
public class TraceHandlerFactory {

    /**
     * Creates a new TraceHandler object.
     * 
     * @param obj
     *        the obj
     * 
     * @return the trace handler
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    static public TraceHandler createTraceHandler(final Object obj) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        TraceHandler handler = null;
        if ("java.util.logging.Logger".equals(obj.getClass().getName())) {
            handler =
                    (TraceHandler) Class.forName("org.eclipse.swordfish.core.components.tracehandler.JavaUtilLogHandler")
                        .newInstance();
        }
        // snip
        // add other loggers here ..
        // snap
        if (handler != null) {
            handler.setLogger(obj);
        }
        return handler;
    }

    /**
     * Instantiates a new trace handler factory.
     */
    private TraceHandlerFactory() {
    }

}
