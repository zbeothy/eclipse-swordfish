/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Mattes - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.planner;

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
public class DefaultHintExtractor implements HintExtractor {

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.HintExtractor#extractHints(javax.jbi.messaging.MessageExchange)
	 */
	public List<Hint<?>> extractHints(MessageExchange messageExchange) {
		List<Hint<?>> hints = new ArrayList<Hint<?>>();
		HashMap<String,Boolean> map = new HashMap<String,Boolean>();
		//map.put(TestInterceptor1.class.getCanonicalName(), true);
		Hint<?> hint = new DefaultHint(map);
		hints.add(hint);
		return hints;
	}

}
