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

import java.io.Serializable;

/**
 * TODO: This class should be enum
 * The Class Transport.
 */
@Deprecated
public final class TransportImpl implements Serializable, Transport {

	/** HTTP constant. */
	public static final TransportImpl HTTP = new TransportImpl(
			Transport.HTTP_STR);
	/** HTTPS constant. */
	public static final TransportImpl HTTPS = new TransportImpl(
			Transport.HTTPS_STR);
	/** JMS constant. */
	public static final TransportImpl JMS = new TransportImpl(
			Transport.JMS_STR);
	/** JBI local constant. */
	public static final TransportImpl JBI = new TransportImpl(
			Transport.JBI_STR);
	/** JBI local constant. */
	public static final TransportImpl SBB2 = new TransportImpl(
			Transport.SBB2_STR);
	/** unknown constant. */
	public static final TransportImpl UNKNOWN = new TransportImpl(
			TransportImpl.UNKNOWN_STR);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9058525248509994123L;

	/** unknown transport for instance by using locator. */
	private static final String UNKNOWN_STR = "UNKNOWN";

	/** value. */
	private String value;

	/**
	 * private constructor.
	 *
	 * @param value the value
	 */
	private TransportImpl(final String value) {
		this.value = value;
	}

	/**
	 * from String.
	 *
	 * @param value the value
	 *
	 * @return enum
	 */
	public static Transport fromString(final String value) {
		if (HTTP_STR.equals(value)) {
			return HTTP;
		}
		if (HTTPS_STR.equals(value)) {
			return HTTPS;
		}
		if (JMS_STR.equals(value)) {
			return JMS;
		}
		if (JBI_STR.equals(value)) {
			return JBI;
		}
		if (SBB2_STR.equals(value)) {
			return SBB2;
		}
		if (UNKNOWN_STR.equals(value)) {
			return UNKNOWN;
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.Transport#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof TransportImpl) {
			if (value.equals(((TransportImpl) obj).value)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.nmr.wsdl.Transport#toString()
	 */
	@Override
	public String toString() {
		return value;
	}
}
