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
package org.eclipse.swordfish.core.exception.report;

import java.util.ResourceBundle;
import org.eclipse.swordfish.core.exception.SBB_Exception;
import org.eclipse.swordfish.core.logging.Log;

/**
 * The Class ExceptionReporter.
 */
public final class ExceptionReporter {

    /** unknown. */
    public static final String UNKNOWN = "unknown host";

    /** message. */
    public static final String STACKTRACEFOLLOWS = "----------Stack Trace follows:----------";

    /**
     * This message return the message of a given throwable.
     * 
     * @param pThrowable
     *        throwable
     * 
     * @return String string
     */
    public static String getMessage(final Throwable pThrowable) {
        StringBuffer result = new StringBuffer();
        ExceptionTextReporter.appendMessageChain(result, pThrowable, null, null, null);
        return result.toString();
    }

    /**
     * This message return the message of a given throwable and a given resourcebundle.
     * 
     * @param pThrowable
     *        throwable
     * @param pResourceBundleName
     *        bundle name
     * 
     * @return String string
     */
    public static String getMessage(final Throwable pThrowable, final String pResourceBundleName) {
        StringBuffer result = new StringBuffer();
        ResourceBundle aResourceBundle = ResourceBundle.getBundle(pResourceBundleName);
        ExceptionTextReporter.appendMessageChain(result, pThrowable, aResourceBundle, null, null);
        return result.toString();
    }

    /**
     * Gets the parameter.
     * 
     * @param pThrowable
     *        throwable
     * 
     * @return String string
     */
    public static String getParameter(final Throwable pThrowable) {
        StringBuffer result = new StringBuffer();
        if (pThrowable instanceof SBB_Exception) {
            ExceptionTextReporter.getParameters(result, pThrowable);
        }
        if (result.length() == 0) return null;

        return result.toString();
    }

    /**
     * Gets the stack trace.
     * 
     * @param pThrowable
     *        throwable
     * 
     * @return String string
     */
    public static String getStackTrace(final Throwable pThrowable) {
        StringBuffer result = new StringBuffer();
        Util.appendCompactStackTrace(result, pThrowable);
        return result.toString();
    }

    /**
     * This method logs a throwable with a given logger.
     * 
     * @param logger
     *        logger
     * @param pThrowable
     *        throwable
     */
    public static void logErrorReport(final Log logger, final Throwable pThrowable) {
        if (logger == null) throw new NullPointerException("Logger is null");

        if (pThrowable == null) return;
        logger.error(getReport(logger, pThrowable));
    }

    /**
     * Log error report.
     * 
     * @param logger
     *        logger
     * @param pThrowable
     *        throwable
     * @param pResourceKey
     *        key
     * @param pParameter
     *        parameter
     */
    public static void logErrorReport(final Log logger, final Throwable pThrowable, final String pResourceKey,
            final String[] pParameter) {
        if (logger == null) throw new NullPointerException("Logger is null");

        if (pThrowable == null) return;
        if (pResourceKey != null) {
            logger.error(getReport(logger, pThrowable, pResourceKey, pParameter));
        } else {
            logger.error(getReport(logger, pThrowable));
        }
    }

    /**
     * This method logs a throwable with a given logger.
     * 
     * @param logger
     *        logger
     * @param pThrowable
     *        throwable
     */
    public static void logWarnReport(final Log logger, final Throwable pThrowable) {

        if (logger == null) throw new NullPointerException("Logger is null");

        if (pThrowable == null) return;

        logger.warn(getReport(logger, pThrowable));
    }

    /**
     * This method logs a throwable with a given logger.
     * 
     * @param logger
     *        logger
     * @param pResourceKey
     *        key
     * @param pThrowable
     *        throwable
     */
    public static void logWarnReport(final Log logger, final Throwable pThrowable, final String pResourceKey) {

        if (logger == null) throw new NullPointerException("Logger is null");

        if (pThrowable == null) return;
        if (pResourceKey != null) {
            logger.warn(getReport(logger, pThrowable, pResourceKey, new String[] {}));
        } else {
            logger.warn(getReport(logger, pThrowable));
        }
    }

