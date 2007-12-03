/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.configrepos.spring;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryInternalException;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;

/**
 * This ResourceEditor will resolve SOP Configuration Repository Resources via a URL. In case the
 * provided URL has the 'sopcr://' protocol identifier, the ResourceEditor will consult a
 * configuration manager instance, who will use the elements of the URL to retreive the resource as
 * an org.springframework.core.io.InputStreamResource object.<p/> The resource editor has to be
 * registered with an ApplicationContext via a
 * 'org.springframework.beans.factory.config.CustomEditorConfigurer'. A typical configuration might
 * look as follows:<p/>
 * 
 * <pre>
 * &amp;ltbean id=&quot;sop_configurer&quot;
 * class=&quot;org.springframework.beans.factory.config.CustomEditorConfigurer&quot;&gt;
 * &amp;ltproperty name=&quot;customEditors&quot;&gt;
 * &amp;ltmap&gt;
 * &amp;ltentry key=&quot;org.springframework.core.io.Resource&quot;&gt;
 * &amp;ltbean class=&quot;org.eclipse.swordfish.configrepos.spring.ConfigurationRepositoryResourceEditor&quot;&gt;
 * &amp;ltproperty name=&quot;manager&quot; ref=&quot;manager_instance&quot;/&gt;
 * &amp;lt/bean&gt;
 * &amp;lt/entry&gt;
 * &amp;lt/map&gt;
 * &amp;lt/property&gt;
 * &amp;lt/bean&gt;
 * </pre>
 * 
 * The bean factory holding the above definition will allow assigning Resource objects to properties
 * of related beans, using a URL style notation:
 * 
 * <pre>
 * &amp;ltbean name=&quot;sop_resource_using_bean&quot; class=&quot;org.eclipse.swordfish.configrepos.mock.ResourceUsingMock&quot;&gt;
 * &amp;ltproperty name=&quot;target&quot; value=&quot;sopcr://SBB/comp/README.txt&quot;/&gt;
 * &amp;lt/bean&gt;
 * </pre>
 * 
 * which will load the resource 'README.txt' for the 'comp' component in the tree 'SBB'. The
 * following definition will load the same file, but in the tree 'OtherTree' and scope path
 * 'Location=Bonn':
 * 
 * <pre>
 * &amp;ltbean name=&quot;sop_resource_using_bean&quot; class=&quot;org.eclipse.swordfish.configrepos.mock.ResourceUsingMock&quot;&gt;
 * &amp;ltproperty name=&quot;target&quot; value=&quot;sopcr://OtherTree/Location=Bonn/comp/README.txt&quot;/&gt;
 * &amp;lt/bean&gt;
 * </pre>
 * 
 * The general URL format looks as follows:
 * 
 * <pre>
 * sopcr://&amp;lttree-qualifier&gt;?/(&amp;lttype&gt;=&amp;ltvalue&gt;/)*(&amp;ltcomponent&gt;/)?&amp;ltresource-name&gt;
 * </pre>
 * 
 */
public class ConfigurationRepositoryResourceEditor extends ResourceEditor {

    /** The Constant SOPCR_URL_PROTOCOL. */
    public static final String SOPCR_URL_PROTOCOL = "sopcr:";

    /** The manager. */
    private ConfigurationRepositoryManagerInternal manager = null;

    /** The tree qualifier. */
    private String treeQualifier = null;

    /** The scope path. */
    private ScopePath scopePath = null;

    /** The component. */
    private String component = null;

    /** The resource name. */
    private String resourceName = null;

    /** The sop url protocol. */
    private String sopUrlProtocol = SOPCR_URL_PROTOCOL;

    /**
     * Instantiates a new configuration repository resource editor.
     */
    public ConfigurationRepositoryResourceEditor() {
        this(new DefaultResourceLoader());
    }

    /**
     * The Constructor.
     * 
     * @param aResourceLoader
     *        which will be used to load resources
     */
    public ConfigurationRepositoryResourceEditor(final ResourceLoader aResourceLoader) {
        super(aResourceLoader);
    }

    /**
     * Default component name.
     * 
     * @return default component name
     */
    public String getComponent() {
        return this.component;
    }

    /**
     * Gets the manager.
     * 
     * @return Returns the manager.
     */
    public ConfigurationRepositoryManagerInternal getManager() {
        return this.manager;
    }

    /**
     * Return the default resource name.
     * 
     * @return default resource name
     */
    public String getResourceName() {
        return this.resourceName;
    }

    /**
     * Default scope path.
     * 
     * @return the default scope path
     */
    public ScopePath getScopePath() {
        return this.scopePath;
    }

    /**
     * Gets the sop url protocol.
     * 
     * @return Returns the sopUrlProtocol.
     */
    public String getSopUrlProtocol() {
        return this.sopUrlProtocol;
    }

