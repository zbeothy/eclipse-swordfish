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
package org.eclipse.swordfish.core.logging;

import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Mapping of debug levels debug to Fine error to severe fatal to severe.
 */
final class JavaUtilLog implements Log {

    /** logger reference. */
    private Logger logger = null;

    /**
     * The bundle base name as handed over to the logfactory during logger creation.
     * 
     * @param log
     *        the log
     */
    // private String bundleBaseName;
    /**
     * constructor.
     * 
     * @param log
     *        logger
     */
    public JavaUtilLog(final Logger log) {
        this.logger = log;
        this.setFilter();
    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String)
     */
    public void config(final String messageKey) {
        this.logger.log(Level.CONFIG, messageKey);
    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object)
     */
    public void config(final String messageKey, final Object param1) {
        Object[] params = {param1};
        this.logger.log(Level.CONFIG, messageKey, params);

    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void config(final String messageKey, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logger.log(Level.CONFIG, messageKey, params);

    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void config(final String messageKey, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.CONFIG, messageKey, params);

    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config (java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Throwable)
     */
    public void config(final String messageKey, final Object param1, final Object param2, final Object param3,
            final Throwable throwable) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.CONFIG, messageKey, params);
        this.logger.log(Level.CONFIG, messageKey, throwable);
    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Throwable)
     */
    public void config(final String messageKey, final Object param1, final Object param2, final Throwable throwable) {
        Object[] params = {param1, param2};
        this.logger.log(Level.CONFIG, messageKey, params);
        this.logger.log(Level.CONFIG, messageKey, throwable);
    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object,
     *      java.lang.Throwable)
     */
    public void config(final String messageKey, final Object param1, final Throwable throwable) {
        Object[] params = {param1};
        this.logger.log(Level.CONFIG, messageKey, params);
        this.logger.log(Level.CONFIG, messageKey, throwable);
    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object[])
     */
    public void config(final String messageKey, final Object[] params) {
        this.logger.log(Level.CONFIG, messageKey, params);

    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Object[],
     *      java.lang.Throwable)
     */
    public void config(final String messageKey, final Object[] params, final Throwable throwable) {
        this.logger.log(Level.CONFIG, messageKey, params);
        this.logger.log(Level.CONFIG, messageKey, throwable);
    }

