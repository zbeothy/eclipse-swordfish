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
package org.eclipse.swordfish.configrepos;

// FIXME import org.apache.log4j.Logger;

/**
 * The Interface RepositorySource.
 * 
 */
public interface RepositorySource {

    /**
     * Close this configuration source. The instance shall thereafter not be required to be able to
     * service additional requests for configurations.
     */
    void close();

    /**
     * Logger set method.
     * 
     * @param aInstance
     *        the a instance
     */
    // FIXME void setLogger(Logger aLogger);
    /**
     * Logger get method
     * 
     * @return the logger which has been assigned to the related property
     */
    // FIXME Logger getLogger();
    /**
     * Will force flushing any transient data which might have influence on subsequent calls to the
     * getConfiguration() and getResource() methods of the repository source tree.
     * 
     * @param aInstance
     *        is the instance id which shall be flushed. In case "null" is provided, the call is
     *        referring to all instances.
     */
    void resynchronize(final String aInstance);
}
