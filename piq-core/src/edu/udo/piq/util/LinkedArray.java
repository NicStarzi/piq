package edu.udo.piq.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedArray<E> implements Iterable<E> {
	
	private Elem<E>[] buffer;
	private int head = -1;
	private int size = 0;
	
	@SuppressWarnings("unchecked")
	public LinkedArray(int capacity) {
		buffer = new Elem[capacity];
	}
	
	public E[] asArray(E[] arr) {
		if (arr.length < size) {
			arr = Arrays.copyOf(arr, size);
		}
		int i = 0;
		for (E e : this) {
			arr[i++] = e;
		}
		return arr;
	}
	
	public boolean contains(Object obj) {
		return indexOf(obj) != -1;
	}
	
	public int indexOf(Object obj) {
		if (size == 0) {
			if (obj == null && buffer.length > 0) {
				return 0;
			}
		}
		if (size >= buffer.length / 2) {
			// tight packed buffer
			for (int i = 0; i < buffer.length; i++) {
				if (buffer[i] == null) {
					if (obj == null) {
						return i;
					}
				} else {
					if (buffer[i].content.equals(obj)) {
						return i;
					}
				}
			}
		} else {
			// sparse packed buffer
			int i = head;
			while (i != -1) {
				if (buffer[i] == null) {
					if (obj == null) {
						return i;
					}
					i = -1;
				} else {
					if (buffer[i].content.equals(obj)) {
						return i;
					}
					i = buffer[i].next;
				}
			}
		}
		return -1;
	}
	
	public void clear() {
		Arrays.fill(buffer, null);
	}
	
	public void fill(E obj) {
		if (obj == null) {
			clear();
			return;
		}
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == null) {
				buffer[i] = new Elem<>(obj);
			} else {
				buffer[i].content = obj;
			}
			buffer[i].prev = i - 1;
			buffer[i].next = i + 1;
		}
		buffer[buffer.length - 1].next = -1;
	}
	
	public void set(int index, E obj) {
		if (buffer[index] == obj) {
			return;
		}
		adjustHeadIfNeeded(index, obj);
		if (buffer[index] == null) {
			if (obj != null) {
				add(index, obj);
			} else {
				// we don't need to override null with null
			}
		} else {
			if (obj == null) {
				remove(index, obj);
			} else {
				override(index, obj);
			}
		}
	}
	
	private void adjustHeadIfNeeded(int index, E obj) {
		if (obj == null) {
			if (head == index) {
				head = buffer[head].next;
			}
		} else {
			if (head == -1 || head > index) {
				head = index;
			}
		}
	}
	
	private void add(int index, E obj) {
		size += 1;
		buffer[index] = new Elem<>(obj);
		for (int i = index - 1; i >= 0; i--) {
			if (buffer[i] != null) {
				buffer[index].prev = i;
				buffer[i].next = index;
				break;
			}
		}
		for (int i = index + 1; i < buffer.length; i++) {
			if (buffer[i] != null) {
				buffer[index].next = i;
				buffer[i].prev = index;
				break;
			}
		}
	}
	
	private void remove(int index, E obj) {
		size -= 1;
		int nextID = buffer[index].next;
		int prevID = buffer[index].prev;
		if (prevID != -1) {
			buffer[prevID].next = nextID;
		}
		if (nextID != -1) {
			buffer[nextID].prev = prevID;
		}
		buffer[index] = null;
	}
	
	private void override(int index, E obj) {
		buffer[index].content = obj;
	}
	
	public E get(int index) {
		Elem<E> elem = buffer[index];
		if (elem == null) {
			return null;
		}
		return elem.content;
	}
	
	public int length() {
		return buffer.length;
	}
	
	public int size() {
		return size;
	}
	
	public Iterator<E> iterator() {
		return new LinkedArrayIterator<>(this);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		E current = null;
		for (Iterator<E> i = iterator(); i.hasNext();) {
			if (current != null) {
				sb.append(", ");
			}
			current = i.next();
			sb.append(current.toString());
		}
		sb.append("]");
		return sb.toString();
	}
	
	private static final class Elem<E> {
		
		int next = -1;
		int prev = -1;
		E content = null;
		
		public Elem(E obj) {
			content = obj;
		}
		
	}
	
	private static final class LinkedArrayIterator<E> implements Iterator<E> {
		
		final LinkedArray<E> lArr;
		int pos;
		
		public LinkedArrayIterator(LinkedArray<E> linkedArray) {
			lArr = linkedArray;
			pos = lArr.head;
		}
		
		public boolean hasNext() {
			return pos != -1;
		}
		
		public E next() {
			if (pos == -1) {
				throw new NoSuchElementException();
			}
			Elem<E> elem = lArr.buffer[pos];
			E next = elem.content;
			pos = elem.next;
			return next;
		}
		
	}
	
}