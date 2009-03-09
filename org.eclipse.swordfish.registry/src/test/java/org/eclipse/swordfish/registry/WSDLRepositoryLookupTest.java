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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.eclipse.swordfish.registry.TstData.*;
import static org.eclipse.swordfish.registry.IsIteratorReturning.isIteratorReturning;

import org.junit.Test;


public class WSDLRepositoryLookupTest {

	private InMemoryRepositoryImpl wsdlRepository = new InMemoryRepositoryImpl(); 

	private WSDLResource wsdl_1 = new WSDLResource();
	private WSDLResource wsdl_2 = new WSDLResource();
	private WSDLResource wsdl_3 = new WSDLResource();

	@Test
	public void shouldReturnWSDLsWithMatchingPortTypeNames() {
		wsdlRepository.registerByPortTypeName(PORT_TYPE_NAME_11, wsdl_1);
		wsdlRepository.registerByPortTypeName(PORT_TYPE_NAME_11, wsdl_2);
		wsdlRepository.registerByPortTypeName(PORT_TYPE_NAME_12, wsdl_3);

		ListResource<WSDLResource> wsdls = wsdlRepository.getByPortTypeName(PORT_TYPE_NAME_11);
		assertThat(wsdls.getResources(), isIteratorReturning(wsdl_1, wsdl_2));
	}

	@Test
	public void shouldReturnWSDLsReferencingSpecifiedPortType() {
		wsdlRepository.registerServiceRefPortType(PORT_TYPE_NAME_11, wsdl_1);
		wsdlRepository.registerServiceRefPortType(PORT_TYPE_NAME_11, wsdl_2);
		wsdlRepository.registerServiceRefPortType(PORT_TYPE_NAME_12, wsdl_3);
		
		ListResource<WSDLResource> wsdls = wsdlRepository.getReferencingPortType (PORT_TYPE_NAME_11);
		assertThat(wsdls.getResources(), isIteratorReturning(wsdl_1, wsdl_2));
	}

	@Test
	public void shouldReturnWSDLsWithMatchingId() {
		wsdlRepository.registerById(WSDL_ID_1, wsdl_1);
		wsdlRepository.registerById(WSDL_ID_2, wsdl_2);
	
		assertEquals(wsdl_1, wsdlRepository.getWithId(WSDL_ID_1));
	}
	
	private static <T> List<T> asList(T... objects) {
		List<T> result = new ArrayList<T>();
		Collections.addAll(result, objects);
		return result;
	}
}
