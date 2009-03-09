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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.Test;
import static org.junit.Assert.*;

public class WSDLResourceTest {
	
	@Test
	public void appendContentWriterShouldReturnContentFromPersistentData() throws Exception {
		final String content = "sassa{ŠŸ§}";
		StringWriter writer = new StringWriter();
		
		WSDLResource wsdlResource = new WSDLResource();
		wsdlResource.setData(new PersistentData() {

			public InputStream getContent() throws IOException {
				return new ByteArrayInputStream(content.getBytes("utf8"));
			}

			public String getId() {
				return null;
			}
		});
		
		wsdlResource.appendContent(writer);
		
		assertThat("", writer.toString(), equalTo(content));
	}

}
