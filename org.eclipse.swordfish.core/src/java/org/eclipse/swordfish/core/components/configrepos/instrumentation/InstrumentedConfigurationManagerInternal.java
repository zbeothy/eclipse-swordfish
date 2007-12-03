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
package org.eclipse.swordfish.core.components.configrepos.instrumentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;

/**
 * The Class InstrumentedConfigurationManagerInternal.
 * 
 */
public class InstrumentedConfigurationManagerInternal implements DisposableBean {

    /** The Constant NAMEDPROPERTY_VALUE_CONFIGURATION_MANAGER. */
    public static final String NAMEDPROPERTY_VALUE_CONFIGURATION_MANAGER = "ConfigurationManager";

    /** The Constant NAMEDPROPERTY_KEY_SCOPE_PATH. */
    public static final String NAMEDPROPERTY_KEY_SCOPE_PATH = "scopePath";

    /** The Constant NAMEDPROPERTY_KEY_TREEQUALIFIER. */
    public static final String NAMEDPROPERTY_KEY_TREEQUALIFIER = "treeQualifier";

    /** The Constant NAMEDPROPERTY_KEY_TYPE. */
    public static final String NAMEDPROPERTY_KEY_TYPE = "type";

    /** The Constant RESOURCE_READINGBUFFERSIZE. */
    private static final int RESOURCE_READINGBUFFERSIZE = 2048;

    /** Private logger. */
    private final static Log LOG = SBBLogFactory.getLog(InstrumentedConfigurationManagerInternal.class);

    // FIXME: We should use weak references here
    /** The manager. */
    private InstrumentationManager manager = null;

    /** The delegate. */
    private ConfigurationRepositoryManagerInternal delegate = null;

    /** The definition. */
    private Resource definition = null;

    /** JMX properties. */
    private Properties jmxProps;

