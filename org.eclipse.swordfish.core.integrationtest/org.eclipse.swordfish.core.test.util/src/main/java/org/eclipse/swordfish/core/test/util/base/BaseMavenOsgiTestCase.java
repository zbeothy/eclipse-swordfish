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
package org.eclipse.swordfish.core.test.util.base;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.swordfish.core.test.util.spring.LocalFileSystemMavenRepository;
import org.eclipse.swordfish.core.test.util.spring.LocalMavenRepositoryLocator;
import org.springframework.core.io.Resource;
import org.springframework.osgi.test.provisioning.ArtifactLocator;

public class BaseMavenOsgiTestCase extends BaseOsgiTestCase {



    private static ArtifactLocator artifactLocator = new LocalMavenRepositoryLocator(
            new LocalFileSystemMavenRepository());

    protected String getBundle(String groupId, String artifactId) {
        return groupId + "," + artifactId + ","
                + getBundleVersion(groupId, artifactId);
    }

    private Properties dependencies;
    public static Resource getMavenRepositoryBundle(String groupId, String artifactId) {
        return artifactLocator.locateArtifact(groupId, artifactId, getBundleVersion(groupId, artifactId));

    }

    protected static String getBundleVersion(String groupId, String artifactId) {
        Properties dependencies = null;
        InputStream inputStream = null;
            try {
                inputStream = BaseMavenOsgiTestCase.class.getClassLoader().getResource("META-INF/maven/dependencies.properties").openStream();
                Properties prop = new Properties();
                prop.load(inputStream);
                dependencies = prop;
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Unable to load dependencies informations", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        throw new AssertionError();
                    }
                }
            }
        String version = dependencies.getProperty(groupId + "/" + artifactId
                + "/version");
        if (version == null) {
            throw new IllegalStateException(
                    "Unable to find dependency information for: " + groupId
                            + "/" + artifactId + "/version");
        }
        return version;
    }

    /**
     * Returns the ArtifactLocator used by this test suite. Subclasses should
     * override this method if the default locator (searching the local Maven2
     * repository) is not enough.
     *
     * <p>
     * <b>Note</b>: This method will be used each time a bundle has to be
     * retrieved; it is highly recommended to return a cached instance instead
     * of a new one each time.
     *
     * @return artifact locator used by this test.
     */
    @Override
    protected ArtifactLocator getLocator() {
        return artifactLocator;
    }

    /**
     * The location of the packaged OSGi bundles to be installed for this test.
     * Values are Spring resource paths. The bundles we want to use are part of
     * the same multi-project maven build as this project is. Hence we use the
     * localMavenArtifact helper method to find the bundles produced by the
     * package phase of the maven build (these tests will run after the
     * packaging phase, in the integration-test phase).
     *
     * JUnit, commons-logging, spring-core and the spring OSGi test bundle are
     * automatically included so do not need to be specified here.
     */
    @Override
    protected String[] getTestBundlesNames() {
        return new String[] {
                getBundle("org.eclipse", "org.eclipse.osgi.services"),
                getBundle("org.eclipse.equinox", "org.eclipse.equinox.cm"),
                getBundle("org.apache.geronimo.specs",
                        "geronimo-activation_1.1_spec"),
                getBundle("org.apache.servicemix.jbi",
                        "org.apache.servicemix.jbi.api"),
                getBundle("org.apache.servicemix.nmr",
                        "org.apache.servicemix.nmr.api"),
                getBundle("org.eclipse.swordfish", "org.eclipse.swordfish.api"),
                getBundle("org.eclipse.swordfish",
                        "org.eclipse.swordfish.core.planner"),
                getBundle("org.springframework.osgi",
                        "spring-osgi-core")
        };
    }
}
