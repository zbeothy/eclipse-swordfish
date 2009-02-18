/**
 *
 */
package org.eclipse.swordfish.core.test.util.mock;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.Hint;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;

/**
 * @author dwolz
 *
 */
public class AcceptAllFilterStrategy implements FilterStrategy {

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#filter(java.util.List, org.eclipse.swordfish.api.ReadOnlyRegistry, java.util.List)
	 */
	public List<Interceptor> filter(List<Interceptor> interceptors,
			ReadOnlyRegistry<Interceptor> registry, List<Hint<?>> hints) {
		List<Interceptor> sorted = new ArrayList<Interceptor>();
		for (Interceptor interceptor: interceptors) {
			sorted.add(interceptor);
		}
		return sorted;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 1;
	}

}
