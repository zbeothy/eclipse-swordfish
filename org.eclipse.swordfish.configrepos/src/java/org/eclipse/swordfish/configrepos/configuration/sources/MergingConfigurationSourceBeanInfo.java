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
package org.eclipse.swordfish.configrepos.configuration.sources;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

/**
 * The Class MergingConfigurationSourceBeanInfo.
 * 
 */
public final class MergingConfigurationSourceBeanInfo implements BeanInfo {

    /**
     * Instantiates a new merging configuration source bean info.
     */
    public MergingConfigurationSourceBeanInfo() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the additional bean info
     * 
     * @see java.beans.BeanInfo#getAdditionalBeanInfo()
     */
    public BeanInfo[] getAdditionalBeanInfo() {
        return new BeanInfo[] {};
    }

    /**
     * (non-Javadoc).
     * 
     * @return the bean descriptor
     * 
     * @see java.beans.BeanInfo#getBeanDescriptor()
     */
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(MergingConfigurationSource.class);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the default event index
     * 
     * @see java.beans.BeanInfo#getDefaultEventIndex()
     */
    public int getDefaultEventIndex() {
        return 0;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the default property index
     * 
     * @see java.beans.BeanInfo#getDefaultPropertyIndex()
     */
    public int getDefaultPropertyIndex() {
        // Auto-generated method stub
        return 0;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the event set descriptors
     * 
     * @see java.beans.BeanInfo#getEventSetDescriptors()
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        return new EventSetDescriptor[] {};
    }

    /**
     * No icon will be provided. Return is null.
     * 
     * @param iconKind
     *        the icon kind
     * 
     * @return the icon
     * 
     * @see java.beans.BeanInfo#getIcon(int)
     */
    public Image getIcon(final int iconKind) {
        return null;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the method descriptors
     * 
     * @see java.beans.BeanInfo#getMethodDescriptors()
     */
    public MethodDescriptor[] getMethodDescriptors() {
        // Auto-generated method stub
        return null;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the property descriptors
     * 
     * @see java.beans.BeanInfo#getPropertyDescriptors()
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            return new PropertyDescriptor[] {
                    new IndexedPropertyDescriptor("configSources", MergingConfigurationSource.class, "getConfigSources",
                            "setConfigSources", "getConfigSource", "setConfigSource"),
                    new PropertyDescriptor("strictMerging", MergingConfigurationSource.class, "isStrictMerging", "setStrictMerging")};
        } catch (IntrospectionException ie) {
            // FIXME Should we do something about this?
            return null;
        }
    }
}
