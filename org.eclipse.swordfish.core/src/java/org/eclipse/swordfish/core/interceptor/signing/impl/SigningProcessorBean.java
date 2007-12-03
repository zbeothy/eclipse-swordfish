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
package org.eclipse.swordfish.core.interceptor.signing.impl;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Base64;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.security.MessageExchangeHelper;
import org.eclipse.swordfish.core.interceptor.security.xkms.XKMSDefinitions;
import org.eclipse.swordfish.core.interceptor.security.xkms.XKMSValidateRequest;
import org.eclipse.swordfish.core.interceptor.security.xkms.XKMSValidateResponse;
import org.eclipse.swordfish.core.interceptor.signing.SigningProcessor;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalSecurityException;

/**
 * The Class SigningProcessorBean.
 */
public class SigningProcessorBean extends AbstractProcessingComponent implements SigningProcessor {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(SigningProcessorBean.class);

    /** component context. */
    private ComponentContextAccess componentContextAccess = null;

    /** message exchamge helper. */
    private MessageExchangeHelper exchangeHelper = null;

    /** signature method URI. */
    private String signatureMethod = null;

    /** certificate alias. */
    private String certificateAlias = null;

    /** signer reference. */
    private MessageSigner signer = null;

    /** verifier reference. */
    private MessageVerifier verifier = null;

    /** base dir. */
    private String baseDir = null;

    /** key store. */
    private KeyStore keyStore = null;

    /** keystore file. */
    private String keyStoreFile = null;

    /** keystore type. */
    private String keyStoreType = null;

    /** keystore password. */
    private String keyStorePassword = null;

    /** private key password. */
    private String keyPassword = null;

    /** key alias. */
    private String keyAlias = null;

    /** internal proxy. */
    private InternalProxy internalProxy = null;

    /** xkms provider service name. */
    private String xkmsServiceName = null;

    /** flag for certificate check for issuer trust. */
    private boolean checkCertificateIssuerTrust = false;

    /** flag for certificate check for validity. */
    private boolean checkCertificateValidity = false;

    /** flag for certificate check for revocation. */
    private boolean checkCertificateRevocation = false;

    /** flag for certificate check for signature. */
    private boolean checkCertificateSignature = false;

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
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#
     *      canHandle(org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        return true;
    }

    /**
     * Gets the base dir.
     * 
     * @return String basedir
     */
    public String getBaseDir() {
        return this.baseDir;
    }

    /**
     * Gets the certificate alias.
     * 
     * @return String certificate alias
     */
    public String getCertificateAlias() {
        return this.certificateAlias;
    }

    /**
     * Gets the certificate chain.
     * 
     * @return Certificate[] certificate[]
     * 
     * @throws Exception
     *         exception
     */
    public Certificate[] getCertificateChain() throws Exception {
        return this.keyStore.getCertificateChain(this.certificateAlias);
    }

    /**
     * Gets the component context access.
     * 
     * @return ComponentContextAccess context
     */
    public ComponentContextAccess getComponentContextAccess() {
        return this.componentContextAccess;
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
        return ContentAction.READ;
    }

    /**
     * Gets the exchange helper.
     * 
     * @return MessageExchangeHelper exchange helper
     */
    public MessageExchangeHelper getExchangeHelper() {
        return this.exchangeHelper;
    }

    /**
     * Gets the key alias.
     * 
     * @return alias
     */
    public String getKeyAlias() {
        return this.keyAlias;
    }

    /**
     * Gets the key for signing.
     * 
     * @return private key for signing
     * 
     * @throws Exception
     *         exception
     */
    public Key getKeyForSigning() throws Exception {
        Key key = this.keyStore.getKey(this.keyAlias, this.keyPassword.toCharArray());
        return key;
    }

