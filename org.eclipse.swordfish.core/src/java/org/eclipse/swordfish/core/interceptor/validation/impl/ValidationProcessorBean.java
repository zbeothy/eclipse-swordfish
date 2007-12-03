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
package org.eclipse.swordfish.core.interceptor.validation.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver;
import org.eclipse.swordfish.core.interceptor.validation.SchemaValidator;
import org.eclipse.swordfish.core.interceptor.validation.ValidationProcessor;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalFatalException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * The Class ValidationProcessorBean.
 * 
 * Templates
 */
public class ValidationProcessorBean extends AbstractProcessingComponent implements ValidationProcessor {

    /** schema element. */
    public static final String XSD_SCHEMA_ELEMENT = "schema";

    /** schema namespace. */
    private static String xsdSchema = "http://www.w3.org/2001/XMLSchema";

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(ValidationProcessorBean.class);

    /** custom validation assertion name. */
    private static final String CUSTOM_SCHEMA_ROOT_ELEMENT = "CustomValidation";

    public static String getXsdSchema() {
        return xsdSchema;
    }

    public static void setXsdSchema(final String xsdSchema) {
        ValidationProcessorBean.xsdSchema = xsdSchema;
    }

    /** resolver. */
    private ServiceDescriptionResolver serviceDescriptionResolver = null;

    /** schema validator. */
    private SchemaValidator schemaValidator = null;

    /** schema cache. */
    private Cache schemaCache = null;

    /** configuration manager reference. */
    private ConfigurationRepositoryManagerInternal configurationManager = null;

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

