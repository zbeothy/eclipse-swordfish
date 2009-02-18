/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dietmar Wolz - planner API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.core.test.util.spring;

import org.springframework.core.io.Resource;
import org.springframework.osgi.test.provisioning.ArtifactLocator;

/**
 * @author Dietmar Wolz
 *
 */
public class LocalMavenRepositoryLocator implements ArtifactLocator {
	
    private LocalFileSystemMavenRepository localFileSystemMavenRepository = null;

    public LocalMavenRepositoryLocator(LocalFileSystemMavenRepository repository) {
    	localFileSystemMavenRepository = repository;
    	localFileSystemMavenRepository.init();
    }

	/**
	 * Find a local maven artifact. First tries to find the resource as a
	 * packaged artifact produced by a local maven build, and if that fails will
	 * search the local maven repository.
	 * 
	 * @param groupId - the groupId of the organization supplying the bundle
	 * @param artifactId - the artifact id of the bundle
	 * @param version - the version of the bundle
	 * @param type - the extension type of the artifact
	 * @return
	 */
	public Resource locateArtifact(String groupId, String artifactId, String version, String type) {
		Resource localMavenBundle = localFileSystemMavenRepository.localMavenBundle(groupId, artifactId, version, type);
		return localMavenBundle;
	}

	/**
	 * Find a local maven artifact. First tries to find the resource as a
	 * packaged artifact produced by a local maven build, and if that fails will
	 * search the local maven repository.
	 * 
	 * @param groupId - the groupId of the organization supplying the bundle
	 * @param artifactId - the artifact id of the bundle
	 * @param version - the version of the bundle
	 * @return the String representing the URL location of this bundle
	 */
	public Resource locateArtifact(String groupId, String artifactId, String version) {
		return locateArtifact(groupId, artifactId, version, DEFAULT_ARTIFACT_TYPE);
	}

}
