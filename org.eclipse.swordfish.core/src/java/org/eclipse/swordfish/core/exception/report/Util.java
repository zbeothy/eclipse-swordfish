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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;
import org.eclipse.swordfish.core.exception.SBB_Exception;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class Util.
 */
public class Util {

    /** The Constant lineSeparator. */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /** The Constant wasCausing. */
    public static final String WAS_CAUSING = "WAS CAUSING:";

    /** The Constant className. */
    private static final String CLASS_NAME = Util.class.getName();

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(Util.class);

    /**
     * Append compact stack trace recursive.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pReportee
     *        the reportee
     * @param pCauseeElements
     *        the causee elements
     */
    public static void appendCompactStackTraceRecursive(final StringBuffer pIOdestination, final Throwable pReportee,
            final StackTraceElement[] pCauseeElements) {
        Throwable cause = pReportee.getCause();
        StackTraceElement[] reporteeElements = pReportee.getStackTrace();

        if (cause != null) {
            appendCompactStackTraceRecursive(pIOdestination, cause, reporteeElements);
            pIOdestination.append(Util.WAS_CAUSING);
            pIOdestination.append(Util.LINE_SEPARATOR);
        }
        String reporteeString = Util.toString(pReportee);
        // String message =
        // ExceptionTextReporter.getMessageTextPattern(pReportee);
        pIOdestination.append(reporteeString);
        // pIOdestination.append(Util.lineSeparator);
        // if (message != null){
        // pIOdestination.append(" Message : " + message);
        // }
        pIOdestination.append(Util.LINE_SEPARATOR);
        appendIrredundantTraceLines(pIOdestination, reporteeElements, pCauseeElements);
    }

    /**
     * Generate uuid.
     * 
     * @return the string
     */
    public static String generateUuid() {
        String uuidString = "00000000-0000-0000-0000-000000000000";
        try {
            uuidString = new UID().toString();
        } catch (Exception e) {
            LOG.info("failed to create UUID", e);
        }
        return uuidString;
    }

    /**
     * Gets the contained exception.
     * 
     * @param pThrowableChain
     *        the throwable chain
     * @param pExpectedThrowableClass
     *        the expected throwable class
     * 
     * @return the contained exception
     */
    public static Throwable getContainedException(final Throwable pThrowableChain, final Class pExpectedThrowableClass) {

        for (Throwable result = pThrowableChain;;) {
            if (result == null) return result;
            if (pExpectedThrowableClass.isInstance(result)) return result;
            final Throwable cause = result.getCause();
            result = cause;
        }
    }

    /**
     * Gets the local host name.
     * 
     * @return the local host name
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName() + " / " + InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.info("could not resolve local host", e);
        }
        return "unknown host";
    }

    /**
     * Gets the original exception.
     * 
     * @param pThrowable
     *        the throwable
     * 
     * @return the original exception
     */
    public final static Throwable getOriginalException(final Throwable pThrowable) {
        for (Throwable result = pThrowable;;) {

            final Throwable cause = result.getCause();
            if (cause == null) return result;
            result = cause;
        }
    }

    /**
     * Gets the package of stack trace from throwable.
     * 
     * @param pThrowable
     *        the throwable
     * 
     * @return the package of stack trace from throwable
     */
    public static String getPackageOfStackTraceFromThrowable(final Throwable pThrowable) {
        if (pThrowable == null) return null;
        return getPackageOfFQC(getClassOfStackTrace(pThrowable.getStackTrace()));

    }

    /**
     * To string.
     * 
     * @param pThrowable
     *        the throwable
     * 
     * @return the string
     */
    public static String toString(final Throwable pThrowable) {
        String throwableString = pThrowable.toString();

        if (pThrowable instanceof SBB_Exception) return throwableString;
        // all others
        String sClassName = pThrowable.getClass().getName();
        String detailMessage = pThrowable.getMessage();
        if (throwableString.indexOf(sClassName) >= 0) return throwableString;

        return sClassName + (detailMessage == null ? "" : ": " + detailMessage) + "; Caused by: " + throwableString;
    }

    /**
     * Append compact stack trace.
     * 
     * @param pIODestination
     *        the IO destination
     * @param pThrowable
     *        the throwable
     */
    static void appendCompactStackTrace(final StringBuffer pIODestination, final Throwable pThrowable) {
        if (pThrowable == null) {
            pIODestination.append("null Throwable provided to ").append(CLASS_NAME).append(".appendCompactStackTrace(...)");
            return;
        }
        appendCompactStackTraceRecursive(pIODestination, pThrowable, new StackTraceElement[0]);
    }

    /**
     * Append irredundant trace lines.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pReporteeElements
     *        the reportee elements
     * @param pCauseeElements
     *        the causee elements
     */
    static void appendIrredundantTraceLines(final StringBuffer pIOdestination, final StackTraceElement[] pReporteeElements,
            final StackTraceElement[] pCauseeElements) {
        int ri = pReporteeElements.length - 1;
        for (int ci = pCauseeElements.length - 1; (ri >= 0) && (ci >= 0); ri--, ci--) {
            StackTraceElement re = pReporteeElements[ri];
            if ((re == null) || !re.equals(pCauseeElements[ci])) {
                break;
            }
        }
        int lastSignificantIndex = ri;

        for (int i = 0; i <= lastSignificantIndex; i++) {
            pIOdestination.append("\tat ");
            pIOdestination.append(pReporteeElements[i].toString());
            pIOdestination.append(Util.LINE_SEPARATOR);
        }
    }

    /**
     * Gets the class of stack trace.
     * 
     * @param pStackTraceElement
     *        the stack trace element
     * 
     * @return the class of stack trace
     */
    static Class getClassOfStackTrace(final StackTraceElement[] pStackTraceElement) {
        if ((pStackTraceElement == null) || (pStackTraceElement.length == 0)) return null;
        Class aClass = null;
        String aClassName = null;

        StackTraceElement aStackTraceElement = pStackTraceElement[0];

        aClassName = aStackTraceElement.getClassName();

        try {
            aClass = Class.forName(aClassName);
        } catch (ClassNotFoundException e) {
            LOG.info("Class not found", e);
        }
        return aClass;
    }

    /**
     * Gets the package of FQC.
     * 
     * @param pClass
     *        the class
     * 
     * @return the package of FQC
     */
    static String getPackageOfFQC(final Class pClass) {
        if (pClass == null) return null;
        return pClass.getPackage().getName();
    }
}
