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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkType;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCache;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.LEDistance;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import com.ibm.wsdl.util.xml.DOMUtils;

/**
 * The Class SrProxyCacheBean.
 */
public class SrProxyCacheBean implements SrProxyCache {

    // constants
    /** The Constant VALID_EXTENSIONS. */
    public static final String[] VALID_EXTENSIONS = {"sdx", "spdx", "agreedpolicy", "policy"};

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(SrProxyCacheBean.class);

    // spring dependency injection points
    /** The component context access. */
    private ComponentContextAccess componentContextAccess;

    // spring configuration properties
    /** The base dir. */
    private String baseDir;

    /** The must have policies. */
    private boolean mustHavePolicies;

    /** The enabled. */
    private boolean enabled;

    // state

    /** The sdx map. */
    private Map sdxMap = Collections.synchronizedMap(new HashMap());

    /** The spdx map. */
    private Map spdxMap = Collections.synchronizedMap(new HashMap());

    /** The agreed policy cache. */
    private AgreedPolicyCache agreedPolicyCache = new AgreedPolicyCache();

    /** The uddi key map. */
    private Map uddiKeyMap = new HashMap();

    /** The definition helper. */
    private DefinitionHelper definitionHelper = null;

    /** The registry SDX. */
    private Resource registrySDX;

    /** The registry SPDX. */
    private Resource registrySPDX;

    /** The registry agreed policy. */
    private Resource registryAgreedPolicy;

    /** The registry URL. */
    private String registryURL;

    /** The registry secure URL. */
    private String registrySecureURL;

    /** The use secure URL. */
    private boolean useSecureURL;

    // public methods

    /**
     * Instantiates a new sr proxy cache bean.
     * 
     * @throws Exception
     */
    public SrProxyCacheBean() throws Exception {
        this.definitionHelper = DefinitionHelper.getInstance();
        this.useSecureURL = false;
    }

    /**
     * Destroy.
     */
    public void destroy() {
        this.componentContextAccess = null;
        if (this.sdxMap != null) {
            this.sdxMap.clear();
            this.sdxMap = null;
        }
        if (this.spdxMap != null) {
            this.spdxMap.clear();
            this.spdxMap = null;
        }
        this.agreedPolicyCache.clear();

    }

    /**
     * Gets the agreed policy.
     * 
     * @param service
     *        the service
     * @param providerId
     *        the provider id
     * 
     * @return the agreed policyy
     */
    public AgreedPolicy getAgreedPolicy(final String service, final String providerId) {
        return ((null == providerId) || (null == service)) ? null : this.agreedPolicyCache.getAgreedPolicy(service, providerId);
    }

    /**
     * Gets the base dir.
     * 
     * @return the base dir
     */
    public String getBaseDir() {
        return this.baseDir;
    }

    /**
     * Gets the component context access.
     * 
     * @return the component context access
     */
    public ComponentContextAccess getComponentContextAccess() {
        return this.componentContextAccess;
    }

    public DefinitionHelper getDefinitionHelper() {
        return this.definitionHelper;
    }

    /**
     * Gets the policies.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the policiess
     */
    public List getPolicies(final String providerId) {
        // TODO implement!
        return new ArrayList();
    }

    /**
     * Gets the providers.
     * 
     * @param service
     *        the service
     * @param policyId
     *        the policy id
     * 
     * @return the providerss
     */
    public Map getProviders(final String service, final String policyId) {
        return ((null == policyId) || (null == service)) ? new HashMap() : this.agreedPolicyCache.getProviders(service, policyId);
    }

    /**
     * Gets the registry agreed policy.
     * 
     * @return the registry agreed policy
     */
    public Resource getRegistryAgreedPolicy() {
        return this.registryAgreedPolicy;
    }

    /**
     * Gets the registry SDX.
     * 
     * @return the registry SDX
     */
    public Resource getRegistrySDX() {
        return this.registrySDX;
    }

    /**
     * Gets the registry secure URL.
     * 
     * @return the registry secure URL
     */
    public String getRegistrySecureURL() {
        return this.registrySecureURL;
    }

