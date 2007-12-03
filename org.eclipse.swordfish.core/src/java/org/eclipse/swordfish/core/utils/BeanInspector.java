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
package org.eclipse.swordfish.core.utils;

import java.lang.reflect.Method;

/**
 * Small utility to conveniently dump the state of java beans.
 * 
 */
public class BeanInspector {

    /**
     * Bean to string.
     * 
     * @param bean
     *        the bean
     * 
     * @return the string
     */
    public static String beanToString(final Object bean) {
        final Object[] args = {};
        StringBuffer buf = new StringBuffer(bean.toString()).append(":\n");
        Class clazz = bean.getClass();
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String name = method.getName();
            if ((name.startsWith("get")) && (0 == method.getParameterTypes().length)) {
                // method is a getter
                buf.append(method.getName().substring(3, name.length())).append(" : ");
                try {
                    Object val = method.invoke(bean, args);
                    String valString = (null != val) ? val.toString() : "<NULL>";
                    buf.append(valString).append("\n");
                } catch (Exception e) {
                    buf.append(e.getMessage()).append("\n");
                }
            }
        }
        buf.append("---------------------------------------------------------------------------\n");
        return new String(buf);
    }

    /**
     * prevents instantiation.
     */
    private BeanInspector() {

    }

}
