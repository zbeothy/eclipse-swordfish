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
package org.eclipse.swordfish.core.interceptor.authentication.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.authentication.AuthenticationProcessor;
import org.eclipse.swordfish.core.interceptor.security.MessageExchangeHelper;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.authentication.GenericCallbackHandler;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalFatalException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * The Class AuthenticationProcessorBean.
 * 
 */
public class AuthenticationProcessorBean extends AbstractProcessingComponent implements AuthenticationProcessor {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(AuthenticationProcessorBean.class);

    /** Used by our login module to determine the LDAP address. */
    private static final String LDAP_MASTER_URL_PROP = "org.eclipse.swordfish.core.sbb.directory.master.url";

    /** Used by our login module to determine the LDAP address. */
    private static final String LDAP_SERVER_URL_PROP = "org.eclipse.swordfish.core.sbb.directory.server.url";

    /** Standard JAAS property: path and file name of the JAAS config file. */
    private static final String LOGIN_CONFIG_PROP = "java.security.auth.login.config";

    /** token assertion type. */
    private final String userNameToken = "UsernameToken";

    /** The SAM l_ TOKEN. */
    private final String samlToken = "SAMLToken";

    /** assertion cache. */
    private Cache assertionCache = null;

    /** credential login type. */
    private String credentialLoginType = null;

    /** assertion login type. */
    private String assertionLoginType = null;

    /** ldap url. */
    private String ldapURL = null;

    /** jaas config file. */
    private String jaasConfigFile = null;

    /** consumer auth stratergy. */
    private ConsumerAuthenticationStrategy consumerAuthentication = null;

    /** provider auth stratergy. */
    private ProviderAuthenticationStrategy providerAuthentication = null;

    /** base config dir. */
    private String baseDir;

    /** component context. */
    private ComponentContextAccess componentContextAccess;

    /** message exchange helper. */
    private MessageExchangeHelper exchangeHelper = null;

    /** message exchange reference. */
    private MessageExchange messageExchange = null;

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
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public boolean canHandle(final Collection/* <Assertion> */assertions) throws InternalSBBException {
        return true;
    }

    public Cache getAssertionCache() {
        return this.assertionCache;
    }

