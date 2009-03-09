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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileBasedLoader implements Loader {

	private File wsdlDirectory; 
	
	FileBasedLoader() {
	}

	public void setDirectory(String directoryName)  throws RegistryException {
		setDirectory(new File(directoryName));
	}
	
	public void setDirectory(File directory) throws RegistryException {
		if (!(directory.exists() && directory.isDirectory())) {
			throw new RegistryException("The directory "  + directory.getName() + "specified to contain the registry WSDL's does either not exist or is not a directory.");			
		}
		wsdlDirectory = directory;
	}

	public void fill(InMemoryRepository repos) throws RegistryException {
		File[] files = wsdlDirectory.listFiles();
		
		for (File file : files) {
			WSDLResource wsdl = new WSDLResource();
			wsdl.setData(new FileData(file));
			wsdl.register(repos);
		}
	}
	
	static class FileData implements PersistentData {
		private File file;

		FileData(File file) {
			this.file = file;
		}
		
		public String getId() {
			return file.getName();	
		}
		
		public InputStream getContent() throws IOException {
			return new FileInputStream(file);
		}
	}
}