    /**
     * The Constructor.
     * 
     * @param aManager
     *        to instrument
     */
    public InstrumentedConfigurationManagerInternal(final InstrumentationManager aManager) {
        super();
        this.manager = aManager;
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#close()
     */
    public void close() {
        throw new IllegalArgumentException("permission denied");
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        if ((null != this.delegate) && (null != this.manager)) {
            this.manager.unregisterInstrumentation(this);
        }
        this.delegate = null;
        this.manager = null;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the boot configuration
     * 
     * @throws ConfigurationRepositoryConfigException
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getBootConfiguration()
     */
    public Map getBootConfiguration() throws ConfigurationRepositoryConfigException {
        return this.configurationToMap((XMLConfiguration) this.delegate.getBootConfiguration());
    }

    /**
     * (non-Javadoc).
     * 
     * @param aPropertyKey
     *        the a property key
     * 
     * @return the configurable proxy property
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getConfigurableProxyProperty(java.lang.String)
     */
    public String getConfigurableProxyProperty(final String aPropertyKey) {
        throw new IllegalArgumentException("fetching configrepos-proxy properties is not implemented yet.");
    }

    /**
     * (non-Javadoc).
     * 
     * @return the configuration
     * 
     * @throws ConfigurationRepositoryConfigException
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getConfiguration(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.ScopePath)
     */
    public Map getConfiguration() throws ConfigurationRepositoryConfigException {
        XMLConfiguration cfg = (XMLConfiguration) this.delegate.getConfiguration(null, null);
        return this.configurationToMap(cfg);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the default tree qualifier
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getDefaultTreeQualifier()
     */
    public String getDefaultTreeQualifier() {
        return this.delegate.getDefaultTreeQualifier();
    }

    /**
     * Gets the definition.
     * 
     * @return Returns the definition.
     */
    public Resource getDefinition() {
        return this.definition;
    }

    /**
     * Gets the delegate.
     * 
     * @return Returns the delegate.
     */
    public ConfigurationRepositoryManagerInternal getDelegate() {
        return this.delegate;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the fixed scope path
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getFixedScopePath()
     */
    public ScopePath getFixedScopePath() {
        return this.delegate.getFixedScopePath();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the local resource base
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getLocalResourceBase()
     */
    public String getLocalResourceBase() {
        return this.delegate.getLocalResourceBase();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#getResource(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.ScopePath, java.lang.String,
     *      java.lang.String)
     */
    public byte[] getResource() throws ConfigurationRepositoryResourceException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = this.delegate.getResource(null, null, null, null);

        byte[] buff = new byte[InstrumentedConfigurationManagerInternal.RESOURCE_READINGBUFFERSIZE];
        int read = -1;
        try {
            while ((read = in.read(buff)) < 0) {
                out.write(buff, 0, read);
            }
        } catch (IOException e) {
            throw new ConfigurationRepositoryResourceException("Error fetching resource data", e);
        }

        return out.toByteArray();
    }

    /**
     * (non-Javadoc).
     * 
     * @return true, if is skip remote repository calls
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#isSkipRemoteRepositoryCalls()
     */
    public boolean isSkipRemoteRepositoryCalls() {
        return this.delegate.isSkipRemoteRepositoryCalls();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aScopePathString
     *        the a scope path string
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#resynchronize(java.lang.String)
     */
    public void resynchronize(final String aScopePathString) {
        this.delegate.resynchronize(aScopePathString);
    }

    /**
     * (non-Javadoc).
     * 
     * @param aPropertyKey
     *        the a property key
     * @param aValue
     *        the a value
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#setConfigurableProxyProperty(java.lang.String,
     *      java.lang.String)
     */
    public void setConfigurableProxyProperty(final String aPropertyKey, final String aValue) {
        throw new IllegalArgumentException("setting configrepos-proxy properties is not implemented yet.");
    }

    /**
     * Sets the definition.
     * 
     * @param definition
     *        The definition to set.
     */
    public void setDefinition(final Resource definition) {
        this.definition = definition;
    }

    /**
     * Sets the delegate.
     * 
     * @param delegate
     *        The delegate to set.
     */
    public void setDelegate(final ConfigurationRepositoryManagerInternal delegate) {
        if (null == this.definition) throw new IllegalArgumentException("missing mbean definition.");

        if ((null != this.delegate) && (null != this.manager)) {
            try {
                this.manager.unregisterInstrumentation(this);
            } catch (InternalInfrastructureException phe) {
                if ((null != LOG) && LOG.isErrorEnabled()) {
                    LOG.error("Failed while unregistering configuration manager delegate.");
                }
            }
        } else if (null == this.manager) {
            if ((null != LOG) && LOG.isErrorEnabled()) {
                LOG.error("No JMX infrastructure available for registering configuration manager");
            }
            return;
        }

        this.delegate = delegate;
        this.jmxProps = new Properties();
        this.jmxProps.setProperty(InstrumentedConfigurationManagerInternal.NAMEDPROPERTY_KEY_TYPE, "["
                + InstrumentedConfigurationManagerInternal.NAMEDPROPERTY_VALUE_CONFIGURATION_MANAGER + "]");
        this.jmxProps.setProperty(InstrumentedConfigurationManagerInternal.NAMEDPROPERTY_KEY_TREEQUALIFIER, "["
                + delegate.getDefaultTreeQualifier() + "]");
        ScopePath scope = delegate.getFixedScopePath();
        if (null != scope) {
            this.jmxProps.setProperty(InstrumentedConfigurationManagerInternal.NAMEDPROPERTY_KEY_SCOPE_PATH, "[" + scope.toString()
                    + "]");
        }

        ClassLoader oldloader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            this.manager.registerInstrumentation(this, this.definition.getInputStream(), this.jmxProps);
        } catch (InternalInfrastructureException phe) {
            throw new RuntimeException("error while registering instrumented configuration manager : " + phe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException("error while accessing mbean definition for configuration manager instrumentation : "
                    + ioe.getMessage());
        } catch (InternalAlreadyRegisteredException e) {
            // ignore
        } finally {
            Thread.currentThread().setContextClassLoader(oldloader);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aPath
     *        the a path
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#setLocalResourceBase(java.lang.String)
     */
    public void setLocalResourceBase(final String aPath) {
        this.delegate.setLocalResourceBase(aPath);
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#setParticipantId(org.eclipse.swordfish.papi.InternalParticipantIdentity)
     */
    public void setParticipantId(final InternalParticipantIdentity arg0) {
        // TODO i18n
        new IllegalArgumentException("permission denied.");
    }

    /**
     * (non-Javadoc).
     * 
     * @param arg0
     *        the arg0
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#setSkipRemoteRepositoryCalls(boolean)
     */
    public void setSkipRemoteRepositoryCalls(final boolean arg0) {
        this.delegate.setSkipRemoteRepositoryCalls(arg0);
    }

    /**
     * Configuration to map.
     * 
     * @param cfg
     *        is the configuration object which should be transformed
     * 
     * @return Hashmap with the configuration values in a hashmap
     */
    private TreeMap configurationToMap(final XMLConfiguration cfg) {
        TreeMap result = new TreeMap();
        JXPathContext ctx = JXPathContext.newContext(cfg.getDocument());
        Iterator iter = ctx.iteratePointers("//*[normalize-space(./text()) != ''] | //attribute::*");
        while (iter.hasNext()) {
            Pointer key = (Pointer) iter.next();
            result.put(key.asPath(), key.getValue());
        }
        return result;
    }
}
