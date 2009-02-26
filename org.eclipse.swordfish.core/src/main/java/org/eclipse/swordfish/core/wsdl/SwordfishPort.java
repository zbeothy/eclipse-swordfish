/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.wsdl;

import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

public interface SwordfishPort {

	/** JMS transport. */
	public static final String JMS_TRANSPORT_URI = "http://schemas.xmlsoap.org/soap/jms";

	/** SOAP via HTTP transport. */
	public static final String HTTP_TRANSPORT_URI = "http://schemas.xmlsoap.org/soap/http";

	/** JBI transport. */
	public static final String JBI_TRANSPORT_URI = "http://binding.sopware.org/soap/jbi";

	/** SBB2 transport. */
	public static final String SBB2_TRANSPORT_URI = "http://binding.sopware.org/soap/sbb2";

	/**
	 * Gets the transport.
	 * 
	 * @return the transport
	 * 
	 * @see org.sbb.servicedesc.SPDXPort#getTransportType()
	 */
	public Transport getTransport();

	/**
	 * Adds the extensibility element.
	 * 
	 * @param arg0
	 *            the arg0
	 * 
	 * @see javax.wsdl.Port#addExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement)
	 */
	public void addExtensibilityElement(final ExtensibilityElement arg0);

	/**
	 * Gets the binding.
	 * 
	 * @return the binding
	 * 
	 * @see javax.wsdl.Port#getBinding()
	 */
	public Binding getBinding();

	/**
	 * Gets the documentation element.
	 * 
	 * @return the documentation element
	 * 
	 * @see javax.wsdl.Port#getDocumentationElement()
	 */
	public Element getDocumentationElement();

	/**
	 * Gets the extensibility elements.
	 * 
	 * @return the extensibility elements
	 * 
	 * @see javax.wsdl.Port#getExtensibilityElements()
	 */
	public List getExtensibilityElements();

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 * 
	 * @see javax.wsdl.Port#getName()
	 */
	public String getName();

	/**
	 * Sets the binding.
	 * 
	 * @param arg0
	 *            the arg0
	 * 
	 * @see javax.wsdl.Port#setBinding(javax.wsdl.Binding)
	 */
	public void setBinding(final Binding arg0);

	/**
	 * Sets the documentation element.
	 * 
	 * @param arg0
	 *            the arg0
	 * 
	 * @see javax.wsdl.Port#setDocumentationElement(org.w3c.dom.Element)
	 */
	public void setDocumentationElement(final Element arg0);

	/**
	 * Sets the name.
	 * 
	 * @param arg0
	 *            the arg0
	 * 
	 * @see javax.wsdl.Port#setName(java.lang.String)
	 */
	public void setName(final String arg0);

	/**
	 * Sets the extension attribute.
	 * 
	 * @param arg0
	 *            the arg0
	 * @param arg1
	 *            the arg1
	 * 
	 * @see javax.wsdl.extensions.AttributeExtensible#setExtensionAttribute(javax.xml.namespace.QName,
	 *      java.lang.Object)
	 */
	public void setExtensionAttribute(final QName arg0, final Object arg1);

	/**
	 * Gets the extension attribute.
	 * 
	 * @param arg0
	 *            the arg0
	 * 
	 * @return the extension attribute
	 * 
	 * @see javax.wsdl.extensions.AttributeExtensible#getExtensionAttribute(javax.xml.namespace.QName)
	 */
	public Object getExtensionAttribute(final QName arg0);

	/**
	 * Gets the extension attributes.
	 * 
	 * @return the extension attributes
	 * 
	 * @see javax.wsdl.extensions.AttributeExtensible#getExtensionAttributes()
	 */
	public Map getExtensionAttributes();

	/**
	 * Gets the native attribute names.
	 * 
	 * @return the native attribute names
	 * 
	 * @see javax.wsdl.extensions.AttributeExtensible#getNativeAttributeNames()
	 */
	public List getNativeAttributeNames();

	/**
	 * {@inheritDoc}
	 * 
	 * @see javax.wsdl.extensions.ElementExtensible#removeExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement)
	 */
	public ExtensibilityElement removeExtensibilityElement(
			ExtensibilityElement arg0);

}