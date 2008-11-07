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

package org.eclipse.swordfish.api;

/**
 * TODO: Andreas, Dietmar could you write the description for this item
 *
 * @param <T>
 */
public interface Hint<T> {

	public Class<T> getType();

	public T getInfo();

}
