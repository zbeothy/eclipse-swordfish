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
package org.eclipse.swordfish.configrepos.resource.sources;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.configuration.sources.OverrideableConfigurationSource;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * The Class OverrideableResourceSource.
 * 
 */
public class OverrideableResourceSource extends AbstractResourceSource {

    /** List of sources which are being processed in order. */
    private List sources = null;

    /** The fixed scope path. */
    private ScopePath fixedScopePath;

    /** The fixed component. */
    private String fixedComponent;

    /** The fixed resource id. */
    private String fixedResourceId;

    /**
     * Instantiates a new overrideable resource source.
     */
    public OverrideableResourceSource() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.eclipse.swordfish.configrepos.AbstractRepositorySource#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().log(Level.FINEST, "destroy " + this.getBeanName());
        }

        this.sources = null;

        super.destroy();
    }

    /**
     * Gets the fixed component.
     * 
     * @return Returns the fixedComponent.
     */
    public String getFixedComponent() {
        return this.fixedComponent;
    }

    /**
     * Gets the fixed resource id.
     * 
     * @return Returns the fixedResourceId.
     */
    public String getFixedResourceId() {
        return this.fixedResourceId;
    }

    /**
     * Gets the fixed scope path.
     * 
     * @return Returns the fixedScopePath.
     */
    public ScopePath getFixedScopePath() {
        return this.fixedScopePath;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * @param aComponent
     *        the a component
     * @param aResourceIdentifier
     *        the a resource identifier
     * 
     * @return the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     *         the configuration repository resource exception
     * 
     * @see org.eclipse.swordfish.configrepos.resource.sources.ResourceSource#getResource(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath, java.lang.String,
     *      java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceIdentifier) throws ConfigurationRepositoryResourceException {
        super.pushBeanNameOnNDC();
        InputStream result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(OverrideableConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }
        try {
            Iterator iter = this.sources.iterator();
            ResourceSource cfgsrc = null;
            while (iter.hasNext()) {
                try {
                    cfgsrc = (ResourceSource) iter.next();
                    result =
                            cfgsrc.getResource(aTreeQualifier, (null != this.fixedScopePath ? this.fixedScopePath : aScopePath),
                                    (null != this.fixedComponent ? this.fixedComponent : aComponent),
                                    (null != this.fixedResourceId ? this.fixedResourceId : aResourceIdentifier));
                    return result;
                } catch (Exception e) {
                    if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                        this.getLogger().log(
                                Level.FINEST,
                                this.getApplicationContext().getMessage(
                                        "org.eclipse.swordfish.configrepos.resource.sources.RESOURCENOTAVAILABLE",
                                        new Object[] {cfgsrc.toString(), e.getLocalizedMessage(), e.getMessage()},
                                        Locale.getDefault()));
                    }
                }
            }
            String errmsg =
                    this.getApplicationContext().getMessage(
                            "org.eclipse.swordfish.configrepos.configuration.sources.NOSOURCEAVAILABLEEXCEPTION",
                            new Object[] {new Integer(this.sources.size())}, Locale.getDefault());
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.SEVERE)) {
                this.getLogger().severe(errmsg);
            }
            // TODO i18n
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE, null);
            }
            throw new ConfigurationRepositoryResourceException(errmsg);
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(OverrideableConfigurationSource.class.getName(), "getConfiguration", result);
            }
            super.popBeanNameFromNDC();
        }
    }

    /**
     * Gets the sources.
     * 
     * @return Returns the sources.
     */
    public List getSources() {
        return this.sources;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aInstance
     *        the a instance
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#resynchronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aInstance) {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(OverrideableResourceSource.class.getName(), "resynchronize", new Object[] {aInstance});
        }

        if ((null != this.sources) && !this.sources.isEmpty()) {
            Iterator iter = this.sources.iterator();
            while (iter.hasNext()) {
                ((ResourceSource) iter.next()).resynchronize(aInstance);
            }
        }

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().exiting(OverrideableResourceSource.class.getName(), "resynchronize");
        }
    }

    /**
     * Sets the fixed component.
     * 
     * @param fixedComponent
     *        The fixedComponent to set.
     */
    public void setFixedComponent(final String fixedComponent) {
        this.fixedComponent = fixedComponent;
    }

    /**
     * Sets the fixed resource id.
     * 
     * @param fixedResourceId
     *        The fixedResourceId to set.
     */
    public void setFixedResourceId(final String fixedResourceId) {
        this.fixedResourceId = fixedResourceId;
    }

    /**
     * Sets the fixed scope path.
     * 
     * @param fixedScopePath
     *        The fixedScopePath to set.
     */
    public void setFixedScopePath(final ScopePath fixedScopePath) {
        this.fixedScopePath = fixedScopePath;
    }

    /**
     * Sets the sources.
     * 
     * @param sources
     *        The sources to set.
     */
    public void setSources(final List sources) {
        this.sources = sources;
    }

}
