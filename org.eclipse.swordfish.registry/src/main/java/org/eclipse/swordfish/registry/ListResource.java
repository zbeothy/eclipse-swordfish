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

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

public class ListResource<T extends Resource> implements Resource {

	private final Collection<T> resources;

	public ListResource(Collection<T> resources) {
		this.resources = resources;
	}

	public String getId() {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public String getContentType() {
		return "application/xml";
	}
	
	public String getCharacterEncoding() {
		return "UTF-8";
	}

	public void appendContent(Writer writer) throws IOException {
		StringBuffer response =  new StringBuffer(1000);

		response.append("<wsdlList>\n");
		for (Resource resource : resources) {
			response.append("  <url>./" + resource.getId() + "</url>\n");
		}
		response.append("</wsdlList>\n");

		writer.append(response);
	}
	
	public Iterator<T> getResources() {
		return resources.iterator();
	}

}