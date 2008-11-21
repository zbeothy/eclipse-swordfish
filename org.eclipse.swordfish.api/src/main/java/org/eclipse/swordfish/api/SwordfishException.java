/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oliver Wolf - initial API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.api;

/**
 * The base class for all the Swordfish framework exceptions
 *
 */
public class SwordfishException extends RuntimeException {

	private static final long serialVersionUID = -7160186353718171117L;

	public SwordfishException() {
	}

	public SwordfishException(String message) {
		super(message);
	}

	public SwordfishException(Throwable cause) {
		super(cause);
	}

	public SwordfishException(String message, Throwable cause) {
		super(message, cause);
	}

}
