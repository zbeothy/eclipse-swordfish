package org.eclipse.swordfish.core.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BaseMavenOsgiTestCase extends BaseOsgiTestCase {

    protected String getBundle(String groupId, String artifactId) {
        return groupId + "," + artifactId + ","
                + getBundleVersion(groupId, artifactId);
    }

    private Properties dependencies;

    protected String getBundleVersion(String groupId, String artifactId) {
        if (dependencies == null) {
            try {
                File f = new File(System.getProperty("basedir"),
                        "target/test-classes/dependencies.properties");
                Properties prop = new Properties();
                prop.load(new FileInputStream(f));
                dependencies = prop;
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Unable to load dependencies informations", e);
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
