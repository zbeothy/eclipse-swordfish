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

/**
 * The implementation guideline for the InternalSBB Logging facility which ones correspond to the
 * demanded requirements:
 * 
 * <p>
 * The six logging levels used by <code>Log</code> are (in order):
 * <ol>
 * <li>trace</li>
 * <li>debug</li>
 * <li>config</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * </ol>
 * 
 * <p>
 * Performance is often a logging concern. By examining the appropriate property, a component can
 * avoid expensive operations (producing information to be logged).
 * </p>
 * 
 * <p>
 * For example, <code><pre>
 * if (log.isDebugEnabled()) {
 * ... do something expensive ...
 * log.debug(theResult);
 * }
 * </pre></code>
 * </p>
 * 
 * <p>
 * Configuration of the underlying logging system will currently be done external to the Logging
 * APIs, through whatever mechanism is supported by that system. This is currently an open issue
 * because the the conceptual phase of the configuration facility has not finished yet.
 * </p>
 * 
 * @todo CTRL: Make behavior transparent to enable the programmatic control of logging behavior.
 * @todo CFG: Change the configuration process from the log4j specific to the InternalSBB provided.
 */
public interface Log {

    // ----------------------------------------------------- Logging Properties

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     */
    void config(final String messageKey);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     */
    void config(final String messageKey, final Object param1);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     */
    void config(final String messageKey, final Object param1, final Object param2);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     * @param param3
     *        the third of parameter that should be provided to the message
     */
    void config(final String messageKey, final Object param1, final Object param2, final Object param3);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param param3
     *        the third parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void config(final String messageKey, final Object param1, final Object param2, final Object param3, final Throwable throwable);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void config(final String messageKey, final Object param1, final Object param2, final Throwable throwable);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void config(final String messageKey, final Object param1, final Throwable throwable);

    // -------------------------------------------------------- Logging Methods

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     */
    void config(final String messageKey, final Object[] params);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void config(final String messageKey, final Object[] params, final Throwable throwable);

    /**
     * Log a config message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     */
    void config(final String messageKey, final Throwable throwable);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     */
    void debug(final String messageKey);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     */
    void debug(final String messageKey, final Object param1);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     */
    void debug(final String messageKey, final Object param1, final Object param2);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     * @param param3
     *        the third of parameter that should be provided to the message
     */
    void debug(final String messageKey, final Object param1, final Object param2, final Object param3);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param param3
     *        the third parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void debug(final String messageKey, final Object param1, final Object param2, final Object param3, final Throwable throwable);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void debug(final String messageKey, final Object param1, final Object param2, final Throwable throwable);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void debug(final String messageKey, final Object param1, final Throwable throwable);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     */
    void debug(final String messageKey, final Object[] params);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void debug(final String messageKey, final Object[] params, final Throwable throwable);

    /**
     * Log a debug message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     */
    void debug(final String messageKey, final Throwable throwable);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     */
    void error(final String messageKey);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     */
    void error(final String messageKey, final Object param1);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     */
    void error(final String messageKey, final Object param1, final Object param2);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     * @param param3
     *        the third of parameter that should be provided to the message
     */
    void error(final String messageKey, final Object param1, final Object param2, final Object param3);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param param3
     *        the third parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void error(final String messageKey, final Object param1, final Object param2, final Object param3, final Throwable throwable);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void error(final String messageKey, final Object param1, final Object param2, final Throwable throwable);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void error(final String messageKey, final Object param1, final Throwable throwable);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     */
    void error(final String messageKey, final Object[] params);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void error(final String messageKey, final Object[] params, final Throwable throwable);

    /**
     * Log a error message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     */
    void error(final String messageKey, final Throwable throwable);

    /**
     * Gets the logger.
     * 
     * @return the native logger used to create this logger
     */
    Object getLogger();

    /**
     * In honor of MSS .... This method obtains for a given message key the corresponding message
     * pattern. If the handed over parameters not null and not empty, it provides the the parameters
     * to this pattern and gives the resulting message back. Otherwise, the the obtained pattern
     * will be returned as message.
     * <p>
     * Also this method implements our mega requirement, that the
     * <ul>
     * <li>full bundle name if it was handed ofer during the logger creation</li>
     * <li>the message key and ...</li>
     * <li>the list of populated parameters ...</li>
     * </ul>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameters
     * 
     * @return a prepared message string
     */
    String getMessage(final String messageKey, final Object[] params);

    /**
     * return name of the logger.
     * 
     * @return name of the logger
     */
    String getName();

    /**
     * Returns the attached ResourceBundle.
     * 
     * @return the attached ResourceBundle or null if no ResourceBundle avaible
     */
    ResourceBundle getResourceBundle();

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     */
    void info(final String messageKey);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     */
    void info(final String messageKey, final Object param1);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     */
    void info(final String messageKey, final Object param1, final Object param2);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     * @param param3
     *        the third of parameter that should be provided to the message
     */
    void info(final String messageKey, final Object param1, final Object param2, final Object param3);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param param3
     *        the third parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void info(final String messageKey, final Object param1, final Object param2, final Object param3, final Throwable throwable);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void info(final String messageKey, final Object param1, final Object param2, final Throwable throwable);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void info(final String messageKey, final Object param1, final Throwable throwable);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     */
    void info(final String messageKey, final Object[] params);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void info(final String messageKey, final Object[] params, final Throwable throwable);

    /**
     * Log a info message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     */
    void info(final String messageKey, final Throwable throwable);