    /**
     * Default tree qualifier.
     * 
     * @return tree qualifier
     */
    public String getTreeQualifier() {
        return this.treeQualifier;
    }

    /**
     * Sets the as text.
     * 
     * @param aURI
     *        which points to the resource
     * 
     * @see java.beans.PropertyEditor#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(final String aURI) {
        if (this.isSOPProtocol(aURI)) {
            try {
                ConfigurationRepositoryUri uri = new ConfigurationRepositoryUri(aURI);
                this.setValue(new InputStreamResource(this.manager.getResource((null == uri.treeQualifier) ? this.treeQualifier
                        : uri.treeQualifier, (null == uri.scopePath) ? this.scopePath : uri.scopePath,
                        (null == uri.componentName) ? this.component : uri.componentName,
                        (null == uri.resourceName) ? this.resourceName : uri.resourceName), "sop resource [" + aURI + "]"));
            } catch (ConfigurationRepositoryInternalException cre) {
                throw new IllegalArgumentException("error while fetching resource.");
            }
        } else {
            super.setAsText(aURI);
        }
    }

    /**
     * Set default component name.
     * 
     * @param component
     *        name to be used as default
     */
    public void setComponent(final String component) {
        this.component = component;
    }

    /**
     * Sets the manager.
     * 
     * @param manager
     *        The manager to set.
     */
    public void setManager(final ConfigurationRepositoryManagerInternal manager) {
        this.manager = manager;
    }

    /**
     * Set default resource name.
     * 
     * @param resourceName
     *        to be used as a default
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Set default scope path.
     * 
     * @param scopePath
     *        to be used as default
     */
    public void setScopePath(final ScopePath scopePath) {
        this.scopePath = scopePath;
    }

    /**
     * Sets the sop url protocol.
     * 
     * @param sopUrlProtocol
     *        The sopUrlProtocol to set.
     */
    public void setSopUrlProtocol(final String sopUrlProtocol) {
        this.sopUrlProtocol = sopUrlProtocol;
    }

    /**
     * Sets the tree qualifier.
     * 
     * @param treeQualifier
     *        to be used as a default
     */
    public void setTreeQualifier(final String treeQualifier) {
        this.treeQualifier = treeQualifier;
    }

    /**
     * Checks if is SOP protocol.
     * 
     * @param aUri
     *        which will be analyzed whether it belongs to the SOP Configuration Repository
     * 
     * @return true in case the URI points to the location in the SOP Configuration Repository
     */
    private boolean isSOPProtocol(final String aUri) {
        return aUri.startsWith(SOPCR_URL_PROTOCOL);
    }

    /**
     * The Class ConfigurationRepositoryUri.
     * 
     */
    protected static class ConfigurationRepositoryUri {

        /** The tree qualifier. */
        private String treeQualifier = null;

        /** The scope path. */
        private ScopePath scopePath = null;

        /** The component name. */
        private String componentName = null;

        /** The resource name. */
        private String resourceName = null;

        /**
         * The Constructor.
         * 
         * @param aUri
         *        which should be internalized
         */
        ConfigurationRepositoryUri(final String aUri) {
            super();

            if (!aUri.startsWith(SOPCR_URL_PROTOCOL + "//")) throw new IllegalArgumentException("Invalid URI '" + aUri + "'");

            String[] elements = StringUtils.split(aUri, '/');
            int position = 1;
            this.treeQualifier = elements[position++];

            while (elements[position].indexOf('=') != -1) {
                // add scope path element
                if (null == this.scopePath) {
                    this.scopePath = new ScopePathImpl();
                }

                String[] tuple = StringUtils.split(elements[position++], '-');
                if (tuple.length != 2)
                    throw new IllegalArgumentException("Malformed scopepath element '" + elements[position - 1] + "'");
                PathPart part = new PathPartImpl();
                part.setType(tuple[0]);
                part.setValue(tuple[1]);
                this.scopePath.getPathPart().add(part);
            }

            if (elements.length - position > 1) {
                this.componentName = elements[position++];
                this.resourceName = elements[position++];
            } else {
                this.resourceName = elements[position++];
            }

            if (elements.length != position) throw new IllegalArgumentException("Malformed resource URI");
        }

        /**
         * Gets the component name.
         * 
         * @return Returns the componentName.
         */
        public String getComponentName() {
            return this.componentName;
        }

        /**
         * Gets the resource name.
         * 
         * @return Returns the resourceName.
         */
        public String getResourceName() {
            return this.resourceName;
        }

        /**
         * Gets the scope path.
         * 
         * @return Returns the scopePath.
         */
        public ScopePath getScopePath() {
            return this.scopePath;
        }

        /**
         * Gets the tree qualifier.
         * 
         * @return Returns the treeQualifier.
         */
        public String getTreeQualifier() {
            return this.treeQualifier;
        }
    };
}
