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
package org.eclipse.swordfish.configrepos.configuration.sources;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryRemoteException;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryRemoteConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQuery;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfiguration;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.ConfigurationQueryImpl;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.GetConfigurationImpl;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The Class RemoteConfigurationSource.
 * 
 */
public class RemoteConfigurationSource extends AbstractConfigurationSource {

    /** Schemaname. */
    private String schemaName = null;

    /** W3C DocumentBuilder. */
    private DocumentBuilder docbuilder = null;

    /** The proxy which should be used. */
    private RemoteConfigSourceProxy proxy = null;

    /**
     * Default constructor.
     */
    public RemoteConfigurationSource() {
        super();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            this.docbuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            this.docbuilder = null;
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#close()
     */
    @Override
    public void close() {
        this.proxy = null;
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().log(Level.FINEST, "destroy " + this.getBeanName());
        }

        this.proxy = null;
        this.docbuilder = null;

        super.destroy();
    }

    /**
     * Fetch the configuration for a specific identifier from the remote source.
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * 
     * @return the configuration
     * 
     * @throws ConfigurationRepositoryConfigException
     *         the configuration repository config exception
     * 
     * @see org.eclipse.swordfish.configrepos.sources.ConfigurationSource#getConfiguration(ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        XMLConfiguration result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(RemoteConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }
        try {
            if (null == this.proxy)
                // TODO i18n
                throw new ConfigurationRepositoryConfigException("remote configuration source was not defined.");
            else {
                // Extract the participant identity from the scopepath
                String applicationID =
                        ScopePathUtil.findValueInScopePath(aScopePath, ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_APPLICATION);

                String instanceID =
                        ScopePathUtil.findValueInScopePath(aScopePath, ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_INSTANCE);
                try {
                    Node payload =
                            this.proxy.getConfigurationData(applicationID, instanceID,
                                    this.compileRequest(aTreeQualifier, aScopePath)).getElementsByTagName("stringResponse").item(0);

                    if (null == payload) // TODO i18n
                        throw new ConfigurationRepositoryConfigException("Unexpected payload response type");

                    result = new XMLConfiguration();
                    String pl = payload.getFirstChild().getNodeValue();
                    // String regex = "sopcs:name=\"default\"";
                    // String newpl = pl.replaceFirst(regex, "");
                    result.load(new ByteArrayInputStream(pl.getBytes()));
                    result.setFileName(this.proxy.getRepositoryIdentifier());

                    if ((null != this.getSchemasource()) && (null != this.getValidator()) && (null != this.schemaName)) {
                        this.validateConfiguration(aTreeQualifier, aScopePath, result, this.schemaName);
                    }

                    return result;
                } catch (ConfigurationRepositoryRemoteException crre) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(
                                ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_REMOTECALLEXCEPTION, new Object[] {crre});
                    }
                    throw new ConfigurationRepositoryConfigException(new ConfigurationRepositoryRemoteConfigException(crre
                        .getCause()));
                } catch (Exception e) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(
                                ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {e});
                    }
                    throw new ConfigurationRepositoryConfigException("Invocation exception:", e);
                }
            }
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(RemoteConfigurationSource.class.getName(), "getConfiguration", result);
            }
        }
    }

    /**
     * Gets the proxy.
     * 
     * @return Returns the proxy.
     */
    public RemoteConfigSourceProxy getProxy() {
        return this.proxy;
    }

    /**
     * Gets the schema name.
     * 
     * @return Returns the schemaName.
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     * Flush any transient data for a specific instance id or all instances.
     * 
     * @param aInstance
     *        the a instance
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#resychronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aInstance) {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(RemoteConfigurationSource.class.getName(), "resynchronize", new Object[] {aInstance});
        }
        // proxy.
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().exiting(RemoteConfigurationSource.class.getName(), "resynchronize");
        }
    }

    /**
     * Sets the proxy.
     * 
     * @param proxy
     *        The proxy to set.
     */
    public void setProxy(final RemoteConfigSourceProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Sets the schema name.
     * 
     * @param schemaName
     *        The schemaName to set.
     */
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Compile a query DOM object.
     * 
     * @param aTreeQualifier
     *        which defines the tree which should contain the configuration
     * @param aScopePath
     *        defining the path the query should take through the scope hierarchie
     * 
     * @return the document containing the query
     * 
     * @throws ParserConfigurationException
     *         in case the request could not be compiled
     */
    private Document compileRequest(final String aTreeQualifier, final ScopePath aScopePath) throws ParserConfigurationException {

        ConfigurationQuery query = new ConfigurationQueryImpl();
        query.setScopePath(aScopePath);
        query.setTree(aTreeQualifier);
        GetConfiguration request = new GetConfigurationImpl();
        request.setConfigurationQuery(query);

        Document doc = this.docbuilder.newDocument();
        request.marshal(doc);

        return doc;
    }
}
