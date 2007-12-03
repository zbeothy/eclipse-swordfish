/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.shared.validation;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * The Class ResourceResolver.
 * 
 */
public class ResourceResolver extends XMLCatalogResolver implements LSResourceResolver {

    /** The context class loader. */
    private static ThreadLocal contextClassLoader = new ThreadLocal();

    /** The log. */
    private Logger log;

    /** The external resolvers. */
    private ExternalResolver[] externalResolvers;

    /**
     * The Constructor.
     * 
     * @param pLog
     *        logger
     * @param urls
     *        catalog URIs
     */
    public ResourceResolver(final Logger pLog, final String[] urls) {
        super();
        this.log = pLog;
        this.setPreferPublic(false);
        this.setCatalogList(urls);
        this.log.finest("ResourceResolver created");
    }

    /**
     * Adds the external resolver.
     * 
     * @param externalResolver
     *        the external resolver
     */
    public void addExternalResolver(final ExternalResolver externalResolver) {
        if (this.externalResolvers == null) {
            this.externalResolvers = new ExternalResolver[0];
        }
        ExternalResolver[] newResolvers = new ExternalResolver[this.externalResolvers.length + 1];
        System.arraycopy(this.externalResolvers, 0, newResolvers, 0, this.externalResolvers.length);
        newResolvers[this.externalResolvers.length] = externalResolver;
        this.externalResolvers = newResolvers;
    }

    /**
     * Gets the context class loader.
     * 
     * @return the context class loader
     */
    public ClassLoader getContextClassLoader() {
        return (ClassLoader) ResourceResolver.contextClassLoader.get();
    }

    /**
     * <p>
     * Resolves a resource using the catalog. This method interprets that the namespace URI
     * corresponds to uri entries in the catalog. Where both a namespace and an external identifier
     * exist, the namespace takes precedence.
     * </p>
     * 
     * @param type
     *        the type of the resource being resolved
     * @param namespaceURI
     *        the namespace of the resource being resolved, or <code>null</code> if none was
     *        supplied
     * @param publicId
     *        the public identifier of the resource being resolved, or <code>null</code> if none
     *        was supplied
     * @param systemId
     *        the system identifier of the resource being resolved, or <code>null</code> if none
     *        was supplied
     * @param baseURI
     *        the absolute base URI of the resource being parsed, or <code>null</code> if there is
     *        no base URI
     * 
     * @return the LS input
     */
    @Override
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId,
            final String baseURI) {

        String resolvedId = null;

        try {
            // The namespace is useful for resolving namespace aware
            // grammars such as XML schema. Let it take precedence over
            // the external identifier if one exists.
            if (namespaceURI != null) {
                resolvedId = this.resolveURI(namespaceURI);
            }
            String systemIdUri = systemId;
            if (!this.getUseLiteralSystemId() && (baseURI != null)) {
                // Attempt to resolve the system identifier against the base
                // URI.
                try {
                    URI uri = new URI(new URI(baseURI), systemIdUri);
                    systemIdUri = uri.toString();
                    // Ignore the exception. Fallback to the literal system
                    // identifier.
                } catch (URI.MalformedURIException ex) {
                    ex.printStackTrace();
                }
            }

            // Resolve against an external identifier if one exists. This
            // is useful for resolving DTD external subsets and other
            // external entities. For XML schemas if there was no namespace
            // mapping we might be able to resolve a system identifier
            // specified as a location hint.
            if (resolvedId == null) {
                if ((publicId != null) && (systemIdUri != null)) {
                    resolvedId = this.resolvePublic(publicId, systemIdUri);
                } else if (systemIdUri != null) {
                    resolvedId = this.resolveSystem(systemIdUri);
                }
            }
            // Ignore IOException. It cannot be thrown from this method.
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (resolvedId != null) {
            if (this.isExternalResource(resolvedId))
                return this.resolveExternalResource(resolvedId);
            else
                return new DOMInputImpl(publicId, resolvedId, baseURI);
        }
        return null;
    }

    /**
     * Sets the context class loader.
     * 
     * @param contextClassLoader
     *        the new context class loader
     */
    public void setContextClassLoader(final ClassLoader contextClassLoader) {
        ResourceResolver.contextClassLoader.set(contextClassLoader);
    }

    /**
     * Unset context class loader.
     */
    public void unsetContextClassLoader() {
        ResourceResolver.contextClassLoader.set(null);
    }

    /**
     * Checks if is external resource.
     * 
     * @param uri
     *        the uri
     * 
     * @return true, if is external resource
     */
    private boolean isExternalResource(final String uri) {
        if (this.externalResolvers == null)
            return false;
        else {
            try {
                for (int i = 0; i < this.externalResolvers.length; i++) {
                    if (this.externalResolvers[i].isExternalResource(uri)) return true;
                }
            } catch (ResourceException e) {
                this.log.severe("Error classifying URI: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Resolve external resource.
     * 
     * @param uri
     *        the uri
     * 
     * @return the LS input
     */
    private LSInput resolveExternalResource(final String uri) {
        if (this.externalResolvers == null)
            return null;
        else {
            ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
            if (null != this.getContextClassLoader()) {
                Thread.currentThread().setContextClassLoader(this.getContextClassLoader());
            }
            try {
                for (int i = 0; i < this.externalResolvers.length; i++) {
                    if (this.externalResolvers[i].isExternalResource(uri)) {
                        LSInput result = this.externalResolvers[i].resolveExternalResource(uri);
                        if (null != result) return result;
                    }
                }
            } catch (ResourceException e) {
                this.log.severe("Error retrieving schema from URI: " + e.getMessage());
            } finally {
                Thread.currentThread().setContextClassLoader(currThreadLoader);
            }
            return null;
        }
    }

}
