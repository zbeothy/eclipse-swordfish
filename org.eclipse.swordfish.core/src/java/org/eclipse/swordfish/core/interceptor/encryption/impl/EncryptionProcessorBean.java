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
package org.eclipse.swordfish.core.interceptor.encryption.impl;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Collection;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.encryption.EncryptionProcessor;
import org.eclipse.swordfish.core.interceptor.security.MessageExchangeHelper;
import org.eclipse.swordfish.core.interceptor.security.xkms.XKMSDefinitions;
import org.eclipse.swordfish.core.interceptor.security.xkms.XKMSLocateRequest;
import org.eclipse.swordfish.core.interceptor.security.xkms.XKMSLocateResponse;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * The Class EncryptionProcessorBean.
 */
public class EncryptionProcessorBean extends AbstractProcessingComponent implements EncryptionProcessor {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(EncryptionProcessorBean.class);

    /** component context. */
    private ComponentContextAccess componentContextAccess = null;

    /** message exchamge helper. */
    private MessageExchangeHelper exchangeHelper = null;

    /** certificate alias. */
    private String certificateAlias = null;

    /** signer reference. */
    private Encrypter encrypter = null;

    /** verifier reference. */
    private Decrypter decrypter = null;

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

    /** symmetric key size. */
    private int symmetricKeySize = 0;

    /** symmetric key algorithm. */
    private String symmetricKeyAlgorithm = null;

    /** algorithm for encrypting symmetric key. */
    private String algorithmToEncryptSymmetricKey = null;

    /** data encryption algorithm. */
    private String dataEncryptionAlgorithm = null;

    /** internal proxy. */
    private InternalProxy internalProxy = null;

    /** xkms provider service name. */
    private String xkmsServiceName = null;

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
     * Gets the algorithm to encrypt symmetric key.
     * 
     * @return String algorithm
     */
    public String getAlgorithmToEncryptSymmetricKey() {
        return this.algorithmToEncryptSymmetricKey;
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
        return ContentAction.READWRITE;
    }

