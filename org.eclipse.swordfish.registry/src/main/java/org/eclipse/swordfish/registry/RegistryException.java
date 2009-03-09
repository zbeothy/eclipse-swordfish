/*******************************************************************************
* Copyright (c) 2008, 2009 SOPERA GmbH.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* SOPERA GmbH - initial API and implementation
*******************************************************************************/
package org.eclipse.swordfish.registry;


public class RegistryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8500631841012079558L;

	public RegistryException(String message) {
		super(message);
	}

	public RegistryException(Throwable e) {
		super(e);
	}

	public RegistryException(String message, Throwable e) {
		super(message, e);
	}

}
