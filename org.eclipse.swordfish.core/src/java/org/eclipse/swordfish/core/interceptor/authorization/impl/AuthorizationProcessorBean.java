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
package org.eclipse.swordfish.core.interceptor.authorization.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509IssuerSerial;
import org.apache.xml.security.signature.XMLSignature;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.authorization.AuthorizationProcessor;
import org.eclipse.swordfish.core.interceptor.security.AssertionRefreshPolicy;
import org.eclipse.swordfish.core.interceptor.security.MessageExchangeHelper;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.opensaml.SAMLAction;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthorizationDecisionQuery;
import org.opensaml.SAMLAuthorizationDecisionStatement;
import org.opensaml.SAMLNameIdentifier;
import org.opensaml.SAMLRequest;
import org.opensaml.SAMLResponse;
import org.opensaml.SAMLSubject;
import org.w3c.dom.Element;
import com.opensymphony.oscache.base.Cache;

/**
 * The Class AuthorizationProcessorBean.
 */
public class AuthorizationProcessorBean extends AbstractProcessingComponent implements AuthorizationProcessor {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(AuthorizationProcessorBean.class);

    /** default resource mapper classname. */
    private static final String DEFAULT_RESOURCE_MAPPER_CLASS =
            "org.eclipse.swordfish.core.interceptor.authorization.impl.DefaultResourceMapper";

    /** default resource action. */
    private static final String RESOURCE_ACTOIN = "Execute";

    public static String getResourceAction() {
        return RESOURCE_ACTOIN;
    }

    /** assertion cache. */
    private Cache assertionCache = null;

    /** consumer auth stratergy. */
    private ConsumerAuthorizationStratergy consumerAuthorization = null;

    /** provider auth stratergy. */
    private ProviderAuthorizationStratergy providerAuthorization = null;

    /** base config dir. */
    private String baseDir;

    /** component context. */
    private ComponentContextAccess componentContextAccess;

    /** message exchange helper. */
    private MessageExchangeHelper exchangeHelper = null;

    /** client verification flag. */
    private boolean clientVerification = false;

    /** client expiry flag. */
    private boolean clientExpiry = false;

    /** resource mapper class. */
    private String resourceMapperClass = null;

    /** key store file. */
    private String keyStoreFile = null;

    /** key store type. */
    private String keyStoreType = null;

    /** key store password. */
    private String keyStorePassword = null;

    /** policy id for calling authorization service. */
    private String authorizationServicePolicyID = null;

    /** resource mapper impl. */
    private ResourceMapper resourceMapperImpl = null;

    /** ref to current message exchange. */
    private MessageExchange msgExchange = null;

    /** reference to internal proxy. */
    private InternalProxy internalProxy = null;

    /** Authorization Service Qname. */
    private String authorizationServiceName = null;

    /** Authorization service operation name. */
    private String authorizationOperationName = null;

    /** expiry clearance. */
    private long expiryClearance = 0;

    /** keystore. */
    private KeyStore keyStore = null;

    /**
     * (non-Javadoc).
     * 
     * @param assertions
     *        the assertions
     * 
     * @return true, if can handle
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #canHandle(org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        return true;
    }

    public Cache getAssertionCache() {
        return this.assertionCache;
    }

    public String getAuthorizationServicePolicyID() {
        return this.authorizationServicePolicyID;
    }

    /**
     * Gets the content action.
     * 
     * @return the content action
     * 
     * @see org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent#getContentAction()
     */
    @Override
    public ContentAction getContentAction() {
        return ContentAction.NONE;
    }

