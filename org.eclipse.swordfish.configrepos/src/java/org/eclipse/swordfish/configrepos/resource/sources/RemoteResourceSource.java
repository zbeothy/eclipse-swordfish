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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryRemoteException;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryRemoteResourceException;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.GetResource;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.GetResourceImpl;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.ResourceQueryImpl;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The Class RemoteResourceSource.
 * 
 */
public class RemoteResourceSource extends AbstractResourceSource {

    /** Proxy which should be externally set and will provide the resource data as an InputStream. */
    private RemoteResourceSourceProxy proxy = null;

    /** W3C DocumentBuilder. */
    private DocumentBuilder docbuilder = null;

    /**
     * Instantiates a new remote resource source.
     */
    public RemoteResourceSource() {
        super();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            this.docbuilder = dbf.newDocumentBuilder();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#close()
     */
    @Override
    public void close() {
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

        this.proxy = null;
        this.docbuilder = null;

        super.destroy();
    }

    /**
     * Gets the proxy.
     * 
     * @return Returns the proxy.
     */
    public RemoteResourceSourceProxy getProxy() {
        return this.proxy;
    }

    /**
     * FIXME Qualifier not implemented yet.
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * @param aComponent
     *        the a component
     * @param aResourceName
     *        the a resource name
     * 
     * @return the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     *         the configuration repository resource exception
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#getConfiguration(java.lang.String,
     *      java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceName) throws ConfigurationRepositoryResourceException {
        InputStream result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(RemoteResourceSource.class.getName(), "getResource",
                    new Object[] {aTreeQualifier, aScopePath, aComponent, aResourceName});
        }

        try {
            if (null == this.proxy)
                // TODO i18n
                throw new ConfigurationRepositoryResourceException("remote resource source was not defined.");
            else {
                try {
                    // Extract the participant identity from the scopepath
                    String applicationID =
                            ScopePathUtil.findValueInScopePath(aScopePath,
                                    ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_APPLICATION);

                    String instanceID =
                            ScopePathUtil
                                .findValueInScopePath(aScopePath, ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_INSTANCE);
                    Node payload =
                            this.proxy.getResourceData(applicationID, instanceID,
                                    this.compileRequest(aTreeQualifier, aScopePath, aComponent, aResourceName))
                                .getElementsByTagName("resourceResponse").item(0);

                    if (null == payload) throw new ConfigurationRepositoryResourceException("unexpected payload response type");

                    if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                        this.getLogger().log(Level.FINEST, "org.eclipse.swordfish.configrepos.resource.sources.DUMP_RESOURCE",
                                payload.getNodeValue());
                    }

                    result = new ByteArrayInputStream(this.extractResourcePayload(payload));
                    return result;
                } catch (ConfigurationRepositoryRemoteException crre) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(
                                ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_REMOTECALLEXCEPTION, new Object[] {crre});
                    }
                    throw new ConfigurationRepositoryResourceException(new ConfigurationRepositoryRemoteResourceException(crre
                        .getCause()));
                } catch (Exception e) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(
                                ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {e});
                    }
                    throw new ConfigurationRepositoryResourceException("Invocation exception:", e);
                }
            }
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(RemoteResourceSource.class.getName(), "getResource", result);
            }
        }
    }

    /**
     * Flush any transient data for a specific instance id or all instances.
     * 
     * @param aInstanceId
     *        the a instance id
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#resychronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aInstanceId) {
        // Auto-generated method stub
    }

    /**
     * Sets the proxy.
     * 
     * @param proxy
     *        The proxy to set.
     */
    public void setProxy(final RemoteResourceSourceProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Compile a query DOM object.
     * 
     * @param aTreeQualifier
     *        which defines the tree which should contain the configuration
     * @param aScopePath
     *        defining the path the query should take through the scope hierarchie
     * @param aComponent
     *        which should be used a qualifier
     * @param aResourceId
     *        is the name of the resource
     * 
     * @return the document containing the query
     */
    private Document compileRequest(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceId) {

        ResourceQueryImpl query = new ResourceQueryImpl();
        query.setScopePath(aScopePath);
        query.setTree(aTreeQualifier);
        query.setResourceId(aResourceId);
        query.setComponentId(aComponent);
        GetResource request = new GetResourceImpl();
        request.setResourceQuery(query);

        Document doc = this.docbuilder.newDocument();
        request.marshal(doc);

        return doc;
    }

    /**
     * Extract the text payload from the node. Return a byte array of zero length if the node is
     * empty.
     * 
     * @param aPayload
     *        to be analyzed
     * 
     * @return byte array
     */
    private byte[] extractResourcePayload(final Node aPayload) {
        Node textchild = aPayload.getFirstChild();
        if (null == textchild) return new byte[0];
        String value = textchild.getNodeValue();
        if (null == value) return new byte[0];
        Base64 decodec = new Base64();
        return decodec.decode(value.getBytes());
    }
}