    /**
     * Gets the data encryption algorithm.
     * 
     * @return String algorithm
     */
    public String getDataEncryptionAlgorithm() {
        return this.dataEncryptionAlgorithm;
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
     * Gets the key for decrypting.
     * 
     * @return Key key
     * 
     * @throws Exception
     *         exception
     */
    public Key getKeyForDecrypting() throws Exception {
        Key key = this.keyStore.getKey(this.keyAlias, this.keyPassword.toCharArray());
        return key;
    }

    /**
     * Gets the key for encrypting.
     * 
     * @param exchange
     *        exchange
     * @param role
     *        Role
     * @param nm
     *        normalized message
     * 
     * @return private key for signing
     * 
     * @throws Exception
     *         exception
     */
    public Key getKeyForEncrypting(final MessageExchange exchange, final Role role, final NormalizedMessage nm) throws Exception {

        String strRequest = null;
        String strResponse = null;
        String receiverId = null;
        XKMSLocateResponse response = null;

        if (role.equals(Role.SENDER)) {
            String serviceName = exchange.getService().toString();

            if (serviceName == null) throw new Exception("Cannot retrieve Servicename of the receiver from the exchange.");

            receiverId = serviceName;

        } else if (role.equals(Role.RECEIVER)) {
            // we need to take the incoming nm here and pick the
            // principal from the incoming nm and lookup for public key for this
            // principal.
            NormalizedMessage incomingNM = exchange.getMessage("in");
            Subject subject = incomingNM.getSecuritySubject();
            if (subject == null)
                throw new Exception("Cannot retrieve Security Subject information of the sender from the incoming exchange.");
            String principalName = this.exchangeHelper.getPrincipalNameFromSubject(subject);

            if (principalName == null)
                throw new Exception("Cannot retrieve Security Principal information of the sender from the incoming exchange.");

            receiverId = principalName;
        }

        strRequest = this.createXKMSLocateRequest(receiverId);

        LOG.debug("Sending the following XKMS Request");
        LOG.debug(strRequest);

        strResponse = this.internalProxy.invokeService(QName.valueOf(this.xkmsServiceName), "locate", strRequest);

        LOG.debug("Received the following XKMS Response");
        LOG.debug(strResponse);

        response = new XKMSLocateResponse(strResponse);

        if ((response == null) || !response.isResponseSuccessfull())
            throw new Exception("Failure response received from the provider.");

        if (!response.usableForEncryption()) throw new Exception("Key cannot be used for Encryption.");

        return response.getPublicKey();
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
     * FIXME enhance content handling.
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
     * Gets the symmetric key algorithm.
     * 
     * @return String key algorithm
     */
    public String getSymmetricKeyAlgorithm() {
        return this.symmetricKeyAlgorithm;
    }

    /**
     * Gets the symmetric key size.
     * 
     * @return int keysize
     */
    public int getSymmetricKeySize() {
        return this.symmetricKeySize;
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
        LOG.info("handling fault for Encryption interceptor.");
        try {
            NormalizedMessage nmMessage = null;
            if (role.equals(Role.SENDER)) {
                nmMessage = context.getMessage("in");
            } else if (role.equals(Role.RECEIVER)) {
                nmMessage = context.getMessage("out");
            }
            this.exchangeHelper.removeEncryptionFromExchange(nmMessage);
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
                    // encrypt here
                    if (this.encrypter == null) {
                        this.encrypter = new Encrypter(this);
                    }
                    this.encrypter.encrypt(nmMessage, role, context);
                    LOG.info("Encryption of message successfully performed.");
                } else if (role.equals(Role.RECEIVER)) {
                    // decrypt here
                    if (this.decrypter == null) {
                        this.decrypter = new Decrypter(this);
                    }
                    this.decrypter.decrypt(nmMessage);
                    LOG.info("Decryption of message successfully performed.");
                }
            } catch (PolicyViolatedException pve) {
                throw pve;
            } catch (InternalSBBException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalInfrastructureException(e);
            } finally {
                LOG.info("EncryptionBean took " + (System.currentTimeMillis() - beforeTime) + " ms.");
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
                if (role.equals(Role.RECEIVER)) {
                    // encrypt here
                    if (this.encrypter == null) {
                        this.encrypter = new Encrypter(this);
                    }
                    this.encrypter.encrypt(nmMessage, role, context);
                    LOG.info("Encryption of message successfully performed.");
                } else if (role.equals(Role.SENDER)) {
                    // decrypt here
                    if (this.decrypter == null) {
                        this.decrypter = new Decrypter(this);
                    }
                    this.decrypter.decrypt(nmMessage);
                    LOG.info("Decryption of message successfully performed.");
                }
            } catch (PolicyViolatedException pve) {
                throw pve;
            } catch (InternalSBBException e) {
                throw e;
            } catch (Exception e) {
                throw new InternalInfrastructureException(e);
            } finally {
                LOG.info("EncryptionBean took " + (System.currentTimeMillis() - beforeTime) + " ms.");
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
     * Sets the algorithm to encrypt symmetric key.
     * 
     * @param algorithmToEncryptSymmetricKey
     *        algorithm
     */
    public void setAlgorithmToEncryptSymmetricKey(final String algorithmToEncryptSymmetricKey) {
        this.algorithmToEncryptSymmetricKey = algorithmToEncryptSymmetricKey;
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
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        context
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    /**
     * Sets the data encryption algorithm.
     * 
     * @param dataEncryptionAlgorithm
     *        algorithm
     */
    public void setDataEncryptionAlgorithm(final String dataEncryptionAlgorithm) {
        this.dataEncryptionAlgorithm = dataEncryptionAlgorithm;
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
     * Sets the symmetric key algorithm.
     * 
     * @param symmetricKeyAlgorithm
     *        key algorithm
     */
    public void setSymmetricKeyAlgorithm(final String symmetricKeyAlgorithm) {
        this.symmetricKeyAlgorithm = symmetricKeyAlgorithm;
    }

    /**
     * Sets the symmetric key size.
     * 
     * @param symmetricKeySize
     *        key size
     */
    public void setSymmetricKeySize(final int symmetricKeySize) {
        this.symmetricKeySize = symmetricKeySize;
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
     * Creates the XKMS locate request.
     * 
     * @param receiverID
     *        the id for which public key is required.
     * 
     * @return the request in xml as string
     * 
     * @throws Exception
     *         in case of error in called functions
     */
    private String createXKMSLocateRequest(final String receiverID) throws Exception {
        String strRequest = null;
        XKMSLocateRequest request = new XKMSLocateRequest();

        // add the respondwith attributes for the request
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_KEYNAME);
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_KEYVALUE);
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_PGP);
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_PGPWEB);
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_X509CERT);
        request.addRespondWith(XKMSDefinitions.XKMS_RESPONDWITH_X509CHAIN);

        // add the key usage attributes for the request
        request.addKeyUsage(XKMSDefinitions.XKMS_KEYUSAGE_ENC);
        request.addKeyUsage(XKMSDefinitions.XKMS_KEYUSAGE_EXH);
        request.addKeyUsage(XKMSDefinitions.XKMS_KEYUSAGE_SIG);

        // add the usekeywith attribute for the request.
        // set the id of the key request.
        request.addUseKeyWith(XKMSDefinitions.XKMS_APPLICATION_NAME, receiverID);

        // convert the request object to an xml string
        strRequest = request.toXMLString();
        return strRequest;
    }

    /**
     * Sets the xkms service policy ID.
     * 
     * @param xkmsServicePolicyID
     *        policy id
     */
}
