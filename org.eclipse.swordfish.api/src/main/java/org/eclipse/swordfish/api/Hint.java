/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.api;

/**
 * A Hint is a piece of information extracted from a message exchange by a
 * {@link HintExtractor}. A List of Hints is passed to the
 * {@link FilterStrategy} and {@link SortingStrategy} which may use these
 * Hints in order to modify the interceptor chain in an appropriate way
 * for processing of the current message exchange. The Swordfish core
 * itself does not make any assumptions on these hints. 
 *
 * @param <T> the class of the information object wrapped by the Hint
 * instance.
 */
public interface Hint<T> {

	/**
	 * Indicate the class/interface of the information object wrapped
	 * by the receiver.
	 * @return the information object class.
	 */
	Class<T> getType();

	/**
	 * Get the information object wrapped by the receiver.
	 * @return the information extracted from the message exchange
	 * (must not be <code>null</code>).
	 */
	T getInfo();

}
