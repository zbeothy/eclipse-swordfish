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
package org.eclipse.swordfish.configrepos.shared.validation;

import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The Class ValidationErrorHandler.
 * 
 */
public class ValidationErrorHandler implements ErrorHandler {

    /** The log. */
    private Logger log;

    /**
     * The Constructor.
     * 
     * @param pLog
     *        logger
     */
    public ValidationErrorHandler(final Logger pLog) {
        this.log = pLog;
    }

    /**
     * (non-Javadoc).
     * 
     * @param spe
     *        the spe
     * 
     * @throws SAXException
     * 
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(final SAXParseException spe) throws SAXException {
        String message = "Error: " + this.getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    /**
     * (non-Javadoc).
     * 
     * @param spe
     *        the spe
     * 
     * @throws SAXException
     * 
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(final SAXParseException spe) throws SAXException {
        String message = "Fatal Error: " + this.getParseExceptionInfo(spe);
        throw new SAXException(message);
    }

    /**
     * (non-Javadoc).
     * 
     * @param spe
     *        the spe
     * 
     * @throws SAXException
     * 
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(final SAXParseException spe) throws SAXException {
        this.log.warning("Warning: " + this.getParseExceptionInfo(spe));
    }

    /**
     * Gets the parse exception info.
     * 
     * @param spe
     *        a exception
     * 
     * @return info the info
     */
    private String getParseExceptionInfo(final SAXParseException spe) {
        String systemId = spe.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
        return info;
    }

}
