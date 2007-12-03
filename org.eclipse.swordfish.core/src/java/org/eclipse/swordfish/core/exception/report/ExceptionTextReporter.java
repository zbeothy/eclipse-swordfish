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

/**
 * the class ExceptionTextReporter
 */
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.eclipse.swordfish.core.exception.SBB_Exception;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class ExceptionTextReporter.
 */
public final class ExceptionTextReporter {

    /** The Constant LOCALESTRINGS. */
    public static final String LOCALESTRINGS = ".LocaleStrings";

    /** The Constant CLASSNAME. */
    public static final String CLASSNAME = ExceptionReporter.class.getName();

    /** The Constant CAUSEMARKER. */
    public static final String CAUSEMARKER = "CAUSE :";

    /** The Constant PARAMETERFOLLOWS. */
    private static final String PARAMETERFOLLOWS = "----------Parameters follows:----------";

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(ExceptionTextReporter.class);

    /**
     * Append message line.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pThrowable
     *        the throwable
     * @param pResourceBundle
     *        the resource bundle
     * 
     * @return the string
     */
    public static String appendMessageLine(final StringBuffer pIOdestination, final Throwable pThrowable,
            final ResourceBundle pResourceBundle) {

        StringBuffer message = new StringBuffer();
        try {
            String textPattern = getMessageTextPattern(pThrowable, pResourceBundle);
            Object[] parameters = getParameters(pThrowable);

            if ((textPattern != null) && ((parameters != null) && (parameters.length > 0))) {
                Locale locale = pResourceBundle == null ? null : pResourceBundle.getLocale();
                format(message, textPattern, parameters, locale);
                pIOdestination.append(message);
            } else if ((textPattern != null) && ((pThrowable instanceof SBB_Exception) || (pResourceBundle != null))) {
                pIOdestination.append(textPattern);
                String detailMessage = pThrowable.getMessage();
                if (detailMessage != null) {
                    pIOdestination.append(": ");
                    pIOdestination.append(detailMessage);
                }
            } else {
                String result = Util.toString(pThrowable);
                pIOdestination.append(result);
                return result;
            }

            if (pThrowable instanceof SBB_Exception) {
                // System.err.println(".... " + pIOdestination.toString());
                ((SBB_Exception) pThrowable).setDefaultMessage(message.toString());
            }

            return null;
        } catch (Exception ex) {
            LOG.error(CLASSNAME + ": WHEN PREPARING MESSAGE LINE OCCURRED EXCEPTION:", ex);
            // LogLog.error(CLASSNAME + ": WHEN PREPARING MESSAGE LINE OCCURRED
            // EXCEPTION:", ex);
            String result = Util.toString(pThrowable);
            pIOdestination.append(result);
            return result;
        }
    }

    /**
     * Append message chain.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pThrowable
     *        the throwable
     * @param pResourceBundle
     *        the resource bundle
     * @param pKey
     *        the key
     * @param pParameter
     *        the parameter
     */
    protected static void appendMessageChain(final StringBuffer pIOdestination, final Throwable pThrowable,
            final ResourceBundle pResourceBundle, final String pKey, final String[] pParameter) {

        ResourceBundle resourceBundle = pResourceBundle == null ? getResourceBundle(pThrowable) : pResourceBundle;
        String causeSeparator = Util.LINE_SEPARATOR + getCauseMarker(resourceBundle);

        if (pThrowable == null) {
            pIOdestination.append("null Throwable provided to ").append(CLASSNAME).append(".appendMessageChain(...)");
            return;
        }

        boolean alreadyReported = false;

        appendAdditonalInfos(pResourceBundle, pIOdestination, pKey, pParameter);

        for (Throwable aThrowable = pThrowable; aThrowable != null;) {
            Throwable cause = aThrowable.getCause();

            if (alreadyReported) {
                pIOdestination.append(causeSeparator);
            }

            appendMessageLineDeletingPreviousOccurence(pIOdestination, aThrowable, resourceBundle);
            alreadyReported = true;
            aThrowable = cause;
        }
    }

    /**
     * Format.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pTextPattern
     *        the text pattern
     * @param pParameters
     *        the parameters
     * @param pLocale
     *        the locale
     */
    static void format(final StringBuffer pIOdestination, final String pTextPattern, final Object[] pParameters,
            final Locale pLocale) {
        MessageFormat format = new MessageFormat(pTextPattern);

        if (pLocale != null) {
            format.setLocale(pLocale);
        }
        format.format(pParameters, pIOdestination, null);
    }

    /**
     * Gets the cause marker.
     * 
     * @param pResourceBundle
     *        the resource bundle
     * 
     * @return the cause marker
     */
    static String getCauseMarker(final ResourceBundle pResourceBundle) {
        return getResource(pResourceBundle, CAUSEMARKER, "Cause : ");
    }

    /**
     * Gets the message text pattern.
     * 
     * @param pThrowable
     *        the throwable
     * 
     * @return the message text pattern
     */
    static String getMessageTextPattern(final Throwable pThrowable) {
        return getMessageTextPattern(pThrowable, null);
    }

    /**
     * Gets the message text pattern.
     * 
     * @param pThrowable
     *        the throwable
     * @param pResourceBundle
     *        the resource bundle
     * 
     * @return the message text pattern
     */
    static String getMessageTextPattern(final Throwable pThrowable, final ResourceBundle pResourceBundle) {

        String aKey = null;
        String aMessagePattern = null;

        if (!(pThrowable instanceof SBB_Exception)) {
            aKey = pThrowable.getMessage();
            aMessagePattern = getResource(pResourceBundle, aKey, null);
        } else {
            aKey = ((SBB_Exception) pThrowable).getResourceKey();
            if (aKey == null) return aMessagePattern;
            aMessagePattern = getResource(pResourceBundle, aKey, null);
            if (aMessagePattern == null) {

                final ResourceBundle gResourceBundle = getResourceBundle(pThrowable);
                aMessagePattern = getResource(gResourceBundle, aKey, null);
            }
        }
        return aMessagePattern;
    }