    /**
     * Config.
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#config(java.lang.String, java.lang.Throwable)
     */
    public void config(final String messageKey, final Throwable throwable) {
        this.logger.log(Level.CONFIG, messageKey, throwable);

    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String)
     */
    public void debug(final String messageKey) {
        this.logger.log(Level.FINER, messageKey);
    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object)
     */
    public void debug(final String messageKey, final Object param1) {
        Object[] params = {param1};
        this.logger.log(Level.FINER, messageKey, params);
    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void debug(final String messageKey, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logger.log(Level.FINER, messageKey, params);

    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void debug(final String messageKey, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.FINER, messageKey, params);

    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Throwable)
     */
    public void debug(final String messageKey, final Object param1, final Object param2, final Object param3,
            final Throwable throwable) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.FINER, messageKey, params);
        this.logger.log(Level.FINER, messageKey, throwable);
    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Throwable)
     */
    public void debug(final String messageKey, final Object param1, final Object param2, final Throwable throwable) {
        Object[] params = {param1, param2};
        this.logger.log(Level.FINER, messageKey, params);
        this.logger.log(Level.FINER, messageKey, throwable);
    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object,
     *      java.lang.Throwable)
     */
    public void debug(final String messageKey, final Object param1, final Throwable throwable) {
        Object[] params = {param1};
        this.logger.log(Level.FINER, messageKey, params);
        this.logger.log(Level.FINER, messageKey, throwable);
    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object[])
     */
    public void debug(final String messageKey, final Object[] params) {
        this.logger.log(Level.FINER, messageKey, params);

    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Object[],
     *      java.lang.Throwable)
     */
    public void debug(final String messageKey, final Object[] params, final Throwable throwable) {
        this.logger.log(Level.FINER, messageKey, params);
        this.logger.log(Level.FINER, messageKey, throwable);
    }

    /**
     * Debug.
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#debug(java.lang.String, java.lang.Throwable)
     */
    public void debug(final String messageKey, final Throwable throwable) {
        this.logger.log(Level.FINER, messageKey, throwable);
    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String)
     */
    public void error(final String messageKey) {
        this.logger.log(Level.SEVERE, messageKey);

    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object)
     */
    public void error(final String messageKey, final Object param1) {
        Object[] params = {param1};
        this.logger.log(Level.SEVERE, messageKey, params);

    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void error(final String messageKey, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logger.log(Level.SEVERE, messageKey, params);

    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void error(final String messageKey, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.SEVERE, messageKey, params);

    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error (java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Throwable)
     */
    public void error(final String messageKey, final Object param1, final Object param2, final Object param3,
            final Throwable throwable) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.SEVERE, messageKey, params);
        this.logger.log(Level.SEVERE, messageKey, throwable);

    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Throwable)
     */
    public void error(final String messageKey, final Object param1, final Object param2, final Throwable throwable) {
        Object[] params = {param1, param2};
        this.logger.log(Level.SEVERE, messageKey, params);
        this.logger.log(Level.SEVERE, messageKey, throwable);
    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object,
     *      java.lang.Throwable)
     */
    public void error(final String messageKey, final Object param1, final Throwable throwable) {
        Object[] params = {param1};
        this.logger.log(Level.SEVERE, messageKey, params);
        this.logger.log(Level.SEVERE, messageKey, throwable);
    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object[])
     */
    public void error(final String messageKey, final Object[] params) {
        this.logger.log(Level.SEVERE, messageKey, params);

    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Object[],
     *      java.lang.Throwable)
     */
    public void error(final String messageKey, final Object[] params, final Throwable throwable) {
        this.logger.log(Level.SEVERE, messageKey, params);
        this.logger.log(Level.SEVERE, messageKey, throwable);
    }

    /**
     * Error.
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#error(java.lang.String, java.lang.Throwable)
     */
    public void error(final String messageKey, final Throwable throwable) {
        this.logger.log(Level.SEVERE, messageKey, throwable);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.logging.Log#getLogger()
     */
    public Object getLogger() {
        return this.logger;
    }

    /**
     * Gets the message.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @return the message
     * 
     * @see org.eclipse.swordfish.core.logging.Log#getMessage(java.lang.String, java.lang.Object[])
     */
    public String getMessage(final String messageKey, final Object[] params) {

        return null;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     * 
     * @see org.eclipse.swordfish.core.logging.Log#getName()
     */
    public String getName() {
        return this.logger.getName();
    }

    /**
     * Gets the resource bundle.
     * 
     * @return the resource bundle
     * 
     * @see org.eclipse.swordfish.core.logging.Log#getResourceBundle()
     */
    public ResourceBundle getResourceBundle() {
        return this.logger.getResourceBundle();
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String)
     */
    public void info(final String messageKey) {
        this.logger.log(Level.INFO, messageKey);
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object)
     */
    public void info(final String messageKey, final Object param1) {
        Object[] params = {param1};
        this.logger.log(Level.INFO, messageKey, params);
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void info(final String messageKey, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logger.log(Level.INFO, messageKey, params);

    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void info(final String messageKey, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.INFO, messageKey, params);

    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Throwable)
     */
    public void info(final String messageKey, final Object param1, final Object param2, final Object param3,
            final Throwable throwable) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.INFO, messageKey, params);
        this.logger.log(Level.INFO, messageKey, throwable);
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Throwable)
     */
    public void info(final String messageKey, final Object param1, final Object param2, final Throwable throwable) {
        Object[] params = {param1, param2};
        this.logger.log(Level.INFO, messageKey, params);
        this.logger.log(Level.INFO, messageKey, throwable);
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object,
     *      java.lang.Throwable)
     */
    public void info(final String messageKey, final Object param1, final Throwable throwable) {
        Object[] params = {param1};
        this.logger.log(Level.INFO, messageKey, params);
        this.logger.log(Level.INFO, messageKey, throwable);
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object[])
     */
    public void info(final String messageKey, final Object[] params) {
        this.logger.log(Level.INFO, messageKey, params);

    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Object[],
     *      java.lang.Throwable)
     */
    public void info(final String messageKey, final Object[] params, final Throwable throwable) {
        this.logger.log(Level.INFO, messageKey, params);
        this.logger.log(Level.INFO, messageKey, throwable);
    }

    /**
     * Info.
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#info(java.lang.String, java.lang.Throwable)
     */
    public void info(final String messageKey, final Throwable throwable) {
        this.logger.log(Level.INFO, messageKey, throwable);
    }

    /**
     * Checks if is config enabled.
     * 
     * @return true, if is config enabled
     * 
     * @see org.eclipse.swordfish.core.logging.Log#isConfigEnabled()
     */
    public boolean isConfigEnabled() {
        return this.logger.isLoggable(Level.CONFIG);
    }

    /**
     * Checks if is debug enabled.
     * 
     * @return true, if is debug enabled
     * 
     * @see org.eclipse.swordfish.core.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINER);
    }

    /**
     * Checks if is error enabled.
     * 
     * @return true, if is error enabled
     * 
     * @see org.eclipse.swordfish.core.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }

    /**
     * Checks if is info enabled.
     * 
     * @return true, if is info enabled
     * 
     * @see org.eclipse.swordfish.core.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }

    /**
     * Checks if is trace enabled.
     * 
     * @return true, if is trace enabled
     * 
     * @see org.eclipse.swordfish.core.logging.Log#isTraceEnabled()
     */
    public boolean isTraceEnabled() {
        return this.logger.isLoggable(Level.FINEST);
    }

    /**
     * Checks if is warn enabled.
     * 
     * @return true, if is warn enabled
     * 
     * @see org.eclipse.swordfish.core.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }

    /**
     * Pop from NDC.
     * 
     * @return the string
     * 
     * @see org.eclipse.swordfish.core.logging.Log#popFromNDC()
     */
    public String popFromNDC() {

        return null;
    }

    /**
     * Push to NDC.
     * 
     * @param message
     *        the message
     * 
     * @see org.eclipse.swordfish.core.logging.Log#pushToNDC(java.lang.String)
     */
    public void pushToNDC(final String message) {

    }

    /**
     * Put to MDC.
     * 
     * @param key
     *        the key
     * @param o
     *        the o
     * 
     * @see org.eclipse.swordfish.core.logging.Log#putToMDC(java.lang.String, java.lang.Object)
     */
    public void putToMDC(final String key, final Object o) {
        // Auto-generated method stub

    }

    /**
     * Removes the from MDC.
     * 
     * @param key
     *        the key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#removeFromMDC(java.lang.String)
     */
    public void removeFromMDC(final String key) {
        // Auto-generated method stub

    }

    /**
     * Removes the NDC.
     * 
     * @see org.eclipse.swordfish.core.logging.Log#removeNDC()
     */
    public void removeNDC() {

    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String)
     */
    public void trace(final String messageKey) {
        this.logger.log(Level.FINEST, messageKey);
    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object)
     */
    public void trace(final String messageKey, final Object param1) {
        Object[] params = {param1};
        this.logger.log(Level.FINEST, messageKey, params);
    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void trace(final String messageKey, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logger.log(Level.FINEST, messageKey, params);

    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void trace(final String messageKey, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.FINEST, messageKey, params);

    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Throwable)
     */
    public void trace(final String messageKey, final Object param1, final Object param2, final Object param3,
            final Throwable throwable) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.FINEST, messageKey, params);
        this.logger.log(Level.FINEST, messageKey, throwable);
    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Throwable)
     */
    public void trace(final String messageKey, final Object param1, final Object param2, final Throwable throwable) {
        Object[] params = {param1, param2};
        this.logger.log(Level.FINEST, messageKey, params);
        this.logger.log(Level.FINEST, messageKey, throwable);
    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object,
     *      java.lang.Throwable)
     */
    public void trace(final String messageKey, final Object param1, final Throwable throwable) {
        Object[] params = {param1};
        this.logger.log(Level.FINEST, messageKey, params);
        this.logger.log(Level.FINEST, messageKey, throwable);
    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object[])
     */
    public void trace(final String messageKey, final Object[] params) {
        this.logger.log(Level.FINEST, messageKey, params);

    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Object[],
     *      java.lang.Throwable)
     */
    public void trace(final String messageKey, final Object[] params, final Throwable throwable) {
        this.logger.log(Level.FINEST, messageKey, params);
        this.logger.log(Level.FINEST, messageKey, throwable);
    }

    /**
     * Trace.
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#trace(java.lang.String, java.lang.Throwable)
     */
    public void trace(final String messageKey, final Throwable throwable) {
        this.logger.log(Level.FINEST, messageKey, throwable);
    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String)
     */
    public void warn(final String messageKey) {
        this.logger.log(Level.WARNING, messageKey);

    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object)
     */
    public void warn(final String messageKey, final Object param1) {
        Object[] params = {param1};
        this.logger.log(Level.WARNING, messageKey, params);

    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object,
     *      java.lang.Object)
     */
    public void warn(final String messageKey, final Object param1, final Object param2) {
        Object[] params = {param1, param2};
        this.logger.log(Level.WARNING, messageKey, params);
    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public void warn(final String messageKey, final Object param1, final Object param2, final Object param3) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.WARNING, messageKey, params);

    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param param3
     *        the param3
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn (java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Object, java.lang.Throwable)
     */
    public void warn(final String messageKey, final Object param1, final Object param2, final Object param3,
            final Throwable throwable) {
        Object[] params = {param1, param2, param3};
        this.logger.log(Level.WARNING, messageKey, params);
        this.logger.log(Level.WARNING, messageKey, throwable);
    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param param2
     *        the param2
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object,
     *      java.lang.Object, java.lang.Throwable)
     */
    public void warn(final String messageKey, final Object param1, final Object param2, final Throwable throwable) {
        Object[] params = {param1, param2};
        this.logger.log(Level.WARNING, messageKey, params);
        this.logger.log(Level.WARNING, messageKey, throwable);
    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the param1
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object,
     *      java.lang.Throwable)
     */
    public void warn(final String messageKey, final Object param1, final Throwable throwable) {
        Object[] params = {param1};
        this.logger.log(Level.WARNING, messageKey, params);
        this.logger.log(Level.WARNING, messageKey, throwable);

    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object[])
     */
    public void warn(final String messageKey, final Object[] params) {
        this.logger.log(Level.WARNING, messageKey, params);

    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        the params
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Object[],
     *      java.lang.Throwable)
     */
    public void warn(final String messageKey, final Object[] params, final Throwable throwable) {
        this.logger.log(Level.WARNING, messageKey, params);
        this.logger.log(Level.WARNING, messageKey, throwable);
    }

    /**
     * Warn.
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     * 
     * @see org.eclipse.swordfish.core.logging.Log#warn(java.lang.String, java.lang.Throwable)
     */
    public void warn(final String messageKey, final Throwable throwable) {
        this.logger.log(Level.WARNING, messageKey, throwable);

    }

    /**
     * setting filter to print the correct class name.
     */
    private void setFilter() {
        this.logger.setFilter(new Filter() {

            public boolean isLoggable(final LogRecord arg0) {
                arg0.setSourceClassName(JavaUtilLog.this.logger.getName());
                return true;
            }
        });
    }

}