    public MessageExchangeHelper getExchangeHelper() {
        return this.exchangeHelper;
    }

    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    public String getKeyStoreFile() {
        return this.keyStoreFile;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    public String getResourceMapperClass() {
        return this.resourceMapperClass;
    }

    public ResourceMapper getResourceMapperImpl() {
        return this.resourceMapperImpl;
    }

    /**
     * Gets the supported sources.
     * 
     * @return the supported sources
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#getSupportedSources()
     */
    @Override
    public Class[] getSupportedSources() {
        return new Class[] {DOMSource.class, StreamSource.class, SAXSource.class};
    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        this.msgExchange = context;
        LOG.info("handling fault for Authorization interceptor.");
        try {
            NormalizedMessage nmMessage = null;
            if (role.equals(Role.SENDER)) {
                nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
            } else if (role.equals(Role.RECEIVER)) {
                nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
            }
            this.exchangeHelper.removeAzAssertionFromExchange(nmMessage);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }

    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     * @throws InternalAuthorizationException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, InternalAuthorizationException {
        this.msgExchange = context;
        long beforeTime = System.currentTimeMillis();
        Iterator it = assertions.iterator();
        PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
        String location = this.getAttribute(assertion, "location", "provider");
        try {
            NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
            if (role.equals(Role.SENDER) && "both".equalsIgnoreCase(location)) {
                if (this.consumerAuthorization == null) {
                    this.consumerAuthorization = new ConsumerAuthorizationStratergy(this);
                }

                this.consumerAuthorization.handle(nmMessage, role, assertions);
                LOG.info("Authorization successfully performed on consumer!!");
            } else if (role.equals(Role.RECEIVER)) {
                if (this.providerAuthorization == null) {
                    this.providerAuthorization = new ProviderAuthorizationStratergy(this);
                }
                this.providerAuthorization.handle(nmMessage, role, assertions);
                LOG.info("Verification for Authorization successfully performed on provider!!");
            }
        } catch (InternalAuthorizationException ae) {
            throw ae;
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Authorization took " + (System.currentTimeMillis() - beforeTime) + " ms.");
        }

    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertions
     *        the assertions
     * 
     * @throws InternalSBBException
     * @throws InternalAuthorizationException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse
     *      (javax.jbi.messaging.MessageExchange, org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, InternalAuthorizationException {
        return;
        /*
         * msgExchange = context; long beforeTime = System.currentTimeMillis(); try {
         * NormalizedMessage nmMessage = getCurrentNormalizedMessage(context, Scope.RESPONSE); if
         * (role.equals(Role.SENDER)) { if (providerAuthorization == null) { providerAuthorization =
         * new ProviderAuthorizationStratergy( this); } providerAuthorization.handle(nmMessage,
         * role, assertions);
         * 
         * log .info("Verification for Authorization successfully performed on consumer!!"); } else
         * if (role.equals(Role.RECEIVER)) { if (consumerAuthorization == null) {
         * consumerAuthorization = new ConsumerAuthorizationStratergy( this); }
         * consumerAuthorization.handle(nmMessage, role, assertions); log.info("Authorization
         * successfully performed on provider!!"); } } catch (InternalAuthorizationException ae) {
         * throw ae; } catch (InternalSBBException se) { throw se; } catch (Exception e) { throw new
         * InternalSBBException(e); } finally { log.info("Authorization took " +
         * (System.currentTimeMillis() - beforeTime) + " ms."); }
         */
    }

    /**
     * initializing method.
     * 
     * @throws Exception
     *         exception
     */
    public void init() throws Exception {

        if (this.resourceMapperClass.trim().length() == 0) {
            this.resourceMapperClass = DEFAULT_RESOURCE_MAPPER_CLASS;
            LOG.info("Using default implementation for the resource mapper");
        }

        Class mapperClass = null;

        try {
            mapperClass = Class.forName(this.resourceMapperClass);
        } catch (ClassNotFoundException cnfe) {
            // if this exception is caught, dont throw it, use the default
            // class.
            LOG.info("ClassNotFound", cnfe);
            LOG.info("Using default implementation for the resource mapper");
            this.resourceMapperClass = DEFAULT_RESOURCE_MAPPER_CLASS;
            mapperClass = Class.forName(this.resourceMapperClass);
        }

        Object obj = mapperClass.newInstance();

        if (!(obj instanceof ResourceMapper))
            throw new Exception("The Authorization mapper class configured should implement" + DEFAULT_RESOURCE_MAPPER_CLASS
                    + "interface.");
        this.resourceMapperImpl = (ResourceMapper) obj;

        this.exchangeHelper = new MessageExchangeHelper();

        this.keyStoreType.toCharArray();
        char[] akeyStorePass = this.keyStorePassword.toCharArray();

        this.keyStore = KeyStore.getInstance(new String(this.keyStoreType));
        FileInputStream fis =
                new FileInputStream(this.componentContextAccess.getInstallRoot() + File.separator + this.baseDir + File.separator
                        + this.keyStoreFile);
        this.keyStore.load(fis, akeyStorePass);
        fis.close();

    }

    public boolean isClientExpiry() {
        return this.clientExpiry;
    }

    public boolean isClientVerification() {
        return this.clientVerification;
    }

    /**
     * Sets the assertion cache.
     * 
     * @param assertionCache
     *        cache
     */
    public void setAssertionCache(final Cache assertionCache) {
        this.assertionCache = assertionCache;
    }

    /**
     * Sets the authorization operation name.
     * 
     * @param authorizationOperationName
     *        operation name
     */
    public void setAuthorizationOperationName(final String authorizationOperationName) {
        this.authorizationOperationName = authorizationOperationName;
    }

    /**
     * Sets the authorization service name.
     * 
     * @param authorizationServiceName
     *        service name
     */
    public void setAuthorizationServiceName(final String authorizationServiceName) {
        this.authorizationServiceName = authorizationServiceName;
    }

    /**
     * Sets the authorization service policy ID.
     * 
     * @param authorizationServicePolicyID
     *        policy id
     */
    public void setAuthorizationServicePolicyID(final String authorizationServicePolicyID) {
        this.authorizationServicePolicyID = authorizationServicePolicyID;
    }

    /**
     * Sets the authorized enviornment.
     * 
     * @param nm
     *        normalized message
     * @param assertion
     *        assertions
     * 
     * @throws Exception
     *         exception
     */
    public void setAuthorizedEnviornment(final NormalizedMessage nm, final SAMLAssertion assertion) throws Exception {
        this.exchangeHelper.setAzAssertionInExchange(nm, assertion.toDOM());
    }

    /**
     * Sets the authorized enviornment and cache.
     * 
     * @param nm
     *        message
     * @param samlAssertion
     *        assertion
     * @param keyForCache
     *        key for cache
     * 
     * @throws Exception
     *         exception
     */
    public void setAuthorizedEnviornmentAndCache(final NormalizedMessage nm, final SAMLAssertion samlAssertion,
            final String keyForCache) throws Exception {
        this.assertionCache.putInCache(keyForCache, samlAssertion, new AssertionRefreshPolicy());
        this.setAuthorizedEnviornment(nm, samlAssertion);
    }

    /**
     * Sets the base dir.
     * 
     * @param baseDir
     *        base dir
     */
    public void setBaseDir(final String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the client expiry.
     * 
     * @param clientExpiry
     *        expity check
     */
    public void setClientExpiry(final boolean clientExpiry) {
        this.clientExpiry = clientExpiry;
    }

    /**
     * Sets the client verification.
     * 
     * @param clientVerification
     *        client verification
     */
    public void setClientVerification(final boolean clientVerification) {
        this.clientVerification = clientVerification;
    }

    /**
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        context
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    public void setExchangeHelper(final MessageExchangeHelper exchangeHelper) {
        this.exchangeHelper = exchangeHelper;
    }

    /**
     * Sets the expiry clearance.
     * 
     * @param expiryClearance
     *        clearance
     */
    public void setExpiryClearance(final long expiryClearance) {
        this.expiryClearance = expiryClearance;
    }

    /**
     * Sets the internal proxy.
     * 
     * @param internalProxy
     *        proxy
     */
    public void setInternalProxy(final InternalProxy internalProxy) {
        this.internalProxy = internalProxy;
    }

    public void setKeyStore(final KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Sets the key store file.
     * 
     * @param keyStoreFile
     *        keystore
     */
    public void setKeyStoreFile(final String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * Sets the key store password.
     * 
     * @param keyStorePassword
     *        keystore password
     */
    public void setKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Sets the key store type.
     * 
     * @param keyStoreType
     *        leystore type
     */
    public void setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    /**
     * Sets the resource mapper class.
     * 
     * @param resourceMapperClass
     *        resource mapper class
     */
    public void setResourceMapperClass(final String resourceMapperClass) {
        this.resourceMapperClass = resourceMapperClass;
    }

    public void setResourceMapperImpl(final ResourceMapper resourceMapperImpl) {
        this.resourceMapperImpl = resourceMapperImpl;
    }

    /**
     * Are assertions valid.
     * 
     * @param samlAssertion
     *        saml assertion
     * @param resource
     *        resource
     * @param chkSignature
     *        signature
     * @param chkExpiry
     *        expiry check
     * 
     * @return boolean result
     * 
     * @throws InternalAuthorizationException
     * @throws InternalSBBException
     */
    protected boolean areAssertionsValid(final SAMLAssertion samlAssertion, final String resource, final boolean chkSignature,
            final boolean chkExpiry) throws InternalAuthorizationException, InternalSBBException {

        // check for the authorization decision
        this.checkDecision(samlAssertion, resource);

        // check saml expiry
        if (chkExpiry) {
            if (this.isExpired(samlAssertion)) throw new InternalAuthorizationException("Authorization assertions are expired.");
        }

        // check signature
        if (chkSignature) {
            this.checkSignature(samlAssertion);
        }

        return true;
    }

    /**
     * Authorize.
     * 
     * @param principal
     *        principal
     * @param resource
     *        resource
     * @param action
     *        action
     * 
     * @return SAMLAssertion saml assertions
     * 
     * @throws InternalSBBException
     */
    protected SAMLAssertion authorize(final String principal, final String resource, final String action)
            throws InternalSBBException {
        try {
            String samlRequest = this.generateSAMLRequest(principal, resource, action);

            String samlResponse =
                    this.internalProxy.invokeService(QName.valueOf(this.authorizationServiceName), this.authorizationOperationName,
                            samlRequest);

            if (samlResponse == null)
                throw new InternalInfrastructureException("SAMLResponse received from Authorization Service is null.");

            SAMLAssertion assertion = this.getAssertionsFromResponse(samlResponse);
            return assertion;
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }
    }

    /**
     * Generate SAML request.
     * 
     * @param principal
     *        user name
     * @param resource
     *        resource name
     * @param action
     *        resource action
     * 
     * @return String saml request
     * 
     * @throws Exception
     *         exception
     */
    protected String generateSAMLRequest(final String principal, final String resource, final String action) throws Exception {
        String pName = "uid=" + principal;
        SAMLNameIdentifier identifier =
                new SAMLNameIdentifier(pName, "", "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
        SAMLSubject subject = new SAMLSubject();
        subject.setName(identifier);
        SAMLAuthorizationDecisionQuery decisionQuery = new SAMLAuthorizationDecisionQuery();
        decisionQuery.addAction(new SAMLAction("", action));
        decisionQuery.setSubject(subject);
        decisionQuery.setResource(resource);
        SAMLRequest samlRequest = new SAMLRequest();
        samlRequest.setQuery(decisionQuery);
        return samlRequest.toString();
    }

    /**
     * Gets the assertions from response.
     * 
     * @param strSamlResponse
     *        saml response
     * 
     * @return SAMLAssertion saml assertions
     * 
     * @throws Exception
     *         exception
     */
    protected SAMLAssertion getAssertionsFromResponse(final String strSamlResponse) throws Exception {
        SAMLAssertion assertions = null;

        SAMLResponse samlResponse = null;
        ClassLoader bootClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(AuthorizationProcessorBean.class.getClassLoader());
            // TODO better use the DOM from the response Document
            // document.getDocumentElemenet();
            samlResponse = new SAMLResponse(new ByteArrayInputStream(strSamlResponse.getBytes()));
        } finally {
            Thread.currentThread().setContextClassLoader(bootClassLoader);
        }

        Iterator iterator = samlResponse.getAssertions();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            assertions = (SAMLAssertion) obj;
            break;
        }

        return assertions;
    }

    /**
     * Gets the certificate from key store.
     * 
     * @param bint
     *        certificate num
     * 
     * @return Certificate certificate
     * 
     * @throws Exception
     *         exception
     */
    protected Certificate getCertificateFromKeyStore(final BigInteger bint) throws Exception {

        Enumeration aliases = this.keyStore.aliases();
        String alias = null;
        Certificate certificate = null;
        String type = null;
        final String reqType = "X.509";
        X509Certificate x509certificate = null;
        BigInteger kbint = null;

        while (aliases.hasMoreElements()) {
            alias = (String) aliases.nextElement();
            certificate = this.keyStore.getCertificate(alias);
            if (certificate == null) {
                continue;
            }
            type = certificate.getType();
            if ((type != null) && type.equals(reqType)) {
                x509certificate = (X509Certificate) certificate;
                kbint = x509certificate.getSerialNumber();

                if ((kbint != null) && kbint.equals(bint)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Found the certificate in the keystore");
                    }
                    return certificate;
                }
            }

        }
        if (LOG.isDebugEnabled()) {
            LOG.info("Cannot find the certificate in the keystore");
        }
        return null;

    }

    /**
     * Gets the resource name.
     * 
     * @return String resource name
     */
    protected String getResourceName() {
        return this.resourceMapperImpl.getResourceID(this.msgExchange.getService().getNamespaceURI(), this.msgExchange.getService()
            .getLocalPart(), this.msgExchange.getOperation().getLocalPart());
    }

    /**
     * Check decision.
     * 
     * @param samlAssertion
     *        assertion
     * @param resource
     *        resource
     * @throws InternalAuthorizationException
     * @throws InternalSBBException
     */
    void checkDecision(final SAMLAssertion samlAssertion, final String resource) throws InternalAuthorizationException,
            InternalSBBException {

        SAMLAuthorizationDecisionStatement decision = null;
        Iterator iterator = samlAssertion.getStatements();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof SAMLAuthorizationDecisionStatement) {
                decision = (SAMLAuthorizationDecisionStatement) obj;
                break;
            }
        }

        if (decision == null)
            throw new InternalMessagingException("Could not retreive Authorization Decision statement from saml response.");

        if (resource.equals(decision.getResource())) {
            if (!decision.getDecision().equalsIgnoreCase("Permit"))
                throw new InternalAuthorizationException("Not authorized to access requested resource:" + resource);
        } else
            throw new InternalMessagingException("Requested resource not found in Authorization assertions:" + resource);
    }

    /**
     * Check signature.
     * 
     * @param samlAssertion
     *        assertion
     * 
     * @throws InternalAuthorizationException
     * @throws InternalSBBException
     */
    void checkSignature(final SAMLAssertion samlAssertion) throws InternalAuthorizationException, InternalSBBException {
        Object obj = samlAssertion.getNativeSignature();
        BigInteger certSerialNum = null;

        try {

            if ((obj != null) && (obj instanceof org.apache.xml.security.signature.XMLSignature)) {
                org.apache.xml.security.signature.XMLSignature sig = (org.apache.xml.security.signature.XMLSignature) obj;
                Element ele = sig.getElement();
                // we need to do this coz of opensaml, it doesnt give back the
                // keyinfo
                // even though its there
                sig = new XMLSignature(ele, null);
                KeyInfo keyinfo = sig.getKeyInfo();
                if ((keyinfo != null) && keyinfo.containsX509Data()) {
                    X509Data x509data = keyinfo.itemX509Data(0);
                    if ((x509data != null) && x509data.containsIssuerSerial()) {
                        XMLX509IssuerSerial issuerserial = x509data.itemIssuerSerial(0);
                        if (issuerserial != null) {
                            certSerialNum = issuerserial.getSerialNumber();
                        }
                    }
                }
            }

            Certificate cert = null;

            if (certSerialNum == null) {
                System.out.print("");
                // TODO get certificate from the keystore and verify the
                // assertions with them.
                // assume that certificate exists in the assertions, dont look
                // for serial number further.
                // samlAssertion.verify();
            } else {
                // get certificate from the keystore with specified serial
                // number.
                cert = this.getCertificateFromKeyStore(certSerialNum);
                if (cert == null)
                    throw new InternalConfigurationException("Cannot get Certificate with serial number " + certSerialNum
                            + " from the keystore.");
            }

        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }

    }

    /**
     * Checks if is expired.
     * 
     * @param samlAssertion
     *        assertion
     * 
     * @return boolean isexpired
     * 
     * @throws InternalAuthorizationException
     */
    boolean isExpired(final SAMLAssertion samlAssertion) throws InternalAuthorizationException {
        Date notBefore = samlAssertion.getNotBefore();
        Date notOnOrAfter = samlAssertion.getNotOnOrAfter();
        Date currentDate = new Date();

        DateFormat datePrintFormatter;
        datePrintFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        LOG.debug("Checking Expiry with following dates");
        LOG.debug("Using clearance " + this.expiryClearance + " minutes");
        LOG.debug("-----------------------------------------");
        LOG.debug("NotBeforeDate is " + datePrintFormatter.format(notBefore));
        LOG.debug("CurrentDate is " + datePrintFormatter.format(currentDate));
        LOG.debug("NotOnOrAfter is " + datePrintFormatter.format(notOnOrAfter));
        LOG.debug("-----------------------------------------");

        boolean notBeforeResult = notBefore.before(currentDate);
        boolean notAfterResult = notOnOrAfter.after(currentDate);

        long temp = notOnOrAfter.getTime() - notBefore.getTime();

        if (temp < this.expiryClearance) {
            LOG.warn("You have set the clearance value larger than the lifetime of the assertions." + " Please take a note of it.");
        }

        // if the currentDate is earlier than the notBefore date.
        if (!notBeforeResult) {
            if (((notBefore.getTime() - currentDate.getTime()) / 60000) <= this.expiryClearance) {
                LOG.info("Using clearance and resulting true");
                notBeforeResult = true;
            }
        }

        // if the currentDate is later than the notOnOrAfter date
        if (!notAfterResult) {
            if (((currentDate.getTime() - notOnOrAfter.getTime()) / 60000) <= this.expiryClearance) {
                LOG.info("Using clearance and resulting true");
                notAfterResult = true;
            }
        }

        boolean validDate = notBeforeResult && notAfterResult;

        LOG.info("---Are Assertions Expired " + (!validDate) + "---");

        return !validDate;
    }
}
