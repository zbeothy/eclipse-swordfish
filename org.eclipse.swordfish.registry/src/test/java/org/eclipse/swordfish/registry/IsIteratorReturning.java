/*******************************************************************************
* Copyright (c) 2008, 2009 SOPERA GmbH.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* SOPERA GmbH - initial API and implementation
*******************************************************************************/
package org.eclipse.swordfish.registry;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsIteratorReturning<T> extends TypeSafeMatcher<Iterator<T>> {

	private Collection<T> elementMatchers;
	
	public IsIteratorReturning(T... matchers) {
		elementMatchers = Arrays.asList(matchers);
	}
	
	public IsIteratorReturning(Collection<T> matchers) {
		elementMatchers = matchers;
	}

	@Override
	public boolean matchesSafely(Iterator<T> iter) {
		List<T> expectedCopy = new ArrayList<T>(elementMatchers);

		while (iter.hasNext()) {
			T el = iter.next();
			if (expectedCopy.contains(el)) {
				expectedCopy.remove(el);
			} else {
				return false;
			}
		}
		return expectedCopy.isEmpty();
	}

	public void describeTo(Description description) {
        description.appendValueList("[", ",", "]", elementMatchers);
	}

	@Factory
	public static <T> Matcher<Iterator<T>> isIteratorReturning(T... matchers ) {
		return new IsIteratorReturning<T>(matchers);
	}

	@Factory
	public static <T> Matcher<Iterator<T>> isIteratorReturning(Collection<T> matchers ) {
		return new IsIteratorReturning<T>(matchers);
	}
}