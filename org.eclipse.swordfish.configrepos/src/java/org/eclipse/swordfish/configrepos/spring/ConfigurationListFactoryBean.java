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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;

/**
 * This factory will transform Jakarta commons-configuration protperties into Java Collection Lists,
 * which can be used ins the Spring Framework to configure JavaBeans.<b/> To create a List which is
 * assigned to a property of a spring bean, you will have to do two things:
 * <H3>Create a listable configuration node</H3>
 * First you should create a node in your XML configuration which can be listed. This might look
 * like this:
 * 
 * <pre>
 * &amp;ltconfiguration&gt;
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
 * &amp;ltcomponent
 * value=&quot;org.eclipse.swordfish.core.plugin.signing.SignerProcessingComponent&quot;/&gt;
 * &amp;lt/processing-unit&gt;
 * &amp;lt/request&gt;
 * &amp;ltsender&gt;
 * &amp;lt/order&gt;
 * &amp;lt/policy-processor name=&quot;policy&quot;&gt;
 * &lt;&amp;ltconfiguration&gt;
 * </pre>
 * 
 * The example will create a list of the names of all components which are part of the policy
 * processing.
 * <H3>Create a Factory</H3>
 * The following spring bean definition creates a factory which will create a Java Collections List.
 * It can be integrated as a sub-bean to any other bean definition, or can be created a stand-alone
 * Spring bean, which thereafter can be referenced by other definitions via a <code>&ltref /></code>
 * entry.
 * 
 * <pre>
 * &amp;ltbean id=&quot;org.eclipse.swordfish.configrepos.spring.ConfigurationListFactoryBean&quot;
 * class=&quot;org.eclipse.swordfish.configrepos.spring.ConfigurationListFactoryBean&quot;&gt;
 * &amp;ltproperty name=&quot;basePath&quot;
 * value=&quot;policy-processor/order/sender/request/processing-unit/assertion&quot;/&gt;
 * &amp;ltproperty name=&quot;attribute&quot; value=&quot;value&quot;/&gt;
 * &amp;ltproperty name=&quot;configuration&quot; ref=&quot;collections_factory_cfg&quot;/&gt;
 * &amp;lt/bean&gt;
 * </pre>
 * 
 */
public class ConfigurationListFactoryBean extends AbstractConfigurationCollectionFactoryBean {

    /**
     * Instantiates a new configuration list factory bean.
     */
    public ConfigurationListFactoryBean() {
        super();
    }

    /**
     * Gets the object type.
     * 
     * @return Class or type which will be created by this factory. Will return java.util.LinkedList
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return LinkedList.class;
    }

    /**
     * Creates the instance.
     * 
     * @return Object is the instance which has been created
     * 
     * @throws Exception
     *         in case the instance could not be created
     * 
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#createInstance()
     */
    @Override
    protected Object createInstance() throws Exception {
        if ((null == this.getConfiguration()) && (null == this.getBasePath()))
            throw new IllegalArgumentException("Setup error. Missing properties ("
                    + (null == this.getConfiguration() ? "configuration" : "") + (null == this.getBasePath() ? "basePath" : "")
                    + ".");

        JXPathContext xpc = JXPathContext.newContext(((XMLConfiguration) this.getConfiguration()).getDocument());
        Iterator iter = xpc.iterate("/configuration/" + StringUtils.stripStart(this.getBasePath(), "/"));
        ArrayList result = new ArrayList();
        while (iter.hasNext()) {
            result.add(iter.next());
        }

        return result;
    }
}
