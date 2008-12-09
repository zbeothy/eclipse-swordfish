/**
 *
 */
package org.eclipse.swordfish.core.test.util.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SortingStrategy;
import org.eclipse.swordfish.api.SwordfishException;

/**
 * @author dwolz
 *
 */
public class MockSortingStrategy implements SortingStrategy {

	public List<Interceptor> sort(Set<Interceptor> interceptorSet,
			ReadOnlyRegistry<Interceptor> arg1) throws SwordfishException {
		// TODO Auto-generated method stub
		return new ArrayList<Interceptor>(interceptorSet);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
