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
/**
 *
 */
package org.eclipse.swordfish.core.test.util.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Hint;
import org.eclipse.swordfish.api.HintExtractor;

/**
 * @author dwolz
 *
 */
public class MockHintExtractor implements HintExtractor {

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.HintExtractor#extractHints(javax.jbi.messaging.MessageExchange)
	 */
	public List<Hint<?>> extractHints(MessageExchange messageExchange) {
		List<Hint<?>> hints = new ArrayList<Hint<?>>();
		HashMap<String,Boolean> map = new HashMap<String,Boolean>();
		map.put(MockInterceptor.class.getCanonicalName(), true);
		Hint<?> hint = new MapBasedHint(map);
		hints.add(hint);
		return hints;
	}

}