    public String getAssertionLoginType() {
        return this.assertionLoginType;
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

    public String getCredentialLoginType() {
        return this.credentialLoginType;
    }

    public MessageExchangeHelper getExchangeHelper() {
        return this.exchangeHelper;
    }

    public String getJaasConfigFile() {
        return this.jaasConfigFile;
    }

    public String getLdapURL() {
        return this.ldapURL;
    }

    public MessageExchange getMessageExchange() {
        return this.messageExchange;
    }

    public String getSamlToken() {
        return this.samlToken;
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

    public String getUserNameToken() {
        return this.userNameToken;
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
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {

        this.messageExchange = context;
        LOG.info("handling fault for Authentication interceptor.");
        try {
            NormalizedMessage nmMessage = null;
            if (role.equals(Role.SENDER)) {
                nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
            } else if (role.equals(Role.RECEIVER)) {
                nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
            }
            nmMessage.setSecuritySubject(null);
            this.exchangeHelper.removeAuthAssertionFromExchange(nmMessage);
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
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {

        this.messageExchange = context;
        long beforeTime = System.currentTimeMillis();
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.REQUEST);
        try {
            if (null != assertion) {
                String agreedAuthType = this.getAgreedAuthenticationType(assertion);
                NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
                if (role.equals(Role.SENDER)) {
                    if (this.consumerAuthentication == null) {
                        this.consumerAuthentication = new ConsumerAuthenticationStrategy(this);
                    }

                    this.consumerAuthentication.handle(nmMessage, context, role, agreedAuthType);
                    LOG.info("Authentication successfully performed on consumer!!");
                } else if (role.equals(Role.RECEIVER)) {
                    if (this.providerAuthentication == null) {
                        this.providerAuthentication = new ProviderAuthenticationStrategy(this);
                    }
                    this.providerAuthentication.handle(nmMessage, context, role, agreedAuthType);
                    LOG.info("Verification for Authentication successfully performed on provider!!");
                }
            }
        } catch (LoginException e) {
            /*
             * Fix for defect #1605, stating soap:fault should be thrown back when authentication
             * fails with correct fault code and fault string set. To enable this fix, we need to
             * get a fix for the binding try { Fault fault = context.createFault();
             * fault.setProperty(MessageProperties.SOAP_FAULT_CODE,
             * "{"+HeaderUtil.WSSECURITY_NS+"}FailedAuthentication");
             * fault.setProperty(MessageProperties.SOAP_FAULT_STRING, "The security token could not
             * be authenticated or authorized"); ByteArrayOutputStream baos = new
             * ByteArrayOutputStream(); e.printStackTrace(new PrintStream(baos));
             * fault.setContent(new DOMSource(TransformerUtil.docFromString("<exception>" +
             * baos.toString() + "</exception>"))); context.setFault(fault); }catch(Exception ex){
             * throw new InternalSBBException(ex); }
             */
            throw new InternalAuthenticationException(e);
        } catch (AuthenticationException e) {
            throw new InternalAuthenticationException(e);
        } catch (NullPointerException e) {
            // FIXME: Workaround for defect in Login module
            throw new InternalAuthenticationException(e);
        } catch (RuntimeException e) {
            throw new InternalFatalException(e);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Authentication took " + (System.currentTimeMillis() - beforeTime) + " ms.");
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
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {

        this.messageExchange = context;
        long beforeTime = System.currentTimeMillis();
        PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.RESPONSE);
        try {
            if (null != assertion) {
                String agreedAuthType = this.getAgreedAuthenticationType(assertion);
                NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
                if (role.equals(Role.SENDER)) {
                    if (this.providerAuthentication == null) {
                        this.providerAuthentication = new ProviderAuthenticationStrategy(this);
                    }
                    this.providerAuthentication.handle(nmMessage, context, role, agreedAuthType);

                    LOG.info("Verification for Authentication successfully performed on consumer!!");
                } else if (role.equals(Role.RECEIVER)) {
                    if (this.consumerAuthentication == null) {
                        this.consumerAuthentication = new ConsumerAuthenticationStrategy(this);
                    }
                    this.consumerAuthentication.handle(nmMessage, context, role, agreedAuthType);
                    LOG.info("Authentication successfully performed on provider!!");
                }
            }
        } catch (LoginException e) {
            /*
             * Fix for defect #1605, stating soap:fault should be thrown back when authentication
             * fails with correct fault code and fault string set. To enable this fix, we need to
             * get a fix for the binding try { Fault fault = context.createFault();
             * fault.setProperty(MessageProperties.SOAP_FAULT_CODE,
             * "{"+HeaderUtil.WSSECURITY_NS+"}FailedAuthentication");
             * fault.setProperty(MessageProperties.SOAP_FAULT_STRING, "The security token could not
             * be authenticated or authorized"); ByteArrayOutputStream baos = new
             * ByteArrayOutputStream(); e.printStackTrace(new PrintStream(baos));
             * fault.setContent(new DOMSource(TransformerUtil.docFromString("<exception>" +
             * baos.toString() + "</exception>"))); context.setFault(fault); }catch(Exception ex){
             * throw new InternalSBBException(ex); }
             */
            throw new InternalAuthenticationException(e);
        } catch (AuthenticationException e) {
            throw new InternalAuthenticationException(e);
        } catch (NamingException e) {
            throw new InternalConfigurationException(e);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Authentication took " + (System.currentTimeMillis() - beforeTime) + " ms.");
        }

    }

    /**
     * initializing method.
     * 
     * @throws Exception
     *         exception
     */
    public void init() throws Exception {

        // only set System properties if they have not been set already

        final String masterUrl = System.getProperty(LDAP_MASTER_URL_PROP);
        if ((masterUrl == null) || (masterUrl.length() == 0)) {
            System.setProperty(LDAP_MASTER_URL_PROP, this.ldapURL);
        }
        final String serverUrl = System.getProperty(LDAP_SERVER_URL_PROP);
        if ((serverUrl == null) || (serverUrl.length() == 0)) {
            System.setProperty(LDAP_SERVER_URL_PROP, this.ldapURL);
        }
        final String loginConfig = System.getProperty(LOGIN_CONFIG_PROP);
        if ((loginConfig == null) || (loginConfig.length() == 0)) {
            System.setProperty(LOGIN_CONFIG_PROP, this.componentContextAccess.getInstallRoot() + File.separator + this.baseDir
                    + File.separator + this.jaasConfigFile);
        }
        this.exchangeHelper = new MessageExchangeHelper();
    }

    /**
     * Lookup in cache.
     * 
     * @param receivedCallbackHandler
     *        the received callback handler
     * 
     * @return the subject
     */
    public Subject lookupInCache(final GenericCallbackHandler receivedCallbackHandler) {
        try {
            String key = receivedCallbackHandler.getHandlerId();
            return (Subject) this.assertionCache.getFromCache(key);
        } catch (NeedsRefreshException e) {
            this.assertionCache.cancelUpdate(receivedCallbackHandler.getHandlerId());
        }
        return null;
    }

    /**
     * Put in cache.
     * 
     * @param callbackhandler
     *        the callbackhandler
     * @param subject
     *        the subject
     */
    public void putInCache(final GenericCallbackHandler callbackhandler, final Subject subject) {
        String key = callbackhandler.getHandlerId();
        this.assertionCache.putInCache(key, subject); // ,new
        // AssertionCacheEntryRefreshPolicy());
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
     * Sets the assertion login type.
     * 
     * @param assertionLoginType
     *        login type
     */
    public void setAssertionLoginType(final String assertionLoginType) {
        this.assertionLoginType = assertionLoginType;
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
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        context
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    /**
     * Sets the credential login type.
     * 
     * @param credentialLoginType
     *        login type
     */
    public void setCredentialLoginType(final String credentialLoginType) {
        this.credentialLoginType = credentialLoginType;
    }

    public void setExchangeHelper(final MessageExchangeHelper exchangeHelper) {
        this.exchangeHelper = exchangeHelper;
    }

    /**
     * Sets the jaas config file.
     * 
     * @param jaasConfigFile
     *        config file
     */
    public void setJaasConfigFile(final String jaasConfigFile) {
        this.jaasConfigFile = jaasConfigFile;
    }

    /**
     * Sets the ldap URL.
     * 
     * @param ldapURL
     *        url
     */
    public void setLdapURL(final String ldapURL) {
        this.ldapURL = ldapURL;
    }

    public void setMessageExchange(final MessageExchange messageExchange) {
        this.messageExchange = messageExchange;
    }

    /**
     * Authenticate.
     * 
     * @param loginType
     *        loign type
     * @param callbackhandler
     *        callback handler
     * 
     * @return Subject authenticated subject
     * 
     * @throws Exception
     *         exception
     */
    protected Subject authenticate(final String loginType, final CallbackHandler callbackhandler) throws Exception {
        LoginContext loginContext = null;
        ClassLoader oldLoader = null;
        try {
            // current thread classloader should be changed to classloader of
            // current class
            // as the JAAS login classes are in SE class loader and the thread
            // class loader
            // is the application class loader.
            oldLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            if (callbackhandler != null) {
                loginContext = new LoginContext(loginType, callbackhandler);
            } else {
                loginContext = new LoginContext(loginType);
            }
            loginContext.login();
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
        if (loginType.equals(this.credentialLoginType) && (callbackhandler instanceof GenericCallbackHandler)) {
            this.putInCache((GenericCallbackHandler) callbackhandler, loginContext.getSubject());
        }
        return loginContext.getSubject();
    }

    /**
     * Generate username token.
     * 
     * @param username
     *        username
     * @param password
     *        password
     * 
     * @return token profile
     * 
     * @throws Exception
     *         exception
     */
    protected String generateUsernameToken(final String username, final String password) throws Exception {

        String token =
                "<wsse:UsernameToken xmlns:wsse=\"" + HeaderUtil.WSSECURITY_NS + "\" >" + "<wsse:Username>" + username
                        + "</wsse:Username>" + "<wsse:Password>" + password + "</wsse:Password>" + "</wsse:UsernameToken>";

        return token;
    }

    /**
     * Gets the assertion from subject.
     * 
     * @param subject
     *        subject
     * 
     * @return String assertions
     * 
     * @throws Exception
     */
    protected String getAssertionFromSubject(final Subject subject) throws Exception {
        String assertion = null;
        Object[] credentials = subject.getPublicCredentials().toArray();
        Method recMethod = null;
        for (int objSize = 0; objSize < credentials.length; objSize++) {

            try {
                recMethod = credentials[objSize].getClass().getMethod("getXmlAssertion", new Class[] {});
            } catch (NoSuchMethodException nsme) {
                // if we get this exception move on with other
                // subjects in the set, dont throw an error here.
                continue;
            }
            if (recMethod != null) {
                Object recObj;
                try {
                    recObj = recMethod.invoke(credentials[objSize], (Object[]) null);
                } catch (Exception e) {
                    continue;
                }
                if (recObj instanceof String) {
                    assertion = (String) recObj;
                    LOG.debug("authentication assertions received from subject is " + assertion);
                    break;
                }
            }
        }

        if (assertion == null) {
            Object principal = null;
            principal = this.exchangeHelper.getSBBPrincipalObjectFromSubject(subject);
            if (principal != null) {
                try {
                    recMethod = principal.getClass().getMethod("getAssertion", new Class[] {});
                    if (recMethod != null) {
                        Object recObj;
                        try {
                            recObj = recMethod.invoke(principal, (Object[]) null);
                            if (recObj instanceof byte[]) {
                                assertion = new String((byte[]) recObj);
                                LOG.debug("authentication assertions received from subject is " + assertion);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // if we get this exception move on , dont throw an
                            // error here.
                        }

                    }
                } catch (NoSuchMethodException nsme) {
                    nsme.printStackTrace();
                    // if we get this exception move on , dont throw an error
                    // here.
                }

            }

        }

        return assertion;

    }

    /**
     * Gets the auth assertion from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return String assertions
     * 
     * @throws Exception
     *         exception
     */
    protected String getAuthAssertionFromExchange(final NormalizedMessage me) throws Exception {

        return this.exchangeHelper.getAuthAssertionFromExchange(me);
    }

    /**
     * Checks if is assertion in subject expired.
     * 
     * @param subject
     *        the subject
     * 
     * @return true, if is assertion in subject expired
     */
    protected boolean isAssertionInSubjectExpired(final Subject subject) {

        Object[] credentials = subject.getPublicCredentials().toArray();
        for (int objSize = 0; objSize < credentials.length; objSize++) {
            Method recMethod = null;
            try {
                recMethod = credentials[objSize].getClass().getMethod("getExpiryDate", (Class[]) null);
            } catch (NoSuchMethodException nsme) {
                // if we get this exception move on with other
                // subjects in the set, dont throw an error here.
                continue;
            }
            if (recMethod != null) {
                Object recObj;
                try {
                    recObj = recMethod.invoke(credentials[objSize], (Object[]) null);
                } catch (Exception e) {
                    continue;
                }
                if (recObj instanceof Date) return ((Date) recObj).before(new Date());
            }
        }
        return true;

    }

    /**
     * Sets the auth assertion in exchange.
     * 
     * @param me
     *        message exchange
     * @param assertions
     *        assertions
     * 
     * @throws Exception
     *         exception
     */
    protected void setAuthAssertionInExchange(final NormalizedMessage me, final String assertions) throws Exception {
        this.exchangeHelper.setAuthAssertionInExchange(me, assertions);
    }

    /**
     * Sets the authenticated enviornment.
     * 
     * @param subject
     *        authenticated subject
     * @param exchange
     *        message exchange
     * 
     * @throws Exception
     *         exception
     */
    protected void setAuthenticatedEnviornment(final NormalizedMessage exchange, final Subject subject) throws Exception {
        this.setSubjectInExchange(exchange, subject);
        String assertions = this.getAssertionFromSubject(subject);
        this.setAuthAssertionInExchange(exchange, assertions);
        LOG.debug("Setting authentication assertion in MessageExchange: " + assertions);

    }

    /**
     * Sets the subject in exchange.
     * 
     * @param message
     *        message
     * @param subject
     *        the subject
     * 
     * @throws Exception
     *         exception
     */
    protected void setSubjectInExchange(final NormalizedMessage message, final Subject subject) throws Exception {
        message.setSecuritySubject(subject);
        if (this.messageExchange != null) {
            CallContextExtension callContext = HeaderUtil.getCallContextExtension(this.messageExchange);
            if (callContext != null) {
                callContext.setCallerSubject(subject);
            }
        }
    }

    /**
     * Sets the username token in exchange.
     * 
     * @param nm
     *        normalized message
     * @param usernameToken
     *        token
     * @param subject
     *        subject
     * 
     * @throws Exception
     *         exception
     */
    protected void setUsernameTokenInExchange(final NormalizedMessage nm, final String usernameToken, final Subject subject)
            throws Exception {
        this.setSubjectInExchange(nm, subject);
        this.exchangeHelper.setUsernametokenInExchange(nm, usernameToken);
        LOG.debug("Setting UsernameToken in MessageExchange: " + usernameToken);
    }

    /**
     * Gets the agreed authentication type.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return the agreed authentication type
     */
    private String getAgreedAuthenticationType(final PrimitiveAssertion assertion) {
        return this.getAttribute(assertion, "type", null);
    }

}
