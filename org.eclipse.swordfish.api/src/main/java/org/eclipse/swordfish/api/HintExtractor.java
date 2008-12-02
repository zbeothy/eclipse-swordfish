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

import java.util.List;

import javax.jbi.messaging.MessageExchange;

/**
 * The HintExtractor is a component which extracts {@link Hint} objects from
 * the current message exchange. It is registered as an OSGI service. Its
 * purpose is to provide a List of Hints to corresponding
 * {@link FilterStrategy} and {@link SortingStrategy} components which can
 * use them to shape the interceptor chain for appropriate processing of the
 * current message exchange.
 *
 */
public interface HintExtractor {

	/**
	 * Extracts hints from the given message exchange and return them as List.
	 * The Hints may be of different types.
	 * @return The hints as List. If no hint has been found, an empty List or
	 * <code>null</code> may be returned.
	 */
	List<Hint<?>> extractHints(MessageExchange messageExchange);

}
