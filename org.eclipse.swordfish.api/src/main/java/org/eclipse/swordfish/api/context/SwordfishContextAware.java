/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oliver Wolf - initial API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.api.context;

/**
 * Interface to be implemented by any object that wishes to be notified of the SwordfishContext.
 * The implementor should be published as the osgi service with name org.eclipse.swordfish.api.context.SwordfishContextAware
 *
 */
public interface SwordfishContextAware {
    public void setContext(SwordfishContext swordfishContext);
}
