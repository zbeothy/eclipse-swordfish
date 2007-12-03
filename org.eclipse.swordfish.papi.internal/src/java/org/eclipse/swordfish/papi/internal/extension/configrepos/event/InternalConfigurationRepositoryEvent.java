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
package org.eclipse.swordfish.papi.internal.extension.configrepos.event;

/**
 * Instances of types implementing this interface will be propagated in case the configuration /
 * resource data in any source - remote or local - has changed and the configuration manager of the
 * SBB has taken notice of it.
 * 
 */
public interface InternalConfigurationRepositoryEvent {

    /**
     * Will return the specific scope path for which the configuration / resource has changed.
     * Returns 'null' in case the change event applies to any configuration / resource tree.
     * 
     * @return String with the identifier this event is related to.
     */
    String getScopePath();

    /**
     * Will return the target tree qualifier the configuration / resource change applies to. This
     * qualifier has usually also be used to pre-select the configuration / resource tree when
     * fetching these in the first place. Will be 'null' in case the change event applies to any
     * configuration / resource tree.
     * 
     * @return String with the qualifier name
     */
    String getTreeQualifier();
}