    /**
     * This method logs a throwable with a given logger.
     * 
     * @param logger
     *        logger
     * @param pThrowable
     *        throwable
     * @param pParameter
     *        parameters
     * @param pResourceKey
     *        key
     */
    public static void logWarnReport(final Log logger, final Throwable pThrowable, final String pResourceKey,
            final String[] pParameter) {

        if (logger == null) throw new NullPointerException("Logger is null");

        if (pThrowable == null) return;
        if (pResourceKey != null) {
            logger.warn(getReport(logger, pThrowable, pResourceKey, pParameter));
        } else {
            logger.warn(getReport(logger, pThrowable));
        }
    }

    /**
     * Prints the stack trace.
     * 
     * @param pIOdestination
     *        buffer
     * @param pThrowable
     *        throwable
     */
    public static void printStackTrace(final StringBuffer pIOdestination, final Throwable pThrowable) {
        Util.appendCompactStackTrace(pIOdestination, pThrowable);
    }

    /**
     * Gets the report.
     * 
     * @param logger
     *        logger
     * @param pThrowable
     *        throwable
     * 
     * @return String string
     */
    private static String getReport(final Log logger, final Throwable pThrowable) {

        StringBuffer result = new StringBuffer();
        printReport(result, pThrowable, logger.getResourceBundle(), null, null);
        return result.toString();
    }

    /**
     * Gets the report.
     * 
     * @param logger
     *        logger
     * @param pThrowable
     *        throwble
     * @param pKey
     *        key
     * @param pParameter
     *        parameter
     * 
     * @return String string
     */
    private static String getReport(final Log logger, final Throwable pThrowable, final String pKey, final String[] pParameter) {

        StringBuffer result = new StringBuffer();
        printReport(result, pThrowable, logger.getResourceBundle(), pKey, pParameter);
        return result.toString();
    }

    /**
     * Prints the report.
     * 
     * @param pIOdestination
     *        buffer
     * @param pThrowable
     *        throwable
     * @param pResourceBundle
     *        bundle
     */
    /*
     * private static void printReport(final StringBuffer pIOdestination, final Throwable
     * pThrowable, final ResourceBundle pResourceBundle) { printMessages(pIOdestination,
     * pThrowable, pResourceBundle, null, null); pIOdestination.append(Util.LINE_SEPARATOR);
     * pIOdestination.append(STACKTRACEFOLLOWS); pIOdestination.append(Util.LINE_SEPARATOR);
     * printStackTrace(pIOdestination, pThrowable); }
     */

    /**
     * Prints the messages.
     * 
     * @param pIOdestination
     *        buffer
     * @param pThrowable
     *        throwable
     * @param pResourceBundle
     *        bundle
     * @param pKey
     *        key
     * @param pParameter
     *        parameter
     */
    private static void printMessages(final StringBuffer pIOdestination, final Throwable pThrowable,
            final ResourceBundle pResourceBundle, final String pKey, final String[] pParameter) {
        ExceptionTextReporter.appendMessageChain(pIOdestination, pThrowable, pResourceBundle, pKey, pParameter);
    }

    /**
     * Prints the report.
     * 
     * @param pIOdestination
     *        buffer
     * @param pThrowable
     *        throwable
     * @param pResourceBundle
     *        bundle
     * @param pKey
     *        key
     * @param pParameter
     *        parameter
     */
    private static void printReport(final StringBuffer pIOdestination, final Throwable pThrowable,
            final ResourceBundle pResourceBundle, final String pKey, final String[] pParameter) {
        if (pKey == null) {
            printMessages(pIOdestination, pThrowable, pResourceBundle, null, null);
        } else {
            printMessages(pIOdestination, pThrowable, pResourceBundle, pKey, pParameter);
        }
        pIOdestination.append(Util.LINE_SEPARATOR);
        pIOdestination.append(STACKTRACEFOLLOWS);
        pIOdestination.append(Util.LINE_SEPARATOR);
        printStackTrace(pIOdestination, pThrowable);
    }

    /**
     * constructor.
     */
    private ExceptionReporter() {
    }
}
