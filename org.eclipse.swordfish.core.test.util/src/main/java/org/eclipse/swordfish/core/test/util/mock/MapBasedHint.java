package org.eclipse.swordfish.core.test.util.mock;

import java.util.Map;

import org.eclipse.swordfish.api.Hint;

public class MapBasedHint implements Hint<Map<String,Boolean>> {

	private Map<String,Boolean> useInterceptor;

	public MapBasedHint(Map<String,Boolean> useInterceptor) {
		setUseInterceptor(useInterceptor);
	}

	/**
	 * @param useInterceptor the useInterceptor to set
	 */
	public void setUseInterceptor(Map<String,Boolean> useInterceptor) {
		this.useInterceptor = useInterceptor;
	}

	public Map<String,Boolean> getInfo() {
		// TODO Auto-generated method stub
		return useInterceptor;
	}

	public Class<Map<String,Boolean>> getType() {
		// TODO Auto-generated method stub
		return (Class<Map<String,Boolean>>) useInterceptor.getClass();
	}


}