    /**
     * Gets the key for verifying.
     * 
     * @param xmlSignature
     *        smlsignature
     * 
     * @return Key key
     * 
     * @throws InternalSBBException
     * @throws InternalSecurityException
     */
    public Key getKeyForVerifying(final XMLSignature xmlSignature) throws InternalSBBException, SecurityException {

        try {
            XKMSValidateResponse response = null;
            X509Certificate cert = null;
            String strRequest = null;
            String strResponse = null;
            cert = null;
            KeyInfo keyinfo = xmlSignature.getKeyInfo();
            if ((keyinfo != null) && keyinfo.containsX509Data()) {
                X509Data x509data = keyinfo.itemX509Data(0);
                if ((x509data != null) && x509data.containsCertificate()) {
                    XMLX509Certificate xcert = x509data.itemCertificate(0);
                    cert = xcert.getX509Certificate();
                }
            }
            strRequest = this.createXKMSValidateRequest(cert.getEncoded());

            LOG.debug("Sending the following XKMS Request");
            LOG.debug(strRequest);

            strResponse = this.internalProxy.invokeService(QName.valueOf(this.xkmsServiceName), "validate", strRequest);

            LOG.debug("Received the following XKMS Response");
            LOG.debug(strResponse);

            response = new XKMSValidateResponse(strResponse);

            if ((response == null) || !response.isResponseSuccessfull())
                throw new InternalMessagingException("Failure response received from the provider.");

            if (this.checkCertificateIssuerTrust) {
                if (response.getStatusForCertificateIssuerTrust().equalsIgnoreCase("Invalid"))
                    throw new SecurityException("Verification of the Certificate Issuer Trust Failed.");
            }

            if (this.checkCertificateRevocation) {
                if (response.getStatusForCertificateRevocation().equalsIgnoreCase("Invalid"))
                    throw new SecurityException("Certificate has been revoked.");
            }

            if (this.checkCertificateSignature) {
                if (response.getStatusForCertificateSignature().equalsIgnoreCase("Invalid"))
                    throw new SecurityException("Verification of the Certificate Signature Failed.");
            }

            if (this.checkCertificateValidity) {
                if (response.getStatusForCertificateValidity().equalsIgnoreCase("Invalid"))
                    throw new SecurityException("Verification of the Certificate validity Failed.");
            }

            return cert.getPublicKey();
        } catch (InternalSBBException e) {
            throw e;
        } catch (SecurityException e) {
            throw new InternalConfigurationException(e);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }
    }

    /**
     * Gets the key password.
     * 
     * @return key password
     */
    public String getKeyPassword() {
        return this.keyPassword;
    }

    /**
     * Gets the key store.
     * 
     * @return KeyStore store
     */
    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    /**
     * Gets the key store file.
     * 
     * @return String store file
     */
    public String getKeyStoreFile() {
        return this.keyStoreFile;
    }

