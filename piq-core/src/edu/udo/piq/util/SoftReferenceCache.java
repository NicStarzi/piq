package edu.udo.piq.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class SoftReferenceCache<K, V> {
	
	protected final Map<K, Entry<K, V>> entryMap = createMapImplementation();
	protected final ReferenceQueue<V> refQ = new ReferenceQueue<>();
	
	public V put(K key, V value) {
		if (value == null) {
			throw new IllegalArgumentException("value == null");
		}
		removeClearedReferences();
		
		Entry<K, V> newEntry = new Entry<>(key, value, refQ);
		Entry<K, V> oldEntry = entryMap.put(key, newEntry);
		
		if (oldEntry == null) {
			return null;
		}
		return oldEntry.get();
	}
	
	public V get(K key) {
		removeClearedReferences();
		
		Entry<K, V> entry = entryMap.get(key);
		
		if (entry == null) {
			return null;
		}
		return entry.get();
	}
	
	public V remove(K key) {
		removeClearedReferences();
		
		Entry<K, V> oldEntry = entryMap.remove(key);
		
		if (oldEntry == null) {
			return null;
		}
		return oldEntry.get();
	}
	
	public void clear() {
		removeClearedReferences();
		entryMap.clear();
	}
	
	public int getSize() {
		removeClearedReferences();
		return entryMap.size();
	}
	
	public boolean isEmpty() {
		removeClearedReferences();
		return entryMap.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	protected void removeClearedReferences() {
		Entry<K, V> entry;
		while ((entry = (Entry<K, V>) refQ.poll()) != null) {
			entryMap.remove(entry.getKey());
		}
	}
	
	protected Map<K, Entry<K, V>> createMapImplementation() {
		return new HashMap<>();
	}
	
	protected static class Entry<K, V> extends SoftReference<V> {
		
		protected final K key;
		
		public Entry(K key, V value, ReferenceQueue<? super V> queue) {
			super(value, queue);
			this.key = key;
		}
		
		public K getKey() {
			return key;
		}
		
	}
	
}