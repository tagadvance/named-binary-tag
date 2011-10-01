/**
 * Copyright 2011 Taggart Spilman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tag;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.MapEvictionListener;
import com.google.common.collect.MapMaker;

// TODO: refactor this to use CacheBuilder
// http://guava-libraries.googlecode.com/svn/trunk/javadoc/com/google/common/cache/CacheBuilder.html
public abstract class Cache<K, V> implements Function<K, V>,
	MapEvictionListener<K, V>, Map<K, V> {

    protected final Map<K, V> map;

    public Cache() {
	this.map = createMap();
    }

    protected Map<K, V> createMap() {
	return new MapMaker().softValues().evictionListener(this)
		.makeComputingMap(this);
    }

    @Override
    public abstract V apply(K key);

    @Override
    public void onEviction(K key, V value) {
    }

    @Override
    public int size() {
	return map.size();
    }

    @Override
    public boolean isEmpty() {
	return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
	return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
	return map.get(key);
    }

    @Override
    public V put(K key, V value) {
	return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
	return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
	map.putAll(m);
    }

    @Override
    public void clear() {
	map.clear();
    }

    @Override
    public Set<K> keySet() {
	return map.keySet();
    }

    @Override
    public Collection<V> values() {
	return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
	return map.entrySet();
    }

}