    /**
     * Gets the parameters.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pThrowable
     *        the throwable
     */
    static void getParameters(final StringBuffer pIOdestination, final Throwable pThrowable) {
        if (!(pThrowable instanceof SBB_Exception) || !((SBB_Exception) pThrowable).hasMessageParameters()) return;

        pIOdestination.append(Util.LINE_SEPARATOR);
        pIOdestination.append(PARAMETERFOLLOWS);
        pIOdestination.append(Util.LINE_SEPARATOR);

        for (int i = 0; i < ((SBB_Exception) pThrowable).getMessageParameters().length; i++) {
            pIOdestination.append("Parameter ");
            pIOdestination.append(i);
            pIOdestination.append(" : ");
            pIOdestination.append(((SBB_Exception) pThrowable).getMessageParameters()[i]);
            pIOdestination.append(Util.LINE_SEPARATOR);
        }
    }

    /**
     * Gets the prefix.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pThrowable
     *        the throwable
     */
    static void getPrefix(final StringBuffer pIOdestination, final Throwable pThrowable) {
        if (!(pThrowable instanceof SBB_Exception)) return;
        pIOdestination.append(pThrowable.getClass().getName());
        pIOdestination.append(" : ");
        pIOdestination.append(((SBB_Exception) pThrowable).getUuid());
        pIOdestination.append(" : ");
        pIOdestination.append(((SBB_Exception) pThrowable).getResourceKey());
        pIOdestination.append(" : ");
    }

    /**
     * Gets the resource bundle.
     * 
     * @param pClass
     *        the class
     * 
     * @return the resource bundle
     */
    static ResourceBundle getResourceBundle(final Class pClass) {

        if (pClass == null) return null;
        ResourceBundle resourceBundle = ResourceBundle.getBundle(pClass.getName());
        return resourceBundle;

    }

    /**
     * Gets the resource bundle.
     * 
     * @param pThrowable
     *        the throwable
     * 
     * @return the resource bundle
     */
    static ResourceBundle getResourceBundle(final Throwable pThrowable) {

        if (!(pThrowable instanceof SBB_Exception)) return null;
        String resourceBundleName = pThrowable.getClass().getPackage().getName() + LOCALESTRINGS;
        // String resourceBundleName =
        // Util.getPackageOfStackTraceFromThrowable(pThrowable) + LOCALESTRINGS;
        ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName);
        return resourceBundle;
    }

    /**
     * Append additonal infos.
     * 
     * @param pResourceBundle
     *        the resource bundle
     * @param pIOdestination
     *        the i odestination
     * @param pKey
     *        the key
     * @param pParameter
     *        the parameter
     */
    private static void appendAdditonalInfos(final ResourceBundle pResourceBundle, final StringBuffer pIOdestination,
            final String pKey, final String[] pParameter) {
        if (pKey == null) return;
        pIOdestination.append(pKey);
        pIOdestination.append(" : ");
        String aTextPattern = getResource(pResourceBundle, pKey, null);

        if (aTextPattern != null) {
            Locale locale = pResourceBundle == null ? null : pResourceBundle.getLocale();
            format(pIOdestination, aTextPattern, pParameter, locale);
            pIOdestination.append(Util.LINE_SEPARATOR);
        }
    }

    /**
     * Append message line deleting previous occurence.
     * 
     * @param pIOdestination
     *        the i odestination
     * @param pThrowable
     *        the throwable
     * @param pResourceBundle
     *        the resource bundle
     */
    private static void appendMessageLineDeletingPreviousOccurence(final StringBuffer pIOdestination, final Throwable pThrowable,
            final ResourceBundle pResourceBundle) {

        int previousLength = pIOdestination.length();
        String lastLine = appendMessageLine(pIOdestination, pThrowable, pResourceBundle);
        if (lastLine != null) {
            int firstOccurenceIndex = pIOdestination.indexOf(lastLine);
            if (firstOccurenceIndex < previousLength) {
                pIOdestination.replace(firstOccurenceIndex, firstOccurenceIndex + lastLine.length(), "...");
            }
        }
    }

    /**
     * Gets the parameters.
     * 
     * @param pThrowable
     *        the throwable
     * 
     * @return the parameters
     */
    private static Object[] getParameters(final Throwable pThrowable) {
        if (!(pThrowable instanceof SBB_Exception)) return null;
        return ((SBB_Exception) pThrowable).getMessageParameters();
    }

    /**
     * Gets the resource.
     * 
     * @param pResourceBundle
     *        the resource bundle
     * @param pKey
     *        the key
     * @param pDefaultValue
     *        the default value
     * 
     * @return the resource
     */
    private static String getResource(final ResourceBundle pResourceBundle, final String pKey, final String pDefaultValue) {

        if (pResourceBundle == null) return pDefaultValue;

        try {
            return pResourceBundle.getString(pKey);
        } catch (java.util.MissingResourceException e) {
            return pDefaultValue;
        } catch (NullPointerException e) {
            return pDefaultValue;
        }
    }

    /**
     * Instantiates a new exception text reporter.
     */
    private ExceptionTextReporter() {
    }
}