    /**
     * <p>
     * Is config logging currently enabled?
     * </p>
     * 
     * <p>
     * Call this method to prevent having to perform expensive operations (for example,
     * <code>String</code> concatenation) when the log level is more than config.
     * </p>
     * 
     * @return true if warning level is enabled
     */
    boolean isConfigEnabled();

    /**
     * <p>
     * Is debug logging currently enabled?
     * </p>
     * 
     * <p>
     * Call this method to prevent having to perform expensive operations (for example,
     * <code>String</code> concatenation) when the log level is more than debug.
     * </p>
     * 
     * @return true if debug level is enabled
     */
    boolean isDebugEnabled();

    /**
     * <p>
     * Is error logging currently enabled?
     * </p>
     * 
     * <p>
     * Call this method to prevent having to perform expensive operations (for example,
     * <code>String</code> concatenation) when the log level is more than error.
     * </p>
     * 
     * @return true if error level is enabled
     */
    boolean isErrorEnabled();

    /**
     * <p>
     * Is info logging currently enabled?
     * </p>
     * 
     * <p>
     * Call this method to prevent having to perform expensive operations (for example,
     * <code>String</code> concatenation) when the log level is more than info.
     * </p>
     * 
     * @return true if info level is enabled
     */
    boolean isInfoEnabled();

    /**
     * <p>
     * Is trace logging currently enabled?
     * </p>
     * 
     * <p>
     * Call this method to prevent having to perform expensive operations (for example,
     * <code>String</code> concatenation) when the log level is more than trace.
     * </p>
     * 
     * @return true if warning level is enabled
     */
    boolean isTraceEnabled();

    /**
     * <p>
     * Is warn logging currently enabled?
     * </p>
     * 
     * <p>
     * Call this method to prevent having to perform expensive operations (for example,
     * <code>String</code> concatenation) when the log level is more than warn.
     * </p>
     * 
     * @return true if warning level is enabled
     */
    boolean isWarnEnabled();

    /**
     * Clients should call this method before leaving a diagnostic context.
     * <p>
     * The returned value is the value that was pushed last. If no context is available, then the
     * empty string "" is returned.
     * 
     * @return String The innermost diagnostic context.
     */
    String popFromNDC();

    /**
     * Push new diagnostic context information for the current thread.
     * <p>
     * The contents of the <code>message</code> parameter is determined solely by the client.
     * 
     * @param message
     *        The new diagnostic context information.
     */
    void pushToNDC(String message);

    /**
     * Put a context value (the <code>o</code> parameter) as identified with the <code>key</code>
     * parameter into the current thread's context map.
     * <p>
     * If the current thread does not have a context map it is created as a side effect.
     * 
     * @param key
     *        the key for the the value should be mapped;
     * @param o
     *        the value that shall be registered.
     */
    void putToMDC(final String key, final Object o);

    /**
     * Remove the the context identified by the <code>key</code> parameter.
     * 
     * @param key
     *        Identificator of the context entry that shall be removed.
     */
    void removeFromMDC(final String key);

    /**
     * Remove the diagnostic context for this thread.
     * <p>
     * Each thread that created a diagnostic context by calling {@link #pushToNDC} should call this
     * method before exiting. Otherwise, the memory used by the <b>thread</b> cannot be reclaimed
     * by the VM.
     */
    void removeNDC();

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     */
    void trace(final String messageKey);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     */
    void trace(final String messageKey, final Object param1);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     */
    void trace(final String messageKey, final Object param1, final Object param2);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     * @param param3
     *        the third of parameter that should be provided to the message
     */
    void trace(final String messageKey, final Object param1, final Object param2, final Object param3);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param param3
     *        the third parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void trace(final String messageKey, final Object param1, final Object param2, final Object param3, final Throwable throwable);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void trace(final String messageKey, final Object param1, final Object param2, final Throwable throwable);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void trace(final String messageKey, final Object param1, final Throwable throwable);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     */
    void trace(final String messageKey, final Object[] params);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void trace(final String messageKey, final Object[] params, final Throwable throwable);

    /**
     * Log a trace message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     */
    void trace(final String messageKey, final Throwable throwable);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     */
    void warn(final String messageKey);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     */
    void warn(final String messageKey, final Object param1);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     */
    void warn(final String messageKey, final Object param1, final Object param2);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first of parameter that should be provided to the message
     * @param param2
     *        the second of parameter that should be provided to the message
     * @param param3
     *        the third of parameter that should be provided to the message
     */
    void warn(final String messageKey, final Object param1, final Object param2, final Object param3);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param param3
     *        the third parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void warn(final String messageKey, final Object param1, final Object param2, final Object param3, final Throwable throwable);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param param2
     *        the second parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void warn(final String messageKey, final Object param1, final Object param2, final Throwable throwable);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param param1
     *        the first parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void warn(final String messageKey, final Object param1, final Throwable throwable);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     */
    void warn(final String messageKey, final Object[] params);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param params
     *        an array of parameter that should be provided to the message
     * @param throwable
     *        the throwable
     */
    void warn(final String messageKey, final Object[] params, final Throwable throwable);

    /**
     * Log a warn message. The corresponding message will be resolved from a corresponding resouce
     * file via the handed over resouce key. <p/> If the key is undefined the following message will
     * be appeared: <p/> <code>No resource is associated with key "foox"</code>
     * 
     * @param messageKey
     *        the message key
     * @param throwable
     *        the throwable
     */
    void warn(final String messageKey, final Throwable throwable);
}
