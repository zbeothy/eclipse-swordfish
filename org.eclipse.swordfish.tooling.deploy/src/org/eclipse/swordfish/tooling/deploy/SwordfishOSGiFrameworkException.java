/*******************************************************************************
 * Copyright (c) 2007 SOPERA GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.tooling.deploy;

/**
 * Thrown when trouble occurred during OSGi operations. 
 * 
 * @author Alex Tselesh
 */
public class SwordfishOSGiFrameworkException extends Exception {

	/**
	 * <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2520446210626850674L;

	/**
	 * @see java.lang.Exception#Exception()
	 */
	public SwordfishOSGiFrameworkException() {
		super();
	}

	/**
	 * @see java.lang.Exception#Exception(java.lang.String, java.lang.Throwable)
	 */
	public SwordfishOSGiFrameworkException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see java.lang.Exception#Exception(java.lang.String)
	 */
	public SwordfishOSGiFrameworkException(final String message) {
		super(message);
	}

	/**
	 * @see java.lang.Exception#Exception(java.lang.Throwable)
	 */
	public SwordfishOSGiFrameworkException(final Throwable cause) {
		super(cause);
	}
}
