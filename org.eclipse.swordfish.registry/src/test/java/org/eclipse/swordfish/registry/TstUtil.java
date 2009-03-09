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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.easymock.IAnswer;

public final class TstUtil {

	public static WSDLResource createWSDLResource(final String id) {
		return new WSDLResource() {
			@Override
			public String getId() {
				return id;
			}
		};
	}

	public static WSDLResource createWSDLResource(final String id, final String content) {
		return new WSDLResource() {
			@Override
			public String getId() {
				return id;
			}

			@Override
			public void appendContent(Writer writer) throws IOException {
				writer.append(content);
			}
		};
	}
	
	public static <K, V> Map<K, V> asMap(Entry<K, V>...entries) {
		Map<K, V> result = new HashMap<K, V>(entries.length);
		for (Entry<K, V> entry : entries) {
			result.put(entry.key, entry.value);
		}
		return result;
	}
	
	public static <K, V> Entry<K, V> entry(K key, V value) {
		return new Entry<K, V>(key, value);
	}
	
	static class Entry<K, V> {
		public K key;
		public V value;
		
		public Entry(K k, V v) {
			key = k;
			value = v;
		}
	}

	
	public static <T> IAnswer<Iterator<T>> asIterator(final T... items) {
		return new IAnswer<Iterator<T>>() {
			public Iterator<T> answer() {
				List<T> result = new ArrayList<T>();
				for (T item : items) {
					result.add(item);
				}
				return result.iterator();
			}
		};		
	}

}
