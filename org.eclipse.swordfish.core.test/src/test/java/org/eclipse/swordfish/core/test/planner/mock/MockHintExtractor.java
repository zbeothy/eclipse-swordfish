/**
 *
 */
package org.eclipse.swordfish.core.test.planner.mock;

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
