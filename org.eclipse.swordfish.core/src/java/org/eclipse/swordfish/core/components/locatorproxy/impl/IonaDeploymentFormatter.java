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
package org.eclipse.swordfish.core.components.locatorproxy.impl;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;

/**
 * This classes duty is to reformat the SOPware formatting of the locator configuration into a
 * configuration that is understandable by the IONA locator Proxy.
 */
public class IonaDeploymentFormatter {

    /** The style sheet. */
    static private StreamSource styleSheet;

    /** The counter. */
    static private int counter = 0;

    /** The transformer. */
    private static Transformer transformer;

    /**
     * read the styleSheet and instantiate the tranformer at the class load time.
     */
    static {
        ClassLoader ldr = IonaDeploymentFormatter.class.getClassLoader();
        styleSheet =
                new StreamSource(ldr
                    .getResourceAsStream("org/eclipse/swordfish/core/components/locatorproxy/impl/IONADeploymentFormatter.xslt"));
        try {
            transformer = TransformerFactory.newInstance().newTransformer(styleSheet);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            transformer = null;
        }
    }

    /**
     * Reformats the locator deployment descriptor in a way that it is accepted by the locator
     * proxy.
     * 
     * @param config
     *        the locator configuration as defined for SOPware
     * 
     * @return the locator configuration as expected by IONA
     * 
     * @throws InfrastructureRuntimeException
     *         if the access to the stylesheet was not possible or the tranformer could not be
     *         instantiated.
     */
    static public String formatDeployment(final String config) throws InfrastructureRuntimeException {
        if (transformer == null)
            throw new InfrastructureRuntimeException("No transformer available to be applied on the locator configuration");
        StringWriter resultWriter = new StringWriter();
        StreamResult output = new StreamResult(resultWriter);
        StreamSource input = new StreamSource(new StringReader(config));

        try {
            synchronized (styleSheet) {
                transformer.setParameter("unique", new Integer(counter++));
                transformer.transform(input, output);
            }
        } catch (Exception e) {
            throw new InfrastructureRuntimeException("could not process locator configuration ", e);
        }

        return resultWriter.toString();
    }

    /**
     * Instantiates a new iona deployment formatter.
     */
    private IonaDeploymentFormatter() {
    }

}
