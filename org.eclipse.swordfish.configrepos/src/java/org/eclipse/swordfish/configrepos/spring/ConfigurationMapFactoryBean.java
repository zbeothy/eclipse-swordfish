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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.jxpath.JXPathContext;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;

/**
 * This class is a component bean to be used in the spring framework, and will render two types of
 * java.util.Map instance from a existing configuration. Taking into account the following example:<p/>
 * 
 * <pre>
 * ...
 * &amp;ltpolicy-processor name=&quot;policy&quot;&gt;
 * &amp;ltorder&gt;
 * &amp;ltsender&gt;
 * &amp;ltrequest&gt;
 * &amp;ltprocessing-unit&gt;
 * &amp;ltassertion value=&quot;compression&quot;/&gt;
 * &amp;ltis-mandatory value=&quot;false&quot;/&gt;
 * &amp;ltcomponent value=&quot;org.eclipse.swordfish.component.compression&quot;/&gt;
 * &amp;lt/processing-unit&gt;
 * &amp;ltprocessing-unit&gt;
 * &amp;ltassertion value=&quot;Signing&quot;/&gt;
 * &amp;ltis-mandatory value=&quot;false&quot;/&gt;
 * &amp;ltcomponent value=&quot;org.eclipse.swordfish.core.plugin.signing.SignerProcessingComponent&quot;/&gt;
 * &amp;lt/processing-unit&gt;
 * &amp;ltprocessing-unit&gt;
 * &amp;ltassertion value=&quot;Authentication&quot;/&gt;
 * &amp;ltis-mandatory value=&quot;false&quot;/&gt;
 * &amp;ltcomponent value=&quot;org.eclipse.swordfish.sec.auth.ClientAuthenticationProcessingComponent&quot;/&gt;
 * &amp;lt/processing-unit&gt;
 * &amp;lt/request&gt;
 * &amp;lt/sender&gt;
 * &amp;lt/order&gt;
 * &amp;lt/policy-processor&gt;
 * ...
 * </pre>
 * 
 * <h2>Simple Map</h2>
 * <p/> The bean "Map_A" will create a map of strings. Each key for the map will be identified by
 * the configuration path
 * 
 * <pre>
 * policy-processor.order.sender.request.processing-unit.assertion[@value]
 * </pre>
 * 
 * and its value will be composed of configuration property
 * 
 * <pre>
 * policy-processor.order.sender.request.processing-unit(*).component[@value]
 * </pre>
 * 
 * <h3>Example Map_A</h3>
 * 
 * <pre>
 * &amp;ltbean id=&quot;Map_A&quot;
 * class=&quot;org.eclipse.swordfish.configrepos.spring.ConfigurationMapFactoryBean&quot;&gt;
 * &amp;ltproperty name=&quot;basePath&quot;
 * value=&quot;policy-processor.order.sender.request.processing-unit&quot;/&gt;
 * &amp;ltproperty name=&quot;keyPath&quot; value=&quot;assertion[@value]&quot;/&gt;
 * &amp;ltproperty name=&quot;valuePath&quot; value=&quot;component[@value]&quot;/&gt;
 * &amp;ltproperty name=&quot;configuration&quot; ref=&quot;collections_factory_cfg&quot;/&gt;
 * &amp;lt/bean&gt;
 * </pre>
 * 
 * <h2>Double-Staged Map</h2>
 * <p/> The bean "Map_B" will create a Map of Sub-Maps. Each sub-map will be contained in the first
 * map with the key identified by the configuration path
 * 
 * <pre>
 * policy-processor.order.sender.request.processing-unit(*).assertion[@value]
 * </pre>
 * 
 * and will be composed of all values of the configuration nodes below
 * 
 * <pre>
 * policy - processor.order.sender.request.processing - unit
 * </pre>
 * 
 * with each child node name being the key for the submap.
 * <h3>Example Map_B</h3>
 * 
 * <pre>
 * &amp;ltbean id=&quot;Map_B&quot;
 * class=&quot;org.eclipse.swordfish.configrepos.spring.ConfigurationMapFactoryBean&quot;&gt;
 * &amp;ltproperty name=&quot;basePath&quot;
 * value=&quot;policy-processor.order.sender.request.processing-unit&quot;/&gt;
 * &amp;lt&lt;property name=&quot;keyPath&quot; value=&quot;assertion[@value]&quot;/&gt;
 * &amp;ltproperty name=&quot;configuration&quot; ref=&quot;collections_factory_cfg&quot;/&gt;
 * &amp;lt/bean&gt;
 * </pre>
 * 
 */
public class ConfigurationMapFactoryBean extends AbstractConfigurationCollectionFactoryBean {

    /** Key path relative to the basePath. */
    private String keyPath = null;

    /** Path to the value. */
    private String valuePath = null;

    /**
     * Instantiates a new configuration map factory bean.
     */
    public ConfigurationMapFactoryBean() {
        super();
    }

    /**
     * Gets the key path.
     * 
     * @return Returns the keyPath.
     */
    public String getKeyPath() {
        return this.keyPath;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the object type
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return Map.class;
    }

    /**
     * Gets the value path.
     * 
     * @return Returns the valuePath.
     */
    public String getValuePath() {
        return this.valuePath;
    }

    /**
     * Sets the key path.
     * 
     * @param keyPath
     *        The keyPath to set.
     */
    public void setKeyPath(final String keyPath) {
        this.keyPath = keyPath;
    }

    /**
     * Sets the value path.
     * 
     * @param valuePath
     *        The valuePath to set.
     */
    public void setValuePath(final String valuePath) {
        this.valuePath = valuePath;
    }

    /**
     * (non-Javadoc).
     * 
     * @return Object containing the map
     * 
     * @throws Exception
     *         in case of an error
     * 
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     */
    @Override
    protected Object createInstance() throws Exception {
        JXPathContext ctx = JXPathContext.newContext(((XMLConfiguration) this.getConfiguration()).getDocument());

        Iterator iter = ctx.iterate("/configuration/" + this.getBasePath() + "/" + this.keyPath);
        if ((null != this.logger) && this.logger.isDebugEnabled()) {
            this.logger.debug("Creating configuration map based on path='" + this.getBasePath() + "', key='" + this.keyPath
                    + "' and values='" + this.valuePath + "'");
        }
        HashMap result = new HashMap();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value =
                    (String) ctx.getValue("/configuration/" + this.getBasePath() + "[" + this.keyPath + "='" + key + "']/"
                            + this.valuePath);
            if ((null != this.logger) && this.logger.isDebugEnabled()) {
                this.logger.debug("Adding key-value pair '" + key + "'='" + value + "' to configuration map bean");
            }
            result.put(key, value);
        }

        return result;
    }
}
