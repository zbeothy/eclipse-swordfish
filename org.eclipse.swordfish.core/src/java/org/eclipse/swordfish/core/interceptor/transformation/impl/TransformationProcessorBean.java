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
package org.eclipse.swordfish.core.interceptor.transformation.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.processing.ContentAction;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.core.interceptor.transformation.TransformationProcessor;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * The Class TransformationProcessorBean.
 */
public class TransformationProcessorBean extends AbstractProcessingComponent implements TransformationProcessor {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(TransformationProcessorBean.class);

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
     * Creates the template.
     * 
     * @param pStyleSheetID
     *        id
     * 
     * @return Templates template
     * 
     * @throws Exception
     *         exception
     */
    public Templates createTemplate(final StyleSheetID pStyleSheetID) throws Exception {
        Templates template = null;
        TransformerFactory aTransformerFactory = null;
        // switch the classloader to make sure that we always get the oracle
        // transformer
        // present in the jbi container classloader(bootstrap classloader), till
        // someone
        // sets a system propertry :(
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            aTransformerFactory = TransformerFactory.newInstance();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        InputStream aStyleSheetFile = this.resolveTemplate(pStyleSheetID);
        StreamSource aSource = new StreamSource(aStyleSheetFile);
        template = aTransformerFactory.newTemplates(aSource);

        // TODO aCache.addObject(pStyleSheetID, aTemplate);
        return template;
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
     * Gets the template.
     * 
     * @param styleSheetID
     *        id
     * 
     * @return Templates template
     * 
     * @throws Exception
     *         exception
     */
    public Templates getTemplate(final StyleSheetID styleSheetID) throws Exception {
        Templates template = null;
        // TODO aTemplate = getTemplateFromCache(pStyleSheetID);
        if (template == null) {
            template = this.createTemplate(styleSheetID);
        }
        return template;
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
        // TODO what should be done here.

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
     *      #handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        long beforeTime = System.currentTimeMillis();
        try {
            NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.REQUEST);
            PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.REQUEST);
            if (null != assertion) {
                Source source = this.transform(nmMessage, assertion);
                if (source == null) throw new InternalMessagingException("The Result Source is null.");
                nmMessage.setContent(source);
            }
        } catch (InternalSBBException se) {
            throw se;
        } catch (ConfigurationRepositoryResourceException e) {
            throw new InternalConfigurationException(e);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Transformation took " + (System.currentTimeMillis() - beforeTime) + " ms.");
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
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection/* <Assertion> */assertions)
            throws InternalSBBException {
        long beforeTime = System.currentTimeMillis();
        try {
            NormalizedMessage nmMessage = this.getCurrentNormalizedMessage(context, Scope.RESPONSE);
            PrimitiveAssertion assertion = this.narrowDown(assertions, role, Scope.RESPONSE);
            if (null != assertion) {
                Source source = this.transform(nmMessage, assertion);
                if (source == null) throw new InternalMessagingException("The Result Source is null.");
                nmMessage.setContent(source);
            }
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        } finally {
            LOG.info("Transformation took " + (System.currentTimeMillis() - beforeTime) + " ms.");
        }
    }

    /**
     * initializing method.
     * 
     * @throws Exception
     *         exception
     */
    public void init() throws Exception {

    }

    /**
     * Resolve template.
     * 
     * @param pStyleSheetID
     *        id
     * 
     * @return InputStream inputstream
     * 
     * @throws Exception
     *         exception
     */
    public InputStream resolveTemplate(final StyleSheetID pStyleSheetID) throws Exception {
        InputStream aTemplate = null;
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            aTemplate =
                    this.configurationManager.getResource("SBB", new ScopePathUtil(
                            ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR).composeScopePath(pStyleSheetID
                        .getSourcePath()), "sbbtran:Transformation", pStyleSheetID.getID());
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

        if (null == aTemplate) throw new InternalConfigurationException("No transformation template available");
        return aTemplate;
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
     * Transform.
     * 
     * @param nm
     *        normalized message
     * @param assertion
     *        assertions
     * 
     * @return DOMResult result
     * 
     * @throws Exception
     *         exception
     */
    public Source transform(final NormalizedMessage nm, final PrimitiveAssertion assertion) throws Exception {
        Source source = nm.getContent();

        if (TransformerUtil.isSourceEmpty(source)) {
            LOG.warn("No data to process for transformation.");
            return source;
        }

        DOMResult domResult = null;
        Object[] arraystyleSheetID = this.getStyleSheetID(assertion);
        for (int i = 0; i < arraystyleSheetID.length; i++) {
            Templates templates = this.getTemplate((StyleSheetID) arraystyleSheetID[i]);
            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            Transformer transformer = null;
            try {
                Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                transformer = templates.newTransformer();
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
            domResult = this.process(source, transformer);
            source = new DOMSource(domResult.getNode());
            LOG.debug("Result of transformation:");
            LOG.debug(TransformerUtil.stringFromSource(source));
        }
        return source;
    }

    /**
     * Gets the style sheet ID.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return StyleSheetID[] id
     * 
     * @throws Exception
     *         exception
     */
    private Object[] getStyleSheetID(final PrimitiveAssertion assertion) throws Exception {

        ArrayList arrayStyleSheetID = new ArrayList();

        String ruleSourcePath = assertion.getAttribute(new QName("ruleSourcePath"));
        if (ruleSourcePath == null) throw new InternalMessagingException("Source Path not found in policy assertions.");
        String ruleId = assertion.getAttribute(new QName("ruleId"));
        if (ruleSourcePath == null) throw new InternalMessagingException("Rule ID not found in policy assertions.");
        arrayStyleSheetID.add(new StyleSheetID(ruleSourcePath, ruleId));
        return arrayStyleSheetID.toArray();
    }

    /**
     * Process.
     * 
     * @param source
     *        source
     * @param transformer
     *        transformer
     * 
     * @return DOMResult result
     * 
     * @throws Exception
     *         exception
     * @throws InternalSBBException
     */
    private DOMResult process(final Source source, final Transformer transformer) throws InternalSBBException {
        try {
            DOMResult domResult = new DOMResult();
            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                try {
                    transformer.transform(source, domResult);
                } catch (Exception e) {
                    // FIXME Workaround for configuration delivering empty Stream
                    // on non-existent template which causes transformation to
                    // fail with unclear exceptions
                    throw new InternalConfigurationException("Bad transformation template provided for transformation", e);
                }
            } finally {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }

            return domResult;
        } catch (InternalSBBException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }
    }

}
