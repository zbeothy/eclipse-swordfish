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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import static java.util.Collections.emptySet;


public class InMemoryRepositoryImpl implements InMemoryRepository {
	
	private Map<QName, Set<WSDLResource>> portTypeWsdls = new HashMap<QName, Set<WSDLResource>>();

	private Map<QName, Set<WSDLResource>> servicesRefPortType = new HashMap<QName, Set<WSDLResource>>();

	private Map<String, WSDLResource> byId = new HashMap<String, WSDLResource>();

	/* 
	 * @see org.eclipse.swordfish.registry.WSDLRepository#getByPortTypeName(javax.xml.namespace.QName)
	 */
	public ListResource<WSDLResource> getByPortTypeName(QName portTypeName) {
		return get(portTypeName, portTypeWsdls);
	}

	public ListResource<WSDLResource> getReferencingPortType(QName portTypeName) {
		return get(portTypeName, servicesRefPortType);
	}

	public WSDLResource getWithId(String id) {
		return byId.get(id);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.registry.WSDLRepository#registerByPortTypeName(javax.xml.namespace.QName, org.eclipse.swordfish.registry.WSDLResource)
	 */
	public void registerByPortTypeName(QName name, WSDLResource wsdl) {
		register( name, wsdl, portTypeWsdls);
	}

	public void registerServiceRefPortType(QName name, WSDLResource wsdl) {
		register( name, wsdl, servicesRefPortType);
	}
	
	public void registerById(String id, WSDLResource wsdl) {
		byId.put(id, wsdl);	
	}

	private <T extends Resource> ListResource<T> get(QName name, Map<QName, Set<T>> wsdls) {
		Set<T> referencing = wsdls.get(name);
		if (referencing == null) {
			referencing =  emptySet();
		}
		return new ListResource<T>(referencing);
	}
	
	private <T extends Resource> void register(QName name, T wsdl, Map<QName, Set<T>> wsdls) {
		Set<T> matching = wsdls.get(name);
		if (matching == null) {
			matching = new HashSet<T>();
			wsdls.put(name, matching);
		}
		matching.add(wsdl);
	}
}