    /**
     * Gets the key store password.
     * 
     * @return String store password
     */
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    /**
     * Gets the key store type.
     * 
     * @return String store type
     */
    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    /**
     * Gets the signature method.
     * 
     * @return String signature method
     */
    public String getSignatureMethod() {
        return this.signatureMethod;
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
        return new Class[] {DOMSource.class};
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
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#
     *      handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        LOG.info("handling fault for Signing interceptor.");
        try {
            NormalizedMessage nmMessage = null;
            if (role.equals(Role.SENDER)) {
                nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
            } else if (role.equals(Role.RECEIVER)) {
                nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
            }
            this.exchangeHelper.removeSignatureFromExchange(nmMessage);
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
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {
        long beforeTime = System.currentTimeMillis();
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.REQUEST);
        if (null != assertion) {
            try {
                NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
                if (role.equals(Role.SENDER)) {
                    // sign here
                    if (this.signer == null) {
                        this.signer = new MessageSigner(this);
                    }
                    this.signer.sign(nmMessage);
                    LOG.info("Signing of message successfully performed.");
                } else if (role.equals(Role.RECEIVER)) {
                    // verify here
                    if (this.verifier == null) {
                        this.verifier = new MessageVerifier(this);
                    }
                    this.verifier.verify(nmMessage);
                    LOG.info("Verification of signed message successfully performed.");
                }
            } catch (PolicyViolatedException pve) {
                throw pve;
            } catch (InternalSBBException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalInfrastructureException(e);
            } finally {
                LOG.info("Signing took " + (System.currentTimeMillis() - beforeTime) + " ms.");
            }
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
     * @throws PolicyViolatedException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException, PolicyViolatedException {
        long beforeTime = System.currentTimeMillis();
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.RESPONSE);
        if (null != assertion) {
            try {
                NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
                if (role.equals(Role.SENDER)) {
                    // verify here
                    if (this.verifier == null) {
                        this.verifier = new MessageVerifier(this);
                    }
                    this.verifier.verify(nmMessage);
                    LOG.info("Verification of signed message successfully performed.");
                } else if (role.equals(Role.RECEIVER)) {
                    // sign here
                    if (this.signer == null) {
                        this.signer = new MessageSigner(this);
                    }
                    this.signer.sign(nmMessage);
                    LOG.info("Signing of message successfully performed.");
                }
            } catch (PolicyViolatedException pve) {
                throw pve;
            } catch (InternalSBBException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalInfrastructureException(e);
            } finally {
                LOG.info("Signing took " + (System.currentTimeMillis() - beforeTime) + " ms.");
            }
        }
    }

    /**
     * initializing method.
     * 
     * @throws Exception
     *         exception
     */
    public void init() throws Exception {
        this.exchangeHelper = new MessageExchangeHelper();
        org.apache.xml.security.Init.init();
        this.keyStore = KeyStore.getInstance(this.keyStoreType);
        FileInputStream fis =
                new FileInputStream(this.componentContextAccess.getInstallRoot() + File.separator + this.baseDir + File.separator
                        + this.keyStoreFile);
        this.keyStore.load(fis, this.keyStorePassword.toCharArray());
        fis.close();
    }

    /**
     * Sets the base dir.
     * 
     * @param baseDir
     *        basedir
     */
    public void setBaseDir(final String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the certificate alias.
     * 
     * @param certificateAlias
     *        alias
     */
    public void setCertificateAlias(final String certificateAlias) {
        this.certificateAlias = certificateAlias;
    }

    /**
     * Sets the check certificate issuer trust.
     * 
     * @param checkCertificateIssuerTrust
     *        check for vertificate issuer trust
     */
    public void setCheckCertificateIssuerTrust(final boolean checkCertificateIssuerTrust) {
        this.checkCertificateIssuerTrust = checkCertificateIssuerTrust;
    }

    /**
     * Sets the check certificate revocation.
     * 
     * @param checkCertificateRevocation
     *        check for certificate revocation
     */
    public void setCheckCertificateRevocation(final boolean checkCertificateRevocation) {
        this.checkCertificateRevocation = checkCertificateRevocation;
    }

    /**
     * Sets the check certificate signature.
     * 
     * @param checkCertificateSignature
     *        check for certificate signature
     */
    public void setCheckCertificateSignature(final boolean checkCertificateSignature) {
        this.checkCertificateSignature = checkCertificateSignature;
    }

    /**
     * Sets the check certificate validity.
     * 
     * @param checkCertificateValidity
     *        check for certificate validity
     */
    public void setCheckCertificateValidity(final boolean checkCertificateValidity) {
        this.checkCertificateValidity = checkCertificateValidity;
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

    /**
     * Sets the internal proxy.
     * 
     * @param internalProxy
     *        internal proxy
     */
    public void setInternalProxy(final InternalProxy internalProxy) {
        this.internalProxy = internalProxy;
    }

    /**
     * Sets the key alias.
     * 
     * @param keyAlias
     *        alias
     */
    public void setKeyAlias(final String keyAlias) {
        this.keyAlias = keyAlias;
    }

    /**
     * Sets the key password.
     * 
     * @param keyPassword
     *        password
     */
    public void setKeyPassword(final String keyPassword) {
        this.keyPassword = keyPassword;
    }

    /**
     * Sets the key store.
     * 
     * @param keyStore
     *        store
     */
    public void setKeyStore(final KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /**
     * Sets the key store file.
     * 
     * @param keyStoreFile
     *        store
     */
    public void setKeyStoreFile(final String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * Sets the key store password.
     * 
     * @param keyStorePassword
     *        store password
     */
    public void setKeyStorePassword(final String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * Sets the key store type.
     * 
     * @param keyStoreType
     *        store type
     */
    public void setKeyStoreType(final String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    /**
     * Sets the signature method.
     * 
     * @param signatureMethod
     *        signature method
     */
    public void setSignatureMethod(final String signatureMethod) {
        this.signatureMethod = signatureMethod;
    }

    /**
     * Sets the xkms service name.
     * 
     * @param xkmsServiceName
     *        service name
     */
    public void setXkmsServiceName(final String xkmsServiceName) {
        this.xkmsServiceName = xkmsServiceName;
    }

    /**
     * creates a request for an xkms server.
     * 
     * @param strCert
     *        the certificate
     * 
     * @return the request in xml as string
     * 
     * @throws Exception
     *         in case of an error in called functions
     */
    private String createXKMSValidateRequest(final byte[] strCert) throws Exception {
        String strRequest = null;

        // construct a new request object.
        XKMSValidateRequest request = new XKMSValidateRequest();

        // add the respondwith attributes for the request
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_X509CERT);

        // add the key usage attributes for the request
        request.addKeyUsage(XKMSDefinitions.XKMS_KEYUSAGE_ENC);
        request.addKeyUsage(XKMSDefinitions.XKMS_KEYUSAGE_EXH);
        request.addKeyUsage(XKMSDefinitions.XKMS_KEYUSAGE_SIG);

        // add the X509Certificate here. Achtung-the data should be Base64
        // encoded.
        String temp = Base64.encode(strCert);

        request.addX509Certificate(temp);

        // convert the request object to an xml string
        strRequest = request.toXMLString();

        return strRequest;

    }
}