        // Nothing to do here.

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
        try {
            NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
            PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.REQUEST);
            if (null != assertion) {
                if (CUSTOM_SCHEMA_ROOT_ELEMENT.equalsIgnoreCase(assertion.getName().getLocalPart())) {
                    this.validateCustom(nmMessage, assertion);
                } else {
                    this.validateMessageName(nmMessage, context.getService(), context.getOperation(), Scope.REQUEST);
                    this.validateSDX(nmMessage, context.getService());
                }
            }
        } catch (PolicyViolatedException pve) {
            throw pve;
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Validation took " + (System.currentTimeMillis() - beforeTime) + " ms.");
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
     *         in case the response could not be processed
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
        try {
            NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
            PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.RESPONSE);
            if (null != assertion) {
                if (CUSTOM_SCHEMA_ROOT_ELEMENT.equalsIgnoreCase(assertion.getName().getLocalPart())) {
                    this.validateCustom(nmMessage, assertion);
                } else {
                    this.validateMessageName(nmMessage, context.getService(), context.getOperation(), Scope.RESPONSE);
                    this.validateSDX(nmMessage, context.getService());
                }
            }
        } catch (PolicyViolatedException pve) {
            throw pve;
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Validation took " + (System.currentTimeMillis() - beforeTime) + " ms.");
        }
    }

    /**
     * initializing method.
     * 
     * @throws Exception
     *         exception
     */
    public void init() throws Exception {
        this.schemaValidator = new XercesSchemaValidator();
    }

    /**
     * Sets the configuration manager.
     * 
     * @param configurationManager
     *        config manager
     */
    public void setConfigurationManager(final ConfigurationRepositoryManagerInternal configurationManager) {
        this.configurationManager = configurationManager;
    }

    /**
     * Sets the schema cache.
     * 
     * @param cache
     *        cache
     */
    public void setSchemaCache(final Cache cache) {
        this.schemaCache = cache;
    }

    /**
     * Sets the service description resolver.
     * 
     * @param serviceDescriptionResolver
     *        service descrpiton resolver
     */
    public void setServiceDescriptionResolver(final ServiceDescriptionResolver serviceDescriptionResolver) {
        this.serviceDescriptionResolver = serviceDescriptionResolver;
    }

    /**
     * Source to input source.
     * 
     * @param source
     *        source
     * 
     * @return InputSource input source
     * 
     * @throws Exception
     *         exception
     */
    public InputSource sourceToInputSource(final Source source) throws Exception {
        if (source instanceof SAXSource)
            return ((SAXSource) source).getInputSource();
        else if (source instanceof StreamSource)
            throw new InternalMessagingException("Cannot work with StreamSource, use a DOMSource instead.");
        else if (source instanceof DOMSource) {
            InputStream is = new ByteArrayInputStream(TransformerUtil.stringFromSource(source).getBytes());
            return new InputSource(is);
        } else
            throw new InternalMessagingException("Unsupported Source found.");
    }

    /**
     * This method validates a given xml document against a xml schema. First, we get the
     * service.name off the handlerinfo and with this name we go to the service registry an get the
     * service description for this service. the sd contains the schema which we can get with the
     * corresponding method of the ServiceDescriptionReader. If we have the Schema, we are able to
     * create a validator and then we validate the document
     * 
     * @param nm
     *        message
     * @param assertion
     *        the assertion
     * 
     * @throws InternalSBBException
     *         Exception
     * @throws PolicyViolatedException
     */
    public void validateCustom(final NormalizedMessage nm, final PrimitiveAssertion assertion) throws InternalSBBException,
            PolicyViolatedException {

        Source aSource = nm.getContent();
        if (aSource == null) throw new InternalFatalException("Source in Normalized Message is null.");

        if (TransformerUtil.isSourceEmpty(aSource)) {
            LOG.warn("No data to process for validation.");
            return;
        }

        Object theSchemas = null;
        try {
            theSchemas = this.getCustomSchema(assertion);
        } catch (ConfigurationRepositoryResourceException e) {
            throw new InternalConfigurationException("Exception accessing the custom schema:", e);
        }

        if (theSchemas == null) throw new InternalConfigurationException("No schema found for validation");

        try {
            this.schemaValidator.validate(theSchemas, this.sourceToInputSource(aSource));
        } catch (Exception e) {
            throw new InternalMessagingException(e);
        }

    }

    /**
     * Validate SDX.
     * 
     * @param nm
     *        the nm
     * @param serviceName
     *        the service name
     * 
     * @throws InternalSBBException
     * @throws PolicyViolatedException
     */
    public void validateSDX(final NormalizedMessage nm, final QName serviceName) throws InternalSBBException,
            PolicyViolatedException {

        Source aSource = nm.getContent();
        if (aSource == null) throw new InternalFatalException("Source in Normalized Message is null.");

        if (TransformerUtil.isSourceEmpty(aSource)) {
            LOG.warn("No data to process for validation.");
            return;
        }

        Object theSchemas = this.getSchemas(serviceName);

        if (theSchemas == null) throw new InternalConfigurationException("No schema found for validation");

        try {
            this.schemaValidator.validate(theSchemas, this.sourceToInputSource(aSource));
        } catch (Exception e) {
            throw new InternalIllegalInputException(e);
        }

    }

    /**
     * Gets the custom schema.
     * 
     * @param assertion
     *        assertion
     * 
     * @return Object schema
     * 
     * @throws ConfigurationRepositoryResourceException
     * @throws InternalSBBException
     *         exception
     */
    private Object getCustomSchema(final PrimitiveAssertion assertion) throws InternalSBBException,
            ConfigurationRepositoryResourceException {

        String schemaSourcePath = assertion.getAttribute(new QName("schemaSourcePath"));
        if (schemaSourcePath == null)
            throw new InternalMessagingException("Invalid assertion, Schema Path not found in policy assertions.");
        String schemaId = assertion.getAttribute(new QName("schemaId"));
        if (schemaId == null)
            throw new InternalMessagingException("Invalid assertion, Schema name not found in policy assertions.");
        ArrayList schemaList = new ArrayList();
        InputStream is = null;
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            is =
                    this.configurationManager.getResource("SBB", new ScopePathUtil(
                            ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR).composeScopePath(schemaSourcePath),
                            "sbbvali:Validation", schemaId);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        schemaList.add(is);

        try {
            return this.schemaValidator.createSchema(schemaList);
        } catch (Exception e) {
            // defective schema considered configuration problem
            throw new InternalConfigurationException(e);
        }
    }

    /**
     * Gets the schema from cache.
     * 
     * @param serviceName
     *        serviceName
     * 
     * @return Object schema
     */
    private Object getSchemaFromCache(final String serviceName) {
        Object schemas = null;
        try {
            schemas = this.schemaCache.getFromCache(serviceName);
        } catch (NeedsRefreshException nre) {
            LOG.info("Schema not found in cache");
            this.schemaCache.cancelUpdate(serviceName);
        }
        return schemas;
    }

    /**
     * If we can't find a Schema in the Chache then we extract the schema form the
     * servicedescritpion.
     * 
     * @param serviceName
     *        serviceName
     * 
     * @return Object a compiled schema
     * 
     * @throws InternalSBBException
     *         exception
     */
    private Object getSchemaOutOfServiceDescription(final QName serviceName) throws InternalSBBException {
        List aSchema = null;
        Object recSchema = null;

        CompoundServiceDescription compoundDesc = this.serviceDescriptionResolver.getServiceDescription(serviceName);

        if (compoundDesc == null)
            throw new InternalServiceDiscoveryException("Could not find service description for service " + serviceName.toString());

        aSchema = compoundDesc.getWSDLdefinedSchemas();

        if ((null == aSchema) || (aSchema.size() == 0))
            throw new InternalConfigurationException("Cannot find Schemas for interface : " + serviceName.toString());

        try {
            recSchema = this.schemaValidator.createSchema(aSchema);
            this.setSchemaToCache(serviceName.toString(), recSchema);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }

        return recSchema;
    }

    /**
     * Here we try to get a Schema with the key serviceName.
     * 
     * @param serviceName
     *        serviceName
     * 
     * @return Object Schema for this Service
     * 
     * @throws InternalSBBException
     *         exception
     */
    private Object getSchemas(final QName serviceName) throws InternalSBBException {
        Object theSchemas = null;
        theSchemas = this.getSchemaFromCache(serviceName.toString());
        if (theSchemas == null) {
            theSchemas = this.getSchemaOutOfServiceDescription(serviceName);
        } else {
            LOG.info("Schemas found in cache");
        }
        return theSchemas;
    }

    /**
     * Sets the schema to cache.
     * 
     * @param serviceName
     *        serviceName
     * @param schema
     *        schema
     */
    private void setSchemaToCache(final String serviceName, final Object schema) {
        this.schemaCache.putInCache(serviceName, schema);
    }

    /**
     * tests wether the message part name in the request matches the message part name defined in
     * the WSDL.
     * 
     * @param nm
     *        the nm
     * @param serviceName
     *        the service name
     * @param operation
     *        the operation
     * @param scope
     *        the scope
     * 
     * @throws InternalSBBException
     */
    private void validateMessageName(final NormalizedMessage nm, final QName serviceName, final QName operation, final Scope scope)
            throws InternalSBBException {

        Source aSource = nm.getContent();
        if (aSource == null) throw new InternalMessagingException("Source in Normalized Message is null.");
        if (TransformerUtil.isSourceEmpty(aSource)) {
            LOG.warn("No data to process for validation.");
            return;
        }
        CompoundServiceDescription compoundDesc = this.serviceDescriptionResolver.getServiceDescription(serviceName);

        if (compoundDesc == null)
            throw new InternalConfigurationException("Could not find service description for service " + serviceName.toString());

        Document doc = TransformerUtil.docFromSource(aSource);
        String ns = doc.getDocumentElement().getNamespaceURI();
        String name = doc.getDocumentElement().getLocalName();
        QName potentialMessagePartName = new QName(ns, name);

        Part part = null;
        if (scope.equals(Scope.REQUEST)) {
            part = compoundDesc.getOperationInputMessagePart(operation.getLocalPart());
        } else {
            part = compoundDesc.getOperationOutputMessagePart(operation.getLocalPart());
        }

        QName elemName = part.getElementName();

        if (elemName == null)
            throw new InternalMessagingException("cannot verify that the message relates to the operation, "
                    + " the message is not determined through its element name ");

        if (!elemName.equals(potentialMessagePartName))
            throw new InternalMessagingException("message Element " + potentialMessagePartName.toString()
                    + " does not match the required schema element name " + elemName);
    }
}
