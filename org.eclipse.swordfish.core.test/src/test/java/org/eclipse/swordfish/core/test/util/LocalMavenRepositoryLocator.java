package org.eclipse.swordfish.core.test.util;

import java.lang.reflect.Method;

import org.springframework.core.io.Resource;
import org.springframework.osgi.test.provisioning.ArtifactLocator;
import org.springframework.osgi.test.provisioning.internal.LocalFileSystemMavenRepository;

public class LocalMavenRepositoryLocator implements ArtifactLocator {
    private   LocalFileSystemMavenRepository delegate;
    private static final char SLASH_CHAR = '/';

    public LocalMavenRepositoryLocator() {
        delegate = new LocalFileSystemMavenRepository();
    }
    public LocalMavenRepositoryLocator(LocalFileSystemMavenRepository delegate) {
        this.delegate = delegate;
    }
    public LocalFileSystemMavenRepository getDelegate() {
        return delegate;
    }
    public void setDelegate(LocalFileSystemMavenRepository delegate) {
        this.delegate = delegate;
    }

    public Resource locateArtifact(String groupId, String artifactId, String version, String type) {
        Resource localMavenBundle = delegate.locateArtifact(groupId, artifactId, version, type); // init() is private
        try {
            Method method = LocalFileSystemMavenRepository.class.getDeclaredMethod("localMavenBundle", String.class, String.class, String.class, String.class);
            method.setAccessible(true);
            localMavenBundle = (Resource) method.invoke(delegate, groupId, artifactId, version, type);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return localMavenBundle;
    }

    public Resource locateArtifact(String group, String id, String version) {
        return locateArtifact(group, id, version, DEFAULT_ARTIFACT_TYPE);
    }

}