    /**
     * Gets the registry SPDX.
     * 
     * @return the registry SPDX
     */
    public Resource getRegistrySPDX() {
        return this.registrySPDX;
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
     * Gets the SD.
     * 
     * @param service
     *        the service
     * 
     * @return the SD xx
     */
    public Definition getSDX(final String service) {
        return (null != service) ? (Definition) this.sdxMap.get(service) : null;
    }

    /**
     * Gets the SPD.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the SPD xx
     */
    public Definition getSPDX(final String providerId) {
        Definition def = (null != providerId) ? (Definition) this.spdxMap.get(providerId) : null;
        if ((def == null) && LOG.isDebugEnabled()) {
            LOG.debug("Did not hit " + providerId
                    + " in the service provider map, trying to find similar cache entries to figure out typos:");
            synchronized (this.spdxMap) {
                Iterator iter = this.spdxMap.keySet().iterator();
                while (iter.hasNext()) {
                    String str = iter.next().toString();
                    if (LEDistance.isAlike(providerId, str)) {
                        LOG.debug("Do you mean " + str + " instead of " + providerId);
                    }
                }
            }
        }
        return def;
    }

    /**
     * Gets the uddi key.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the uddi keyy
     */
    public String getUddiKey(final String providerId) {
        return (String) this.uddiKeyMap.get(providerId);
    }

    /**
     * Gets the use secure URL.
     * 
     * @return the use secure URL
     */
    public boolean getUseSecureURL() {
        return this.useSecureURL;
    }

    /**
     * Checks for SD .
     * 
     * @param service
     *        the service
     * 
     * @return true, if successful
     */
    public boolean hasSDX(final String service) {
        return (null != service) ? this.sdxMap.containsKey(service) : false;
    }

    /**
     * Checks for SPDX.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return true, if successful
     */
    public boolean hasSPDX(final String providerId) {
        return (null != providerId) ? this.spdxMap.containsKey(providerId) : false;
    }

    /**
     * Init.
     */
    public synchronized void init() {
        boolean cacheEnabled = this.isEnabled();

        if (!cacheEnabled) {
            this.setEnabled(true);
        }

        /*
         * put the very basic SR Definitions that are injected via Spring into the cache first.
         */
        try {
            Definition srSdxDefinition = this.definitionHelper.inputStreamTodefinition(this.getRegistrySDX().getInputStream());
            this.putSDX(srSdxDefinition);
        } catch (Exception e) {
            LOG.error("Error creating Service Registry SDX," + " remote registry will not be avilable.", e);
        }

        try {
            Definition srSpdxDefinition = this.definitionHelper.inputStreamTodefinition(this.getRegistrySPDX().getInputStream());
            this.replaceRegistryEndpoints(srSpdxDefinition, this.registryURL, this.registrySecureURL);
            this.putSPDX(srSpdxDefinition);
        } catch (Exception e) {
            LOG.error("Error creating Service Registry SPDX," + " remote registry will not be avilable.", e);
        }

        try {
            InputStream readFromThis = null;
            if (this.useSecureURL) {
                String policy = TransformerUtil.stringFromInputStream(this.getRegistryAgreedPolicy().getInputStream());
                String securePolicy = policy.replaceAll("HTTP", "HTTPS");
                readFromThis = new ByteArrayInputStream(securePolicy.getBytes());
            } else {
                readFromThis = this.getRegistryAgreedPolicy().getInputStream();
            }
            AgreedPolicy agreedPolicy = AgreedPolicyFactory.getInstance().createFrom(readFromThis);
            this.putAgreedPolicy(agreedPolicy);
        } catch (Exception e) {
            LOG.error("Error creating Service Registry agreed policy," + " remote registry will not be avilable.", e);
        }

        /*
         * get all other local stuff out of the file system
         */

        File srDir = new File(this.getAbsoluteBaseDir());
        if (!srDir.isDirectory()) return;

        String[] filesToLoad = srDir.list(new SrFileFilter(VALID_EXTENSIONS));
        for (int i = 0; i < filesToLoad.length; i++) {
            String fileToLoad = filesToLoad[i];
            String extension = fileToLoad.substring(fileToLoad.lastIndexOf('.') + 1).toLowerCase();
            if ("sdx".equals(extension)) {
                try {
                    Definition definition =
                            this.definitionHelper.fileTodefinition(this.getAbsoluteBaseDir() + File.separator + fileToLoad);
                    this.putSDX(definition);
                    LOG.debug("Loaded " + fileToLoad);
                } catch (Exception e) {
                    LOG.error("File " + fileToLoad + " is invalid (not loaded).", e);
                }
            } else if ("spdx".equals(extension)) {
                try {
                    Definition definition =
                            this.definitionHelper.fileTodefinition(this.getAbsoluteBaseDir() + File.separator + fileToLoad);
                    this.putSPDX(definition);
                    LOG.debug("Loaded " + fileToLoad);
                } catch (Exception e) {
                    LOG.error("File " + fileToLoad + " is invalid (not loaded).", e);
                }
            } else if ("agreedpolicy".equals(extension)) {
                try {
                    AgreedPolicy agreedPolicy =
                            AgreedPolicyFactory.getInstance().createFrom(
                                    new FileInputStream(this.getAbsoluteBaseDir() + File.separator + fileToLoad));
                    this.putAgreedPolicy(agreedPolicy);
                    LOG.debug("Loaded " + fileToLoad);
                } catch (Exception e) {
                    LOG.error("File " + fileToLoad + " is invalid (not loaded).", e);
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            this.agreedPolicyCache.dump();
        }
        this.setEnabled(cacheEnabled);
    }

    /**
     * Checks if is enabled.
     * 
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Checks if is must have policies.
     * 
     * @return true, if is must have policies
     */
    public boolean isMustHavePolicies() {
        return this.mustHavePolicies;
    }

    /**
     * Put agreed policy.
     * 
     * @param policy
     *        the policy
     */
    public void putAgreedPolicy(final AgreedPolicy policy) {
        if ((null != policy) && this.isEnabled()) {
            this.agreedPolicyCache.putAgreedPolicy(policy);
        }
    }

    /**
     * Put SDX.
     * 
     * @param definition
     *        the definition
     */
    public void putSDX(final Definition definition) {
        if ((null != definition) && this.isEnabled()) {
            /*
             * fix for 3491, try to figure out the service port out of the partner link type before
             * assuming the first port type is the service port type
             */
            PortType portType = null;
            QName qname = null;
            Iterator extensibilityElementIterator = definition.getExtensibilityElements().iterator();
            while (extensibilityElementIterator.hasNext()) {
                Object extensibilityElement = extensibilityElementIterator.next();
                if (extensibilityElement instanceof PartnerLinkType) {
                    PartnerLinkRole role = ((PartnerLinkType) extensibilityElement).getPartnerLinkRole("service");
                    portType = definition.getPortType(role.getPortTypeQName());
                }
            }

            // if we did not find a partnerlink describin the service desc,
            // we assume the first port type in the WSDL to be the service
            if (portType == null) {
                portType = ((PortType) definition.getPortTypes().values().iterator().next());
            }

            if (null != portType) {
                qname = portType.getQName();
                if (null != qname) {
                    this.sdxMap.put(qname.toString(), definition);
                }
            }
        }
    }

    /**
     * Put SPDX.
     * 
     * @param definition
     *        the definition
     */
    public void putSPDX(final Definition definition) {
        if (null != definition) {
            Service service = (Service) definition.getServices().values().iterator().next();
            if (null != service) {
                String key = service.getQName().toString();
                if (this.isEnabled()) {
                    this.spdxMap.put(key, definition);
                }
                String uddiKey = this.getUddiKeyFromService(service);
                if (null != uddiKey) {
                    this.uddiKeyMap.put(key, uddiKey);
                }
            } else {
                LOG.warn("Tried to store service provider description without Service element.");
            }
        }
    }

    /**
     * Removes the agreed policy.
     * 
     * @param policy
     *        the policy
     */
    public void removeAgreedPolicy(final AgreedPolicy policy) {
        if ((null != policy) && this.isEnabled()) {
            this.agreedPolicyCache.removeAgreedPolicy(policy);
        }
    }

    /**
     * Sets the base dir.
     * 
     * @param baseDir
     *        the new base dir
     */
    public void setBaseDir(final String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        the new component context access
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    public void setDefinitionHelper(final DefinitionHelper definitionHelper) {
        this.definitionHelper = definitionHelper;
    }

    /**
     * Sets the enabled.
     * 
     * @param enabled
     *        the new enabled
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the must have policies.
     * 
     * @param mustHavePolicies
     *        the new must have policies
     */
    public void setMustHavePolicies(final boolean mustHavePolicies) {
        this.mustHavePolicies = mustHavePolicies;
    }

    /**
     * Sets the registry agreed policy.
     * 
     * @param registryAgreedPolicy
     *        the new registry agreed policy
     */
    public void setRegistryAgreedPolicy(final Resource registryAgreedPolicy) {
        this.registryAgreedPolicy = registryAgreedPolicy;
    }

    /**
     * Sets the registry SDX.
     * 
     * @param registrySDX
     *        the new registry SDX
     */
    public void setRegistrySDX(final Resource registrySDX) {
        this.registrySDX = registrySDX;
    }

    /**
     * Sets the registry secure URL.
     * 
     * @param registrySecureURL
     *        the new registry secure URL
     */
    public void setRegistrySecureURL(final String registrySecureURL) {
        this.registrySecureURL = registrySecureURL;
    }

    /**
     * Sets the registry SPDX.
     * 
     * @param registrySPDX
     *        the new registry SPDX
     */
    public void setRegistrySPDX(final Resource registrySPDX) {
        this.registrySPDX = registrySPDX;
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
     * Sets the use secure URL.
     * 
     * @param useSecureURL
     *        the new use secure URL
     */
    public void setUseSecureURL(final boolean useSecureURL) {
        this.useSecureURL = useSecureURL;
    }

    // private methods
    /**
     * Gets the absolute base dir.
     * 
     * @return the absolute base dir
     */
    private String getAbsoluteBaseDir() {
        return this.componentContextAccess.getInstallRoot() + File.separator + this.baseDir;

    }

    /**
     * Gets the uddi key from service.
     * 
     * @param service
     *        the service
     * 
     * @return the uddi key from service
     */
    private String getUddiKeyFromService(final Service service) {
        List extEls = service.getExtensibilityElements();
        for (Iterator iter = extEls.iterator(); iter.hasNext();) {
            ExtensibilityElement element = (ExtensibilityElement) iter.next();
            if (element.getElementType().equals(new QName("http://types.sopware.org/registry/ServiceRegistry/1.0", "serviceKey"))) {
                // we did not registered a deserializer for this element, thus
                // this element should be an unknown extensibility element
                Element el = ((UnknownExtensibilityElement) element).getElement();
                return DOMUtils.getChildCharacterData(el);
            }
        }
        return null;
    }

    /**
     * Replace endpoind.
     * 
     * @param port
     *        the port
     * @param url
     *        the url
     */
    private void replaceEndpoind(final Port port, final String url) {
        List elms = port.getExtensibilityElements();
        for (Iterator iter = elms.iterator(); iter.hasNext();) {
            ExtensibilityElement elm = (ExtensibilityElement) iter.next();
            if (elm instanceof SOAPAddress) {
                SOAPAddress address = (SOAPAddress) elm;
                address.setLocationURI(url);
            }
        }
    }

    /**
     * Replace registry endpoints.
     * 
     * @param def
     *        the def
     * @param url
     *        the url
     * @param secureurl
     *        the secureurl
     */
    private void replaceRegistryEndpoints(final Definition def, final String url, final String secureurl) {
        Set entrySet = def.getServices().entrySet();
        // we assume there is at least one service, which is our registry
        // service
        Service srv = (Service) ((Entry) entrySet.iterator().next()).getValue();

        Port port = null;
        port = srv.getPort("ServiceRegistry_Http_Port");
        this.replaceEndpoind(port, url);

        port = srv.getPort("ServiceRegistry_Https_Port");
        this.replaceEndpoind(port, secureurl);
    }

    /**
     * Is responsible to cache agreed policies.
     */
    public static class AgreedPolicyCache {

        /**
         * To Q name.
         * 
         * @param uriName
         *        the uri name
         * 
         * @return the q name
         */
        private synchronized static QName toQName(final String uriName) {
            if ((null == uriName) || (uriName.trim().length() == 0)) return new QName("", "");
            if (uriName.trim().startsWith("{"))
                return QName.valueOf(uriName.trim());
            else {
                int pos = uriName.lastIndexOf("/");
                return new QName(uriName.substring(0, pos), uriName.substring(pos + 1));
            }
        }

        /**
         * Map to resolve policies for given metadata Content is service => (consumerPolicyId =>
         * (serviceProviderId => agreedPolicy)).
         */
        private Map policyMap = new HashMap();

        /**
         * Clear.
         */
        public synchronized void clear() {
            if (this.policyMap != null) {
                this.policyMap.clear();
                this.policyMap = null;
            }
        }

        /**
         * Gets the agreed policy.
         * 
         * @param service
         *        the service
         * @param providerId
         *        the provider id
         * 
         * @return the agreed policy
         */
        public synchronized AgreedPolicy getAgreedPolicy(final String service, final String providerId) {
            Map innerMap = (Map) this.policyMap.get(service);
            if (null == innerMap) return null;
            Collection values = innerMap.values();
            for (Iterator iter = values.iterator(); iter.hasNext();) {
                Map value = (Map) iter.next();
                if (value.containsKey(providerId)) return (AgreedPolicy) value.get(providerId);
            }
            return null;
        }

        /**
         * Gets the providers.
         * 
         * @param service
         *        the service
         * @param policyId
         *        the policy id
         * 
         * @return the providers
         */
        public synchronized Map getProviders(final String service, final String policyId) {
            Map innerMap = (Map) this.policyMap.get(service);
            /*
             * log diagnostics as we have such a trouble with this point
             */
            if ((innerMap == null) && LOG.isDebugEnabled()) {
                LOG.debug("Did not hit " + service
                        + " in the policy map, trying to find similar cache entries to figure out typos:");
                Iterator iter = this.policyMap.keySet().iterator();
                while (iter.hasNext()) {
                    String str = iter.next().toString();
                    if (LEDistance.isAlike(service, str)) {
                        LOG.debug("Do you mean " + str + " instead of " + service);
                    }
                }
            }
            /*
             * empty resultset instead of null
             */
            if (null == innerMap) return new HashMap();
            Map innermostMap = (Map) innerMap.get(policyId);

            if ((innermostMap == null) && LOG.isDebugEnabled()) {
                LOG.debug("Did not hit policyId " + policyId
                        + " in the cache, trying to find similar cache entries to figure out typos:");
                Iterator iter = innerMap.keySet().iterator();
                while (iter.hasNext()) {
                    String str = iter.next().toString();
                    if (LEDistance.isAlike(policyId, str)) {
                        LOG.debug("Do you mean " + str + " instead of " + policyId);
                    }
                }
            }

            return innermostMap;
        }

        /**
         * Put agreed policy.
         * 
         * @param policy
         *        the policy
         */
        public synchronized void putAgreedPolicy(final AgreedPolicy policy) {
            String key = toQName(policy.getService()).toString();
            if (!this.policyMap.containsKey(key)) {
                this.policyMap.put(key, new HashMap());
            }
            Map innerMap = (Map) this.policyMap.get(key);
            key = policy.getConsumerPolicyIdentity().getKeyName();
            if (!innerMap.containsKey(key)) {
                innerMap.put(key, new HashMap());
            }
            Map innermostMap = (Map) innerMap.get(key);
            innermostMap.put(toQName(policy.getProvider()).toString(), policy);
        }

        /**
         * Removes the agreed policy.
         * 
         * @param policy
         *        the policy
         */
        public synchronized void removeAgreedPolicy(final AgreedPolicy policy) {
            String key = toQName(policy.getService()).toString();
            Map innerMap = (Map) this.policyMap.get(key);
            if (innerMap != null) {
                key = policy.getConsumerPolicyIdentity().getKeyName();
                Map innermostMap = (Map) innerMap.get(key);
                if (innermostMap != null) {
                    innermostMap.remove(toQName(policy.getProvider()).toString());
                }
            }
        }

        /**
         * Dump.
         */
        private synchronized void dump() {
            LOG.debug("Service provider info");
            for (Iterator iter = this.policyMap.keySet().iterator(); iter.hasNext();) {
                String key1 = (String) iter.next();
                LOG.debug("  for service " + key1);
                Map map1 = (Map) this.policyMap.get(key1);
                if (null == map1) {
                    continue;
                }
                for (Iterator iterator = map1.keySet().iterator(); iterator.hasNext();) {
                    String key2 = (String) iterator.next();
                    LOG.debug("    for consumerId " + key2);
                    Map map2 = (Map) map1.get(key2);
                    for (Iterator iterator2 = map2.keySet().iterator(); iterator2.hasNext();) {
                        String key3 = (String) iterator2.next();
                        LOG.debug("      providerId " + key3);
                    }
                }
            }
        }
    }

    /**
     * The Class SrFileFilter.
     */
    private class SrFileFilter implements FilenameFilter {

        /** The valid extensions. */
        private List validExtensions = new ArrayList();

        /**
         * Instantiates a new sr file filter.
         * 
         * @param extensions
         *        the extensions
         */
        SrFileFilter(final String[] extensions) {
            for (int i = 0; i < extensions.length; i++) {
                this.validExtensions.add(extensions[i].trim().toLowerCase());
            }
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(final File fBaseDir, final String filename) {
            int separatorPos = filename.lastIndexOf('.');
            if (separatorPos <= 0) return false;
            String extension = filename.substring(separatorPos + 1).toLowerCase();
            return this.validExtensions.contains(extension);
        }
    }
}
