package org.eclipse.swordfish.core.wsdl;

import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * This class implements SPDXPort.
 */
public class SwordfishPortImpl implements SwordfishPort {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2250071086424670955L;

	/** The base WSDL4J port. */
	private Port port;

	/** The transport type. */
	private TransportImpl transport;

	/** Weather or not this data type has been interpreted yet. */
	private boolean interpreted;

	/**
	 * Creates a new SPDX port.
	 *
	 * @param newPort
	 *            a javax.wsdl.Port
	 */
	public SwordfishPortImpl(final Port port) {
		this.port = port;
	}

	/**
	 * Instantiates a new SPDX port impl.
	 *
	 * @param port
	 *            the port
	 * @param transport
	 *            the transport
	 */
	public SwordfishPortImpl(final Port port, TransportImpl transport) {
		this(port);
		this.transport = transport;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getTransport()
	 */
	public TransportImpl getTransport() {

		if (!interpreted) {
			interpret();
		}

		return this.transport;
	}

	/**
	 * Interprets the port to fill member variables.
	 */
	private void interpret() {

		if (null == this.transport) {
			// Transport type
			SOAPBinding binding = null;
			for (ExtensibilityElement element: (Iterable<ExtensibilityElement>)port.getExtensibilityElements()) {
				if (element instanceof SOAPBinding) {
					binding = (SOAPBinding) element;
					break;
				}
			}
			if (binding != null) {
				if (binding.getTransportURI().equals(JBI_TRANSPORT_URI)) {
					this.transport = TransportImpl.JBI;
				} else if (binding.getTransportURI().equals(SBB2_TRANSPORT_URI)) {
					this.transport = TransportImpl.SBB2;
				} else if (binding.getTransportURI().equals(JMS_TRANSPORT_URI)) {
					this.transport = TransportImpl.JMS;
				} else if (binding.getTransportURI().equals(HTTP_TRANSPORT_URI)) {
					for (ExtensibilityElement element: (Iterable<ExtensibilityElement>)port.getExtensibilityElements()) {
						if (element instanceof SOAPAddress) {
							String uri = ((SOAPAddress) element)
									.getLocationURI();
							if (uri.toLowerCase().startsWith("https")) {
								this.transport = TransportImpl.HTTPS;
							} else {
								this.transport = TransportImpl.HTTP;
							}
						}
						break;
					}
				}
			}
			// we could not find out any transport
			if (this.transport == null) {
				this.transport = TransportImpl.UNKNOWN;
			}
		}
		interpreted = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#addExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement)
	 */
	public void addExtensibilityElement(final ExtensibilityElement arg0) {
		port.addExtensibilityElement(arg0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getBinding()
	 */
	public Binding getBinding() {
		return port.getBinding();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getDocumentationElement()
	 */
	public Element getDocumentationElement() {
		return port.getDocumentationElement();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getExtensibilityElements()
	 */
	public List getExtensibilityElements() {
		return port.getExtensibilityElements();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getName()
	 */
	public String getName() {
		return port.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#setBinding(javax.wsdl.Binding)
	 */
	public void setBinding(final Binding arg0) {
		port.setBinding(arg0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#setDocumentationElement(org.w3c.dom.Element)
	 */
	public void setDocumentationElement(final Element arg0) {
		port.setDocumentationElement(arg0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#setName(java.lang.String)
	 */
	public void setName(final String arg0) {
		port.setName(arg0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#setExtensionAttribute(javax.xml.namespace.QName, java.lang.Object)
	 */
	public void setExtensionAttribute(final QName arg0, final Object arg1) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getExtensionAttribute(javax.xml.namespace.QName)
	 */
	public Object getExtensionAttribute(final QName arg0) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getExtensionAttributes()
	 */
	public Map getExtensionAttributes() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#getNativeAttributeNames()
	 */
	public List getNativeAttributeNames() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.SwordfishPort#removeExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement)
	 */
	public ExtensibilityElement removeExtensibilityElement(
			ExtensibilityElement arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
}
