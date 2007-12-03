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
package org.eclipse.swordfish.core.components.helpers;

import java.lang.reflect.Constructor;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * A factory for creating InternalInfrastructureHelper objects.
 */
public class InfrastructureHelperFactory implements ExtensionFactory {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(InfrastructureHelperFactory.class);

    /** The impl class name. */
    private final String implClassName = "org.eclipse.swordfish.core.components.helpers.impl.InfrastructureHelperImpl";

    /** The instance. */
    private Object instance = null;

    /** The registry URL. */
    private String registryURL = null;

    /** The secure registry URL. */
    private String secureRegistryURL = null;

    /** The use secure registry URL. */
    private boolean useSecureRegistryURL = false;

    /** The configuration URL. */
    private String configurationURL = null;

    /**
     * Destroy.
     */
    public void destroy() {
        this.instance = null;
    }

    /**
     * Gets the configuration URL.
     * 
     * @return the configuration URL
     */
    public String getConfigurationURL() {
        return this.configurationURL;
    }

    /**
     * {@inheritDoc}
     */
    public Object getInstance(final String diversifier) {
        if (null == this.instance) {
            this.instance = this.createInstance();
        }
        return this.instance;
    }

    /**
     * Gets the registry URL.
     * 
     * @return the registry URL
     */
    public String getRegistryURL() {
        return this.registryURL;
    }

    /**
     * Gets the secure registry URL.
     * 
     * @return the secure registry URL
     */
    public String getSecureRegistryURL() {
        return this.secureRegistryURL;
    }

    /**
     * Checks if is use secure registry URL.
     * 
     * @return true, if is use secure registry URL
     */
    public boolean isUseSecureRegistryURL() {
        return this.useSecureRegistryURL;
    }

    /**
     * Sets the configuration URL.
     * 
     * @param configurationURL
     *        the new configuration URL
     */
    public void setConfigurationURL(final String configurationURL) {
        this.configurationURL = configurationURL;
    }

    /**
     * Sets the registry URL.
     * 
     * @param registryURL
     *        the new registry URL
     */
    public void setRegistryURL(final String registryURL) {
        this.registryURL = registryURL;
    }

    /**
     * Sets the secure registry URL.
     * 
     * @param secureRegistryURL
     *        the new secure registry URL
     */
    public void setSecureRegistryURL(final String secureRegistryURL) {
        this.secureRegistryURL = secureRegistryURL;
    }

    /**
     * Sets the use secure registry URL.
     * 
     * @param useSecureRegistryURL
     *        the new use secure registry URL
     */
    public void setUseSecureRegistryURL(final boolean useSecureRegistryURL) {
        this.useSecureRegistryURL = useSecureRegistryURL;
    }

    /**
     * Creates a new InternalInfrastructureHelper object.
     * 
     * @return the object
     */
    private Object createInstance() {
        Object retObject = null;
        try {
            Class implClass = this.getClass().getClassLoader().loadClass(this.implClassName);
            Constructor constructor = implClass.getConstructor(new Class[] {String.class, String.class});
            if (this.isUseSecureRegistryURL()) {
                retObject = constructor.newInstance(new Object[] {this.configurationURL, this.secureRegistryURL});
            } else {
                retObject = constructor.newInstance(new Object[] {this.configurationURL, this.registryURL});
            }
        } catch (Exception e) {
            LOG.error("Error while loading class " + this.implClassName, e);
        }
        return retObject;
    }

}
